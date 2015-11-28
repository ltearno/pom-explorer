package fr.lteconsulting.pomexplorer.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Project;

public class ProjectRepository
{
	private final Map<GAV, Project> projects = new HashMap<>();
	
	public boolean contains( GAV gav )
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

	public Project forGav( GAV gav )
	{
		return projects.get( gav );
	}

	public int size()
	{
		return projects.size();
	}

	public Set<GAV> keySet()
	{
		return projects.keySet();
	}

	public Collection<Project> values()
	{
		return projects.values();
	}
}