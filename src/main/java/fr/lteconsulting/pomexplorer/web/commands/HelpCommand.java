package fr.lteconsulting.pomexplorer.web.commands;

import fr.lteconsulting.pomexplorer.Client;

public class HelpCommand extends BaseCommand
{
	public HelpCommand()
	{
		super("help", "?");
	}

	@Override
	public String execute(Client client, String[] params)
	{
		StringBuilder sb = new StringBuilder();

		String sep = "";
		for (Command c : CommandList.getCommands())
		{
			sb.append(sep);
			sep = "<br/>";

			String sep2 = "";
			for (String shortcut : c.getShortcuts())
			{
				sb.append(sep2);
				sep2 = ", ";
				sb.append("'" + shortcut + "'");
			}

			sb.append(" ");
			sb.append(c.getName());
		}

		return sb.toString();
	}
}
