package fr.lteconsulting.pomexplorer.graph.relation;

public class ParentRelation extends Relation
{
	public ParentRelation()
	{
		super( RelationType.PARENT );
	}

	@Override
	public String toString()
	{
		return "parent";
	}
}
