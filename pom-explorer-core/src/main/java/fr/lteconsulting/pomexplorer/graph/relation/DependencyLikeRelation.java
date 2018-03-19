package fr.lteconsulting.pomexplorer.graph.relation;

import fr.lteconsulting.pomexplorer.model.Dependency;
import fr.lteconsulting.pomexplorer.model.Gav;

public abstract class DependencyLikeRelation extends Relation
{
	private final Dependency dependency;

	public DependencyLikeRelation( Gav source, Gav target, Dependency dependency, RelationType type )
	{
		super( source, target, type );

		this.dependency = dependency;
	}

	public Dependency getDependency()
	{
		return dependency;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dependency == null) ? 0 : dependency.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if( this == obj )
			return true;
		if( !super.equals( obj ) )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		DependencyLikeRelation other = (DependencyLikeRelation ) obj;
		if( dependency == null )
		{
			if( other.dependency != null )
				return false;
		}
		else if( !dependency.equals( other.dependency ) )
			return false;
		return true;
	}
}
