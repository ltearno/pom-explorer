package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.settings.Settings;
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

import fr.lteconsulting.pomexplorer.model.Gav;

public class MavenResolver
{
	private MavenResolverSystem resolver;

	private MavenWorkingSessionImpl mavenSession;

	private RepositorySystemSession s;

	private MavenRepositorySystem system;

	private Settings settings;

	private String localRepositoryPath;

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
		settings = getField( mavenSession, "settings" );
		localRepositoryPath = getField( settings, "localRepository" );
	}

	public File resolvePom( Gav gav, String extension, boolean online )
	{
		if( gav == null || !gav.isResolved() )
			return null;

		String key = gav.toString() + ":" + extension;

		if( gav.getVersion().startsWith( "[" ) )
			return null;

		File pomFile = resolvedFiles.get( key );
		if( resolvedFiles.containsKey( key ) )
			return pomFile;

		if( pomFile == null && "pom".equals( extension ) && localRepositoryPath != null )
		{
			Path path = Paths.get( localRepositoryPath );
			String[] parts = gav.getGroupId().split( "\\." );
			if( parts != null )
			{
				for( String part : parts )
					path = path.resolve( Paths.get( part ) );
			}
			path = path.resolve( Paths.get( gav.getArtifactId() ) );
			path = path.resolve( Paths.get( gav.getVersion() ) );
			path = path.resolve( gav.getArtifactId() + "-" + gav.getVersion() + ".pom" );

			pomFile = path.toFile();
			if( !pomFile.exists() || !pomFile.isFile() )
				pomFile = null;
		}

		if( pomFile == null && online )
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
		}

		resolvedFiles.put( key, pomFile );

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
