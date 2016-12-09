package fr.lteconsulting.pomexplorer.depanalyze;

import fr.lteconsulting.pomexplorer.PomSection;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.graph.relation.Scope;
import fr.lteconsulting.pomexplorer.model.Gav;

public class GavLocation extends Location
{
	private final PomSection section;

	private Gav gav;

	private Gav unresolvedGav;

	private Scope scope;

	private String classifier;

	private String type;

	public GavLocation( Project project, PomSection section, Gav gav )
	{
		this( project, section, gav, gav );
	}

	public GavLocation( Project project, PomSection section, Gav resolvedGav, Gav unresolvedGav )
	{
		this( project, section, resolvedGav, unresolvedGav, null, null, "jar" );
	}

	public GavLocation( Project project, PomSection section, Gav resolvedGav, Gav unresolvedGav, String scope, String classifier, String type )
	{
		super( project, null );

		this.section = section;
		this.gav = resolvedGav;
		this.unresolvedGav = unresolvedGav;
		this.scope = Scope.fromString( scope );
		this.classifier = classifier;
		this.type = type;
	}

	public Scope getScope()
	{
		return scope;
	}

	public String getClassifier()
	{
		return classifier;
	}

	public PomSection getSection()
	{
		return section;
	}

	public String getType()
	{
		return type;
	}

	public Gav getGav()
	{
		if( gav != null )
			return gav;

		return unresolvedGav;
	}

	public Gav getResolvedGav()
	{
		return gav;
	}

	public Gav getUnresolvedGav()
	{
		return unresolvedGav;
	}

	@Override
	public String toString()
	{
		String res = "[" + section + "] ";

		if( gav != null && unresolvedGav != null )
		{
			if( gav.equals( unresolvedGav ) )
				res += gav.toString();
			else
				res += "[*] " + gav + " / " + unresolvedGav;
		}
		else
		{
			if( gav != null )
				res = gav.toString();
			else if( unresolvedGav != null )
				res += "[unresolved]" + unresolvedGav;
			else
				res += "[!!!] NULL";
		}

		return res;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((gav == null) ? 0 : gav.hashCode());
		result = prime * result + ((section == null) ? 0 : section.hashCode());
		result = prime * result + ((unresolvedGav == null) ? 0 : unresolvedGav.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if( this == obj )
			return true;
		if( !super.equals( obj ) )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		GavLocation other = (GavLocation) obj;
		if( gav == null )
		{
			if( other.gav != null )
				return false;
		}
		else if( !gav.equals( other.gav ) )
			return false;
		if( section != other.section )
			return false;
		if( unresolvedGav == null )
		{
			if( other.unresolvedGav != null )
				return false;
		}
		else if( !unresolvedGav.equals( other.unresolvedGav ) )
			return false;
		return true;
	}
}
