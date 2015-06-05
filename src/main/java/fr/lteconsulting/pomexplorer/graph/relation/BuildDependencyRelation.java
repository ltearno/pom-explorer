package fr.lteconsulting.pomexplorer.graph.relation;

public class BuildDependencyRelation extends Relation
{
	public BuildDependencyRelation()
	{
		super( RelationType.BUILD_DEPENDENCY );
	}
	
	@Override
	public String toString()
	{
		return "[build]";
	}
}
