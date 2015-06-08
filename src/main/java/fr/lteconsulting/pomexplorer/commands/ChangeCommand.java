package fr.lteconsulting.pomexplorer.commands;

import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.changes.Change;
import fr.lteconsulting.pomexplorer.changes.GavChange;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;
import fr.lteconsulting.pomexplorer.depanalyze.Location;

public class ChangeCommand
{
	@Help( "Changes the GAV version and also in dependent projects. Parameters : gav, newVersion" )
	public static String gav( CommandOptions options, Client client, WorkingSession session, String originalGavString, String newGavString )
	{
		GAV originalGav = Tools.string2Gav( originalGavString );
		GAV newGav = Tools.string2Gav( newGavString );

		if( originalGav == null || newGav == null )
			return "specify the GAV with the group:artifact:version format please";

		StringBuilder log = new StringBuilder();

		log.append( "<b>Changing</b> " + originalGav + " to " + newGav + "<br/><br/>" );

		Set<Change<? extends Location>> changes = new HashSet<>();

		GavLocation loc = GavLocation.createProjectGavLocation(session, originalGav, log);
		changes.add(new GavChange(loc, new GAV(loc.getGav().getGroupId(), loc.getGav().getArtifactId(), newGav
				.getVersion())));
		log.append(Tools.changeGav(client, session, originalGav, newGav, changes, new HashSet<GAV>()));
		Tools.printChangeList( log, changes );

		CommandTools.maybeApplyChanges(session, options, log, changes);

		return log.toString();
	}
}
