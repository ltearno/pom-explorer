package fr.lteconsulting.pomexplorer.graph.relation;

import fr.lteconsulting.pomexplorer.model.Gav;

public abstract class Relation
{
	protected final RelationType type;
	protected final Gav source;
	protected final Gav target;

	protected Relation( Gav source, Gav target, RelationType type )
	{
		this.type = type;
		this.source = source;
		this.target = target;
	}

	public Gav getSource()
	{
		return source;
	}

	public Gav getTarget()
	{
		return target;
	}

	public final RelationType getRelationType()
	{
		return type;
	}

	public final DependencyRelation asDependencyRelation()
	{
		if( type != RelationType.DEPENDENCY )
			return null;

		return (DependencyRelation) this;
	}

	public final BuildDependencyRelation asBuildDependencyRelation()
	{
		if( type != RelationType.BUILD_DEPENDENCY )
			return null;

		return (BuildDependencyRelation) this;
	}

	public final ParentRelation asParentRelation()
	{
		if( type != RelationType.PARENT )
			return null;

		return (ParentRelation) this;
	}

	public final void visit( RelationVisitor visitor )
	{
		switch( type )
		{
			case DEPENDENCY:
				visitor.onDependencyRelation( asDependencyRelation() );
				break;
			case BUILD_DEPENDENCY:
				visitor.onBuildDependencyRelation( asBuildDependencyRelation() );
				break;
			case PARENT:
				visitor.onParentRelation( asParentRelation() );
				break;
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Relation other = (Relation) obj;
		if( source == null )
		{
			if( other.source != null )
				return false;
		}
		else if( !source.equals( other.source ) )
			return false;
		if( target == null )
		{
			if( other.target != null )
				return false;
		}
		else if( !target.equals( other.target ) )
			return false;
		if( type != other.type )
			return false;
		return true;
	}
}
