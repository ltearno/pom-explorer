package fr.lteconsulting.pomexplorer.model;

import fr.lteconsulting.pomexplorer.graph.relation.Scope;

import java.util.Optional;

public class VersionScope
{
	private String version;
	private Boolean isVersionSelfManaged;

	private Scope scope;

	public VersionScope( String version, Boolean isVersionSelfManaged, Scope scope )
	{
		this.version = version;
		this.isVersionSelfManaged = isVersionSelfManaged;
		this.scope = scope;
	}

	public String getVersion()
	{
		return version;
	}

	/**
	 * Indicates whether {@link #getVersion()} is self managed or not whereas {@link Optional#empty()} indicates it is unknown.
	 */
	public Optional<Boolean> isVersionSelfManaged()
	{
		return Optional.ofNullable(isVersionSelfManaged);
	}

	public void setVersion(String version )
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
