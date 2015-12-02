package fr.lteconsulting.pomexplorer.graph.relation;

import fr.lteconsulting.pomexplorer.GAV;

public class BuildDependencyRelation extends Relation
{
	public BuildDependencyRelation( GAV source, GAV target )
	{
		super( source, target, RelationType.BUILD_DEPENDENCY );
	}

	@Override
	public String toString()
	{
		return "[build]";
	}
}
