package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProjectWatcher
{
	private WatchService service;
	private Path projectPath;

	private final Set<WatchKey> keys = new HashSet<>();
	private final List<Path> paths = new ArrayList<>();

	public ProjectWatcher( Path projectPath, WatchService service )
	{
		this.projectPath = projectPath;
		this.service = service;
	}

	public void register() throws IOException
	{
		getSubPaths( projectPath.toFile(), paths );

		for( Path path : paths )
		{
			watchFile( path );
		}
	}

	private void watchFile( Path path ) throws IOException
	{
		System.out.println( "Watch item " + path.toString() );
		WatchKey key = path.register( service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY );
		keys.add( key );
	}

	private void unwatchFile( Path path, WatchKey key )
	{
		System.out.println( "Unwatch file " + path.toString() );
		key.cancel();
		keys.remove( key );
		paths.remove( path );
	}

	public boolean waitChange() throws InterruptedException, IOException
	{
		WatchKey key = service.take();
		if( key == null )
			return false;

		key.reset();

		for( WatchEvent<?> event : key.pollEvents() )
		{
			System.out.println( "Happenened event " + event.kind() + " on item " + event.context() );

			if( event.kind() == StandardWatchEventKinds.ENTRY_CREATE )
			{
				Path path = Paths.get( projectPath.toString(), event.context().toString() );
				if( path != null && path.toFile().isDirectory() )
					watchFile( path );
			}
			else if( event.kind() == StandardWatchEventKinds.ENTRY_DELETE )
			{
				unwatchFile( Paths.get( projectPath.toString(), event.context().toString() ), key );
			}
		}

		return true;
	}

	private void getSubPaths( File file, List<Path> paths )
	{
		if( file == null )
			return;

		if( file.isDirectory() )
		{
			paths.add( file.toPath() );

			for( File child : file.listFiles() )
				getSubPaths( child, paths );
		}
	}
}
