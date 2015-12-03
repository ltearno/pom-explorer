package fr.lteconsulting.pomexplorer.commands;

import java.util.HashMap;
import java.util.Map;

public class CommandOptions
{
	private final Map<String, Object> options = new HashMap<>();

	public void setOption( String key, Object value )
	{
		options.put( key, value );
	}

	public Object getOption( String key )
	{
		return options.get( key );
	}

	public boolean hasFlag( String key )
	{
		return "true".equals( getOption( key ) + "" );
	}
}
