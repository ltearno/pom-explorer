package fr.lteconsulting.pomexplorer.changes.processor;

import java.util.Set;

import fr.lteconsulting.pomexplorer.PomSection;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.changes.Change;
import fr.lteconsulting.pomexplorer.changes.GavChange;
import fr.lteconsulting.pomexplorer.changes.IChangeSet;
import fr.lteconsulting.pomexplorer.depanalyze.Location;

public class FollowGavProcessor extends AbstractGavChangeProcessor
{
	@Override
	protected void processChange( WorkingSession session, StringBuilder log, GavChange change, IChangeSet changeSet )
	{
		// if only change is a project gav change,
		if( change.getLocation().getSection() != PomSection.PROJECT )
			return;

		Set<Location> locations = Tools.getDirectDependenciesLocations( session, log, change.getLocation().getGav() );

		for( Location location : locations )
			changeSet.addChange( Change.create( location, change.getNewGav() ) );
	}
}
