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

import fr.lteconsulting.pomexplorer.ApplicationSession;
import fr.lteconsulting.pomexplorer.DefaultPomFileLoader;
import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Profile;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.ProjectTools;
import fr.lteconsulting.pomexplorer.TransitivityResolver;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.graph.relation.RelationType;
import fr.lteconsulting.pomexplorer.graph.relation.Scope;
import fr.lteconsulting.pomexplorer.model.DependencyKey;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.model.transitivity.DependencyNode;
import fr.lteconsulting.pomexplorer.tools.FilteredGAVs;

public class ProjectsCommand
{
	@Help( "list the session's projects" )
	public void main( ApplicationSession session, Log log )
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
	public void details( ApplicationSession session, CommandOptions options, FilteredGAVs gavFilter, Log logi )
	{
		assert gavFilter != null;

		logi.html( "projects details filtered by '" + gavFilter.getFilterDescription() + "':<br/>" );
		logi.html( "<i>possible options: managed, nofetch, offline, profiles</i><br/>" );

		// Is some profiles passed in option ?
		logi.html( "Read profiles to use in the analyze...<br/>" );
		Object optionP = options.getOption( "profiles" );
		Map<String, Profile> profiles = new HashMap<>();
		if( optionP != null )
		{
			String[] profilesTab = ((String) optionP).trim().split( "," );
			for( int i = 0; i < profilesTab.length; i++ )
			{
				profiles.put( profilesTab[i], new Profile( profilesTab[i] ) );
			}
		}

		List<Project> list = gavFilter
				.getGavs( session.session() )
				.stream()
				.map( gav -> session.projects().forGav( gav ) )
				.filter( p -> p != null )
				.sorted( Project.alphabeticalComparator )
				.collect( toList() );

		if( list.isEmpty() )
		{
			logi.html( "found no project corresponding to your search...<br/>" );
			return;
		}

		StringBuilder log = new StringBuilder();

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
			ProjectTools.showDependencyManagement( project, log, session.projects(), logi );
			ProjectTools.showPluginManagement( project, log, session.projects(), logi );
			ProjectTools.showDependencies( project, log, session.projects(), logi );
			ProjectTools.showPlugins( project, log, session.projects(), logi );
			showTransitiveDependencies( showManagedDependencies, fetchMissingProjects, online, log, session.graph().read(), project, profiles, session, logi );
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

	private void showProperties( ApplicationSession session, StringBuilder log, Project project )
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

			Gav parentGav = current.getParentGav();
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

	private void showReferences( StringBuilder sb, PomGraphReadTransaction tx, Project project, ApplicationSession session, Log logi )
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
			Map<String, Profile> profiles, ApplicationSession session, Log log )
	{
		sb.append( "<div><div>transitive dependencies</div><div>" );
		
		DefaultPomFileLoader loader = new DefaultPomFileLoader( session.session(), online );

		TransitivityResolver transitivityResolver = new TransitivityResolver();
		DependencyNode dependencyNode = transitivityResolver.getTransitiveDependencyTree( session.session(), project, true, online, profiles, loader, log );
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
			sb.append("<b>no ").append(title).append(" dependencies.</b><br/>");
		}
		else
		{
			sb.append("<b>").append(title).append(" dependencies:</b><br/>");
			deps.entrySet().stream().sorted(Comparator.comparing(a -> a.getKey().toString())).forEach(entry -> {
				DependencyKey key = entry.getKey();
				DependencyNode n = entry.getValue();
				sb.append(key).append(":").append(n.getVs().getVersion()).append(":").append(n.getVs().getScope());
				sb.append(", <i>declared level ").append(n.getLevel()).append(" in ").append(n.getProject().getGav()).append("</i>");
				// if( n.getLevel()>1)
				// sb.append( ", <i>originated by " + n.d.getOriginatingDependency() + "</i>" );
				sb.append( "<br/>" );
			} );
		}

	}

	private void showParenChain( StringBuilder sb, ApplicationSession session, Project project )
	{
		sb.append( "<div><div>parent chain</div><div>" );

		boolean first = true;
		Gav parent = project.getParentGav();
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
