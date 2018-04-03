package fr.lteconsulting.pomexplorer;

import static fr.lteconsulting.pomexplorer.Tools.isMavenVariable;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.project.MavenProject;

import fr.lteconsulting.pomexplorer.graph.relation.Scope;
import fr.lteconsulting.pomexplorer.model.Dependency;
import fr.lteconsulting.pomexplorer.model.Gav;

public class ProjectTools
{
	public static void showDependencies( Project project, StringBuilder sb, ProjectContainer projects, Log log )
	{
		MavenProject mavenProject = project.getMavenProject();
		if( mavenProject.getDependencies() != null && !mavenProject.getDependencies().isEmpty() )
		{
			sb.append( "<div><div>dependencies</div><div>" );
			mavenProject.getDependencies().stream()
					.map( d -> new Dependency( d.getGroupId(), d.getArtifactId(), d.getVersion(), Scope.fromString( d.getScope() ), d.getClassifier(), d.getType() ) )
					.sorted( Dependency.alphabeticalComparator ).forEach( dependency -> {
						showDependency( project, dependency, sb, projects, log );
						sb.append( "<br/>" );
					} );
			sb.append( "</div></div>" );
		}
	}

	public static void showDependencyManagement( Project project, StringBuilder sb, ProjectContainer projects, Log log )
	{
		MavenProject mavenProject = project.getMavenProject();
		if( mavenProject.getDependencyManagement() != null && !mavenProject.getDependencyManagement().getDependencies().isEmpty() )
		{
			sb.append( "<div><div>dependency management</div><div>" );
			mavenProject.getDependencyManagement().getDependencies().stream()
					.map( d -> new Dependency( d.getGroupId(), d.getArtifactId(), d.getVersion(), Scope.fromString( d.getScope() ), d.getClassifier(), d.getType() ) )
					.sorted( Dependency.alphabeticalComparator )
					.forEach( dependency -> {
						showDependency( project, dependency, sb, projects, log );
						sb.append( "<br/>" );
					} );
			sb.append( "</div></div>" );
		}
	}

	public static void showDependency( Project project, Dependency d, StringBuilder sb, ProjectContainer projects, Log log )
	{
		showDifferences( project, d.getGroupId(), sb, projects, log );
		sb.append( ":" );
		showDifferences( project, d.getArtifactId(), sb, projects, log );
		sb.append( ":" );
		showDifferences( project, d.getVersion(), sb, projects, log );
		sb.append( ":" + d.getScope() );
		if( d.getClassifier() != null )
		{
			sb.append( ":" );
			showDifferences( project, d.getClassifier(), sb, projects, log );
		}
		if( d.getType() != null )
		{
			sb.append( ":" );
			showDifferences( project, d.getType(), sb, projects, log );
		}
	}

	public static void showPlugins( Project project, StringBuilder sb, ProjectContainer projects, Log log )
	{
		MavenProject mavenProject = project.getMavenProject();
		if( mavenProject.getBuildPlugins() != null && !mavenProject.getBuildPlugins().isEmpty() )
		{
			sb.append( "<div><div>build plugins</div><div>" );
			mavenProject.getBuildPlugins().stream().map( p -> new Gav( p.getGroupId(), p.getArtifactId(), p.getVersion() ) ).sorted( Gav.alphabeticalComparator ).forEach( gav -> {
				showGav( project, gav, sb, projects, log );
				sb.append( "<br/>" );
			} );
			sb.append( "</div></div>" );
		}
	}

	public static void showPluginManagement( Project project, StringBuilder sb, ProjectContainer projects, Log log )
	{
		MavenProject mavenProject = project.getMavenProject();
		if( mavenProject.getPluginManagement() != null && mavenProject.getPluginManagement().getPlugins() != null && !mavenProject.getPluginManagement().getPlugins().isEmpty() )
		{
			sb.append( "<div><div>plugin management</div><div>" );
			mavenProject.getPluginManagement().getPlugins().stream().map( p -> new Gav( p.getGroupId(), p.getArtifactId(), p.getVersion() ) ).sorted( Gav.alphabeticalComparator )
					.forEach( gav -> {
						showGav( project, gav, sb, projects, log );
						sb.append( "<br/>" );
					} );
			sb.append( "</div></div>" );
		}
	}

	public static void showDifferences( Project project, String value, StringBuilder sb, ProjectContainer projects, Log log )
	{
		ValueResolution resolution = project.interpolateValueEx( value, projects, log );
		Map<String, String> properties = resolution.getProperties();

		if( properties != null && !properties.isEmpty() )
		{
			sb.append( resolution.getResolved() );

			sb.append( " <span class='inline-badge'>" );

			if( isMavenVariable( value ) )
			{
				sb.append( "(" ).append( Tools.getPropertyNameFromPropertyReference( value ) ).append( ")" );
			}
			else
			{
				sb.append( " (" ).append( value ).append( ", " );
				boolean first = true;
				for( Entry<String, String> e : properties.entrySet() )
				{
					if( first )
						first = false;
					else
						sb.append( ", " );
					sb.append( e.getKey() ).append( "=" ).append( e.getValue() );
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

	public static void showGav( Project project, Gav d, StringBuilder sb, ProjectContainer projects, Log log )
	{
		showDifferences( project, d.getGroupId(), sb, projects, log );
		sb.append( ":" );
		showDifferences( project, d.getArtifactId(), sb, projects, log );
		sb.append( ":" );
		showDifferences( project, d.getVersion(), sb, projects, log );
	}
}
