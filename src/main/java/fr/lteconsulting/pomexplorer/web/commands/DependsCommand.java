package fr.lteconsulting.pomexplorer.web.commands;

import fr.lteconsulting.pomexplorer.Dep;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class DependsCommand
{
	public String on( WorkingSession session, String gavString )
	{
		String[] parts = gavString.split( ":" );
		if(parts.length!=3)
			return "specify the GAV with the group:artifact:version format please";
		
		GAV gav = new GAV( parts[0], parts[1], parts[2] );
		
		StringBuilder res = new StringBuilder();
		
		res.append( "Directly dependent GAVs:<br/>" );

		for( Dep dep : session.getGraph().incomingEdgesOf( gav ) )
		{
			GAV dependency = session.getGraph().getEdgeSource( dep );
			
			res.append( dependency );
			
			Project project = session.getProjects().get( dependency );
			if( project != null )
				res.append( " @ " + project.getPomFile().getAbsolutePath() );
			
			res.append( "<br/>" );
		}
		
		return res.toString();
	}
}
