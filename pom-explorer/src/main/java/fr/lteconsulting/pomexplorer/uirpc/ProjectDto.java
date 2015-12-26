package fr.lteconsulting.pomexplorer.uirpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;

import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.ProjectTools;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.graph.relation.RelationType;
import fr.lteconsulting.pomexplorer.model.Gav;

public class ProjectDto
{
	@SuppressWarnings( "unused" )
	private String gav;
	@SuppressWarnings( "unused" )
	private String packaging;
	@SuppressWarnings( "unused" )
	private boolean buildable;
	@SuppressWarnings( "unused" )
	private String description;
	@SuppressWarnings( "unused" )
	private String file;
	@SuppressWarnings( "unused" )
	private String scm;
	@SuppressWarnings( "unused" )
	private Map<String, String> properties;
	@SuppressWarnings( "unused" )
	private List<String> parentChain;
	@SuppressWarnings( "unused" )
	private List<Reference> references;
	@SuppressWarnings( "unused" )
	private String dependencyManagement;
	@SuppressWarnings( "unused" )
	private String dependencies;
	@SuppressWarnings( "unused" )
	private String pluginManagement;
	@SuppressWarnings( "unused" )
	private String plugins;

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

		StringBuilder sb = new StringBuilder();
		ProjectTools.showDependencyManagement( project, sb, null );
		dto.dependencyManagement = sb.toString();
		sb = new StringBuilder();
		ProjectTools.showPluginManagement( project, sb, null );
		dto.pluginManagement = sb.toString();
		sb = new StringBuilder();
		ProjectTools.showDependencies( project, sb, null );
		dto.dependencies = sb.toString();
		sb = new StringBuilder();
		ProjectTools.showPlugins( project, sb, null );
		dto.plugins = sb.toString();

		return dto;
	}

	private static List<Reference> getReferences( Session session, Project project )
	{
		PomGraphReadTransaction tx = session.graph().read();
		Set<Relation> relations = tx.relationsReverse( project.getGav() );
		List<Reference> res = new ArrayList<>();
		if( relations != null && !relations.isEmpty() )
		{
			relations.stream().sorted( ( a, b ) -> tx.sourceOf( a ).toString().compareTo( tx.sourceOf( b ).toString() ) ).forEach( relation -> {
				Gav source = tx.sourceOf( relation );
				RelationType type = relation.getRelationType();

				res.add( new Reference( source.toString(), type.toString() ) );
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

	protected static class Reference
	{
		protected String gav;
		protected String dependencyType;

		public Reference( String gav, String dependencyType )
		{
			this.gav = gav;
			this.dependencyType = dependencyType;
		}
	}
}
