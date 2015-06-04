package fr.lteconsulting.pomexplorer;

import javax.swing.JFrame;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class GraphFrame extends JFrame
{
	private static final long serialVersionUID = -3492357340545532640L;

	public GraphFrame( mxGraph graph )
	{
		mxGraphComponent graphComponent = new mxGraphComponent( graph );
		getContentPane().add( graphComponent );

		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setSize( 800, 620 );
		setVisible( true );
	}
}
