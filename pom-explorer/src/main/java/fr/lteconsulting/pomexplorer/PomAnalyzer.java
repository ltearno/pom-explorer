package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

		Set<File> pomFiles = new HashSet<>();
		File file = new File( directory );
		if( !file.exists() )
			log.html( Tools.errorMessage( "'" + directory + "' does not exist !" ) );

		scanPomFiles( file, session, log, pomFiles );

		Set<Project> unresolvableProjects = new HashSet<>();

		Set<Project> loadedProjects = new HashSet<>();
		for( File pomFile : pomFiles )
		{
			Project project = createAndRegisterProject( pomFile, false, session, log );
			if( project != null )
				loadedProjects.add( project );
		}

		log.html( "loaded " + loadedProjects.size() + " projects<br/><br/>" );
		if( verbose )
		{
			log.html( "<br/>loaded projects:<br/>" );
			loadedProjects.stream().sorted( Project.alphabeticalComparator ).forEach( ( p ) -> log.html( p + "<br/>" ) );
		}

		log.html("Read profiles to use in the analyze...<br/>");
		Map<String, Profile> profiles = new HashMap<>();
		for (int i=0; i<profilesId.length; i++)
		{
			profiles.put(profilesId[i], new Profile(profilesId[i]));
		}
		
		log.html( "fetching missing parents and boms...<br/>" );
		Set<Project> toGraphProjects = new HashSet<>();
		for( Project project : loadedProjects )
		{
			if( project.fetchMissingGavsForResolution( online, log, toGraphProjects ) )
				toGraphProjects.add( project );
			else
				unresolvableProjects.add( project );
		}

		log.html( "fetched missing projects, " + toGraphProjects.size() + " resolved projects and " + unresolvableProjects.size() + " unresolved projects<br/>" );

		log.html( "adding projects to graph" );
		PomGraphWriteTransaction tx = session.graph().write();
		for( Project project : toGraphProjects )
		{
			addProjectToGraph( project, tx, fetchMissingProjects, online, session, profiles, log );
		}

		for( Project unresolvable : unresolvableProjects )
			session.projects().remove( unresolvable );

		tx.commit();

		duration = System.currentTimeMillis() - duration;

		log.html( "<br/>analysis report:<br/>" + loadedProjects.size() + " projects loaded and added to the pom graph,<br/>" + toGraphProjects.size() + " projects added to graph,<br/>in " + duration + " ms.<br/>" );

		if( !unresolvableProjects.isEmpty() )
		{
			StringBuilder sb = new StringBuilder();
			sb.append( "<br/>" + unresolvableProjects.size() + " unresolvable projects:<br/>" );
			unresolvableProjects.stream().sorted( Project.alphabeticalComparator ).forEach( g -> sb.append( "- " + g + "<br/>" ) );
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
			project = createAndRegisterProject( pomFile, true, session, log );

		return project;
	}

	public void addProjectToGraph( Project project, PomGraphWriteTransaction tx, boolean fetchMissingProjects, boolean online, Session session, Map<String, Profile> profiles, Log log )
	{
		tx.removeRelations( tx.relations( project.getGav() ) );

		if (profiles == null)
			profiles = new HashMap<>();
		
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
