package fr.lteconsulting.pomexplorer.graph.relation;

public class DependencyRelation extends Relation
{
	private final String scope;

	private final String classifier;

	public DependencyRelation( String scope, String classifier )
	{
		super( Type.DEPENDENCY );
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
		return "scope: " + scope + ", classifier:" + classifier;
	}
}
