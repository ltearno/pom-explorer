package fr.lteconsulting.pomexplorer.web.commands;

import fr.lteconsulting.pomexplorer.AppFactory;
import fr.lteconsulting.pomexplorer.Client;

public class HelpCommand
{
	@Help("gives this message")
	public String main( Client client )
	{
		return AppFactory.get().commands().help();
	}
}
