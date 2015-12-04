package fr.lteconsulting.pomexplorer;

import static fr.lteconsulting.pomexplorer.Tools.isMavenVariable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;

/**
 * A POM project information
 * 
 * @author Arnaud
 */
public class Project
{
	private final File pomFile;

	private final boolean isExternal;

	private final MavenProject project;

	private final Gav parentGav;

	private final Gav gav;

	private final Map<String, String> properties;

	private Map<WorkingSession, Map<Gav, GavLocation>> dependencies;

	private Map<WorkingSession, Map<Gav, GavLocation>> pluginDependencies;

	public Project( File pomFile, boolean isExternal ) throws Exception
	{
		this.pomFile = pomFile;
		this.isExternal = isExternal;

		project = readPomFile( pomFile );
		if( project == null )
			throw new RuntimeException( "cannot read pom " + pomFile.getAbsolutePath() );

		Parent parent = project.getModel().getParent();
		if( parent != null )
		{
			parentGav = new Gav( parent.getGroupId(), parent.getArtifactId(), parent.getVersion() );
			if( !parentGav.isResolved() )
				throw new RuntimeException( "parent project not resolved" );
		}
		else
			parentGav = null;

		String groupId = project.getGroupId() != null ? project.getGroupId() : getParent().getGroupId();
		String version = project.getVersion() != null ? project.getVersion() : getParent().getVersion();
		if( "${parent.version}".equals( version ) )
			version = getParent().getVersion();

		gav = new Gav( groupId, project.getArtifactId(), version );

		if( !gav.isResolved() )
			throw new RuntimeException( "Non resolved project's GAV: " + gav );

		properties = new HashMap<>();
		project.getProperties().forEach( ( key, value ) -> properties.put( key.toString(), value.toString() ) );
	}

	public boolean isBuildable()
	{
		return !isExternal;
	}

	public Set<Gav> getMissingGavsForResolution( WorkingSession session, ILogger log )
	{
		return getMissingGavsForResolution( session, log, null );
	}

	public Set<Gav> getMissingGavsForResolution( WorkingSession session, ILogger log, Set<Gav> gavs )
	{
		if( parentGav != null )
		{
			if( gavs == null )
				gavs = new HashSet<>();

			Project parentProject = session.projects().forGav( parentGav );
			if( parentProject == null )
			{
				gavs.add( parentGav );
			}
			else
			{
				Set<Gav> missingGavs = parentProject.getMissingGavsForResolution( session, log );
				if( missingGavs != null )
					gavs.addAll( missingGavs );
			}
		}

		if( project.getDependencyManagement() != null && project.getDependencyManagement().getDependencies() != null )
		{
			for( Dependency d : project.getDependencyManagement().getDependencies() )
			{
				if( "import".equals( d.getScope() ) && "pom".equals( d.getType() ) )
				{
					String version;
					if( isMavenVariable( d.getVersion() ) )
						version = resolveProperty( session, log, d.getVersion() );
					else
						version = d.getVersion();

					Gav bomGav = resolveGav( new Gav( d.getGroupId(), d.getArtifactId(), version ), session, log, true, false );

					Project bomProject = session.projects().forGav( bomGav );
					if( bomProject == null )
					{
						if( gavs == null )
							gavs = new HashSet<>();

						gavs.add( bomGav );
					}
					else
					{
						bomProject.getMissingGavsForResolution( session, log, gavs );
					}
				}
			}
		}

		return gavs;
	}

