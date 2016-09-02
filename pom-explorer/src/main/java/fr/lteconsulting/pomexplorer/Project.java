package fr.lteconsulting.pomexplorer;

import static fr.lteconsulting.pomexplorer.Tools.isMavenVariable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
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
import fr.lteconsulting.pomexplorer.model.transitivity.DependencyNode;
import fr.lteconsulting.pomexplorer.model.transitivity.RawDependency;
import fr.lteconsulting.pomexplorer.model.transitivity.Repository;

/**
 * A POM project information
 * 
 * levels of accessors :
 * <ol>
 * <li>raw : how the thing is declared in maven pom file
 * <li>declared : how the thing is declared with variables resolved
 * <li>local : how the thing is resolved with the local hierarchy (parents)
 * <li>transitive : how the thing is resolves including transitive dependency
 * processing
 * 
 * @author Arnaud
 */
public class Project
{
	private final Session session;
	private final File pomFile;
	private final boolean isExternal;

	private final MavenProject project;

	private final Gav parentGav;
	private final Gav gav;
	private final Map<String, String> properties;

	private Map<DependencyKey, Dependency> dependencyManagement;
	private Set<Dependency> dependencies;
	private Set<Gav> pluginDependencies;

	private DependencyNode partialTree = null;
	private DependencyNode fullTree = null;

	public static final Comparator<Project> alphabeticalComparator = ( a, b ) -> a.toString().compareTo( b.toString() );

