package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.lang.reflect.Field;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenRepositorySystem;

public class MavenResolver
{
	private MavenResolverSystem resolver;
	private MavenWorkingSessionImpl mavenSession;
	private RepositorySystemSession s;
	private MavenRepositorySystem system;

	void init( WorkingSession session )
	{
		String mavenSettingsFilePath = session.getMavenSettingsFilePath();

		if( mavenSettingsFilePath != null && !mavenSettingsFilePath.isEmpty() )
			resolver = Maven.configureResolver().fromFile( mavenSettingsFilePath );
		else
			resolver = Maven.resolver();

		mavenSession = getField( getField( resolver, "delegate" ), "session" );
		s = getField( mavenSession, "session" );
		system = getField( mavenSession, "system" );
	}

	File resolvePom( GAV gav, String extension )
	{
		Artifact pomArtifact = new DefaultArtifact( gav.getGroupId(), gav.getArtifactId(), "", extension, gav.getVersion() );
		try
		{
			ArtifactRequest request = new ArtifactRequest( pomArtifact, getField( mavenSession, "remoteRepositories" ), null );
			pomArtifact = system.resolveArtifact( s, request ).getArtifact();

		}
		catch( ArtifactResolutionException e )
		{
			return null;
		}

		File pomFile = pomArtifact.getFile();
		return pomFile;
	}

	@SuppressWarnings( "unchecked" )
	private <T> T getField( Object o, String field )
	{
		Class<?> currentClass = o.getClass();
		Field f = null;
		do
		{
			try
			{
				f = currentClass.getDeclaredField( field );
			}
			catch( Exception e1 )
			{
			}

			try
			{
				if( f != null )
				{
					f.setAccessible( true );
					return (T) f.get( o );
				}
			}
			catch( IllegalAccessException e )
			{
				e.printStackTrace();
				return null;
			}

			currentClass = currentClass.getSuperclass();
		}
		while( f == null && currentClass != null );

		return null;
	}
}