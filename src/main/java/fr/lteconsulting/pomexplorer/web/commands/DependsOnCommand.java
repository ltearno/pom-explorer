package fr.lteconsulting.pomexplorer.web.commands;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Dep;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class DependsOnCommand extends BaseCommand
{
	public DependsOnCommand()
	{
		super( "dependOn" );
	}

	@Override
	public String execute( Client client, String[] params )
	{
		WorkingSession session = client.getCurrentSession();
		if( session == null )
			return "No working session associated, please create one.";
		
		if( params==null || params.length<1)
			return "specify the GAV with the group:artifact:version format please";
		
		String[] parts = params[0].split( ":" );
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
