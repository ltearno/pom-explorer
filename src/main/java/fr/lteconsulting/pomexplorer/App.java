package fr.lteconsulting.pomexplorer;

import fr.lteconsulting.hexa.client.tools.Func1;
import fr.lteconsulting.hexa.client.tools.Func2;
import fr.lteconsulting.pomexplorer.WebServer.XWebServer;

/**
 * Hello world!
 */
public class App
{
	public static void main(String[] args)
	{
		App app = new App();
		app.run();
	}

	private void run()
	{
		XWebServer xWebServer = new XWebServer()
		{
			@Override
			public void onNewClient(Client client)
			{
				System.out.println("New client " + client.getId());
				
				// create a session for the user
				AppFactory.get().commands().takeCommand( client, "session create" );
				AppFactory.get().commands().takeCommand( client, "analyse directory c:\\documents\\repos" );
			}

			@Override
			public void onClientLeft(Client client)
			{
				System.out.println("Bye bye client !");
			}
		};

		Func1<String, String> service = new Func1<String, String>()
		{
			@Override
			public String exec(String query)
			{
				return "Super, you just asked for " + query;
			}
		};

		Func2<Client, String, String> socket = new Func2<Client, String, String>()
		{
			@Override
			public String exec(Client client, String query)
			{
				if (query == null || query.isEmpty())
					return "NOP.";

				return AppFactory.get().commands().takeCommand( client, query );
			}
		};

		WebServer server = new WebServer(xWebServer, service, socket);
		server.start();
	}
}
