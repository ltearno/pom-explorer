package fr.lteconsulting.pomexplorer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.lteconsulting.pomexplorer.model.Gav;

public class GraphQuery
{
	private final Set<Gav> roots;

	private final static Map<String, GraphQuery> queries = new HashMap<>();

	public static String register( Set<Gav> roots )
	{
		String id = Integer.toHexString( System.identityHashCode( new Object() ) );
		queries.put( id, new GraphQuery( roots ) );
		return id;
	}

	public static GraphQuery get( String id )
	{
		return queries.get( id );
	}

	public GraphQuery( Set<Gav> roots )
	{
		this.roots = roots;
	}

	public Set<Gav> getRoots()
	{
		return roots;
	}
}
