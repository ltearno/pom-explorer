package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.traverse.TopologicalOrderIterator;

import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.webserver.MessageFactory;
import fr.lteconsulting.superman.Superman;

@Superman
public class Builder
{
	private final String talkId = MessageFactory.newGuid();

	private WorkingSession session;

	private Set<Project> projectsToBuild = new HashSet<>();

	private Project lastChangedProject;

	private Project lastErroredProject;

	public void setSession( WorkingSession session )
	{
		this.session = session;
	}

	public void clearJobs()
	{
		projectsToBuild.clear();
	}

	boolean justBuiltSomething = false;

	public void run()
	{
		while( true )
		{
			step();
		}
	}

	private void step()
	{
		Project changed = session.projectsWatcher().hasChanged();
		if( changed != null )
		{
			processProjectChange( session, changed );
			return;
		}

		try
		{
			Thread.sleep( 1000 );
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}

		Project toBuild = findProjectToBuild();
		if( toBuild != null )
		{
			lastErroredProject = null;
			lastChangedProject = toBuild;

			printBuildPipelineState( toBuild );

			boolean success = build( toBuild );

			if( success )
			{
				success( "build succesful for project " + toBuild.getGav() + " : " + toBuild );
			}
			else
			{
				error( "error during build ! this project and dependent ones are going to be removed from the build list.<br/>fix the problem which prevent the build to success and the build will restart automatically..." );
				dependentsAndSelf( toBuild.getGav() ).stream().map( g -> session.projects().forGav( g ) ).filter( p -> p != null ).forEach( p -> projectsToBuild.remove( p ) );
			}

			justBuiltSomething = true;
		}
		else
		{
			if( justBuiltSomething )
			{
				printBuildPipelineState( null );

				if( lastErroredProject == null )
					success( "build pipeline all up to date ! All artifacts have been built." );
				else
					error( "build pipeline in error because of " + lastErroredProject + "." );
			}

			justBuiltSomething = false;
		}
	}

	private void printBuildPipelineState( Project projectBuilding )
	{
		try
		{
			TopologicalOrderIterator<GAV, Relation> iterator = new TopologicalOrderIterator<>( session.graph().internalGraph() );
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
				if( project != null && inDependenciesOfMaintainedProjects( project ) )
				{
					sb.append( "<span class='" + (project == lastChangedProject ? "refreshedProject " : "") + (projectsToBuild.contains( project ) ? "toBuildProject " : "") + (projectBuilding == project ? "buildingProject " : "")
							+ (lastErroredProject == project ? "errorProject " : "") + (session.maintainedProjects().contains( project ) ? "maintainedProject " : "") + "'>" + project.getGav() + (session.maintainedProjects().contains( project ) ? " [maintained]" : "")
							+ (projectBuilding == project ? " [building]" : "") + (projectsToBuild.contains( project ) ? " [build waiting...]" : "") + (lastErroredProject == project ? " [project in error]" : "") + "</span><br/>" );
				}
			}
			sb.append( "<br/>" );

			log( sb.toString() );
		}
		catch( Exception e )
		{
			log( "error: " + e );
		}
	}

	/**
	 * Find the first project to be built in the graph's topological order
	 * 
	 * @return the project or null
	 */
	private Project findProjectToBuild()
	{
		try
		{
			TopologicalOrderIterator<GAV, Relation> iterator = new TopologicalOrderIterator<>( session.graph().internalGraph() );
			List<GAV> gavs = new ArrayList<>();
			while( iterator.hasNext() )
				gavs.add( iterator.next() );
			Collections.reverse( gavs );

			for( GAV gav : gavs )
			{
				Project project = session.projects().forGav( gav );
				if( project != null && projectsToBuild.contains( project ) && inDependenciesOfMaintainedProjects( project ) )
				{
					projectsToBuild.remove( project );
					return project;
				}
			}
		}
		catch( Exception e )
		{
			log( "error: " + e );
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
		HashSet<GAV> res = new HashSet<>();
		res.add( gav );
		session.graph().relationsRec( gav ).stream().forEach( r -> res.add( r.getTarget() ) );
		return res;
	}

	private Set<GAV> dependentsAndSelf( GAV gav )
	{
		HashSet<GAV> res = new HashSet<>();
		res.add( gav );
		session.graph().relationsReverseRec( gav ).stream().forEach( r -> res.add( r.getSource() ) );
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
			MavenBuildTaskSuperman builder = new MavenBuildTaskSuperman();
			boolean res = builder.build( session, project, talkId );
			builder.stop();
			if( !res )
				lastErroredProject = project;
			return res;
		}
	}

	private void log( String message )
	{
		message = Tools.buildMessage( message );
		for( Client client : session.getClients() )
			client.sendHtml( talkId, message );
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
