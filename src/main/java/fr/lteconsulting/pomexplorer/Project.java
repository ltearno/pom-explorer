package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.Dependency;
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

	public Project( File pomFile, ParsedPomFile resolvedPom, MavenProject project )
	{
		this.pomFile = pomFile;
		this.resolvedPom = resolvedPom;
		this.project = project;

		dependencies = new HashMap<>();
		for( MavenDependency dependency : resolvedPom.getDependencies() )
		{
			DependencyInfo info = new DependencyInfo( dependency );

			dependencies.put( new GAV( dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion() ), info );
		}

		for( Dependency dependency : project.getDependencies() )
		{
			DependencyInfo info = getApproximateDependency( dependency.getGroupId(), dependency.getArtifactId() );
			if( info == null )
			{
				info = new DependencyInfo( dependency );
				dependencies.put( info.getGav(), info );
			}
			else
			{
				info.setReadDependency( dependency );
			}
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

	public class DependencyInfo
	{
		MavenDependency resolved;
		Dependency readden;

		GAV gav;
		GAV unresolvedGav;

		public DependencyInfo( MavenDependency resolved )
		{
			this.resolved = resolved;
		}

		public DependencyInfo( Dependency readden )
		{
			this.readden = readden;
		}

		public void setReadDependency( Dependency readden )
		{
			this.readden = readden;
		}

		public GAV getGav()
		{
			if( getResolvedGav() != null )
				return getResolvedGav();
			else if( getUnresolvedGav() != null )
				return getUnresolvedGav();
			return null;
		}

		public GAV getResolvedGav()
		{
			if( gav == null && resolved != null )
				gav = new GAV( resolved.getGroupId(), resolved.getArtifactId(), resolved.getVersion() );

			return gav;
		}

		public GAV getUnresolvedGav()
		{
			if( unresolvedGav == null && readden != null )
				unresolvedGav = new GAV( readden.getGroupId(), readden.getArtifactId(), readden.getVersion() );
			return unresolvedGav;
		}

		@Override
		public String toString()
		{
			String res = "";

			if( getResolvedGav() != null && getUnresolvedGav() != null )
			{
				if( getResolvedGav().equals( getUnresolvedGav() ) )
					res = getResolvedGav().toString();
				else
					res = "[*] " + getResolvedGav() + " / " + getUnresolvedGav();
			}
			else
			{
				if( getResolvedGav() != null )
					res = getResolvedGav().toString();
				else if( getUnresolvedGav() != null )
					res = "[!]" + getUnresolvedGav();
				else
					res = "[!!!] NULL";
			}

			if( resolved != null )
				res += " " + resolved.getClassifier() + ":" + resolved.getScope();
			else if( readden != null )
				res += " " + readden.getClassifier() + ":" + readden.getScope();

			return res;
		}
	}

	@Override
	public String toString()
	{
		return getGav() + " @ " + pomFile.getAbsolutePath();
	}
}
