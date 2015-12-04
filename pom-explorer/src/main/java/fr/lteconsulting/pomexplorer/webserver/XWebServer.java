package fr.lteconsulting.pomexplorer.webserver;

import fr.lteconsulting.pomexplorer.Client;

public interface XWebServer
{
	void onNewClient( Client client );

	void onWebsocketMessage( Client client, String message );

	String onGraphQuery( String sessionIdString, String graphQueryId );

	void onClientLeft( Client client );
}