package fr.lteconsulting.pomexplorer.web.commands;

import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;

import fr.lteconsulting.pomexplorer.Dep;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class StatsCommand
{
	@Help("general statistics on the session")
	public String main( WorkingSession session )
	{
		StringBuilder res = new StringBuilder();

		res.append( "There are " + session.getGavs().size() + " gavs<br/>" );

		StrongConnectivityInspector<GAV, Dep> conn = new StrongConnectivityInspector<>( session.getGraph() );
		res.append( "There are " + conn.stronglyConnectedSets().size() + " strongly connected components<br/>" );

		ConnectivityInspector<GAV, Dep> ccon = new ConnectivityInspector<>( session.getGraph() );
		res.append( "There are " + ccon.connectedSets().size() + " weakly connected components<br/>" );

		CycleDetector<GAV, Dep> cycles = new CycleDetector<GAV, Dep>( session.getGraph() );
		res.append( "Is there cycles ? " + cycles.detectCycles() + "<br/>" );

		return res.toString();
	}

	@Help("gives the details of the connected components of the pom graph")
	public String components( WorkingSession session )
	{
		StringBuilder res = new StringBuilder();

		ConnectivityInspector<GAV, Dep> ccon = new ConnectivityInspector<>( session.getGraph() );
		res.append( "There are " + ccon.connectedSets().size() + " weakly connected components<br/>" );

		for( Set<GAV> gavs : ccon.connectedSets() )
		{
			res.append( "<br/>Set of connected GAVs :<br/>" );
			for( GAV gav : gavs )
			{
				res.append( "- " + gav + "<br/>" );
			}
		}

		return res.toString();
	}
}
