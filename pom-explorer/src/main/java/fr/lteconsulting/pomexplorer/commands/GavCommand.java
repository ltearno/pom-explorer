package fr.lteconsulting.pomexplorer.commands;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Gav;
import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.PomAnalyzer;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;

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
		StringBuilder sb = new StringBuilder();

		sb.append( "<br/>GAV list filtered with '" + (gavFilter != null ? gavFilter.getFilter() : "no filter") + "' :<br/>" );
		if( gavFilter != null )
		{
			gavFilter.getGavs( session ).stream().sorted( ( g1, g2 ) -> g1.toString().compareTo( g2.toString() ) )
					.forEach( gav -> sb.append( gav + "<br/>" ) );
		}
		else
		{
			PomGraphReadTransaction tx = session.graph().read();
			
			tx.gavs().stream().sorted( ( g1, g2 ) -> g1.toString().compareTo( g2.toString() ) )
					.forEach( gav -> sb.append( gav + "<br/>" ) );
		}

		log.html( sb.toString() );
	}

	@Help( "analyze all the gav's dependencies and add them in the pom graph." )
	public void add( WorkingSession session, ILogger log, Client client, Gav gav )
	{
		PomAnalyzer analyzer = new PomAnalyzer();

		analyzer.fetchGavWithMaven( session, log, gav );

		log.html( "finished !<br/>" );
	}

	@Help( "analyze gavs which have no associated project" )
	public void resolve( WorkingSession session, ILogger log, Client client )
	{
		PomGraphReadTransaction tx = session.graph().read();
		PomAnalyzer analyzer = new PomAnalyzer();

		tx.gavs().stream().filter( gav -> session.projects().forGav( gav ) == null )
				.parallel().forEach( gav -> {
					log.html( "analyzing " + gav + "...<br/>" );
					analyzer.fetchGavWithMaven( session, log, gav );
				} );

		log.html( "finished !<br/>" );
	}

	@Help( "load a gav from repository and analyze it" )
	public void load( WorkingSession session, ILogger log, Client client, String gavString )
	{
		PomAnalyzer analyzer = new PomAnalyzer();

		Gav gav = Tools.string2Gav( gavString );
		if( gav == null )
		{
			log.html( Tools.warningMessage( "the string '" + gavString
					+ "' is not parsable into a fully qualified GAV (groupId:artifactId:version) !" ) );
			return;
		}

		log.html( "analyzing " + gav + "...<br/>" );
		analyzer.fetchGavWithMaven( session, log, gav );

		log.html( "finished !<br/>" );
	}
}
