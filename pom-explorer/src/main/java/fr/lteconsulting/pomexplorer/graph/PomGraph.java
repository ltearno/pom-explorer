package fr.lteconsulting.pomexplorer.graph;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.graph.relation.BuildDependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.ParentRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

public class PomGraph
{
	private DirectedGraph<GAV, Relation> g = new DirectedMultigraph<GAV, Relation>(Relation.class);

	public Set<GAV> gavs()
	{
		return g.vertexSet();
	}

	public Set<Relation> relations()
	{
		return g.edgeSet();
	}

	public DirectedGraph<GAV, Relation> internalGraph()
	{
		return g;
	}

	public boolean hasArtifact(GAV gav)
	{
		return g.containsVertex(gav);
	}

	public boolean addGav(GAV gav)
	{
		return g.addVertex(gav);
	}

	public boolean addRelation(Relation relation)
	{
		return g.addEdge(relation.getSource(), relation.getTarget(), relation);
	}

	public GAV parent(GAV gav)
	{
		Set<ParentRelation> relations = filterParentRelations(relations(gav));

		if (relations == null || relations.size() != 1)
			return null;

		GAV parent = relations.iterator().next().getTarget();

		return parent;
	}

	public Set<GAV> children(GAV gav)
	{
		Set<GAV> res = new HashSet<>();

		Set<ParentRelation> relations = filterParentRelations(relationsReverse(gav));
		for (ParentRelation relation : relations)
			res.add(relation.getSource());

		return res;
	}

	/**
	 * Gets the outgoing relations of a GAV
	 * 
	 * @param gav
	 * @return
	 */
	public Set<Relation> relations(GAV gav)
	{
		Set<Relation> res = new HashSet<>();
		relations(gav, res);
		return res;
	}

	/**
	 * Recursively gets the outgoing relations of a GAV
	 * 
	 * @param gav
	 * @return
	 */
	public Set<Relation> relationsRec(GAV gav)
	{
		Set<Relation> res = new HashSet<>();
		relationsRec(gav, res, new HashSet<>());
		return res;
	}

	/**
	 * Gets the ingoing relations of a GAV
	 * 
	 * @param gav
	 * @return
	 */
	public Set<Relation> relationsReverse(GAV gav)
	{
		Set<Relation> res = new HashSet<>();
		relationsReverse(gav, res);
		return res;
	}

	/**
	 * Recursively gets the ingoing relations of a GAV
	 * 
	 * @param gav
	 * @return
	 */
	public Set<Relation> relationsReverseRec(GAV gav)
	{
		Set<Relation> res = new HashSet<>();
		relationsReverseRec(gav, res, new HashSet<>());
		return res;
	}

	public Set<DependencyRelation> dependencies(GAV gav)
	{
		return filterDependencyRelations(relations(gav));
	}

	public Set<DependencyRelation> dependenciesRec(GAV gav)
	{
		return filterDependencyRelations(relationsRec(gav));
	}

	public Set<BuildDependencyRelation> buildDependencies(GAV gav)
	{
		return filterBuildDependencyRelations(relations(gav));
	}

	public Set<BuildDependencyRelation> buildDependenciesRec(GAV gav)
	{
		return filterBuildDependencyRelations(relationsRec(gav));
	}

	/**
	 * Returns the set of GAVs which depend directly on the one passed by parameter
	 * 
	 * @param gav
	 * @return
	 */
	public Set<DependencyRelation> dependents(GAV gav)
	{
		return filterDependencyRelations(relationsReverse(gav));
	}

	public Set<DependencyRelation> dependentsRec(GAV gav)
	{
		return filterDependencyRelations(relationsReverseRec(gav));
	}

	public Set<BuildDependencyRelation> buildDependents(GAV gav)
	{
		return filterBuildDependencyRelations(relationsReverse(gav));
	}

	public Set<BuildDependencyRelation> buildDependentsRec(GAV gav)
	{
		return filterBuildDependencyRelations(relationsReverseRec(gav));
	}

	private Set<ParentRelation> filterParentRelations(Set<Relation> relations)
	{
		Set<ParentRelation> res = new HashSet<>();
		relations.stream().filter(r -> r instanceof ParentRelation).map(r -> (ParentRelation)r)
				.forEach(r -> res.add(r));

		return res;
	}

	private Set<DependencyRelation> filterDependencyRelations(Set<Relation> relations)
	{
		Set<DependencyRelation> res = new HashSet<>();
		relations.stream().filter(r -> r instanceof DependencyRelation).map(r -> (DependencyRelation)r)
				.forEach(r -> res.add(r));

		return res;
	}

	private Set<BuildDependencyRelation> filterBuildDependencyRelations(Set<Relation> relations)
	{
		Set<BuildDependencyRelation> res = new HashSet<>();
		relations.stream().filter(r -> r instanceof BuildDependencyRelation).map(r -> (BuildDependencyRelation)r)
				.forEach(r -> res.add(r));

		return res;
	}

	private void relations(GAV gav, Set<Relation> set)
	{
		Set<Relation> relations = g.outgoingEdgesOf(gav);
		set.addAll(relations);
	}

	private void relationsRec(GAV gav, Set<Relation> set, Set<GAV> visitedGavs)
	{
		if (!g.containsVertex(gav))
			return;
		if (visitedGavs.contains(gav))
			return;
		visitedGavs.add(gav);
	
		Set<Relation> relations = g.outgoingEdgesOf(gav);
		set.addAll(relations);
		for (Relation r : relations)
		{
			GAV target = g.getEdgeTarget(r);
			relationsRec(target, set, visitedGavs);
		}
	}

	private void relationsReverse(GAV gav, Set<Relation> set)
	{
		if (!g.containsVertex(gav))
			return;
	
		set.addAll(g.incomingEdgesOf(gav));
	}

	private void relationsReverseRec(GAV gav, Set<Relation> set, Set<GAV> visitedGavs)
	{
		if (visitedGavs.contains(gav))
			return;
		visitedGavs.add(gav);
	
		Set<Relation> relations = g.incomingEdgesOf(gav);
		for (Relation r : relations)
		{
			GAV source = g.getEdgeSource(r);
			set.add(r);
			relationsReverseRec(source, set, visitedGavs);
		}
	}
}
