package fr.lteconsulting.pomexplorer;

import static io.undertow.Handlers.websocket;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.io.IOException;
import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.Map;

import fr.lteconsulting.hexa.client.tools.Func2;

public class WebServer
{
	private final XWebServer xWebServer;

	private final Func2<Client, String, String> socketCallback;

	private final Map<Integer, Client> clients = new HashMap<>();

	public WebServer( XWebServer xWebServer, Func2<Client, String, String> socketCallback )
	{
		this.xWebServer = xWebServer;
		this.socketCallback = socketCallback;
	}

	public interface XWebServer
	{
		void onNewClient( Client client );

		void onClientLeft( Client client );
	}

	private Client getClient( Channel channel )
	{
		return clients.get( System.identityHashCode( channel ) );
	}

	public void start()
	{
		PathHandler pathHandler = new PathHandler();

		// web app static files
		pathHandler.addPrefixPath( "/", new ResourceHandler( new ClassPathResourceManager( getClass().getClassLoader(), "fr/lteconsulting/pomexplorer/webapp" ) ).addWelcomeFiles( "index.html" ) );

		// web socket end point
		pathHandler.addPrefixPath( "/ws", websocket( new WebSocketConnectionCallback()
		{
			@Override
			public void onConnect( WebSocketHttpExchange exchange, WebSocketChannel channel )
			{
				Client client = new Client( System.identityHashCode( channel ), channel );
				clients.put( client.getId(), client );

				xWebServer.onNewClient( client );
				
				channel.getReceiveSetter().set( new AbstractReceiveListener()
				{
					@Override
					protected void onFullTextMessage( WebSocketChannel channel, BufferedTextMessage message )
					{
						String messageData = socketCallback.exec( getClient( channel ), message.getData() );
						WebSockets.sendText( messageData, channel, null );
					}

					@Override
					protected void onClose( WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel ) throws IOException
					{
						super.onClose( webSocketChannel, channel );

						xWebServer.onClientLeft( getClient( channel ) );
						clients.remove( System.identityHashCode( channel ) );
					}
				} );
				
				channel.resumeReceives();
			}
		} ) );

		Undertow server = Undertow.builder().addHttpListener( 90, "localhost" ).setHandler( pathHandler ).build();
		server.start();
	}
}
