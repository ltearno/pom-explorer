package fr.lteconsulting.pomexplorer.commands;

import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.model.Gav;

public class StatsCommand
{
	@Help( "general statistics on the session" )
	public void main( Session session, Log log )
	{
		PomGraphReadTransaction tx = session.graph().read();

		log.html( "There are " + tx.gavs().size() + " gavs<br/>" );

		StrongConnectivityInspector<Gav, Relation> conn = new StrongConnectivityInspector<>( tx.internalGraph() );
		log.html( "There are " + conn.stronglyConnectedSets().size() + " strongly connected components<br/>" );

		ConnectivityInspector<Gav, Relation> ccon = new ConnectivityInspector<>( tx.internalGraph() );
		log.html( "There are " + ccon.connectedSets().size() + " weakly connected components<br/>" );

		CycleDetector<Gav, Relation> cycles = new CycleDetector<Gav, Relation>( tx.internalGraph() );
		log.html( "Is there cycles ? " + cycles.detectCycles() + "<br/>" );
	}

	@Help( "gives the details of the connected components of the pom graph" )
	public void components( Session session, Log log )
	{
		PomGraphReadTransaction tx = session.graph().read();

		ConnectivityInspector<Gav, Relation> ccon = new ConnectivityInspector<>( tx.internalGraph() );
		log.html( "There are " + ccon.connectedSets().size() + " weakly connected components<br/>" );

		for( Set<Gav> gavs : ccon.connectedSets() )
		{
			log.html( "<br/>Set of connected GAVs :<br/>" );
			for( Gav gav : gavs )
			{
				log.html( "- " + gav + "<br/>" );
			}
		}
	}
}
