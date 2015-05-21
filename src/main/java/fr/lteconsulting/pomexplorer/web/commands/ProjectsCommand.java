package fr.lteconsulting.pomexplorer.web.commands;

import java.util.Map.Entry;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Parent;
import org.apache.maven.project.MavenProject;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Project.DependencyInfo;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class ProjectsCommand extends BaseCommand
{
	public ProjectsCommand()
	{
		super( "projects" );
	}

	@Override
	public String execute( Client client, String[] params )
	{
		WorkingSession session = client.getCurrentSession();
		if( session == null )
			return "No working session associated, please create one.";

		if( params != null && params.length > 0 && "-v".equals( params[0] ) )
		{
			return verbose( session );
		}

		StringBuilder res = new StringBuilder();

		res.append( "<br/>Project list:<br/>" );
		for( Project project : session.getProjects().values() )
			res.append( project + "<br/>" );

		return res.toString();
	}

	private String verbose( WorkingSession session )
	{
		StringBuilder res = new StringBuilder();

		for( Project project : session.getProjects().values() )
		{
			res.append( "PROJECT FILE : " + project.getPomFile().getAbsolutePath() + "<br/>" );
			
			ParsedPomFile resolvedPom = project.getResolvedPom();
			MavenProject unresolvedProject = project.getUnresolvedPom();

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
