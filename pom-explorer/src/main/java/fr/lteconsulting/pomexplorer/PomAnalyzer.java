package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
		log.html( "analyzing '" + directory + "'<br/>" );

		long duration = System.currentTimeMillis();

		Set<Project> loadedProjects = new HashSet<>();
		loadDirectoryOrFile( new File( directory ), session, log, loadedProjects );

		log.html( "integrity checks...<br/>" );
		boolean somethingToDo = true;
		while( somethingToDo )
		{
			somethingToDo = false;

			Set<Project> reloadedProjects = new HashSet<>();

			for( Project project : loadedProjects )
				somethingToDo |= fixProjectResolution( project, reloadedProjects, session, log );

			loadedProjects.addAll( reloadedProjects );
		}

		log.html( "graph update<br/>" );
		int nbUnresolved = 0;
		for( Project project : loadedProjects )
		{
			if( !project.isResolvable( session, log ) )
			{
				nbUnresolved++;
				log.html( Tools.warningMessage( "non resolvable project " + project ) );

				log.html( "missing the following projects : " + project.getMissingGavsForResolution( session, log ) + "<br/>" );

				continue;
			}
			
			addProjectToGraph( project, session, log );
		}

		duration = System.currentTimeMillis() - duration;

		log.html( "analysis report: " + loadedProjects.size() + " loaded projects in " + duration + " ms, " + nbUnresolved + " unresolved.<br/>" );
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
		MavenResolver resolver = session.mavenResolver();

		File pomFile = resolver.resolvePom( gav, "pom" );
		
		Project project = null;
		if( pomFile != null )
			project = loadProjectFromPomFile( pomFile, session, log );
		
		if(project==null)
			log.html( Tools.warningMessage( "cannot fetch " + gav + " through maven" ) );

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
	private boolean fixProjectResolution( Project project, Set<Project> loadedProjects, WorkingSession session, ILogger log )
	{
		Set<GAV> missingProjects = project.getMissingGavsForResolution( session, log );

		if( missingProjects.isEmpty() )
			return false;

		boolean somethingChanged = false;

		for( GAV missingProject : missingProjects )
		{
			log.html( "fetching " + missingProject + " (needed by resolution of " + project + ")<br/>" );
			Project loadedProject = fetchGavWithMaven( session, log, missingProject );
			somethingChanged |= loadedProject != null;
			if( loadedProject != null )
				loadedProjects.add( loadedProject );
		}

		return somethingChanged;
	}
}
