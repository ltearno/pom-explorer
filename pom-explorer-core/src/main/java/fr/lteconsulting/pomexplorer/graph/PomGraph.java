package fr.lteconsulting.pomexplorer.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import fr.lteconsulting.pomexplorer.graph.relation.*;
import org.jgrapht.graph.DirectedMultigraph;

import fr.lteconsulting.pomexplorer.model.Gav;

public class PomGraph
{
	private PomGraphReadTransaction readTransaction = null;

	private final AtomicReference<DirectedMultigraph<Gav, Relation>> graphReference = new AtomicReference<>( createGraph() );

	public PomGraphWriteTransaction write()
	{
		return new PomGraphWriteTransaction();
	}

	public PomGraphReadTransaction read()
	{
		if( readTransaction == null )
			readTransaction = new PomGraphReadTransaction( graphReference.get() );
		return readTransaction;
	}

	private DirectedMultigraph<Gav, Relation> copyGraph( DirectedMultigraph<Gav, Relation> graph )
	{
		DirectedMultigraph<Gav, Relation> newGraph = createGraph();
		for( Gav gav : graph.vertexSet() )
			newGraph.addVertex( gav );
		for( Relation edge : graph.edgeSet() )
			newGraph.addEdge( graph.getEdgeSource( edge ), graph.getEdgeTarget( edge ), edge );
		return newGraph;
	}

	private DirectedMultigraph<Gav, Relation> createGraph()
	{
		@SuppressWarnings( "unchecked" )
		Class<? extends Relation> edgeClass = Relation.class;
		return new DirectedMultigraph<>(edgeClass);
	}

	public static class PomGraphReadTransaction
	{
		protected final DirectedMultigraph<Gav, Relation> txGraph;

		public PomGraphReadTransaction( DirectedMultigraph<Gav, Relation> txGraph )
		{
			this.txGraph = txGraph;
		}

		public Set<Gav> gavs()
		{
			return txGraph.vertexSet();
		}

		public Set<Relation> relations()
		{
			return txGraph.edgeSet();
		}

		public boolean hasArtifact( Gav gav )
		{
			return txGraph.containsVertex( gav );
		}

		/**
		 * For read only purpose only !
		 */
		public DirectedMultigraph<Gav, Relation> internalGraph()
		{
			return txGraph;
		}

		public Gav sourceOf( Relation relation )
		{
			return relation.getSource();
		}

		public Gav targetOf( Relation relation )
		{
			return relation.getTarget();
		}

		public Gav parent( Gav gav )
		{
			Set<ParentRelation> relations = filterParentRelations( relations( gav ) );

			if( relations == null || relations.size() != 1 )
				return null;

			return txGraph.getEdgeTarget( relations.iterator().next() );
		}

		public Set<Gav> children( Gav gav )
		{
			Set<Gav> res = new HashSet<>();

			Set<ParentRelation> relations = filterParentRelations( relationsReverse( gav ) );
			for( ParentRelation relation : relations )
				res.add( txGraph.getEdgeSource( relation ) );

			return res;
		}

		/**
		 * Gets the outgoing relations of a GAV
		 */
		public Set<Relation> relations( Gav gav )
		{
			Set<Relation> res = new HashSet<>();
			relations( gav, res );
			return res;
		}

		/**
		 * Recursively gets the outgoing relations of a GAV
		 */
		public Set<Relation> relationsRec( Gav gav )
		{
			Set<Relation> res = new HashSet<>();
			relationsRec( gav, res, new HashSet<>() );
			return res;
		}

		/**
		 * Gets the ingoing relations of a GAV
		 */
		public Set<Relation> relationsReverse( Gav gav )
		{
			if( !txGraph.containsVertex( gav ) )
				return null;
			return txGraph.incomingEdgesOf( gav );
		}

		/**
		 * Recursively gets the ingoing relations of a GAV
		 */
		public Set<Relation> relationsReverseRec( Gav gav )
		{
			Set<Relation> res = new HashSet<>();
			relationsReverseRec( gav, res, new HashSet<>() );
			return res;
		}

