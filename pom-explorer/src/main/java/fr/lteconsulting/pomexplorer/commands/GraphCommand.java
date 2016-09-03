package fr.lteconsulting.pomexplorer.commands;

import java.io.File;
import java.io.Writer;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.ext.IntegerEdgeNameProvider;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.DirectedSubgraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;

import fr.lteconsulting.pomexplorer.AppFactory;
import fr.lteconsulting.pomexplorer.GitTools;
import fr.lteconsulting.pomexplorer.GraphFrame;
import fr.lteconsulting.pomexplorer.GraphQuery;
import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.graph.Repository;
import fr.lteconsulting.pomexplorer.graph.RepositoryRelation;
import fr.lteconsulting.pomexplorer.graph.relation.BuildDependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.ParentRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.graph.relation.Scope;
import fr.lteconsulting.pomexplorer.model.Gav;

public class GraphCommand
{
	@Help( "displays an interactive 3d WebGL graph of the projects" )
	public void main( Session session, Log log )
	{
		String url = "graph.html?session=" + System.identityHashCode( session );
		url += "&graphQueryId=" + GraphQuery.register( null );
		log.html( "To display the graph, go to : <a href='" + url + "' target='_blank'>" + url + "</a><br/>" );
	}

	@Help( "displays an interactive 3d WebGL graph of the projects, limited to dependency tree of the given root gavs" )
	public void roots( Session session, Log log, FilteredGAVs roots )
	{
		String url = "graph.html?session=" + System.identityHashCode( session );
		url += "&graphQueryId=" + GraphQuery.register( new HashSet<>( roots.getGavs( session ) ) );
		log.html( "Root gavs : " );
		StringBuilder sb = new StringBuilder();
		roots.getGavs( session ).forEach( root -> sb.append( root + "<br/>" ) );
		log.html( sb.toString() );
		log.html( "To display the graph, go to : <a href='" + url + "' target='_blank'>" + url + "</a><br/>" );
	}

	private boolean isOkGav( Gav gav )
	{
		return true;
	}

	private boolean isOkRelation( Relation relation )
	{
		if( relation instanceof BuildDependencyRelation )
			return false;

		if( relation instanceof DependencyRelation )
		{
			if( relation.asDependencyRelation().getDependency().getScope() == Scope.TEST )
				return false;
		}

		return true;
	}

