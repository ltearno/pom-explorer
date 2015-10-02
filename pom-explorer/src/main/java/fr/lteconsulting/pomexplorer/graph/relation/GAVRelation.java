package fr.lteconsulting.pomexplorer.graph.relation;

import fr.lteconsulting.pomexplorer.GAV;

public class GAVRelation<T extends Relation>
{
	private final GAV source;
	private final GAV target;
	private final T relation;

	public GAVRelation( GAV source, GAV target, T relation )
	{
		this.source = source;
		this.target = target;
		this.relation = relation;
	}

	public GAV getTarget()
	{
		return target;
	}

	public T getRelation()
	{
		return relation;
	}

	public GAV getSource()
	{
		return source;
	}
}
