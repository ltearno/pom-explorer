package fr.lteconsulting.pomexplorer.commands;

import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.ApplicationSession;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.tools.FilteredGAVs;

public class BuildCommand
{
	@Help( "clear the build list. any currently building process will finish and no other will be started" )
	public void stop( ApplicationSession session, Log log )
	{
		session.cleanBuildList();

		log.html( "build list cleaned.<br/>" );
	}

	@Help( "build the project(s) with the specified gav(s)" )
	public void gav( ApplicationSession session, Log log, FilteredGAVs gavs )
	{
		gavs.getGavs( session.session() ).forEach( gav -> {
			Project project = session.projects().forGav( gav );
			if( project == null )
			{
				log.html( "cannot find the project for GAV " + gav + "<br/>" );
				return;
			}

			session.builder().buildProject( project, log );

			log.html( "project " + project + " marked for building<br/>" );
		} );
	}

	@Help( "builds all the watched projects, like if they were touched" )
	public void all( ApplicationSession session, Log log )
	{
		log.html( "all watched projects marked to be built." );
		session.builder().buildAll();
	}

	@Help( "adds a GAV to the list of maintained projects. A project needs to be found for that GAV." )
	public void maintain( Client client, ApplicationSession session, Log log, FilteredGAVs gavs )
	{
		PomGraphReadTransaction tx = session.graph().read();
		Set<Gav> toWatch = new HashSet<>();

		for( Gav gav : gavs.getGavs( session.session() ) )
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

			tx.relationsRec( gav ).stream().map( r -> tx.targetOf( r ) ).forEach( g -> toWatch.add( g ) );
		}

		log.html( "<br/>" );

		if( toWatch.isEmpty() )
		{
			log.html( "no project added to watch list.<br/>" );
		}
		else
		{
			log.html( "projects added to watch list:<br/>" );
			for( Gav g : toWatch )
			{
				Project p = session.projects().forGav( g );
				if( p == null || !p.isBuildable() )
					continue;

				log.html( Tools.logMessage( "watching " + p ) );
				session.projectsWatcher().watchProject( p, log );
			}
		}
	}

	@Help( "Displays the list of maintained projects in this working session." )
	public void listMaintained( ApplicationSession session, Log log )
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
}
