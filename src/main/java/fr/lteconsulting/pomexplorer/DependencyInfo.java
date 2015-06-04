package fr.lteconsulting.pomexplorer;

import org.apache.maven.model.Dependency;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

public class DependencyInfo
{
	final DependencyInfoType type;
	final MavenDependency resolved;
	Dependency readden;

	GAV gav;
	GAV unresolvedGav;

	public DependencyInfo( MavenDependency resolved, DependencyInfoType type )
	{
		this.resolved = resolved;
		this.type = type;
	}

	public DependencyInfo( Dependency readden, DependencyInfoType type )
	{
		this.resolved = null;
		this.readden = readden;
		this.type = type;
	}

	public void setReadDependency( Dependency readden )
	{
		this.readden = readden;
	}

	public DependencyInfoType getType()
	{
		return type;
	}

	public GAV getGav()
	{
		if( getResolvedGav() != null )
			return getResolvedGav();
		else if( getUnresolvedGav() != null )
			return getUnresolvedGav();
		return null;
	}

	public GAV getResolvedGav()
	{
		if( gav == null && resolved != null )
			gav = new GAV( resolved.getGroupId(), resolved.getArtifactId(), resolved.getVersion() );

		return gav;
	}

	public GAV getUnresolvedGav()
	{
		if( unresolvedGav == null && readden != null )
			unresolvedGav = new GAV( readden.getGroupId(), readden.getArtifactId(), readden.getVersion() );
		return unresolvedGav;
	}

	@Override
	public String toString()
	{
		String res = "";

		if( getResolvedGav() != null && getUnresolvedGav() != null )
		{
			if( getResolvedGav().equals( getUnresolvedGav() ) )
				res = getResolvedGav().toString();
			else
				res = "[*] " + getResolvedGav() + " / " + getUnresolvedGav();
		}
		else
		{
			if( getResolvedGav() != null )
				res = getResolvedGav().toString();
			else if( getUnresolvedGav() != null )
				res = "[!]" + getUnresolvedGav();
			else
				res = "[!!!] NULL";
		}

		if( resolved != null )
			res += " " + resolved.getClassifier() + ":" + resolved.getScope();
		else if( readden != null )
			res += " " + readden.getClassifier() + ":" + readden.getScope();

		return res;
	}
}