package fr.lteconsulting.pomexplorer.commands;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.GAVRelation;

public class BuildCommand
{
	@Help("clear the build list. any currently building process will finish and no other will be started")
	public String stop(WorkingSession session)
	{
		session.cleanBuildList();
		
		return "build list cleaned.";
	}
	
	@Help("build the project and all which depends on it")
	public String gav(WorkingSession session, GAV gav)
	{
		StringBuilder log = new StringBuilder();

		Project project = session.projects().forGav( gav );
		if( project == null )
			return "cannot find the project for GAV " + gav;

		buildRec(session, project, log);

		return log.toString();
	}

	@Help("Adds a GAV to the list of maintained projects. A project needs to be found for that GAV.")
	public String maintain(Client client, WorkingSession session, GAV gav)
	{
		StringBuilder log = new StringBuilder();

		Project project = session.projects().forGav(gav);
		if (project == null)
			return "cannot find the project for GAV " + gav;

		client.send("Adding project " + project + " to list of maintained projects, and watching dependencies...<br/>");

		session.maintainedProjects().add(project);
		log.append("Project " + project + " added to the list of maintained projects<br/>");

		log.append("detecting dependencies to be watched...<br/>");

		Set<GAV> toWatch = new HashSet<>();
		toWatch.add(gav);
		session.graph().relationsRec(gav).stream().map(r -> r.getTarget()).distinct().forEach(g -> toWatch.add(g));
		for(GAV g : toWatch)
		{
			Project p = session.projects().forGav(g);
			if (p == null)
				continue;

			try
			{
				session.projectsWatcher().watchProject(p);
				log.append(Tools.logMessage("watching " + p));
			}
			catch (IOException e)
			{
				log.append(Tools.errorMessage("error while trying to watch project ! " + e));
			}
		}

		return log.toString();
	}

	@Help("Displays the list of maintained projects in this working session.")
	public String listMaintained(WorkingSession session)
	{
		StringBuilder log = new StringBuilder();

		if (session.maintainedProjects().isEmpty())
		{
			log.append("No project is currently maintained<br/>");
		}
		else
		{
			log.append("Maintained projects :<br/>");
			log.append("<ul>");
			for (Project project : session.maintainedProjects())
				log.append("<li>" + project + "</li>");
			log.append("</ul>");
		}

		return log.toString();
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
		log.append("mvn install -N -DskipTests<br/>");

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
