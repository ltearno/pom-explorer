package fr.lteconsulting.pomexplorer;

import java.util.Map;

public class ValueResolution
{
	private String raw;
	private String resolved;
	private Map<String, String> properties;
	private boolean isSelfManaged;

	public String getRaw()
	{
		return raw;
	}

	public void setRaw( String raw )
	{
		this.raw = raw;
	}

	public String getResolved()
	{
		return resolved;
	}

	public void setResolved( String resolved )
	{
		this.resolved = resolved;
	}

	public Map<String, String> getProperties()
	{
		return properties;
	}

	public void setProperties( Map<String, String> properties )
	{
		this.properties = properties;
	}

	public boolean isSelfManaged()
	{
		return isSelfManaged;
	}

	public void setSelfManaged( Boolean selfManaged )
	{
		isSelfManaged = selfManaged;
	}
}