package fr.lteconsulting.pomexplorer.graph.relation;

import fr.lteconsulting.pomexplorer.GAV;

public class GAVDependencyRelation
{
	private final GAV gav;
	private final DependencyRelation dependencyRelation;
	
	public GAVDependencyRelation( GAV gav, DependencyRelation dependencyRelation )
	{
		this.gav = gav;
		this.dependencyRelation = dependencyRelation;
	}

	public GAV getGav()
	{
		return gav;
	}

	public DependencyRelation getDependencyRelation()
	{
		return dependencyRelation;
	}
}
