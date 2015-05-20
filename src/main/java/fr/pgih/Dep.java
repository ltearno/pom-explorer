package fr.pgih;

public class Dep
{
	private final String scope;

	private final String classifier;

	public Dep(String scope, String classifier)
	{
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
		return "depends(" + scope + ", " + classifier + ") on ";
	}

}
