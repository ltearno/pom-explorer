package fr.lteconsulting.pomexplorer.change.graph.processor;

import java.util.Set;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange.GavChange;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange.RelationChange;
import fr.lteconsulting.pomexplorer.change.graph.ChangeProcessor;
import fr.lteconsulting.pomexplorer.change.graph.ChangeSet;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

/**
 * If a gav is changed, all dependencies to it are also changed
 * 
 * @author Arnaud Tournier
 */
public class FollowProcessor implements ChangeProcessor<GraphChange>
{
	@Override
	public void processChange( Session session, Log log, GraphChange change, ChangeSet<GraphChange> changeSet )
	{
		if( !(change instanceof GavChange) )
			return;

		PomGraphReadTransaction tx = session.graph().read();
		Set<Relation> relations = tx.relationsReverse( change.getSource() );
		for( Relation r : relations )
		{
			RelationChange c = RelationChange.create( r, change.getNewValue() );
			if( c != null )
				changeSet.addChange( c ).addCause( this, change );
		}
	}

	@Override
	public String toString()
	{
		return "follow";
	}
}
