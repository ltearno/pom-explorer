package fr.lteconsulting.pomexplorer.web.commands;

import java.util.ArrayList;
import java.util.Collections;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class GavsCommand
{
	@Help( "list the session's GAVs" )
	public String main( WorkingSession session )
	{
		return list( session );
	}

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
		Collections.sort( gavs, Tools.gavAlphabeticalComparator );

		StringBuilder res = new StringBuilder();

		res.append( "<br/>GAV list " + (gavFilter != null ? ("filtering with '" + gavFilter + "'") : "") + ":<br/>" );
		for( GAV gav : gavs )
		{
			if( gavFilter != null && !gav.toString().toLowerCase().contains( gavFilter ) )
				continue;
			res.append( gav + "<br/>" );
		}

		return res.toString();
	}
}
