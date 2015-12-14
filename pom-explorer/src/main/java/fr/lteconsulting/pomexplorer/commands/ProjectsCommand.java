package fr.lteconsulting.pomexplorer.commands;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.graph.relation.RelationType;
import fr.lteconsulting.pomexplorer.graph.relation.Scope;
import fr.lteconsulting.pomexplorer.model.Dependency;
import fr.lteconsulting.pomexplorer.model.DependencyKey;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.model.transitivity.DependencyNode;

public class ProjectsCommand
{
	@Help( "list the session's projects" )
	public void main( Session session, Log log )
	{
		log.html( "<br/>Project list:<br/>" );
		List<Project> list = new ArrayList<>();
		list.addAll( session.projects().values() );
		Collections.sort( list, new Comparator<Project>()
		{
			@Override
			public int compare( Project o1, Project o2 )
			{
				return Gav.alphabeticalComparator.compare( o1.getGav(), o2.getGav() );
			}
		} );
		for( Project project : list )
			log.html( project + "<br/>" );
	}

	@Help( "list the session's projects - with details. Parameter is a filter for the GAVs" )
	public void details( Session session, CommandOptions options, FilteredGAVs gavFilter, Log logi )
	{
		assert gavFilter != null;

		logi.html( "projects details filtered by '" + gavFilter.getFilter() + "':<br/>" );
		logi.html( "<i>possible options: managed, nofetch, offline</i><br/>" );

		StringBuilder log = new StringBuilder();
		List<Project> list = gavFilter.getGavs( session ).stream().map( gav -> session.projects().forGav( gav ) ).filter( p -> p != null ).sorted( Project.alphabeticalComparator )
				.collect( toList() );
		if( list.isEmpty() )
		{
			logi.html( "found no project corresponding to your search...<br/>" );
			return;
		}

		log.append( "<div class='projects'>" );

		boolean showManagedDependencies = options.hasFlag( "managed" );
		boolean fetchMissingProjects = !options.hasFlag( "nofetch" );
		boolean online = !options.hasFlag( "offline" );

		for( Project project : list )
		{
			MavenProject mavenProject = project.getMavenProject();

			log.append( "<div class='project'>" );

			log.append( "<div class='title'><span class='packaging'>" + mavenProject.getModel().getPackaging() + "</span>" );
			if( project.isBuildable() )
				log.append( "<span class='badge'>buildable</span>" );

			Set<Gav> missingProjects = null;// project.getMissingGavsForResolution( logi, null );
			// if( missingProjects != null && !missingProjects.isEmpty() )
			// log.append( "<span class='badge error'>not resolvable</span>" );

			log.append( "<span class='gav'>" + project.getGav().getGroupId() + ":<span class='artifactId'>" + project.getGav().getArtifactId() + "</span>:"
					+ project.getGav().getVersion() + "</span>" );
			log.append( "</div>" );

			log.append( "<div class='properties'>" );
			showMissingProjects( log, missingProjects );
			log.append( "<div><div>file</div><div>" + project.getPomFile().getAbsolutePath() + "</div></div>" );
			showParenChain( log, session, project );
			showReferences( log, session.graph().read(), project, session, logi );
			showScm( log, mavenProject );
			showProperties( session, log, project );
			showDependencyManagement( project, log, logi );
			showPluginManagement( project, log, logi );
			showDependencies( project, log, logi );
			showPlugins( project, log, logi );
			showTransitiveDependencies( showManagedDependencies, fetchMissingProjects, online, log, session.graph().read(), project, session, logi );
			log.append( "</div>" );

			log.append( "</div>" );
		}

		log.append( "</div>" );

		logi.html( log.toString() );
	}

	private void showMissingProjects( StringBuilder log, Set<Gav> missingProjects )
	{
		if( missingProjects != null && !missingProjects.isEmpty() )
		{
			log.append( "<div><div>missing projects</div><div style='color:orange;'>" );
			for( Gav missingProject : missingProjects )
				log.append( missingProject + "<br/>" );
			log.append( "</div></div>" );
		}
	}

	private void showPlugins( Project project, StringBuilder sb, Log log )
	{
		MavenProject mavenProject = project.getMavenProject();
		if( mavenProject.getBuildPlugins() != null && !mavenProject.getBuildPlugins().isEmpty() )
		{
			sb.append( "<div><div>build plugins</div><div>" );
			mavenProject.getBuildPlugins().stream().map( p -> new Gav( p.getGroupId(), p.getArtifactId(), p.getVersion() ) ).sorted( Gav.alphabeticalComparator ).forEach( gav -> {
				showGav( project, gav, sb, log );
				sb.append( "<br/>" );
			} );
			sb.append( "</div></div>" );
		}
	}

