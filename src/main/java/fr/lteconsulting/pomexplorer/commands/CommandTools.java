package fr.lteconsulting.pomexplorer.commands;

import java.util.Set;

import fr.lteconsulting.pomexplorer.changes.Change;
import fr.lteconsulting.pomexplorer.changes.Changer;
import fr.lteconsulting.pomexplorer.depanalyze.Location;

public class CommandTools
{
	public static void maybeApplyChanges( CommandOptions options, StringBuilder res, Set<Change<? extends Location>> changes )
	{
		if( Boolean.TRUE.equals( options.getOption( "apply" ) ) )
		{
			res.append( "<br/><b>Applying changes...</b><br/><br/>" );

			Changer changer = new Changer();
			changer.doChanges( changes, res );
		}
		else
		{
			res.append( "<br/><i>You can apply changes with the '<b>-apply</b>' flag in the command.</i><br/><br/>" );
		}
	}
}
