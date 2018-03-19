package fr.lteconsulting.pomexplorer.graph.relation;

import fr.lteconsulting.pomexplorer.model.Dependency;
import fr.lteconsulting.pomexplorer.model.Gav;

public class DependencyRelation extends DependencyLikeRelation
{

	public DependencyRelation( Gav source, Gav target, Dependency dependency )
	{
		super( source, target,dependency, RelationType.DEPENDENCY );
	}

	@Override
	public String toString()
	{
		return source + "--------->" + getDependency().toString();
	}
}