	private void showPluginManagement( Project project, StringBuilder sb, Log log )
	{
		MavenProject mavenProject = project.getMavenProject();
		if( mavenProject.getPluginManagement() != null && mavenProject.getPluginManagement().getPlugins() != null && !mavenProject.getPluginManagement().getPlugins().isEmpty() )
		{
			sb.append( "<div><div>plugin management</div><div>" );
			mavenProject.getPluginManagement().getPlugins().stream().map( p -> new Gav( p.getGroupId(), p.getArtifactId(), p.getVersion() ) ).sorted( Gav.alphabeticalComparator )
					.forEach( gav -> {
						showGav( project, gav, sb, log );
						sb.append( "<br/>" );
					} );
			sb.append( "</div></div>" );
		}
	}

	private void showDependencies( Project project, StringBuilder sb, Log log )
	{
		MavenProject mavenProject = project.getMavenProject();
		if( mavenProject.getDependencies() != null && !mavenProject.getDependencies().isEmpty() )
		{
			sb.append( "<div><div>dependencies</div><div>" );
			mavenProject.getDependencies().stream()
					.map( d -> new Dependency( d.getGroupId(), d.getArtifactId(), d.getVersion(), Scope.fromString( d.getScope() ), d.getClassifier(), d.getType() ) )
					.sorted( Dependency.alphabeticalComparator ).forEach( dependency -> {
						showDependency( project, dependency, sb, log );
						sb.append( "<br/>" );
					} );
			sb.append( "</div></div>" );
		}
	}

	private void showDependencyManagement( Project project, StringBuilder sb, Log log )
	{
		MavenProject mavenProject = project.getMavenProject();
		if( mavenProject.getDependencyManagement() != null && !mavenProject.getDependencyManagement().getDependencies().isEmpty() )
		{
			sb.append( "<div><div>dependency management</div><div>" );
			mavenProject.getDependencyManagement().getDependencies().stream()
					.map( d -> new Dependency( d.getGroupId(), d.getArtifactId(), d.getVersion(), Scope.fromString( d.getScope() ), d.getClassifier(), d.getType() ) )
					.sorted( Dependency.alphabeticalComparator ).forEach( dependency -> {
						showDependency( project, dependency, sb, log );
						sb.append( "<br/>" );
					} );
			sb.append( "</div></div>" );
		}
	}

	private void showGav( Project project, Gav d, StringBuilder sb, Log log )
	{
		showDifferences( project, d.getGroupId(), sb, log );
		sb.append( ":" );
		showDifferences( project, d.getArtifactId(), sb, log );
		sb.append( ":" );
		showDifferences( project, d.getVersion(), sb, log );
	}

	private void showDependency( Project project, Dependency d, StringBuilder sb, Log log )
	{
		showDifferences( project, d.getGroupId(), sb, log );
		sb.append( ":" );
		showDifferences( project, d.getArtifactId(), sb, log );
		sb.append( ":" );
		showDifferences( project, d.getVersion(), sb, log );
		sb.append( ":" + d.getScope() );
		if( d.getClassifier() != null )
		{
			sb.append( ":" );
			showDifferences( project, d.getClassifier(), sb, log );
		}
		if( d.getType() != null )
		{
			sb.append( ":" );
			showDifferences( project, d.getType(), sb, log );
		}
	}

	private void showDifferences( Project project, String value, StringBuilder sb, Log log )
	{
		if( Tools.isMavenVariable( value ) )
			sb.append( "<span class='inline-badge'>" + project.resolveProperty( log, value ) + " (" + Tools.getPropertyNameFromPropertyReference( value ) + ")</span>" );
		else
			sb.append( value );
	}

	private void showProperties( Session session, StringBuilder log, Project project )
	{
		Project current = project;
		boolean first = true;

		while( current != null )
		{
			MavenProject mavenProject = current.getMavenProject();
			Properties properties = mavenProject.getProperties();
			if( properties != null && !properties.isEmpty() )
			{
				if( current != project )
				{
					if( first )
					{
						log.append( "<div><div>properties</div><div>" );
						first = false;
					}

					log.append( "<span style='font-style:italic;'><b>parent properties in " + current.getGav() + "</b>:</span><br/>" );
				}

				for( Entry<Object, Object> e : properties.entrySet() )
				{
					if( first )
					{
						log.append( "<div><div>properties</div><div>" );
						first = false;
					}

					if( current == project )
						log.append( e.getKey() + " = " + e.getValue() + "<br/>" );
					else
						log.append( "<span style='font-style:italic;'>" + e.getKey() + " = " + e.getValue() + "</span><br/>" );
				}
			}

			Gav parentGav = current.getParent();
			if( parentGav != null )
				current = session.projects().forGav( parentGav );
			else
				current = null;
		}

		if( !first )
			log.append( "</div></div>" );
	}

