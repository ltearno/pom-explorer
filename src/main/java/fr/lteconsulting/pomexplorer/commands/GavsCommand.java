package fr.lteconsulting.pomexplorer.commands;

import java.util.ArrayList;
import java.util.Collections;

import fr.lteconsulting.pomexplorer.*;

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

		ArrayList<GAV> gavs = new ArrayList<>( session.graph().gavs() );
		Collections.sort( gavs, Tools.gavAlphabeticalComparator );

		StringBuilder log = new StringBuilder();

		log.append( "<br/>GAV list " + (gavFilter != null ? ("filtering with '" + gavFilter + "'") : "") + ":<br/>" );
		for( GAV gav : gavs )
		{
			if( gavFilter != null && !gav.toString().toLowerCase().contains( gavFilter ) )
				continue;
			log.append( gav + "<br/>" );
		}

		return log.toString();
	}

	@Help( "adds a gav in the pom graph." )
	public String add( WorkingSession session, Client client, String gavName )
	{
		GAV gav;
		if( gavName == null || gavName.isEmpty() || (gav = Tools.string2Gav( gavName )) == null )
			return Tools.errorMessage( "You should supply a GAV in the correct G:A:V format" );

		StringBuilder log = new StringBuilder();

		PomAnalyzer analyzer = new PomAnalyzer();

		analyzer.registerExternalDependency( session, client, log, gav );

		log.append( "finished !<br/>" );

		return log.toString();
	}

	@Help( "analyze gavs which have no associated project" )
	public String resolve( WorkingSession session, Client client )
	{
		StringBuilder log = new StringBuilder();

		PomAnalyzer analyzer = new PomAnalyzer();

		session.graph().gavs().stream().filter( gav -> session.projects().get( gav ) == null ).parallel().forEach( gav -> {
			log.append( "analyzing " + gav + "...<br/>" );
			client.send( "analyzing " + gav + "...<br/>" );
			analyzer.registerExternalDependency( session, client, log, gav );
		} );

		log.append( "finished !<br/>" );

		return log.toString();
	}
}
