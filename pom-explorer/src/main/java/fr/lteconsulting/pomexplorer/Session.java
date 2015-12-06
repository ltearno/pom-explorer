package fr.lteconsulting.pomexplorer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.lteconsulting.pomexplorer.change.graph.GraphChange;
import fr.lteconsulting.pomexplorer.change.project.ProjectChange;
import fr.lteconsulting.pomexplorer.graph.PomGraph;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
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
public class Session
{
	private String mavenSettingsFilePath = null;

	private String mavenShellCommand = "C:\\Program Files (x86)\\apache-maven-3.1.1\\bin\\mvn.bat";

	private final GitRepositories gitRepositories = new GitRepositories();

	private final ProjectRepository projects = new ProjectRepository();

	private final PomGraph graph = new PomGraph();

	private final Set<Project> maintainedProjects = new HashSet<>();

	private final Set<Client> clients = new HashSet<>();

	private final ProjectsWatcher projectsWatcher = new ProjectsWatcherAutoThreaded();

	private final BuilderAutoThreaded builder = new BuilderAutoThreaded();

	private final Map<String, MavenResolver> resolvers = new HashMap<>();

	private final Set<ProjectChange> projectChanges = new HashSet<>();

	private final Set<GraphChange> graphChanges = new HashSet<>();

	public Session()
	{
		builder.setSession( this );
	}

	public void configure( ApplicationSettings settings )
	{
		this.mavenSettingsFilePath = settings.getDefaultMavenSettingsFile();
	}

	public MavenResolver mavenResolver()
	{
		String mavenSettingsFilePath = getMavenSettingsFilePath();
		MavenResolver resolver = resolvers.get( mavenSettingsFilePath == null ? "-" : mavenSettingsFilePath );
		if( resolver == null )
		{
			resolver = new MavenResolver();
			resolver.init( mavenSettingsFilePath );
			resolvers.put( mavenSettingsFilePath == null ? "-" : mavenSettingsFilePath, resolver );
		}

		return resolver;
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

	public Builder builder()
	{
		return builder;
	}

	public Set<ProjectChange> projectChanges()
	{
		return projectChanges;
	}

	public Set<GraphChange> graphChanges()
	{
		return graphChanges;
	}

	public void cleanBuildList()
	{
		builder.clearJobs();
	}

	public String getMavenSettingsFilePath()
	{
		return mavenSettingsFilePath;
	}

	public void setMavenSettingsFilePath( String mavenSettingsFilePath )
	{
		this.mavenSettingsFilePath = mavenSettingsFilePath;
	}

	public String getMavenShellCommand()
	{
		return mavenShellCommand;
	}

	public void setMavenShellCommand( String mavenShellCommand )
	{
		this.mavenShellCommand = mavenShellCommand;
	}

	public String getDescription()
	{
		PomGraphReadTransaction tx = graph.read();
		return "<div><b>WorkingSession " + System.identityHashCode( this ) + "</b><br/>" + "Maven configuration file : "
				+ (mavenSettingsFilePath != null ? mavenSettingsFilePath : "(system default)") + "<br/>" + "Maven shell command : "
				+ (mavenShellCommand != null ? mavenShellCommand : "(null)") + "<br/>" + projects.size() + " projects<br/>" + tx.gavs().size() + " GAVs<br/>"
				+ tx.relations().size() + " relations<br/></div>";
	}

	public void addClient( Client client )
	{
		clients.add( client );
	}

	public void removeClient( Client client )
	{
		clients.remove( client );
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
