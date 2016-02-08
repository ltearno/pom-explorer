package fr.lteconsulting.pomexplorer.rpccommands;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange.GavChange;
import fr.lteconsulting.pomexplorer.model.Gav;

public class GavService
{
	public Object change( Session session, Client client, Log log, Gav oldGav, Gav newGav )
	{
		GavChange change = new GavChange( oldGav, newGav );
		session.graphChanges().add( change );
		log.html( "added change in change set: " + change + "<br/>" );

		return null;
	}
}
