package fr.lteconsulting.pomexplorer.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Parent;
import org.apache.maven.project.MavenProject;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.BuildDependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;

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

			Set<GAV> missingProjects = project.getMissingGavsForResolution( session, logi, null );
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

			if( !unresolvedProject.getDependencies().isEmpty() )
			{
				log.append( "<div><div>declared dependencies</div><div>" );
				for( Dependency dependency : unresolvedProject.getDependencies() )
				{
					log.append( dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() );
					if( dependency.getClassifier() != null )
						log.append( ":" + dependency.getClassifier() );
					if( dependency.getScope() != null )
						log.append( ":" + dependency.getScope() );
					log.append( "<br/>" );
				}
				log.append( "</div></div>" );
			}

			PomGraphReadTransaction tx = session.graph().read();

			log.append( "<div><div>effective dependencies</div><div>" );
			Set<DependencyRelation> directDependencies = tx.dependencies( project.getGav() );
			if( directDependencies.isEmpty() )
			{
				log.append( "no dependency<br/>" );
			}
				else
				{
					directDependencies.stream().sorted( ( a, b ) -> a.getTarget().toString().compareTo( b.getTarget().toString() ) )
							.forEach( d -> log.append( d.getTarget() + " " + d.toString() + "<br/>" ) );
				}
				log.append( "</div></div>" );

				Set<BuildDependencyRelation> buildDependencies = tx.buildDependencies( project.getGav() );
				if( !buildDependencies.isEmpty() )
				{
					log.append( "<div><div>effective build dependencies</div><div>" );

					buildDependencies.stream().sorted( ( a, b ) -> a.getTarget().toString().compareTo( b.getTarget().toString() ) )
							.forEach( d -> log.append( d.getTarget() + " " + d.toString() + "<br/>" ) );

					log.append( "</div></div>" );
				}

				log.append( "</div></div>" );
			} );

		log.append( "</div>" );

		logi.html( log.toString() );
	}
}
