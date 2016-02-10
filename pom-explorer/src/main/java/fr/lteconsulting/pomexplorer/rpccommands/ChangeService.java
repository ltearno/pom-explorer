package fr.lteconsulting.pomexplorer.rpccommands;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange;
import fr.lteconsulting.pomexplorer.change.project.ProjectChange;

public class ChangeService
{
	private JsonExporter jsonExporter = new JsonExporter();

	public Object list( Session session, Log log ) throws IllegalArgumentException, IllegalAccessException
	{
		ChangeList list = new ChangeList( new ArrayList<GraphChange>(), new ArrayList<ProjectChange>() );
		list.getGraphChanges().addAll( session.graphChanges() );
		list.getProjectChanges().addAll( session.projectChanges() );

		JsonElement e = jsonExporter.export( list );

		return e;
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