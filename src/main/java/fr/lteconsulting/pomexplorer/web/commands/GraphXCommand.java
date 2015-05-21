package fr.lteconsulting.pomexplorer.web.commands;

import org.jgrapht.ext.JGraphXAdapter;

import com.mxgraph.layout.mxFastOrganicLayout;

import fr.lteconsulting.pomexplorer.Dep;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.GraphFrame;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class GraphXCommand
{
	@Help( "displays a graph on the server machine" )
	public String main( WorkingSession session )
	{
		JGraphXAdapter<GAV, Dep> ga = new JGraphXAdapter<>( session.getGraph() );

		new GraphFrame( ga );

		mxFastOrganicLayout layout = new mxFastOrganicLayout( ga );
		layout.setUseBoundingBox( true );
		layout.setForceConstant( 200 );
		layout.execute( ga.getDefaultParent() );

		return "ok, graph displayed on the server.";
	}
}
