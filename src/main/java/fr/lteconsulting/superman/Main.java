package fr.lteconsulting.superman;

import java.util.HashMap;

public class Main
{
	public static void main(String[] args)
	{
		PersonRegistrySuperman persons = new PersonRegistrySuperman(new IPersonRegistry()
		{
			private volatile int nextId = 1;

			private final HashMap<Integer, Person> map = new HashMap<>();

			@Override
			public Person getPerson(int id)
			{
				return map.get(id);
			}

			@Override
			public int addPerson(Person p)
			{
				int id = nextId++;
				map.put(id, p);

				// System.out.println("inserted person, id = " + id + ", person = " + p);

				return id;
			}
		});

		persons.start();

		sleep(1000);
		for (int t = 0; t < 1000; t++)
		{
			final int tt = t;
			new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						for (int i = 0; i < 1000; i++)
						{
							int id = persons.addPerson(new Person("Arnaud-" + i, "Tournier-" + tt));
							// System.out.println("inserted person, id = " + id);
						}
					}
					catch (Exception e)
					{
						System.out.println("aborted call...");
					}
				}
			}.start();
		}

		sleep(3000);

		persons.stop();
		System.out.println("exited.");
	}

	private static void sleep(int x)
	{
		try
		{
			Thread.sleep(x);
		}
		catch (InterruptedException e)
		{
		}
	}
}
