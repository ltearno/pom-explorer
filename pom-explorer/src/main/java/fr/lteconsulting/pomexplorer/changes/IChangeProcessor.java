package fr.lteconsulting.pomexplorer.changes;

import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.depanalyze.Location;

public interface IChangeProcessor
{
	void processChange( WorkingSession session, StringBuilder log, Change<? extends Location> change, IChangeSet changeSet );
}