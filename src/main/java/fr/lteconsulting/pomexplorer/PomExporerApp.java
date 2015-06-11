package fr.lteconsulting.pomexplorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import fr.lteconsulting.hexa.client.tools.Func2;
import fr.lteconsulting.pomexplorer.WebServer.XWebServer;

/**
 * POM Explorer main class
 */
public class PomExporerApp
{
	public static void main( String[] args )
	{
		System.out.println( "" );
		System.out.println( "" );
		System.out.println( "Welcome to POM Explorer (beta) !" );
		System.out.println( "================================" );
		System.out.println( "visit http://localhost:90 with a browser to access the application." );
		System.out.println( "" );
		System.out.println( "" );

		PomExporerApp app = new PomExporerApp();
		app.run();
	}

	private void run()
	{
		XWebServer xWebServer = new XWebServer()
		{
			@Override
			public void onNewClient( Client client )
			{
				System.out.println( "New client " + client.getId() );

				// running the default script
				List<String> commands = readFileLines( "welcome.commands" );
				for( String command : commands )
				{
					if( command.isEmpty() || command.startsWith( "#" ) )
						continue;

					if( command.startsWith( "=" ) )
					{
						String message = command.substring( 1 );
						if( message.isEmpty() )
							message = "<br/>";
						client.send( message );
					}
					else
						client.send( AppFactory.get().commands().takeCommand( client, command ) );
				}
			}

			@Override
			public void onClientLeft( Client client )
			{
				System.out.println( "Client left." );
			}
		};

		Func2<Client, String, String> socket = new Func2<Client, String, String>()
		{
			@Override
			public String exec( Client client, String query )
			{
				if( query == null || query.isEmpty() )
					return "nop.";

				return AppFactory.get().commands().takeCommand( client, query );
			}
		};

		WebServer server = new WebServer( xWebServer, socket );
		server.start();
	}

	private static List<String> readFileLines( String path )
	{
		ArrayList<String> res = new ArrayList<String>();

		File file = new File( path );
		if( !file.exists() )
			return res;

		try
		{
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( file ), "UTF8" ) );

			String str;

			while( (str = in.readLine()) != null )
			{
				res.add( str );
			}

			in.close();
		}
		catch( Exception e )
		{
		}

		return res;
	}
}
