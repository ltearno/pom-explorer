package fr.lteconsulting.pomexplorer.web.commands;

import fr.lteconsulting.pomexplorer.AppFactory;
import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class SessionCommand extends BaseCommand
{
	public SessionCommand()
	{
		super("session");
	}

	@Override
	public String execute(Client client, String[] params)
	{
		if (params == null || params.length < 1)
		{
			WorkingSession session = client.getCurrentSession();
			return session != null ? ("You are working on session " + session)
					: "You are not working on any session.";
		}

		String flag = params[0];
		switch (flag)
		{
			case "-l":
				StringBuilder sb = new StringBuilder();
				sb.append("There are " + AppFactory.get().sessions().size() + " opened work sessions:");
				for (WorkingSession session : AppFactory.get().sessions())
				{
					sb.append("<br/>- " + session.hashCode());
				}
				return sb.toString();

			case "-c":
			{
				WorkingSession session = new WorkingSession();
				AppFactory.get().sessions().add(session);
				client.setCurrentSession(session);
				return "Session created and registered. It has been attached to your profile.";
			}
		}

		return null;
	}
}
