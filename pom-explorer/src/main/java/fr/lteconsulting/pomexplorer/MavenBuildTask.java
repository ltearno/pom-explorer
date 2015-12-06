package fr.lteconsulting.pomexplorer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.lteconsulting.autothreaded.AutoThreaded;

@AutoThreaded
public class MavenBuildTask
{
	public Boolean build( Session session, Project project, String talkId )
	{
		try
		{
			log( session, project, talkId, "start ..." );
			Process p = Runtime.getRuntime().exec( new String[] { session.getMavenShellCommand(), "install", "-N", "-B", "-DskipTests" }, null,
					project.getPomFile().getParentFile() );

			BufferedReader reader = new BufferedReader( new InputStreamReader( p.getInputStream(), "ISO-8859-1" ) );
			String line = "";
			while( (line = reader.readLine()) != null )
			{
				log( session, project, talkId, line );
			}

			p.waitFor();

			log( session, project, talkId, "done (" + p.exitValue() + ")." );

			return p.exitValue() == 0;
		}
		catch( IOException | InterruptedException e )
		{
			log( session, project, talkId, "error ! " + e );

			return false;
		}
	}

	private void log( Session session, Project project, String talkId, String message )
	{
		message = Tools.buildMessage( "[building " + project.getGav() + "] " + message );
		for( Client client : session.getClients() )
			client.sendHtml( talkId, message );
	}
}
