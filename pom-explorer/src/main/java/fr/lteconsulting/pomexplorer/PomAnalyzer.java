package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;

import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;
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
		log.html( "Loading projects from '" + directory + "'<br/>" );
		Set<Project> loadedProjects = new HashSet<>();
		loadDirectoryOrFile( new File( directory ), session, log, loadedProjects );

		log.html( "Fetching related projects...<br/>" );
		boolean somethingToDo = true;
		while( somethingToDo )
		{
			somethingToDo = false;
			for( Project project : loadedProjects )
				somethingToDo |= fixProjectResolution( project, session, log );
		}

		log.html( "Updating graph<br/>" );
		for( Project project : loadedProjects )
			addProjectToGraph( project, session, log );
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
	public Project fetchGavWithMaven( WorkingSession session, ILogger log, GAV gav )
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
			return null;
		}

		log.html( "resolved artifact : " + resolvedArtifact.getCoordinate().toString() + "<br/>" );

		// Big hack here ! I don't manage to get the resolve the pom file with shrinkwrap,
		// so i resolve the jar and find the path to the pom.xml file. That does only
		// work for jar packaged projects, and especially not for pom packaged bom imports... TODO
		String pomPath = resolvedArtifact.asFile().getAbsolutePath();
		int idx = pomPath.lastIndexOf( '.' );
		if( idx < 0 )
			return null;

		pomPath = pomPath.substring( 0, idx + 1 ) + "pom";

		Project project = loadProjectFromPomFile( new File( pomPath ), session, log );

		return project;
	}

	public void addProjectToGraph( Project project, WorkingSession session, ILogger log )
	{
		session.graph().removeRelations( session.graph().relations( project.getGav() ) );

		try
		{
			GAV gav = project.getGav();
			GAV parentGav = project.getParent();
			Collection<GavLocation> dependencies = project.getDependencies( session, log ).values();
			Collection<GavLocation> pluginDependencies = project.getPluginDependencies( session, log ).values();

			session.graph().addGav( gav );
			if( parentGav != null )
			{
				session.graph().addGav( parentGav );
				session.graph().addRelation( new ParentRelation( gav, parentGav ) );
			}

			for( GavLocation location : dependencies )
			{
				session.graph().addGav( location.getResolvedGav() );
				session.graph().addRelation( new DependencyRelation( gav, location.getResolvedGav(), location.getScope(), location.getClassifier() ) );
			}

			for( GavLocation location : pluginDependencies )
			{
				session.graph().addGav( location.getResolvedGav() );
				session.graph().addRelation( new BuildDependencyRelation( gav, location.getResolvedGav() ) );
			}

			// log.html( "updated graph with project " + project.getGav() + "<br/>" );
		}
		catch( Exception e )
		{
			log.html( Tools.errorMessage( "Cannot add project " + project + " to graph. Cause: " + e.getMessage() ) );
		}
	}

	private boolean acceptedDir( String name )
	{
		for( String ignored : IGNORED_DIRS )
			if( ignored.equalsIgnoreCase( name ) )
				return false;
		return true;
	}

	private void loadDirectoryOrFile( File file, WorkingSession session, ILogger log, Set<Project> loadedProjects )
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
					return filteredName.endsWith( ".pom" ) || "pom.xml".equalsIgnoreCase( filteredName ) || Files.isDirectory( filtered ) && acceptedDir( filteredName );
				} ).forEach( ( path ) -> loadDirectoryOrFile( new File( path.toString() ), session, log, loadedProjects ) );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		else if( file.getName().equalsIgnoreCase( "pom.xml" ) || file.getName().endsWith( ".pom" ) )
		{
			Project project = loadProjectFromPomFile( file, session, log );
			if( project != null )
				loadedProjects.add( project );
		}
	}

	private Project loadProjectFromPomFile( File pomFile, WorkingSession session, ILogger log )
	{
		try
		{
			Project project = new Project( pomFile );
			session.projects().add( project );
			session.repositories().add( project );
			return project;
		}
		catch( Exception e )
		{
			log.html( Tools.errorMessage( "error loading pom file " + pomFile.getAbsolutePath() + ", message: " + e.getMessage() ) );
		}

		return null;
	}

	/**
	 * sometimes a project cannot be resolved because it's parent or a bom import is not in the analyzed directory.
	 * 
	 * At that time, the parent or bom import is fetched through maven.
	 * 
	 * returns true if something has moved and resolution should be attempted again on other projects
	 */
	private boolean fixProjectResolution( Project project, WorkingSession session, ILogger log )
	{
		Set<GAV> missingProjects = project.getMissingGavsForResolution( session, log );

		if( missingProjects.isEmpty() )
			return false;

		log.html( Tools.warningMessage( "For the project " + project + " to be resolvable, some projects wich are not in the analyzed directory need to be fetched with maven :" ) );

		boolean somethingChanged = false;

		for( GAV missingProject : missingProjects )
		{
			log.html( "Fetching missing project " + missingProject + " with maven<br/>" );
			somethingChanged |= fetchGavWithMaven( session, log, missingProject ) != null;
		}

		return somethingChanged;
	}
}
