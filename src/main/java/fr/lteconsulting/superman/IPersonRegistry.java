package fr.lteconsulting.superman;

public interface IPersonRegistry
{
	int addPerson(Person p);

	Person getPerson(int id);
}
