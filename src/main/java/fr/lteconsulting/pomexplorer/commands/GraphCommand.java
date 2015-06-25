package fr.lteconsulting.pomexplorer.commands;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.GraphFrame;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.graph.Repository;
import fr.lteconsulting.pomexplorer.graph.RepositoryRelation;
import fr.lteconsulting.pomexplorer.graph.relation.BuildDependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.ParentRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

public class GraphCommand
{
	@Help("displays an interactive 3d WebGL graph of the pom data")
	public String main(WorkingSession session)
	{
		StringBuilder log = new StringBuilder();

		String url = "graph.html?session=" + System.identityHashCode(session);
		log.append("To display the graph, go to : <a href='" + url + "' target='_blank'>" + url + "</a><br/>");

		return log.toString();
	}

	private boolean isOkGav(GAV gav)
	{
		return gav.toString().contains("fr");
	}

	private boolean isOkRelation(Relation relation)
	{
		if (relation instanceof BuildDependencyRelation)
			return false;

		if (relation instanceof DependencyRelation)
		{
			if ("test".equals(relation.asDependencyRelation().getScope().toLowerCase()))
				return false;
		}

		return true;
	}

	@Help("Exports a GraphML file")
	public String export(WorkingSession session)
	{
		try
		{
			GraphMLExporter<GAV, Relation> exporter = new GraphMLExporter<GAV, Relation>(
					new IntegerNameProvider<GAV>(),
					new VertexNameProvider<GAV>()
					{
						@Override
						public String getVertexName(GAV vertex)
						{
							return vertex.toString();
						}
					},
					new IntegerEdgeNameProvider<Relation>(),
					new EdgeNameProvider<Relation>()
					{
						@Override
						public String getEdgeName(Relation edge)
						{
							return edge.toString();
						}
					});

			GraphMLExporter<Repository, RepositoryRelation> repoExporter = new GraphMLExporter<Repository, RepositoryRelation>(
					new IntegerNameProvider<Repository>(),
					new VertexNameProvider<Repository>()
					{
						@Override
						public String getVertexName(Repository vertex)
						{
							return vertex.toString();
						}
					},
					new IntegerEdgeNameProvider<RepositoryRelation>(),
					new EdgeNameProvider<RepositoryRelation>()
					{
						@Override
						public String getEdgeName(RepositoryRelation edge)
						{
							return edge.toString();
						}
					});

			DirectedGraph<GAV, Relation> g = session.graph().internalGraph();

			DirectedGraph<GAV, Relation> ng = new DirectedMultigraph<GAV, Relation>(Relation.class);
			for (GAV gav : g.vertexSet())
			{
				if (!isOkGav(gav))
					continue;

				ng.addVertex(gav);

				for (Relation relation : g.outgoingEdgesOf(gav))
				{
					GAV target = g.getEdgeTarget(relation);

					if (!isOkGav(target))
						continue;

					if (!isOkRelation(relation))
						continue;

					ng.addVertex(target);

					ng.addEdge(gav, target, relation);
				}
			}

			DirectedGraph<Repository, RepositoryRelation> repoGraph = new DirectedMultigraph<Repository, RepositoryRelation>(
					RepositoryRelation.class);
			for (GAV gav : ng.vertexSet())
			{
				String repoPath = getGAVRepository(session, gav);
				if (repoPath == null)
					continue;

				Repository repo = new Repository(new File(repoPath).toPath());
				repoGraph.addVertex(repo);

				for (Relation relation : ng.outgoingEdgesOf(gav))
				{
					GAV target = ng.getEdgeTarget(relation);
					String targetRepoPath = getGAVRepository(session, target);
					if (targetRepoPath == null)
						continue;

					Repository targetRepo = new Repository(new File(targetRepoPath).toPath());


					if (repo.equals(targetRepo))
						continue;

					repoGraph.addVertex(targetRepo);

					RepositoryRelation rr = repoGraph.getEdge(repo, targetRepo);
					if (rr == null)
					{
						rr = new RepositoryRelation();

						System.out.println(repo + " ---> " + targetRepo + "   rel " + System.identityHashCode(relation));
						repoGraph.addEdge(repo, targetRepo, rr);
					}

					if (relation.getClass() == ParentRelation.class)
						rr.addRelation("PARENT");
					else if (relation.getClass() == DependencyRelation.class)
						rr.addRelation("DEP");
					else if (relation.getClass() == BuildDependencyRelation.class)
						rr.addRelation("BUILD");
				}
			}

			FileWriter fileWriter = new FileWriter("exportedGraph.graphml");
			exporter.export(fileWriter, ng);
			fileWriter.close();

			fileWriter = new FileWriter("exportedGraphRepos.graphml");
			repoExporter.export(fileWriter, repoGraph);
			fileWriter.close();

			return "done.<br/>";
		}
		catch (Exception e)
		{
			return "Error ! : " + e.getMessage() + "<br/>";
		}
	}

	private String getGAVRepository(WorkingSession session, GAV gav)
	{
		Project project = session.projects().get(gav);
		if (project == null)
			return null;

		return findGitRoot(project.getPomFile().getParent());
	}

	private String findGitRoot(String path)
	{
		if (path == null)
			return null;

		Path gitPath = Paths.get(path, ".git");
		File gitPathFile = gitPath.toFile();
		if (gitPathFile.exists() && gitPathFile.isDirectory())
			return path;

		Path parentPath = Paths.get(path).getParent();
		if (parentPath == null)
			return null;

		return findGitRoot(parentPath.toString());
	}

	@Help("displays a graph on the server machine")
	public String server(WorkingSession session)
	{
		return server(session, null);
	}

	@Help("displays a graph on the server machine. Parameter is the filter for GAVs")
	public String server(WorkingSession session, String filter)
	{
		if (filter != null)
			filter = filter.toLowerCase();

		DirectedGraph<GAV, Relation> fullGraph = session.graph().internalGraph();

		Set<GAV> vertexSubset = new HashSet<>();
		for (GAV gav : fullGraph.vertexSet())
		{
			// if (gav.toString().contains("socle-test") ||
			// gav.toString().contains("legacy"))
			// continue;
			if (filter == null || gav.toString().toLowerCase().contains(filter))
			{
				vertexSubset.add(gav);
			}
		}

		Set<Relation> edgeSubset = new HashSet<>();
		for (Relation r : fullGraph.edgeSet())
		{
			if (vertexSubset.contains(fullGraph.getEdgeSource(r)) && vertexSubset.contains(fullGraph.getEdgeTarget(r)))
				edgeSubset.add(r);
		}

		DirectedSubgraph<GAV, Relation> subGraph = new DirectedSubgraph<>(fullGraph, vertexSubset, edgeSubset);

		JGraphXAdapter<GAV, Relation> ga = new JGraphXAdapter<>(subGraph);

		new GraphFrame(ga);

		mxHierarchicalLayout layout = new mxHierarchicalLayout(ga);
		// mxFastOrganicLayout layout = new mxFastOrganicLayout( ga );
		layout.setUseBoundingBox(true);
		// layout.setForceConstant( 200 );
		layout.execute(ga.getDefaultParent());

		return "ok, graph displayed on the server.";
	}
}
