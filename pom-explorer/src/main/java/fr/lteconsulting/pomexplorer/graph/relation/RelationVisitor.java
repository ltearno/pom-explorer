package fr.lteconsulting.pomexplorer.graph.relation;

public interface RelationVisitor
{
	void onDependencyRelation( DependencyRelation relation );

	void onBuildDependencyRelation( BuildDependencyRelation relation );

	void onParentRelation( ParentRelation relation );
}