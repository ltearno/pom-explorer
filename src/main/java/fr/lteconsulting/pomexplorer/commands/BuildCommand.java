package fr.lteconsulting.pomexplorer.commands;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.GAVRelation;

public class BuildCommand
{
	public String gav( WorkingSession session, String gavString )
	{
		GAV gav = Tools.string2Gav( gavString );
		if( gav == null )
			return "specify the GAV with the group:artifact:version format please";

		StringBuilder res = new StringBuilder();

		// build the project and all which depends on it

		Project project = session.projects().forGav( gav );
		if( project == null )
			return "cannot find the project for GAV " + gav;

		buildRec( session, project, res );

		return res.toString();
	}

	private void buildRec( WorkingSession session, Project project, StringBuilder log )
	{
		if( project == null )
		{
			log.append( "cannot find the project !" );
			return;
		}

		File directory = project.getPomFile().getParentFile();
		log.append( "cd " + directory + "<br/>" );
		log.append( "mvn install -DskipTests<br/>" );

		Set<GAVRelation<DependencyRelation>> dependents = session.graph().dependents( project.getGav() );
		Set<GAV> children = session.graph().children( project.getGav() );

		Set<GAV> toBuild = new HashSet<>();
		for( GAVRelation<DependencyRelation> d : dependents )
			toBuild.add( d.getSource() );
		toBuild.addAll( children );

		for( GAV gav : toBuild )
		{
			Project subProject = session.projects().forGav( gav );
			if( subProject == null )
			{
				log.append( "cannot find project for GAV " + gav + "<br/>" );
				continue;
			}

			buildRec( session, subProject, log );
		}
	}
}
