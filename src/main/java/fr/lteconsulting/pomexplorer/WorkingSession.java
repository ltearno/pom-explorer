package fr.lteconsulting.pomexplorer;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;

public class WorkingSession
{
	private DirectedGraph<GAV, Dep> g = new DirectedMultigraph<GAV, Dep>( Dep.class );

	private HashMap<String, GAV> gavs = new HashMap<>();

	public DirectedGraph<GAV, Dep> getGraph()
	{
		return g;
	}

	public Map<String, GAV> getGavs()
	{
		return gavs;
	}

	/**
	 * Can return null if not interested by the artifact
	 * 
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @param g
	 * @return
	 */
	public GAV ensureArtifact( String groupId, String artifactId, String version )
	{
		// if (!groupId.startsWith("fr."))
		// return null;

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
}
