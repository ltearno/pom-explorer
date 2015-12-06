package fr.lteconsulting.pomexplorer.oldchanges;

import fr.lteconsulting.pomexplorer.depanalyze.Location;

public interface IChangeSet
{
	/**
	 * Adds a change in the change set.
	 */
	void addChange( Change<? extends Location> change, String causeMessage );

	/**
	 * Adds a change in the change set.
	 */
	void addChange( Change<? extends Location> change, Change<? extends Location> cause );

	/**
	 * Invalidate and remove a change from the change set.
	 */
	void invalidateChange( Change<? extends Location> change );
}