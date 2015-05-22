package fr.lteconsulting.pomexplorer.web.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.jgrapht.traverse.TopologicalOrderIterator;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

public class GavsCommand
{
	@Help( "list the session's GAVs" )
	public String list( WorkingSession session )
	{
		return list( session, null );
	}

	@Help( "list the session's GAVs, with filtering" )
	public String list( WorkingSession session, String gavFilter )
	{
		if( gavFilter != null )
			gavFilter = gavFilter.toLowerCase();

		ArrayList<GAV> gavs = new ArrayList<>( session.graph().getGavs() );
		Collections.sort( gavs, new Comparator<GAV>()
		{
			@Override
			public int compare( GAV o1, GAV o2 )
			{
				int r = o1.getGroupId().compareTo( o2.getGroupId() );
				if( r != 0 )
					return r;

				r = o1.getArtifactId().compareTo( o2.getArtifactId() );
				if( r != 0 )
					return r;

				r = o1.getVersion().compareTo( o2.getVersion() );

				return 0;
			}
		} );

		StringBuilder res = new StringBuilder();

		res.append( "<br/>GAV list "+(gavFilter!=null?("filtering with '"+gavFilter+"'"):"")+":<br/>" );
		for( GAV gav : gavs )
		{
			if( gavFilter != null && !gav.toString().toLowerCase().contains( gavFilter ) )
				continue;
			res.append( gav + "<br/>" );
		}

		return res.toString();
	}
}
