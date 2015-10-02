package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GitTools
{
	public static String findGitRoot( String path )
	{
		if( path == null )
			return null;

		Path gitPath = Paths.get( path, ".git" );
		File gitPathFile = gitPath.toFile();
		if( gitPathFile.exists() && gitPathFile.isDirectory() )
			return path;

		Path parentPath = Paths.get( path ).getParent();
		if( parentPath == null )
			return null;

		return findGitRoot( parentPath.toString() );
	}
}
