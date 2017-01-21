package fr.lteconsulting.pomexplorer;

import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.Session.XSession;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange;
import fr.lteconsulting.pomexplorer.change.project.ProjectChange;
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
public class ApplicationSession
{
	private final Session session = new Session();

	// To Move to Application
	private final GitRepositories gitRepositories = new GitRepositories();
	private final Set<Project> maintainedProjects = new HashSet<>();
	private final Set<Client> clients = new HashSet<>();
	private final ProjectsWatcher projectsWatcher = new ProjectsWatcherAutoThreaded();
	private final BuilderAutoThreaded builder = new BuilderAutoThreaded();

	public ApplicationSession()
	{
		builder.setSession( this );

		session.setCallback( new XSession()
		{
			@Override
			public void projectAdded( Project project )
			{
				repositories().add( project );
			}
		} );
	}

	public Session session()
	{
		return session;
	}

	public void configure( ApplicationSettings settings )
	{
		session.setMavenSettingsFilePath( settings.getDefaultMavenSettingsFile() );
	}

	public MavenResolver mavenResolver()
	{
		return session.mavenResolver();
	}

	public PomGraph graph()
	{
		return session.graph();
	}

	public ProjectRepository projects()
	{
		return session.projects();
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
		return session.projectChanges();
	}

	public Set<GraphChange> graphChanges()
	{
		return session.graphChanges();
	}

	public void cleanBuildList()
	{
		builder.clearJobs();
	}

	public String getMavenSettingsFilePath()
	{
		return session.getMavenSettingsFilePath();
	}

	public void setMavenSettingsFilePath( String mavenSettingsFilePath )
	{
		session.setMavenSettingsFilePath( mavenSettingsFilePath );
	}

	public String getMavenShellCommand()
	{
		return session.getMavenShellCommand();
	}

	public void setMavenShellCommand( String mavenShellCommand )
	{
		session.setMavenShellCommand( mavenShellCommand );
	}
	
	public void setIgnoredDirs(Set<String> ignoredDirs)
    {
        session.setIgnoredDirs(ignoredDirs);
    }

	public String getDescription()
	{
		return session.getDescription();
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
