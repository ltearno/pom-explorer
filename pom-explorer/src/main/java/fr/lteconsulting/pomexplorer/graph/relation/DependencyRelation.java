package fr.lteconsulting.pomexplorer.graph.relation;

import fr.lteconsulting.pomexplorer.GAV;

public class DependencyRelation extends Relation
{
	public enum Scope
	{
		COMPILE,
		PROVIDED,
		RUNTIME,
		TEST,
		SYSTEM,
		IMPORT;

		public static Scope fromString( String scope )
		{
			if( scope == null || scope.isEmpty() )
				return COMPILE;

			return Scope.valueOf( scope.toUpperCase() );
		}

		@Override
		public String toString()
		{
			return super.toString().toLowerCase();
		}
	}

	private final Scope scope;

	private final String classifier;

	public DependencyRelation( GAV source, GAV target, String scope, String classifier )
	{
		super( source, target, RelationType.DEPENDENCY );
		this.scope = Scope.fromString( scope );
		if( this.scope == null )
			throw new RuntimeException( "Scope is null for dependency between " + source + " to " + target );
		this.classifier = classifier;
	}

	public Scope getScope()
	{
		return scope;
	}

	public String getClassifier()
	{
		return classifier;
	}

	@Override
	public String toString()
	{
		String res = "";
		String sep = "";

		res += "scope:" + scope;
		sep = ", ";

		if( classifier != null && !classifier.isEmpty() )
		{
			res += sep + "classifier:" + classifier;
			sep = ", ";
		}

		return res;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if( this == obj )
			return true;
		if( !super.equals( obj ) )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		DependencyRelation other = (DependencyRelation) obj;
		if( classifier == null )
		{
			if( other.classifier != null )
				return false;
		}
		else if( !classifier.equals( other.classifier ) )
			return false;
		if( scope == null )
		{
			if( other.scope != null )
				return false;
		}
		else if( !scope.equals( other.scope ) )
			return false;
		return true;
	}
}