	public Project( Session session, File pomFile, boolean isExternal ) throws Exception
	{
		this.session = session;
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
		{
			parentGav = null;
		}

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

	public Gav getGav()
	{
		return gav;
	}

	public Gav getDeclaredGav()
	{
		return new Gav( project.getModel().getGroupId(), project.getModel().getArtifactId(),
				project.getModel().getVersion() );
	}

	public Gav getDeclaredParentGav()
	{
		Parent parent = project.getModel().getParent();
		if( parent == null )
			return null;

		return new Gav( parent.getGroupId(), parent.getArtifactId(), parent.getVersion() );
	}

	public Gav getParent()
	{
		return parentGav;
	}

	public Project getParentProject()
	{
		if( parentGav == null )
			return null;

		return session.projects().forGav( parentGav );
	}

	public File getPomFile()
	{
		return pomFile;
	}

	public MavenProject getMavenProject()
	{
		return project;
	}

	public Map<String, String> getProperties()
	{
		return properties;
	}

	public Map<DependencyKey, Dependency> getDependencyManagement( Log log )
	{
		if( dependencyManagement == null )
		{
			dependencyManagement = new HashMap<>();

			if( project.getDependencyManagement() != null
					&& project.getDependencyManagement().getDependencies() != null )
			{
				for( org.apache.maven.model.Dependency d : project.getDependencyManagement().getDependencies() )
				{
					String groupId = resolveValue( log, d.getGroupId() );
					String artifactId = resolveValue( log, d.getArtifactId() );
					String version = resolveValue( log, d.getVersion() );
					Scope scope = Scope.fromString( resolveValue( log, d.getScope() ) );
					String classifier = resolveValue( log, d.getClassifier() );
					String type = resolveValue( log, d.getType() );

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
	 * 
	 * @param log
	 * @return
	 */
	public Set<Dependency> getDependencies( Log log )
	{
		if( dependencies == null )
		{
			dependencies = new HashSet<>();

			for( org.apache.maven.model.Dependency d : project.getDependencies() )
			{
				String groupId = resolveValue( log, d.getGroupId() );
				String artifactId = resolveValue( log, d.getArtifactId() );
				String version = resolveValue( log, d.getVersion() );
				Scope scope = Scope.fromString( resolveValue( log, d.getScope() ) );
				String classifier = resolveValue( log, d.getClassifier() );
				String type = resolveValue( log, d.getType() );

				dependencies.add( new Dependency( new Gav( groupId, artifactId, version ), scope, classifier, type ) );
			}
		}
		return dependencies;
	}

	public Set<Gav> getPluginDependencies( Map<String, Profile> profiles, Log log )
	{
		if( pluginDependencies == null )
		{
			pluginDependencies = new HashSet<>();

			for( Plugin plugin : project.getBuildPlugins() )
			{
				Gav unresolvedGav = new Gav( plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion() );
				pluginDependencies.add( resolveGav( unresolvedGav, log ) );
			}

			List<org.apache.maven.model.Profile> projectProfiles = getMavenProject().getModel().getProfiles();
			if( projectProfiles != null )
			{
				projectProfiles.stream()
						.filter( p -> isProfileActivated( profiles, p ) )
						.filter( p -> p.getBuild() != null )
						.filter( p -> p.getBuild().getPlugins() != null )
						.map( p -> p.getBuild().getPlugins() )
						.forEach( plugins -> plugins.stream().forEach( plugin -> {
							Gav unresolvedGav = new Gav( plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion() );
							pluginDependencies.add( resolveGav( unresolvedGav, log ) );
						} ) );
			}
		}

		return pluginDependencies;
	}

	public Project getPropertyDefinitionProject( String property )
	{
		if( property.startsWith( "project." ) )
			return this;

		if( properties.containsKey( property ) )
			return this;

		Project parentProject = null;
		if( parentGav != null )
			parentProject = session.projects().forGav( parentGav );

		if( parentProject != null )
		{
			Project definition = parentProject.getPropertyDefinitionProject( property );
			if( definition != null )
				return definition;
		}

		return null;
	}

	private String resolveProperty( Log log, String propertyName )
	{
		PropertyLocation propertyDefinition = getPropertyDefinition( log, propertyName, true );
		if( propertyDefinition == null )
		{
			if( log != null )
				log.html( Tools
						.warningMessage( "cannot resolve property '" + propertyName + "' in project " + toString() ) );
			else
				System.out.println( "cannot resolve property '" + propertyName + "' in project " + toString() );
			return null;
		}

		if( isMavenVariable( propertyDefinition.getPropertyValue() ) )
			return propertyDefinition.getProject().resolveProperty( log, propertyDefinition.getPropertyValue() );

		return propertyDefinition.getPropertyValue();
	}

	public static class ValueResolution
	{
		private String raw;
		private String resolved;
		private Map<String, String> properties;

		public String getRaw()
		{
			return raw;
		}

		public String getResolved()
		{
			return resolved;
		}

		public Map<String, String> getProperties()
		{
			return properties;
		}
	}

	private Map<String, ValueResolution> cachedResolutions;

	public ValueResolution resolveValueEx( Log log, String value )
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
					String propertyResolved = resolveProperty( log, propertyReference );
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

	public String resolveValue( Log log, String value )
	{
		ValueResolution res = resolveValueEx( log, value );
		return res.resolved;
	}

	public Gav resolveGav( Gav gav, Log log )
	{
		String groupId = resolveValue( log, gav.getGroupId() );
		String artifactId = resolveValue( log, gav.getArtifactId() );
		String version = resolveValue( log, gav.getVersion() );

		if( version == null )
		{
			log.html( Tools.warningMessage( "unspecified dependency version to " + groupId + ":" + artifactId
					+ " in project '" + this.gav + "', check the pom file please" ) );

			// find a way to handle those versions :
			// "org.apache.maven.plugins:maven-something-plugin":
		}

		return new Gav( groupId, artifactId, version );
	}

	public boolean isBuildable()
	{
		return !isExternal && pomFile.getParentFile().toPath().resolve( "src" ).toFile().exists();
	}

	public boolean fetchMissingGavsForResolution( boolean online, Log log, Set<Project> fetchedProjects )
	{
		boolean ok = true;

		if( parentGav != null )
		{
			if( !session.projects().contains( parentGav ) )
			{
				Project parentProject = session.projects().fetchProject( parentGav, online, log );
				if( parentProject != null )
					fetchedProjects.add( parentProject );
				if( parentProject == null
						|| !parentProject.fetchMissingGavsForResolution( online, log, fetchedProjects ) )
				{
					ok = false;
					log.html( Tools.errorMessage( "cannot resolve project " + toString()
							+ " due to:<br/>&nbsp;&nbsp;&nbsp;missing parent project " + parentGav ) );
				}
			}
		}

		if( project.getDependencyManagement() != null && project.getDependencyManagement().getDependencies() != null )
		{
			for( org.apache.maven.model.Dependency d : project.getDependencyManagement().getDependencies() )
			{
				if( "import".equals( d.getScope() ) && "pom".equals( d.getType() ) )
				{
					Gav bomGav = resolveGav( new Gav( d.getGroupId(), d.getArtifactId(), d.getVersion() ), log );
					if( !session.projects().contains( bomGav ) )
					{
						Project bomProject = session.projects().fetchProject( bomGav, online, log );
						if( bomProject != null )
							fetchedProjects.add( bomProject );
						if( bomProject == null
								|| !bomProject.fetchMissingGavsForResolution( online, log, fetchedProjects ) )
						{
							ok = false;
							log.html( Tools.errorMessage( "cannot resolve project " + toString()
									+ " due to:<br/>&nbsp;&nbsp;&nbsp;missing bom import " + bomGav ) );
						}
					}
				}
			}
		}

		return ok;
	}

	private Map<DependencyKey, DependencyManagement> cachedLocalDependencyManagement;

	public Map<DependencyKey, DependencyManagement> getLocalDependencyManagement(
			Map<DependencyKey, DependencyManagement> result, boolean online, Map<String, Profile> profiles, Log log )
	{
		if( result == null && cachedLocalDependencyManagement != null )
			return cachedLocalDependencyManagement;

		boolean storeInCache = result == null;

		Project current = this;
		while( current != null )
		{
			result = current.getDeclaredDependencyManagement( result, online, profiles, log );

			current = current.getParentProject( online, log );
		}

		if( storeInCache )
			cachedLocalDependencyManagement = result;

		return result;
	}

	private Map<DependencyKey, DependencyManagement> completeDependencyManagementMap(
			Map<DependencyKey, DependencyManagement> result, List<org.apache.maven.model.Dependency> dependencies,
			boolean online, Map<String, Profile> profiles, Log log )
	{

		if( dependencies != null )
		{
			for( org.apache.maven.model.Dependency d : dependencies )
			{
				String groupId = resolveValue( log, d.getGroupId() );
				String artifactId = resolveValue( log, d.getArtifactId() );
				String version = resolveValue( log, d.getVersion() );
				Scope scope = Scope.fromString( resolveValue( log, d.getScope() ) );
				String classifier = resolveValue( log, d.getClassifier() );
				String type = resolveValue( log, d.getType() );

				assert groupId != null;
				assert artifactId != null;
				assert type != null;

				DependencyKey key = new DependencyKey( groupId, artifactId, classifier, type );
				if( result != null && result.containsKey( key ) )
					continue;

				if( scope == Scope.IMPORT )
				{
					// importer le bom
					assert version != null;

					Project bomProject = session.projects().fetchProject( new Gav( groupId, artifactId, version ), online,
							log );
					if( bomProject == null )
					{
						log.html( Tools.errorMessage( "cannot fetch the project " + groupId + ":" + artifactId + ":"
								+ version + ", dependency resolution won't be exact" ) );
						continue;
					}
					result = bomProject.getLocalDependencyManagement( result, online, profiles, log );
				}

				DependencyManagement mngt = new DependencyManagement( new VersionScope( version, scope ) );

				if( d.getExclusions() != null && !d.getExclusions().isEmpty() )
				{
					for( Exclusion exclusion : d.getExclusions() )
					{
						String excludedGroupId = resolveValue( log, exclusion.getGroupId() );
						String excludedArtifactId = resolveValue( log, exclusion.getArtifactId() );
						mngt.addExclusion( new GroupArtifact( excludedGroupId, excludedArtifactId ) );
					}
				}

				if( result == null )
					result = new HashMap<>();
				result.put( key, mngt );
			}
		}

		return result;
	}

	public Map<DependencyKey, DependencyManagement> getDeclaredDependencyManagement( Map<DependencyKey, DependencyManagement> dependencyMap, boolean online, Map<String, Profile> profiles, Log log )
	{
		if( project.getDependencyManagement() != null && project.getDependencyManagement().getDependencies() != null )
		{
			if( dependencyMap != null )
				dependencyMap.putAll( completeDependencyManagementMap( dependencyMap, project.getDependencyManagement().getDependencies(), online, profiles, log ) );
		}

		List<org.apache.maven.model.Profile> projectProfiles = getMavenProject().getModel().getProfiles();
		if( projectProfiles != null )
		{
			projectProfiles.stream()
					.filter( p -> isProfileActivated( profiles, p ) )
					.filter( p -> p.getDependencyManagement() != null )
					.filter( p -> p.getDependencyManagement().getDependencies() != null )
					.map( p -> p.getDependencyManagement().getDependencies() )
					.map( dependencies -> completeDependencyManagementMap( dependencyMap, dependencies, online, profiles, log ) )
					.forEach( dependencyMap::putAll );
		}

		return dependencyMap;
	}

	public Map<DependencyKey, RawDependency> getLocalDependencies( Map<DependencyKey, RawDependency> res, boolean online,
			Map<String, Profile> profiles, Log log )
	{
		Project current = this;

		while( current != null )
		{
			res = current.getDeclaredDependencies( res, profiles, log );

			current = current.getParentProject( online, log );
		}

		return res;
	}

	public Map<DependencyKey, RawDependency> getRawDependencies()
	{
		Map<DependencyKey, RawDependency> res = new HashMap<>();

		for( org.apache.maven.model.Dependency d : getMavenProject().getDependencies() )
		{
			DependencyKey key = new DependencyKey( d.getGroupId(), d.getArtifactId(), d.getClassifier(), d.getType() );

			RawDependency raw = new RawDependency( new VersionScope( d.getVersion(), Scope.fromString( d.getScope() ) ),
					d.isOptional() );
			if( d.getExclusions() != null && !d.getExclusions().isEmpty() )
			{
				for( Exclusion exclusion : d.getExclusions() )
					raw.addExclusion( new GroupArtifact( exclusion.getGroupId(), exclusion.getArtifactId() ) );
			}

			res.put( key, raw );
		}

		return res;
	}

	private Map<DependencyKey, RawDependency> completeDependenciesMap( Map<DependencyKey, RawDependency> res,
			List<org.apache.maven.model.Dependency> dependencies, Log log )
	{
		if( dependencies != null )
		{
			for( org.apache.maven.model.Dependency d : dependencies )
			{
				String groupId = resolveValue( log, d.getGroupId() );
				String artifactId = resolveValue( log, d.getArtifactId() );
				String version = resolveValue( log, d.getVersion() );
				Scope scope = Scope.fromString( resolveValue( log, d.getScope() ) );
				String classifier = resolveValue( log, d.getClassifier() );
				String type = resolveValue( log, d.getType() );

				DependencyKey key = new DependencyKey( groupId, artifactId, classifier, type );
				if( res != null && res.containsKey( key ) )
					continue;

				RawDependency raw = new RawDependency( new VersionScope( version, scope ), d.isOptional() );
				if( d.getExclusions() != null && !d.getExclusions().isEmpty() )
				{
					for( Exclusion exclusion : d.getExclusions() )
					{
						String excludedGroupId = resolveValue( log, exclusion.getGroupId() );
						String excludedArtifactId = resolveValue( log, exclusion.getArtifactId() );
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

	public Map<DependencyKey, RawDependency> getDeclaredDependencies( Map<DependencyKey, RawDependency> res,
			Map<String, Profile> profiles, Log log )
	{
		if( res == null )
			res = new HashMap<>();

		res = completeDependenciesMap( res, getMavenProject().getDependencies(), log );
		Map<DependencyKey, RawDependency> fRes = res;

		List<org.apache.maven.model.Profile> projectProfiles = getMavenProject().getModel().getProfiles();
		if( projectProfiles != null )
		{
			projectProfiles.stream()
					.filter( p -> isProfileActivated( profiles, p ) )
					.forEach( p -> completeDependenciesMap( fRes, p.getDependencies(), log ) );
		}

		return res;
	}

	public DependencyNode getDependencyTree( boolean full, boolean online, Map<String, Profile> profiles, Log log )
	{
		if( full && fullTree != null )
			return fullTree;

		if( !full && partialTree != null )
			return partialTree;

		Queue<DependencyNode> nodeQueue = new LinkedList<>();

		DependencyKey gact = new DependencyKey( gav.getGroupId(), gav.getArtifactId(), null, project.getPackaging() );
		VersionScope vs = new VersionScope( gav.getVersion(), Scope.COMPILE );

		DependencyNode rootNode = new DependencyNode( this, gact, vs );
		nodeQueue.add( rootNode );
		buildDependencyTree( nodeQueue, full, online, session, profiles, log );

		if( full )
			fullTree = rootNode;
		else
			partialTree = rootNode;

		return rootNode;
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

	private PropertyLocation getPropertyDefinition( Log log, String propertyName, boolean online )
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
				log.html( Tools.warningMessage( "illegal property 'version' used in the project " + toString()
						+ ", value resolved to project's version." ) );
			case "project.version":
			case "pom.version":
				return new PropertyLocation( this, null, "project.version", gav.getVersion() );

			case "groupId":
			case "@project.groupId@":
				log.html( Tools.warningMessage( "illegal property '" + propertyName + "' used in the project " + toString()
						+ ", value resolved to project's groupId." ) );
			case "project.groupId":
			case "pom.groupId":
				return new PropertyLocation( this, null, "project.groupId", gav.getGroupId() );

			case "artifactId":
				log.html( Tools.warningMessage( "illegal property 'artifactId' used in the project " + toString()
						+ ", value resolved to project's artifactId." ) );
			case "project.artifactId":
			case "pom.artifactId":
				return new PropertyLocation( this, null, "project.artifactId", gav.getArtifactId() );

			case "project.prerequisites.maven":
				if( project.getPrerequisites() != null )
					return new PropertyLocation( this, null, "project.prerequisites.maven",
							project.getPrerequisites().getMaven() );
				break;

			case "mavenVersion":
				return new PropertyLocation( this, null, propertyName, "3.1.1" );

			case "java.version":
				return new PropertyLocation( this, null, propertyName, propertyName );
		}

		if( parentGav != null )
		{
			Project parentProject = session.projects().forGav( parentGav );
			if( parentProject == null )
				parentProject = session.projects().fetchProject( parentGav, online, log );

			if( parentProject != null )
			{
				if( propertyName.startsWith( "project.parent." ) )
					propertyName = propertyName.replace( "project.parent.", "project." );

				return parentProject.getPropertyDefinition( log, propertyName, online );
			}
			else
			{
				log.html( Tools.warningMessage( "cannot find parent project to resolve property '"
						+ originalRequestedPropertyName + "' in project " + toString() ) );
			}
		}

		return null;
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

	private void buildDependencyTree( Queue<DependencyNode> nodeQueue, boolean full, boolean online, Session session,
			Map<String, Profile> profiles, Log log )
	{
		int neededLevels = full ? -1 : 1;

		while( !nodeQueue.isEmpty() )
		{
			DependencyNode node = nodeQueue.poll();

			if( neededLevels >= 0 && node.getLevel() >= neededLevels )
				continue;

			node.collectDependencyManagement( online, profiles, log );

			Map<DependencyKey, RawDependency> localDependencies = node.getProject().getLocalDependencies( null, online, profiles, log );

			if( localDependencies == null )
				continue;
			for( Entry<DependencyKey, RawDependency> e : localDependencies.entrySet() )
			{
				DependencyKey dependencyKey = e.getKey();
				RawDependency dependency = e.getValue();
				if( dependency.isOptional() && !node.isRoot() )
					continue;

				GroupArtifact ga = new GroupArtifact( dependencyKey.getGroupId(), dependencyKey.getArtifactId() );
				if( isGroupArtifactExcluded( node, ga ) )
					continue;

				DependencyNode existingNode = node.searchNodeForGroupArtifact( ga );
				if( existingNode != null )
				{
					if( existingNode.getLevel() <= node.getLevel() + 1 )
						continue;
					else
						existingNode.removeFromParent();
				}

				String version = null;
				Scope scope = null;

				DependencyManagement dependencyManagement = node.getTopLevelManagement( dependencyKey );
				DependencyManagement localManagement = node.getLocalManagement( dependencyKey );
				if( dependencyManagement != null && dependencyManagement.getVs().getVersion() != null )
				{
					// si le top level management est le notre, c'est la version
					// déclarée qui prend le pas
					if( dependencyManagement == localManagement && dependency.getVs().getVersion() != null )
						version = dependency.getVs().getVersion();
					else
						version = dependencyManagement.getVs().getVersion();
				}
				else
				{
					version = dependency.getVs().getVersion();
				}

				if( dependencyManagement != null && dependencyManagement.getVs().getScope() != null )
				{
					if( dependencyManagement == localManagement && dependency.getVs().getScope() != null )
						scope = dependency.getVs().getScope();
					else
						scope = dependencyManagement.getVs().getScope();
				}
				else
				{
					if( node.isRoot() )
					{
						scope = dependency.getVs().getScope();
						if( scope == null )
							scope = Scope.COMPILE;
					}
					else
					{
						scope = Scope.getScopeTransformation( node.getVs().getScope(), dependency.getVs().getScope() );
						if( scope == null )
							continue;
					}
				}

				assert scope != null;
				assert version != null;

				if( scope == Scope.IMPORT || scope == Scope.SYSTEM )
					continue;

				// get remote repositories
				List<Repository> additionalRepos = node.getProject().getProjectRepositories( log );

				Gav dependencyGav = new Gav( dependencyKey.getGroupId(), dependencyKey.getArtifactId(), version );

				Project childProject = null;

				if( neededLevels < 0 || node.getLevel() >= neededLevels )
				{
					childProject = session.projects().fetchProject( dependencyGav, online, additionalRepos, log );
					if( childProject == null )
					{
						// TODO : use specified repositories if needed !
						if( dependency.isOptional() )
							log.html( Tools.warningMessage( "cannot fetch project " + dependencyGav + " referenced in "
									+ node.getProject() + " (this is an optional dependency)" ) );
						else
							log.html( Tools.errorMessage(
									"cannot fetch project " + dependencyGav + " referenced in " + node.getProject() ) );
						continue;
					}
				}

				DependencyNode child = new DependencyNode( childProject, dependencyKey,
						new VersionScope( version, scope ) );
				child.addExclusions( dependency.getExclusions() );

				node.addChild( child );

				DependencyManagement dm = node.getLocalManagement( dependencyKey );
				if( dm != null )
					child.addExclusions( dm.getExclusions() );

				if( scope == Scope.SYSTEM )
					continue;

				// TODO it seems to me that transitive dependency policy only
				// applies to jar artifacts, is that true ??
				// if( "jar".equals( dependencyKey.getType() ) )
				nodeQueue.add( child );
			}
		}
	}

	private List<Repository> getProjectRepositories( Log log )
	{
		List<Repository> res = null;

		Project current = this;
		while( current != null )
		{
			if( current.project.getRepositories() != null && !current.project.getRepositories().isEmpty() )
			{
				if( res == null )
					res = new ArrayList<>();

				for( org.apache.maven.model.Repository r : current.project.getRepositories() )
				{
					res.add( new Repository( r.getId(), r.getUrl() ) );
				}
			}
			current = current.getParentProject( true, log );
		}

		return res;
	}

	private boolean isGroupArtifactExcluded( DependencyNode node, GroupArtifact ga )
	{
		while( node != null )
		{
			if( node.isExcluded( ga ) )
				return true;
			node = node.getParent();
		}

		return false;
	}

	private Project getParentProject( boolean online, Log log )
	{
		if( parentGav == null )
			return null;

		return session.projects().fetchProject( parentGav, online, log );
	}

	private boolean isProfileActivated( Map<String, Profile> profiles, org.apache.maven.model.Profile p )
	{
		return profiles.keySet().contains( p.getId() )
				|| (p.getActivation() != null && p.getActivation().isActiveByDefault());
	}
}
