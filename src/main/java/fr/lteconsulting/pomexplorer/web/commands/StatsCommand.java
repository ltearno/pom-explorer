package fr.lteconsulting.pomexplorer.web.commands;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Dep;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class StatsCommand extends BaseCommand
{
	public StatsCommand()
	{
		super( "stats" );
	}
	
	@Override
	public String execute( Client client, String[] params )
	{
		WorkingSession session = client.getCurrentSession();
		if( session == null )
			return "No working session associated, please create one.";

		StringBuilder res = new StringBuilder();

		res.append( "There are " + session.getGavs().size() + " gavs<br/>" );

		StrongConnectivityInspector<GAV, Dep> conn = new StrongConnectivityInspector<>( session.getGraph() );
		res.append( "There are " + conn.stronglyConnectedSets().size() + " strongly connected components<br/>" );

		ConnectivityInspector<GAV, Dep> ccon = new ConnectivityInspector<>( session.getGraph() );
		res.append( "There are " + ccon.connectedSets().size() + " weakly connected components<br/>" );
//		for( Set<GAV> comp : ccon.connectedSets() )
//		{
//			res.append( "  - " + comp.toString() );
//		}

		CycleDetector<GAV, Dep> cycles = new CycleDetector<GAV, Dep>( session.getGraph() );
		res.append( "Is there cycles ? " + cycles.detectCycles() + "<br/>" );

		return res.toString();
	}
}
