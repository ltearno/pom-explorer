package fr.lteconsulting.pomexplorer.graph.relation;

import fr.lteconsulting.pomexplorer.GAV;

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

	private final GAV source;

	private final GAV target;

	private final RelationType type;

	public Relation( GAV source, GAV target, RelationType type )
	{
		this.source = source;
		this.target = target;
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

	public GAV getSource()
	{
		return source;
	}

	public GAV getTarget()
	{
		return target;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		Relation other = (Relation) obj;
		if( source == null )
		{
			if( other.source != null )
				return false;
		}
		else if( !source.equals( other.source ) )
			return false;
		if( target == null )
		{
			if( other.target != null )
				return false;
		}
		else if( !target.equals( other.target ) )
			return false;
		if( type != other.type )
			return false;
		return true;
	}
}
