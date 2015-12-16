package fr.lteconsulting.pomexplorer.change.graph.processor;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.change.ChangeProcessor;
import fr.lteconsulting.pomexplorer.change.ChangeSet;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange.GavChange;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;

/**
 * If a project is released (ie its version changes from not released to released),
 * takes care that the dependencies are also released
 * 
 * @author Arnaud Tournier
 */
public class ReleaseProcessor implements ChangeProcessor<GraphChange>
{
	@Override
	public void processChange( Session session, Log log, GraphChange change, ChangeSet<GraphChange> changeSet )
	{
		if( !(change instanceof GavChange) )
			return;

		if( Tools.isReleased( change.getSource() ) || !Tools.isReleased( change.getNewValue() ) )
			return;

		PomGraphReadTransaction tx = session.graph().read();
		tx.dependenciesRec( change.getSource() ).stream().filter( r -> !Tools.isReleased( r.getTarget() ) ).forEach( r -> {
			GavChange c = new GavChange( r.getTarget(), Tools.releasedGav( r.getTarget() ) );
			changeSet.addChange( c ).addCause( this, change );
		} );
	}

	@Override
	public String toString()
	{
		return "release";
	}
}
