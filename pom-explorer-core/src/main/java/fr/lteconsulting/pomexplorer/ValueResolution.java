package fr.lteconsulting.pomexplorer;

import java.util.Map;

public class ValueResolution
{
	String raw;
	String resolved;
	Map<String, String> properties;

	public String getRaw()
	{
		return raw;
	}

	public String getResolved()
	{
		return resolved;
	}

	public Map<String, String> getProperties()
	{
		return properties;
	}
}