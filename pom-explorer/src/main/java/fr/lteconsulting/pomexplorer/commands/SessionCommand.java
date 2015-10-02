package fr.lteconsulting.pomexplorer.commands;

import java.util.List;

import fr.lteconsulting.pomexplorer.AppFactory;
import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class SessionCommand
{
	@Help( "tells about the current working session" )
	public String main( Client client, WorkingSession session )
	{
		return "You are working on session " + session.getDescription();
	}

	@Help( "list the existing sessions" )
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

	@Help( "create and attach a new session" )
	public String create( Client client )
	{
		WorkingSession session = new WorkingSession();
		session.configure( AppFactory.get().getSettings() );
		AppFactory.get().sessions().add( session );
		client.setCurrentSession( session );
		return "Session created and registered. It has been attached to your profile.";
	}

	@Help( "sets the path to the maven settings file" )
	public String mavenSettingsFilePath( Client client, WorkingSession session, String path )
	{
		session.setMavenSettingsFilePath( path );

		return "Session's maven settings file set to " + (path != null ? path : "(system default)");
	}
	
	@Help( "sets the maven shell command to execute maven" )
	public String mavenShellCommand( Client client, WorkingSession session, String command )
	{
		session.setMavenShellCommand( command );

		return "Session's maven shell command set to " + command;
	}

	@Help( "sets the current working session to the specified index" )
	public String workOn( Client client, WorkingSession session, Integer index )
	{
		if( index == null )
			return main( client, session );

		List<WorkingSession> sessions = AppFactory.get().sessions();
		if( index < 0 || index >= sessions.size() )
			return "<span style='color:red;'>The session " + index + " does not exist !</span>";

		client.setCurrentSession( sessions.get( index ) );

		return "Session " + index + " attached to your profile.";
	}
}
