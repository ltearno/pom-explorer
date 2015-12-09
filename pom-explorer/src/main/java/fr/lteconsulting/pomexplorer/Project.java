package fr.lteconsulting.pomexplorer;

import static fr.lteconsulting.pomexplorer.Tools.isMavenVariable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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

/**
 * A POM project information
 * 
 * levels of accessors :
 * <ol>
 * <li>raw : how the thing is declared in maven pom file
 * <li>declared : how the thing is declared with variables resolved
 * <li>local : how the thing is resolved with the local hierarchy (parents)
 * <li>transitive : how the thing is resolves including transitive dependency processing
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

	public Map<DependencyKey, Dependency> getDependencyManagement( Log log )
	{
		if( dependencyManagement == null )
		{
			dependencyManagement = new HashMap<>();

			if( project.getDependencyManagement() != null && project.getDependencyManagement().getDependencies() != null )
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

	public Set<Gav> getPluginDependencies( Log log )
	{
		if( pluginDependencies == null )
		{
			pluginDependencies = new HashSet<>();

			for( Plugin plugin : project.getBuildPlugins() )
			{
				Gav unresolvedGav = new Gav( plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion() );
				pluginDependencies.add( resolveGav( unresolvedGav, log ) );
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

	public String resolveProperty( Log log, String propertyName )
	{
		PropertyLocation propertyDefinition = getPropertyDefinition( log, propertyName, true );
		if( propertyDefinition == null )
		{
			log.html( Tools.warningMessage( "cannot resolve property '" + propertyName + "' in project " + toString() ) );
			return null;
		}

		if( isMavenVariable( propertyDefinition.getPropertyValue() ) )
			return propertyDefinition.getProject().resolveProperty( log, propertyDefinition.getPropertyValue() );

		return propertyDefinition.getPropertyValue();
	}

	public String resolveValue( Log log, String value )
	{
		if( value == null )
			return null;
		if( isMavenVariable( value ) )
			return resolveProperty( log, value );
		return value;
	}

	public Gav resolveGav( Gav gav, Log log )
	{
		String groupId = resolveValue( log, gav.getGroupId() );
		String artifactId = resolveValue( log, gav.getArtifactId() );
		String version = resolveValue( log, gav.getVersion() );

		if( version == null )
		{
			log.html( Tools.warningMessage( "unspecified dependency version to " + groupId + ":" + artifactId + " in project '" + this.gav + "', check the pom file please" ) );

			// find a way to handle those versions :
			// "org.apache.maven.plugins:maven-something-plugin":
		}

		return new Gav( groupId, artifactId, version );
	}

	public boolean isBuildable()
	{
		return !isExternal && pomFile.getParentFile().toPath().resolve( "src" ).toFile().exists();
	}

	public Set<Gav> getMissingGavsForResolution( Log log )
	{
		return getMissingGavsForResolution( log, null );
	}

	public Set<Gav> getMissingGavsForResolution( Log log, Set<Gav> gavs )
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
				Set<Gav> missingGavs = parentProject.getMissingGavsForResolution( log );
				if( missingGavs != null )
					gavs.addAll( missingGavs );
			}
		}

		if( project.getDependencyManagement() != null && project.getDependencyManagement().getDependencies() != null )
		{
			for( org.apache.maven.model.Dependency d : project.getDependencyManagement().getDependencies() )
			{
				if( "import".equals( d.getScope() ) && "pom".equals( d.getType() ) )
				{
					String version;
					if( isMavenVariable( d.getVersion() ) )
						version = resolveProperty( log, d.getVersion() );
					else
						version = d.getVersion();

					Gav bomGav = resolveGav( new Gav( d.getGroupId(), d.getArtifactId(), version ), log );

					Project bomProject = session.projects().forGav( bomGav );
					if( bomProject == null )
					{
						if( gavs == null )
							gavs = new HashSet<>();

						gavs.add( bomGav );
					}
					else
					{
						bomProject.getMissingGavsForResolution( log, gavs );
					}
				}
			}
		}

		return gavs;
	}

	public Map<DependencyKey, DependencyManagement> getLocalDependencyManagement( Map<DependencyKey, DependencyManagement> result, boolean online, Log log )
	{
		Project current = this;
		while( current != null )
		{
			result = current.getDeclaredDependencyManagement( result, online, log );

			current = current.getParentProject( online, log );
		}

		return result;
	}

	public Map<DependencyKey, DependencyManagement> getDeclaredDependencyManagement( Map<DependencyKey, DependencyManagement> result, boolean online, Log log )
	{
		if( project.getDependencyManagement() != null && project.getDependencyManagement().getDependencies() != null )
		{
			for( org.apache.maven.model.Dependency d : project.getDependencyManagement().getDependencies() )
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

					Project bomProject = session.projects().fetchProject( new Gav( groupId, artifactId, version ), online, log );
					if( bomProject == null )
					{
						log.html( Tools.errorMessage( "cannot fetch the project " + groupId + ":" + artifactId + ":" + version + ", dependency resolution won't be exact" ) );
						continue;
					}
					result = bomProject.getLocalDependencyManagement( result, online, log );
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

	public Map<DependencyKey, RawDependency> getLocalDependencies( Map<DependencyKey, RawDependency> res, boolean online, Log log )
	{
		Project current = this;

		while( current != null )
		{
			res = current.getDeclaredDependencies( res, log );

			current = current.getParentProject( online, log );
		}

		return res;
	}

	public Map<DependencyKey, RawDependency> getDeclaredDependencies( Map<DependencyKey, RawDependency> res, Log log )
	{
		for( org.apache.maven.model.Dependency d : getMavenProject().getDependencies() )
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

		return res;
	}

	public DependencyNode getDependencyTree( boolean full, boolean online, Log log )
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
		buildDependencyTree( nodeQueue, full, online, session, log );

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
				log.html( Tools.warningMessage( "cannot find parent project to resolve property '" + originalRequestedPropertyName + "' in project " + toString() ) );
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

	private void buildDependencyTree( Queue<DependencyNode> nodeQueue, boolean full, boolean online, Session session, Log log )
	{
		int neededLevels = full ? -1 : 1;

		while( !nodeQueue.isEmpty() )
		{
			DependencyNode node = nodeQueue.poll();

			if( neededLevels >= 0 && node.getLevel() > neededLevels )
				continue;

			node.collectDependencyManagement( online, log );

			Map<DependencyKey, RawDependency> localDependencies = node.getProject().getLocalDependencies( null, online, log );

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

				DependencyNode existingNode = node.getRootNode().getForGroupArtifact( ga );
				if( existingNode != null )
				{
					if( existingNode.getLevel() <= node.getLevel() + 1 )
						continue;
					else
						existingNode.remove();
				}

				String version = null;
				Scope scope = null;

				DependencyManagement dependencyManagement = node.getTopLevelManagement( dependencyKey );
				DependencyManagement localManagement = node.getLocalManagement( dependencyKey );
				if( dependencyManagement != null && dependencyManagement.getVs().getVersion() != null )
				{
					// si le top level management est le notre, c'est la version déclarée qui prend le pas
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

				Gav dependencyGav = new Gav( dependencyKey.getGroupId(), dependencyKey.getArtifactId(), version );
				Project childProject = session.projects().fetchProject( dependencyGav, online, log );
				if( childProject == null )
				{
					// TODO : use specified repositories if needed !
					if( dependency.isOptional() )
						log.html( Tools.warningMessage( "cannot fetch project " + dependencyGav + " referenced in " + node.getProject() + " (this is an optional dependency)" ) );
					else
						log.html( Tools.errorMessage( "cannot fetch project " + dependencyGav + " referenced in " + node.getProject() ) );
					continue;
				}

				DependencyNode child = new DependencyNode( childProject, dependencyKey, new VersionScope( version, scope ) );
				child.setParent( node );
				child.addExclusions( dependency.getExclusions() );

				DependencyManagement dm = node.getLocalManagement( dependencyKey );
				if( dm != null )
					child.addExclusions( dm.getExclusions() );

				if( scope == Scope.SYSTEM )
					continue;

				// TODO it seems to me that transitive dependency policy only applies to jar artifacts, is that true ??
				// if( "jar".equals( dependencyKey.getType() ) )
				nodeQueue.add( child );
			}
		}
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
}
