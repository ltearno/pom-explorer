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
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.graph.relation.GAVRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

public class ProjectsCommand
{
	@Help( "list the session's projects" )
	public String main( WorkingSession session )
	{
		StringBuilder res = new StringBuilder();

		res.append( "<br/>Project list:<br/>" );
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
			res.append( project + "<br/>" );

		return res.toString();
	}

	@Help( "list the session's projects - with details" )
	public String details( WorkingSession session )
	{
		return details( session, null );
	}

	@Help( "list the session's projects - with details. Parameter is a filter for the GAVs" )
	public String details( WorkingSession session, String gavFilter )
	{
		if( gavFilter != null )
			gavFilter = gavFilter.toLowerCase();

		StringBuilder res = new StringBuilder();

		res.append( "Projects details. Filter with: '" + gavFilter + "'<br/>" );

		for( Project project : session.projects().values() )
		{
			ParsedPomFile resolvedPom = project.getResolvedPom();
			MavenProject unresolvedProject = project.getUnresolvedPom();

			if( gavFilter != null && !project.getGav().toString().toLowerCase().contains( gavFilter ) )
				continue;

			res.append( "file : " + project.getPomFile().getAbsolutePath() + "<br/>" );
			res.append( "gav : " + project.getGav() + " " + resolvedPom.getPackagingType().getId() + ":" + resolvedPom.getPackagingType().getExtension() + ":" + resolvedPom.getPackagingType().getClassifier() + "<br/>" );

			Parent parent = unresolvedProject.getModel().getParent();
			if( parent != null )
				res.append( "parent : " + parent.getId() + ":" + parent.getRelativePath() + "<br/>" );

			Properties ptties = unresolvedProject.getProperties();
			if( ptties != null )
			{
				for( Entry<Object, Object> e : ptties.entrySet() )
					res.append( "property : " + e.getKey() + " = " + e.getValue() + "<br/>" );
			}

			if( unresolvedProject.getDependencyManagement() != null )
			{
				for( Dependency dependency : unresolvedProject.getDependencyManagement().getDependencies() )
					res.append( "dependency management : " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + ":" + dependency.getClassifier() + ":" + dependency.getScope() + "<br/>" );
			}

			for( Dependency dependency : unresolvedProject.getDependencies() )
				res.append( "dependency : " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + ":" + dependency.getClassifier() + ":" + dependency.getScope() + "<br/>" );

			res.append( "effective dependencies :<br/>" );
			Set<GAVRelation<? extends Relation>> directDependencies = effectiveDependencies( session, project.getGav() );
			directDependencies.forEach( d -> res.append( "[" + d.getRelation().getRelationType().shortName() + "] " + d.getTarget() + " " + d.getRelation().toString() + "<br/>" ) );

			res.append( "<br/>" );
		}

		return res.toString();
	}

	private Set<GAVRelation<? extends Relation>> effectiveDependencies( WorkingSession session, GAV gav )
	{
		HashSet<GAVRelation<? extends Relation>> res = new HashSet<>();

		GAV parent = session.graph().parent( gav );
		if( parent != null )
			res.addAll( effectiveDependencies( session, parent ) );

		res.addAll( session.graph().dependencies( gav ) );
		res.addAll( session.graph().buildDependencies( gav ) );

		return res;
	}
}
