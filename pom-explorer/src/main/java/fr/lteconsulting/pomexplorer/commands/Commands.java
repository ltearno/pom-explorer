package fr.lteconsulting.pomexplorer.commands;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.lteconsulting.hexa.client.tools.Func1;
import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Gav;
import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class Commands
{
	private final Map<String, Object> commands = new HashMap<>();

	public void addCommand(Object command)
	{
		if (command == null)
			return;

		String className = command.getClass().getSimpleName();
		addCommand(className.substring(0, className.length() - "Command".length()).toLowerCase(), command);
	}

	public void addCommand(String name, Object command)
	{
		if (name == null || command == null)
			return;

		commands.put(name, command);
	}

	public String help()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<b>List of commands</b><br/>");
		sb.append("<i>You can type only the first letters of commands, for example '<b>st co</b>' instead of '<b>stats components</b>'</i><br/><br/>");

		List<String> cs = new ArrayList<String>(commands.keySet());
		Collections.sort(cs);

		for (String shortcut : cs)
		{
			Object c = commands.get(shortcut);

			for (Method m : c.getClass().getDeclaredMethods())
			{
				if (!Modifier.isPublic(m.getModifiers()))
					continue;

				sb.append("<b>");

				String mName = m.getName();
				if (mName.equals("main"))
					sb.append(shortcut);
				else
					sb.append(shortcut + " " + mName);

				sb.append("</b>");

				for (int i = 0; i < m.getParameterTypes().length; i++)
				{
					Class<?> pCls = m.getParameterTypes()[i];

					if (pCls == Client.class || pCls == WorkingSession.class || pCls == CommandOptions.class
							|| pCls == ILogger.class)
						continue;

					sb.append(" <b><i>" + m.getParameters()[i].getName() + "</i></b>");
				}

				Help help = m.getAnnotation(Help.class);
				if (help != null)
					sb.append(" : " + help.value());

				sb.append("<br/>");
			}
		}

		return sb.toString();
	}

	/**
	 * Returns the error or null if success
	 */
	public void takeCommand(Client client, ILogger log, String text)
	{
		if (text == null || text.isEmpty())
		{
			log.html(Tools.warningMessage("no text"));
			return;
		}

		if ("?".equals(text))
			text = "help";

		final String parts[] = text.split(" ");
		CommandCallInfo info = findMethodForCommand(parts, log);
		if (info == null)
			return;

		CommandOptions options = new CommandOptions();
		WorkingSession session = null;
		Class<?>[] argTypes = info.method.getParameterTypes();
		Object[] args = new Object[argTypes.length];
		int curPart = 2;
		int curArg = 0;
		while (curArg < argTypes.length || curPart < parts.length)
		{
			if (curPart < parts.length)
			{
				String val = parts[curPart];
				if (val.startsWith("--"))
				{
					options.setOption(val.substring(2), parts[curPart + 1]);
					curPart += 2;
					continue;
				}

				if (val.startsWith("-"))
				{
					options.setOption(val.substring(1), true);
					curPart++;
					continue;
				}
			}

			if (curArg < argTypes.length)
			{
				if (argTypes[curArg] == Client.class)
				{
					args[curArg] = client;
					curArg++;
					continue;
				}

				if (argTypes[curArg] == ILogger.class)
				{
					args[curArg] = log;
					curArg++;
					continue;
				}

				if (argTypes[curArg] == WorkingSession.class)
				{
					if (session == null)
					{
						session = client.getCurrentSession();
						if (session == null)
						{
							log.html(Tools.warningMessage("you should have a session, type 'session create'."));
							return;
						}
					}

					args[curArg] = session;
					curArg++;
					continue;
				}

				if (argTypes[curArg] == CommandOptions.class)
				{
					args[curArg] = options;
					curArg++;
					continue;
				}

				if (argTypes[curArg] == FilteredGAVs.class)
				{
					args[curArg] = new FilteredGAVs(parts[curPart]);
					curArg++;
					curPart++;
					continue;
				}

				if (argTypes[curArg] == Gav.class)
				{
					args[curArg] = parts[curPart] == null ? null : Gav.parse(parts[curPart]);
					if (args[curArg] == null)
					{
						log.html(Tools.warningMessage("Argument " + (curArg + 1)
								+ " should be a GAV specified with the group:artifact:version format please"));
						return;
					}
					curArg++;
					curPart++;
					continue;
				}
			}

			if (argTypes[curArg] == Integer.class)
				args[curArg] = Integer.parseInt(parts[curPart]);
			else
				args[curArg] = parts[curPart];

			curPart++;
			curArg++;
		}

		try
		{
			info.method.invoke(info.command, args);
		}
		catch (Exception e)
		{
			log.html(Tools.errorMessage("Error when interpreting command '<b>" + text + "</b>'"));
			log.html("Command class : <b>" + info.command.getClass().getSimpleName() + "</b><br/>");
			log.html("Command method : <b>" + info.method.getName() + "</b><br/>");
			for (Object a : args)
				log.html("Argument : "
						+ (a == null ? "(null)" : ("class: " + a.getClass().getName() + " toString : " + a.toString()))
						+ "<br/>");

			Tools.logStacktrace(e, log);
		}
	}

	public class CommandCallInfo
	{
		public final Object command;

		public final Method method;

		public CommandCallInfo(Object command, Method method)
		{
			this.command = command;
			this.method = method;
		}
	}

	// public for testing
	public CommandCallInfo findMethodForCommand(String[] parts, ILogger log)
	{
		if (parts.length < 1)
		{
			log.html(Tools.warningMessage("syntax error (should be 'command [verb] [parameters]')"));
			return null;
		}

		List<Entry<String, Object>> potentialCommands = Tools.filter(commands.entrySet(),
				(e) -> e.getKey().toLowerCase().startsWith(parts[0].toLowerCase()));

		if (potentialCommands == null || potentialCommands.isEmpty())
		{
			log.html(Tools.warningMessage("command not found: " + parts[0]));
			return null;
		}
		if (potentialCommands.size() != 1)
		{
			List<String> possible = new ArrayList<>();
			potentialCommands.forEach((e) -> possible.add(e.getKey()));
			log.html(Tools.warningMessage("ambiguous command: " + parts[0] + " possible are " + possible));
			return null;
		}

		Entry<String, Object> commandEntry = potentialCommands.get(0);
		Object command = commandEntry.getValue();

		String verb = parts.length >= 2 ? parts[1] : "main";
		int nbParamsGiven = 0;
		for (int i = 2; i < parts.length; i++)
		{
			if (parts[i].startsWith("--"))
			{
				i++;
				continue;
			}

			if (parts[i].startsWith("-"))
				continue;

			nbParamsGiven++;
		}

		Method m = findMethodWith(command, verb, nbParamsGiven);
		if (m == null)
		{
			log.html(Tools.warningMessage("verb '" + verb + "' does not exist for command: " + commandEntry.getKey()));
			return null;
		}

		return new CommandCallInfo(command, m);
	}

	private Method findMethodWith(Object o, final String verb, final int nbParamsGiven)
	{
		List<Method> methods = Tools.filter(o.getClass().getMethods(), new Func1<Method, Boolean>()
		{
			@Override
			public Boolean exec(Method m)
			{
				return Modifier.isPublic(m.getModifiers()) && m.getName().toLowerCase().startsWith(verb.toLowerCase())
						&& getRealParametersCount(m) == nbParamsGiven;
			}
		});

		if (methods == null || methods.size() != 1)
			return null;

		return methods.get(0);
	}

	private int getRealParametersCount(Method m)
	{
		int c = 0;
		for (Class<?> t : m.getParameterTypes())
		{
			if (t != Client.class && t != WorkingSession.class && t != CommandOptions.class && t != ILogger.class)
				c++;
		}
		return c;
	}
}
