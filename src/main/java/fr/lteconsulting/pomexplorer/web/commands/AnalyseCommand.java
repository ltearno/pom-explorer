package fr.lteconsulting.pomexplorer.web.commands;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.PomAnalyzer;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class AnalyseCommand extends BaseCommand
{
	public AnalyseCommand()
	{
		super("analyze", "analyse");
	}
	
	@Override
	public String execute( Client client, String[] params )
	{
		WorkingSession session = client.getCurrentSession();
		if(session == null)
			return "No working session associated, please create one.";
		
		if(params==null ||params.length<1)
			return "Please, specify the directory to analyse.";
		
		String directory = params[0];
		
		String res = "Analyzing folder " + directory + "<br/>";
		
		PomAnalyzer analyzer = new PomAnalyzer();
		analyzer.analyze( directory, session );
		
		return res;
	}
}
