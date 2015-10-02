package fr.lteconsulting.superman;

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
	
	public Person getPerson(int id)
	{
		return (Person) superman.sendMessage( new Supermessage( 0, new Object[]{ id } ) );
	}
	
	private Person on_getPerson(int id)
	{
		return super.getPerson( id );
	}
}