	@Help( "exports a GraphML file" )
	public void export( Session session, Log log )
	{
		PomGraphReadTransaction tx = session.graph().read();

		try
		{
			GraphMLExporter<Gav, Relation> exporter = new GraphMLExporter<Gav, Relation>( new IntegerNameProvider<Gav>(), new VertexNameProvider<Gav>()
			{
				@Override
				public String getVertexName( Gav vertex )
				{
					return vertex.toString();
				}
			}, new IntegerEdgeNameProvider<Relation>(), new EdgeNameProvider<Relation>()
			{
				@Override
				public String getEdgeName( Relation edge )
				{
					return edge.toString();
				}
			} );

			GraphMLExporter<Repository, RepositoryRelation> repoExporter = new GraphMLExporter<Repository, RepositoryRelation>( new IntegerNameProvider<Repository>(),
					new VertexNameProvider<Repository>()
					{
						@Override
						public String getVertexName( Repository vertex )
						{
							return vertex.toString();
						}
					}, new IntegerEdgeNameProvider<RepositoryRelation>(), new EdgeNameProvider<RepositoryRelation>()
					{
						@Override
						public String getEdgeName( RepositoryRelation edge )
						{
							return edge.toString();
						}
					} );

			DirectedGraph<Gav, Relation> g = tx.internalGraph();

			DirectedGraph<Gav, Relation> ng = new DirectedMultigraph<Gav, Relation>( (Class<? extends Relation>) Relation.class );
			for( Gav gav : g.vertexSet() )
			{
				if( !isOkGav( gav ) )
					continue;

				ng.addVertex( gav );

				for( Relation relation : g.outgoingEdgesOf( gav ) )
				{
					Gav target = g.getEdgeTarget( relation );

					if( !isOkGav( target ) )
						continue;

					if( !isOkRelation( relation ) )
						continue;

					ng.addVertex( target );

					ng.addEdge( gav, target, relation );
				}
			}

			DirectedGraph<Repository, RepositoryRelation> repoGraph = new DirectedMultigraph<Repository, RepositoryRelation>( RepositoryRelation.class );
			for( Gav gav : ng.vertexSet() )
			{
				String repoPath = getGAVRepository( session, gav );
				if( repoPath == null )
					continue;

				Repository repo = new Repository( new File( repoPath ).toPath() );
				repoGraph.addVertex( repo );

				for( Relation relation : ng.outgoingEdgesOf( gav ) )
				{
					Gav target = ng.getEdgeTarget( relation );
					String targetRepoPath = getGAVRepository( session, target );
					if( targetRepoPath == null )
						continue;

					Repository targetRepo = new Repository( new File( targetRepoPath ).toPath() );

					if( repo.equals( targetRepo ) )
						continue;

					repoGraph.addVertex( targetRepo );

					RepositoryRelation rr = repoGraph.getEdge( repo, targetRepo );
					if( rr == null )
					{
						rr = new RepositoryRelation();
						repoGraph.addEdge( repo, targetRepo, rr );
					}

					if( relation.getClass() == ParentRelation.class )
						rr.addRelation( "PARENT" );
					else if( relation.getClass() == DependencyRelation.class )
						rr.addRelation( "DEP" );
					else if( relation.getClass() == BuildDependencyRelation.class )
						rr.addRelation( "BUILD" );
				}
			}

			String graphFileName = "graph-session-" + System.identityHashCode( session ) + "-" + new Date().getTime() + ".graphml";
			Writer writer = AppFactory.get().webServer().pushFile( graphFileName );
			exporter.export( writer, ng );
			writer.close();

			String graphReposFileName = "graph-repos-session-" + System.identityHashCode( session ) + "-" + new Date().getTime() + ".graphml";
			writer = AppFactory.get().webServer().pushFile( graphReposFileName );
			repoExporter.export( writer, repoGraph );
			writer.close();

			String url = AppFactory.get().webServer().getFileUrl( graphFileName );
			String urlRepos = AppFactory.get().webServer().getFileUrl( graphReposFileName );

			log.html( "GraphML file for the whole dependency graph is available here : <a href='" + url + "' target='_blank'>" + url + "</a><br/>" );
			log.html( "GraphML file for the git repositories is available here : <a href='" + urlRepos + "' target='_blank'>" + urlRepos + "</a><br/>" );
		}
		catch( Exception e )
		{
			log.html( Tools.errorMessage( "Error ! : " + e.getMessage() ) );
		}
	}

	private String getGAVRepository( Session session, Gav gav )
	{
		Project project = session.projects().forGav( gav );
		if( project == null )
			return null;

		return GitTools.findGitRoot( project.getPomFile().getParent() );
	}

	@Help( "displays a graph on the server machine" )
	public void server( Session session, Log log )
	{
		server( session, null, log );
	}

	@Help( "displays a graph on the server machine. Parameter is the filter for GAVs" )
	public void server( Session session, String filter, Log log )
	{
		PomGraphReadTransaction tx = session.graph().read();

		if( filter != null )
			filter = filter.toLowerCase();

		DirectedGraph<Gav, Relation> fullGraph = tx.internalGraph();

		Set<Gav> vertexSubset = new HashSet<>();
		for( Gav gav : fullGraph.vertexSet() )
		{
			if( filter == null || gav.toString().toLowerCase().contains( filter ) )
				vertexSubset.add( gav );
		}

		Set<Relation> edgeSubset = new HashSet<>();
		for( Relation r : fullGraph.edgeSet() )
		{
			if( vertexSubset.contains( fullGraph.getEdgeSource( r ) ) && vertexSubset.contains( fullGraph.getEdgeTarget( r ) ) )
				edgeSubset.add( r );
		}

		DirectedSubgraph<Gav, Relation> subGraph = new DirectedSubgraph<>( fullGraph, vertexSubset, edgeSubset );

		JGraphXAdapter<Gav, Relation> ga = new JGraphXAdapter<>( subGraph );

		new GraphFrame( ga );

		mxHierarchicalLayout layout = new mxHierarchicalLayout( ga );
		// mxFastOrganicLayout layout = new mxFastOrganicLayout( ga );
		layout.setUseBoundingBox( true );
		// layout.setForceConstant( 200 );
		layout.execute( ga.getDefaultParent() );

		log.html( "ok, graph displayed on the server.<br/>" );
	}
}
