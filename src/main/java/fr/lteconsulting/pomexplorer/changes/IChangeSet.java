package fr.lteconsulting.pomexplorer.changes;

import fr.lteconsulting.pomexplorer.depanalyze.Location;

public interface IChangeSet
{
	/**
	 * Adds a change in the change set.
	 */
	void addChange( Change<? extends Location> change );

	/**
	 * Removes a change from the change set.
	 */
	void removeChange( Change<? extends Location> change );
}