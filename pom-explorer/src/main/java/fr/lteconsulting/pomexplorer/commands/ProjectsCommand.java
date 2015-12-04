package fr.lteconsulting.pomexplorer.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;

import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;
import fr.lteconsulting.pomexplorer.model.Gav;

public class ProjectsCommand
{
	@Help( "list the session's projects" )
	public void main( WorkingSession session, ILogger log )
	{
		log.html( "<br/>Project list:<br/>" );
		List<Project> list = new ArrayList<>();
		list.addAll( session.projects().values() );
		Collections.sort( list, new Comparator<Project>()
		{
			@Override
			public int compare( Project o1, Project o2 )
			{
				return Tools.gavAlphabeticalComparator.compare( o1.getGav(), o2.getGav() );
			}
		} );
		for( Project project : list )
			log.html( project + "<br/>" );
	}

	@Help( "list the session's projects - with details" )
	public void details( WorkingSession session, ILogger log )
	{
		details( session, null, log );
	}

	@Help( "list the session's projects - with details. Parameter is a filter for the GAVs" )
	public void details( WorkingSession session, FilteredGAVs gavFilter, ILogger logi )
	{
		logi.html( "projects details " + (gavFilter != null ? ", filtered with '" + gavFilter.getFilter() + "'" : "")
				+ " :<br/><br/>" );

		StringBuilder log = new StringBuilder();

		log.append( "<div class='projects'>" );

		session
				.projects()
				.values()
				.stream()
				.sorted( Tools.projectAlphabeticalComparator )
				.forEach(
						( project ) ->
						{
							if( gavFilter != null && !gavFilter.accept( project.getGav() ) )
								return;

							MavenProject mavenProject = project.getMavenProject();

							log.append( "<div class='project'>" );

							log.append( "<div class='title'><span class='packaging'>" + mavenProject.getModel().getPackaging()
									+ "</span>" );
							if( project.isBuildable() )
								log.append( "<span class='badge'>buildable</span>" );

							Set<Gav> missingProjects = project.getMissingGavsForResolution( session, logi, null );
							if( missingProjects != null && !missingProjects.isEmpty() )
								log.append( "<span class='badge error'>not resolvable</span>" );

							log.append( "<span class='gav'>" + project.getGav().getGroupId() + ":<span class='artifactId'>"
									+ project.getGav().getArtifactId() + "</span>:" + project.getGav().getVersion() + "</span>" );
							log.append( "</div>" );

							log.append( "<div class='properties'>" );

							if( missingProjects != null && !missingProjects.isEmpty() )
							{
								log.append( "<div><div>missing projects</div><div style='color:orange;'>" );
								for( Gav missingProject : missingProjects )
									log.append( missingProject + "<br/>" );
								log.append( "</div></div>" );
							}

							log.append( "<div><div>file</div><div>" + project.getPomFile().getAbsolutePath() + "</div></div>" );

							Gav parentGav = project.getParent();
							if( parentGav != null )
								log.append( "<div><div>parent</div><div>" + parentGav + "</div></div>" );

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

							Properties ptties = mavenProject.getProperties();
							if( ptties != null && !ptties.isEmpty() )
							{
								log.append( "<div><div>properties</div><div>" );
								for( Entry<Object, Object> e : ptties.entrySet() )
									log.append( e.getKey() + " = " + e.getValue() + "<br/>" );
								log.append( "</div></div>" );
							}

							if( mavenProject.getDependencyManagement() != null
									&& !mavenProject.getDependencyManagement().getDependencies().isEmpty() )
							{
								log.append( "<div><div>dependency management</div><div>" );
								for( Dependency dependency : mavenProject.getDependencyManagement().getDependencies() )
								{
									Gav unresolvedGav = new Gav( dependency.getGroupId(), dependency.getArtifactId(), dependency
											.getVersion() );
									Gav resolvedGav = project.resolveGav( unresolvedGav, session, logi, true, false );

									log.append( resolvedGav.toString() );

									if( dependency.getClassifier() != null )
										log.append( ":" + dependency.getClassifier() );
									if( dependency.getScope() != null )
										log.append( ":" + dependency.getScope() );

									appendGavIfDifferent( resolvedGav, unresolvedGav, log );

									log.append( "<br/>" );
								}
								log.append( "</div></div>" );
							}

							List<GavLocation> dependencies = project.getDependencies( session, logi );
							appendDependencies( "dependencies", dependencies, log );

							Map<Gav, GavLocation> buildDependencies = project.getPluginDependencies( session, logi );
							appendDependencies( "build dependencies", buildDependencies.values(), log );

							log.append( "</div></div>" );
						} );

		log.append( "</div>" );

		logi.html( log.toString() );
	}

	private void appendDependencies( String title, Collection<GavLocation> dependencies, StringBuilder log )
	{
		if( dependencies.isEmpty() )
			return;

		log.append( "<div><div>" + title + "</div><div>" );
		dependencies.stream().sorted( ( a, b ) -> a.getResolvedGav().toString().compareTo( b.getResolvedGav().toString() ) )
				.forEach( d ->
				{
					log.append( d.getResolvedGav() );

					if( d.getClassifier() != null )
						log.append( ":" + d.getClassifier() );

					if( d.getScope() != null )
						log.append( ":" + d.getScope() );

					appendGavIfDifferent( d.getResolvedGav(), d.getUnresolvedGav(), log );

					log.append( "<br/>" );
				} );
		log.append( "</div></div>" );
	}

	private void appendGavIfDifferent( Gav resolved, Gav unresolved, StringBuilder log )
	{
		if( !resolved.equals( unresolved ) )
		{
			log.append( " <i>declared: " );
			appendEmphasizeDifference( resolved.getGroupId(), unresolved.getGroupId(), log );
			log.append( ":" );
			appendEmphasizeDifference( resolved.getArtifactId(), unresolved.getArtifactId(), log );
			log.append( ":" );
			appendEmphasizeDifference( resolved.getVersion(), unresolved.getVersion(), log );
			log.append( "</i>" );
		}
	}

	private void appendEmphasizeDifference( String a, String b, StringBuilder log )
	{
		if( a.equals( b ) )
			log.append( a );
		else
			log.append( "<b>" + b + "</b>" );
	}
}
