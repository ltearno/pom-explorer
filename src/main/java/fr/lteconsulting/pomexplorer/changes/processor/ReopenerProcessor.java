package fr.lteconsulting.pomexplorer.changes.processor;

import fr.lteconsulting.pomexplorer.GAV;
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
	protected void processChange( WorkingSession session, StringBuilder log, GavChange change, IChangeSet changeSet )
	{
		if( change.getLocation().getProject() == null )
			return;

		GAV gav = change.getLocation().getProject().getGav();

		// maybe the change is itself opening a pom
		if( change.getLocation().getSection() == PomSection.PROJECT && !Tools.isReleased( change.getNewGav() ) )
			return;

		if( !Tools.isReleased( gav ) )
			return;

		log.append( Tools.warningMessage( "modifying a released gav (" + gav + "), reopening it" ) );

		// find the opened version
		String nextVersion = nextVersion( gav.getVersion() );
		GAV newGav = new GAV( gav.getGroupId(), gav.getArtifactId(), nextVersion + "-SNAPSHOT" );

		// add the change to the changeset
		// TODO add the comment : "reopening"
		changeSet.addChange( new GavChange( new GavLocation( change.getLocation().getProject(), PomSection.PROJECT, gav ), newGav ), change );
	}

	private String nextVersion( String version )
	{
		try
		{
			int index = version.lastIndexOf( "." );
			if( index >= 0 && index != version.length() - 1 )
			{
				Integer num = Integer.parseInt( version.substring( index + 1 ) );
				return version.substring( 0, index + 1 ) + (num + 1);
			}
		}
		catch( Exception e )
		{
		}

		return version + "-open";
	}
}
