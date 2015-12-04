package fr.lteconsulting.pomexplorer.graph.relation;

public enum Scope
{
	COMPILE,
	PROVIDED,
	RUNTIME,
	TEST,
	SYSTEM,
	IMPORT;

	public static Scope fromString( String scope )
	{
		if( scope == null || scope.isEmpty() )
			return COMPILE;

		return Scope.valueOf( scope.toUpperCase() );
	}

	@Override
	public String toString()
	{
		return super.toString().toLowerCase();
	}
}