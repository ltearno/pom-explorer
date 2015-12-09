package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphWriteTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.BuildDependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.ParentRelation;
import fr.lteconsulting.pomexplorer.model.Dependency;
import fr.lteconsulting.pomexplorer.model.DependencyKey;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.model.transitivity.DependencyNode;

public class PomAnalyzer
{
	private final static Set<String> IGNORED_DIRS = new HashSet<>( Arrays.asList( "target", "bin", "src", "node_modules", ".git", "war", "gwt-unitCache", ".idea", ".settings" ) );

	public void analyze( String directory, boolean verbose, boolean fetchMissingProjects, boolean online, Session session, Log log )
	{
		log.html( "analyzing '" + directory + "'<br/>" );
		if( !fetchMissingProjects )
			log.html( Tools.warningMessage( "fetching missing projects is disabled" ) );

		long duration = System.currentTimeMillis();

		Set<File> pomFiles = new HashSet<>();
		scanPomFiles( new File( directory ), session, log, pomFiles );

		Set<Project> loadedProjects = new HashSet<>();
		for( File pomFile : pomFiles )
		{
			Project project = createAndRegisterProject( pomFile, false, session, log );
			if( project != null )
				loadedProjects.add( project );
		}

		log.html( "graph update<br/>" );

		PomGraphWriteTransaction tx = session.graph().write();

		for( Project project : loadedProjects )
		{
			assert project.getMissingGavsForResolution( log, new HashSet<>() ).isEmpty() : "error : not resolvable project should not be in this set !";

			addProjectToGraph( project, tx, fetchMissingProjects, online, session, log );
		}

		tx.commit();

		duration = System.currentTimeMillis() - duration;

		if( verbose )
		{
			log.html( "<br/>Loaded projects:<br/>" );
			loadedProjects.stream().sorted( Project.alphabeticalComparator ).forEach( ( p ) -> log.html( p + "<br/>" ) );
		}

		log.html( "<br/>analysis report: " + loadedProjects.size() + " loaded projects, projects added to the pom graph in " + duration + " ms.<br/>" );
	}

	/**
	 * Takes a GAV, download it if needed, analyse its dependencies and add them to the graph
	 *
	 * @param gav
	 *            gav to be analyzed
	 * @param session
	 *            working session
	 */
	public Project fetchGavWithMaven( Session session, Log log, Gav gav, boolean online )
	{
		if( gav == null || !gav.isResolved() || gav.getVersion().startsWith( "[" ) || gav.getVersion().startsWith( "@" ) )
			return null;

		MavenResolver resolver = session.mavenResolver();

		File pomFile = resolver.resolvePom( gav, "pom", online );

		Project project = null;
		if( pomFile != null )
			project = createAndRegisterProject( pomFile, true, session, log );

		if( project == null )
			log.html( Tools.warningMessage( "cannot fetch " + gav + " through maven" ) );

		return project;
	}

	private void addProjectToGraph( Project project, PomGraphWriteTransaction tx, boolean fetchMissingProjects, boolean online, Session session, Log log )
	{
		tx.removeRelations( tx.relations( project.getGav() ) );

		try
		{
			Gav gav = project.getGav();
			Gav parentGav = project.getParent();
			DependencyNode dependencyNode = project.getDependencyTree( false, online, log );
			Set<Gav> pluginDependencies = project.getPluginDependencies( log );

			tx.addGav( gav );
			if( parentGav != null )
			{
				tx.addGav( parentGav );
				tx.addRelation( new ParentRelation( gav, parentGav ) );
			}

			if( dependencyNode.getChildren() != null )
			{
				for( DependencyNode level1Node : dependencyNode.getChildren() )
				{
					DependencyKey key = level1Node.getKey();

					Gav dependencyGav = new Gav( key.getGroupId(), key.getArtifactId(), level1Node.getVs().getVersion() );
					tx.addGav( dependencyGav );
					tx.addRelation( new DependencyRelation( gav, dependencyGav, new Dependency( dependencyGav, level1Node.getVs().getScope(), key.getClassifier(), key.getType() ) ) );
				}
			}

			for( Gav pluginGav : pluginDependencies )
			{
				tx.addGav( pluginGav );
				tx.addRelation( new BuildDependencyRelation( gav, pluginGav ) );
			}
		}
		catch( Exception e )
		{
			log.html( Tools.errorMessage( "Cannot add project " + project + " to graph. Cause: " + e.getMessage() ) );
			Tools.logStacktrace( e, log );
		}
	}

	private boolean acceptedDir( String name )
	{
		for( String ignored : IGNORED_DIRS )
			if( ignored.equalsIgnoreCase( name ) )
				return false;
		return true;
	}

	private void scanPomFiles( File file, Session session, Log log, Set<File> pomFiles )
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

	private Project createAndRegisterProject( File pomFile, boolean isExternal, Session session, Log log )
	{
		try
		{
			Project project = new Project( session, pomFile, isExternal );
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
}
