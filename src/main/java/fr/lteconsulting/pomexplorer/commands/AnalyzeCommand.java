package fr.lteconsulting.pomexplorer.commands;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.PomAnalyzer;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class AnalyzeCommand
{
	@Help( "analyse all the pom files in a directory, recursively" )
	public String directory( Client client, WorkingSession session, String directory )
	{
		StringBuilder log = new StringBuilder();

		client.send( "Analyzing directoy '" + directory + "'...<br/>" );

		PomAnalyzer analyzer = new PomAnalyzer();
		analyzer.analyze( directory, session, client );

		log.append( "Analyzis completed for '" + directory + "'." );

		return log.toString();
	}
}
