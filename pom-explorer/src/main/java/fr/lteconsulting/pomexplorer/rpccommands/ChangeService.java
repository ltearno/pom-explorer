package fr.lteconsulting.pomexplorer.rpccommands;

import java.util.ArrayList;
import java.util.List;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange;
import fr.lteconsulting.pomexplorer.change.project.ProjectChange;

public class ChangeService
{
	public Object list( Session session, Log log )
	{
		ChangeList list = new ChangeList( new ArrayList<GraphChange>(), new ArrayList<ProjectChange>() );
		list.getGraphChanges().addAll( session.graphChanges() );
		list.getProjectChanges().addAll( session.projectChanges() );
		return list.getGraphChanges();
	}
}

class ChangeList
{
	private List<GraphChange> graphChanges;
	private List<ProjectChange> projectChanges;

	public ChangeList( List<GraphChange> graphChanges, List<ProjectChange> projectChanges )
	{
		this.graphChanges = graphChanges;
		this.projectChanges = projectChanges;
	}

	public List<GraphChange> getGraphChanges()
	{
		return graphChanges;
	}

	public List<ProjectChange> getProjectChanges()
	{
		return projectChanges;
	}
}