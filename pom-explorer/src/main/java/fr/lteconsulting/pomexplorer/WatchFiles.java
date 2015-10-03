package fr.lteconsulting.pomexplorer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.WatchService;

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
			ProjectWatcher watcher = new ProjectWatcher( Paths.get( "c:\\tmp\\titi" ) );
			watcher.register();

			while( watcher.waitChange() )
			{
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}
