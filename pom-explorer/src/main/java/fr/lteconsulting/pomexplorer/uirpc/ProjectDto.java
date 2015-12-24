package fr.lteconsulting.pomexplorer.uirpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.graph.relation.RelationType;
import fr.lteconsulting.pomexplorer.model.Gav;

public class ProjectDto
{
	private String gav;
	private String packaging;
	private boolean buildable;
	private String description;
	private String file;
	private String scm;
	private Map<String, String> properties;
	private List<String> parentChain;
	private List<Reference> references;
	private List<DependencyManagement> dependencyManagement;
	private List<Dependency> dependencies;
	private List<PluginManagement> pluginManagement;
	private List<Plugin> plugins;

	public static ProjectDto fromProject( Session session, Project project )
	{
		MavenProject mavenProject = project.getMavenProject();
		ProjectDto dto = new ProjectDto();

		dto.gav = project.getGav().toString();
		dto.packaging = mavenProject.getModel().getPackaging();
		dto.buildable = project.isBuildable();
		dto.description = mavenProject.getDescription();
		dto.file = project.getPomFile().getAbsolutePath();
		dto.scm = mavenProject.getScm() != null ? mavenProject.getScm().getUrl() : null;
		dto.properties = project.getProperties();
		dto.parentChain = getParentChain( session, project );
		dto.references = getReferences( session, project );
		// showDependencyManagement( project, log, logi );
		// showPluginManagement( project, log, logi );
		// showDependencies( project, log, logi );
		// showPlugins( project, log, logi );
		return dto;
	}

	private static List<Reference> getReferences( Session session, Project project )
	{
		PomGraphReadTransaction tx = session.graph().read();
		Set<Relation> relations = tx.relationsReverse( project.getGav() );
		List<Reference> res = new ArrayList<>();
		if( relations!=null && !relations.isEmpty() )
		{
			relations.stream().sorted( ( a, b ) -> tx.sourceOf( a ).toString().compareTo( tx.sourceOf( b ).toString() ) ).forEach( relation -> {
				Gav source = tx.sourceOf( relation );
				RelationType type = relation.getRelationType();

				res.add( new Reference(source.toString(), type.toString()) );
			} );
		}
		return res;
	}

	private static List<String> getParentChain( Session session, Project project )
	{
		List<String> res = new ArrayList<>();

		do
		{
			project = project.getParentProject();
			if( project != null )
				res.add( project.getGav().toString() );
		}
		while( project != null );

		return res;
	}

	private static class Reference
	{
		private String gav;
		private String dependencyType;

		public Reference( String gav, String dependencyType )
		{
			this.gav = gav;
			this.dependencyType = dependencyType;
		}

		public Reference()
		{
		}
	}

	private static class DependencyManagement
	{
	}

	private static class Dependency
	{
	}

	private static class PluginManagement
	{
	}

	private static class Plugin
	{
	}
	/*
	 * interface Project {
	 * gav: string;
	 * packaging: string;
	 * buildable: boolean;
	 * description: string;
	 * file: string;
	 * properties: { [key: string]: string };
	 * parentChain: string[];
	 * references: Reference[];
	 * scm: string;
	 * dependencyManagement: DependencyManagement[];
	 * dependencies: Dependency[];
	 * pluginManagement: PluginManagement[];
	 * plugins: Plugin[];
	 * }
	 */
}
