package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphWriteTransaction;
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

		Set<File> pomFiles = new HashSet<>();
		scanPomFiles( new File( directory ), session, log, pomFiles );

		Set<Project> loadedProjects = new HashSet<>();
		for( File pomFile : pomFiles )
		{
			Project project = createAndRegisterProject( pomFile, session, log );
			if( project != null )
				loadedProjects.add( project );
		}

		log.html( "integrity checks...<br/>" );

		Set<Project> toProcess = loadedProjects;
		Set<Project> toGraphProjects = new HashSet<>();
		int nbUnresolved = 0;
		boolean firstRound = true;

		while( toProcess != null && !toProcess.isEmpty() )
		{
			Set<Project> next = null;

			for( Project project : toProcess )
			{
				boolean isOk = true;
				while( isOk )
				{
					Set<GAV> missingProjects = project.getMissingGavsForResolution( session, log );
					if( missingProjects == null || missingProjects.isEmpty() )
						break;

					for( GAV missingProjectGav : missingProjects )
					{
						log.html( "fetching " + missingProjectGav + " to resolve " + project + "<br/>" );
						File pomFile = session.mavenResolver().resolvePom( missingProjectGav, "pom" );
						if( pomFile == null )
						{
							log.html( Tools.errorMessage( "cannot fetch project " + missingProjectGav ) );
							isOk = false;
							break;
						}

						Project missingProject = createAndRegisterProject( pomFile, session, log );
						if( missingProject == null )
						{
							log.html( Tools.errorMessage( "cannot load project " + pomFile.getAbsolutePath() ) );
							isOk = false;
							break;
						}

						if( next == null )
							next = new HashSet<>();
						next.add( missingProject );
					}
				}

				if( isOk )
				{
					assert project.getMissingGavsForResolution( session, log, new HashSet<>() ).isEmpty() : "project should be resolvable here " + toString();
					toGraphProjects.add( project );
				}
				else if( firstRound )
				{
					nbUnresolved++;
				}
			}

			toProcess = next;
			firstRound = false;
		}

		log.html( "graph update<br/>" );
		
		PomGraphWriteTransaction tx = session.graph().startTransaction();

		for( Project project : toGraphProjects )
		{
			assert project.getMissingGavsForResolution( session, log, new HashSet<>() ).isEmpty() : "error : not resolvable project should not be in this set !";

			addProjectToGraph( project, tx, session, log );
		}
		
		tx.commit();

		duration = System.currentTimeMillis() - duration;

		log.html( "analysis report: " + loadedProjects.size() + " loaded projects, " + nbUnresolved
				+ " unresolved, " + toGraphProjects.size() + " projects added to the pom graph in " + duration + " ms.<br/>" );
	}

	/**
	 * Takes a GAV, download it if needed, analyse its dependencies and add them to the graph
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
			project = createAndRegisterProject( pomFile, session, log );

		if( project == null )
			log.html( Tools.warningMessage( "cannot fetch " + gav + " through maven" ) );

		return project;
	}

	private void addProjectToGraph( Project project, PomGraphWriteTransaction tx, WorkingSession session, ILogger log )
	{
		tx.removeRelations( tx.relations( project.getGav() ) );

		try
		{
			GAV gav = project.getGav();
			GAV parentGav = project.getParent();
			Collection<GavLocation> dependencies = project.getDependencies( session, log ).values();
			Collection<GavLocation> pluginDependencies = project.getPluginDependencies( session, log ).values();

			tx.addGav( gav );
			if( parentGav != null )
			{
				tx.addGav( parentGav );
				tx.addRelation( new ParentRelation( gav, parentGav ) );
			}

			for( GavLocation location : dependencies )
			{
				tx.addGav( location.getResolvedGav() );
				tx.addRelation( new DependencyRelation( gav, location.getResolvedGav(), location.getScope(), location.getClassifier() ) );
			}

			for( GavLocation location : pluginDependencies )
			{
				tx.addGav( location.getResolvedGav() );
				tx.addRelation( new BuildDependencyRelation( gav, location.getResolvedGav() ) );
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

	private void scanPomFiles( File file, WorkingSession session, ILogger log, Set<File> pomFiles )
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
				Files.newDirectoryStream(
						file.toPath(),
						( filtered ) -> {
							String filteredName = filtered.getFileName().toString();
							return filteredName.endsWith( ".pom" ) || "pom.xml".equalsIgnoreCase( filteredName )
									|| Files.isDirectory( filtered ) && acceptedDir( filteredName );
						} ).forEach( ( path ) -> scanPomFiles( new File( path.toString() ), session, log, pomFiles ) );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		else if( file.getName().equalsIgnoreCase( "pom.xml" ) || file.getName().endsWith( ".pom" ) )
		{
			pomFiles.add( file );
		}
	}

	private Project createAndRegisterProject( File pomFile, WorkingSession session, ILogger log )
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
			log.html( Tools.errorMessage( "error loading pom file " + pomFile.getAbsolutePath() + ", message: "
					+ e.getMessage() ) );
		}

		return null;
	}
}
