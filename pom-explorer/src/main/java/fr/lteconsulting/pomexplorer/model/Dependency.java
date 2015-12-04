package fr.lteconsulting.pomexplorer.model;

import fr.lteconsulting.pomexplorer.graph.relation.Scope;

public class Dependency extends Gav
{
	private final Scope scope;
	private final String classifier;
	private final String type;

	public Dependency( Gav gav, Scope scope, String classifier, String type )
	{
		super( gav );

		this.scope = scope == null ? Scope.COMPILE : scope;
		this.classifier = classifier;
		this.type = type == null ? "jar" : type;
	}

	public Scope getScope()
	{
		return scope;
	}

	public String getClassifier()
	{
		return classifier;
	}

	public String getType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		return super.toString() + ":" + scope + (classifier != null ? (":" + classifier) : "") + (type != null ? (":" + type) : "");
	}

	public boolean sameGav( Gav gav )
	{
		return gav != null && groupId.equals( gav.groupId ) && artifactId.equals( gav.getArtifactId() ) && version.equals( gav.getVersion() );
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Dependency other = (Dependency) obj;
		if( classifier == null )
		{
			if( other.classifier != null )
				return false;
		}
		else if( !classifier.equals( other.classifier ) )
			return false;
		if( scope != other.scope )
			return false;
		if( type == null )
		{
			if( other.type != null )
				return false;
		}
		else if( !type.equals( other.type ) )
			return false;
		return true;
	}
}
