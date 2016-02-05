package fr.lteconsulting.pomexplorer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.CatchClause;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.google.gson.Gson;

import fr.lteconsulting.pomexplorer.commands.AnalyzeCommand;
import fr.lteconsulting.pomexplorer.commands.BuildCommand;
import fr.lteconsulting.pomexplorer.commands.ChangeCommand;
import fr.lteconsulting.pomexplorer.commands.CheckCommand;
import fr.lteconsulting.pomexplorer.commands.ClassesCommand;
import fr.lteconsulting.pomexplorer.commands.Commands;
import fr.lteconsulting.pomexplorer.commands.ExportCommand;
import fr.lteconsulting.pomexplorer.commands.GarbageCommand;
import fr.lteconsulting.pomexplorer.commands.GavCommand;
import fr.lteconsulting.pomexplorer.commands.GitCommand;
import fr.lteconsulting.pomexplorer.commands.GraphCommand;
import fr.lteconsulting.pomexplorer.commands.HelpCommand;
import fr.lteconsulting.pomexplorer.commands.ProjectsCommand;
import fr.lteconsulting.pomexplorer.commands.SessionCommand;
import fr.lteconsulting.pomexplorer.commands.StatsCommand;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.rpccommands.ProjectsService;
import fr.lteconsulting.pomexplorer.rpccommands.RpcServices;
import fr.lteconsulting.pomexplorer.uirpc.ProjectDto;
import fr.lteconsulting.pomexplorer.webserver.Message;
import fr.lteconsulting.pomexplorer.webserver.MessageFactory;
import fr.lteconsulting.pomexplorer.webserver.RpcMessage;
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

	private final List<Session> sessions = new ArrayList<>();

	private Commands commands;

	private RpcServices rpcServices;

	private ApplicationSettings settings;

	private WebServer webServer;

	public List<Session> sessions()
	{
		return sessions;
	}

	public Commands commands()
	{
		if( commands == null )
		{
			commands = new Commands();

			// shell commands available
			commands.addCommand( new HelpCommand() );
			commands.addCommand( new SessionCommand() );
			commands.addCommand( new AnalyzeCommand() );
			commands.addCommand( new StatsCommand() );
			commands.addCommand( new GavCommand() );
			commands.addCommand( new ProjectsCommand() );
			commands.addCommand( new ChangeCommand() );
			commands.addCommand( new BuildCommand() );
			commands.addCommand( new GraphCommand() );
			commands.addCommand( new CheckCommand() );
			commands.addCommand( new ClassesCommand() );
			commands.addCommand( new GitCommand() );
			commands.addCommand( new GarbageCommand() );
			commands.addCommand( new ExportCommand() );
		}

		return commands;
	}

	public RpcServices rpcServices()
	{
		if( rpcServices == null )
		{
			rpcServices = new RpcServices();

			rpcServices.addService( new ProjectsService() );
		}

		return rpcServices;
	}

	public ApplicationSettings getSettings()
	{
		if( settings == null )
		{
			settings = new ApplicationSettings();
			settings.load();
		}

		return settings;
	}

	public WebServer webServer()
	{
		if( webServer == null )
			webServer = new WebServer( xWebServer );

		return webServer;
	}

	private XWebServer xWebServer = new XWebServer()
	{
		@Override
		public void onNewClient( Client client )
		{
			System.out.println( "New client " + client.getId() );

			final String talkId = MessageFactory.newGuid();

			// running the default script
			List<String> commands = Tools.readFileLines( "welcome.commands" );
			for( String command : commands )
			{
				if( command.isEmpty() || command.startsWith( "#" ) )
					continue;

				if( command.startsWith( "=" ) )
				{
					String message = command.substring( 1 );
					if( message.isEmpty() )
						message = "<br/>";
					client.sendHtml( talkId, message );
				}
				else
					AppFactory.get().commands().takeCommand( client, createLogger( client, talkId ), command );
			}

			client.sendClose( talkId );
		}

		@Override
		public void onWebsocketMessage( Client client, String messageText )
		{
			Gson gson = new Gson();
			Message message = gson.fromJson( messageText, Message.class );
			if( message == null )
			{
				client.sendHtml( MessageFactory.newGuid(), Tools.warningMessage( "null message received !" ) );
				return;
			}

			if( "text/command".equals( message.getPayloadFormat() ) )
			{
				commands().takeCommand( client, createLogger( client, message.getTalkGuid() ), message.getPayload() );
			}
			else if( "hangout/reply".equals( message.getPayloadFormat() ) )
			{
				for( int i = 0; i < waitingHangouts.size(); i++ )
				{
					HangOutHandle handle = waitingHangouts.get( i );
					if( handle.message.getGuid().equals( message.getResponseTo() ) )
					{
						handle.answer = message.getPayload();
						handle.waitingAnswer = false;
						synchronized( handle )
						{
							handle.notify();
						}
					}
				}
			}
			else if( "application/rpc".equals( message.getPayloadFormat() ) )
			{
				try
				{
					RpcMessage rpcMessage = gson.fromJson( message.getPayload(), RpcMessage.class );
					Object result = rpcServices().takeCall( client, createLogger( client, message.getTalkGuid() ), rpcMessage );
					client.send( new Message( MessageFactory.newGuid(), message.getTalkGuid(), null, true, "application/rpc", gson.toJson( result ) ) );
				}
				catch( Exception o )
				{
					System.out.println( "BIG ERROR RPC ... TODO " + o );
					o.printStackTrace();
				}
			}
			else
			{
				client.sendHtml( message.getTalkGuid(), Tools.warningMessage( "ununderstood message " + messageText + ".<br/>" ) );
			}

			client.sendClose( message.getTalkGuid() );

		}

		@Override
		public String onGraphQuery( String sessionIdString, String graphQueryId )
		{
			List<Session> sessions = AppFactory.get().sessions();
			if( sessions == null || sessions.isEmpty() )
				return "No session available. Go to main page !";

			Session session = null;

			try
			{
				Integer sessionId = Integer.parseInt( sessionIdString );
				if( sessionId != null )
				{
					for( Session s : sessions )
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

			GraphQuery query = GraphQuery.get( graphQueryId );

			PomGraphReadTransaction tx = session.graph().read();

			GraphDto dto = new GraphDto();
			dto.gavs = new HashSet<>();
			dto.relations = new HashSet<>();

			if( query != null && query.getRoots() != null )
			{
				for( Gav root : query.getRoots() )
				{
					Set<Relation> relations = tx.relationsRec( root );

					dto.gavs.add( root.toString() );

					for( Relation relation : relations )
					{
						Gav dSource = tx.sourceOf( relation );
						Gav dTarget = tx.targetOf( relation );

						dto.gavs.add( dSource.toString() );
						dto.gavs.add( dTarget.toString() );

						EdgeDto edge = new EdgeDto( dSource.toString(), dTarget.toString(), relation );
						dto.relations.add( edge );
					}
				}
			}
			else
			{
				for( Gav gav : tx.gavs() )
				{
					dto.gavs.add( gav.toString() );

					for( Relation relation : tx.relations( gav ) )
					{
						Gav target = tx.targetOf( relation );
						EdgeDto edge = new EdgeDto( gav.toString(), target.toString(), relation );
						dto.relations.add( edge );
					}
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

	private final List<HangOutHandle> waitingHangouts = new ArrayList<>();

	private class HangOutHandle
	{
		final Message message;

		String answer;

		boolean waitingAnswer;

		public HangOutHandle( Message message )
		{
			this.message = message;
		}
	}

	private Log createLogger( Client client, String talkId )
	{
		return new Log()
		{
			@Override
			public void html( String log )
			{
				client.sendHtml( talkId, log );
			}

			@Override
			public String prompt( String question )
			{
				Message message = client.sendHangOutText( talkId, question );
				HangOutHandle handle = new HangOutHandle( message );
				waitingHangouts.add( handle );

				handle.waitingAnswer = true;
				synchronized( handle )
				{
					while( handle.waitingAnswer )
					{
						try
						{
							handle.wait();
						}
						catch( InterruptedException e )
						{
							e.printStackTrace();
						}
					}
				}

				return handle.answer;
			}
		};
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

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((from == null) ? 0 : from.hashCode());
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			result = prime * result + ((relation == null) ? 0 : relation.hashCode());
			result = prime * result + ((to == null) ? 0 : to.hashCode());
			return result;
		}

		@Override
		public boolean equals( Object obj )
		{
			if( this == obj )
				return true;
			if( obj == null )
				return false;
			if( getClass() != obj.getClass() )
				return false;

			EdgeDto other = (EdgeDto) obj;

			if( from == null )
			{
				if( other.from != null )
					return false;
			}
			else if( !from.equals( other.from ) )
				return false;
			if( label == null )
			{
				if( other.label != null )
					return false;
			}
			else if( !label.equals( other.label ) )
				return false;
			if( relation == null )
			{
				if( other.relation != null )
					return false;
			}
			else if( !relation.equals( other.relation ) )
				return false;
			if( to == null )
			{
				if( other.to != null )
					return false;
			}
			else if( !to.equals( other.to ) )
				return false;
			return true;
		}
	}

	static class GraphDto
	{
		Set<String> gavs;

		Set<EdgeDto> relations;
	}
}
