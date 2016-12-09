package fr.lteconsulting.pomexplorer.graph.relation;

import fr.lteconsulting.pomexplorer.model.Gav;

public class ParentRelation extends Relation
{
	public ParentRelation( Gav source, Gav target )
	{
		super( source, target, RelationType.PARENT );
	}

	@Override
	public String toString()
	{
		return "parent";
	}
}
