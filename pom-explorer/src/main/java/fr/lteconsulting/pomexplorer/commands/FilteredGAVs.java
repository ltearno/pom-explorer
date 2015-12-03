package fr.lteconsulting.pomexplorer.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;

/**
 * Commodity class to handle GAV filtering by name in commands
 */
public class FilteredGAVs
{
	private final String filter;

	public FilteredGAVs( String filter )
	{
		if( filter != null )
			filter = filter.toLowerCase();
		this.filter = filter;
	}

	public String getFilter()
	{
		return filter;
	}

	public boolean accept( GAV gav )
	{
		return gav != null && (filter == null || gav.toString().toLowerCase().contains( filter ));
	}

	List<GAV> getGavs( WorkingSession session )
	{
		PomGraphReadTransaction tx = session.graph().read();
		Stream<GAV> stream;

		if( filter != null )
			stream = tx.gavs().stream().filter( gav -> gav.toString().toLowerCase().contains( filter ) );
		else
			stream = tx.gavs().stream();

		List<GAV> res = new ArrayList<>();

		stream.sorted( Tools.gavAlphabeticalComparator ).forEachOrdered( gav -> res.add( gav ) );

		return res;
	}
}
