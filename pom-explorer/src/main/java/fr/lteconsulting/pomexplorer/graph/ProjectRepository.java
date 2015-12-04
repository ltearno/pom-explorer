package fr.lteconsulting.pomexplorer.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.model.Gav;

public class ProjectRepository
{
	private final Map<Gav, Project> projects = new HashMap<>();

	public boolean contains( Gav gav )
	{
		return projects.containsKey( gav );
	}

	public void add( Project project )
	{
		projects.put( project.getGav(), project );
	}

	public void remove( Project project )
	{
		projects.remove( project );
	}

	public Project forGav( Gav gav )
	{
		return projects.get( gav );
	}

	public int size()
	{
		return projects.size();
	}

	public Set<Gav> keySet()
	{
		return projects.keySet();
	}

	public Collection<Project> values()
	{
		return projects.values();
	}
}