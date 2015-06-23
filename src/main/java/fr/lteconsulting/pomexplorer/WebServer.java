package fr.lteconsulting.pomexplorer;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;

import com.google.gson.Gson;

import fr.lteconsulting.hexa.client.tools.Func2;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

public class WebServer
{
	private final XWebServer xWebServer;

	private final Func2<Client, String, String> socketCallback;

	private final Map<Integer, Client> clients = new HashMap<>();

	public WebServer(XWebServer xWebServer, Func2<Client, String, String> socketCallback)
	{
		this.xWebServer = xWebServer;
		this.socketCallback = socketCallback;
	}

	public interface XWebServer
	{
		void onNewClient(Client client);

		void onClientLeft(Client client);
	}

	private Client getClient(Channel channel)
	{
		return clients.get(System.identityHashCode(channel));
	}

	static class EdgeDto
	{
		String from;

		String to;

		String label;
		
		Relation relation;

		public EdgeDto(String from, String to, Relation relation)
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

	public void start()
	{
		PathHandler pathHandler = new PathHandler();

		// web app static files
		pathHandler.addPrefixPath("/", new ResourceHandler(new ClassPathResourceManager(getClass().getClassLoader(),
				"fr/lteconsulting/pomexplorer/webapp")).addWelcomeFiles("index.html"));

		// http end point
		pathHandler.addExactPath("/graph", new HttpHandler()
		{
			boolean takeGav(GAV gav)
			{
				return true;//gav.getGroupId().startsWith("fr");
			}

			boolean takeRelation(Relation relation)
			{
				return true;/*(relation instanceof DependencyRelation)
						&& (((DependencyRelation)relation).getScope() == null || "COMPILE"
								.equals(((DependencyRelation)relation).getScope()));*/
			}

			@Override
			public void handleRequest(HttpServerExchange exchange) throws Exception
			{
				List<WorkingSession> sessions = AppFactory.get().sessions();
				if (sessions == null || sessions.isEmpty())
					return;

				WorkingSession session = sessions.get(0);

				DirectedGraph<GAV, Relation> g = session.graph().internalGraph();

				GraphDto dto = new GraphDto();
				dto.gavs = new HashSet<>();
				dto.relations = new HashSet<>();
				for (GAV gav : g.vertexSet())
				{
					if (!takeGav(gav))
						continue;

					dto.gavs.add(gav.toString());

					for (Relation relation : g.outgoingEdgesOf(gav))
					{
						if (!takeRelation(relation))
							continue;

						GAV target = g.getEdgeTarget(relation);
						if (!takeGav(target))
							continue;

						EdgeDto edge = new EdgeDto(gav.toString(), target.toString(), relation);
						dto.relations.add(edge);
					}
				}

				Gson gson = new Gson();
				exchange.getResponseSender().send(gson.toJson(dto));
			}
		});

		// web socket end point
		pathHandler.addPrefixPath("/ws", websocket(new WebSocketConnectionCallback()
		{
			@Override
			public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel)
			{
				Client client = new Client(System.identityHashCode(channel), channel);
				clients.put(client.getId(), client);

				xWebServer.onNewClient(client);

				channel.getReceiveSetter().set(new AbstractReceiveListener()
				{
					@Override
					protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message)
					{
						String messageData = socketCallback.exec(getClient(channel), message.getData());
						WebSockets.sendText(messageData, channel, null);
					}

					@Override
					protected void onClose(WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel)
							throws IOException
					{
						super.onClose(webSocketChannel, channel);

						xWebServer.onClientLeft(getClient(channel));
						clients.remove(System.identityHashCode(channel));
					}
				});

				channel.resumeReceives();
			}
		}));

		Undertow server = Undertow.builder().addHttpListener(90, "0.0.0.0").setHandler(pathHandler).build();
		server.start();
	}
}
