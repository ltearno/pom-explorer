package fr.lteconsulting.pomexplorer.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;

import fr.lteconsulting.hexa.client.tools.Func1;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.graph.relation.BuildDependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.GAVRelation;
import fr.lteconsulting.pomexplorer.graph.relation.ParentRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation.RelationType;

public class PomGraph
{
	private DirectedGraph<GAV, Relation> g = new DirectedMultigraph<GAV, Relation>( Relation.class );

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

	public boolean hasArtifact( GAV gav )
	{
		return g.containsVertex( gav );
	}

	public boolean addGav( GAV gav )
	{
		return g.addVertex( gav );
	}

	public boolean addRelation( GAV source, GAV target, Relation relation )
	{
		return g.addEdge( source, target, relation );
	}

	public GAV parent( GAV gav )
	{
		Set<GAVRelation<ParentRelation>> relations = filterParentRelations( relations( gav ) );

		if( relations == null || relations.size() != 1 )
			return null;

		GAV parent = relations.iterator().next().getTarget();

		return parent;
	}

	public Set<GAV> children( GAV gav )
	{
		Set<GAV> res = new HashSet<>();

		Set<GAVRelation<ParentRelation>> relations = filterParentRelations( relationsReverse( gav ) );
		for( GAVRelation<ParentRelation> relation : relations )
			res.add( relation.getSource() );

		return res;
	}

	/**
	 * Gets the outgoing relations of a GAV
	 * 
	 * @param gav
	 * @return
	 */
	public Set<GAVRelation<Relation>> relations( GAV gav )
	{
		Set<GAVRelation<Relation>> res = new HashSet<>();
		relations( gav, res );
		return res;
	}

	public void relations( GAV gav, Set<GAVRelation<Relation>> set )
	{
		Set<Relation> relations = g.outgoingEdgesOf( gav );
		for( Relation r : relations )
			set.add( new GAVRelation<Relation>( gav, g.getEdgeTarget( r ), r ) );
	}

	/**
	 * Recursively gets the outgoing relations of a GAV
	 * 
	 * @param gav
	 * @return
	 */
	public Set<GAVRelation<Relation>> relationsRec( GAV gav )
	{
		Set<GAVRelation<Relation>> res = new HashSet<>();
		relationsRec( gav, res );
		return res;
	}

	public void relationsRec( GAV gav, Set<GAVRelation<Relation>> set )
	{
		if( !g.containsVertex( gav ) )
			return;
		Set<Relation> relations = g.outgoingEdgesOf( gav );
		for( Relation r : relations )
		{
			GAV target = g.getEdgeTarget( r );
			set.add( new GAVRelation<Relation>( gav, target, r ) );
			relationsRec( target, set );
		}
	}

	/**
	 * Gets the ingoing relations of a GAV
	 * 
	 * @param gav
	 * @return
	 */
	public Set<GAVRelation<Relation>> relationsReverse( GAV gav )
	{
		Set<GAVRelation<Relation>> res = new HashSet<>();
		relationsReverse( gav, res );
		return res;
	}

	public void relationsReverse( GAV gav, Set<GAVRelation<Relation>> set )
	{
		if( !g.containsVertex( gav ) )
			return;

		Set<Relation> relations = g.incomingEdgesOf( gav );
		for( Relation r : relations )
			set.add( new GAVRelation<Relation>( g.getEdgeSource( r ), gav, r ) );
	}

	/**
	 * Recursively gets the ingoing relations of a GAV
	 * 
	 * @param gav
	 * @return
	 */
	public Set<GAVRelation<Relation>> relationsReverseRec( GAV gav )
	{
		Set<GAVRelation<Relation>> res = new HashSet<>();
		relationsReverseRec( gav, res );
		return res;
	}

	public void relationsReverseRec( GAV gav, Set<GAVRelation<Relation>> set )
	{
		Set<Relation> relations = g.incomingEdgesOf( gav );
		for( Relation r : relations )
		{
			GAV source = g.getEdgeSource( r );
			set.add( new GAVRelation<Relation>( source, gav, r ) );
			relationsReverseRec( source, set );
		}
	}

	public Set<GAVRelation<DependencyRelation>> dependencies( GAV gav )
	{
		return filterDependencyRelations( relations( gav ) );
	}

	public Set<GAVRelation<DependencyRelation>> dependenciesRec( GAV gav )
	{
		return filterDependencyRelations( relationsRec( gav ) );
	}

	public Set<GAVRelation<BuildDependencyRelation>> buildDependencies( GAV gav )
	{
		return filterBuildDependencyRelations( relations( gav ) );
	}

	public Set<GAVRelation<BuildDependencyRelation>> buildDependenciesRec( GAV gav )
	{
		return filterBuildDependencyRelations( relationsRec( gav ) );
	}

	public Set<GAVRelation<DependencyRelation>> dependents( GAV gav )
	{
		return filterDependencyRelations( relationsReverse( gav ) );
	}

	public Set<GAVRelation<DependencyRelation>> dependentsRec( GAV gav )
	{
		return filterDependencyRelations( relationsReverseRec( gav ) );
	}

	public Set<GAVRelation<BuildDependencyRelation>> buildDependents( GAV gav )
	{
		return filterBuildDependencyRelations( relationsReverse( gav ) );
	}

	public Set<GAVRelation<BuildDependencyRelation>> buildDependentsRec( GAV gav )
	{
		return filterBuildDependencyRelations( relationsReverseRec( gav ) );
	}

	private Set<GAVRelation<ParentRelation>> filterParentRelations( Set<GAVRelation<Relation>> relations )
	{
		@SuppressWarnings( { "unchecked", "rawtypes" } )
		List<GAVRelation<ParentRelation>> res = (List<GAVRelation<ParentRelation>>) (List) Tools.filter( relations, new Func1<GAVRelation<Relation>, Boolean>()
		{
			@Override
			public Boolean exec( GAVRelation<Relation> relation )
			{
				return relation.getRelation().getRelationType() == RelationType.PARENT;
			}
		} );

		Set<GAVRelation<ParentRelation>> result = new HashSet<>();
		result.addAll( res );

		return result;
	}

	private Set<GAVRelation<DependencyRelation>> filterDependencyRelations( Set<GAVRelation<Relation>> relations )
	{
		@SuppressWarnings( { "rawtypes", "unchecked" } )
		List<GAVRelation<DependencyRelation>> result = (List<GAVRelation<DependencyRelation>>) (List) Tools.filter( relations, new Func1<GAVRelation<Relation>, Boolean>()
		{
			@Override
			public Boolean exec( GAVRelation<Relation> relation )
			{
				return relation.getRelation().getRelationType() == RelationType.DEPENDENCY;
			}
		} );

		Set<GAVRelation<DependencyRelation>> res = new HashSet<>();
		res.addAll( result );

		return res;
	}

	private Set<GAVRelation<BuildDependencyRelation>> filterBuildDependencyRelations( Set<GAVRelation<Relation>> relations )
	{
		@SuppressWarnings( { "rawtypes", "unchecked" } )
		List<GAVRelation<BuildDependencyRelation>> result = (List<GAVRelation<BuildDependencyRelation>>) (List) Tools.filter( relations, new Func1<GAVRelation<Relation>, Boolean>()
		{
			@Override
			public Boolean exec( GAVRelation<Relation> relation )
			{
				return relation.getRelation().getRelationType() == RelationType.BUILD_DEPENDENCY;
			}
		} );

		Set<GAVRelation<BuildDependencyRelation>> res = new HashSet<>();
		res.addAll( result );

		return res;
	}
}
