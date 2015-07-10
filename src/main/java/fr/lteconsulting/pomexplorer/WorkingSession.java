package fr.lteconsulting.pomexplorer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.lteconsulting.pomexplorer.graph.PomGraph;
import fr.lteconsulting.pomexplorer.graph.ProjectRepository;

public class WorkingSession
{
	public class Repositories
	{
		private final Map<Path, GitRepository> repositories = new HashMap<>();

		public void add( Project project )
		{
			String path = GitTools.findGitRoot( project.getPomFile().getParent() );
			if( path == null )
				return;
			Path p = Paths.get( path );

			GitRepository repo = repositories.get( p );
			if( repo == null )
			{
				repo = new GitRepository( p );
				repositories.put( p, new GitRepository( p ) );
			}

			repo.addProject( project );
		}

		public Collection<GitRepository> values()
		{
			return repositories.values();
		}
	}

	private String mavenSettingsFilePath = null;

	private Repositories gitRepositories = new Repositories();

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

	public Repositories repositories()
	{
		return gitRepositories;
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
