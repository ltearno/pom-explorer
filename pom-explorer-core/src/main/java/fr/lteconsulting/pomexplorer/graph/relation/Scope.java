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
			return null;

		return Scope.valueOf( scope.toUpperCase() );
	}

	@Override
	public String toString()
	{
		return super.toString().toLowerCase();
	}

	public static Scope getScopeTransformation( Scope source, Scope declaredScope )
	{
		if( declaredScope == null )
			declaredScope = Scope.COMPILE;

		if( source == null )
			return declaredScope;

		switch( source )
		{
			case COMPILE:
				switch( declaredScope )
				{
					case COMPILE:
						return Scope.COMPILE;
					case PROVIDED:
					case SYSTEM:
						return null;
					case RUNTIME:
						return Scope.RUNTIME;
					case TEST:
						return null;

					default:
						return null;
				}

			case PROVIDED:
			case SYSTEM:
				switch( declaredScope )
				{
					case COMPILE:
						return source;
					case PROVIDED:
					case SYSTEM:
						return null;
					case RUNTIME:
						return source;
					case TEST:
						return null;

					default:
						return null;
				}

			case RUNTIME:
				switch( declaredScope )
				{
					case COMPILE:
						return Scope.RUNTIME;
					case PROVIDED:
					case SYSTEM:
						return null;
					case RUNTIME:
						return Scope.RUNTIME;
					case TEST:
						return null;

					default:
						return null;
				}

			case TEST:
				switch( declaredScope )
				{
					case COMPILE:
						return Scope.TEST;
					case PROVIDED:
					case SYSTEM:
						return null;
					case RUNTIME:
						return Scope.TEST;
					case TEST:
						return null;

					default:
						return null;
				}

			default:
				return null;
		}
	}
}