package fr.lteconsulting.pomexplorer.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.model.Gav;

/**
 * Commodity class to handle GAV filtering by name in commands
 */
public class FilteredGAVs
{
	private final String[] filters;

	public FilteredGAVs( String filter )
	{
		if( filter != null )
		{
			filter = filter.toLowerCase();
			filters = filter.split( "," );
		}
		else
		{
			filters = null;
		}
	}

	public String getFilterDescription()
	{
		return Arrays.stream( filters ).collect( Collectors.joining( ", " ) );
	}

	public List<Gav> getGavs( Session session )
	{
		PomGraphReadTransaction tx = session.graph().read();

		Stream<Gav> stream;

		if( filters != null )
			stream = tx.gavs()
					.stream()
					.filter( gav ->
					{
						String toSearch = gav.toString().toLowerCase();
						return Arrays.stream( filters ).anyMatch( filter -> toSearch.contains( filter ) );
					} );
		else
			stream = tx.gavs().stream();

		List<Gav> res = new ArrayList<>();

		stream.sorted( Gav.alphabeticalComparator ).forEachOrdered( gav -> res.add( gav ) );

		return res;
	}
}
