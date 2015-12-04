package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
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

	private List<RemoteRepository> repositories;

	private final Map<String, File> resolvedFiles = new HashMap<>();

	public void init( String mavenSettingsFilePath )
	{
		if( mavenSettingsFilePath != null && !mavenSettingsFilePath.isEmpty() )
			resolver = Maven.configureResolver().fromFile( mavenSettingsFilePath );
		else
			resolver = Maven.resolver();

		// have the session initialize remote repositories
		mavenSession = getField( getField( resolver, "delegate" ), "session" );
		repositories = callMethod( mavenSession, "getRemoteRepositories" );
		s = getField( mavenSession, "session" );
		system = getField( mavenSession, "system" );
	}

	public File resolvePom( Gav gav, String extension )
	{
		String key = gav.toString() + ":" + extension;

		File pomFile = resolvedFiles.get( key );
		if( pomFile == null )
		{
			Artifact pomArtifact = new DefaultArtifact( gav.getGroupId(), gav.getArtifactId(), null, extension, gav.getVersion() );
			try
			{
				ArtifactRequest request = new ArtifactRequest( pomArtifact, repositories, null );
				pomArtifact = system.resolveArtifact( s, request ).getArtifact();
			}
			catch( ArtifactResolutionException e )
			{
				return null;
			}

			pomFile = pomArtifact.getFile();
			resolvedFiles.put( key, pomFile );
		}

		return pomFile;
	}

	@SuppressWarnings( "unchecked" )
	private <T> T callMethod( Object object, String methodName )
	{
		try
		{
			Method m = object.getClass().getDeclaredMethod( methodName );
			m.setAccessible( true );
			Object result = m.invoke( object );

			return (T) result;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			throw new RuntimeException( e );
		}
	}

	@SuppressWarnings( { "unchecked" } )
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
				return (T) null;
			}

			currentClass = currentClass.getSuperclass();
		}
		while( f == null && currentClass != null );

		return (T) null;
	}
}
