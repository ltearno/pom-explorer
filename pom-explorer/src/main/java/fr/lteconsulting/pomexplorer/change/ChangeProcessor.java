package fr.lteconsulting.pomexplorer.change;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Session;

public interface ChangeProcessor<T>
{
	void processChange( Session session, Log log, T change, ChangeSet<T> changeSet );
}