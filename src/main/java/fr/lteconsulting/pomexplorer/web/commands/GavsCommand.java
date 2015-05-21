package fr.lteconsulting.pomexplorer.web.commands;

import org.jgrapht.traverse.TopologicalOrderIterator;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Dep;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class GavsCommand extends BaseCommand
{
	public GavsCommand()
	{
		super( "gavs" );
	}
	
	@Override
	public String execute( Client client, String[] params )
	{
		WorkingSession session = client.getCurrentSession();
		if( session == null )
			return "No working session associated, please create one.";

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
