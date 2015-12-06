package fr.lteconsulting.pomexplorer.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

public class ExportCommand
{
	@Help( "exports the node connections in csv format (outputs the file 'export.csv')." )
	public void csv( Client client, Session session, Log log )
	{
		PomGraphReadTransaction tx = session.graph().read();

		try
		{
			ExportVisitor visitor = new ExportVisitor( session );

			tx.relations().stream().forEach( visitor::visit );

			visitor.close();

			log.html( tx.relations().size() + " relations exported to file 'export.csv'." );
		}
		catch( FileNotFoundException | UnsupportedEncodingException e )
		{
			e.printStackTrace();

			log.html( Tools.errorMessage( "error while creating the 'export.csv' file : " + e.getMessage() + "<br/>Detailed exception trace should appear in the console." ) );
		}
	}
}

class ExportVisitor
{
	private PomGraphReadTransaction tx;
	private PrintWriter w;

	public ExportVisitor( Session session ) throws FileNotFoundException, UnsupportedEncodingException
	{
		tx = session.graph().read();

		File file = new File( "export.csv" );
		w = new PrintWriter( file, "UTF-8" );

		w.println( "from;relation_type;to" );
	}

	public void close()
	{
		w.close();
	}

	public void visit( Relation relation )
	{
		w.println( tx.sourceOf( relation ) + ";" + relation.getRelationType() + ";" + tx.targetOf( relation ) );
	}
}