package fr.lteconsulting.pomexplorer.commands;

import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.changes.Change;
import fr.lteconsulting.pomexplorer.depanalyze.Location;

public class ChangeCommand
{
	@Help( "Changes the GAV version and also in dependent projects. Parameters : gav, newVersion" )
	public static String gav( Client client, WorkingSession session, String originalGavString, String newGavString )
	{
		GAV originalGav = Tools.string2Gav( originalGavString );
		GAV newGav = Tools.string2Gav( newGavString );

		if( originalGav == null || newGav == null )
			return "specify the GAV with the group:artifact:version format please";

		StringBuilder res = new StringBuilder();
		
		Set<Change<? extends Location>> changes = new HashSet<>();
		
		Tools.changeGav( client, session, originalGav, newGav, changes );
		Tools.printChangeList( res, changes );
		
		return res.toString();
	}
}
