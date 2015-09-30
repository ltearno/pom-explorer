package fr.lteconsulting.superman;

import java.util.HashMap;

public class PersonRegistry implements IPersonRegistry
{
	private volatile int nextId = 1;

	private final HashMap<Integer, Person> map = new HashMap<>();
	
	public PersonRegistry(String name, int id, String other)
	{
	}

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
}
