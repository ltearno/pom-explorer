package fr.lteconsulting.pomexplorer;

import static fr.lteconsulting.pomexplorer.Tools.isMavenVariable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import fr.lteconsulting.pomexplorer.depanalyze.PropertyLocation;
import fr.lteconsulting.pomexplorer.graph.relation.Scope;
import fr.lteconsulting.pomexplorer.model.Dependency;
import fr.lteconsulting.pomexplorer.model.DependencyKey;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.model.GroupArtifact;
import fr.lteconsulting.pomexplorer.model.VersionScope;
import fr.lteconsulting.pomexplorer.model.transitivity.DependencyManagement;
import fr.lteconsulting.pomexplorer.model.transitivity.RawDependency;

/**
 * A POM project information
 * <p>
 * levels of accessors :
 * <ol>
 * <li>raw : how the thing is declared in maven pom file
 * <li>declared : how the thing is declared with variables resolved
 * <li>local : how the thing is resolved with the local hierarchy (parents+boms)
 * <li>transitive : how the thing is resolves including transitive dependency
 * processing
 * 
 * 
 * <ol>
 * <li>Just reading the local pom file (Maybe required to read the parent chain to know the gav, ERROR if unable)
 * <li>Reading the parents and BOMs if required => able to build the graph
 * <li>Having access to the desired level of transitive dependencies (so need to fetch new projects)
 * 
 * <ol>
 * <li>Local project information (without variable resolution) (many things can be null)
 * <li>Hierarchical project information (with variable resolution) [project+ancestors+boms] (allow to resolve everything for the local project)
 * <li>Transitive information [transitive projects] (allow to know a the transitive dependency tree)
 */
public class Project
{
	public static final Comparator<Project> alphabeticalComparator = Comparator.comparing( Project::toString );

	private final File pomFile;
	private final boolean isExternal;

	private MavenProject project;
	private Gav parentGav;
	private Gav gav;
	private Map<String, String> properties;

	private Map<DependencyKey, Dependency> dependencyManagement;
	private Set<Dependency> dependencies;
	private Set<Gav> pluginDependencies;

	private Map<String, ValueResolution> cachedResolutions;
	private Map<DependencyKey, DependencyManagement> cachedLocalDependencyManagement;
	private Map<GroupArtifact, String> cachedLocalPluginDependencyManagement;

	public Project( File pomFile, boolean isExternal )
	{
		this.pomFile = pomFile;
		this.isExternal = isExternal;
	}

	public void readPomFile() {
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
		{
			parentGav = null;
		}

		String groupId = project.getGroupId() != null ? project.getGroupId() : getParentGav().getGroupId();
		String version = project.getVersion() != null ? project.getVersion() : getParentGav().getVersion();
		if( "${parent.version}".equals( version ) )
			version = getParentGav().getVersion();

		gav = new Gav( groupId, project.getArtifactId(), version );

		if( !gav.isResolved() )
			throw new RuntimeException( "Non resolved project's GAV: " + gav );

		properties = new HashMap<>();
		project.getProperties().forEach( ( key, value ) -> properties.put( key.toString(), value.toString() ) );
	}

	public File getPomFile()
	{
		return pomFile;
	}

	public MavenProject getMavenProject()
	{
		return project;
	}

	public boolean isExternal(){
		return isExternal;
	}

	public boolean isBuildable()
	{
		return !isExternal && pomFile.getParentFile().toPath().resolve( "src" ).toFile().exists();
	}

	public Gav getRawGav()
	{
		return new Gav( project.getModel().getGroupId(), project.getModel().getArtifactId(), project.getModel().getVersion() );
	}

	public Gav getGav()
	{
		return gav;
	}

	public Gav getRawParentGav()
	{
		Parent parent = project.getModel().getParent();
		if( parent == null )
			return null;

		return new Gav( parent.getGroupId(), parent.getArtifactId(), parent.getVersion() );
	}

	public Gav getParentGav()
	{
		return parentGav;
	}

	public Map<String, String> getRawProperties()
	{
		return properties;
	}

	public String interpolateValue( String value, ProjectContainer projects, Log log )
	{
		ValueResolution res = interpolateValueEx( value, projects, log );
		return res.resolved;
	}

