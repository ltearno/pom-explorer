package fr.lteconsulting.pomexplorer.depanalyze;

import fr.lteconsulting.pomexplorer.Project;

public class PropertyLocation extends Location
{
	private final String propertyName;
	private final String propertyValue;

	public PropertyLocation( Project project, Location cause, String propertyName, String propertyValue )
	{
		super( project, cause );

		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	public String getPropertyValue()
	{
		return propertyValue;
	}

	@Override
	public String toString()
	{
		return "[PROPERTY] '" + propertyName + "' " + (propertyValue != null ? ("with current value '" + propertyValue + "'") : "(null value)");
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((propertyName == null) ? 0 : propertyName.hashCode());
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
		PropertyLocation other = (PropertyLocation) obj;
		if( propertyName == null )
		{
			if( other.propertyName != null )
				return false;
		}
		else if( !propertyName.equals( other.propertyName ) )
			return false;
		return true;
	}
}