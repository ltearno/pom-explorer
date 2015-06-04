package fr.lteconsulting.pomexplorer.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;

import fr.lteconsulting.hexa.client.tools.Func1;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.GAVDependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.ParentRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation.Type;

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
		List<ParentRelation> relations = filterParentRelations( g.outgoingEdgesOf( gav ) );

		if( relations == null || relations.size() != 1 )
			return null;

		GAV parent = g.getEdgeTarget( relations.get( 0 ) );

		return parent;
	}

	public Set<GAV> children( GAV gav )
	{
		Set<GAV> res = new HashSet<>();

		List<ParentRelation> relations = filterParentRelations( g.incomingEdgesOf( gav ) );
		for( ParentRelation relation : relations )
			res.add( g.getEdgeSource( relation ) );

		return res;
	}

	public Set<GAVDependencyRelation> dependencies( GAV gav )
	{
		Set<GAVDependencyRelation> res = new HashSet<>();

		dependencies( gav, res );

		return res;
	}
	
	private void dependencies( GAV gav, Set<GAVDependencyRelation> set )
	{
		List<DependencyRelation> relations = filterDependencyRelations( g.outgoingEdgesOf( gav ) );

		for( DependencyRelation relation : relations )
		{
			GAV dependencyGav = g.getEdgeTarget( relation );

			set.add( new GAVDependencyRelation( dependencyGav, relation ) );
		}
	}

	public Set<GAVDependencyRelation> dependenciesRec( GAV gav )
	{
		Set<GAVDependencyRelation> res = new HashSet<>();

		dependenciesRec( gav, res );

		return res;
	}

	public void dependenciesRec( GAV gav, Set<GAVDependencyRelation> set )
	{
		Set<GAVDependencyRelation> deps = dependencies( gav );
		for( GAVDependencyRelation d : deps )
		{
			set.add( d );
			dependenciesRec( d.getGav(), set );
		}
	}

	public Set<GAVDependencyRelation> dependents( GAV gav )
	{
		Set<GAVDependencyRelation> res = new HashSet<>();

		List<DependencyRelation> relations = filterDependencyRelations( g.incomingEdgesOf( gav ) );

		for( DependencyRelation relation : relations )
		{
			GAV dependencyGav = g.getEdgeSource( relation );

			res.add( new GAVDependencyRelation( dependencyGav, relation ) );
		}

		return res;
	}

	private List<ParentRelation> filterParentRelations( Set<Relation> relations )
	{
		@SuppressWarnings( { "unchecked", "rawtypes" } )
		List<ParentRelation> res = (List<ParentRelation>) (List) Tools.filter( relations, new Func1<Relation, Boolean>()
		{
			@Override
			public Boolean exec( Relation relation )
			{
				return relation.getType() == Type.PARENT;
			}
		} );

		return res;
	}

	private List<DependencyRelation> filterDependencyRelations( Set<Relation> relations )
	{
		@SuppressWarnings( { "rawtypes", "unchecked" } )
		List<DependencyRelation> result = (List<DependencyRelation>) (List) Tools.filter( relations, new Func1<Relation, Boolean>()
		{
			@Override
			public Boolean exec( Relation relation )
			{
				return relation.getType() == Type.DEPENDENCY;
			}
		} );

		return result;
	}
}
