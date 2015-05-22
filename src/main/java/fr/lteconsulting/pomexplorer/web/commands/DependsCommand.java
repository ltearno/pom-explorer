package fr.lteconsulting.pomexplorer.web.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.maven.model.Parent;
import org.apache.maven.project.MavenProject;

import fr.lteconsulting.pomexplorer.Dep;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Project.DependencyInfo;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class DependsCommand
{
	public String def( WorkingSession session )
	{
		return on( session, "fr.lteconsulting:hexa.binding:1.2-SNAPSHOT" );
		// return on( session, "fr.lteconsulting:hexa.css:1.2-SNAPSHOT" );
		// return on( session, "fr.lteconsulting:hexa.utils:1.2-SNAPSHOT" );
	}

	private Set<GAV> dependentGAVs( WorkingSession session, GAV gav )
	{
		Set<GAV> res = new HashSet<>();

		dependentGAVsRec( session, gav, res );

		return res;
	}

	private void dependentGAVsRec( WorkingSession session, GAV gav, Set<GAV> list )
	{
		Set<Dep> deps = session.getGraph().incomingEdgesOf( gav );
		for( Dep dep : deps )
		{
			GAV ancestor = session.getGraph().getEdgeSource( dep );

			list.add( ancestor );

			dependentGAVsRec( session, ancestor, list );
		}
	}

	@Help( "lists the GAVs directly depending on the one given in parameter" )
	public String on( WorkingSession session, String gavString )
	{
		String[] parts = gavString.split( ":" );
		if( parts.length != 3 )
			return "specify the GAV with the group:artifact:version format please";

		GAV gav = new GAV( parts[0], parts[1], parts[2] );

		StringBuilder res = new StringBuilder();

		HashMap<Project, HashSet<Location>> locations = new HashMap<>();

		res.append( "<br/>" + gav + " dependencies<br/>" );

		res.append( "<br/>List of dependent GAVs ([D]=direct dependency, [H]=parent's dependency, [T]=transitive dependency, [?]/[!]=error)<br/>" );

		for( GAV dependency : dependentGAVs( session, gav ) )
		{
			Project project = session.getProjects().get( dependency );
			if( project == null )
			{
				res.append( "<b style='color:orange;'>" + dependency + "</b> (no project found)<br/>" );
				continue;
			}

			// find where the dependency is specified :
			// - in the project
			// - in an ancestor project ?
			String dependencyKind = "?";

			Project specifyingProject = getProjectWhereDependencyIsSpecifiedInHierarchy( session, project, gav );
			if( specifyingProject != null )
			{
				dependencyKind = specifyingProject == project ? "D" : "H";
			}
			else
			{
				specifyingProject = getProjectWhereDependencyIsSpecifiedInTransitiveDeps( session, dependency, gav );
				if( specifyingProject != null )
				{
					dependencyKind = "T";
				}
			}

			if( specifyingProject == null )
				dependencyKind = "!";

			res.append( "[" + dependencyKind + "] " + project + "<br/>" );

			if( specifyingProject == null )
			{
				res.append( "(dependency declaration not found although it is in the dependency graph, ignoring)<br/>" );
				continue;
			}

			DependencyInfo info = specifyingProject.getDependencies().get( gav );
			if( info == null || info.getUnresolvedGav() == null )
			{
				res.append( "(WARNING, dependency not found in project !)<br/>" );
				continue;
			}

			if( info.getUnresolvedGav().equals( info.getResolvedGav() ) )
			{
				HashSet<Location> hs = ensureProjectInLocations( locations, specifyingProject );
				hs.add( new DependencyLocation( specifyingProject, info ) );
			}
			else
			{
				// if variable, try to find where it is defined
				List<String> properties = getProperties( info.getUnresolvedGav() );
				for( String property : properties )
				{
					Project definitionProject = getPropertyDefinitionProject( session, specifyingProject, property );
					if( definitionProject != null )
					{
						HashSet<Location> hs = ensureProjectInLocations( locations, specifyingProject );
						hs.add( new PropertyLocation( specifyingProject, info, property, definitionProject.getUnresolvedPom().getProperties().getProperty( property ) ) );
					}
					else
					{
						res.append( "[ERROR] not found property definition for property " + property + "<br/>" );
					}
				}
			}

		}

		res.append( "<br/>Dependency locations:<br/>" );

		for( Entry<Project, HashSet<Location>> e : locations.entrySet() )
		{
			Project project = e.getKey();

			res.append( project.toString() + "<br/>" );

			HashSet<Location> ls = e.getValue();
			for( Location l : ls )
			{
				res.append( "&nbsp;&nbsp;&nbsp;&nbsp;" + l.toString() + "<br/>" );
			}
		}

		return res.toString();
	}

	private HashSet<Location> ensureProjectInLocations( HashMap<Project, HashSet<Location>> locations, Project specifyingProject )
	{
		HashSet<Location> hs = locations.get( specifyingProject );
		if( hs == null )
		{
			hs = new HashSet<>();
			locations.put( specifyingProject, hs );
		}
		return hs;
	}

	private List<String> getProperties( GAV gav )
	{
		if( gav == null )
			return null;

		ArrayList<String> res = new ArrayList<>();

		if( isVariable( gav.getGroupId() ) )
			res.add( extractProperty( gav.getGroupId() ) );

		if( isVariable( gav.getArtifactId() ) )
			res.add( extractProperty( gav.getArtifactId() ) );

		if( isVariable( gav.getVersion() ) )
			res.add( extractProperty( gav.getVersion() ) );

		return res;
	}

	private boolean isVariable( String text )
	{
		return text != null && text.startsWith( "${" ) && text.endsWith( "}" );
	}

	private String extractProperty( String variable )
	{
		assert isVariable( variable );
		return variable.substring( 2, variable.length() - 1 );
	}

	private Project getPropertyDefinitionProject( WorkingSession session, Project startingProject, String property )
	{
		if( property.startsWith( "project." ) )
			return startingProject;

		// search a property definition in the project. if found, return it
		String value = propertyValue( startingProject, property );
		if( value != null )
			return startingProject;

		// go deeper in hierarchy
		Project parentProject = getParentProject( session, startingProject );
		if( parentProject != null )
		{
			Project definition = getPropertyDefinitionProject( session, parentProject, property );
			if( definition != null )
				return definition;
		}

		return null;
	}

	private String propertyValue( Project startingProject, String property )
	{
		Object res = startingProject.getUnresolvedPom().getProperties().get( property );
		if( res instanceof String )
			return (String) res;
		return null;
	}

	private static class PropertyLocation extends Location
	{
		private final String propertyName;
		private final String propertyValue;
		private final DependencyInfo dependency;

		public PropertyLocation( Project project, DependencyInfo dependency, String propertyName, String propertyValue )
		{
			super( project );
			this.propertyName = propertyName;
			this.propertyValue = propertyValue;
			this.dependency = dependency;
		}

		@Override
		public String toString()
		{
			return "property '" + propertyName + "' " + (propertyValue != null ? ("with current value '" + propertyValue + "'") : "") + " specifying dependency to " + dependency.getGav();
		}
	}

	private static class DependencyLocation extends Location
	{
		private final DependencyInfo dependency;

		public DependencyLocation( Project project, DependencyInfo dependency )
		{
			super( project );
			this.dependency = dependency;
		}

		@Override
		public String toString()
		{
			return "dependency to " + dependency.getGav();
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((dependency == null) ? 0 : dependency.getGav().hashCode());
			return result;
		}

		@Override
		public boolean equals( Object obj )
		{
			if( this == obj )
				return true;
			if( !super.equals( obj ) )
				return false;
			if( getClass() != obj.getClass() )
				return false;
			DependencyLocation other = (DependencyLocation) obj;
			if( dependency == null )
			{
				if( other.dependency != null )
					return false;
			}
			else if( !dependency.getGav().equals( other.dependency.getGav() ) )
				return false;
			return true;
		}
	}

	private static abstract class Location
	{
		private final Project project;

		public Location( Project project )
		{
			this.project = project;
		}

		@Override
		public String toString()
		{
			return project.toString();
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((project == null) ? 0 : project.hashCode());
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
			Location other = (Location) obj;
			if( project == null )
			{
				if( other.project != null )
					return false;
			}
			else if( !project.equals( other.project ) )
				return false;
			return true;
		}
	}

	private Project getProjectWhereDependencyIsSpecifiedInHierarchy( WorkingSession session, Project project, GAV gav )
	{
		if( project == null )
			return null;

		DependencyInfo info = project.getDependencies().get( gav );

		// if an unresolved gav is found, it means the dependency is written in
		// the pom
		if( info != null && info.getUnresolvedGav() != null )
		{
			return project;
		}
		else
		{
			Project parentProject = getParentProject( session, project );
			if( parentProject == null )
				return null;

			return getProjectWhereDependencyIsSpecifiedInHierarchy( session, parentProject, gav );
		}
	}

	private Project getParentProject( WorkingSession session, Project project )
	{
		// TODO : should use resolved, but it does not have the parentId !
		MavenProject pom = project.getUnresolvedPom();
		if( pom == null )
			return null;

		Parent parent = pom.getModel().getParent();
		if( parent == null )
			return null;

		GAV parentGav = new GAV( parent.getGroupId(), parent.getArtifactId(), parent.getVersion() );

		Project parentProject = session.getProjects().get( parentGav );

		return parentProject;
	}

	private Project getProjectWhereDependencyIsSpecifiedInTransitiveDeps( WorkingSession session, GAV currentGav, GAV searchedGav )
	{
		for( Dep dep : session.getGraph().outgoingEdgesOf( currentGav ) )
		{
			GAV dependencyGav = session.getGraph().getEdgeTarget( dep );

			Project project = session.getProjects().get( dependencyGav );
			if( project != null )
			{
				DependencyInfo info = project.getDependencies().get( searchedGav );

				// if an unresolved gav is found, it means the dependency is
				// written in
				// the pom
				if( info != null && info.getUnresolvedGav() != null )
				{
					return project;
				}
			}

			project = getProjectWhereDependencyIsSpecifiedInTransitiveDeps( session, dependencyGav, searchedGav );
			if( project != null )
				return project;
		}

		return null;
	}
}
