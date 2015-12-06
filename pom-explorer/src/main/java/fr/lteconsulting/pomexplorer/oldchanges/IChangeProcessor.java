package fr.lteconsulting.pomexplorer.oldchanges;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.depanalyze.Location;

public interface IChangeProcessor
{
	void processChange( Session session, Log log, Change<? extends Location> change, IChangeSet changeSet );
}