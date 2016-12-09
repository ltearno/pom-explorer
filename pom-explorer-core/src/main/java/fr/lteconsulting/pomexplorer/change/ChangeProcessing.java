package fr.lteconsulting.pomexplorer.change;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Session;

public class ChangeProcessing<T>
{
	protected final List<ChangeProcessor<T>> processors = new ArrayList<>();

	public Set<T> process( Session session, Log log, Set<T> changes )
	{
		Map<Item, Item> items = new HashMap<>();
		changes.stream().map( c -> new Item( c ) ).forEach( item -> items.put( item, item ) );

		ChangeSet<T> changeSet = new ChangeSet<T>()
		{
			@Override
			public void removeChange( T change )
			{
				items.remove( new Item( change ) );
			}

			@Override
			public T addChange( T change )
			{
				if( change == null )
					return (T) null;

				// be careful not adding useless changes !!
				Item key = new Item( change );
				Item res = items.get( key );
				if( res != null )
					return res.value;
				items.put( key, key );
				return key.value;
			}
		};

		List<Item> notProcessed = new ArrayList<>( items.values() );

		while( !notProcessed.isEmpty() )
		{
			for( Item change : notProcessed )
			{
				for( ChangeProcessor<T> processor : processors )
					processor.processChange( session, log, change.value, changeSet );

				change.processed = true;
			}

			notProcessed = items.values().stream().filter( i -> !i.processed ).collect( toList() );
		}

		return items.values().stream().map( i -> i.value ).collect( toSet() );
	}

	private class Item
	{
		T value;

		boolean processed;

		public Item( T value )
		{
			this.value = value;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
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
			@SuppressWarnings( "unchecked" )
			Item other = (Item) obj;
			if( value == null )
			{
				if( other.value != null )
					return false;
			}
			else if( !value.equals( other.value ) )
				return false;
			return true;
		}
	}
}
