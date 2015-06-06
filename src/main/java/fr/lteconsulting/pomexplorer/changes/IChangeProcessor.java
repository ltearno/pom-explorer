package fr.lteconsulting.pomexplorer.changes;

import fr.lteconsulting.pomexplorer.depanalyze.Location;

public interface IChangeProcessor
{
	void processChange(Change<? extends Location> change, IChangeSet changeSet);
}