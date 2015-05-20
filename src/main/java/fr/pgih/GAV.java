package fr.pgih;

public class GAV
{
	private final String groupId;

	private final String artifactId;

	private final String version;

	public GAV(String groupId, String artifactId, String version)
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
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GAV other = (GAV)obj;
		if (artifactId == null)
		{
			if (other.artifactId != null)
				return false;
		}
		else if (!artifactId.equals(other.artifactId))
			return false;
		if (groupId == null)
		{
			if (other.groupId != null)
				return false;
		}
		else if (!groupId.equals(other.groupId))
			return false;
		if (version == null)
		{
			if (other.version != null)
				return false;
		}
		else if (!version.equals(other.version))
			return false;
		return true;
	}

}
