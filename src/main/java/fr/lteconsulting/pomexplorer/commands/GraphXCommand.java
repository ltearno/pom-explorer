package fr.lteconsulting.pomexplorer.commands;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DirectedSubgraph;

import com.mxgraph.layout.mxFastOrganicLayout;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.GraphFrame;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

public class GraphXCommand
{
	@Help( "displays a graph on the server machine" )
	public String main(WorkingSession session)
	{
		return display(session, null);
	}

	@Help("displays a graph on the server machine. Parameter is the filter for GAVs")
	public String display(WorkingSession session, String filter)
	{
		if (filter != null)
			filter = filter.toLowerCase();

		DirectedGraph<GAV, Relation> fullGraph = session.graph().internalGraph();
		
		Set<GAV> vertexSubset = new HashSet<>();
		for (GAV gav : fullGraph.vertexSet())
		{
			if (filter == null || gav.toString().toLowerCase().contains(filter))
			{
				vertexSubset.add(gav);
			}
		}

		Set<Relation> edgeSubset = new HashSet<>();
		for (Relation r : fullGraph.edgeSet())
		{
			if (vertexSubset.contains(fullGraph.getEdgeSource(r)) && vertexSubset.contains(fullGraph.getEdgeTarget(r)))
				edgeSubset.add(r);
		}

		DirectedSubgraph<GAV, Relation> subGraph = new DirectedSubgraph<>(fullGraph, vertexSubset, edgeSubset);

		JGraphXAdapter<GAV, Relation> ga = new JGraphXAdapter<>(subGraph);

		new GraphFrame( ga );

		mxFastOrganicLayout layout = new mxFastOrganicLayout( ga );
		layout.setUseBoundingBox( true );
		layout.setForceConstant( 200 );
		layout.execute( ga.getDefaultParent() );

		return "ok, graph displayed on the server.";
	}
}
