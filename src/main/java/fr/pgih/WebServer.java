package fr.pgih;

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
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import fr.lteconsulting.hexa.client.tools.Func1;

public class WebServer
{
	private final Func1<String, String> serviceCallback;

	public WebServer(Func1<String, String> serviceCallback)
	{
		this.serviceCallback = serviceCallback;
	}

	public void start()
	{
		PathHandler pathHandler = new PathHandler();

		// web app static files
		pathHandler.addPrefixPath("/", new ResourceHandler(new ClassPathResourceManager(getClass().getClassLoader(),
				"fr/lteconsulting/pomexplorer/webapp")).addWelcomeFiles("index.html"));

		// service end point
		pathHandler.addPrefixPath("/service", new HttpHandler()
		{
			@Override
			public void handleRequest(HttpServerExchange exchange) throws Exception
			{
				exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
				String query = exchange.getRelativePath();
				if (query.startsWith("/"))
					query = query.substring(1);
				exchange.getResponseSender().send(serviceCallback.exec(query));
			}
		});

		// web socket end point
		pathHandler.addPrefixPath("/ws", websocket(new WebSocketConnectionCallback()
		{
			@Override
			public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel)
			{
				channel.getReceiveSetter().set(new AbstractReceiveListener()
				{
					@Override
					protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message)
					{
						final String messageData = serviceCallback.exec(message.getData());
						// for (WebSocketChannel session : channel.getPeerConnections())
						// {
						// WebSockets.sendText(messageData, session, null);
						// }
						WebSockets.sendText(messageData, channel, null);
					}
				});
				channel.resumeReceives();
			}
		}));

		Undertow server = Undertow.builder()
				.addHttpListener(90, "localhost")
				.setHandler(pathHandler)
				.build();
		server.start();
	}
}
