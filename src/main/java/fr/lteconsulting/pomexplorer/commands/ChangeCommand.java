package fr.lteconsulting.pomexplorer.commands;

import java.util.Set;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.depanalyze.Location;

public class ChangeCommand
{
	@Help( "Changes the GAV version and also in dependent projects. Parameters : gav, newVersion" )
	public String gav( WorkingSession session, String gavString, String newVersion )
	{
		GAV gav = Tools.string2Gav( gavString );
		if( gav == null )
			return "specify a valid GAV";

		StringBuilder res = new StringBuilder();

		Set<Location> locations = Tools.getImpactedLocationsToChangeGav( session, gav, res, false );

		for( Location l : locations )
		{
			res.append( "file: " + l.getProject().getPomFile().getAbsolutePath() + "<br/>" + "location: " + l + "<br/>" + "change to: " + newVersion + "<br>" + "<br/>" );
		}

		return res.toString();
	}
}
