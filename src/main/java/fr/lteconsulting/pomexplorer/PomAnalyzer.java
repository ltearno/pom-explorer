package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.task.AddScopedDependenciesTask;

import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.ParentRelation;

public class PomAnalyzer
{
	public void analyze( String directory, WorkingSession session, Client client )
	{
		processFile( new File( directory ), session, client );
	}

	private void processFile( File file, WorkingSession session, Client client )
	{
		if( file == null )
			return;

		if( file.isDirectory() )
		{
			String name = file.getName();
			if( "target".equalsIgnoreCase( name ) || "src".equalsIgnoreCase( name ) )
				return;

			for( File f : file.listFiles() )
				processFile( f, session, client );
		}
		else if( file.getName().equalsIgnoreCase( "pom.xml" ) )
		{
			processPom( file, session, client );
		}
	}

	private void processPom( File pomFile, WorkingSession session, Client client )
	{
		MavenProject unresolved = readPomFile( pomFile );
		if( unresolved == null )
		{
			System.out.println( "Cannot read this pom file : " + pomFile.getAbsolutePath() );
			return;
		}
		
		ParsedPomFile resolved = loadPomFile( pomFile );
		if( resolved == null )
		{
			System.out.println( "Cannot load this pom file : " + pomFile.getAbsolutePath() );
			return;
		}

		GAV gav = new GAV( resolved.getGroupId(), resolved.getArtifactId(), resolved.getVersion() );

		if( session.hasProject( gav ) )
		{
			System.out.println( "POM already processed '" + pomFile.getAbsolutePath() + "' ! Ignoring." );
			return;
		}

		session.graph().addGav( gav );
		
		// hierarchy
		Parent parent = unresolved.getModel().getParent();
		if( parent != null )
		{
			GAV parentGav = new GAV( parent.getGroupId(), parent.getArtifactId(), parent.getVersion() );
			session.graph().addGav( parentGav );
			
			session.graph().addRelation( gav, parentGav, new ParentRelation() );
		}

		// dependencies
		for( MavenDependency dependency : resolved.getDependencies() )
		{
			GAV depGav = new GAV( dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion() );
			session.graph().addGav( depGav );
			
			session.graph().addRelation( gav, depGav, new DependencyRelation( dependency.getScope().name(), dependency.getClassifier() ) );
		}

		Project projectInfo = new Project( pomFile, resolved, unresolved );
		session.registerProject( projectInfo );
		
		client.send( "processed project " + projectInfo.getGav() );
	}

	private ParsedPomFile loadPomFile( File pomFile )
	{
		MavenWorkingSession session = new MavenWorkingSessionImpl();
		session = new AddScopedDependenciesTask( ScopeType.COMPILE, ScopeType.IMPORT, ScopeType.SYSTEM, ScopeType.RUNTIME ).execute( session );
	
		try
		{
			session.loadPomFromFile( pomFile );
			return session.getParsedPomFile();
		}
		catch( InvalidConfigurationFileException e )
		{
			return null;
		}
	}

	private MavenProject readPomFile( File pom )
	{
		Model model = null;
		FileReader reader = null;
		MavenXpp3Reader mavenreader = new MavenXpp3Reader();
		try
		{
			reader = new FileReader( pom );
		}
		catch( FileNotFoundException e1 )
		{
		}
		try
		{
			model = mavenreader.read( reader );
			model.setPomFile( pom );
		}
		catch( IOException | XmlPullParserException e1 )
		{
		}
		MavenProject project = new MavenProject( model );

		return project;
	}
}
