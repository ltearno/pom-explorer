package fr.lteconsulting.pomexplorer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitRepository
{
	private final Path path;

	private final Set<Project> projects = new HashSet<>();

	private Repository repository;

	public GitRepository( Path path )
	{
		this.path = path;
	}

	public Path getPath()
	{
		return path;
	}

	public void addProject( Project project )
	{
		projects.add( project );
	}

	public Set<Project> getProjects()
	{
		return projects;
	}

	@Override
	public boolean equals( Object obj )
	{
		if( !(obj instanceof GitRepository) )
			return false;
		return path.equals( ((GitRepository) obj).path );
	}

	@Override
	public int hashCode()
	{
		return path.hashCode();
	}

	public void getStatus( Log log, boolean details )
	{
		ensureOpen();

		Status status;
		try( Git git = Git.open( Paths.get( path.toString(), ".git" ).toFile() ) )
		{
			status = git.status().call();
			int nb = status.getAdded().size() + status.getChanged().size() + status.getConflicting().size() + status.getMissing().size() + status.getModified().size()
					+ status.getRemoved().size();

			log.html( (nb > 0 ? "[*] " : "[ ] ") + path.toAbsolutePath().toString() );
			log.html( " " + git.getRepository().getBranch() );
			log.html( (nb > 0 ? (" <b>(" + nb + " changes</b>)") : "") + "<br/>" );

			if( details )
			{
				log.html( "<br/>" );
				if( !status.getAdded().isEmpty() )
					log.html( "Added: " + status.getAdded() + "<br/>" );
				if( !status.getChanged().isEmpty() )
					log.html( "Changed: " + status.getChanged() + "<br/>" );
				if( !status.getConflicting().isEmpty() )
				{
					log.html( "Conflicting: " + status.getConflicting() + "<br/>" );
					log.html( "ConflictingStageState: " + status.getConflictingStageState() + "<br/>" );
				}
				if( !status.getMissing().isEmpty() )
					log.html( "Missing: " + status.getMissing() + "<br/>" );
				if( !status.getModified().isEmpty() )
					log.html( "Modified: " + status.getModified() + "<br/>" );
				if( !status.getRemoved().isEmpty() )
					log.html( "Removed: " + status.getRemoved() + "<br/>" );
				if( !status.getUntracked().isEmpty() )
					log.html( "Untracked: " + status.getUntracked() + "<br/>" );
				if( !status.getUntrackedFolders().isEmpty() )
					log.html( "UntrackedFolders: " + status.getUntrackedFolders() + "<br/>" );
				log.html( "<br/>" );
			}
		}
		catch( Exception e )
		{
			throw new RuntimeException( e );
		}

		closeRepo();
	}

	private void ensureOpen()
	{
		if( repository != null )
			return;

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try
		{
			repository = builder.setGitDir( path.toFile() ).readEnvironment().findGitDir().setWorkTree( path.toFile() ).build();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	private void closeRepo()
	{
		repository.close();
		repository = null;
	}
}
