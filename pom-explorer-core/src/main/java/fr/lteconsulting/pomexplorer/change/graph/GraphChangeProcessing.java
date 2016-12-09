package fr.lteconsulting.pomexplorer.change.graph;

import fr.lteconsulting.pomexplorer.change.ChangeProcessing;
import fr.lteconsulting.pomexplorer.change.graph.processor.FollowProcessor;
import fr.lteconsulting.pomexplorer.change.graph.processor.OpeningProcessor;
import fr.lteconsulting.pomexplorer.change.graph.processor.ReleaseProcessor;

public class GraphChangeProcessing extends ChangeProcessing<GraphChange>
{
	public GraphChangeProcessing()
	{
		processors.add( new FollowProcessor() );
		processors.add( new ReleaseProcessor() );
		processors.add( new OpeningProcessor() );
	}
}
