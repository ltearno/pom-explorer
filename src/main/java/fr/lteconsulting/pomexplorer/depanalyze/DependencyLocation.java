package fr.lteconsulting.pomexplorer.depanalyze;

import fr.lteconsulting.pomexplorer.DependencyInfo;
import fr.lteconsulting.pomexplorer.Project;

public class DependencyLocation extends Location
{
	private final DependencyInfo dependency;

	public DependencyLocation( Project project, DependencyInfo dependency )
	{
		super( project );
		this.dependency = dependency;
	}

	@Override
	public String toString()
	{
		return "dependency " + dependency.getType() + " to " + dependency.getGav();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dependency == null) ? 0 : dependency.getGav().hashCode());
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
		DependencyLocation other = (DependencyLocation) obj;
		if( dependency == null )
		{
			if( other.dependency != null )
				return false;
		}
		else if( !dependency.getGav().equals( other.dependency.getGav() ) )
			return false;
		return true;
	}
}