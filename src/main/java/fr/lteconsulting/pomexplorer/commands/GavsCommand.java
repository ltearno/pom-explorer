package fr.lteconsulting.pomexplorer.commands;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.PomAnalyzer;
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
	public String list(WorkingSession session, FilteredGAVs gavFilter)
	{
		if (gavFilter == null)
			return Tools.warningMessage("You should specify a GAV filter !");

		StringBuilder log = new StringBuilder();

		log.append("<br/>GAV list filtered with '" + gavFilter.getFilter() + "' :<br/>");
		gavFilter.getGavs(session).forEach(gav ->
		{
			log.append( gav + "<br/>" );
		});

		return log.toString();
	}

	@Help( "analyze all the gav's dependencies and add them in the pom graph." )
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

		session.graph().gavs().stream().filter( gav -> session.projects().forGav( gav ) == null ).parallel().forEach( gav -> {
			log.append( "analyzing " + gav + "...<br/>" );
			client.send( "analyzing " + gav + "...<br/>" );
			analyzer.registerExternalDependency( session, client, log, gav );
		} );

		log.append( "finished !<br/>" );

		return log.toString();
	}
}
