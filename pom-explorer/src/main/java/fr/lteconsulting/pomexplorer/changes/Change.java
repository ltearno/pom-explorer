package fr.lteconsulting.pomexplorer.changes;

import java.util.ArrayList;
import java.util.List;

import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;
import fr.lteconsulting.pomexplorer.depanalyze.Location;
import fr.lteconsulting.pomexplorer.depanalyze.PropertyLocation;
import fr.lteconsulting.pomexplorer.model.Gav;

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

	private List<Change<? extends Location>> causes;

	private String causingMessage;

	public Change( L location )
	{
		this.location = location;
	}

	public L getLocation()
	{
		return location;
	}

	public void setCausingMessage( String causingMessage )
	{
		this.causingMessage = causingMessage;
	}

	/**
	 * Register the change that was the cause of the creation of this change
	 * 
	 * @param causingChange
	 */
	public void addCause( Change<? extends Location> causingChange )
	{
		if( causingChange == null )
			return;

		ensureCausesList();
		causes.add( causingChange );
	}

	/**
	 * Returns the list of changes which were the cause of the creation of this
	 * one.
	 */
	public List<Change<? extends Location>> getCauses()
	{
		return causes;
	}

	private void ensureCausesList()
	{
		if( causes == null )
			causes = new ArrayList<>();
	}

	@Override
	public String toString()
	{
		String res = "";
		if( location.getProject() != null )
			res = "in: " + location.getProject().getGav() + " (<i>" + location.getProject().getPomFile().getAbsolutePath() + "</i>)<br/>";
		else
			res = "in: [!project not found!]<br/>";
		res += "location: " + location + "<br/>";

		if( causingMessage != null )
			res += " cause:" + causingMessage + "<br/>";

		if( causes != null && !causes.isEmpty() )
		{
			res += " " + causes.size() + " causes" + "<br/>";
		}
		return res;
	}

	public static Change<? extends Location> create( Location location, Gav newGav )
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
