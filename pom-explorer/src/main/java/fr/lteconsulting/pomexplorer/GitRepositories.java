package fr.lteconsulting.pomexplorer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GitRepositories
{
	private final Map<Path, GitRepository> repositories = new HashMap<>();

	public void add( Project project )
	{
		String path = GitTools.findGitRoot( project.getPomFile().getParent() );
		if( path == null )
			return;
		Path p = Paths.get( path );

		GitRepository repo = repositories.get( p );
		if( repo == null )
		{
			repo = new GitRepository( p );
			repositories.put( p, new GitRepository( p ) );
		}

		repo.addProject( project );
	}

	public Collection<GitRepository> values()
	{
		return repositories.values();
	}
}