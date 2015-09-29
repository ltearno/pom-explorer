package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
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

public class WatchFiles
{
	static WatchService watchService;

	public static void main( String[] args )
	{
		try
		{
			watchService = FileSystems.getDefault().newWatchService();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		
		try
		{
			ProjectWatcher watcher = new ProjectWatcher( Paths.get( "c:\\tmp" ), watchService );
			watcher.register();
			
			while( watcher.waitChange() )
			{
				System.out.println("Watcher signals one or more events !");
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

//		Path path = Paths.get( "c:\\tmp" );
//		watchChanges( path );
	}

	private static void watchChanges( Path path )
	{
		List<Path> directories = new ArrayList<>();
		List<Path> files = new ArrayList<>();
		getSubPaths( path.toFile(), directories, files );

		Set<WatchKey> keys = new HashSet<>();

		try
		{
			for( Path directory : directories )
			{
				System.out.println( "Watch directory " + directory.toString() );
				WatchKey key = path.register( watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY );
				keys.add( key );
			}

			for( Path file : files )
			{
				System.out.println( "Watch file " + file.toString() );
				WatchKey key = path.register( watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY );
				keys.add( key );
			}

			WatchKey key = null;
			while( (key = watchService.take()) != null )
			{
				key.reset();
				
				for( WatchEvent<?> event : key.pollEvents() )
				{
					System.out.println("Happenened event " + event.kind() + " on item " + event.context() );
				}
			}
		}
		catch( IOException | InterruptedException e )
		{
			e.printStackTrace();
		}
	}

	private static void getSubPaths( File file, List<Path> directories, List<Path> files )
	{
		if( file == null )
			return;

		if( file.isDirectory() )
		{
			directories.add( file.toPath() );

			for( File child : file.listFiles() )
				getSubPaths( child, directories, files );
		}
		else
		{
			files.add( file.toPath() );
		}
	}
}