	public ValueResolution interpolateValueEx( String value, ProjectContainer projects, Log log )
	{
		if( cachedResolutions != null && cachedResolutions.containsKey( value ) )
			return cachedResolutions.get( value );

		ValueResolution res = new ValueResolution();
		res.raw = value;

		if( value != null )
		{
			int start = value.indexOf( "${" );
			int end = -1;
			if( start >= 0 )
			{
				StringBuilder sb = new StringBuilder();

				while( start >= 0 )
				{
					if( start > end + 1 )
						sb.append( value.substring( end + 1, start ) );

					end = value.indexOf( "}", start );

					String propertyReference = value.substring( start + 2, end );
					String propertyResolved = resolveProperty( log, propertyReference, projects );
					if( res.properties == null )
						res.properties = new HashMap<>();
					else
						res.properties.size();
					res.properties.put( propertyReference, propertyResolved );
					sb.append( propertyResolved );
					sb.append( value.substring( end + 1 ) );

					start = value.indexOf( "${", end + 1 );
				}

				if( end < value.length() - 1 )
					sb.append( value.substring( end + 1 ) );

				value = sb.toString();
			}
		}

		res.resolved = value;

		if( cachedResolutions == null )
			cachedResolutions = new HashMap<>();
		cachedResolutions.put( value, res );

		return res;
	}

	public Gav interpolateGav( Gav gav, ProjectContainer projects, Log log )
	{
		String groupId = interpolateValue( gav.getGroupId(), projects, log );
		String artifactId = interpolateValue( gav.getArtifactId(), projects, log );
		String version = interpolateValue( gav.getVersion(), projects, log );

		return new Gav( groupId, artifactId, version );
	}

	public Map<DependencyKey, Dependency> getInterpolatedDependencyManagement( ProjectContainer projects, Log log )
	{
		if( dependencyManagement == null )
		{
			dependencyManagement = new HashMap<>();

			if( project.getDependencyManagement() != null && project.getDependencyManagement().getDependencies() != null )
			{
				for( org.apache.maven.model.Dependency d : project.getDependencyManagement().getDependencies() )
				{
					String groupId = interpolateValue( d.getGroupId(), projects, log );
					String artifactId = interpolateValue( d.getArtifactId(), projects, log );
					String version = interpolateValue( d.getVersion(), projects, log );
					Scope scope = Scope.fromString( interpolateValue( d.getScope(), projects, log ) );
					String classifier = interpolateValue( d.getClassifier(), projects, log );
					String type = interpolateValue( d.getType(), projects, log );

					Gav dependencyGav = new Gav( groupId, artifactId, version );
					Dependency dependency = new Dependency( dependencyGav, scope, classifier, type );

					dependencyManagement.put( dependency.key(), dependency );
				}
			}
		}

		return dependencyManagement;
	}

	/**
	 * Declared dependencies with values resolved
	 */
	public Set<Dependency> getInterpolatedDependencies( ProjectContainer projects, Log log )
	{
		if( dependencies == null )
		{
			dependencies = new HashSet<>();

			for( org.apache.maven.model.Dependency d : project.getDependencies() )
			{
				String groupId = interpolateValue( d.getGroupId(), projects, log );
				String artifactId = interpolateValue( d.getArtifactId(), projects, log );
				String version = interpolateValue( d.getVersion(), projects, log );
				Scope scope = Scope.fromString( interpolateValue( d.getScope(), projects, log ) );
				String classifier = interpolateValue( d.getClassifier(), projects, log );
				String type = interpolateValue( d.getType(), projects, log );

				dependencies.add( new Dependency( new Gav( groupId, artifactId, version ), scope, classifier, type ) );
			}
		}
		return dependencies;
	}

	public Set<Gav> getInterpolatedPluginDependencies( Map<String, Profile> profiles, ProjectContainer projects, Log log )
	{
		if( pluginDependencies == null )
		{
			pluginDependencies = new HashSet<>();

			for( Plugin plugin : project.getBuildPlugins() )
			{
				Gav rawGav = new Gav( plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion() );
				pluginDependencies.add( interpolateGav( rawGav, projects, log ) );
			}

			List<org.apache.maven.model.Profile> projectProfiles = getMavenProject().getModel().getProfiles();
			if( projectProfiles != null )
			{
				projectProfiles.stream().filter( p -> isProfileActivated( profiles, p ) ).filter( p -> p.getBuild() != null ).filter( p -> p.getBuild().getPlugins() != null ).map( p -> p.getBuild().getPlugins() ).forEach( plugins -> plugins.stream().forEach( plugin -> {
					Gav ramGav = new Gav( plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion() );
					pluginDependencies.add( interpolateGav( ramGav, projects, log ) );
				} ) );
			}
		}

		return pluginDependencies;
	}

