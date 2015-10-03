package fr.lteconsulting.pomexplorer.changes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.changes.processor.FollowGavProcessor;
import fr.lteconsulting.pomexplorer.changes.processor.GavToPropertyProcessor;
import fr.lteconsulting.pomexplorer.changes.processor.NoVersionProjectProcessor;
import fr.lteconsulting.pomexplorer.changes.processor.ReopenerProcessor;
import fr.lteconsulting.pomexplorer.depanalyze.Location;

/**
 * Manages a set of changes.<br/>
 * 
 * As changes are added to this ChangeSet, they are processed to check whether
 * other changes should be generated, or if some should be removed
 * 
 * - allows to transform changes according to the situation
 * 
 * - allows to trace back generated changes to their cause
 * 
 * - allows to check consistency between changes
 */
public class ChangeSetManager implements IChangeSet, Iterable<Change<? extends Location>>
{
	private final List<IChangeProcessor> processors = new ArrayList<>();

	private final Map<ChangeInfo, ChangeInfo> changes = new HashMap<>();

	public ChangeSetManager()
	{
		addProcessor( new NoVersionProjectProcessor() );
		addProcessor( new FollowGavProcessor() );
		addProcessor( new GavToPropertyProcessor() );
		addProcessor( new ReopenerProcessor() );
	}

	public void addProcessor( IChangeProcessor processor )
	{
		processors.add( processor );
	}

	@Override
	public void addChange( Change<? extends Location> change, String causingMessage )
	{
		ChangeInfo existingChange = addChange( change );

		if( causingMessage != null )
			existingChange.getChange().setCausingMessage( causingMessage );
	}

	@Override
	public void addChange( Change<? extends Location> change, Change<? extends Location> causingChange )
	{
		ChangeInfo existingChange = addChange( change );

		if( causingChange != null )
			existingChange.getChange().addCause( causingChange );
	}

	private ChangeInfo addChange( Change<? extends Location> change )
	{
		ChangeInfo info = new ChangeInfo( change );

		ChangeInfo existingChange = changes.get( info );
		if( existingChange == null )
		{
			changes.put( info, info );
			existingChange = info;
		}

		return existingChange;
	}

	@Override
	public void invalidateChange( Change<? extends Location> change )
	{
		ChangeInfo info = new ChangeInfo( change );

		changes.remove( info );
	}

	/**
	 * Process change resolution
	 */
	public void resolveChanges( WorkingSession session, ILogger log )
	{
		log.html( "<br/>Resolving changes...<br/><br/>" );

		int round = 0;

		while( true )
		{
			// while there are not yet processed changes
			List<ChangeInfo> notProcessed = new ArrayList<>();
			for( ChangeInfo info : changes.values() )
				if( !info.isProcessed() )
					notProcessed.add( info );

			if( notProcessed.isEmpty() )
				break;

			// process them : run each processor on it
			for( ChangeInfo info : notProcessed )
			{
				log.html( "[" + round + "] processing change " + info.getChange().getLocation() + "<br/>" );

				processChange( session, log, info );

				// mark them as processed
				info.setProcessed( true );
			}

			round++;
		}
	}

	private void processChange( WorkingSession session, ILogger log, ChangeInfo info )
	{

		for( IChangeProcessor processor : processors )
			processor.processChange( session, log, info.getChange(), this );
	}

	private static class ChangeInfo
	{
		private final Change<? extends Location> change;

		private boolean isProcessed;

		public ChangeInfo( Change<? extends Location> change )
		{
			this.change = change;
		}

		public Change<? extends Location> getChange()
		{
			return change;
		}

		public void setProcessed( boolean isProcessed )
		{
			this.isProcessed = isProcessed;
		}

		public boolean isProcessed()
		{
			return isProcessed;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((change == null) ? 0 : change.hashCode());
			return result;
		}

		@Override
		public boolean equals( Object obj )
		{
			if( this == obj )
				return true;
			if( obj == null )
				return false;
			if( getClass() != obj.getClass() )
				return false;
			ChangeInfo other = (ChangeInfo) obj;
			if( change == null )
			{
				if( other.change != null )
					return false;
			}
			else if( !change.equals( other.change ) )
				return false;
			return true;
		}
	}

	@Override
	public Iterator<Change<? extends Location>> iterator()
	{
		Set<Change<? extends Location>> res = new HashSet<>();

		for( ChangeInfo info : changes.values() )
			res.add( info.getChange() );

		return res.iterator();
	}
}
