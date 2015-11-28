package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;

/**
 * A POM project information
 * 
 * @author Arnaud
 */
public class Project
{
	private final File pomFile;
	private final MavenProject project;
	private final GAV parentGav;
	private final GAV gav;
	private final Map<String, String> properties;

	private Map<WorkingSession, Map<GAV, GavLocation>> dependencies;
	private Map<WorkingSession, Map<GAV, GavLocation>> pluginDependencies;

	public Project( File pomFile, MavenProject project, GAV parentGav )
	{
		this.pomFile = pomFile;
		this.project = project;
		this.parentGav = parentGav;

		String groupId = project.getGroupId() != null ? project.getGroupId() : getParent().getGroupId();
		String version = project.getVersion() != null ? project.getVersion() : getParent().getVersion();

		gav = new GAV( groupId, project.getArtifactId(), version );

		properties = new HashMap<>();
		project.getProperties().forEach( ( key, value ) -> properties.put( key.toString(), value.toString() ) );
	}

	public Set<GAV> getMissingGavsForResolution( WorkingSession session, ILogger log )
	{
		Set<GAV> gavs = new HashSet<>();
		getMissingGavsForResolutionInternal( session, log, gavs );
		return gavs;
	}

	private void getMissingGavsForResolutionInternal( WorkingSession session, ILogger log, Set<GAV> gavs )
	{
		if( parentGav != null )
		{
			Project parentProject = session.projects().forGav( parentGav );
			if( parentProject == null || !parentProject.isResolvable( session, log ) )
				gavs.add( parentGav );
		}

		if( project.getDependencyManagement() != null && project.getDependencyManagement().getDependencies() != null )
		{
			for( Dependency d : project.getDependencyManagement().getDependencies() )
			{
				if( "import".equals( d.getScope() ) && "pom".equals( d.getType() ) )
				{
					String version;
					if( Tools.isMavenVariable( d.getVersion() ) )
						version = resolveProperty( session, log, d.getVersion() );
					else
						version = d.getVersion();

					GAV bomGav = new GAV( d.getGroupId(), d.getArtifactId(), version );

					Project bomProject = session.projects().forGav( bomGav );
					if( bomProject == null || !bomProject.isResolvable( session, log ) )
						gavs.add( bomGav );
				}
			}
		}
	}

	private GAV resolveGav( GAV gav, WorkingSession session, ILogger log, boolean resolveVersionWithDependencyMngt, boolean resolveVersionWithBuildDependencyMngt )
	{
		String groupId;
		if( Tools.isMavenVariable( gav.getGroupId() ) )
			groupId = resolveProperty( session, log, gav.getGroupId() );
		else
			groupId = gav.getGroupId();

		String artifactId;
		if( Tools.isMavenVariable( gav.getArtifactId() ) )
			artifactId = resolveProperty( session, log, gav.getArtifactId() );
		else
			artifactId = gav.getArtifactId();

		String version = null;
		if( Tools.isMavenVariable( gav.getVersion() ) )
		{
			version = resolveProperty( session, log, gav.getVersion() );
		}
		else if( gav.getVersion() == null )
		{
			if( resolveVersionWithDependencyMngt )
			{
				GavLocation gavLocation = findDependencyLocationInDependencyManagement( session, log, gav.getGroupId(), gav.getArtifactId() );
				if( gavLocation != null )
					version = gavLocation.getResolvedGav().getVersion();
			}

			if( version == null && resolveVersionWithBuildDependencyMngt )
			{
				GavLocation gavLocation = findDependencyLocationInBuildDependencyManagement( session, log, gav.getGroupId(), gav.getArtifactId() );
				if( gavLocation != null )
					version = gavLocation.getResolvedGav().getVersion();
			}
		}
		else
		{
			version = gav.getVersion();
		}

		if( version == null )
		{
			if( "maven-war-plugin".equals( gav.getArtifactId() ) && "org.apache.maven.plugins".equals( gav.getGroupId() ) )
				version = "2.2";
			else if( "maven-assembly-plugin".equals( gav.getArtifactId() ) && "org.apache.maven.plugins".equals( gav.getGroupId() ) )
				version = "2.2-beta-5";
			else if( "maven-compiler-plugin".equals( gav.getArtifactId() ) && "org.apache.maven.plugins".equals( gav.getGroupId() ) )
				version = "2.5.1";
			else if( "org.apache.maven.plugins".equals( gav.getGroupId() ) )
				version = "default-maven-version";
		}

		if( !(isResolved( version ) && isResolved( groupId ) && isResolved( artifactId )) )
			throw new IllegalStateException( toString() + " : cannot resolve incomplete gav : " + gav );

		return new GAV( groupId, artifactId, version );
	}

	private boolean isResolved( String value )
	{
		return value != null && !Tools.isMavenVariable( value );
	}

	public boolean isResolvable( WorkingSession session, ILogger log )
	{
		return getMissingGavsForResolution( session, log ).isEmpty();
	}

	public String resolveProperty( WorkingSession session, ILogger log, String propertyName )
	{
		String value;

		do
		{
			value = resolvePropertyInternal( session, log, propertyName );
			propertyName = value;
		}
		while( value != null && Tools.isMavenVariable( value ) );

		return value;
	}

