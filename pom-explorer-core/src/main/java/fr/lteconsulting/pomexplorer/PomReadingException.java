package fr.lteconsulting.pomexplorer;

import java.io.File;

public class PomReadingException extends RuntimeException
{
	private final File pomFile;

	PomReadingException(File pomFile, Throwable cause)
	{
		super("Could not load pom file: " + pomFile.getAbsolutePath(), cause);
		this.pomFile = pomFile;
	}

	public File getPomFile()
	{
		return pomFile;
	}
}
