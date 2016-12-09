package fr.lteconsulting.pomexplorer.change.graph;

import fr.lteconsulting.pomexplorer.change.Change;
import fr.lteconsulting.pomexplorer.graph.relation.BuildDependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.ParentRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.model.Dependency;
import fr.lteconsulting.pomexplorer.model.DependencyKey;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.model.GroupArtifact;

public abstract class GraphChange extends Change
{
	protected final Gav source;
	protected final Gav newValue;

	protected GraphChange( Gav gav, Gav newTarget )
	{
		this.source = gav;
		this.newValue = newTarget;
	}

	public interface Visitor
	{
		void visit( ParentChange parentChange );

		void visit( DependencyChange change );

		void visit( PluginChange change );

		void visit( GavChange change );
	}

	public void visit( Visitor v )
	{
		if( this instanceof ParentChange )
			v.visit( (ParentChange) this );
		if( this instanceof DependencyChange )
			v.visit( (DependencyChange) this );
		if( this instanceof PluginChange )
			v.visit( (PluginChange) this );
		if( this instanceof GavChange )
			v.visit( (GavChange) this );
	}

	public abstract static class RelationChange extends GraphChange
	{
		enum Action
		{
			SET,
			REMOVE;
		}

		protected final Action action;

		public static RelationChange create( Relation relation, Gav newTarget )
		{
			Gav source = relation.getSource();

			if( relation instanceof ParentRelation )
				return new ParentChange( source, newTarget );

			if( relation instanceof BuildDependencyRelation )
				return new PluginChange( source, new GroupArtifact( relation.getTarget().getGroupId(), relation.getTarget().getArtifactId() ), newTarget );

			if( relation instanceof DependencyRelation )
			{
				DependencyRelation r = (DependencyRelation) relation;
				Dependency d = r.getDependency();
				return new DependencyChange( source, new DependencyKey( d.getGroupId(), d.getArtifactId(), d.getClassifier(), d.getType() ), newTarget );
			}

			return null;
		}

		public static RelationChange create( Gav source, String location, Gav newTarget )
		{
			if( location == null )
				return null;

			int i = location.indexOf( ":" );
			String section;
			String other;
			if( i < 0 )
			{
				section = location;
				other = null;
			}
			else
			{
				section = location.substring( 0, i );
				other = location.substring( i + 1 );
			}

			switch( section )
			{
				case "parent":
					return new ParentChange( source, newTarget );
				case "d":
					return new DependencyChange( source, DependencyKey.parse( other ), newTarget );
				case "p":
					return new PluginChange( source, GroupArtifact.parse( other ), newTarget );
				default:
					return null;
			}
		}

		protected RelationChange( Gav source, Gav newTarget )
		{
			super( source, newTarget );
			this.action = newTarget != null ? Action.SET : Action.REMOVE;
		}
	}

	public static class ParentChange extends RelationChange
	{
		public ParentChange( Gav source, Gav newParent )
		{
			super( source, newParent );
		}

		@Override
		public String toString()
		{
			if( action == Action.SET )
				return "set " + source + " parent to " + newValue;
			else if( action == Action.REMOVE )
				return "remove " + source + "'s link to its parent";
			return "unknown parent change";
		}
	}

	public static class DependencyChange extends RelationChange
	{
		private final DependencyKey relationKey;

		public DependencyChange( Gav source, DependencyKey relationKey, Gav newTarget )
		{
			super( source, newTarget );
			this.relationKey = relationKey;
		}

		public DependencyKey getRelationKey()
		{
			return relationKey;
		}

		@Override
		public String toString()
		{
			if( action == Action.SET )
				return "set " + source + "'s dependency to '" + relationKey + "' to '" + newValue + "'";
			else if( action == Action.REMOVE )
				return "remove " + source + " dependency to '" + relationKey + "'";
			return "unknown dependency change";
		}
	}

	public static class PluginChange extends RelationChange
	{
		private final GroupArtifact relationKey;

		public PluginChange( Gav source, GroupArtifact relationKey, Gav newTarget )
		{
			super( source, newTarget );
			this.relationKey = relationKey;
		}

		public GroupArtifact getRelationKey()
		{
			return relationKey;
		}

		@Override
		public String toString()
		{
			if( action == Action.SET )
				return "set " + source + "'s build plugin '" + relationKey + "' to '" + newValue + "'";
			else if( action == Action.REMOVE )
				return "remove " + source + " build plugin '" + relationKey + "'";
			return "unknown dependency change";
		}
	}

	public static class GavChange extends GraphChange
	{
		public GavChange( Gav gav, Gav newGav )
		{
			super( gav, newGav );
		}

		@Override
		public String toString()
		{
			return "change " + source + " to " + newValue;
		}
	}

	public Gav getSource()
	{
		return source;
	}

	public Gav getNewValue()
	{
		return newValue;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((newValue == null) ? 0 : newValue.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		GraphChange other = (GraphChange) obj;
		if( newValue == null )
		{
			if( other.newValue != null )
				return false;
		}
		else if( !newValue.equals( other.newValue ) )
			return false;
		if( source == null )
		{
			if( other.source != null )
				return false;
		}
		else if( !source.equals( other.source ) )
			return false;
		return true;
	}
}
