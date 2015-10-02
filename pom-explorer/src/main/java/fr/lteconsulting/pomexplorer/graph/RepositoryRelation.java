package fr.lteconsulting.pomexplorer.graph;

import java.util.HashSet;
import java.util.Set;

public class RepositoryRelation
{
	private final Set<String> relations = new HashSet<>();

	public void addRelation( String name )
	{
		relations.add( name );
	}

	@Override
	public String toString()
	{
		return relations.toString();
	}
}
