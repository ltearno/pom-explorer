package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;

/**
 * A POM project information
 * 
 * @author Arnaud
 */
public class Project
{
	private File pomFile;
	private ParsedPomFile resolvedPom;
	private MavenProject project;

	private GAV gav;
	private Map<GAV, DependencyInfo> dependencies;

	private Map<GAV, DependencyInfo> pluginDependencies;

	public Project( File pomFile, ParsedPomFile resolvedPom, MavenProject project )
	{
		this.pomFile = pomFile;
		this.resolvedPom = resolvedPom;
		this.project = project;

		dependencies = new HashMap<>();
		for( MavenDependency dependency : resolvedPom.getDependencies() )
		{
			DependencyInfo info = new DependencyInfo( dependency, DependencyInfoType.DEPENDENCY );

			dependencies.put( new GAV( dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion() ), info );
		}

		for( Dependency dependency : project.getDependencies() )
		{
			DependencyInfo info = getApproximateDependency( dependency.getGroupId(), dependency.getArtifactId() );
			if( info == null )
			{
				info = new DependencyInfo( dependency, DependencyInfoType.DEPENDENCY );
				dependencies.put( info.getGav(), info );
			}
			else
			{
				info.setReadDependency( dependency );
			}
		}

		pluginDependencies = new HashMap<>();
		for( Plugin plugin : project.getBuildPlugins() )
		{
			GAV pluginGAV = new GAV( plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion() );
			Dependency dep = new Dependency();
			dep.setGroupId( plugin.getGroupId() );
			dep.setArtifactId( plugin.getArtifactId() );
			dep.setVersion( plugin.getVersion() );

			DependencyInfo info = new DependencyInfo( dep, DependencyInfoType.PLUGIN );

			pluginDependencies.put( pluginGAV, info );
		}
	}

	public File getPomFile()
	{
		return pomFile;
	}

	public ParsedPomFile getResolvedPom()
	{
		return resolvedPom;
	}

	public MavenProject getUnresolvedPom()
	{
		return project;
	}

	private DependencyInfo getApproximateDependency( String groupId, String artifactId )
	{
		for( DependencyInfo info : dependencies.values() )
		{
			if( info.getGav().getGroupId().equals( groupId ) && info.getGav().getArtifactId().equals( artifactId ) )
				return info;
		}

		return null;
	}

	public GAV getGav()
	{
		if( gav == null )
			gav = new GAV( resolvedPom.getGroupId(), resolvedPom.getArtifactId(), resolvedPom.getVersion() );
		return gav;
	}

	public Map<GAV, DependencyInfo> getDependencies()
	{
		return dependencies;
	}

	public Map<GAV, DependencyInfo> getPluginDependencies()
	{
		return pluginDependencies;
	}

	@Override
	public String toString()
	{
		return getGav() + " @ " + pomFile.getAbsolutePath();
	}
}
