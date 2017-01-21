package fr.lteconsulting.pomexplorer.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.lteconsulting.pomexplorer.AppFactory;
import fr.lteconsulting.pomexplorer.ApplicationSession;
import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Tools;

public class SessionCommand
{
	@Help( "tells about the current working session" )
	public void main( Client client, ApplicationSession session, Log log )
	{
		log.html( "You are working on session " + session.getDescription() + "<br/>" );
	}

	@Help( "list the existing sessions" )
	public void list( Log log )
	{
		log.html( "There are " + AppFactory.get().sessions().size() + " opened work sessions:" );
		for( ApplicationSession session : AppFactory.get().sessions() )
			log.html( "<br/>- WorkingSession " + session.hashCode() + ", " + (session.getClients().isEmpty() ? "inactive" : (session.getClients().size() + " connected users")) );
	}

	@Help( "create and attach a new session" )
	public void create( Client client, Log log )
	{
		ApplicationSession session = new ApplicationSession();
		session.configure( AppFactory.get().getSettings() );
		AppFactory.get().sessions().add( session );
		client.setCurrentSession( session );
		log.html( "Session created and registered. It has been attached to your profile<br/>" );
	}

	@Help( "sets the path to the maven settings file" )
	public void mavenSettingsFilePath( Client client, ApplicationSession session, String path, Log log )
	{
		session.setMavenSettingsFilePath( path );

		log.html( "Session's maven settings file set to " + (path != null ? path : "(system default)<br/>") );
	}

	@Help( "sets the maven shell command to execute maven" )
	public void mavenShellCommand( Client client, ApplicationSession session, String command, Log log )
	{
		session.setMavenShellCommand( command );

		log.html( "Session's maven shell command set to " + command + "<br/>" );
	}

	@Help( "ignore directories when analyzing projects" )
    public void ignoreDirs( Client client, ApplicationSession session, @Help( "comma separated list of directory names or absolute paths" ) String dirNames, Log log )
    {
        if (dirNames != null) {
            Set<String> ignoredDirs = new HashSet<>(Arrays.asList(dirNames.split(",")));
            session.setIgnoredDirs(ignoredDirs);
        }
        else {
            session.setIgnoredDirs(Collections.emptySet());
        }
        log.html( "Session's custom ignored directories set to " + (dirNames != null && !dirNames.isEmpty() ? dirNames : "(none)") + "<br/>" );
    }
	
	@Help( "sets the current working session to the specified index" )
	public void workOn( Client client, ApplicationSession session, Integer index, Log log )
	{
		if( index == null )
		{
			main( client, session, log );
			return;
		}

		List<ApplicationSession> sessions = AppFactory.get().sessions();
		if( index < 0 || index >= sessions.size() )
		{
			log.html( Tools.errorMessage( "The session " + index + " does not exist !" ) );
			return;
		}

		client.setCurrentSession( sessions.get( index ) );

		log.html( "Session " + index + " attached to your profile.<br/>" );
	}
}
