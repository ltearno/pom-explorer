package fr.lteconsulting.pomexplorer.web.commands;

import fr.lteconsulting.pomexplorer.PomAnalyzer;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class AnalyzeCommand
{
	public String directory( WorkingSession session, String directory )
	{
		String res = "Analyzing folder " + directory + "<br/>";
		
		PomAnalyzer analyzer = new PomAnalyzer();
		analyzer.analyze( directory, session );
		
		return res;
	}
}
