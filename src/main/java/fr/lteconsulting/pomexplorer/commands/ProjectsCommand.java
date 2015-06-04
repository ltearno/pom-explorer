package fr.lteconsulting.pomexplorer.commands;

import java.util.Map.Entry;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Parent;
import org.apache.maven.project.MavenProject;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;

import fr.lteconsulting.pomexplorer.DependencyInfo;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class ProjectsCommand
{
	@Help( "list the session's projects" )
	public String main( WorkingSession session )
	{
		StringBuilder res = new StringBuilder();

		res.append( "<br/>Project list:<br/>" );
		for( Project project : session.projects().values() )
			res.append( project + "<br/>" );

		return res.toString();
	}

	@Help( "list the session's projects - with details" )
	public String verbose( WorkingSession session )
	{
		return verbose( session, null );
	}

	@Help( "list the session's projects - with details. Parameter is a filter for the GAVs" )
	public String verbose( WorkingSession session, String gavFilter )
	{
		if( gavFilter != null )
			gavFilter = gavFilter.toLowerCase();

		StringBuilder res = new StringBuilder();

		res.append( "Verbose list of projects. Filter with: '" + gavFilter + "'<br/>" );

		for( Project project : session.projects().values() )
		{

			ParsedPomFile resolvedPom = project.getResolvedPom();
			MavenProject unresolvedProject = project.getUnresolvedPom();

			if( gavFilter != null && !project.getGav().toString().toLowerCase().contains( gavFilter ) )
				continue;

			res.append( "FILE : " + project.getPomFile().getAbsolutePath() + "<br/>" );
			res.append( "GAV : " + project.getGav() + " " + resolvedPom.getPackagingType().getId() + ":" + resolvedPom.getPackagingType().getExtension() + ":" + resolvedPom.getPackagingType().getClassifier() + "<br/>" );

			Parent parent = unresolvedProject.getModel().getParent();
			if( parent != null )
				res.append( "PARENT : " + parent.getId() + ":" + parent.getRelativePath() + "<br/>" );

			Properties ptties = unresolvedProject.getProperties();
			if( ptties != null )
			{
				for( Entry<Object, Object> e : ptties.entrySet() )
					res.append( "PPTY: " + e.getKey() + " = " + e.getValue() + "<br/>" );
			}

			if( unresolvedProject.getDependencyManagement() != null )
			{
				for( Dependency dependency : unresolvedProject.getDependencyManagement().getDependencies() )
					res.append( "MNGT: " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + ":" + dependency.getClassifier() + ":" + dependency.getScope() + "<br/>" );
			}

			for( Dependency dependency : unresolvedProject.getDependencies() )
				res.append( "DEP: " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + ":" + dependency.getClassifier() + ":" + dependency.getScope() + "<br/>" );

			for( DependencyInfo dependency : project.getDependencies().values() )
				res.append( " - depends on ->  " + dependency.toString() + "<br/>" );

			res.append( "<br/>" );
		}

		return res.toString();
	}
}
