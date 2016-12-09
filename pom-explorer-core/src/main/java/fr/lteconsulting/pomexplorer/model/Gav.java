package fr.lteconsulting.pomexplorer.model;

import static fr.lteconsulting.pomexplorer.Tools.isNonResolvedValue;

import java.util.Comparator;

public class Gav
{
	private final String groupId;
	private final String artifactId;
	private final String version;

	public static final Comparator<Gav> alphabeticalComparator = new Comparator<Gav>()
	{
		@Override
		public int compare( Gav o1, Gav o2 )
		{
			int r = o1.groupId.compareTo( o2.groupId );
			if( r != 0 )
				return r;

			r = o1.artifactId.compareTo( o2.artifactId );
			if( r != 0 )
				return r;

			if( o1.version == null && o2.version == null )
				return 0;
			if( o1.version == null )
				return -1;
			if( o2.version == null )
				return 1;

			r = o1.version.compareTo( o2.version );

			return 0;
		}
	};

	public static Gav parse( String gavString )
	{
		String[] parts = gavString.split( ":" );
		if( parts.length != 3 )
			return null;

		Gav gav = new Gav( parts[0], parts[1], parts[2] );

		return gav;
	}

	public Gav copyWithVersion( String version )
	{
		return new Gav( groupId, artifactId, version );
	}

	public Gav( String groupId, String artifactId, String version )
	{
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public Gav( Gav gav )
	{
		this( gav.groupId, gav.artifactId, gav.version );
	}

	public String getGroupId()
	{
		return groupId;
	}

	public String getArtifactId()
	{
		return artifactId;
	}

	public String getVersion()
	{
		return version;
	}

	public boolean isAllComponentsNotNull()
	{
		return groupId != null && artifactId != null && version != null;
	}

	public boolean isResolved()
	{
		return isAllComponentsNotNull() && !isNonResolvedValue( groupId ) && !isNonResolvedValue( artifactId ) && !isNonResolvedValue( version );
	}

	@Override
	public String toString()
	{
		return groupId + ":" + artifactId + ":" + version;
	}

	public boolean sameGroupArtifact( GroupArtifact ga )
	{
		if( ga == null )
			return false;

		return groupId.equals( ga.getGroupId() ) && artifactId.equals( ga.getArtifactId() );
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		Gav other = (Gav) obj;
		if( artifactId == null )
		{
			if( other.artifactId != null )
				return false;
		}
		else if( !artifactId.equals( other.artifactId ) )
			return false;
		if( groupId == null )
		{
			if( other.groupId != null )
				return false;
		}
		else if( !groupId.equals( other.groupId ) )
			return false;
		if( version == null )
		{
			if( other.version != null )
				return false;
		}
		else if( !version.equals( other.version ) )
			return false;
		return true;
	}
}