	public String resolvePropertyInternal( WorkingSession session, ILogger log, String propertyName )
	{
		if( Tools.isMavenVariable( propertyName ) )
			propertyName = Tools.getPropertyNameFromPropertyReference( propertyName );

		String property = properties.get( propertyName );
		if( property != null )
			return property;

		if( "project.version".equals( propertyName ) || "pom.version".equals( propertyName ) )
			return gav.getVersion();

		if( "project.groupId".equals( propertyName ) || "pom.groupId".equals( propertyName ) )
			return gav.getGroupId();

		if( "project.artifactId".equals( propertyName ) || "pom.artifactId".equals( propertyName ) )
			return gav.getArtifactId();

		if( "project.prerequisites.maven".equals( propertyName ) )
			return project.getPrerequisites().getMaven();

		if( "java.version".equals( propertyName ) )
			return propertyName;

		Project parentProject = session.projects().forGav( parentGav );
		if( parentProject == null )
		{
			log.html( Tools.errorMessage( "cannot resolve " + getGav() + " project's property '" + propertyName + "' because parent project " + parentGav + " can't be found." ) );
			return null;
		}

		if( propertyName.startsWith( "project.parent." ) )
			return parentProject.resolveProperty( session, log, propertyName.replace( "project.parent.", "project." ) );

		return parentProject.resolveProperty( session, log, propertyName );
	}

	public GavLocation findDependencyLocationInDependencyManagement( WorkingSession session, ILogger log, String groupId, String artifactId )
	{
		if( project.getDependencyManagement() != null && project.getDependencyManagement().getDependencies() != null )
		{
			for( Dependency d : project.getDependencyManagement().getDependencies() )
			{
				// TODO resolve possible properties

				if( "import".equals( d.getScope() ) && "pom".equals( d.getType() ) )
				{
					GAV bomGav = resolveGav( new GAV( d.getGroupId(), d.getArtifactId(), d.getVersion() ), session, log, false, false );

					Project bomProject = session.projects().forGav( bomGav );
					if( bomProject != null )
					{
						GavLocation inBom = bomProject.findDependencyLocationInDependencyManagement( session, log, groupId, artifactId );
						if( inBom != null )
							return inBom;
					}
					else
					{
						log.html( Tools.warningMessage( "cannot find the project " + bomGav
								+ " which is imported as a bom in the project " + project
								+ ". This prevents BOM dependency analysis to find dependency to " + groupId + ":" + artifactId ) );
					}
				}

				if( groupId.equals( d.getGroupId() ) && artifactId.equals( d.getArtifactId() ) )
				{
					GAV g = resolveGav( new GAV( d.getGroupId(), d.getArtifactId(), d.getVersion() ), session, log, true, false );
					return new GavLocation( this, PomSection.DEPENDENCY_MNGT, g, d );
				}
			}
		}

		if( parentGav != null )
		{
			Project parentProject = session.projects().forGav( parentGav );
			if( parentProject != null )
				return parentProject.findDependencyLocationInDependencyManagement( session, log, groupId, artifactId );
		}

		return null;
	}

	private GavLocation findDependencyLocationInBuildDependencyManagement( WorkingSession session, ILogger log, String groupId, String artifactId )
	{
		if( project.getBuild() != null && project.getBuild().getPluginManagement() != null )
		{
			for( Plugin d : project.getBuild().getPluginManagement().getPlugins() )
			{
				if( groupId.equals( d.getGroupId() ) && artifactId.equals( d.getArtifactId() ) )
				{
					// TODO resolve possible properties
					GAV g = resolveGav( new GAV( d.getGroupId(), d.getArtifactId(), d.getVersion() ), session, log, false, false );
					return new GavLocation( this, PomSection.DEPENDENCY_MNGT, g );
				}
			}
		}

		if( parentGav != null )
		{
			Project parentProject = session.projects().forGav( parentGav );
			if( parentProject != null )
				return parentProject.findDependencyLocationInBuildDependencyManagement( session, log, groupId, artifactId );
		}

		return null;
	}

	public GAV getParent()
	{
		return parentGav;
	}

	public File getPomFile()
	{
		return pomFile;
	}

	public MavenProject getUnresolvedPom()
	{
		return project;
	}

	public GAV getGav()
	{
		return gav;
	}

	public Map<GAV, GavLocation> getDependencies( WorkingSession session, ILogger log )
	{
		if( dependencies == null )
			dependencies = new HashMap<>();

		if( !dependencies.containsKey( session ) )
		{
			Map<GAV, GavLocation> dependencies = new HashMap<>();

			for( Dependency dependency : project.getDependencies() )
			{
				GAV dependencyGav = resolveGav( new GAV( dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion() ), session, log, true, false );

				GavLocation info = new GavLocation( this, PomSection.DEPENDENCY, dependencyGav, dependency );
				dependencies.put( dependencyGav, info );
			}

			this.dependencies.put( session, dependencies );
		}

		return dependencies.get( session );
	}

	public Map<GAV, GavLocation> getPluginDependencies( WorkingSession session, ILogger log )
	{
		if( pluginDependencies == null )
			pluginDependencies = new HashMap<>();

		if( !pluginDependencies.containsKey( session ) )
		{
			Map<GAV, GavLocation> pluginDependencies = new HashMap<>();

			for( Plugin plugin : project.getBuildPlugins() )
			{
				GAV dependencyGav = resolveGav( new GAV( plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion() ), session, log, false, true );

				GavLocation info = new GavLocation( this, PomSection.PLUGIN, dependencyGav, dependencyGav );
				pluginDependencies.put( dependencyGav, info );
			}

			this.pluginDependencies.put( session, pluginDependencies );
		}

		return pluginDependencies.get( session );
	}

	public String getPath()
	{
		return pomFile.getParentFile().getAbsolutePath();
	}

	@Override
	public String toString()
	{
		return getGav() + " (<i>" + pomFile.getAbsolutePath() + "</i>)";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pomFile == null) ? 0 : pomFile.getAbsolutePath().hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		Project other = (Project) obj;
		if( pomFile == null )
		{
			if( other.pomFile != null )
				return false;
		}
		else if( !pomFile.getAbsolutePath().equals( other.pomFile.getAbsolutePath() ) )
			return false;
		return true;
	}
}
