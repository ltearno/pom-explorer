package fr.lteconsulting.pomexplorer.changes;

import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.depanalyze.Location;

public interface IChangeProcessor
{
	void processChange( WorkingSession session, ILogger log, Change<? extends Location> change, IChangeSet changeSet );
}