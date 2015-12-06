package fr.lteconsulting.pomexplorer;

import static fr.lteconsulting.pomexplorer.Tools.isMavenVariable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

/**
 * A POM project information
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

	private TransitiveDependencies transitiveDependencies;

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

	public TransitiveDependencies getTransitiveDependencies( boolean fetchMissingProjects, Log log )
	{
		if( transitiveDependencies != null )
			return transitiveDependencies;

		transitiveDependencies = new TransitiveDependencies();

		if( transitiveDependencies.visitedProjects.contains( this ) )
			return null;
		transitiveDependencies.visitedProjects.add( this );

		collectDependenciesRec( transitiveDependencies, fetchMissingProjects, log );

		return transitiveDependencies;
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
		PropertyLocation propertyDefinition = getPropertyDefinition( log, propertyName );
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
			log.html( Tools.warningMessage( "unspecified dependency version to " + groupId + ":" + artifactId + " in project '" + toString() + "', check the pom file please !" ) );

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

	private PropertyLocation getPropertyDefinition( Log log, String propertyName )
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
			case "java.version":
				return new PropertyLocation( this, null, propertyName, propertyName );
		}

		if( parentGav != null )
		{
			Project parentProject = session.projects().forGav( parentGav );
			if( parentProject == null )
				parentProject = session.projects().fetchProject( parentGav, session, log );

			if( parentProject != null )
			{
				if( propertyName.startsWith( "project.parent." ) )
					propertyName = propertyName.replace( "project.parent.", "project." );

				return parentProject.getPropertyDefinition( log, propertyName );
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

	private void collectDependenciesRec( TransitiveDependencies state, boolean fetchMissingProjects, Log log )
	{
		collectDependencyManagement( state, fetchMissingProjects, log );

		collectDependencies( state, fetchMissingProjects, log );
	}

	private void collectDependencyManagement( TransitiveDependencies state, boolean fetchMissingProjects, Log log )
	{
		for( Dependency d : getDependencyManagement( log ).values() )
		{
			if( d.getScope() == Scope.IMPORT )
			{
				Gav dependencyGav = d.toGav();
				Project importedBomProject = session.projects().forGav( dependencyGav );
				if( importedBomProject == null && fetchMissingProjects )
					importedBomProject = session.projects().fetchProject( dependencyGav, session, log );

				if( importedBomProject == null )
					state.addMissingProject( dependencyGav );
				else
					importedBomProject.collectDependencyManagement( state, fetchMissingProjects, log );
			}
			else
			{
				state.addManagedDependency( d, this, 0 );
			}
		}

		if( parentGav == null )
			return;

		Project parent = session.projects().forGav( parentGav );
		if( parent == null && fetchMissingProjects )
			parent = session.projects().fetchProject( parentGav, session, log );

		if( parent == null )
			state.addMissingProject( parentGav );
		else
			parent.collectDependenciesRec( state, fetchMissingProjects, log );
	}

	private void collectDependencies( TransitiveDependencies state, boolean fetchMissingProjects, Log log )
	{
		for( org.apache.maven.model.Dependency md : project.getDependencies() )
		{
			String groupId = resolveValue( log, md.getGroupId() );
			String artifactId = resolveValue( log, md.getArtifactId() );
			String version = resolveValue( log, md.getVersion() );
			Scope scope = Scope.fromString( resolveValue( log, md.getScope() ) );
			String classifier = resolveValue( log, md.getClassifier() );
			String type = resolveValue( log, md.getType() );

			Dependency d = new Dependency( new Gav( groupId, artifactId, version ), scope, classifier, type );

			Gav dependencyGav = d.toGav();
			state.addDependency( new Dependency( dependencyGav, d.getScope(), d.getClassifier(), d.getType() ), this, 0, md.isOptional() );

			// if( type != null && (type.contains( "ejb" ) || type.contains( "war" ) || type.contains( "ear" )) )
			// {
			// log.html( Tools.warningMessage( "skipping transitive dependency collection after " + d + " because dependency type is " + type + "(in project " +
			// toString() + ")" ) );
			// continue;
			// }

			DependencyInfo actualised = state.getDependencies().get( d.key() );
			dependencyGav = actualised.dependency.toGav();
			d = actualised.dependency;

			if( d.getScope() == Scope.SYSTEM )
				continue;

			Project dependencyProject = session.projects().forGav( dependencyGav );
			if( dependencyProject == null && fetchMissingProjects )
				dependencyProject = session.projects().fetchProject( dependencyGav, session, log );

			if( dependencyProject == null )
			{
				state.addMissingProject( dependencyGav );
				continue;
			}

			TransitiveDependencies dependencyDependencies = dependencyProject.getTransitiveDependencies( fetchMissingProjects, log );
			for( DependencyInfo info : dependencyDependencies.getDependencies().values() )
			{
				state.mergeDependency( d.getScope(), info, d, md.isOptional() );
			}
		}

		if( parentGav == null )
			return;

		Project parent = session.projects().forGav( parentGav );
		if( parent == null && fetchMissingProjects )
			parent = session.projects().fetchProject( parentGav, session, log );

		if( parent == null )
			state.addMissingProject( parentGav );
		else
			parent.collectDependenciesRec( state, fetchMissingProjects, log );
	}
}
