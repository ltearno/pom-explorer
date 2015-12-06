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

	private Session currentSession;

	public Client( int id, WebSocketChannel channel )
	{
		this.id = id;
		this.channel = channel;
	}

	public Session getCurrentSession()
	{
		return currentSession;
	}

	public void setCurrentSession( Session currentSession )
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

	public void sendHtml( String talkGuid, String html )
	{
		if( html == null )
			return;

		send( MessageFactory.htmlMessage( talkGuid, html ) );
	}

	public void sendClose( String talkGuid )
	{
		send( MessageFactory.closeTalkMessage( talkGuid ) );
	}

	public Message sendHangOutText( String talkGuid, String question )
	{
		Message message = MessageFactory.hangOutTextMessage( talkGuid, question );
		send( message );
		return message;
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
