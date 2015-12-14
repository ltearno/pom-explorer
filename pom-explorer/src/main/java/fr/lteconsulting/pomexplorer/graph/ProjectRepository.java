package fr.lteconsulting.pomexplorer.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.PomAnalyzer;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.model.transitivity.Repository;

public class ProjectRepository
{
	private final Session session;

	private final Map<Gav, Project> projects = new HashMap<>();

	public ProjectRepository( Session session )
	{
		this.session = session;
	}
	
	public Project fetchProject( Gav gav, boolean online, Log log )
	{
		return fetchProject( gav, online, null, log );
	}

	public Project fetchProject( Gav gav, boolean online, List<Repository> additionalRepos, Log log )
	{
		if( contains( gav ) )
			return forGav( gav );

		PomAnalyzer analyzer = new PomAnalyzer();

		Project project = analyzer.fetchGavWithMaven( session, log, gav, online, additionalRepos );

		return project;
	}

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