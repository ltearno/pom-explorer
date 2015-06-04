package fr.lteconsulting.pomexplorer.commands;

import java.util.Set;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.depanalyze.Location;

public class DependsCommand
{
	public String test( WorkingSession session )
	{
		return on( session, "fr.lteconsulting:hexa.binding:1.2-SNAPSHOT" );
		// return on( session, "fr.lteconsulting:hexa.css:1.2-SNAPSHOT" );
		// return on( session, "fr.lteconsulting:hexa.utils:1.2-SNAPSHOT" );
	}

	@Help( "lists the GAVs directly depending on the one given in parameter" )
	public String on( WorkingSession session, String gavString )
	{
		GAV gav = Tools.string2Gav( gavString );
		if( gav == null )
			return "specify the GAV with the group:artifact:version format please";

		StringBuilder res = new StringBuilder();

		res.append( "<br/>" + gav + " dependencies<br/>" );
		res.append( "<br/>List of dependent GAVs ([D]=direct dependency, [H]=parent's dependency, [T]=transitive dependency, [?]/[!]=error)<br/>" );

		Set<Location> locations = Tools.getImpactedLocationsToChangeGav( session, gav, res, true );

		res.append( "<br/>Dependency locations:<br/>" );

		for( Location l : locations )
			res.append( l.getProject().getPomFile().getAbsolutePath() + " : " + l.toString() + "<br/>" );

		return res.toString();
	}
	
	@Help( "lists the GAVs that the GAV passed in parameters depends on" )
	public String by( WorkingSession session, String gavString )
	{
		GAV gav = Tools.string2Gav( gavString );
		if( gav == null )
			return "specify the GAV with the group:artifact:version format please";

		StringBuilder res = new StringBuilder();
		
		return res.toString();
	}
}
