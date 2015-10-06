package fr.lteconsulting.pomexplorer.graph.relation;

import fr.lteconsulting.pomexplorer.GAV;

public class ParentRelation extends Relation
{
	public ParentRelation(GAV source, GAV target)
	{
		super(source, target, RelationType.PARENT);
	}

	@Override
	public String toString()
	{
		return "parent";
	}
}
