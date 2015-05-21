package fr.lteconsulting.pomexplorer;

import java.util.ArrayList;
import java.util.List;

public class AppFactory
{
	private static final AppFactory INSTANCE = new AppFactory();

	private AppFactory()
	{
	}

	public static AppFactory get()
	{
		return INSTANCE;
	}

	private final List<WorkingSession> sessions = new ArrayList<>();

	public List<WorkingSession> sessions()
	{
		return sessions;
	}
}
