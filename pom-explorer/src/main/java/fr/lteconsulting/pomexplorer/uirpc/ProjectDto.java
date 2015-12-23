package fr.lteconsulting.pomexplorer.uirpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.project.MavenProject;

import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Session;

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
//		showReferences( log, session.graph().read(), project, session, logi );
//		showDependencyManagement( project, log, logi );
//		showPluginManagement( project, log, logi );
//		showDependencies( project, log, logi );
//		showPlugins( project, log, logi );
		return dto;
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
