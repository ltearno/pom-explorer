package fr.lteconsulting.pomexplorer.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

import fr.lteconsulting.pomexplorer.graph.relation.Scope;

public class Dependency
{
	private final String groupId;
	private final String artifactId;
	private final String version;
	private final Scope scope;
	private final Boolean isVersionSelfManaged;
	private final String classifier;
	private final String type;

	private DependencyKey key;
	private Set<GroupArtifact> exclusions;

	public static final Comparator<Dependency> alphabeticalComparator = Comparator.comparing(Dependency::toString);

	public Dependency( Gav gav, Scope scope, String classifier, String type )
	{
		this( gav.getGroupId(), gav.getArtifactId(), gav.getVersion(), scope, classifier, type );
	}

	public Dependency( DependencyKey key, Scope scope, String version )
	{
		this( key.getGroupId(), key.getArtifactId(), version, scope, key.getClassifier(), key.getType() );
	}

	public Dependency( String groupId, String artifactId, String version, Scope scope, String classifier, String type )
	{
		this(groupId, artifactId, version, null, scope, classifier, type, Collections.emptySet());
	}

	public Dependency(String groupId, String artifactId, VersionScope vs, String classifier, String type)
	{
		this(groupId, artifactId, vs, classifier, type, Collections.emptySet());
	}

	public Dependency(String groupId, String artifactId, VersionScope vs, String classifier, String type, Set<GroupArtifact> exclusions) {
		this(groupId, artifactId, vs.getVersion(), vs.isVersionSelfManaged().orElse(null), vs.getScope(), classifier, type, exclusions);
	}

	public Dependency(String groupId, String artifactId, String version, Boolean isVersionSelfManaged, Scope scope, String classifier, String type, Set<GroupArtifact> exclusions)
	{
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.isVersionSelfManaged = isVersionSelfManaged;
		this.scope = scope == null ? Scope.COMPILE : scope;
		this.classifier = classifier;
		this.type = type == null ? "jar" : type;
		this.exclusions = exclusions;
	}

	public DependencyKey key()
	{
		if( key == null )
			key = new DependencyKey( groupId, artifactId, classifier, type );
		return key;
	}

	public boolean isComplete()
	{
		return version != null && scope != null && groupId != null && artifactId != null;
	}

	public Gav toGav()
	{
		return new Gav( groupId, artifactId, version );
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

	public Optional<Boolean> isVersionSelfManaged() {
		return Optional.ofNullable(isVersionSelfManaged);
	}

	public Scope getScope()
	{
		return scope;
	}

	public String getClassifier()
	{
		return classifier;
	}

	public String getType()
	{
		return type;
	}

	public Set<GroupArtifact> getExclusions()
	{
		return exclusions;
	}

	@Override
	public String toString()
	{
		return groupId + ":" + artifactId + ":" + version + ":" + scope + (classifier != null ? (":" + classifier) : "") + (type != null ? (":" + type) : "");
	}

	public boolean sameGav( Gav gav )
	{
		return gav != null && groupId.equals( gav.getGroupId() ) && artifactId.equals( gav.getArtifactId() ) && version.equals( gav.getVersion() );
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((exclusions == null) ? 0 : exclusions.hashCode());
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
		Dependency other = (Dependency) obj;
		if( artifactId == null )
		{
			if( other.artifactId != null )
				return false;
		}
		else if( !artifactId.equals( other.artifactId ) )
			return false;
		if( classifier == null )
		{
			if( other.classifier != null )
				return false;
		}
		else if( !classifier.equals( other.classifier ) )
			return false;
		if( groupId == null )
		{
			if( other.groupId != null )
				return false;
		}
		else if( !groupId.equals( other.groupId ) )
			return false;
		if( key == null )
		{
			if( other.key != null )
				return false;
		}
		else if( !key.equals( other.key ) )
			return false;
		if( scope != other.scope )
			return false;
		if( type == null )
		{
			if( other.type != null )
				return false;
		}
		else if( !type.equals( other.type ) )
			return false;
		if( version == null )
		{
			if( other.version != null )
				return false;
		}
		else if( !version.equals( other.version ) )
			return false;
		if( exclusions == null )
		{
			if( other.exclusions != null )
				return false;
		}
		else if( !exclusions.equals( other.exclusions ) )
			return false;
		return true;
	}
}
