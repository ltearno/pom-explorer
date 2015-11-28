package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;

import fr.lteconsulting.pomexplorer.graph.relation.BuildDependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.ParentRelation;

public class PomAnalyzer
{
	private final static Set<String> IGNORED_DIRS = new HashSet<>( Arrays.asList(
			"target",
			"bin",
			"src",
			"node_modules",
			".git",
			"war",
			"gwt-unitCache",
			".idea",
			".settings" ) );

	public void analyze( String directory, WorkingSession session, ILogger log )
	{
		processFile( new File( directory ), session, log );
	}

	private boolean acceptedDir( String name )
	{
		for( String ignored : IGNORED_DIRS )
			if( ignored.equalsIgnoreCase( name ) )
				return false;
		return true;
	}

	private void processFile( File file, WorkingSession session, ILogger log )
	{
		if( file == null )
			return;

		if( file.isDirectory() )
		{
			String name = file.getName();
			if( !acceptedDir( name ) )
				return;

			try
			{
				Files.newDirectoryStream( file.toPath(), ( filtered ) -> {
					String filteredName = filtered.getFileName().toString();
					return "pom.xml".equalsIgnoreCase( filteredName ) || Files.isDirectory( filtered ) && acceptedDir( filteredName );
				} ).forEach( ( path ) -> processFile( new File( path.toString() ), session, log ) );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
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
		MavenProject unresolved = readPomFile( pomFile, log );
		if( unresolved == null )
		{
			log.html( Tools.warningMessage( "cannot read pom " + pomFile.getAbsolutePath() ) );
			return;
		}

		// TODO : here we have a resolution problem
		GAV gav = new GAV( unresolved.getGroupId(), unresolved.getArtifactId(), unresolved.getVersion() );

		if( session.projects().contains( gav ) )
		{
			log.html( Tools.warningMessage( "pom already processed '" + pomFile.getAbsolutePath() + "' ! Ignoring." ) );
			return;
		}

		session.graph().addGav( gav );

		// hierarchy
		// TODO : here we have a resolution problem
		Parent parent = unresolved.getModel().getParent();
		if( parent != null )
		{
			GAV parentGav = new GAV( parent.getGroupId(), parent.getArtifactId(), parent.getVersion() );
			session.graph().addGav( parentGav );

			session.graph().addRelation( new ParentRelation( gav, parentGav ) );
		}

		// TODO : here we have a resolution problem
		// dependencies
		for( Dependency dependency : unresolved.getDependencies() )
		{
			GAV depGav = new GAV( dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion() );
			session.graph().addGav( depGav );

			session.graph().addRelation( new DependencyRelation( gav, depGav, dependency.getScope(), dependency.getClassifier() ) );
		}

		// build dependencies
		try
		{
			// TODO : here we have a resolution problem
			Model model = unresolved.getModel();
			if( model.getBuild() != null && model.getBuild().getPlugins() != null )
			{
				for( Plugin plugin : model.getBuild().getPlugins() )
				{
					GAV depGav = new GAV( plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion() );
					session.graph().addGav( depGav );

					session.graph().addRelation( new BuildDependencyRelation( gav, depGav ) );
				}
			}
		}
		catch( IllegalArgumentException | SecurityException e )
		{
			e.printStackTrace();
		}

		Project projectInfo = new Project( pomFile, unresolved );
		session.projects().add( projectInfo );

		session.repositories().add( projectInfo );

		log.html( "processed project " + projectInfo.getGav() + "<br/>" );
	}

	private MavenProject readPomFile( File pom, ILogger log )
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
			log.html( Tools.errorMessage( "error reading project file : " + pom.getAbsolutePath() ) );
			Tools.dumpStacktrace( e1, log );
			return null;
		}
	}
}
