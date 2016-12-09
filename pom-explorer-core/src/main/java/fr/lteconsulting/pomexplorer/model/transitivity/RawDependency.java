package fr.lteconsulting.pomexplorer.model.transitivity;

import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.model.GroupArtifact;
import fr.lteconsulting.pomexplorer.model.VersionScope;

public class RawDependency
{
	private final VersionScope vs;
	private final boolean optional;

	private Set<GroupArtifact> exclusions;

	public RawDependency( VersionScope vs, boolean optional )
	{
		this.vs = vs;
		this.optional = optional;
	}

	public boolean isOptional()
	{
		return optional;
	}

	public VersionScope getVs()
	{
		return vs;
	}

	public Set<GroupArtifact> getExclusions()
	{
		return exclusions;
	}

	public void addExclusion( GroupArtifact ga )
	{
		if( exclusions == null )
			exclusions = new HashSet<>();
		exclusions.add( ga );
	}

	@Override
	public String toString()
	{
		return "RawDependency [vs=" + vs + ", optional=" + optional + ", exclusions=" + exclusions + "]";
	}
}