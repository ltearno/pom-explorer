package fr.lteconsulting.pomexplorer.changes;

import fr.lteconsulting.pomexplorer.Gav;
import fr.lteconsulting.pomexplorer.PomSection;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;

public class GavChange extends Change<GavLocation>
{
	private final Gav newGav;

	public GavChange( Project project, PomSection pomSection, Gav gav, Gav newGav )
	{
		this( new GavLocation( project, pomSection, gav ), newGav );
	}

	public GavChange( GavLocation location, Gav newGav )
	{
		super( location );
		this.newGav = newGav;
	}

	public Gav getNewGav()
	{
		return newGav;
	}

	@Override
	public String toString()
	{
		return super.toString() + "change to: " + newGav + "<br><br/>";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((newGav == null) ? 0 : newGav.hashCode());
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
		GavChange other = (GavChange) obj;
		if( newGav == null )
		{
			if( other.newGav != null )
				return false;
		}
		else if( !newGav.equals( other.newGav ) )
			return false;
		return true;
	}
}
