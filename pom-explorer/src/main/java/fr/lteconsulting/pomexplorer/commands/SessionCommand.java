package fr.lteconsulting.pomexplorer.commands;

import java.util.List;

import fr.lteconsulting.pomexplorer.AppFactory;
import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class SessionCommand
{
	@Help( "tells about the current working session" )
	public void main( Client client, WorkingSession session, ILogger log )
	{
		log.html( "You are working on session " + session.getDescription() + "<br/>" );
	}

	@Help( "list the existing sessions" )
	public void list( ILogger log )
	{
		log.html( "There are " + AppFactory.get().sessions().size() + " opened work sessions:" );
		for( WorkingSession session : AppFactory.get().sessions() )
			log.html( "<br/>- " + session.hashCode() );
	}

	@Help( "create and attach a new session" )
	public void create( Client client, ILogger log )
	{
		WorkingSession session = new WorkingSession();
		session.configure( AppFactory.get().getSettings() );
		AppFactory.get().sessions().add( session );
		client.setCurrentSession( session );
		log.html( "Session created and registered. It has been attached to your profile<br/>" );
	}

	@Help( "sets the path to the maven settings file" )
	public void mavenSettingsFilePath( Client client, WorkingSession session, String path, ILogger log )
	{
		session.setMavenSettingsFilePath( path );

		log.html( "Session's maven settings file set to " + (path != null ? path : "(system default)<br/>") );
	}

	@Help( "sets the maven shell command to execute maven" )
	public void mavenShellCommand( Client client, WorkingSession session, String command, ILogger log )
	{
		session.setMavenShellCommand( command );

		log.html( "Session's maven shell command set to " + command + "<br/>" );
	}

	@Help( "sets the current working session to the specified index" )
	public void workOn( Client client, WorkingSession session, Integer index, ILogger log )
	{
		if( index == null )
		{
			main( client, session, log );
			return;
		}

		List<WorkingSession> sessions = AppFactory.get().sessions();
		if( index < 0 || index >= sessions.size() )
		{
			log.html( Tools.errorMessage( "The session " + index + " does not exist !" ) );
			return;
		}

		client.setCurrentSession( sessions.get( index ) );

		log.html( "Session " + index + " attached to your profile.<br/>" );
	}
}
