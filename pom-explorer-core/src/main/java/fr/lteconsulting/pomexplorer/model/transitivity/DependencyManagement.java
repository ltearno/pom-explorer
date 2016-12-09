package fr.lteconsulting.pomexplorer.model.transitivity;

import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.model.GroupArtifact;
import fr.lteconsulting.pomexplorer.model.VersionScope;

public class DependencyManagement
{
	private final VersionScope vs;
	private Set<GroupArtifact> exclusions;

	public DependencyManagement( VersionScope vs )
	{
		this.vs = vs;
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
		if( this.exclusions == null )
			exclusions = new HashSet<>();

		exclusions.add( ga );
	}
}