		public Set<DependencyManagementRelation> dependenciesManagement( Gav gav )
		{
			return filterDependencyManagementRelations( relations( gav ) );
		}

		public Set<DependencyManagementRelation> dependenciesManagementRec( Gav gav )
		{
			return filterDependencyManagementRelations( relationsRec( gav ) );
		}

		public Set<DependencyRelation> dependencies( Gav gav )
		{
			return filterDependencyRelations( relations( gav ) );
		}

		public Set<DependencyRelation> dependenciesRec( Gav gav )
		{
			return filterDependencyRelations( relationsRec( gav ) );
		}

		public Set<BuildDependencyRelation> buildDependencies( Gav gav )
		{
			return filterBuildDependencyRelations( relations( gav ) );
		}

		public Set<BuildDependencyRelation> buildDependenciesRec( Gav gav )
		{
			return filterBuildDependencyRelations( relationsRec( gav ) );
		}

		/**
		 * Returns the set of GAVs which depend directly on the one passed by parameter
		 */
		public Set<DependencyRelation> dependents( Gav gav )
		{
			return filterDependencyRelations( relationsReverse( gav ) );
		}

		public Set<DependencyRelation> dependentsRec( Gav gav )
		{
			return filterDependencyRelations( relationsReverseRec( gav ) );
		}

		public Set<DependencyManagementRelation> dependentsManagement( Gav gav )
		{
			return filterDependencyManagementRelations( relationsReverse( gav ) );
		}

		public Set<DependencyManagementRelation> dependentsManagementRec( Gav gav )
		{
			return filterDependencyManagementRelations( relationsReverseRec( gav ) );
		}

		public Set<BuildDependencyRelation> buildDependents( Gav gav )
		{
			return filterBuildDependencyRelations( relationsReverse( gav ) );
		}

		public Set<BuildDependencyRelation> buildDependentsRec( Gav gav )
		{
			return filterBuildDependencyRelations( relationsReverseRec( gav ) );
		}

		private static Set<ParentRelation> filterParentRelations( Set<Relation> relations )
		{
            return filterRelations(relations, ParentRelation.class);
		}

        private static Set<DependencyRelation> filterDependencyRelations( Set<Relation> relations )
		{
            return filterRelations(relations, DependencyRelation.class);
		}

		private static Set<DependencyManagementRelation> filterDependencyManagementRelations( Set<Relation> relations )
		{
			return filterRelations(relations, DependencyManagementRelation.class);
		}


		private static Set<BuildDependencyRelation> filterBuildDependencyRelations( Set<Relation> relations )
		{
		    return filterRelations(relations, BuildDependencyRelation.class);
		}

        private static <T> Set<T> filterRelations(Set<Relation> relations, Class<T> clazz) {
            return relations.stream()
                    .filter(clazz::isInstance)
                    .map(clazz::cast)
                    .collect(Collectors.toSet());
        }

		private void relations( Gav gav, Set<Relation> set )
		{
			if( !txGraph.containsVertex( gav ) )
				return;

			set.addAll( txGraph.outgoingEdgesOf( gav ) );
		}

		private void relationsRec( Gav gav, Set<Relation> set, Set<Gav> visitedGavs )
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
				Gav target = txGraph.getEdgeTarget( r );
				relationsRec( target, set, visitedGavs );
			}
		}

		private void relationsReverseRec( Gav gav, Set<Relation> set, Set<Gav> visitedGavs )
		{
			if( visitedGavs.contains( gav ) )
				return;
			visitedGavs.add( gav );

			Set<Relation> relations = txGraph.incomingEdgesOf( gav );
			for( Relation r : relations )
			{
				Gav source = txGraph.getEdgeSource( r );
				set.add( r );
				relationsReverseRec( source, set, visitedGavs );
			}
		}
	}

	public class PomGraphWriteTransaction extends PomGraphReadTransaction
	{
		private PomGraphWriteTransaction()
		{
			super( copyGraph( graphReference.get() ) );
		}

		public void commit()
		{
			graphReference.set( txGraph );
			readTransaction = null;
		}

		public boolean addGav( Gav gav )
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
