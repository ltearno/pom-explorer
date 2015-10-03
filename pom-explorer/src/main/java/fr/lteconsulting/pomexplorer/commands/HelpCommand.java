package fr.lteconsulting.pomexplorer.commands;

import fr.lteconsulting.pomexplorer.AppFactory;
import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.ILogger;

public class HelpCommand
{
	@Help( "gives this message" )
	public void main( Client client, ILogger log )
	{
		log.html( AppFactory.get().commands().help() );
	}
}
