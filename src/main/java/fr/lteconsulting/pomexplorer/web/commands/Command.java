package fr.lteconsulting.pomexplorer.web.commands;

import fr.lteconsulting.pomexplorer.Client;

public interface Command
{
	String execute(Client client, String[] params);

	String[] getShortcuts();

	String getName();
}
