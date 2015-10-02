package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.traverse.TopologicalOrderIterator;

import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.superman.Superman;

@Superman
public class Builder
{
	private WorkingSession session;

	private Set<Project> projectsToBuild = new HashSet<>();

	public void setSession( WorkingSession session )
	{
		this.session = session;
	}

	public void run()
	{
		while( true )
		{
			Project changed = session.projectsWatcher().hasChanged();
			if( changed != null )
			{
				processProjectChange( session, changed );
				continue;
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
				build( toBuild );
		}
	}

	/**
	 * Find the first project to be built in the graph's topological order
	 * 
	 * @return the project or null
	 */
	private Project findProjectToBuild()
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
		session.graph().dependenciesRec( gav ).stream().forEach( r -> res.add( r.getTarget() ) );
		return res;
	}

	private Set<GAV> dependentsAndSelf( GAV gav )
	{
		HashSet<GAV> res = new HashSet<>();
		res.add( gav );
		session.graph().dependentsRec( gav ).stream().forEach( r -> res.add( r.getSource() ) );
		return res;
	}

	private boolean build( Project project )
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

	private void log( String message )
	{
		message = Tools.warningMessage( message );
		for( Client client : session.getClients() )
			client.send( message );
	}
}
