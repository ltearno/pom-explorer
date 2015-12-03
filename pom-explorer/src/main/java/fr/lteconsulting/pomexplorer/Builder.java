package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.traverse.TopologicalOrderIterator;

import fr.lteconsulting.autothreaded.AutoThreaded;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.webserver.MessageFactory;

@AutoThreaded
public class Builder
{
	private final String talkId = MessageFactory.newGuid();

	private final String pipelineStatusTalkId = "buildPipelineStatus";

	private WorkingSession session;

	private final Set<Project> projectsToBuild = new HashSet<>();

	private final Set<Project> projectsToBuildForced = new HashSet<>();

	private Project lastChangedProject;

	private final Set<Project> erroredProjects = new HashSet<>();

	public void setSession( WorkingSession session )
	{
		this.session = session;
	}

	public void clearJobs()
	{
		projectsToBuild.clear();
		projectsToBuildForced.clear();

		printBuildPipelineState( null );
	}

	public void buildProject( Project project )
	{
		projectsToBuildForced.add( project );

		printBuildPipelineState( null );
	}

	public void buildAll()
	{
		projectsToBuild.addAll( session.projectsWatcher().watchedProjects() );

		printBuildPipelineState( null );
	}

	protected void onEmptyMessageQueue()
	{
		step();
	}

	private void step()
	{
		try
		{
			Thread.sleep( 1000 );
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}

		if( session == null )
			return;

		Project changed = session.projectsWatcher().hasChanged();
		if( changed != null )
		{
			erroredProjects.remove( changed );
			lastChangedProject = changed;
			processProjectChange( session, changed );

			printBuildPipelineState( null );

			return;
		}

		Project toBuild = findProjectToBuild();
		if( toBuild != null )
		{
			printBuildPipelineState( toBuild );

			boolean success = build( toBuild );

			if( success )
			{
				success( "build succesful for project " + toBuild.getGav() + " : " + toBuild );
				erroredProjects.remove( toBuild );
			}
			else
			{
				erroredProjects.add( toBuild );

				error( "error building " + toBuild + " !<br/>this project and dependent ones are going to be removed from the build list.<br/>fix the problem which prevent the build to success and the build will restart automatically..." );
				dependentsAndSelf( toBuild.getGav() ).stream().map( g -> session.projects().forGav( g ) ).filter( p -> p != null ).forEach( p -> projectsToBuild.remove( p ) );
			}

			printBuildPipelineState( null );
		}
	}

	private void printBuildPipelineState( Project projectBuilding )
	{
		PomGraphReadTransaction tx = session.graph().read();
		
		try
		{
			TopologicalOrderIterator<GAV, Relation> iterator = new TopologicalOrderIterator<>( tx.internalGraph() );
			List<GAV> gavs = new ArrayList<>();
			while( iterator.hasNext() )
				gavs.add( iterator.next() );
			Collections.reverse( gavs );

			StringBuilder sb = new StringBuilder();

			sb.append( "<br/>" );
			sb.append( "build pipeline state:<br/>" );
			for( GAV gav : gavs )
			{
				Project project = session.projects().forGav( gav );
				if( project != null && (inDependenciesOfMaintainedProjects( project ) || projectsToBuildForced.contains( project )) )
				{
					sb.append( "<span class='" + (project == lastChangedProject ? "refreshedProject " : "") + (projectsToBuildForced.contains( project ) ? "BUILD FORCED " : "") + (projectsToBuild.contains( project ) ? "toBuildProject " : "")
							+ (projectBuilding == project ? "buildingProject " : "") + (erroredProjects.contains( project ) ? "errorProject " : "") + (session.maintainedProjects().contains( project ) ? "maintainedProject " : "") + "'>" + project.getGav()
							+ (session.maintainedProjects().contains( project ) ? " [maintained]" : "") + (projectBuilding == project ? " [building]" : "") + (projectsToBuild.contains( project ) ? " [build waiting...]" : "")
							+ (erroredProjects.contains( project ) ? " [project in error]" : "") + "</span><br/>" );
				}
			}
			sb.append( "<br/>" );

			logBuildPipeline( sb.toString() );
		}
		catch( Exception e )
		{
			logBuildPipeline( "error: " + e );
		}
	}

