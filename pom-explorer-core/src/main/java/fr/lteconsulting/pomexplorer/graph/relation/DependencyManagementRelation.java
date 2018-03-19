package fr.lteconsulting.pomexplorer.graph.relation;

import fr.lteconsulting.pomexplorer.model.Dependency;
import fr.lteconsulting.pomexplorer.model.Gav;

public class DependencyManagementRelation extends DependencyLikeRelation
{

	public DependencyManagementRelation( Gav source, Gav target, Dependency dependency )
	{
		super( source, target, dependency, RelationType.DEPENDENCY_MANAGEMENT );
	}

	@Override
	public String toString()
	{
		return source + "-(mgnt)->" + getDependency().toString();
	}

}
