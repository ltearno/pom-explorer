package fr.lteconsulting.pomexplorer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ProjectsWatcher
{
	private final Map<Project, ProjectWatcher> watchers = new HashMap<>();

	public void watchProject( Project project ) throws IOException
	{
		if( watchers.containsKey( project ) )
			return;

		ProjectWatcher watcher = new ProjectWatcher( Paths.get( project.getPath() ) );
		watchers.put( project, watcher );

		watcher.register();
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
}
