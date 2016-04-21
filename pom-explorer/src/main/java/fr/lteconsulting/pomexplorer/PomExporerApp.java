package fr.lteconsulting.pomexplorer;

/**
 * POM Explorer main class
 */
public class PomExporerApp
{
	public static void main( String[] args )
	{
		int webServerPort = 8090;
		if( args != null && args.length > 0 )
		{
			try
			{
				webServerPort = Integer.parseInt( args[0] );
				System.out.println( "Using tcp port " + webServerPort );
			}
			catch( Exception e )
			{
				System.err.println( "Error parsing arguments, usage : java -jar pom-explorer.jar [webServerPort]" );
				return;
			}
		}

		System.out.println( "" );
		System.out.println( "" );
		System.out.println( "Welcome to POM Explorer (beta) !" );
		System.out.println( "================================" );
		System.out.println( "visit http://localhost:" + webServerPort + " with a browser to access the application." );
		System.out.println( "" );
		System.out.println( "" );

		AppFactory.get().webServer().start( webServerPort );
	}
}
