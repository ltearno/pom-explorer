package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.util.List;

import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.model.transitivity.Repository;

public class DefaultPomFileLoader implements PomFileLoader
{
	private final Session session;
	private final boolean online;

	public DefaultPomFileLoader( Session session, boolean online )
	{
		this.session = session;
		this.online = online;
	}

	@Override
	public File loadPomFileForGav( Gav gav, List<Repository> additionalRepos, Log log )
	{
		if( gav == null || !gav.isResolved() || gav.getVersion().startsWith( "[" ) || gav.getVersion().startsWith( "@" ) )
			return null;

		MavenResolver resolver = session.mavenResolver();

		File pomFile = resolver.resolvePom( gav, "pom", online, additionalRepos, log );
		if( pomFile == null || !pomFile.exists() )
			return null;

		return pomFile;
	}
}
