package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.maven.model.Dependency;
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
import org.jgrapht.DirectedGraph;

import fr.lteconsulting.pomexplorer.Project.DependencyInfo;

public class PomAnalyzer
{
	public void analyze( String directory, WorkingSession session )
	{
		processFile( new File( directory ), session );
	}

	private void processFile( File file, WorkingSession session )
	{
		if( file == null )
			return;

		if( file.isDirectory() )
		{
			String name = file.getName();
			if( "target".equalsIgnoreCase( name ) || "src".equalsIgnoreCase( name ) )
				return;

			for( File f : file.listFiles() )
				processFile( f, session );
		}
		else if( file.getName().equalsIgnoreCase( "pom.xml" ) )
		{
			processPom( file, session );
		}
	}

	private void resolvePom( File pomFile )
	{
		ParsedPomFile parsedPom = parsePomFile( pomFile );
		if( parsedPom == null )
		{
			System.out.println( "Cannot load this pom file : " + pomFile.getAbsolutePath() );
			return;
		}

		
	}

	private ParsedPomFile parsePomFile( File pomFile )
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

	private void processPom( File pomFile, WorkingSession session )
	{
		ParsedPomFile resolvedPom = parsePomFile( pomFile );
		if( resolvedPom == null )
		{
			System.out.println( "Cannot load this pom file : " + pomFile.getAbsolutePath() );
			return;
		}
		
		MavenProject project = loadProject( pomFile );
		if( project == null )
		{
			System.out.println( "Cannot read this pom file : " + pomFile.getAbsolutePath() );
			return;
		}

		GAV gav = new GAV( resolvedPom.getGroupId(), resolvedPom.getArtifactId(), resolvedPom.getVersion() );

		if( session.hasProject( gav ) )
		{
			System.out.println( "POM already processed '" + pomFile.getAbsolutePath() + "' ! Ignoring." );
			return;
		}

		gav = session.registerArtifact( resolvedPom.getGroupId(), resolvedPom.getArtifactId(), resolvedPom.getVersion() );
		
		DirectedGraph<GAV, Dep> g = session.getGraph();
		for( MavenDependency dependency : resolvedPom.getDependencies() )
		{
			GAV depGav = session.registerArtifact( dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion() );
			
			if( gav != null && depGav != null )
				g.addEdge( gav, depGav, new Dep( dependency.getScope().name(), dependency.getClassifier() ) );
		}

		Project projectInfo = new Project( pomFile, resolvedPom, project );
		session.registerProject( projectInfo );
	}

	private MavenProject loadProject( File pom )
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
