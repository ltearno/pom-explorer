package fr.lteconsulting.pomexplorer.web.commands;

public abstract class BaseCommand implements Command
{
	private final String[] shortcuts;

	public BaseCommand(String... shortcuts)
	{
		super();
		this.shortcuts = shortcuts;
	}

	@Override
	public String[] getShortcuts()
	{
		return shortcuts;
	}

	@Override
	public String getName()
	{
		String className = getClass().getSimpleName();
		return className.substring(0, className.length() - "Command".length());
	}
}
