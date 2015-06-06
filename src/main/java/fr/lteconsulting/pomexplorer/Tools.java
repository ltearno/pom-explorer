package fr.lteconsulting.pomexplorer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.pom.ParsedPomFileImpl;

import fr.lteconsulting.hexa.client.tools.Func1;
import fr.lteconsulting.pomexplorer.changes.Change;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;
import fr.lteconsulting.pomexplorer.depanalyze.Location;
import fr.lteconsulting.pomexplorer.depanalyze.PropertyLocation;
import fr.lteconsulting.pomexplorer.graph.relation.GAVRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

public class Tools
{
	public static GAV string2Gav( String gavString )
	{
		String[] parts = gavString.split( ":" );
		if( parts.length != 3 )
			return null;

		GAV gav = new GAV( parts[0], parts[1], parts[2] );

		return gav;
	}

	/**
	 * Prints a list of changes to be made to follow a GAV change
	 */
	public static String changeGav( Client client, WorkingSession session, GAV originalGav, GAV newGav, Set<Change<? extends Location>> changes )
	{
		StringBuilder sb = new StringBuilder();

		Set<Location> locations = Tools.getDirectDependenciesLocations( session, sb, originalGav );

		for( Location location : locations )
		{
			Change<? extends Location> c = Change.create( location, newGav );
			changes.add( c );
		}

		return sb.toString();
	}

	public static void printChangeList( StringBuilder res, Set<Change<? extends Location>> changes )
	{
		List<Change<? extends Location>> changeList = new ArrayList<>();
		changeList.addAll( changes );

		Collections.sort( changeList, new Comparator<Change<? extends Location>>()
		{
			@Override
			public int compare( Change<? extends Location> o1, Change<? extends Location> o2 )
			{
				Project p1 = o1.getLocation().getProject();
				Project p2 = o2.getLocation().getProject();

				if( p1 == null && p2 == null )
					return 0;
				if( p1 == null )
					return -1;
				if( p2 == null )
					return 1;

				return p1.getPomFile().getAbsolutePath().compareTo( p2.getPomFile().getAbsolutePath() );
			}
		} );

		for( Change<? extends Location> c : changeList )
		{
			res.append( c.toString() );
		}
	}

	/***
	 * Maven tools
	 */

	public static Set<Location> getDirectDependenciesLocations( WorkingSession session, StringBuilder log, GAV gav )
	{
		Set<Location> set = new HashSet<>();

		Set<GAVRelation<Relation>> relations = session.graph().relationsReverse( gav );
		for( GAVRelation<Relation> relation : relations )
		{
			GAV updatedGav = relation.getSource();

			Project updatedProject = session.projects().get( updatedGav );
			if( updatedProject == null )
			{
				if( log != null )
					log.append( "<span style='color:orange;'>Cannot find project for GAV " + updatedGav + " which dependency should be modified ! skipping.</span>" );
				continue;
			}

			Location dependencyLocation = Tools.findDependencyLocation( session, updatedProject, relation );
			if( dependencyLocation == null )
			{
				if( log != null )
					log.append( "<span style='color:red;'>Cannot find the location of dependency to " + relation.getTarget() + " in this project " + updatedProject + "</span><br/>" );
				continue;
			}

			set.add( dependencyLocation );
		}

		return set;
	}

	public static List<String> getMavenProperties( GAV gav )
	{
		if( gav == null )
			return null;

		ArrayList<String> res = new ArrayList<>();

		if( isMavenVariable( gav.getGroupId() ) )
			res.add( extractMavenProperty( gav.getGroupId() ) );

		if( isMavenVariable( gav.getArtifactId() ) )
			res.add( extractMavenProperty( gav.getArtifactId() ) );

		if( isMavenVariable( gav.getVersion() ) )
			res.add( extractMavenProperty( gav.getVersion() ) );

		return res;
	}

	public static boolean isMavenVariable( String text )
	{
		return text != null && text.startsWith( "${" ) && text.endsWith( "}" );
	}

	private static String extractMavenProperty( String variable )
	{
		assert isMavenVariable( variable );
		return variable.substring( 2, variable.length() - 1 );
	}

	public static Project getPropertyDefinitionProject( WorkingSession session, Project startingProject, String property )
	{
		if( property.startsWith( "project." ) )
			return startingProject;

		// search a property definition in the project. if found, return it
		String value = propertyValue( startingProject, property );
		if( value != null )
			return startingProject;

		// go deeper in hierarchy
		GAV parentGav = session.graph().parent( startingProject.getGav() );
		Project parentProject = null;
		if( parentGav != null )
			parentProject = session.projects().get( parentGav );

		if( parentProject != null )
		{
			Project definition = getPropertyDefinitionProject( session, parentProject, property );
			if( definition != null )
				return definition;
		}

		return null;
	}

	private static String propertyValue( Project startingProject, String property )
	{
		Object res = startingProject.getUnresolvedPom().getProperties().get( property );
		if( res instanceof String )
			return (String) res;
		return null;
	}

