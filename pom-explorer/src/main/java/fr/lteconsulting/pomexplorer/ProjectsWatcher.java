package fr.lteconsulting.pomexplorer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fr.lteconsulting.autothreaded.AutoThreaded;

@AutoThreaded
public class ProjectsWatcher
{
	private final Map<Project, ProjectWatcher> watchers = new HashMap<>();

	public void watchProject( Project project, Log log )
	{
		if( !project.isBuildable() )
		{
			log.html( Tools.errorMessage( "cannot watch the non-buildable project " + project ) );
			return;
		}

		if( watchers.containsKey( project ) )
			return;

		ProjectWatcher watcher = new ProjectWatcher( Paths.get( project.getPomFile().getParentFile().getAbsolutePath() ) );
		watchers.put( project, watcher );

		try
		{
			watcher.register();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	public Project hasChanged()
	{
		for( Entry<Project, ProjectWatcher> e : watchers.entrySet() )
		{
			if( e.getValue().hasChanges() )
				return e.getKey();
		}

		return null;
	}

	public Set<Project> watchedProjects()
	{
		Set<Project> res = new HashSet<>();
		watchers.entrySet().stream().forEach( e -> res.add( e.getKey() ) );
		return res;
	}
}
