package fr.lteconsulting.pomexplorer.changes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.changes.processor.FollowGavProcessor;
import fr.lteconsulting.pomexplorer.changes.processor.ProjectVersionProcessor;
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
	private final Set<ChangeInfo> changes = new HashSet<>();

	public ChangeSetManager()
	{
		addProcessor( new ProjectVersionProcessor() );
		addProcessor( new FollowGavProcessor() );
	}

	public void addProcessor( IChangeProcessor processor )
	{
		processors.add( processor );
	}

	@Override
	public void addChange( Change<? extends Location> change )
	{
		ChangeInfo info = new ChangeInfo( change );

		changes.add( info );
	}

	@Override
	public void removeChange( Change<? extends Location> change )
	{
		ChangeInfo info = new ChangeInfo( change );

		changes.remove( info );
	}

	/**
	 * Process change resolution
	 */
	public void resolveChanges( WorkingSession session, StringBuilder log )
	{
		while( true )
		{
			// while there are not yet processed changes
			List<ChangeInfo> notProcessed = new ArrayList<>();
			for( ChangeInfo info : changes )
				if( !info.isProcessed() )
					notProcessed.add( info );

			if( notProcessed.isEmpty() )
				break;

			// process them : run each processor on it
			for( ChangeInfo info : notProcessed )
			{
				processChange( session, log, info );

				// mark them as processed
				info.setProcessed( true );
			}
		}
	}

	private void processChange( WorkingSession session, StringBuilder log, ChangeInfo info )
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

		for( ChangeInfo info : changes )
			res.add( info.getChange() );

		return res.iterator();
	}
}
