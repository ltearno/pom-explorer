package fr.lteconsulting.pomexplorer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.lteconsulting.pomexplorer.change.graph.GraphChange;
import fr.lteconsulting.pomexplorer.change.project.ProjectChange;
import fr.lteconsulting.pomexplorer.graph.PomGraph;
import fr.lteconsulting.pomexplorer.graph.ProjectRepository;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;

/**
 * A contextual session for building a POM graph and project store
 */
public class Session
{
	/**
	 * Session callback interface
	 */
	public interface XSession
	{
		void projectAdded( Project project );
	}

	private String mavenSettingsFilePath = null;
	private String mavenShellCommand = "C:\\Program Files (x86)\\apache-maven-3.1.1\\bin\\mvn.bat";
	private final PomGraph graph = new PomGraph();
	private final ProjectRepository projects = new ProjectRepository( this );
	private final Map<String, MavenResolver> resolvers = new HashMap<>();
	private final Set<ProjectChange> projectChanges = new HashSet<>();
	private final Set<GraphChange> graphChanges = new HashSet<>();
	private XSession xSession = null;
	private Set<String> ignoredDirs = new HashSet<>();

	public XSession setCallback( XSession callback )
	{
		XSession old = xSession;
		this.xSession = callback;
		return old;
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

	public Set<ProjectChange> projectChanges()
	{
		return projectChanges;
	}

	public Set<GraphChange> graphChanges()
	{
		return graphChanges;
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

	public Set<String> getIgnoredDirs() {
        return ignoredDirs;
    }

    public void setIgnoredDirs(Set<String> ignoredDirs) {
        this.ignoredDirs = ignoredDirs;
    }

    public String getDescription()
	{
		PomGraphReadTransaction tx = graph.read();
		return "<div><b>WorkingSession " + System.identityHashCode( this ) + "</b><br/>" + "Maven configuration file : "
				+ (mavenSettingsFilePath != null ? mavenSettingsFilePath : "(system default)") + "<br/>" + "Maven shell command : "
				+ (mavenShellCommand != null ? mavenShellCommand : "(null)") + "<br/>" + projects.size() + " projects<br/>" + tx.gavs().size() + " GAVs<br/>"
				+ tx.relations().size() + " relations<br/>"
				+ "custom ignored directories: " + ignoredDirs + "<br/>"
				+ "</div>";
	}

	public void sendEventAddedProject( Project project )
	{
		if( xSession != null )
			xSession.projectAdded( project );
	}
   
}
