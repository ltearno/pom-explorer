package fr.lteconsulting.pomexplorer.commands;

import java.util.List;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.GavTools;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.javac.JavaSourceAnalyzer;

public class ClassesCommand
{
	@Help( "gives the java classes provided by the session's gavs" )
	public String main( WorkingSession session, Client client )
	{
		return providedBy( session, client, null );
	}

	@Help( "gives the java classes provided by the session's gavs, filtered by the given parameter" )
	public String providedBy( WorkingSession session, Client client, String gavFilter )
	{
		StringBuilder log = new StringBuilder();

		log.append( "<br/>GAV list " + (gavFilter != null ? ("filtering with '" + gavFilter + "'") : "") + ":<br/>" );

		for( GAV gav : GavTools.filterGavs( session.graph().gavs(), gavFilter ) )
		{
			List<String> classes = GavTools.analyseProvidedClasses( session, gav, log );
			for( String className : classes )
				log.append( className + "<br/>" );
		}

		return log.toString();
	}

	@Help( "gives the fqn list of referenced classes by the session's gavs, filtered by the given parameter" )
	public String referencedBy( WorkingSession session, String gavFilter )
	{
		/*
		 * parse all the Java source files in the gav's project directory and
		 * extract all referenced fqns.
		 * 
		 * substract the gav's provided classes from this set, to get external
		 * references
		 */
		StringBuilder log = new StringBuilder();

		JavaSourceAnalyzer analyzer = new JavaSourceAnalyzer();

		for( GAV gav : GavTools.filterGavs( session.graph().gavs(), gavFilter ) )
		{
			Project project = session.projects().get( gav );
			if( project == null )
				continue;

			analyzer.analyzeProject( project, true, log );
		}

		return log.toString();
	}
}
