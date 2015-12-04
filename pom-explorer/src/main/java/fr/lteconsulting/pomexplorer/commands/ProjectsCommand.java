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
import org.apache.maven.model.Parent;
import org.apache.maven.project.MavenProject;

import fr.lteconsulting.pomexplorer.Gav;
import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;

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
		logi.html( "projects details " + (gavFilter != null ? ", filtered with '" + gavFilter.getFilter() + "'" : "") + " :<br/><br/>" );

		StringBuilder log = new StringBuilder();

		log.append( "<div class='projects'>" );

		session.projects().values().stream().sorted( Tools.projectAlphabeticalComparator ).forEach( ( project ) ->
		{
			if( gavFilter != null && !gavFilter.accept( project.getGav() ) )
				return;

			MavenProject unresolvedProject = project.getMavenProject();

			log.append( "<div class='project'>" );

			log.append( "<div class='title'><span class='packaging'>" + unresolvedProject.getModel().getPackaging() + "</span>" );
			if( project.isBuildable() )
				log.append( "<span class='badge'>buildable</span>" );
			log.append( "<span class='gav'>" + project.getGav().getGroupId() + ":<span class='artifactId'>" + project.getGav().getArtifactId() + "</span>:" + project.getGav().getVersion() + "</span>" );
			log.append( "</div>" );

			Set<Gav> missingProjects = project.getMissingGavsForResolution( session, logi, null );
			if( missingProjects != null && !missingProjects.isEmpty() )
				log.append( "<span class='badge error'>not resolvable</span>" );

			log.append( "<div class='properties'>" );

			log.append( "<div><div>file</div><div>" + project.getPomFile().getAbsolutePath() + "</div></div>" );

			Parent parent = unresolvedProject.getModel().getParent();
			if( parent != null )
				log.append( "<div><div>parent</div><div>" + parent.getId() + "</div></div>" );

			Properties ptties = unresolvedProject.getProperties();
			if( ptties != null && !ptties.isEmpty() )
			{
				log.append( "<div><div>properties</div><div>" );
				for( Entry<Object, Object> e : ptties.entrySet() )
					log.append( e.getKey() + " = " + e.getValue() + "<br/>" );
				log.append( "</div></div>" );
			}

			if( unresolvedProject.getDependencyManagement() != null && !unresolvedProject.getDependencyManagement().getDependencies().isEmpty() )
			{
				log.append( "<div><div>dependency management</div><div>" );
				for( Dependency dependency : unresolvedProject.getDependencyManagement().getDependencies() )
					log.append( dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + ":" + dependency.getClassifier() + ":" + dependency.getScope() + "<br/>" );
				log.append( "</div></div>" );
			}

			Map<Gav, GavLocation> dependencies = project.getDependencies( session, logi );
			appendDependencies( "dependencies", dependencies.values(), log );

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
			appendMaybeDifferent( resolved.getGroupId(), unresolved.getGroupId(), log );
			log.append( ":" );
			appendMaybeDifferent( resolved.getArtifactId(), unresolved.getArtifactId(), log );
			log.append( ":" );
			appendMaybeDifferent( resolved.getVersion(), unresolved.getVersion(), log );
			log.append( "</i>" );
		}
	}

	private void appendMaybeDifferent( String a, String b, StringBuilder log )
	{
		if( a.equals( b ) )
			log.append( a );
		else
			log.append( "<b>" + b + "</b>" );
	}
}
