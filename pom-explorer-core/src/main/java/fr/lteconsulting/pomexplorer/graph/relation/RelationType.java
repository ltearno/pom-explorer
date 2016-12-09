package fr.lteconsulting.pomexplorer.graph.relation;

/**
 * Whether it's a normal dependency, a build one or a parent one.
 * 
 * @author Arnaud
 *
 */
public enum RelationType
{
	DEPENDENCY( "D" ),
	BUILD_DEPENDENCY( "B" ),
	PARENT( "P" );

	private final String shortName;

	RelationType( String shortName )
	{
		this.shortName = shortName;
	}

	public String shortName()
	{
		return shortName;
	}
}