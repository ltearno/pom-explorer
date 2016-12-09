package fr.lteconsulting.pomexplorer.graph.relation;

import fr.lteconsulting.pomexplorer.model.Gav;

public class BuildDependencyRelation extends Relation
{
	public BuildDependencyRelation( Gav source, Gav target )
	{
		super( source, target, RelationType.BUILD_DEPENDENCY );
	}

	@Override
	public String toString()
	{
		return "[B] " + target;
	}
}
