package fr.lteconsulting.pomexplorer;

import fr.lteconsulting.pomexplorer.model.Dependency;

public class DependencyInfo
{
	Dependency dependency;
	int level;
	Project managingProject;
	Project declaringProject;
	Dependency originatingDependency;
	boolean optional;

	public DependencyInfo( Dependency dependency, int level )
	{
		this.dependency = dependency;
		this.level = level;
	}

	public boolean isOptional()
	{
		return optional;
	}

	public Dependency getDependency()
	{
		return dependency;
	}
	
	public boolean isManaged()
	{
		return managingProject != null;
	}

	public boolean isDeclared()
	{
		return declaringProject != null;
	}

	public Project getManagingProject()
	{
		return managingProject;
	}

	public Project getDeclaringProject()
	{
		return declaringProject;
	}
	
	public Dependency getOriginatingDependency()
	{
		return originatingDependency;
	}

	public int getLevel()
	{
		return level;
	}
}