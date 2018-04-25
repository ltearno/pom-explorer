package fr.lteconsulting.pomexplorer;


public class ProjectAnalyseException extends RuntimeException
{
	private final Project project;

	ProjectAnalyseException( Project project, Throwable cause)
	{
		super("Could not add project to graph: " + project.getGav(), cause);
		this.project = project;
	}

	public Project getProject()
	{
		return project;
	}
}
