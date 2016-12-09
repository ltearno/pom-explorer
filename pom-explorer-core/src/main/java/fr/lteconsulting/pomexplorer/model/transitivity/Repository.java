package fr.lteconsulting.pomexplorer.model.transitivity;

public class Repository
{
	private final String id;
	private final String url;

	public Repository( String id, String url )
	{
		this.id = id;
		this.url = url;
	}

	public String getId()
	{
		return id;
	}

	public String getUrl()
	{
		return url;
	}

	@Override
	public String toString()
	{
		return "Repository [id=" + id + ", url=" + url + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		if( url == null )
		{
			if( other.url != null )
				return false;
		}
		else if( !url.equals( other.url ) )
			return false;
		return true;
	}
}
