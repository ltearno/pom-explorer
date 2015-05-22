package fr.lteconsulting.pomexplorer;

import java.util.ArrayList;
import java.util.List;

import fr.lteconsulting.pomexplorer.web.commands.AnalyzeCommand;
import fr.lteconsulting.pomexplorer.web.commands.ChangeCommand;
import fr.lteconsulting.pomexplorer.web.commands.Commands;
import fr.lteconsulting.pomexplorer.web.commands.DependsCommand;
import fr.lteconsulting.pomexplorer.web.commands.GavsCommand;
import fr.lteconsulting.pomexplorer.web.commands.GraphXCommand;
import fr.lteconsulting.pomexplorer.web.commands.HelpCommand;
import fr.lteconsulting.pomexplorer.web.commands.ProjectsCommand;
import fr.lteconsulting.pomexplorer.web.commands.ReleaseCommand;
import fr.lteconsulting.pomexplorer.web.commands.SessionCommand;
import fr.lteconsulting.pomexplorer.web.commands.StatsCommand;

public class AppFactory
{
	private static final AppFactory INSTANCE = new AppFactory();

	private AppFactory()
	{
	}

	public static AppFactory get()
	{
		return INSTANCE;
	}

	private final List<WorkingSession> sessions = new ArrayList<>();

	public List<WorkingSession> sessions()
	{
		return sessions;
	}

	private Commands commands;

	public Commands commands()
	{
		if( commands == null )
		{
			commands = new Commands();

			// shell commands available
			commands.addCommand( new HelpCommand() );
			commands.addCommand( new SessionCommand() );
			commands.addCommand( new AnalyzeCommand() );
			commands.addCommand( new StatsCommand() );
			commands.addCommand( new GavsCommand() );
			commands.addCommand( new ProjectsCommand() );
			commands.addCommand( new DependsCommand() );
			commands.addCommand( new ReleaseCommand() );
			commands.addCommand( new ChangeCommand() );
			commands.addCommand( new GraphXCommand() );
		}
		
		return commands;
	}
}
