package fr.lteconsulting.pomexplorer;

/**
 * POM Explorer main class
 */
public class PomExporerApp
{
	public static void main( String[] args )
	{
		System.out.println( "" );
		System.out.println( "" );
		System.out.println( "Welcome to POM Explorer (beta) !" );
		System.out.println( "================================" );
		System.out.println( "visit http://localhost:90 with a browser to access the application." );
		System.out.println( "" );
		System.out.println( "" );

		AppFactory.get().webServer().start();
	}
}
