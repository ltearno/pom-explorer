package fr.lteconsulting.pomexplorer.model;

import static fr.lteconsulting.pomexplorer.Tools.isMavenVariable;

public class Gav extends GroupArtifact
{
	protected final String version;

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
		super( groupId, artifactId );

		this.version = version;
	}

	public Gav( Gav gav )
	{
		this( gav.getGroupId(), gav.getArtifactId(), gav.getVersion() );
	}

	public String getVersion()
	{
		return version;
	}

	public boolean isResolved()
	{
		return groupId != null && artifactId != null && version != null && !isMavenVariable( groupId ) && !isMavenVariable( artifactId ) && !isMavenVariable( version );
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

		return groupId.equals( ga.groupId ) && artifactId.equals( ga.artifactId );
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		Gav other = (Gav) obj;
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
