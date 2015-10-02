package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;

public class GavTools
{
	public static List<String> analyseProvidedClasses( WorkingSession session, GAV gav, StringBuilder log )
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
			return null;
		}

		log.append( "resolved file : " + resolvedFile.getAbsolutePath() + "<br/>" );

		try
		{
			List<String> classNames = new ArrayList<String>();
			ZipInputStream zip = new ZipInputStream( new FileInputStream( resolvedFile ) );
			for( ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry() )
			{
				System.out.println( entry.getName() );
				if( !entry.isDirectory() && entry.getName().endsWith( ".class" ) )
				{
					String className = entry.getName().replace( '/', '.' );
					classNames.add( className.substring( 0, className.length() - ".class".length() ) );
				}
			}
			zip.close();

			Collections.sort( classNames );

			return classNames;
		}
		catch( Exception e )
		{
			log.append( Tools.errorMessage( "error during file inspection ! " + e.getMessage() ) );
			return null;
		}
	}
}
