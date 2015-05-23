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
import fr.lteconsulting.pomexplorer.graph.relation.ParentRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation.Type;

public class PomGraph
{
	private DirectedGraph<GAV, Relation> g = new DirectedMultigraph<GAV, Relation>( Relation.class );

	public Set<GAV> getGavs()
	{
		return g.vertexSet();
	}

	public DirectedGraph<GAV, Relation> getGraphInternal()
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

	public GAV getParent( GAV gav )
	{
		List<ParentRelation> relations = parentRelations( g.outgoingEdgesOf( gav ) );

		if( relations == null || relations.size() != 1 )
			return null;

		GAV parent = g.getEdgeTarget( relations.get( 0 ) );

		return parent;
	}
	
	public Set<GAV> getChildren( GAV gav )
	{
		Set<GAV> res = new HashSet<>();
		
		List<ParentRelation> relations = parentRelations( g.incomingEdgesOf( gav ) );
		for( ParentRelation relation : relations )
			res.add( g.getEdgeSource( relation ) );
		
		return res;
	}

	public Set<GAV> getDependencies( GAV gav )
	{
		Set<GAV> res = new HashSet<>();
		
		List<DependencyRelation> relations = dependencyRelations( g.outgoingEdgesOf( gav ) );

		for( DependencyRelation relation : relations )
		{
			GAV dependencyGav = g.getEdgeTarget( relation );
			
			res.add( dependencyGav );
		}
		
		return res;
	}
	
	public Set<GAV> getDependents( GAV gav )
	{
		Set<GAV> res = new HashSet<>();
		
		List<DependencyRelation> relations = dependencyRelations( g.incomingEdgesOf( gav ) );

		for( DependencyRelation relation : relations )
		{
			GAV dependencyGav = g.getEdgeSource( relation );
			
			res.add( dependencyGav );
		}
		
		return res;
	}

	private List<ParentRelation> parentRelations( Set<Relation> relations )
	{
		@SuppressWarnings( { "unchecked", "rawtypes" } )
		List<ParentRelation> res = (List<ParentRelation>)(List)Tools.filter( relations, new Func1<Relation, Boolean>()
		{
			@Override
			public Boolean exec( Relation relation )
			{
				return relation.getType() == Type.PARENT;
			}
		} );
		
		return res;
	}

	private List<DependencyRelation> dependencyRelations( Set<Relation> relations )
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
