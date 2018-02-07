package fr.lteconsulting.pomexplorer.commands;

import fr.lteconsulting.pomexplorer.ApplicationSession;
import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.DefaultPomFileLoader;
import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.PomAnalysis;
import fr.lteconsulting.pomexplorer.PomFileLoader;
import fr.lteconsulting.pomexplorer.Tools;

public class AnalyzeCommand
{
	@Help( "analyse all the pom files in a directory, recursively" )
	public void directory( CommandOptions options, Client client, ApplicationSession session, Log log, String directory )
	{
		log.html( "Analyzing directory '" + directory + "'...<br/>" );
		log.html( "<i>possible options: verbose, nofetch, offline, profiles</i>" );

		String[] profiles = null;
		if( options.getOption( "profiles" ) != null )
			profiles = ((String) options.getOption( "profiles" )).trim().split( "," );

		PomFileLoader pomFileLoader = null;
		if( !options.hasFlag( "nofetch" ) )
			pomFileLoader = new DefaultPomFileLoader( session.session(), !options.hasFlag( "offline" ) );
		else
			log.html( Tools.logMessage( "<b>nofetch</b> options set, no pom resolution will be attempted" ) );

		PomAnalysis.runFullRecursiveAnalysis( directory, session.session(), pomFileLoader, profiles, options.hasFlag( "verbose" ), log );

		log.html( "Analysis completed.<br/>" );
	}
}
