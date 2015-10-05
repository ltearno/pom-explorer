package fr.lteconsulting.pomexplorer.commands;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.PomAnalyzer;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class GavCommand
{
	@Help( "list the session's GAVs" )
	public void main( WorkingSession session, ILogger log )
	{
		list( session, log );
	}

	@Help( "list the session's GAVs" )
	public void list( WorkingSession session, ILogger log )
	{
		list( session, null, log );
	}

	@Help( "list the session's GAVs, with filtering" )
	public void list( WorkingSession session, FilteredGAVs gavFilter, ILogger log )
	{
		log.html( "<br/>GAV list filtered with '" + (gavFilter != null ? gavFilter.getFilter() : "no filter") + "' :<br/>" );
		if( gavFilter != null )
			gavFilter.getGavs( session ).stream().sorted( (g1, g2)->g1.toString().compareTo( g2.toString() ) ).forEach( gav -> log.html( gav + "<br/>" ) );
		else
			session.graph().gavs().stream().sorted( (g1, g2)->g1.toString().compareTo( g2.toString() ) ).forEach( gav -> log.html( gav + "<br/>" ) );
	}

	@Help( "analyze all the gav's dependencies and add them in the pom graph." )
	public void add( WorkingSession session, ILogger log, Client client, GAV gav )
	{
		PomAnalyzer analyzer = new PomAnalyzer();

		analyzer.registerExternalDependency( session, client, log, gav );

		log.html( "finished !<br/>" );
	}

	@Help( "analyze gavs which have no associated project" )
	public void resolve( WorkingSession session, ILogger log, Client client )
	{
		PomAnalyzer analyzer = new PomAnalyzer();

		session.graph().gavs().stream().filter( gav -> session.projects().forGav( gav ) == null ).parallel().forEach( gav -> {
			log.html( "analyzing " + gav + "...<br/>" );
			analyzer.registerExternalDependency( session, client, log, gav );
		} );

		log.html( "finished !<br/>" );
	}
}
