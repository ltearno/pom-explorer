package fr.lteconsulting.pomexplorer;

import com.google.gson.Gson;

import fr.lteconsulting.pomexplorer.webserver.Message;
import fr.lteconsulting.pomexplorer.webserver.MessageFactory;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

public class Client
{
	private final int id;

	private final WebSocketChannel channel;

	private WorkingSession currentSession;

	public Client( int id, WebSocketChannel channel )
	{
		this.id = id;
		this.channel = channel;
	}

	public WorkingSession getCurrentSession()
	{
		return currentSession;
	}

	public void setCurrentSession( WorkingSession currentSession )
	{
		if( this.currentSession != null )
			this.currentSession.removeClient( this );

		this.currentSession = currentSession;

		if( this.currentSession != null )
			this.currentSession.addClient( this );
	}

	public int getId()
	{
		return id;
	}

	public WebSocketChannel getChannel()
	{
		return channel;
	}
	
	public void send( String talkGuid, String html )
	{
		if( html == null || html.isEmpty() )
			return;
		
		send( MessageFactory.htmlMessage( talkGuid, html ) );
	}
	
	public void sendClose( String talkGuid )
	{
		send( MessageFactory.closeTalkMessage( talkGuid ) );
	}
	
	public void send( Message message )
	{
		if( message == null )
			return;
		
		Gson gson = new Gson();
		String text = gson.toJson( message );

		WebSockets.sendText( text, channel, null );
	}
}
