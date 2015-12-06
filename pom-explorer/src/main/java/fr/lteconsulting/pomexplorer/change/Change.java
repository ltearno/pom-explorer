package fr.lteconsulting.pomexplorer.change;

import java.util.HashSet;
import java.util.Set;

public class Change
{
	private Set<ChangeCause> causes;

	public static class ChangeCause
	{
		private final Object processor;
		private final Change change;

		public ChangeCause( Object processor, Change change )
		{
			this.processor = processor;
			this.change = change;
		}

		public Object getProcessor()
		{
			return processor;
		}

		public Change getChange()
		{
			return change;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((change == null) ? 0 : change.hashCode());
			result = prime * result + ((processor == null) ? 0 : processor.hashCode());
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
			ChangeCause other = (ChangeCause) obj;
			if( change == null )
			{
				if( other.change != null )
					return false;
			}
			else if( !change.equals( other.change ) )
				return false;
			if( processor == null )
			{
				if( other.processor != null )
					return false;
			}
			else if( !processor.equals( other.processor ) )
				return false;
			return true;
		}
	}

	public void addCause( Object processor, Change change )
	{
		if( change == this )
			return;

		if( causes == null )
			causes = new HashSet<>();

		causes.add( new ChangeCause( processor, change ) );
	}

	public Set<ChangeCause> getCauses()
	{
		return causes;
	}
}
