package fr.lteconsulting.superman;


public class PersonRegistrySuperman extends BaseSuperman implements IPersonRegistry
{
	private final IPersonRegistry implementation;

	public PersonRegistrySuperman(IPersonRegistry implementation)
	{
		this.implementation = implementation;
	}

	@Override
	protected Object processMessage(Supermessage message)
	{
		switch (message.getMethodId())
		{
			case 0:
				return implementation.addPerson((Person)message.getParameters()[0]);
			case 1:
				return implementation.getPerson((int)message.getParameters()[0]);
		}

		throw new IllegalStateException("Unknown message : " + message);
	}

	@Override
	public int addPerson(Person p)
	{
		return (int)sendMessage(new Supermessage(0, new Object[] { p }));
	}

	@Override
	public Person getPerson(int id)
	{
		return (Person)sendMessage(new Supermessage(1, new Object[] { id }));
	}
}
