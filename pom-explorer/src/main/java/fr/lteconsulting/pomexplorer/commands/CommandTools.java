package fr.lteconsulting.pomexplorer.commands;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.oldchanges.ChangeSetManager;
import fr.lteconsulting.pomexplorer.oldchanges.Changer;

public class CommandTools
{
	public static void maybeApplyChanges( Session session, CommandOptions options, Log log, ChangeSetManager changes )
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
