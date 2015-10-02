package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ApplicationSettings
{
	private static final String CONFIG_FILE_PATH = "config.properties";

	private static final String CONFIG_DEFAULT_MAVEN_SETTINGS_FILE_KEY = "defaultMavenSettingsFile";

	private final Properties properties = new Properties();

	public void load()
	{
		File configFile = new File( CONFIG_FILE_PATH );
		if( !configFile.exists() )
			return;

		try
		{
			properties.load( new FileReader( configFile ) );
		}
		catch( IOException e )
		{
			System.out.println( "No configuration file found, skipping. You can create the '" + configFile.getAbsolutePath() + "' file if you need." );
		}
	}

	public String getDefaultMavenSettingsFile()
	{
		return properties.getProperty( CONFIG_DEFAULT_MAVEN_SETTINGS_FILE_KEY, null );
	}
}
