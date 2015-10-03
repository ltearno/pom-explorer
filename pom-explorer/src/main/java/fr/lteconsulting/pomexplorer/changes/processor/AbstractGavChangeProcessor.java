package fr.lteconsulting.pomexplorer.changes.processor;

import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.changes.Change;
import fr.lteconsulting.pomexplorer.changes.GavChange;
import fr.lteconsulting.pomexplorer.changes.IChangeProcessor;
import fr.lteconsulting.pomexplorer.changes.IChangeSet;
import fr.lteconsulting.pomexplorer.depanalyze.Location;

public abstract class AbstractGavChangeProcessor implements IChangeProcessor
{
	abstract protected void processChange( WorkingSession session, ILogger log, GavChange change, IChangeSet changeSet );

	@Override
	public void processChange( WorkingSession session, ILogger log, Change<? extends Location> change, IChangeSet changeSet )
	{
		if( !(change instanceof GavChange) )
			return;

		processChange( session, log, (GavChange) change, changeSet );
	}
}
