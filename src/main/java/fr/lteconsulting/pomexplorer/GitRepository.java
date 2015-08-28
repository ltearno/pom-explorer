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

	public void getStatus( StringBuilder log, boolean details )
	{
		ensureOpen();

		Status status;
		try( Git git = Git.open( Paths.get( path.toString(), ".git" ).toFile() ) )
		{
			status = git.status().call();
			int nb = status.getAdded().size() + status.getChanged().size() + status.getConflicting().size() + status.getMissing().size() + status.getModified().size() + status.getRemoved().size();

			log.append((nb > 0 ? "[*] " : "[ ] ") + path.toAbsolutePath().toString());
			log.append(" " + git.getRepository().getBranch());
			log.append((nb > 0 ? (" <b>(" + nb + " changes</b>)") : "") + "<br/>");

			if( details )
			{
				log.append( "<br/>" );
				if (!status.getAdded().isEmpty())
					log.append("Added: " + status.getAdded() + "<br/>");
				if (!status.getChanged().isEmpty())
					log.append("Changed: " + status.getChanged() + "<br/>");
				if (!status.getConflicting().isEmpty())
				{
					log.append("Conflicting: " + status.getConflicting() + "<br/>");
					log.append("ConflictingStageState: " + status.getConflictingStageState() + "<br/>");
				}
				if (!status.getMissing().isEmpty())
					log.append("Missing: " + status.getMissing() + "<br/>");
				if (!status.getModified().isEmpty())
					log.append("Modified: " + status.getModified() + "<br/>");
				if (!status.getRemoved().isEmpty())
					log.append("Removed: " + status.getRemoved() + "<br/>");
				if (!status.getUntracked().isEmpty())
					log.append("Untracked: " + status.getUntracked() + "<br/>");
				if (!status.getUntrackedFolders().isEmpty())
					log.append("UntrackedFolders: " + status.getUntrackedFolders() + "<br/>");
				log.append( "<br/>" );
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
