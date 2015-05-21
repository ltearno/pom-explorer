package fr.lteconsulting.pomexplorer.web.commands;

import fr.lteconsulting.pomexplorer.AppFactory;
import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class SessionCommand
{
	@Help("tells about the current working session")
	public String main( Client client, WorkingSession session )
	{
		return "You are working on session " + session;
	}

	@Help("list the existing sessions")
	public String list()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "There are " + AppFactory.get().sessions().size() + " opened work sessions:" );
		for( WorkingSession session : AppFactory.get().sessions() )
		{
			sb.append( "<br/>- " + session.hashCode() );
		}
		return sb.toString();
	}

	@Help("create and attach a new session")
	public String create( Client client )
	{
		WorkingSession session = new WorkingSession();
		AppFactory.get().sessions().add( session );
		client.setCurrentSession( session );
		return "Session created and registered. It has been attached to your profile.";
	}
}
