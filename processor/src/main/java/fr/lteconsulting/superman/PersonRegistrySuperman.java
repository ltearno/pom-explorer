package fr.lteconsulting.superman;

import java.util.concurrent.Future;

public class PersonRegistrySuperman extends PersonRegistry
{
	private final BaseSuperman superman = new BaseSuperman()
	{
		@Override
		protected Object processMessage( Supermessage message )
		{
			switch(message.getMethodId())
			{
				case 0:
					return on_getPerson( (int ) message.getParameters()[0] );
			}
			
			throw new IllegalStateException( "");
		}
	};
	
	public PersonRegistrySuperman()
	{
		superman.start();
	}
	
	@Override
	public Person getPerson(int id)
	{
		return (Person) superman.sendMessage( new Supermessage( 0, new Object[]{ id } ) );
	}
	
	private Person on_getPerson(int id)
	{
		return super.getPerson( id );
	}

	@SuppressWarnings("unchecked")
	public Future<Person> getPersonAsync(int id)
	{
		return (Future<Person>)(Future<?>)superman.postMessage(new Supermessage(0, new Object[] { id }));
	}
}
