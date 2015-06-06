package fr.lteconsulting.pomexplorer.changes;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;

public class GavChange extends Change<GavLocation>
{
	private final GAV newGav;

	public GavChange( GavLocation location, GAV newGav )
	{
		super( location );
		this.newGav = newGav;
	}

	public GAV getNewGav()
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
