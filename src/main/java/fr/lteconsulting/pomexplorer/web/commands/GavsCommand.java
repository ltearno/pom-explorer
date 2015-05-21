package fr.lteconsulting.pomexplorer.web.commands;

import org.jgrapht.traverse.TopologicalOrderIterator;

import fr.lteconsulting.pomexplorer.Dep;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class GavsCommand
{
	@Help("list the session's GAVs")
	public String main( WorkingSession session )
	{
		StringBuilder res = new StringBuilder();
	
		GAV v;
		TopologicalOrderIterator<GAV, Dep> orderIterator;
		
		orderIterator = new TopologicalOrderIterator<>( session.getGraph() );
		res.append( "<br/>GAV list:<br/>" );
		while( orderIterator.hasNext() )
		{
			v = orderIterator.next();
			res.append( v + "<br/>" );
		}

		return res.toString();
	}
}
