package fr.lteconsulting.pomexplorer.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.graph.relation.BuildDependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.ParentRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

public class PomGraph
{
	private PomGraphReadTransaction readTransaction = null;
	private AtomicReference<DirectedGraph<GAV, Relation>> graphReference = new AtomicReference<>( new DirectedMultigraph<GAV, Relation>( Relation.class ) );
	private final Object duplicateGraphLock = new Object();

	private DirectedMultigraph<GAV, Relation> duplicateGraph( DirectedGraph<GAV, Relation> graph )
	{
		synchronized( duplicateGraphLock )
		{
			DirectedMultigraph<GAV, Relation> newGraph = new DirectedMultigraph<GAV, Relation>( Relation.class );
			for( GAV gav : graph.vertexSet() )
				newGraph.addVertex( gav );
			for( Relation edge : graph.edgeSet() )
				newGraph.addEdge( graph.getEdgeSource( edge ), graph.getEdgeTarget( edge ), edge );
			return newGraph;
		}
	}

	public PomGraphWriteTransaction startTransaction()
	{
		return new PomGraphWriteTransaction();
	}

	public PomGraphReadTransaction read()
	{
		if( readTransaction == null )
			readTransaction = new PomGraphReadTransaction( graphReference.get() );
		return readTransaction;
	}

	public static class PomGraphReadTransaction
	{
		protected final DirectedGraph<GAV, Relation> txGraph;

		public PomGraphReadTransaction( DirectedGraph<GAV, Relation> txGraph )
		{
			this.txGraph = txGraph;
		}

		public Set<GAV> gavs()
		{
			return txGraph.vertexSet();
		}

		public Set<Relation> relations()
		{
			return txGraph.edgeSet();
		}

		public boolean hasArtifact( GAV gav )
		{
			return txGraph.containsVertex( gav );
		}

		/**
		 * For read only purpose only !
		 */
		public DirectedGraph<GAV, Relation> internalGraph()
		{
			return txGraph;
		}

		public GAV parent( GAV gav )
		{
			Set<ParentRelation> relations = filterParentRelations( relations( gav ) );

			if( relations == null || relations.size() != 1 )
				return null;

			GAV parent = relations.iterator().next().getTarget();

			return parent;
		}

		public Set<GAV> children( GAV gav )
		{
			Set<GAV> res = new HashSet<>();

			Set<ParentRelation> relations = filterParentRelations( relationsReverse( gav ) );
			for( ParentRelation relation : relations )
				res.add( relation.getSource() );

			return res;
		}

		/**
		 * Gets the outgoing relations of a GAV
		 * 
		 * @param gav
		 * @return
		 */
		public Set<Relation> relations( GAV gav )
		{
			Set<Relation> res = new HashSet<>();
			relations( gav, res );
			return res;
		}

		/**
		 * Recursively gets the outgoing relations of a GAV
		 * 
		 * @param gav
		 * @return
		 */
		public Set<Relation> relationsRec( GAV gav )
		{
			Set<Relation> res = new HashSet<>();
			relationsRec( gav, res, new HashSet<>() );
			return res;
		}

		/**
		 * Gets the ingoing relations of a GAV
		 * 
		 * @param gav
		 * @return
		 */
		public Set<Relation> relationsReverse( GAV gav )
		{
			Set<Relation> res = new HashSet<>();
			relationsReverse( gav, res );
			return res;
		}

		/**
		 * Recursively gets the ingoing relations of a GAV
		 * 
		 * @param gav
		 * @return
		 */
		public Set<Relation> relationsReverseRec( GAV gav )
		{
			Set<Relation> res = new HashSet<>();
			relationsReverseRec( gav, res, new HashSet<>() );
			return res;
		}

		public Set<DependencyRelation> dependencies( GAV gav )
		{
			return filterDependencyRelations( relations( gav ) );
		}

		public Set<DependencyRelation> dependenciesRec( GAV gav )
		{
			return filterDependencyRelations( relationsRec( gav ) );
		}

		public Set<BuildDependencyRelation> buildDependencies( GAV gav )
		{
			return filterBuildDependencyRelations( relations( gav ) );
		}