	public static Location findDependencyLocation( WorkingSession session, Project project, GAVRelation<Relation> relation )
	{
		if( project.getGav().equals( relation.getTarget() ) )
			return new GavLocation( project, PomSection.PROJECT, project.getGav(), project.getGav() );

		Location dependencyLocation = null;

		switch( relation.getRelation().getRelationType() )
		{
			case DEPENDENCY:
				dependencyLocation = findDependencyLocationInDependencies( session, project, relation.getTarget() );
				break;

			case BUILD_DEPENDENCY:
				dependencyLocation = findDependencyLocationInPlugins( session, project, relation.getTarget() );
				break;

			case PARENT:
				dependencyLocation = new GavLocation( project, PomSection.PARENT, relation.getTarget(), relation.getTarget() );
				break;
		}

		dependencyLocation = maybeFindPropertyLocation( session, dependencyLocation );

		return dependencyLocation;
	}

	public static Location maybeFindPropertyLocation( WorkingSession session, Location loc )
	{
		if( loc == null )
			return null;

		if( !(loc instanceof GavLocation) )
			return loc;

		GavLocation depLoc = (GavLocation) loc;

		if( depLoc.getUnresolvedGav() == null )
			return depLoc;

		if( !Tools.isMavenVariable( depLoc.getUnresolvedGav().getVersion() ) )
			return depLoc;

		String property = getPropertyNameFromPropertyReference( depLoc.getUnresolvedGav().getVersion() );

		Project definitionProject = Tools.getPropertyDefinitionProject( session, depLoc.getProject(), property );
		if( definitionProject != null )
			return new PropertyLocation( depLoc.getProject(), depLoc, property, definitionProject.getUnresolvedPom().getProperties().getProperty( property ) );

		return null;
	}

	public static String getPropertyNameFromPropertyReference( String name )
	{
		if( !(name.startsWith( "${" ) && name.endsWith( "}" )) )
			return name;

		return name.substring( 2, name.length() - 1 );
	}

	public static GavLocation findDependencyLocationInDependencies( WorkingSession session, Project project, GAV searchedDependency )
	{
		if( project == null )
			return null;

		// dependencies
		GavLocation info = project.getDependencies().get( searchedDependency );
		if( info != null && info.getUnresolvedGav() != null && info.getUnresolvedGav().getVersion() != null )
			return info;

		// dependency management
		GavLocation locationInDepMngt = findDependencyLocationInDependencyManagement( session, project, searchedDependency );
		if( locationInDepMngt != null )
			return locationInDepMngt;

		// parent
		GavLocation locationInParent = findDependencyLocationInDependencies( session, session.projects().get( session.graph().parent( project.getGav() ) ), searchedDependency );
		if( locationInParent != null )
			return locationInParent;

		return null;
	}

	public static GavLocation findDependencyLocationInDependencyManagement( WorkingSession session, Project project, GAV searchedDependency )
	{
		if( project.getUnresolvedPom().getDependencyManagement() == null )
			return null;
		if( project.getUnresolvedPom().getDependencyManagement().getDependencies() == null )
			return null;
		for( Dependency d : project.getUnresolvedPom().getDependencyManagement().getDependencies() )
		{
			if( searchedDependency.getGroupId().equals( d.getGroupId() ) && searchedDependency.getArtifactId().equals( d.getArtifactId() ) )
			{
				GAV g = new GAV( d.getGroupId(), d.getArtifactId(), d.getVersion() );
				return new GavLocation( project, PomSection.DEPENDENCY_MNGT, g, searchedDependency );
			}
		}

		return null;
	}

	public static GavLocation findDependencyLocationInPlugins( WorkingSession session, Project project, GAV searchedPlugin )
	{
		if( project == null )
			return null;

		GavLocation info = project.getPluginDependencies().get( searchedPlugin );
		if( info != null )
			return info;

		// TODO search in the plugin management section

		// find in parent
		return findDependencyLocationInPlugins( session, session.projects().get( session.graph().parent( project.getGav() ) ), searchedPlugin );
	}

	private static Field modelField;

	public static Model getParsedPomFileModel( ParsedPomFile parsedPomFile )
	{
		if( modelField == null )
		{
			try
			{
				modelField = ParsedPomFileImpl.class.getDeclaredField( "model" );
				modelField.setAccessible( true );

			}
			catch( NoSuchFieldException | SecurityException | IllegalArgumentException e )
			{
				e.printStackTrace();
				return null;
			}
		}

		try
		{
			Model model = (Model) modelField.get( parsedPomFile );
			return model;
		}
		catch( IllegalArgumentException | IllegalAccessException e )
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Collection utilities
	 */

	public static <T> List<T> filter( Iterable<T> list, Func1<T, Boolean> predicate )
	{
		List<T> res = new ArrayList<>();
		if( list == null )
			return res;

		for( T t : list )
			if( predicate.exec( t ) )
				res.add( t );
		return res;
	}

	public static <T> List<T> filter( T[] list, Func1<T, Boolean> predicate )
	{
		List<T> res = new ArrayList<>();
		if( list == null )
			return res;

		for( T t : list )
			if( predicate.exec( t ) )
				res.add( t );
		return res;
	}

	public static final Comparator<GAV> gavAlphabeticalComparator = new Comparator<GAV>()
	{
		@Override
		public int compare( GAV o1, GAV o2 )
		{
			int r = o1.getGroupId().compareTo( o2.getGroupId() );
			if( r != 0 )
				return r;

			r = o1.getArtifactId().compareTo( o2.getArtifactId() );
			if( r != 0 )
				return r;

			if( o1.getVersion() == null && o2.getVersion() == null )
				return 0;
			if( o1.getVersion() == null )
				return -1;
			if( o2.getVersion() == null )
				return 1;

			r = o1.getVersion().compareTo( o2.getVersion() );

			return 0;
		}
	};
}
