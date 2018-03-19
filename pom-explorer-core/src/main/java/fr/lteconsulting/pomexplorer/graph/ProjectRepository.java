package fr.lteconsulting.pomexplorer.graph;

import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.ProjectContainer;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.model.Gav;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectRepository implements ProjectContainer
{
	private final Session session;

	private final Map<Gav, Project> projects = new HashMap<>();

	public ProjectRepository( Session session )
	{
		this.session = session;
	}

	public boolean contains( Gav gav )
	{
		return projects.containsKey( gav );
	}

	public void add( Project project )
	{
		projects.put( project.getGav(), project );

		session.sendEventAddedProject( project );
	}

	public void remove( Project project )
	{
		projects.remove( project );
	}

	@Override
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

	/**
	 * Returns all submodules as {@link List} of {@link Project}s of the project with the given {@code gav}
	 * or an empty list if it does not have any submodules or they are not part of this repository.
	 *
	 * @throws RuntimeException if the pom of the submodule does not exist.
	 */
	public List<Project> getSubmodules( Gav gav ){
		return getSubmodulesAsStream( gav ).collect( Collectors.toList());
	}

	/**
	 * Returns all submodules as {@link Stream} of {@link Project} of the project with the given {@code gav}
	 * or an empty list if it does not have any submodules or they are not part of this repository.
	 *
	 * @throws RuntimeException if the pom of the submodule does not exist.
	 */
	public Stream<Project> getSubmodulesAsStream( Gav gav )
	{
		Project project = forGav( gav );
		return project.getSubmodules()
				.map( Project::getGav )
				.filter( x -> session.projects().contains( x ) )
				.map( x -> session.projects().forGav( x ) );
	}
}