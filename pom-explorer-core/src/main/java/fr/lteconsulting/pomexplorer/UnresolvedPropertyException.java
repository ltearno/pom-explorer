package fr.lteconsulting.pomexplorer;


public class UnresolvedPropertyException extends RuntimeException
{
	private final String propertyName;
	private final Project project;

	UnresolvedPropertyException( String propertyName, Project project )
	{
		super( "Could not resolve property " + propertyName + " of project " + project.getGav());
		this.propertyName = propertyName;
		this.project = project;
	}

	public Project getProject()
	{
		return project;
	}
	public String getPropertyName()
	{
		return propertyName;
	}
}
