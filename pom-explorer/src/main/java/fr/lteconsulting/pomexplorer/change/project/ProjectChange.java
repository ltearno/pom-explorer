package fr.lteconsulting.pomexplorer.change.project;

import java.util.Map;

import fr.lteconsulting.Mandatory;
import fr.lteconsulting.UseBuilderGenerator;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.change.Change;
import fr.lteconsulting.pomexplorer.change.project.Location.Dependency;
import fr.lteconsulting.pomexplorer.change.project.Location.DependencyManagement;
import fr.lteconsulting.pomexplorer.change.project.Location.Parent;
import fr.lteconsulting.pomexplorer.change.project.Location.Plugin;
import fr.lteconsulting.pomexplorer.change.project.Location.PluginManagement;
import fr.lteconsulting.pomexplorer.change.project.Location.Property;
import fr.lteconsulting.pomexplorer.model.DependencyKey;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.model.GroupArtifact;
import fr.lteconsulting.pomexplorer.model.transitivity.RawDependency;

public class ProjectChange extends Change
{
	public enum PomSection
	{
		PARENT,
		PROJECT,
		PROPERTIES,
		DEPENDENCY_MNGT,
		DEPENDENCY,
		PLUGIN_MNGT,
		PLUGIN
	}

	public enum Coordinate
	{
		GROUP_ID,
		ARTIFACT_ID,
		VERSION,
		SCOPE,
		CLASSIFIER,
		TYPE
	}

	public enum Action
	{
		SET,
		REMOVE
	}

	private final Project project;
	private final Location location;
	private final Action action;
	private final String nodeName;
	private final String newValue;

	public static final String GROUP_ID = "groupId";
	public static final String ARTIFACT_ID = "artifactId";
	public static final String VERSION = "version";
	public static final String SCOPE = "scope";
	public static final String TYPE = "type";
	public static final String CLASSIFIER = "classifier";

	public Location getLocation()
	{
		return location;
	}

	public String getNewValue()
	{
		return newValue;
	}

	public String getNodeName()
	{
		return nodeName;
	}

	public String getCurrentValue()
	{
		return location.visit( new Location.Visitor<String>()
		{
			@Override
			public String visit( PluginManagement pluginManagement )
			{
				throw new IllegalStateException( "nyi" );
			}

			@Override
			public String visit( Plugin plugin )
			{
				throw new IllegalStateException( "nyi" );
			}

			@Override
			public String visit( DependencyManagement dependencyManagement )
			{
				throw new IllegalStateException( "nyi" );
			}

			@Override
			public String visit( Dependency dependency )
			{
				Map<DependencyKey, RawDependency> deps = project.getRawDependencies();
				DependencyKey key = dependency.getKey();

				RawDependency dep = deps.get( key );
				if( dep == null )
					return null;

				return getDependencyValue( key, dep, nodeName );
			}

			@Override
			public String visit( Property property )
			{
				return project.getProperties().get( nodeName );
			}

			@Override
			public String visit( fr.lteconsulting.pomexplorer.change.project.Location.Project project )
			{
				Gav gav = ProjectChange.this.project.getDeclaredGav();
				return getGavValue( gav, nodeName );
			}

			@Override
			public String visit( Parent parent )
			{
				Gav gav = ProjectChange.this.project.getDeclaredParentGav();
				return getGavValue( gav, nodeName );
			}
		} );
	}

	private static String getDependencyValue( DependencyKey key, RawDependency dep, String nodeName )
	{
		switch( nodeName )
		{
			case GROUP_ID:
				return key.getGroupId();
			case ARTIFACT_ID:
				return key.getArtifactId();
			case VERSION:
				return dep.getVs().getVersion();
			case SCOPE:
				// TODO bad !
				return dep.getVs().getScope().toString().toLowerCase();
			case TYPE:
				return key.getType();
			case CLASSIFIER:
				return key.getClassifier();
			default:
				throw new IllegalArgumentException( "this node name is illegal for a Dependency location: " + nodeName );
		}
	}

