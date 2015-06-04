package fr.lteconsulting.pomexplorer.depanalyze;

import fr.lteconsulting.pomexplorer.DependencyInfo;
import fr.lteconsulting.pomexplorer.Project;

public class PropertyLocation extends Location
{
	private final String propertyName;
	private final String propertyValue;

	public PropertyLocation( Project project, DependencyInfo dependency, String propertyName, String propertyValue )
	{
		super( project );
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

	@Override
	public String toString()
	{
		return "property '" + propertyName + "' " + (propertyValue != null ? ("with current value '" + propertyValue + "'") : "");
	}
}