package fr.lteconsulting.pomexplorer.commands;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.PomSection;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.changes.ChangeSetManager;
import fr.lteconsulting.pomexplorer.changes.GavChange;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;

public class ChangeCommand
{
	@Help( "changes the GAV version and also in dependent projects. Parameters : gav, newVersion" )
	public static String gav( CommandOptions options, Client client, WorkingSession session, String originalGavString, String newGavString )
	{
		GAV originalGav = Tools.string2Gav( originalGavString );
		GAV newGav = Tools.string2Gav( newGavString );

		if( originalGav == null || newGav == null )
			return "specify the GAV with the group:artifact:version format please";

		StringBuilder log = new StringBuilder();

		log.append( "<b>Changing</b> " + originalGav + " to " + newGav + "<br/><br/>" );

		ChangeSetManager changes = new ChangeSetManager();

		GavLocation loc = new GavLocation( session.projects().forGav( originalGav ), PomSection.PROJECT, originalGav );
		changes.addChange( new GavChange( loc, newGav ), "changing " + originalGav + " to " + newGav );

		changes.resolveChanges( session, log );

		Tools.printChangeList( log, changes );

		CommandTools.maybeApplyChanges( session, options, log, changes );

		return log.toString();
	}
}
