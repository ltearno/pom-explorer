package fr.lteconsulting.pomexplorer.web.commands;

import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

public class StatsCommand
{
	@Help("general statistics on the session")
	public String main( WorkingSession session )
	{
		StringBuilder res = new StringBuilder();

		res.append( "There are " + session.graph().getGavs().size() + " gavs<br/>" );

		StrongConnectivityInspector<GAV, Relation> conn = new StrongConnectivityInspector<>( session.graph().getGraphInternal() );
		res.append( "There are " + conn.stronglyConnectedSets().size() + " strongly connected components<br/>" );

		ConnectivityInspector<GAV, Relation> ccon = new ConnectivityInspector<>( session.graph().getGraphInternal() );
		res.append( "There are " + ccon.connectedSets().size() + " weakly connected components<br/>" );

		CycleDetector<GAV, Relation> cycles = new CycleDetector<GAV, Relation>( session.graph().getGraphInternal() );
		res.append( "Is there cycles ? " + cycles.detectCycles() + "<br/>" );

		return res.toString();
	}

	@Help("gives the details of the connected components of the pom graph")
	public String components( WorkingSession session )
	{
		StringBuilder res = new StringBuilder();

		ConnectivityInspector<GAV, Relation> ccon = new ConnectivityInspector<>( session.graph().getGraphInternal() );
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
