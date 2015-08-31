package fr.lteconsulting.pomexplorer.webserver;

import static io.undertow.Handlers.websocket;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.Headers;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.lteconsulting.pomexplorer.Client;

public class WebServer
{
	private final static String DATA_FILE_PREFIX_URL = "/files/";
	private final static String DATA_FILE_STORE_DIR = "served-data";

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

	/**
	 * Stores a file to make it servable by the web server
	 * 
	 * @param fileName
	 *            the file name
	 * @return The URL to get the file
	 */
	public Writer pushFile( String fileName )
	{
		try
		{
			FileWriter fileWriter = new FileWriter( Paths.get( DATA_FILE_STORE_DIR, fileName ).toString() );
			return fileWriter;
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}

		return null;
	}

	public String getFileUrl( String fileName )
	{
		return DATA_FILE_PREFIX_URL + fileName;
	}

	public void start()
	{
		PathHandler pathHandler = new PathHandler();

		// web app static files
		pathHandler.addPrefixPath( "/", new ResourceHandler( new ClassPathResourceManager( getClass().getClassLoader(), "fr/lteconsulting/pomexplorer/webapp" ) ).addWelcomeFiles( "index.html" ) );

		pathHandler.addPrefixPath( DATA_FILE_PREFIX_URL, new HttpHandler()
		{
			@Override
			public void handleRequest( HttpServerExchange exchange ) throws Exception
			{
				executor.submit( () -> {
					String name = exchange.getRelativePath();

					File dataDir = new File( DATA_FILE_STORE_DIR );
					dataDir.mkdirs();

					ByteBuffer buf = readFile( Paths.get( dataDir.toPath().toString(), name ).toString() );

					if( buf != null )
					{
						exchange.getResponseHeaders().put( Headers.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"" );
						exchange.getResponseSender().send( buf );
					}
					else
					{
						exchange.getResponseSender().send( "not found !" );
					}
				} );
			}

			private ByteBuffer readFile( String path )
			{
				try
				{
					RandomAccessFile aFile = new RandomAccessFile( path, "r" );
					FileChannel inChannel = aFile.getChannel();
					MappedByteBuffer buffer = inChannel.map( FileChannel.MapMode.READ_ONLY, 0, inChannel.size() );
					buffer.load();
					inChannel.close();
					aFile.close();
					return buffer;
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				return null;
			}
		} );

		// http end point
		pathHandler.addExactPath( "/graph", new HttpHandler()
		{
			@Override
			public void handleRequest( HttpServerExchange exchange ) throws Exception
			{
				// executor.submit( ( ) -> {
				String result = xWebServer.onGraphQuery( getQueryParameter( exchange, "session" ) );
				exchange.getResponseSender().send( result );
				// } );
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
						executor.submit( () -> {
							String messageData = xWebServer.onWebsocketMessage( getClient( channel ), message.getData() );
							WebSockets.sendText( messageData, channel, null );
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