	public Set<Gav> getLocalPluginDependencies( Map<String, Profile> profiles, ProjectContainer projects, Log log )
	{
		Set<Gav> interpolated = getInterpolatedPluginDependencies( profiles, projects, log );
		Set<Gav> result = new HashSet<>();

		for( Gav i : interpolated )
		{
			if( i.getVersion() != null )
			{
				result.add( i );
			}
			else
			{
				String version = getHierarchicalPluginDependencyManagement( null, profiles, projects, log ).get( new GroupArtifact( i.getGroupId(), i.getArtifactId() ) );
				if( version == null )
					log.html( Tools.warningMessage( "unresolvable plugin dependency to " + i + " in project " + this ) );

				result.add( new Gav( i.getGroupId(), i.getArtifactId(), version ) );
			}
		}

		return result;
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

	public Map<DependencyKey, DependencyManagement> getHierarchicalDependencyManagement( Map<DependencyKey, DependencyManagement> result, Map<String, Profile> profiles, ProjectContainer projects, Log log )
	{
		if( cachedLocalDependencyManagement == null )
		{
			cachedLocalDependencyManagement = new HashMap<>();

			Project current = this;
			while( current != null )
			{
				current.getInterpolatedDependencyManagementWithBomImport( cachedLocalDependencyManagement, profiles, projects, log );
				current = projects.getParentProject( current );
			}
		}

		if( result == null )
			result = new HashMap<>();

		result.putAll( cachedLocalDependencyManagement );

		return result;
	}

	public Map<DependencyKey, RawDependency> getLocalDependencies( Map<DependencyKey, RawDependency> res, Map<String, Profile> profiles, ProjectContainer projects, Log log )
	{
		res = completeDependenciesMap( res, getMavenProject().getDependencies(), profiles, projects, log );
		Map<DependencyKey, RawDependency> fRes = res;

		List<org.apache.maven.model.Profile> projectProfiles = getMavenProject().getModel().getProfiles();
		if( projectProfiles != null )
			projectProfiles.stream().filter( p -> isProfileActivated( profiles, p ) ).forEach( p -> completeDependenciesMap( fRes, p.getDependencies(), profiles, projects, log ) );

		return res;
	}

	public Map<GroupArtifact, String> getHierarchicalPluginDependencyManagement( Map<GroupArtifact, String> result, Map<String, Profile> profiles, ProjectContainer projects, Log log )
	{
		if( cachedLocalPluginDependencyManagement == null )
		{
			cachedLocalPluginDependencyManagement = new HashMap<>();

			Project current = this;
			while( current != null )
			{
				current.getInterpolatedPluginDependencyManagement( cachedLocalPluginDependencyManagement, profiles, projects, log );
				current = projects.getParentProject( current );
			}
		}

		if( result == null )
			result = new HashMap<>();

		result.putAll( cachedLocalPluginDependencyManagement );

		return result;
	}

	/**
	 * TODO should use depmngt from the parent to resolve values if missing
	 */
	private Map<DependencyKey, DependencyManagement> getInterpolatedDependencyManagementWithBomImport( Map<DependencyKey, DependencyManagement> dependencyMap, Map<String, Profile> profiles, ProjectContainer projects, Log log )
	{
		if( project.getDependencyManagement() != null && project.getDependencyManagement().getDependencies() != null )
		{
			if( dependencyMap == null )
				dependencyMap = new HashMap<>();

			completeDependencyManagementMap( dependencyMap, project.getDependencyManagement().getDependencies(), profiles, projects, log );
		}

		List<org.apache.maven.model.Profile> projectProfiles = getMavenProject().getModel().getProfiles();
		if( projectProfiles != null )
		{
			if( dependencyMap == null )
				dependencyMap = new HashMap<>();

			final Map<DependencyKey, DependencyManagement> dependencyMapFinal = dependencyMap;

			projectProfiles.stream()
					.filter( p -> isProfileActivated( profiles, p ) )
					.filter( p -> p.getDependencyManagement() != null )
					.filter( p -> p.getDependencyManagement().getDependencies() != null )
					.map( p -> p.getDependencyManagement().getDependencies() )
					.map( dependencies -> completeDependencyManagementMap( dependencyMapFinal, dependencies, profiles, projects, log ) );
		}

		return dependencyMap;
	}

	/**
	 * TODO should use depmngt from the parent to resolve values if missing
	 */
	private Map<GroupArtifact, String> getInterpolatedPluginDependencyManagement( Map<GroupArtifact, String> dependencyMap, Map<String, Profile> profiles, ProjectContainer projects, Log log )
	{
		if( project.getPluginManagement() != null && project.getPluginManagement().getPlugins() != null )
		{
			if( dependencyMap == null )
				dependencyMap = new HashMap<>();

			completePluginDependencyManagementMap( dependencyMap, project.getPluginManagement().getPlugins(), projects, log );
		}

		// TODO Is there really nothing in profiles for Plugin Management ?

		return dependencyMap;
	}

	private Map<GroupArtifact, String> completePluginDependencyManagementMap( Map<GroupArtifact, String> result, List<org.apache.maven.model.Plugin> plugins, ProjectContainer projects, Log log )
	{
		if( plugins != null )
		{
			for( org.apache.maven.model.Plugin d : plugins )
			{
				String groupId = interpolateValue( d.getGroupId(), projects, log );
				String artifactId = interpolateValue( d.getArtifactId(), projects, log );
				String version = interpolateValue( d.getVersion(), projects, log );

				if( version == null )
					continue;

				assert groupId != null;
				assert artifactId != null;

				GroupArtifact key = new GroupArtifact( groupId, artifactId );
				if( result != null && result.containsKey( key ) )
					continue;

				if( result == null )
					result = new HashMap<>();

				result.put( key, version );
			}
		}

		return result;
	}

	private Map<DependencyKey, RawDependency> completeDependenciesMap( Map<DependencyKey, RawDependency> res, List<org.apache.maven.model.Dependency> dependencies, Map<String, Profile> profiles, ProjectContainer projects, Log log )
	{
		if( dependencies != null )
		{
			for( org.apache.maven.model.Dependency d : dependencies )
			{
				String groupId = interpolateValue( d.getGroupId(), projects, log );
				String artifactId = interpolateValue( d.getArtifactId(), projects, log );
				String classifier = interpolateValue( d.getClassifier(), projects, log );
				String type = interpolateValue( d.getType(), projects, log );

				DependencyKey key = new DependencyKey( groupId, artifactId, classifier, type );
				if( res != null && res.containsKey( key ) )
					continue;

				String version = interpolateValue( d.getVersion(), projects, log );
				Scope scope = Scope.fromString( interpolateValue( d.getScope(), projects, log ) );

				if( version == null || scope == null )
				{
					Map<DependencyKey, DependencyManagement> mngt = getHierarchicalDependencyManagement( null, profiles, projects, log );
					DependencyManagement dm = mngt.get( key );

					if( version == null && (dm == null || dm.getVs() == null) )
						log.html( Tools.warningMessage( "missing version and version not found in depencency management for dependency to " + key + " in project " + this ) );

					if( version == null && dm != null )
						version = dm.getVs().getVersion();

					if( scope == null )
						scope = dm != null ? dm.getVs().getScope() : Scope.COMPILE;
				}

				RawDependency raw = new RawDependency( new VersionScope( version, scope ), d.isOptional() );
				if( d.getExclusions() != null && !d.getExclusions().isEmpty() )
				{
					for( Exclusion exclusion : d.getExclusions() )
					{
						String excludedGroupId = interpolateValue( exclusion.getGroupId(), projects, log );
						String excludedArtifactId = interpolateValue( exclusion.getArtifactId(), projects, log );
						raw.addExclusion( new GroupArtifact( excludedGroupId, excludedArtifactId ) );
					}
				}

				if( res == null )
					res = new HashMap<>();
				res.put( key, raw );
			}
		}
		return res;
	}

	/**
	 * For the dependencies given,
	 * 
	 * <p>
	 * if they are not already present in the map,
	 * <ul>
	 * <li>interpolate,
	 * <li>if it is a bom import, import it as well
	 */
	private Map<DependencyKey, DependencyManagement> completeDependencyManagementMap( Map<DependencyKey, DependencyManagement> result, List<org.apache.maven.model.Dependency> dependencies, Map<String, Profile> profiles, ProjectContainer projects, Log log )
	{
		if( dependencies != null )
		{
			List<Gav> importedBoms = new ArrayList<>();

			for( org.apache.maven.model.Dependency d : dependencies )
			{
				String groupId = interpolateValue( d.getGroupId(), projects, log );
				String artifactId = interpolateValue( d.getArtifactId(), projects, log );
				String version = interpolateValue( d.getVersion(), projects, log );
				Scope scope = Scope.fromString( interpolateValue( d.getScope(), projects, log ) );
				String classifier = interpolateValue( d.getClassifier(), projects, log );
				String type = interpolateValue( d.getType(), projects, log );

				assert groupId != null;
				assert artifactId != null;
				assert type != null;

				DependencyKey key = new DependencyKey( groupId, artifactId, classifier, type );
				if( result != null && result.containsKey( key ) )
					continue;

				if( scope == Scope.IMPORT )
				{
					assert version != null;

					importedBoms.add( new Gav( groupId, artifactId, version ) );
				}

				DependencyManagement mngt = new DependencyManagement( new VersionScope( version, scope ) );

				if( d.getExclusions() != null && !d.getExclusions().isEmpty() )
				{
					for( Exclusion exclusion : d.getExclusions() )
					{
						String excludedGroupId = interpolateValue( exclusion.getGroupId(), projects, log );
						String excludedArtifactId = interpolateValue( exclusion.getArtifactId(), projects, log );
						mngt.addExclusion( new GroupArtifact( excludedGroupId, excludedArtifactId ) );
					}
				}

				if( result == null )
					result = new HashMap<>();

				result.put( key, mngt );
			}

			for( Gav bomGav : importedBoms )
			{
				Project bomProject = projects.forGav( bomGav );
				if( bomProject == null )
				{
					log.html( Tools.errorMessage( "missing project " + bomGav + ", dependency management resolution won't be exact for project " + this ) );
					continue;
				}

				result = bomProject.getHierarchicalDependencyManagement( result, profiles, projects, log );
			}
		}

		return result;
	}

	private MavenProject readPomFile( File pom )
	{
		try( FileReader reader = new FileReader( pom ) )
		{
			MavenXpp3Reader mavenReader = new MavenXpp3Reader();
			Model model = mavenReader.read( reader );
			model.setPomFile( pom );

			return new MavenProject( model );
		}
		catch( IOException | XmlPullParserException e )
		{
			return null;
		}
	}

	private boolean isProfileActivated( Map<String, Profile> profiles, org.apache.maven.model.Profile p )
	{
		if( profiles == null )
			return false;

		return profiles.keySet().contains( p.getId() ) || (p.getActivation() != null && p.getActivation().isActiveByDefault());
	}

	private String resolveProperty( Log log, String propertyName, ProjectContainer projects )
	{
		PropertyLocation propertyDefinition = getPropertyDefinition( log, propertyName, true, projects );
		if( propertyDefinition == null )
		{
			if( log != null )
				log.html( Tools.warningMessage( "cannot resolve property '" + propertyName + "' in project " + toString() ) );
			else
				System.out.println( "cannot resolve property '" + propertyName + "' in project " + toString() );
			return null;
		}

		if( isMavenVariable( propertyDefinition.getPropertyValue() ) )
			return propertyDefinition.getProject().resolveProperty( log, propertyDefinition.getPropertyValue(), projects );

		return propertyDefinition.getPropertyValue();
	}

	private PropertyLocation getPropertyDefinition( Log log, String propertyName, boolean online, ProjectContainer projects )
	{
		String originalRequestedPropertyName = propertyName;

		if( isMavenVariable( propertyName ) )
			propertyName = Tools.getPropertyNameFromPropertyReference( propertyName );

		String value = properties.get( propertyName );
		if( value != null )
			return new PropertyLocation( this, null, propertyName, value );

		switch( propertyName )
		{
			case "version":
				log.html( Tools.warningMessage( "illegal property 'version' used in the project " + toString() + ", value resolved to project's version." ) );
			case "project.version":
			case "pom.version":
				return new PropertyLocation( this, null, "project.version", gav.getVersion() );

			case "groupId":
			case "@project.groupId@":
				log.html( Tools.warningMessage( "illegal property '" + propertyName + "' used in the project " + toString() + ", value resolved to project's groupId." ) );
			case "project.groupId":
			case "pom.groupId":
				return new PropertyLocation( this, null, "project.groupId", gav.getGroupId() );

			case "artifactId":
				log.html( Tools.warningMessage( "illegal property 'artifactId' used in the project " + toString() + ", value resolved to project's artifactId." ) );
			case "project.artifactId":
			case "pom.artifactId":
				return new PropertyLocation( this, null, "project.artifactId", gav.getArtifactId() );

			case "project.prerequisites.maven":
				if( project.getPrerequisites() != null )
					return new PropertyLocation( this, null, "project.prerequisites.maven", project.getPrerequisites().getMaven() );
				break;

			case "mavenVersion":
				return new PropertyLocation( this, null, propertyName, "3.1.1" );

			case "java.version":
				return new PropertyLocation( this, null, propertyName, propertyName );
		}

		if( parentGav != null )
		{
			Project parentProject = projects.forGav( parentGav );
			if( parentProject != null )
			{
				if( propertyName.startsWith( "project.parent." ) )
					propertyName = propertyName.replace( "project.parent.", "project." );

				return parentProject.getPropertyDefinition( log, propertyName, online, projects );
			}
			else
			{
				log.html( Tools.warningMessage( "cannot find parent project to resolve property '" + originalRequestedPropertyName + "' in project " + toString() ) );
			}
		}

		return null;
	}
}
