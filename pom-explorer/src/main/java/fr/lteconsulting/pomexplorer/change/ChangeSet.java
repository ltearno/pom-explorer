package fr.lteconsulting.pomexplorer.change;

public interface ChangeSet<T>
{
	T addChange( T change );

	void removeChange( T change );
}