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
		
		if( scope != null && ! scope.isEmpty() )
		{
			res += "scope:" + scope;
			sep = ", ";
		}

		if( classifier != null && ! classifier.isEmpty() )
		{
			res += sep + "classifier:" + classifier;
			sep = ", ";
		}

		return res;
	}
}
