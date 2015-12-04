package fr.lteconsulting.pomexplorer.changes.processor;

import java.util.Set;

import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.PomSection;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.changes.Change;
import fr.lteconsulting.pomexplorer.changes.GavChange;
import fr.lteconsulting.pomexplorer.changes.IChangeSet;
import fr.lteconsulting.pomexplorer.depanalyze.Location;

/**
 * When a project's gav is changed, this impacts projects which depends on it
 */
public class FollowGavProcessor extends AbstractGavChangeProcessor
{
	@Override
	protected void processChange( WorkingSession session, ILogger log, GavChange change, IChangeSet changeSet )
	{
		if( change.getLocation().getSection() != PomSection.PROJECT )
			return;

		Set<Location> locations = Tools.getDirectDependenciesLocations( session, log, change.getLocation().getGav() );

		for( Location location : locations )
			changeSet.addChange( Change.create( location, change.getNewGav() ), change );
	}
}
