package fr.lteconsulting.superman;


public class Main
{
	public static void main( String[] args )
	{
		IPersonRegistrySuperman persons = new IPersonRegistrySuperman( new PersonRegistry() );

		persons.start();

		sleep( 1000 );
		for( int t = 0; t < 1000; t++ )
		{
			final int tt = t;
			new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						for( int i = 0; i < 1000; i++ )
						{
							int id = persons.addPerson( new Person( "Arnaud-" + i, "Tournier-" + tt ) );
							// System.out.println("inserted person, id = " +
							// id);
						}
					}
					catch( Exception e )
					{
						System.out.println( "aborted call..." );
					}
				}
			}.start();
		}

		sleep( 3000 );

		persons.stop();
		System.out.println( "exited." );
	}

	private static void sleep( int x )
	{
		try
		{
			Thread.sleep( x );
		}
		catch( InterruptedException e )
		{
		}
	}
}
