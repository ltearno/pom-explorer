package fr.lteconsulting.pomexplorer.commands;

import java.util.List;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.GavTools;
import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.javac.JavaSourceAnalyzer;
import fr.lteconsulting.pomexplorer.model.Gav;

public class ClassesCommand
{
	@Help( "gives the java classes provided by the session's gavs" )
	public void main( Session session, Log log, Client client )
	{
		providedBy( session, log, client, null );
	}

	@Help( "gives the java classes provided by the session's gavs, filtered by the given parameter" )
	public void providedBy( Session session, Log log, Client client, FilteredGAVs gavFilter )
	{
		if( gavFilter == null )
		{
			log.html( Tools.warningMessage( "You should specify a GAV filter" ) );
			return;
		}

		log.html( "<br/>GAV list filtered with '" + gavFilter + "' :<br/>" );

		for( Gav gav : gavFilter.getGavs( session ) )
		{
			List<String> classes = GavTools.analyseProvidedClasses( session, gav, log );
			if( classes == null )
			{
				log.html( Tools.warningMessage( "No class provided by gav " + gav ) );
				continue;
			}

			for( String className : classes )
				log.html( className + "<br/>" );
		}
	}

	/*
	 * parse all the Java source files in the gav's project directory and
	 * extract all referenced fqns.
	 * 
	 * substract the gav's provided classes from this set, to get external
	 * references
	 */
	@Help( "gives the fqn list of referenced classes by the session's gavs, filtered by the given parameter" )
	public void referencedBy( Session session, Log log, FilteredGAVs gavFilter )
	{
		JavaSourceAnalyzer analyzer = new JavaSourceAnalyzer();

		for( Gav gav : gavFilter.getGavs( session ) )
		{
			Project project = session.projects().forGav( gav );
			if( project == null )
				continue;

			analyzer.analyzeProject( project, true, log );
		}
	}
}
