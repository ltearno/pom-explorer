package fr.lteconsulting.pomexplorer.graph.relation;

public abstract class Relation
{
	public enum Type
	{
		DEPENDENCY,
		PARENT;
	}

	private final Type type;

	public Relation( Type type )
	{
		this.type = type;
	}

	public Type getType()
	{
		return type;
	}
}
