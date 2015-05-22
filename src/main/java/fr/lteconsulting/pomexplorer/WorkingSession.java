package fr.lteconsulting.pomexplorer;

import java.util.HashMap;
import java.util.Map;

import fr.lteconsulting.pomexplorer.graph.PomGraph;

public class WorkingSession
{
	private Map<GAV, Project> projects = new HashMap<>();

	private PomGraph graph = new PomGraph();
	
	public PomGraph graph()
	{
		return graph;
	}

	public boolean hasProject( GAV gav )
	{
		return projects.containsKey( gav );
	}

	public void registerProject( Project project )
	{
		System.out.println( "registered project " + project.getGav() );

		projects.put( project.getGav(), project );
	}

	public Map<GAV, Project> projects()
	{
		return projects;
	}
}
