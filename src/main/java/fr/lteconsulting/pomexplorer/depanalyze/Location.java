package fr.lteconsulting.pomexplorer.depanalyze;

import fr.lteconsulting.pomexplorer.Project;

public abstract class Location
{
	private final Project project;
	private final Location cause;

	public Location( Project project, Location cause )
	{
		this.project = project;
		this.cause = cause;
	}

	public Project getProject()
	{
		return project;
	}

	public Location getCause()
	{
		return cause;
	}

	@Override
	public String toString()
	{
		return project.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((project == null) ? 0 : project.hashCode());
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
		Location other = (Location) obj;
		if( project == null )
		{
			if( other.project != null )
				return false;
		}
		else if( !project.equals( other.project ) )
			return false;
		return true;
	}
}