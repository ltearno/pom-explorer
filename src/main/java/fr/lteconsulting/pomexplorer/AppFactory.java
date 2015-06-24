package fr.lteconsulting.pomexplorer;

import java.util.ArrayList;
import java.util.List;

import fr.lteconsulting.pomexplorer.commands.AnalyzeCommand;
import fr.lteconsulting.pomexplorer.commands.BuildCommand;
import fr.lteconsulting.pomexplorer.commands.ChangeCommand;
import fr.lteconsulting.pomexplorer.commands.CheckCommand;
import fr.lteconsulting.pomexplorer.commands.ClassesCommand;
import fr.lteconsulting.pomexplorer.commands.Commands;
import fr.lteconsulting.pomexplorer.commands.DependsCommand;
import fr.lteconsulting.pomexplorer.commands.GavsCommand;
import fr.lteconsulting.pomexplorer.commands.GraphCommand;
import fr.lteconsulting.pomexplorer.commands.HelpCommand;
import fr.lteconsulting.pomexplorer.commands.ProjectsCommand;
import fr.lteconsulting.pomexplorer.commands.ReleaseCommand;
import fr.lteconsulting.pomexplorer.commands.SessionCommand;
import fr.lteconsulting.pomexplorer.commands.StatsCommand;

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
			commands.addCommand( new BuildCommand() );
			commands.addCommand( new GraphCommand() );
			commands.addCommand( new CheckCommand() );
			commands.addCommand( new ClassesCommand() );
		}

		return commands;
	}

	private ApplicationSettings settings;

	public ApplicationSettings getSettings()
	{
		if( settings == null )
		{
			settings = new ApplicationSettings();
			settings.load();
		}

		return settings;
	}
}
