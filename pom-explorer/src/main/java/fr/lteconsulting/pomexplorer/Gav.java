package fr.lteconsulting.pomexplorer;

import static fr.lteconsulting.pomexplorer.Tools.isMavenVariable;

public class Gav
{
	private final String groupId;

	private final String artifactId;

	private final String version;

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

	public boolean isResolved()
	{
		return groupId != null && artifactId != null && version != null && !isMavenVariable( groupId ) && !isMavenVariable( artifactId ) && !isMavenVariable( version );
	}

	@Override
	public String toString()
	{
		return groupId + ":" + artifactId + ":" + version;
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