	private static String getGavValue( Gav gav, String nodeName )
	{
		switch( nodeName )
		{
			case GROUP_ID:
				return gav.getGroupId();
			case ARTIFACT_ID:
				return gav.getArtifactId();
			case VERSION:
				return gav.getVersion();
			default:
				throw new IllegalArgumentException( "this node name is illegal for a Gav location: " + nodeName );
		}
	}

	public static ProjectChange set( Project project, Location location, String nodeName, String value )
	{
		return new ProjectChange( project, location, value != null ? Action.SET : Action.REMOVE, nodeName, value );
	}

	public static ProjectChange set( Project project, String location, String nodeName, String value )
	{
		return set( project, parse( location ), nodeName, value );
	}

	public static ProjectChange remove( Project project, String location, String nodeName )
	{
		return new ProjectChange( project, parse( location ), Action.REMOVE, nodeName, null );
	}

	public static ProjectChange parentGroupId( Project project, String value )
	{
		return new ProjectChange( project, new Location.Parent(), Action.SET, GROUP_ID, value );
	}

	public static ProjectChange setProject( Project project, String nodeName, String newValue )
	{
		return set( project, new Location.Project(), nodeName, newValue );
	}

	@UseBuilderGenerator
	public static ProjectChange setProperty( @Mandatory Project project, @Mandatory String propertyName, @Mandatory String newValue )
	{
		return set( project, new Location.Property(), propertyName, newValue );
	}

	public static ProjectChange setDependency( Project project, DependencyKey key, String nodeName, String newValue )
	{
		return set( project, new Location.Dependency( key ), nodeName, newValue );
	}

	public static ProjectChange setPlugin( Project project, GroupArtifact key, String nodeName, String newValue )
	{
		return set( project, new Location.Plugin( key ), nodeName, newValue );
	}

	public static ProjectChange setParent( Project project, String nodeName, String newValue )
	{
		return set( project, new Location.Parent(), nodeName, newValue );
	}

	/**
	 * Parse a location :
	 * 
	 * <ul>
	 * <li>
	 * <code>parent</code> : the parent
	 * <li>
	 * <code>project</code> : the project
	 * <li>
	 * <code>property</code> : a property
	 * <li>
	 * <code>d:<i>group:artifact:classifier:type</i></code> : a dependency
	 * <li>
	 * <code>dm:<i>group:artifact:classifier:type</i></code> : a dependency management
	 * <li>
	 * <code>p:<i>group:artifact</i></code> : a build plugin
	 * <li>
	 * <code>pm:<i>group:artifact</i></code> : a build plugin management
	 * </ul>
	 */
	public static Location parse( String location )
	{
		if( location == null )
			return null;

		int i = location.indexOf( ":" );
		String section;
		String other;
		if( i < 0 )
		{
			section = location;
			other = null;
		}
		else
		{
			section = location.substring( 0, i );
			other = location.substring( i + 1 );
		}

		switch( section )
		{
			case "parent":
				return new Location.Parent();
			case "project":
				return new Location.Project();
			case "property":
				return new Location.Project();
			case "d":
				return new Location.Dependency( DependencyKey.parse( other ) );
			case "dm":
				return new Location.DependencyManagement( DependencyKey.parse( other ) );
			case "p":
				return new Location.Plugin( GroupArtifact.parse( other ) );
			case "pm":
				return new Location.PluginManagement( GroupArtifact.parse( other ) );
			default:
				return null;
		}
	}

	public Project getProject()
	{
		return project;
	}

	@Override
	public String toString()
	{
		if( action == Action.SET )
			return "set " + location + " '" + nodeName + "' to '" + newValue + "' in project " + project;
		else if( action == Action.REMOVE )
			return "remove " + location + " '" + nodeName + "' from project " + project;
		return "unknown project change";
	}

	public ProjectChange( Project project, Location location, Action action, String nodeName, String newValue )
	{
		this.project = project;
		this.location = location;
		this.action = action;
		this.nodeName = nodeName;
		this.newValue = newValue;
	}
}
