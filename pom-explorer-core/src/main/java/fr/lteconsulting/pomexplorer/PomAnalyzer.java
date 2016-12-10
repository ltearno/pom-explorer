package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphWriteTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.BuildDependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.ParentRelation;
import fr.lteconsulting.pomexplorer.model.Dependency;
import fr.lteconsulting.pomexplorer.model.DependencyKey;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.model.transitivity.DependencyNode;
import fr.lteconsulting.pomexplorer.model.transitivity.Repository;

public class PomAnalyzer
{
	private final static Set<String> IGNORED_DIRS = new HashSet<>( Arrays.asList( "target", "bin", "src", "node_modules", ".git", "war", "gwt-unitCache", ".idea", ".settings" ) );

	public void analyze( String directory, boolean verbose, boolean fetchMissingProjects, boolean online, String[] profilesId, Session session, Log log )
	{
		log.html( "analyzing '" + directory + "'<br/>" );
		if( !fetchMissingProjects )
			log.html( Tools.warningMessage( "fetching missing projects is disabled" ) );

		long duration = System.currentTimeMillis();

		File file = new File( directory );
		if( !file.exists() )
			log.html( Tools.errorMessage( "'" + directory + "' does not exist !" ) );

		Map<String, Profile> profiles = null;
		if( profilesId != null )
		{
			profiles = new HashMap<>();
			for( int i = 0; i < profilesId.length; i++ )
				profiles.put( profilesId[i], new Profile( profilesId[i] ) );
		}

		Set<File> pomFiles = new HashSet<>();

		scanPomFiles( file, session, log, pomFiles );

		Set<Project> loadedProjects = new HashSet<>();
		for( File pomFile : pomFiles )
		{
			Project project = readAndRegisterProject( pomFile, false, session, log );
			if( project != null )
				loadedProjects.add( project );
		}

		log.html( "loaded " + loadedProjects.size() + " projects<br/><br/>" );
		if( verbose )
		{
			log.html( "<br/>loaded projects:<br/>" );
			loadedProjects.stream().sorted( Project.alphabeticalComparator ).forEach( ( Project p ) -> log.html( p + "<br/>" ) );
		}

		log.html( "fetching missing parents and boms...<br/>" );
		Set<Project> unresolvableProjects = new HashSet<>();
		Set<Project> toGraphProjects = new HashSet<>();
		for( Project project : loadedProjects )
		{
			if( project.fetchMissingGavsForResolution( online, log, toGraphProjects ) )
				toGraphProjects.add( project );
			else
				unresolvableProjects.add( project );
		}
		log.html( "fetched missing projects<br/>" );

		log.html( toGraphProjects.size() + " resolved projects and " + unresolvableProjects.size() + " unresolved projects<br/>" );

		log.html( "adding projects to graph" );
		PomGraphWriteTransaction tx = session.graph().write();

		for( Project project : toGraphProjects )
			addProjectToGraph( project, tx, fetchMissingProjects, online, session, profiles, log );

		for( Project unresolvable : unresolvableProjects )
			session.projects().remove( unresolvable );

		tx.commit();

		duration = System.currentTimeMillis() - duration;

		log.html( "<br/>analysis report:<br/>" + loadedProjects.size() + " projects loaded and added to the pom graph,<br/>" + toGraphProjects.size() + " projects added to graph,<br/>in " + duration + " ms.<br/>" );

		if( !unresolvableProjects.isEmpty() )
		{
			StringBuilder sb = new StringBuilder();
			sb.append( "<br/>" + unresolvableProjects.size() + " unresolvable projects:<br/>" );
			unresolvableProjects.stream().sorted( Project.alphabeticalComparator ).forEach( project -> sb.append( "- " + project + "<br/>" ) );
			log.html( sb.toString() );
		}
	}

	public Project fetchGavWithMaven( Session session, Log log, Gav gav, boolean online )
	{
		return fetchGavWithMaven( session, log, gav, online, null );
	}

	public Project fetchGavWithMaven( Session session, Log log, Gav gav, boolean online, List<Repository> additionalRepos )
	{
		if( gav == null || !gav.isResolved() || gav.getVersion().startsWith( "[" ) || gav.getVersion().startsWith( "@" ) )
			return null;

		MavenResolver resolver = session.mavenResolver();

		File pomFile = resolver.resolvePom( gav, "pom", online, additionalRepos, log );

		Project project = null;
		if( pomFile != null )
			project = readAndRegisterProject( pomFile, true, session, log );

		return project;
	}

	public void addProjectToGraph( Project project, PomGraphWriteTransaction tx, boolean fetchMissingProjects, boolean online, Session session, Map<String, Profile> profiles, Log log )
	{
		tx.removeRelations( tx.relations( project.getGav() ) );

		try
		{
			Gav gav = project.getGav();
			Gav parentGav = project.getParent();
			DependencyNode dependencyNode = project.getDependencyTree( false, online, profiles, log );
			Set<Gav> pluginDependencies = project.getPluginDependencies( profiles, log );

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

	private boolean acceptedPath( Path path )
	{
		String pathName = path.getFileName().toString();

		return (Files.isDirectory( path ) && acceptedDir( pathName )) || (pathName.endsWith( ".pom" ) || "pom.xml".equalsIgnoreCase( pathName ));
	}

	private void scanPomFiles( File startFile, Session session, Log log, Set<File> pomFiles )
	{
		assert startFile != null && startFile.exists();

		List<File> queue = new ArrayList<>();
		queue.add( startFile );

		while( !queue.isEmpty() )
		{
			File file = queue.remove( 0 );

			if( file.isDirectory() )
			{
				String name = file.getName();
				if( !acceptedDir( name ) )
					return;

				try( DirectoryStream<Path> pathStream = Files.newDirectoryStream( file.toPath(), this::acceptedPath ) )
				{
					pathStream.forEach( path -> queue.add( new File( path.toString() ) ) );
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
	}

	private Project readAndRegisterProject( File pomFile, boolean isExternal, Session session, Log log )
	{
		try
		{
			Project project = new Project( session, pomFile, isExternal );

			project.initialize();

			session.projects().add( project );

			return project;
		}
		catch( Exception e )
		{
			log.html( Tools.errorMessage( "error loading pom file " + pomFile.getAbsolutePath() + ", message: " + e.getMessage() ) );

			return null;
		}
	}
}
