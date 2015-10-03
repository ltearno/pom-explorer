package fr.lteconsulting.pomexplorer.graph.relation;

public abstract class Relation
{
	/**
	 * Whether it's a normal dependency, a build one or a parent one.
	 * 
	 * @author Arnaud
	 *
	 */
	public enum RelationType
	{
		DEPENDENCY( "D" ),
		BUILD_DEPENDENCY( "B" ),
		PARENT( "P" );

		private final String shortName;

		RelationType( String shortName )
		{
			this.shortName = shortName;
		}

		public String shortName()
		{
			return shortName;
		}
	}

	public interface RelationVisitor
	{
		void onDependencyRelation( DependencyRelation relation );

		void onBuildDependencyRelation( BuildDependencyRelation relation );

		void onParentRelation( ParentRelation relation );
	}

	private final RelationType type;

	public Relation( RelationType type )
	{
		this.type = type;
	}

	public RelationType getRelationType()
	{
		return type;
	}

	public DependencyRelation asDependencyRelation()
	{
		if( type != RelationType.DEPENDENCY )
			return null;

		return (DependencyRelation) this;
	}

	public BuildDependencyRelation asBuildDependencyRelation()
	{
		if( type != RelationType.BUILD_DEPENDENCY )
			return null;

		return (BuildDependencyRelation) this;
	}

	public ParentRelation asParentRelation()
	{
		if( type != RelationType.PARENT )
			return null;

		return (ParentRelation) this;
	}

	public void visit( RelationVisitor visitor )
	{
		switch( type )
		{
			case DEPENDENCY:
				visitor.onDependencyRelation( asDependencyRelation() );
				break;
			case BUILD_DEPENDENCY:
				visitor.onBuildDependencyRelation( asBuildDependencyRelation() );
				break;
			case PARENT:
				visitor.onParentRelation( asParentRelation() );
				break;
		}
	}
}
