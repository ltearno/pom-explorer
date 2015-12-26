package fr.lteconsulting.pomexplorer;

import static fr.lteconsulting.pomexplorer.Tools.isMavenVariable;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.project.MavenProject;

import fr.lteconsulting.pomexplorer.Project.ValueResolution;
import fr.lteconsulting.pomexplorer.graph.relation.Scope;
import fr.lteconsulting.pomexplorer.model.Dependency;
import fr.lteconsulting.pomexplorer.model.Gav;

public class ProjectTools
{

	public static void showDependencies( Project project, StringBuilder sb, Log log )
	{
		MavenProject mavenProject = project.getMavenProject();
		if( mavenProject.getDependencies() != null && !mavenProject.getDependencies().isEmpty() )
		{
			sb.append( "<div><div>dependencies</div><div>" );
			mavenProject.getDependencies().stream()
					.map( d -> new Dependency( d.getGroupId(), d.getArtifactId(), d.getVersion(), Scope.fromString( d.getScope() ), d.getClassifier(), d.getType() ) )
					.sorted( Dependency.alphabeticalComparator ).forEach( dependency -> {
						showDependency( project, dependency, sb, log );
						sb.append( "<br/>" );
					} );
			sb.append( "</div></div>" );
		}
	}

	public static void showDependencyManagement( Project project, StringBuilder sb, Log log )
	{
		MavenProject mavenProject = project.getMavenProject();
		if( mavenProject.getDependencyManagement() != null && !mavenProject.getDependencyManagement().getDependencies().isEmpty() )
		{
			sb.append( "<div><div>dependency management</div><div>" );
			mavenProject.getDependencyManagement().getDependencies().stream()
					.map( d -> new Dependency( d.getGroupId(), d.getArtifactId(), d.getVersion(), Scope.fromString( d.getScope() ), d.getClassifier(), d.getType() ) )
					.sorted( Dependency.alphabeticalComparator )
					.forEach( dependency -> {
						showDependency( project, dependency, sb, log );
						sb.append( "<br/>" );
					} );
			sb.append( "</div></div>" );
		}
	}

	public static void showDependency( Project project, Dependency d, StringBuilder sb, Log log )
	{
		showDifferences( project, d.getGroupId(), sb, log );
		sb.append( ":" );
		showDifferences( project, d.getArtifactId(), sb, log );
		sb.append( ":" );
		showDifferences( project, d.getVersion(), sb, log );
		sb.append( ":" + d.getScope() );
		if( d.getClassifier() != null )
		{
			sb.append( ":" );
			showDifferences( project, d.getClassifier(), sb, log );
		}
		if( d.getType() != null )
		{
			sb.append( ":" );
			showDifferences( project, d.getType(), sb, log );
		}
	}

	public static void showPlugins( Project project, StringBuilder sb, Log log )
	{
		MavenProject mavenProject = project.getMavenProject();
		if( mavenProject.getBuildPlugins() != null && !mavenProject.getBuildPlugins().isEmpty() )
		{
			sb.append( "<div><div>build plugins</div><div>" );
			mavenProject.getBuildPlugins().stream().map( p -> new Gav( p.getGroupId(), p.getArtifactId(), p.getVersion() ) ).sorted( Gav.alphabeticalComparator ).forEach( gav -> {
				showGav( project, gav, sb, log );
				sb.append( "<br/>" );
			} );
			sb.append( "</div></div>" );
		}
	}

	public static void showPluginManagement( Project project, StringBuilder sb, Log log )
	{
		MavenProject mavenProject = project.getMavenProject();
		if( mavenProject.getPluginManagement() != null && mavenProject.getPluginManagement().getPlugins() != null && !mavenProject.getPluginManagement().getPlugins().isEmpty() )
		{
			sb.append( "<div><div>plugin management</div><div>" );
			mavenProject.getPluginManagement().getPlugins().stream().map( p -> new Gav( p.getGroupId(), p.getArtifactId(), p.getVersion() ) ).sorted( Gav.alphabeticalComparator )
					.forEach( gav -> {
						showGav( project, gav, sb, log );
						sb.append( "<br/>" );
					} );
			sb.append( "</div></div>" );
		}
	}

	public static void showDifferences( Project project, String value, StringBuilder sb, Log log )
	{
		ValueResolution resolution = project.resolveValueEx( log, value );
		Map<String, String> properties = resolution.getProperties();

		if( properties != null && !properties.isEmpty() )
		{
			sb.append( resolution.getResolved() );

			sb.append( " <span class='inline-badge'>" );

			if( isMavenVariable( value ) )
			{
				sb.append( "(" + Tools.getPropertyNameFromPropertyReference( value ) + ")" );
			}
			else
			{
				sb.append( " (" + value + ", " );
				boolean first = true;
				for( Entry<String, String> e : properties.entrySet() )
				{
					if( first )
						first = false;
					else
						sb.append( ", " );
					sb.append( e.getKey() + "=" + e.getValue() );
				}
				sb.append( ")" );
			}

			sb.append( "</span>" );
		}
		else
		{
			sb.append( resolution.getResolved() );
		}
	}

	public static void showGav( Project project, Gav d, StringBuilder sb, Log log )
	{
		showDifferences( project, d.getGroupId(), sb, log );
		sb.append( ":" );
		showDifferences( project, d.getArtifactId(), sb, log );
		sb.append( ":" );
		showDifferences( project, d.getVersion(), sb, log );
	}
}
