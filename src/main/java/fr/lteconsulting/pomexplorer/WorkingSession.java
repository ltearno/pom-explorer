package fr.lteconsulting.pomexplorer;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;

public class WorkingSession
{
	private DirectedGraph<GAV, Dep> g = new DirectedMultigraph<GAV, Dep>( Dep.class );

	private HashMap<String, GAV> gavs = new HashMap<>();

	private Map<GAV, Project> projects = new HashMap<>();

	public DirectedGraph<GAV, Dep> getGraph()
	{
		return g;
	}

	public Map<String, GAV> getGavs()
	{
		return gavs;
	}

	public boolean hasArtifact( GAV gav )
	{
		return hasArtifact( gav.getGroupId(), gav.getArtifactId(), gav.getVersion() );
	}

	public boolean hasArtifact( String groupId, String artifactId, String version )
	{
		String sig = groupId + ":" + artifactId + ":" + version;
		GAV gav = gavs.get( sig );

		return gav != null;
	}

	public GAV registerArtifact( String groupId, String artifactId, String version )
	{
		String sig = groupId + ":" + artifactId + ":" + version;
		GAV gav = gavs.get( sig );

		if( gav == null )
		{
			gav = new GAV( groupId, artifactId, version );
			
			gavs.put( sig, gav );
			g.addVertex( gav );
		}

		return gav;
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

	public Map<GAV, Project> getProjects()
	{
		return projects;
	}
}
