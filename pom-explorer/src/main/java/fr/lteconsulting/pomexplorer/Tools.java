package fr.lteconsulting.pomexplorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import fr.lteconsulting.pomexplorer.model.Gav;

public class Tools
{
	public static int compareStrings( String a, String b )
	{
		if( a == b )
			return 0;
		if( a == null )
			return -1;
		if( b == null )
			return 1;

		return a.compareTo( b );
	}

	/***
	 * Maven tools
	 */

	public static boolean isNonResolvedValue( String text )
	{
		return text != null && text.contains( "${" );// && text.contains( "}" ));
	}

	public static boolean isMavenVariable( String text )
	{
		return text != null && (text.startsWith( "${" ) && text.endsWith( "}" ));
	}

	public static String getPropertyNameFromPropertyReference( String name )
	{
		if( !(name.startsWith( "${" ) && name.endsWith( "}" )) )
			return name;

		return name.substring( 2, name.length() - 1 );
	}

	/**
	 * Collection utilities
	 */

	public static String logMessage( String message )
	{
		return "<span style=''>" + message + "</span><br/>";
	}

	public static String warningMessage( String message )
	{
		return "<span style='color:orange;'>" + message + "</span><br/>";
	}

	public static String successMessage( String message )
	{
		return "<span style='color:green;'>" + message + "</span><br/>";
	}

	public static String buildMessage( String message )
	{
		return "<span style='color:grey;font-size:90%;'>" + message + "</span><br/>";
	}

	public static String errorMessage( String message )
	{
		return "<span style='color:red;'>" + message + "</span><br/>";
	}

	public static void logStacktrace( Exception e, Log log )
	{
		Throwable t = e;
		if( t instanceof InvocationTargetException )
			t = ((InvocationTargetException) t).getTargetException();

		StringBuilder sb = new StringBuilder();

		sb.append( t.toString() + "<br/>" );

		for( StackTraceElement st : t.getStackTrace() )
			sb.append( st.toString() + "<br/>" );

		log.html( sb.toString() );
	}

	/**
	 * 
	 */

	private final static String SNAPSHOT_SUFFIX = "-SNAPSHOT";

	public static boolean isReleased( Gav gav )
	{
		return !gav.getVersion().endsWith( SNAPSHOT_SUFFIX );
	}

	public static Gav releasedGav( Gav gav )
	{
		if( !isReleased( gav ) )
			return new Gav( gav.getGroupId(), gav.getArtifactId(), gav.getVersion().substring( 0, gav.getVersion().length() - SNAPSHOT_SUFFIX.length() ) );

		return gav;
	}

	public static Gav openGavVersion( Gav gav )
	{
		if( !isReleased( gav ) )
			return gav;

		String version = gav.getVersion();

		int major = 0;
		int minor = 0;
		int patch = 0;

		String[] parts = version.split( "\\." );
		if( parts.length > 0 )
		{
			try
			{
				major = Integer.parseInt( parts[0] );
			}
			catch( Exception e )
			{
			}
		}
		if( parts.length > 1 )
		{
			try
			{
				minor = Integer.parseInt( parts[1] );
			}
			catch( Exception e )
			{
			}
		}
		if( parts.length > 2 )
		{
			try
			{
				patch = Integer.parseInt( parts[2] );
			}
			catch( Exception e )
			{
			}
		}

		// new version, hard coded major version upgrade !
		major++;

		if( parts.length == 3 )
			version = String.format( "%1d.%1d.%1d", major, minor, patch );
		else if( parts.length == 2 )
			version = String.format( "%1d.%1d", major, minor );
		else if( parts.length == 1 )
			version = String.format( "%1d", major );
		else
			version += "-open";

		return gav.copyWithVersion( version + SNAPSHOT_SUFFIX );
	}

	/**
	 * Reads a whole file into a String assuming the file is UTF-8 encoded
	 */
	public static String readFile( File file )
	{
		try( Scanner scanner = new Scanner( file, "UTF-8" ) )
		{
			return scanner.useDelimiter( "\\A" ).next();
		}
		catch( FileNotFoundException e )
		{
			return null;
		}
	}

	public static List<String> readFileLines( String path )
	{
		ArrayList<String> res = new ArrayList<String>();

		File file = new File( path );
		if( !file.exists() )
			return res;

		try
		{
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( file ), "UTF8" ) );

			String str;

			while( (str = in.readLine()) != null )
			{
				res.add( str );
			}

			in.close();
		}
		catch( Exception e )
		{
		}

		return res;
	}
}
