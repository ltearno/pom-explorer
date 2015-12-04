package fr.lteconsulting.pomexplorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import fr.lteconsulting.hexa.client.tools.Func1;
import fr.lteconsulting.pomexplorer.changes.Change;
import fr.lteconsulting.pomexplorer.changes.ChangeSetManager;
import fr.lteconsulting.pomexplorer.depanalyze.Location;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.model.Dependency;
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

	public static void printChangeList( ILogger log, ChangeSetManager changes )
	{
		log.html( "<br/>Change list...<br/><br/>" );

		List<Change<? extends Location>> changeList = new ArrayList<>();
		for( Change<? extends Location> c : changes )
			changeList.add( c );

		Collections.sort( changeList, new Comparator<Change<? extends Location>>()
		{
			@Override
			public int compare( Change<? extends Location> o1, Change<? extends Location> o2 )
			{
				Project p1 = o1.getLocation().getProject();
				Project p2 = o2.getLocation().getProject();

				if( p1 == null && p2 == null )
					return 0;
				if( p1 == null )
					return -1;
				if( p2 == null )
					return 1;

				return p1.getPomFile().getAbsolutePath().compareTo( p2.getPomFile().getAbsolutePath() );
			}
		} );

		for( Change<? extends Location> c : changeList )
		{
			log.html( c.toString() );
		}
	}

	/***
	 * Maven tools
	 */

	public static Set<Location> getDirectDependenciesLocations( WorkingSession session, ILogger log, Gav gav )
	{
		PomGraphReadTransaction tx = session.graph().read();
		Set<Location> set = new HashSet<>();

		Set<Relation> relations = tx.relationsReverse( gav );
		for( Relation relation : relations )
		{
			Gav updatedGav = tx.sourceOf( relation );

			Project updatedProject = session.projects().forGav( updatedGav );
			if( updatedProject == null )
			{
				if( log != null )
					log.html( Tools.warningMessage( "Cannot find project for GAV " + updatedGav
							+ " which dependency should be modified ! skipping." ) );
				continue;
			}

			Location dependencyLocation = updatedProject.findDependencyLocation( session, log, relation );
			if( dependencyLocation == null )
			{
				if( log != null )
					log.html( Tools.errorMessage( "Cannot find the location of dependency to " + tx.targetOf( relation ) + " in this project " + updatedProject ) );
				continue;
			}

			set.add( dependencyLocation );
		}

		return set;
	}

	public static boolean isMavenVariable( String text )
	{
		return text != null && text.startsWith( "${" ) && text.endsWith( "}" );
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

	public static <T> List<T> filter( Iterable<T> list, Func1<T, Boolean> predicate )
	{
		List<T> res = new ArrayList<>();
		if( list == null )
			return res;

		for( T t : list )
			if( predicate.exec( t ) )
				res.add( t );
		return res;
	}

	public static <T> List<T> filter( T[] list, Func1<T, Boolean> predicate )
	{
		List<T> res = new ArrayList<>();
		if( list == null )
			return res;

		for( T t : list )
			if( predicate.exec( t ) )
				res.add( t );
		return res;
	}

	public static final Comparator<Gav> gavAlphabeticalComparator = new Comparator<Gav>()
	{
		@Override
		public int compare( Gav o1, Gav o2 )
		{
			int r = o1.getGroupId().compareTo( o2.getGroupId() );
			if( r != 0 )
				return r;

			r = o1.getArtifactId().compareTo( o2.getArtifactId() );
			if( r != 0 )
				return r;

			if( o1.getVersion() == null && o2.getVersion() == null )
				return 0;
			if( o1.getVersion() == null )
				return -1;
			if( o2.getVersion() == null )
				return 1;

			r = o1.getVersion().compareTo( o2.getVersion() );

			return 0;
		}
	};

	public static final Comparator<Project> projectAlphabeticalComparator = ( a, b ) -> a.toString().compareTo( b.toString() );
	
	public static final Comparator<Dependency> dependencyAlphabeticalComparator = ( a, b ) -> a.toString().compareTo( b.toString() );

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

	public static void logStacktrace( Exception e, ILogger log )
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
			return new Gav( gav.getGroupId(), gav.getArtifactId(), gav.getVersion().substring( 0,
					gav.getVersion().length() - SNAPSHOT_SUFFIX.length() ) );

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
		try
		{
			return new Scanner( file, "UTF-8" ).useDelimiter( "\\A" ).next();
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
