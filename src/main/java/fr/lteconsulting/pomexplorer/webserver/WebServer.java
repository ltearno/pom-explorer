package fr.lteconsulting.pomexplorer.webserver;

import static io.undertow.Handlers.websocket;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
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
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.lteconsulting.pomexplorer.Client;

public class WebServer
{
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final XWebServer xWebServer;

	private final Map<Integer, Client> clients = new HashMap<>();

	public WebServer( XWebServer xWebServer )
	{
		this.xWebServer = xWebServer;
	}

	private Client getClient( Channel channel )
	{
		return clients.get( System.identityHashCode( channel ) );
	}

	private String getQueryParameter( HttpServerExchange exchange, String name )
	{
		Deque<String> de = exchange.getQueryParameters().get( name );
		if( de == null || de.isEmpty() )
			return null;

		assert de.size() == 1;
		String value = de.getFirst();
		return value;
	}

	public void start()
	{
		PathHandler pathHandler = new PathHandler();

		// web app static files
		pathHandler.addPrefixPath( "/", new ResourceHandler( new ClassPathResourceManager( getClass().getClassLoader(), "fr/lteconsulting/pomexplorer/webapp" ) ).addWelcomeFiles( "index.html" ) );

		// http end point
		pathHandler.addExactPath( "/graph", new HttpHandler()
		{
			@Override
			public void handleRequest( HttpServerExchange exchange ) throws Exception
			{
				if( exchange.isInIoThread() )
				{
					exchange.dispatch(this);
					return;
				}
				
				String result = xWebServer.onGraphQuery(getQueryParameter( exchange, "session" ));
				exchange.getResponseSender().send( result );
			}
		} );

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
					protected void onFullTextMessage( final WebSocketChannel channel, final BufferedTextMessage message )
					{
						executor.submit( new Runnable()
						{
							@Override
							public void run()
							{
								String messageData = xWebServer.onWebsocketMessage( getClient( channel ), message.getData() );
								WebSockets.sendText( messageData, channel, null );
							}
						} );
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

		Undertow server = Undertow.builder().addHttpListener( 90, "0.0.0.0" ).setHandler( pathHandler ).build();
		server.start();
	}
}
