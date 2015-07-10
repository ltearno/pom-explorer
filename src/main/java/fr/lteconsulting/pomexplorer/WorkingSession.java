package fr.lteconsulting.pomexplorer;

import fr.lteconsulting.pomexplorer.graph.PomGraph;
import fr.lteconsulting.pomexplorer.graph.ProjectRepository;

public class WorkingSession
{
	private String mavenSettingsFilePath = null;

	private ProjectRepository projects = new ProjectRepository();

	private PomGraph graph = new PomGraph();

	public void configure( ApplicationSettings settings )
	{
		this.mavenSettingsFilePath = settings.getDefaultMavenSettingsFile();
	}

	public PomGraph graph()
	{
		return graph;
	}

	public ProjectRepository projects()
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