	public Gav resolveGav(Gav gav, WorkingSession session, ILogger log, boolean resolveVersionWithDependencyMngt,
			boolean resolveVersionWithBuildDependencyMngt )
	{
		String groupId;
		if( isMavenVariable( gav.getGroupId() ) )
			groupId = resolveProperty( session, log, gav.getGroupId() );
		else
			groupId = gav.getGroupId();

		String artifactId;
		if( isMavenVariable( gav.getArtifactId() ) )
			artifactId = resolveProperty( session, log, gav.getArtifactId() );
		else
			artifactId = gav.getArtifactId();

		String version = null;
		if( isMavenVariable( gav.getVersion() ) )
		{
			version = resolveProperty( session, log, gav.getVersion() );
		}
		else if( gav.getVersion() == null )
		{
			if( resolveVersionWithDependencyMngt )
			{
				GavLocation gavLocation = findDependencyLocationInDependencyManagement( session, log, groupId, artifactId );
				if( gavLocation != null )
					version = gavLocation.getResolvedGav().getVersion();
			}

			if( version == null && resolveVersionWithBuildDependencyMngt )
			{
				GavLocation gavLocation = findDependencyLocationInBuildDependencyManagement( session, log, groupId,
						artifactId );
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
			String ga = groupId + ":" + artifactId;

			version = "UNKNOWN";
			log.html( Tools.warningMessage( "unspecified dependency version to " + ga + " in project '" + toString() + "' resolved to '" + version + "', check the pom file please !" ) );

			// find a way to handle those versions :
			// "org.apache.maven.plugins:maven-war-plugin":
			// "org.apache.maven.plugins:maven-assembly-plugin":
			// "org.apache.maven.plugins:maven-compiler-plugin":
			// "org.apache.maven.plugins:maven-source-plugin":
			// "org.codehaus.mojo:buildnumber-maven-plugin":
			// "org.apache.felix:maven-bundle-plugin":
		}

		if( !(isResolved( version ) && isResolved( groupId ) && isResolved( artifactId )) )
			throw new IllegalStateException( toString() + " : cannot resolve incomplete gav : " + gav );

		return new Gav( groupId, artifactId, version );
	}

	private boolean isResolved( String value )
	{
		return value != null && !isMavenVariable( value );
	}

	public String resolveValue( WorkingSession session, ILogger log, String value )
	{
		if( value == null )
			return null;
		if( isMavenVariable( value ) )
			return resolveProperty( session, log, value );
		return value;
	}

	public String resolveProperty( WorkingSession session, ILogger log, String propertyName )
	{
		String originalRequestedPropertyName = propertyName;

		if( isMavenVariable( propertyName ) )
			propertyName = Tools.getPropertyNameFromPropertyReference( propertyName );

		String value = properties.get( propertyName );

		if( value == null )
		{
			switch( propertyName )
			{
				case "version":
					log.html( Tools.warningMessage( "illegal property 'version' used in the project " + toString() + ", value resolved to project's version." ) );
				case "project.version":
				case "pom.version":
					value = gav.getVersion();
					break;

				case "groupId":
					log.html( Tools.warningMessage( "illegal property 'groupId' used in the project " + toString() + ", value resolved to project's groupId." ) );
				case "project.groupId":
				case "pom.groupId":
					value = gav.getGroupId();
					break;

				case "project.artifactId":
				case "pom.artifactId":
					value = gav.getArtifactId();
					break;

				case "project.prerequisites.maven":
					value = project.getPrerequisites().getMaven();
					break;

				case "mavenVersion":
				case "java.version":
					value = propertyName;
					break;
			}
		}

		if( value == null )
		{
			Project parentProject = session.projects().forGav( parentGav );
			if( parentProject != null )
			{
				if( propertyName.startsWith( "project.parent." ) )
					value = parentProject.resolveProperty( session, log, propertyName.replace( "project.parent.", "project." ) );
				else
					value = parentProject.resolveProperty( session, log, propertyName );
			}
		}

		if( value != null && isMavenVariable( value ) )
			value = resolveProperty( session, log, value );

		if( value == null )
		{
			log.html( Tools.warningMessage( "cannot resolve property '" + originalRequestedPropertyName + "' in project " + toString() ) );
			value = "unknown";
		}

		return value;
	}

	public GavLocation findDependencyLocationInDependencyManagement( WorkingSession session, ILogger log, String groupId,
			String artifactId )
	{
		if( project.getDependencyManagement() != null && project.getDependencyManagement().getDependencies() != null )
		{
			for( Dependency d : project.getDependencyManagement().getDependencies() )
			{
				String dependencyGroupId = d.getGroupId();
				if( isMavenVariable( dependencyGroupId ) )
					dependencyGroupId = resolveProperty( session, log, dependencyGroupId );

				String dependencyArtifactId = d.getArtifactId();
				if( isMavenVariable( dependencyArtifactId ) )
					dependencyArtifactId = resolveProperty( session, log, dependencyArtifactId );

				if( "import".equals( d.getScope() ) && "pom".equals( d.getType() ) )
				{
					Gav bomGav = resolveGav( new Gav( dependencyGroupId, dependencyArtifactId, d.getVersion() ), session, log,
							false, false );

					Project bomProject = session.projects().forGav( bomGav );
					if( bomProject != null )
					{
						GavLocation inBom = bomProject.findDependencyLocationInDependencyManagement( session, log, groupId,
								artifactId );
						if( inBom != null )
							return inBom;
					}
					else
					{
						log.html( Tools
								.warningMessage( "cannot find the project " + bomGav
										+ " which is imported as a bom in the project " + project
										+ ". This prevents BOM dependency analysis to find dependency to " + groupId + ":"
										+ artifactId ) );
					}
				}

				if( groupId.equals( dependencyGroupId ) && artifactId.equals( dependencyArtifactId ) )
				{
					if( d.getVersion() != null )
					{
						Gav unresolvedGav = new Gav( dependencyGroupId, dependencyArtifactId, d.getVersion() );
						Gav g = resolveGav( unresolvedGav, session, log, true, false );
						if( g.isResolved() )
							return new GavLocation( this, PomSection.DEPENDENCY_MNGT, g, unresolvedGav, resolveValue( session, log, d.getScope() ), d.getClassifier() );
					}
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

	public GavLocation findDependencyLocationInBuildDependencyManagement( WorkingSession session, ILogger log,
			String groupId, String artifactId )
	{
		if( project.getBuild() != null && project.getBuild().getPluginManagement() != null )
		{
			for( Plugin d : project.getBuild().getPluginManagement().getPlugins() )
			{
				String dependencyGroupId = d.getGroupId();
				if( isMavenVariable( dependencyGroupId ) )
					dependencyGroupId = resolveProperty( session, log, dependencyGroupId );

				String dependencyArtifactId = d.getArtifactId();
				if( isMavenVariable( dependencyArtifactId ) )
					dependencyArtifactId = resolveProperty( session, log, dependencyArtifactId );

				if( groupId.equals( dependencyGroupId ) && artifactId.equals( dependencyArtifactId ) )
				{
					if( d.getVersion() != null )
					{
						Gav unresolvedGav = new Gav( dependencyGroupId, dependencyArtifactId, d.getVersion() );
						Gav g = resolveGav( unresolvedGav, session, log,
								false, true );
						if( g.isResolved() )
							return new GavLocation( this, PomSection.DEPENDENCY_MNGT, g, unresolvedGav );
					}
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

	public Gav getParent()
	{
		return parentGav;
	}

	public File getPomFile()
	{
		return pomFile;
	}

	public MavenProject getMavenProject()
	{
		return project;
	}

	public Gav getGav()
	{
		return gav;
	}

	public Map<Gav, GavLocation> getDependencies( WorkingSession session, ILogger log )
	{
		if( dependencies == null )
			dependencies = new HashMap<>();

		if( !dependencies.containsKey( session ) )
		{
			Map<Gav, GavLocation> dependencies = new HashMap<>();

			for( Dependency dependency : project.getDependencies() )
			{
				Gav unresolvedGav = new Gav( dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion() );
				Gav dependencyGav = resolveGav( unresolvedGav, session, log, true, false );

				GavLocation info = new GavLocation( this, PomSection.DEPENDENCY, dependencyGav, unresolvedGav, resolveValue( session, log, dependency.getScope() ), dependency.getClassifier() );
				dependencies.put( dependencyGav, info );
			}

			this.dependencies.put( session, dependencies );
		}

		return dependencies.get( session );
	}

	public Map<Gav, GavLocation> getPluginDependencies( WorkingSession session, ILogger log )
	{
		if( pluginDependencies == null )
			pluginDependencies = new HashMap<>();

		if( !pluginDependencies.containsKey( session ) )
		{
			Map<Gav, GavLocation> pluginDependencies = new HashMap<>();

			for( Plugin plugin : project.getBuildPlugins() )
			{
				Gav unresolvedGav = new Gav( plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion() );
				Gav dependencyGav = resolveGav( unresolvedGav,
						session, log, false, true );

				GavLocation info = new GavLocation( this, PomSection.PLUGIN, dependencyGav, unresolvedGav );
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

	private MavenProject readPomFile( File pom )
	{
		try( FileReader reader = new FileReader( pom ) )
		{
			MavenXpp3Reader mavenreader = new MavenXpp3Reader();
			Model model = mavenreader.read( reader );
			model.setPomFile( pom );

			return new MavenProject( model );
		}
		catch( IOException | XmlPullParserException e )
		{
			return null;
		}
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
