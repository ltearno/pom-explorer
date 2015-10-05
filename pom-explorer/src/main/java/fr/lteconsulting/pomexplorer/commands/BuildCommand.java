package fr.lteconsulting.pomexplorer.commands;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.GAVRelation;

public class BuildCommand
{
	@Help( "clear the build list. any currently building process will finish and no other will be started" )
	public void stop( WorkingSession session, ILogger log )
	{
		session.cleanBuildList();

		log.html( "build list cleaned.<br/>" );
	}

	@Help( "build the project and all which depends on it" )
	public void gav( WorkingSession session, ILogger log, GAV gav )
	{
		Project project = session.projects().forGav( gav );
		if( project == null )
		{
			log.html( "cannot find the project for GAV " + gav + "<br/>" );
			return;
		}

		buildRec( session, project, log );
	}

	@Help( "builds all the watched projects, like if they were touched" )
	public void all( WorkingSession session, ILogger log )
	{
		log.html( "all watched projects marked to be built." );
		session.builder().buildAll();
	}

	@Help( "adds a GAV to the list of maintained projects. A project needs to be found for that GAV." )
	public void maintain( Client client, WorkingSession session, ILogger log, FilteredGAVs gavs )
	{
		Set<GAV> toWatch = new HashSet<>();

		for( GAV gav : gavs.getGavs( session ) )
		{
			Project project = session.projects().forGav( gav );
			if( project == null )
			{
				log.html( Tools.warningMessage( "cannot find the project for GAV " + gav ) );
				continue;
			}

			log.html( "adding project " + project + " and its dependencies to the set of maintained projects<br/>" );

			session.maintainedProjects().add( project );

			toWatch.add( gav );
			session.graph().relationsRec( gav ).stream().map( r -> r.getTarget() ).distinct().forEach( g -> toWatch.add( g ) );
		}

		log.html( "<br/>" );

		if( toWatch.isEmpty() )
		{
			log.html( "no project added to watch list.<br/>" );
		}
		else
		{
			log.html( "projects added to watch list:<br/>" );
			for( GAV g : toWatch )
			{
				Project p = session.projects().forGav( g );
				if( p == null )
					continue;

				try
				{
					session.projectsWatcher().watchProject( p );
					log.html( Tools.logMessage( "watching " + p ) );
				}
				catch( IOException e )
				{
					log.html( Tools.errorMessage( "error while trying to watch project ! " + e ) );
				}
			}
		}
	}

	@Help( "Displays the list of maintained projects in this working session." )
	public void listMaintained( WorkingSession session, ILogger log )
	{
		if( session.maintainedProjects().isEmpty() )
		{
			log.html( "No project is currently maintained<br/>" );
		}
		else
		{
			log.html( "Maintained projects :<br/>" );
			log.html( "<ul>" );
			for( Project project : session.maintainedProjects() )
				log.html( "<li>" + project + "</li>" );
			log.html( "</ul>" );
		}
	}

	private void buildRec( WorkingSession session, Project project, ILogger log )
	{
		if( project == null )
		{
			log.html( "cannot find the project !" );
			return;
		}

		File directory = project.getPomFile().getParentFile();
		log.html( "cd " + directory + "<br/>" );
		log.html( "mvn install -N -DskipTests<br/>" );

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
				log.html( "cannot find project for GAV " + gav + "<br/>" );
				continue;
			}

			buildRec( session, subProject, log );
		}
	}
}
