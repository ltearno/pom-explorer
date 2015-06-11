package fr.lteconsulting.pomexplorer;

import java.util.HashMap;
import java.util.Map;

import fr.lteconsulting.pomexplorer.graph.PomGraph;

public class WorkingSession
{
	private String mavenSettingsFilePath = null;

	private Map<GAV, Project> projects = new HashMap<>();

	private PomGraph graph = new PomGraph();

	public void configure( ApplicationSettings settings )
	{
		this.mavenSettingsFilePath = settings.getDefaultMavenSettingsFile();
	}

	public PomGraph graph()
	{
		return graph;
	}

	public boolean hasProject( GAV gav )
	{
		return projects.containsKey( gav );
	}

	public void registerProject( Project project )
	{
		projects.put( project.getGav(), project );
	}

	public Map<GAV, Project> projects()
	{
		return projects;
	}

	public String getMavenSettingsFilePath()
	{
		return mavenSettingsFilePath;
	}

	public void setMavenSettingsFilePath( String mavenSettingsFilePath )
	{
		this.mavenSettingsFilePath = mavenSettingsFilePath;
	}

	public String getDescription()
	{
		return "<div><b>WorkingSession " + System.identityHashCode( this ) + "</b><br/>" + "Maven configuration file : " + (mavenSettingsFilePath != null ? mavenSettingsFilePath : "(system default)") + "<br/>" + projects.size() + " projects<br/>" + graph.gavs().size()
				+ " GAVs<br/>" + graph.relations().size() + " relations<br/></div>";
	}
}