		public Set<BuildDependencyRelation> buildDependenciesRec( GAV gav )
		{
			return filterBuildDependencyRelations( relationsRec( gav ) );
		}

		/**
		 * Returns the set of GAVs which depend directly on the one passed by parameter
		 * 
		 * @param gav
		 * @return
		 */
		public Set<DependencyRelation> dependents( GAV gav )
		{
			return filterDependencyRelations( relationsReverse( gav ) );
		}

		public Set<DependencyRelation> dependentsRec( GAV gav )
		{
			return filterDependencyRelations( relationsReverseRec( gav ) );
		}

		public Set<BuildDependencyRelation> buildDependents( GAV gav )
		{
			return filterBuildDependencyRelations( relationsReverse( gav ) );
		}

		public Set<BuildDependencyRelation> buildDependentsRec( GAV gav )
		{
			return filterBuildDependencyRelations( relationsReverseRec( gav ) );
		}

		private Set<ParentRelation> filterParentRelations( Set<Relation> relations )
		{
			Set<ParentRelation> res = new HashSet<>();
			relations.stream().filter( r -> r instanceof ParentRelation ).map( r -> (ParentRelation) r )
					.forEach( r -> res.add( r ) );

			return res;
		}

		private Set<DependencyRelation> filterDependencyRelations( Set<Relation> relations )
		{
			Set<DependencyRelation> res = new HashSet<>();
			relations.stream().filter( r -> r instanceof DependencyRelation ).map( r -> (DependencyRelation) r )
					.forEach( r -> res.add( r ) );

			return res;
		}

		private Set<BuildDependencyRelation> filterBuildDependencyRelations( Set<Relation> relations )
		{
			Set<BuildDependencyRelation> res = new HashSet<>();
			relations.stream().filter( r -> r instanceof BuildDependencyRelation ).map( r -> (BuildDependencyRelation) r )
					.forEach( r -> res.add( r ) );

			return res;
		}

		private void relations( GAV gav, Set<Relation> set )
		{
			if( !txGraph.containsVertex( gav ) )
				return;

			set.addAll( txGraph.outgoingEdgesOf( gav ) );
		}

		private void relationsRec( GAV gav, Set<Relation> set, Set<GAV> visitedGavs )
		{
			if( !txGraph.containsVertex( gav ) )
				return;
			if( visitedGavs.contains( gav ) )
				return;
			visitedGavs.add( gav );

			Set<Relation> relations = txGraph.outgoingEdgesOf( gav );
			set.addAll( relations );
			for( Relation r : relations )
			{
				GAV target = txGraph.getEdgeTarget( r );
				relationsRec( target, set, visitedGavs );
			}
		}

		private void relationsReverse( GAV gav, Set<Relation> set )
		{
			if( !txGraph.containsVertex( gav ) )
				return;

			set.addAll( txGraph.incomingEdgesOf( gav ) );
		}

		private void relationsReverseRec( GAV gav, Set<Relation> set, Set<GAV> visitedGavs )
		{
			if( visitedGavs.contains( gav ) )
				return;
			visitedGavs.add( gav );

			Set<Relation> relations = txGraph.incomingEdgesOf( gav );
			for( Relation r : relations )
			{
				GAV source = txGraph.getEdgeSource( r );
				set.add( r );
				relationsReverseRec( source, set, visitedGavs );
			}
		}
	}

	public class PomGraphWriteTransaction extends PomGraphReadTransaction
	{
		private PomGraphWriteTransaction()
		{
			super( duplicateGraph( graphReference.get() ) );
		}

		public void commit()
		{
			graphReference.set( txGraph );
			readTransaction = null;
		}

		public boolean addGav( GAV gav )
		{
			return txGraph.addVertex( gav );
		}

		public boolean addRelation( Relation relation )
		{
			return txGraph.addEdge( relation.getSource(), relation.getTarget(), relation );
		}

		public void removeRelations( Collection<Relation> relations )
		{
			txGraph.removeAllEdges( relations );
		}
	}
}
