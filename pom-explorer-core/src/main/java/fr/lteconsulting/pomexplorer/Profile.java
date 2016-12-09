/**
 * 
 */
package fr.lteconsulting.pomexplorer;

/**
 * Representation for a profile element in Maven.
 * 
 * @author D. Chaumont
 *
 */
public class Profile
{

	// ID of the profile
	private String id;

	public Profile( String id )
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public void setId( String id )
	{
		this.id = id;
	}
}
