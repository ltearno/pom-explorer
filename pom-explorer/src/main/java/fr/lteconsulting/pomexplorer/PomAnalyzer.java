package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.task.AddScopedDependenciesTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureSettingsFromFileTask;

import fr.lteconsulting.pomexplorer.graph.relation.BuildDependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.ParentRelation;

public class PomAnalyzer
{
	public void analyze( String directory, WorkingSession session, ILogger log )
	{
		processFile( new File( directory ), session, log );
	}

	private void processFile( File file, WorkingSession session, ILogger log )
	{
		if( file == null )
			return;

		if( file.isDirectory() )
		{
			String name = file.getName();
			if( "target".equalsIgnoreCase( name ) || "bin".equalsIgnoreCase( name ) || "src".equalsIgnoreCase( name ) )
				return;

			for( File f : file.listFiles() )
				processFile( f, session, log );
		}
		else if( file.getName().equalsIgnoreCase( "pom.xml" ) )
		{
			processPom( file, session, log );
		}
	}

	/**
	 * Takes a GAV, download it if needed, analyse its dependencies and add them
	 * to the graph
	 *
	 * @param gav
	 *            gav to be analyzed
	 * @param session
	 *            working session
	 */
	public void registerExternalDependency( WorkingSession session, Client client, ILogger log, GAV gav )
	{
		String mavenSettingsFilePath = session.getMavenSettingsFilePath();

		MavenResolverSystem resolver;
		if( mavenSettingsFilePath != null && !mavenSettingsFilePath.isEmpty() )
			resolver = Maven.configureResolver().fromFile( mavenSettingsFilePath );
		else
			resolver = Maven.resolver();

		MavenResolvedArtifact resolvedArtifact = null;
		try
		{
			resolvedArtifact = resolver.resolve( gav.toString() ).withoutTransitivity().asSingle( MavenResolvedArtifact.class );
		}
		catch( Exception e )
		{
			log.html( Tools.errorMessage( "shrinkwrap error : " + e.getMessage() ) );
		}

		if( resolvedArtifact == null )
		{
			log.html( Tools.warningMessage( "cannot resolve the artifact " + gav ) );
			return;
		}

		log.html( "resolved artifact : " + resolvedArtifact.getCoordinate().toString() + "<br/>" );

		// Big hack here !
		String pomPath = resolvedArtifact.asFile().getAbsolutePath();
		int idx = pomPath.lastIndexOf( '.' );
		if( idx < 0 )
			return;

		pomPath = pomPath.substring( 0, idx + 1 ) + "pom";

		processPom( new File( pomPath ), session, log );
	}

	private void processPom( File pomFile, WorkingSession session, ILogger log )
	{
		MavenProject unresolved = readPomFile( pomFile );
		if( unresolved == null )
		{
			log.html( "<span style='color:orange;'>cannot read pom " + pomFile.getAbsolutePath() + "</span><br/>" );
			return;
		}

		ParsedPomFile resolved = loadPomFile( session, pomFile );
		if( resolved == null )
		{
			log.html( "<span style='color:orange;'>cannot load pom " + unresolved.getGroupId() + ":" + unresolved.getArtifactId() + ":" + unresolved.getVersion() + " (<i>" + pomFile.getAbsolutePath() + "</i>)</span><br/>" );
			return;
		}

		GAV gav = new GAV( resolved.getGroupId(), resolved.getArtifactId(), resolved.getVersion() );

		if( session.projects().contains( gav ) )
		{
			log.html( "<span style='color:orange;'>pom already processed '" + pomFile.getAbsolutePath() + "' ! Ignoring.</span><br/>" );
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

		// build dependencies
		try
		{
			Model model = Tools.getParsedPomFileModel( resolved );
			for( Plugin plugin : model.getBuild().getPlugins() )
			{
				GAV depGav = new GAV( plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion() );
				session.graph().addGav( depGav );

				session.graph().addRelation( gav, depGav, new BuildDependencyRelation() );
			}
		}
		catch( IllegalArgumentException | SecurityException e )
		{
			e.printStackTrace();
		}

		Project projectInfo = new Project( pomFile, resolved, unresolved );
		session.projects().add( projectInfo );

		session.repositories().add( projectInfo );

		log.html( "processed project " + projectInfo.getGav()+"<br/>" );
	}

	private static MavenWorkingSession createMavenWorkingSession( WorkingSession workingSession )
	{
		try
		{
			String mavenSettingsFilePath = workingSession.getMavenSettingsFilePath();

			MavenWorkingSession session = new MavenWorkingSessionImpl();
			if( mavenSettingsFilePath != null )
				session = new ConfigureSettingsFromFileTask( mavenSettingsFilePath ).execute( session );

			session = new AddScopedDependenciesTask( ScopeType.COMPILE, ScopeType.IMPORT, ScopeType.SYSTEM, ScopeType.RUNTIME ).execute( session );

			return session;
		}
		catch( InvalidConfigurationFileException e )
		{
			return null;
		}
	}

	private ParsedPomFile loadPomFile( WorkingSession workingSession, File pomFile )
	{
		MavenWorkingSession session = createMavenWorkingSession( workingSession );
		if( session == null )
			return null;

		try
		{
			session.loadPomFromFile( pomFile );
			return session.getParsedPomFile();
		}
		catch( Exception e )
		{
			return null;
		}
	}

	private MavenProject readPomFile( File pom )
	{
		Model model = null;
		MavenXpp3Reader mavenreader = new MavenXpp3Reader();
		try( FileReader reader = new FileReader( pom ) )
		{
			model = mavenreader.read( reader );
			model.setPomFile( pom );

			MavenProject project = new MavenProject( model );
			return project;
		}
		catch( IOException | XmlPullParserException e1 )
		{
			return null;
		}
	}
}
