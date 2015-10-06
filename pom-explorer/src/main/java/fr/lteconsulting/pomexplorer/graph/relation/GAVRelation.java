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

	@Override
	public String toString()
	{
		return "GAVRelation [source=" + source + ", target=" + target + ", relation=" + relation + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((relation == null) ? 0 : relation.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GAVRelation other = (GAVRelation)obj;
		if (relation == null)
		{
			if (other.relation != null)
				return false;
		}
		else if (!relation.equals(other.relation))
			return false;
		if (source == null)
		{
			if (other.source != null)
				return false;
		}
		else if (!source.equals(other.source))
			return false;
		if (target == null)
		{
			if (other.target != null)
				return false;
		}
		else if (!target.equals(other.target))
			return false;
		return true;
	}
}