	/**
	 * Find the first project to be built in the graph's topological order
	 * 
	 * @return the project or null
	 */
	private Project findProjectToBuild()
	{
		PomGraphReadTransaction tx = session.graph().read();
		
		try
		{
			TopologicalOrderIterator<GAV, Relation> iterator = new TopologicalOrderIterator<>( tx.internalGraph() );
			List<GAV> gavs = new ArrayList<>();
			while( iterator.hasNext() )
				gavs.add( iterator.next() );
			Collections.reverse( gavs );

			for( GAV gav : gavs )
			{
				Project project = session.projects().forGav( gav );
				if( project != null && (!erroredProjects.contains( project )) && (projectsToBuildForced.contains( project ) || (projectsToBuild.contains( project ) && inDependenciesOfMaintainedProjects( project ))) )
				{
					projectsToBuild.remove( project );
					projectsToBuildForced.remove( project );
					return project;
				}
			}
		}
		catch( Exception e )
		{
			System.err.println( "error in Builder thread :" );
			e.printStackTrace();
		}

		return null;
	}

	private void processProjectChange( WorkingSession session, Project project )
	{
		if( project == null || projectsToBuild.contains( project ) )
			return;

		log( "project " + project + " has been modified, appending to build list..." );

		for( GAV gav : dependentsAndSelf( project.getGav() ) )
		{
			Project p = session.projects().forGav( gav );
			if( p != null )
			{
				if( !inDependenciesOfMaintainedProjects( p ) )
					continue;
				log( "add to build list : " + p.getGav() );
				projectsToBuild.add( p );
			}
		}
	}

	private boolean inDependenciesOfMaintainedProjects( Project project )
	{
		if( project == null )
			return false;

		Set<GAV> possibleGavs = new HashSet<>();
		for( Project p : session.maintainedProjects() )
			possibleGavs.addAll( dependenciesAndSelf( p.getGav() ) );

		boolean res = possibleGavs.contains( project.getGav() );
		return res;
	}

	private Set<GAV> dependenciesAndSelf( GAV gav )
	{
		PomGraphReadTransaction tx = session.graph().read();
		
		HashSet<GAV> res = new HashSet<>();
		res.add( gav );
		tx.relationsRec( gav ).stream().forEach( r -> res.add( r.getTarget() ) );
		return res;
	}

	private Set<GAV> dependentsAndSelf( GAV gav )
	{
		PomGraphReadTransaction tx = session.graph().read();
		HashSet<GAV> res = new HashSet<>();
		res.add( gav );
		tx.relationsReverseRec( gav ).stream().forEach( r -> res.add( r.getSource() ) );
		return res;
	}

	private boolean build( Project project )
	{
		boolean demo = false;

		if( demo )
		{
			log( "building " + project + "..." );
			File directory = project.getPomFile().getParentFile();
			log( "cd " + directory + "<br/>" );
			log( "mvn install -N -DskipTests<br/>" );
			try
			{
				Thread.sleep( 500 );
			}
			catch( InterruptedException e )
			{
			}
			log( project + " build done" );
			return true;
		}
		else
		{
			MavenBuildTaskAutoThreaded builder = new MavenBuildTaskAutoThreaded();
			boolean res = builder.build( session, project, talkId );
			builder.stop();
			return res;
		}
	}

	private void log( String message )
	{
		message = Tools.buildMessage( message );
		for( Client client : session.getClients() )
			client.sendHtml( talkId, message );
	}

	private void logBuildPipeline( String message )
	{
		message = Tools.buildMessage( message );
		for( Client client : session.getClients() )
			client.sendHtml( pipelineStatusTalkId, message );
	}

	private void error( String message )
	{
		message = Tools.errorMessage( message );
		for( Client client : session.getClients() )
			client.sendHtml( talkId, message );
	}

	private void success( String message )
	{
		message = Tools.successMessage( message );
		for( Client client : session.getClients() )
			client.sendHtml( talkId, message );
	}
}
