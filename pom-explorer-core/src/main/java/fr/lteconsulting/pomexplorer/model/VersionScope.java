package fr.lteconsulting.pomexplorer.model;

import fr.lteconsulting.pomexplorer.graph.relation.Scope;

public class VersionScope
{
	private String version;

	private Scope scope;

	public VersionScope( String version, Scope scope )
	{
		this.version = version;
		this.scope = scope;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion( String version )
	{
		this.version = version;
	}

	public Scope getScope()
	{
		return scope;
	}

	public void setScope( Scope scope )
	{
		this.scope = scope;
	}

	@Override
	public String toString()
	{
		return version + ":" + scope;
	}
}
