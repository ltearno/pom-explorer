package fr.lteconsulting.pomexplorer.changes;

import fr.lteconsulting.pomexplorer.depanalyze.PropertyLocation;

public class PropertyChange extends Change<PropertyLocation>
{
	private final String newValue;

	public PropertyChange( PropertyLocation location, String newValue )
	{
		super( location );
		this.newValue = newValue;
	}

	public String getNewValue()
	{
		return newValue;
	}

	@Override
	public String toString()
	{
		return super.toString() + "change to: " + newValue + "<br><br/>";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((newValue == null) ? 0 : newValue.hashCode());
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
		PropertyChange other = (PropertyChange) obj;
		if( newValue == null )
		{
			if( other.newValue != null )
				return false;
		}
		else if( !newValue.equals( other.newValue ) )
			return false;
		return true;
	}
}