	private void showScm( StringBuilder log, MavenProject mavenProject )
	{
		Scm scm = mavenProject.getScm();
		if( scm != null )
		{
			log.append( "<div><div>scm</div><div>" );
			log.append( "connection: " + scm.getConnection() + "<br/>" );
			log.append( "developper connection: " + scm.getDeveloperConnection() + "<br/>" );
			log.append( "tag: " + scm.getTag() + "<br/>" );
			log.append( "url: " + scm.getUrl() + "<br/>" );
			log.append( "</div></div>" );
		}
	}

	private void showReferences( StringBuilder sb, PomGraphReadTransaction tx, Project project, Session session, Log logi )
	{
		Set<Relation> relations = tx.relationsReverse( project.getGav() );
		if( !relations.isEmpty() )
		{
			sb.append( "<div><div>referenced by</div><div>" );
			relations.stream().sorted( ( a, b ) -> tx.sourceOf( a ).toString().compareTo( tx.sourceOf( b ).toString() ) ).forEach( relation -> {
				Gav source = tx.sourceOf( relation );

				RelationType type = relation.getRelationType();

				sb.append( source + " references this project as a " + type + "<br/>" );
			} );
			sb.append( "</div></div>" );
		}
	}

	private void showTransitiveDependencies( boolean showManaged, boolean fetchMissingProjects, boolean online, StringBuilder sb, PomGraphReadTransaction tx, Project project,
			Session session, Log log )
	{
		sb.append( "<div><div>transitive dependencies</div><div>" );

		DependencyNode dependencyNode = project.getDependencyTree( true, online, log );
		Map<DependencyKey, DependencyNode> dependencies = new HashMap<>();
		dependencyNode.visitDepth( n -> dependencies.put( n.getKey(), n ) );

		Map<DependencyKey, DependencyNode> compileDependencies = new HashMap<>();
		Map<DependencyKey, DependencyNode> testDependencies = new HashMap<>();
		Map<DependencyKey, DependencyNode> runtimeDependencies = new HashMap<>();
		Map<DependencyKey, DependencyNode> providedDependencies = new HashMap<>();
		Map<DependencyKey, DependencyNode> systemDependencies = new HashMap<>();
		Map<DependencyKey, DependencyNode> managedDependencies = new HashMap<>();

		dependencies.entrySet().forEach( entry -> {
			DependencyKey key = entry.getKey();
			DependencyNode node = entry.getValue();

			Scope scope = node.getVs().getScope();
			if( scope == null )
				scope = Scope.COMPILE;
			switch( scope )
			{
				case COMPILE:
					compileDependencies.put( key, node );
					break;
				case RUNTIME:
					runtimeDependencies.put( key, node );
					break;
				case PROVIDED:
					providedDependencies.put( key, node );
					break;
				case TEST:
					testDependencies.put( key, node );
					break;
				case SYSTEM:
					systemDependencies.put( key, node );
					break;
				default:
					break;
			}
		} );

		maybeShowDeps( "compile scoped", compileDependencies, sb );
		maybeShowDeps( "runtime scoped", runtimeDependencies, sb );
		maybeShowDeps( "provided scoped", providedDependencies, sb );
		maybeShowDeps( "system scoped", systemDependencies, sb );
		maybeShowDeps( "test scoped", testDependencies, sb );
		if( showManaged )
			maybeShowDeps( "managed", managedDependencies, sb );
		else
			sb.append( "<br/><b>To show managed dependencies, use the '-managed' option.</b><br/>" );

		sb.append( "</div></div>" );
	}

	private void maybeShowDeps( String title, Map<DependencyKey, DependencyNode> deps, StringBuilder sb )
	{
		if( deps.isEmpty() )
		{
			sb.append( "<b>no " + title + " dependencies.</b><br/>" );
		}
		else
		{
			sb.append( "<b>" + title + " dependencies:</b><br/>" );
			deps.entrySet().stream().sorted( ( a, b ) -> a.getKey().toString().compareTo( b.getKey().toString() ) ).forEach( entry -> {
				DependencyKey key = entry.getKey();
				DependencyNode n = entry.getValue();
				sb.append( key + ":" + n.getVs().getVersion() + ":" + n.getVs().getScope() );
				sb.append( ", <i>declared level " + n.getLevel() + " in " + n.getProject().getGav() + "</i>" );
				// if( n.getLevel()>1)
				// sb.append( ", <i>originated by " + n.d.getOriginatingDependency() + "</i>" );
					sb.append( "<br/>" );
				} );
		}

	}

	private void showParenChain( StringBuilder sb, Session session, Project project )
	{
		sb.append( "<div><div>parent chain</div><div>" );

		boolean first = true;
		Gav parent = project.getParent();
		if( parent == null )
		{
			sb.append( "-" );
		}
		{
			while( parent != null )
			{
				if( !first )
					sb.append( " -> " );

				first = false;
				sb.append( parent );

				parent = session.graph().read().parent( parent );
			}
		}

		sb.append( "</div></div>" );
	}
}
