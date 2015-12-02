package fr.lteconsulting.pomexplorer.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

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
	public void details( WorkingSession session, FilteredGAVs gavFilter, ILogger log )
	{
		log.html( "projects details " + (gavFilter != null ? ", filtered with '" + gavFilter.getFilter() + "'" : "") + " :<br/><br/>" );

		for( Project project : session.projects().values() )
		{
			MavenProject unresolvedProject = project.getMavenProject();

			if( gavFilter != null && !gavFilter.accept( project.getGav() ) )
				continue;

			log.html( "gav : " + project.getGav() + ", packaging: " + unresolvedProject.getModel().getPackaging() + "<br/>" );
			log.html( "file : " + project.getPomFile().getAbsolutePath() + "<br/>" );

			Parent parent = unresolvedProject.getModel().getParent();
			if( parent != null )
				log.html( "parent : " + parent.getId() + ":" + parent.getRelativePath() + "<br/>" );

			Properties ptties = unresolvedProject.getProperties();
			if( ptties != null )
			{
				for( Entry<Object, Object> e : ptties.entrySet() )
					log.html( "property : " + e.getKey() + " = " + e.getValue() + "<br/>" );
			}

			if( unresolvedProject.getDependencyManagement() != null )
			{
				for( Dependency dependency : unresolvedProject.getDependencyManagement().getDependencies() )
					log.html( "dependency management : " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + ":" + dependency.getClassifier() + ":" + dependency.getScope() + "<br/>" );
			}

			for( Dependency dependency : unresolvedProject.getDependencies() )
				log.html( "dependency : " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + ":" + dependency.getClassifier() + ":" + dependency.getScope() + "<br/>" );

			Set<? extends Relation> directDependencies = effectiveDependencies( session, project.getGav() );
			if( !directDependencies.isEmpty() )
				log.html( "effective dependencies :<br/>" );
			else
				log.html( "no dependency<br/>" );
			directDependencies.stream().sorted( ( a, b ) -> a.getTarget().toString().compareTo( b.getTarget().toString() ) )
					.forEach( d -> log.html( "[" + d.getRelationType().shortName() + "] " + d.getTarget() + " "
							+ d.toString() + "<br/>" ) );

			log.html( "<br/>" );
		}
	}

	private Set<Relation> effectiveDependencies( WorkingSession session, GAV gav )
	{
		HashSet<Relation> res = new HashSet<>();

		GAV parent = session.graph().parent( gav );
		if( parent != null )
			res.addAll( effectiveDependencies( session, parent ) );

		res.addAll( session.graph().dependencies( gav ) );
		res.addAll( session.graph().buildDependencies( gav ) );

		return res;
	}
}
