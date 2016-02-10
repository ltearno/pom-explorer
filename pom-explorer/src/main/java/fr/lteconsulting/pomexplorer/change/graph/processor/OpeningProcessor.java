package fr.lteconsulting.pomexplorer.change.graph.processor;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.change.ChangeProcessor;
import fr.lteconsulting.pomexplorer.change.ChangeSet;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange.GavChange;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange.RelationChange;
import fr.lteconsulting.pomexplorer.model.Gav;

/**
 * When a gav dependency if changed, care is taken to reopen the relation source in snapshot if needed
 * 
 * @author Arnaud
 */
public class OpeningProcessor implements ChangeProcessor<GraphChange>
{
	@Override
	public void processChange( Session session, Log log, GraphChange change, ChangeSet<GraphChange> changeSet )
	{
		if( !(change instanceof RelationChange) )
			return;

		Gav sourceGav = change.getSource();
		if( Tools.isReleased( sourceGav ) )
		{
			GavChange openingChange = new GavChange( sourceGav, Tools.openGavVersion( sourceGav ) );
			openingChange.addCause( this, change );
			changeSet.addChange( openingChange );
		}
	}
}
