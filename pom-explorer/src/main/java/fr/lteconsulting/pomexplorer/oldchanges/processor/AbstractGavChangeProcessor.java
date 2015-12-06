package fr.lteconsulting.pomexplorer.oldchanges.processor;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.depanalyze.Location;
import fr.lteconsulting.pomexplorer.oldchanges.Change;
import fr.lteconsulting.pomexplorer.oldchanges.GavChange;
import fr.lteconsulting.pomexplorer.oldchanges.IChangeProcessor;
import fr.lteconsulting.pomexplorer.oldchanges.IChangeSet;

public abstract class AbstractGavChangeProcessor implements IChangeProcessor
{
	abstract protected void processChange( Session session, Log log, GavChange change, IChangeSet changeSet );

	@Override
	public void processChange( Session session, Log log, Change<? extends Location> change, IChangeSet changeSet )
	{
		if( !(change instanceof GavChange) )
			return;

		processChange( session, log, (GavChange) change, changeSet );
	}
}
