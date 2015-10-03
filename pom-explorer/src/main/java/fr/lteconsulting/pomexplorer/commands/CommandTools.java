package fr.lteconsulting.pomexplorer.commands;

import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.changes.ChangeSetManager;
import fr.lteconsulting.pomexplorer.changes.Changer;

public class CommandTools
{
	public static void maybeApplyChanges( WorkingSession session, CommandOptions options, ILogger log, ChangeSetManager changes )
	{
		if( Boolean.TRUE.equals( options.getOption( "apply" ) ) )
		{
			log.html( "<br/><b>Applying changes...</b><br/><br/>" );

			Changer changer = new Changer();
			changer.doChanges( session, changes, log );
		}
		else
		{
			log.html( "<br/><i>You can apply changes with the '<b>-apply</b>' flag in the command.</i><br/><br/>" );
		}
	}
}
