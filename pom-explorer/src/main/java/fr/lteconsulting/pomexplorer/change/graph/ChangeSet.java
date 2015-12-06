package fr.lteconsulting.pomexplorer.change.graph;

public interface ChangeSet<T>
{
	T addChange( T change );

	void removeChange( T change );
}