package fr.lteconsulting.pomexplorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;

import com.google.gson.Gson;

import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.webserver.WebServer;
import fr.lteconsulting.pomexplorer.webserver.XWebServer;

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


	static class EdgeDto
	{
		String from;

		String to;

		String label;

		Relation relation;

		public EdgeDto( String from, String to, Relation relation )
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

	private void run()
	{
		WebServer server = new WebServer( xWebServer );
		server.start();
	}
	
	private XWebServer xWebServer = new XWebServer()
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
		public String onWebsocketMessage( Client client, String message )
		{
			if( message == null || message.isEmpty() )
				return "nop.";

			return AppFactory.get().commands().takeCommand( client, message );
		}
		
		@Override
		public String onGraphQuery( String sessionIdString )
		{
			List<WorkingSession> sessions = AppFactory.get().sessions();
			if( sessions == null || sessions.isEmpty() )
				return "No session available. Go to main page !";

			WorkingSession session = null;

			try
			{
				Integer sessionId = Integer.parseInt( sessionIdString );
				if( sessionId != null )
				{
					for( WorkingSession s : sessions )
					{
						if( System.identityHashCode( s ) == sessionId )
						{
							session = s;
							break;
						}
					}
				}
			}
			catch( Exception e )
			{
			}

			if( session == null )
				session = sessions.get( 0 );

			DirectedGraph<GAV, Relation> g = session.graph().internalGraph();

			GraphDto dto = new GraphDto();
			dto.gavs = new HashSet<>();
			dto.relations = new HashSet<>();
			for( GAV gav : g.vertexSet() )
			{
				dto.gavs.add( gav.toString() );

				for( Relation relation : g.outgoingEdgesOf( gav ) )
				{
					GAV target = g.getEdgeTarget( relation );
					EdgeDto edge = new EdgeDto( gav.toString(), target.toString(), relation );
					dto.relations.add( edge );
				}
			}

			Gson gson = new Gson();
			String result = gson.toJson( dto );
			
			return result;
		}

		@Override
		public void onClientLeft( Client client )
		{
			System.out.println( "Client left." );
		}
	};

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
