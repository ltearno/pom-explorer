package fr.lteconsulting.pomexplorer.graph;

import java.nio.file.Path;

public class Repository
{
	private final Path path;

	public Repository( Path path )
	{
		this.path = path;
	}

	@Override
	public String toString()
	{
		return path.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		Repository other = (Repository) obj;
		if( path == null )
		{
			if( other.path != null )
				return false;
		}
		else if( !path.equals( other.path ) )
			return false;
		return true;
	}
}
