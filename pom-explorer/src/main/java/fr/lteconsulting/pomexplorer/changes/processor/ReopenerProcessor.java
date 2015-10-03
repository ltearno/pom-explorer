package fr.lteconsulting.pomexplorer.changes.processor;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.PomSection;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.changes.GavChange;
import fr.lteconsulting.pomexplorer.changes.IChangeSet;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;

/**
 * When a change is made to a pom file, it should be ensured that this pom is in
 * a snapshot version. If not, the pom version should be incremented and
 * suffixed with -SNAPSHOT
 *
 */
public class ReopenerProcessor extends AbstractGavChangeProcessor
{
	@Override
	protected void processChange( WorkingSession session, ILogger log, GavChange change, IChangeSet changeSet )
	{
		if( change.getLocation().getProject() == null )
			return;

		GAV gav = change.getLocation().getProject().getGav();

		// maybe the change is itself opening a pom
		if( change.getLocation().getSection() == PomSection.PROJECT && !Tools.isReleased( change.getNewGav() ) )
			return;

		if( !Tools.isReleased( gav ) )
			return;

		log.html( Tools.warningMessage( "modifying a released gav (" + gav + "), reopening it" ) );

		// find the opened version
		GAV newGav = Tools.openGavVersion( gav );

		// add the change to the changeset
		// TODO add the comment : "reopening"
		changeSet.addChange( new GavChange( new GavLocation( change.getLocation().getProject(), PomSection.PROJECT, gav ), newGav ), change );
	}
}
