package fr.lteconsulting.pomexplorer.commands;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class ClassesCommand
{
	@Help( "Gives the java classes provided by the session's gavs" )
	public String main( WorkingSession session, Client client )
	{
		return by( session, client, null );
	}

	@Help( "Gives the java classes provided by the session's gavs, filtered by the given parameter" )
	public String by( WorkingSession session, Client client, String gavFilter )
	{
		if( gavFilter != null )
			gavFilter = gavFilter.toLowerCase();

		ArrayList<GAV> gavs = new ArrayList<>( session.graph().gavs() );
		Collections.sort( gavs, Tools.gavAlphabeticalComparator );

		StringBuilder log = new StringBuilder();

		log.append( "<br/>GAV list " + (gavFilter != null ? ("filtering with '" + gavFilter + "'") : "") + ":<br/>" );
		for( GAV gav : gavs )
		{
			if( gavFilter != null && !gav.toString().toLowerCase().contains( gavFilter ) )
				continue;

			analyseProvidedClasses( session, gav, log );
		}

		return log.toString();
	}

	private void analyseProvidedClasses( WorkingSession session, GAV gav, StringBuilder log )
	{
		log.append( "<br/><b>Java classes provided by gav " + gav + "</b> :<br/>" );

		String mavenSettingsFilePath = session.getMavenSettingsFilePath();

		MavenResolverSystem resolver;
		if( mavenSettingsFilePath != null && !mavenSettingsFilePath.isEmpty() )
			resolver = Maven.configureResolver().fromFile( mavenSettingsFilePath );
		else
			resolver = Maven.resolver();

		File resolvedFile = null;
		try
		{
			resolvedFile = resolver.resolve( gav.toString() ).withoutTransitivity().asSingleFile();
		}
		catch( Exception e )
		{
			log.append( Tools.errorMessage( "shrinkwrap error : " + e.getMessage() ) );
		}

		if( resolvedFile == null )
		{
			log.append( Tools.warningMessage( "cannot resolve the gav " + gav ) );
			return;
		}

		log.append( "resolved file : " + resolvedFile.getAbsolutePath() + "<br/>" );

		try
		{
			List<String> classNames = new ArrayList<String>();
			ZipInputStream zip = new ZipInputStream( new FileInputStream( resolvedFile ) );
			for( ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry() )
			{
				if( !entry.isDirectory() && entry.getName().endsWith( ".class" ) )
				{
					String className = entry.getName().replace( '/', '.' );
					classNames.add( className.substring( 0, className.length() - ".class".length() ) );
				}
			}
			zip.close();

			Collections.sort( classNames );

			for( String className : classNames )
			{
				log.append( className + "<br/>" );
			}
		}
		catch( Exception e )
		{
			log.append( Tools.errorMessage( "error during file inspection ! " + e.getMessage() ) );
		}
	}
}
