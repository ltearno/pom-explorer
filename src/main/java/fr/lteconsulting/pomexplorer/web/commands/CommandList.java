package fr.lteconsulting.pomexplorer.web.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandList
{
	private final static List<Command> commands = new ArrayList<>();

	static
	{
		commands.add(new HelpCommand());
		commands.add(new SessionCommand());
		commands.add(new AnalyseCommand());
		commands.add(new StatsCommand());
		commands.add(new GavsCommand());
		commands.add(new ProjectsCommand());
		commands.add(new DependsOnCommand());
	}

	public static List<Command> getCommands()
	{
		return commands;
	}

	public static Command getCommand(String command)
	{
		for (Command c : commands)
		{
			String[] shortcuts = c.getShortcuts();
			for (String shortcut : shortcuts)
			{
				if (shortcut.equalsIgnoreCase(command))
					return c;
			}
		}

		return null;
	}
}
