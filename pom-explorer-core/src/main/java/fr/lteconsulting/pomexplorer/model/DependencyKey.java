package fr.lteconsulting.pomexplorer.model;

import fr.lteconsulting.pomexplorer.Tools;

public class DependencyKey implements Comparable<DependencyKey>
{
	final String groupId;
	final String artifactId;
	final String classifier;
	final String type;

	public static DependencyKey parse( String string )
	{
		if( string == null )
			return null;

		String[] parts = string.split( ":" );
		if( parts.length == 2 )
			return new DependencyKey( parts[0], parts[1], null, "jar" );
		else if( parts.length == 3 )
			return new DependencyKey( parts[0], parts[1], null, parts[2] );
		else if( parts.length == 4 )
			return new DependencyKey( parts[0], parts[1], parts[2], parts[3] );

		return null;
	}

	public DependencyKey( String groupId, String artifactId, String classifier, String type )
	{
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.classifier = classifier;
		this.type = (type == null || type.isEmpty()) ? "jar" : type;
	}

	public boolean isComplete()
	{
		return groupId != null && artifactId != null && type != null;
	}

	public String getGroupId()
	{
		return groupId;
	}

	public String getArtifactId()
	{
		return artifactId;
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
		return groupId + ":" + artifactId + (classifier != null ? (":" + classifier) : "") + ":" + type;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
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
		DependencyKey other = (DependencyKey) obj;
		if( artifactId == null )
		{
			if( other.artifactId != null )
				return false;
		}
		else if( !artifactId.equals( other.artifactId ) )
			return false;
		if( classifier == null )
		{
			if( other.classifier != null )
				return false;
		}
		else if( !classifier.equals( other.classifier ) )
			return false;
		if( groupId == null )
		{
			if( other.groupId != null )
				return false;
		}
		else if( !groupId.equals( other.groupId ) )
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

	@Override
	public int compareTo( DependencyKey o )
	{
		int res = groupId.compareTo( o.groupId );
		if( res != 0 )
			return res;
		res = Tools.compareStrings( artifactId, o.artifactId );
		if( res != 0 )
			return res;
		res = Tools.compareStrings( classifier, o.classifier );
		if( res != 0 )
			return res;
		res = Tools.compareStrings( type, o.type );
		return res;
	}
}