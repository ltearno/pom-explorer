package fr.lteconsulting.pomexplorer.graph.relation;

public interface RelationVisitor
{
	//TODO would break backward compatibility thus not yet added yet
	//void onDependencyManagementRelation( DependencyRelation relation );

	void onDependencyRelation( DependencyRelation relation );

	void onBuildDependencyRelation( BuildDependencyRelation relation );

	void onParentRelation( ParentRelation relation );
}