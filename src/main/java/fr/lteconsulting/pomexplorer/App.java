package fr.lteconsulting.pomexplorer;

import fr.lteconsulting.hexa.client.tools.Func1;
import fr.lteconsulting.hexa.client.tools.Func2;
import fr.lteconsulting.pomexplorer.WebServer.XWebServer;
import fr.lteconsulting.pomexplorer.web.commands.Command;
import fr.lteconsulting.pomexplorer.web.commands.CommandList;

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

				String[] parts = query.split(" ");
				String command = parts[0];
				String[] parameters = new String[parts.length - 1];
				for (int i = 1; i < parts.length; i++)
					parameters[i - 1] = parts[i];

				Command cmd = CommandList.getCommand(command);
				if (cmd == null)
					return "Unknown command '" + command + "'";

				String result = cmd.execute(client, parameters);
				return result;
			}
		};

		WebServer server = new WebServer(xWebServer, service, socket);
		server.start();
	}

	private void test()
	{
		

		
		// processFile(new File("C:\\gr"), g);

		

		

//		JGraphXAdapter<GAV, Dep> ga = new JGraphXAdapter<>(g);
//		GraphFrame frame = new GraphFrame(ga);
//
//		mxFastOrganicLayout layout = new mxFastOrganicLayout(ga);
//		layout.setUseBoundingBox(true);
//		layout.setForceConstant(200);
//		layout.execute(ga.getDefaultParent());
	}
}
