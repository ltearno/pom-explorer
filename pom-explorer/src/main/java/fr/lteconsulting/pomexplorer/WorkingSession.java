package fr.lteconsulting.pomexplorer;

import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.graph.PomGraph;
import fr.lteconsulting.pomexplorer.graph.ProjectRepository;

/**
 * Some projects can be pinned as needed to be always up to date
 * 
 * <p>
 * watch all recursive dependencies with local project.
 * <p>
 * for each one, build the project if a change is detected
 * <p>
 * process changes with a customizable delay, so builds are made in the best possible order
 * <p>
 * builds should be cancellable (kill the build process simply)
 * 
 * @author Arnaud
 *
 */
public class WorkingSession
{
	private String mavenSettingsFilePath = null;

	private final GitRepositories gitRepositories = new GitRepositories();

	private final ProjectRepository projects = new ProjectRepository();

	private final PomGraph graph = new PomGraph();

	private final Set<Project> maintainedProjects = new HashSet<>();

	private final Set<Client> clients = new HashSet<>();

	private final ProjectsWatcher projectsWatcher = new ProjectsWatcher();

	private final BuilderSuperman builder = new BuilderSuperman();

	public WorkingSession()
	{
		builder.setSession(this);
		builder.runAsync();
	}

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

	public GitRepositories repositories()
	{
		return gitRepositories;
	}

	public Set<Project> maintainedProjects()
	{
		return maintainedProjects;
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

	public void addClient(Client client)
	{
		clients.add(client);
	}

	public void removeClient(Client client)
	{
		clients.remove(client);
	}

	public Set<Client> getClients()
	{
		return clients;
	}

	public ProjectsWatcher projectsWatcher()
	{
		return projectsWatcher;
	}
}
