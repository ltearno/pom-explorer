package fr.lteconsulting.pomexplorer.graph.relation;

public class DependencyRelation extends Relation
{
	private final String scope;

	private final String classifier;

	public DependencyRelation( String scope, String classifier )
	{
		super( RelationType.DEPENDENCY );
		this.scope = scope;
		this.classifier = classifier;
	}

	public String getScope()
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

		if( scope != null && !scope.isEmpty() )
		{
			res += "scope:" + scope;
			sep = ", ";
		}

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
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DependencyRelation other = (DependencyRelation)obj;
		if (classifier == null)
		{
			if (other.classifier != null)
				return false;
		}
		else if (!classifier.equals(other.classifier))
			return false;
		if (scope == null)
		{
			if (other.scope != null)
				return false;
		}
		else if (!scope.equals(other.scope))
			return false;
		return true;
	}
}
