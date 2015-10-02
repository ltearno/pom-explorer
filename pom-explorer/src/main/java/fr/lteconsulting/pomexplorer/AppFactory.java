package fr.lteconsulting.pomexplorer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;

import com.google.gson.Gson;

import fr.lteconsulting.pomexplorer.commands.AnalyzeCommand;
import fr.lteconsulting.pomexplorer.commands.BuildCommand;
import fr.lteconsulting.pomexplorer.commands.ChangeCommand;
import fr.lteconsulting.pomexplorer.commands.CheckCommand;
import fr.lteconsulting.pomexplorer.commands.ClassesCommand;
import fr.lteconsulting.pomexplorer.commands.Commands;
import fr.lteconsulting.pomexplorer.commands.DependsCommand;
import fr.lteconsulting.pomexplorer.commands.GarbageCommand;
import fr.lteconsulting.pomexplorer.commands.GavsCommand;
import fr.lteconsulting.pomexplorer.commands.GitCommand;
import fr.lteconsulting.pomexplorer.commands.GraphCommand;
import fr.lteconsulting.pomexplorer.commands.HelpCommand;
import fr.lteconsulting.pomexplorer.commands.ProjectsCommand;
import fr.lteconsulting.pomexplorer.commands.ReleaseCommand;
import fr.lteconsulting.pomexplorer.commands.SessionCommand;
import fr.lteconsulting.pomexplorer.commands.StatsCommand;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.webserver.WebServer;
import fr.lteconsulting.pomexplorer.webserver.XWebServer;

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

	private Commands commands;

	private ApplicationSettings settings;

	private WebServer webServer;

	public List<WorkingSession> sessions()
	{
		return sessions;
	}

	public Commands commands()
	{
		if (commands == null)
		{
			commands = new Commands();

			// shell commands available
			commands.addCommand(new HelpCommand());
			commands.addCommand(new SessionCommand());
			commands.addCommand(new AnalyzeCommand());
			commands.addCommand(new StatsCommand());
			commands.addCommand(new GavsCommand());
			commands.addCommand(new ProjectsCommand());
			commands.addCommand(new DependsCommand());
			commands.addCommand(new ReleaseCommand());
			commands.addCommand(new ChangeCommand());
			commands.addCommand(new BuildCommand());
			commands.addCommand(new GraphCommand());
			commands.addCommand(new CheckCommand());
			commands.addCommand(new ClassesCommand());
			commands.addCommand(new GitCommand());
			commands.addCommand(new GarbageCommand());
		}

		return commands;
	}

	public ApplicationSettings getSettings()
	{
		if (settings == null)
		{
			settings = new ApplicationSettings();
			settings.load();
		}

		return settings;
	}

	public WebServer webServer()
	{
		if (webServer == null)
			webServer = new WebServer(xWebServer);

		return webServer;
	}

	private XWebServer xWebServer = new XWebServer()
	{
		@Override
		public void onNewClient(Client client)
		{
			System.out.println("New client " + client.getId());

			// running the default script
			List<String> commands = Tools.readFileLines("welcome.commands");
			for (String command : commands)
			{
				if (command.isEmpty() || command.startsWith("#"))
					continue;

				if (command.startsWith("="))
				{
					String message = command.substring(1);
					if (message.isEmpty())
						message = "<br/>";
					client.send(message);
				}
				else
					client.send(AppFactory.get().commands().takeCommand(client, command));
			}
		}

		@Override
		public String onWebsocketMessage(Client client, String message)
		{
			if (message == null || message.isEmpty())
				return "nop.";

			return AppFactory.get().commands().takeCommand(client, message);
		}

		@Override
		public String onGraphQuery(String sessionIdString)
		{
			List<WorkingSession> sessions = AppFactory.get().sessions();
			if (sessions == null || sessions.isEmpty())
				return "No session available. Go to main page !";

			WorkingSession session = null;

			try
			{
				Integer sessionId = Integer.parseInt(sessionIdString);
				if (sessionId != null)
				{
					for (WorkingSession s : sessions)
					{
						if (System.identityHashCode(s) == sessionId)
						{
							session = s;
							break;
						}
					}
				}
			}
			catch (Exception e)
			{
			}

			if (session == null)
				session = sessions.get(0);

			DirectedGraph<GAV, Relation> g = session.graph().internalGraph();

			GraphDto dto = new GraphDto();
			dto.gavs = new HashSet<>();
			dto.relations = new HashSet<>();
			for (GAV gav : g.vertexSet())
			{
				dto.gavs.add(gav.toString());

				for (Relation relation : g.outgoingEdgesOf(gav))
				{
					GAV target = g.getEdgeTarget(relation);
					EdgeDto edge = new EdgeDto(gav.toString(), target.toString(), relation);
					dto.relations.add(edge);
				}
			}

			Gson gson = new Gson();
			String result = gson.toJson(dto);

			return result;
		}

		@Override
		public void onClientLeft(Client client)
		{
			System.out.println("Client left.");
		}
	};

	static class EdgeDto
	{
		String from;

		String to;

		String label;

		Relation relation;

		public EdgeDto(String from, String to, Relation relation)
		{
			this.from = from;
			this.to = to;
			this.relation = relation;

			label = relation.toString();
		}
	}

	static class GraphDto
	{
		Set<String> gavs;

		Set<EdgeDto> relations;
	}
}
