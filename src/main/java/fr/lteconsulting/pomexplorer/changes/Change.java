package fr.lteconsulting.pomexplorer.changes;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;
import fr.lteconsulting.pomexplorer.depanalyze.Location;
import fr.lteconsulting.pomexplorer.depanalyze.PropertyLocation;

/**
 * A change to be made in a pom.xml file.
 * 
 * See subclasses for real changes
 * 
 * @author Arnaud
 *
 */
public abstract class Change<L extends Location>
{
	private final L location;

	public Change( L location )
	{
		this.location = location;
	}

	public L getLocation()
	{
		return location;
	}

	@Override
	public String toString()
	{
		return "in: " + location.getProject().getGav() + " (<i>" + location.getProject().getPomFile().getAbsolutePath() + "</i>)<br/>" + "location: " + location + "<br/>";
	}

	public static Change<? extends Location> create( Location location, GAV newGav )
	{
		if( location instanceof GavLocation )
			return new GavChange( ((GavLocation) location), newGav );

		if( location instanceof PropertyLocation )
			return new PropertyChange( (PropertyLocation) location, newGav.getVersion() );

		return null;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
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
		@SuppressWarnings( "unchecked" )
		Change<? extends Location> other = (Change<? extends Location>) obj;
		if( location == null )
		{
			if( other.location != null )
				return false;
		}
		else if( !location.equals( other.location ) )
			return false;
		return true;
	}
}