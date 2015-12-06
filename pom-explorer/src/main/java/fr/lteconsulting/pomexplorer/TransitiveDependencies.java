package fr.lteconsulting.pomexplorer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.lteconsulting.pomexplorer.graph.relation.Scope;
import fr.lteconsulting.pomexplorer.model.Dependency;
import fr.lteconsulting.pomexplorer.model.DependencyKey;
import fr.lteconsulting.pomexplorer.model.Gav;

public class TransitiveDependencies
{
	final Set<Project> visitedProjects = new HashSet<>();
	private final Set<Gav> missingProjects = new HashSet<>();
	private final Map<DependencyKey, DependencyInfo> data = new HashMap<>();

	void addMissingProject( Gav gav )
	{
		if( gav.isAllComponentsNotNull() )
			missingProjects.add( gav );
	}

	void addManagedDependency( Dependency d, Project managingProject, int level )
	{
		assert d.isComplete();

		DependencyInfo info = data.get( d.key() );
		if( info == null )
		{
			info = new DependencyInfo( d, level );
			data.put( d.key(), info );
		}
		else
		{
			if( !info.isManaged() || info.level > level )
			{
				// TODO this is a very rudimentary attempt to manage version range...
				if( d.getVersion() != null && d.getVersion().startsWith( "[" ) && info.dependency.getVersion() != null )
					info.dependency = new Dependency( d.getGroupId(), d.getArtifactId(), info.dependency.getVersion(), d.getScope(), d.getClassifier(), d.getType() );
				else
					info.dependency = d;
				info.level = level;
			}
		}

		info.managingProject = managingProject;
	}

	void addDependency( Dependency d, Project declaringProject, int level, boolean isOptional )
	{
		addDependency( d, declaringProject, null, null, level, isOptional );
	}

	void mergeDependency( Scope scope, DependencyInfo info, Dependency originatingDependency, boolean originatingDependencyOptionnality )
	{
		if( info.isOptional() )
			return;

		scope = getScopeTransformation( scope, info.dependency.getScope() );
		if( scope == null )
			return;

		Dependency transitiveDependency = new Dependency( info.dependency.toGav(), scope, info.dependency.getClassifier(), info.dependency.getType() );
		addDependency( transitiveDependency, info.declaringProject, info.managingProject, originatingDependency, info.level + 1, originatingDependencyOptionnality );
	}

	private void addDependency( Dependency d, Project declaringProject, Project managingProject, Dependency originatingDependency, int level, boolean isOptional )
	{
		assert d.key().isComplete();

		DependencyInfo info = data.get( d.key() );
		if( info == null )
		{
			info = new DependencyInfo( d, level );
			data.put( d.key(), info );
			info.originatingDependency = originatingDependency;
			info.optional = isOptional;
		}
		else
		{
			if( info.level > level )
			{
				info.dependency = d;
				info.level = level;
				info.managingProject = managingProject;
				info.originatingDependency = originatingDependency;
				info.optional = isOptional;
			}
		}

		info.declaringProject = declaringProject;
	}

	public Map<DependencyKey, DependencyInfo> getDependencies()
	{
		return data;
	}

	public Set<Gav> getMissingProjects()
	{
		return missingProjects;
	}

	private static Scope getScopeTransformation( Scope source, Scope declaredScope )
	{
		if( source == null )
			return declaredScope;

		switch( source )
		{
			case COMPILE:
				switch( declaredScope )
				{
					case COMPILE:
						return Scope.COMPILE;
					case PROVIDED:
					case SYSTEM:
						return null;
					case RUNTIME:
						return Scope.RUNTIME;
					case TEST:
						return null;

					default:
						return null;
				}

			case PROVIDED:
			case SYSTEM:
				switch( declaredScope )
				{
					case COMPILE:
						return source;
					case PROVIDED:
					case SYSTEM:
						return null;
					case RUNTIME:
						return source;
					case TEST:
						return null;

					default:
						return null;
				}

			case RUNTIME:
				switch( declaredScope )
				{
					case COMPILE:
						return Scope.RUNTIME;
					case PROVIDED:
					case SYSTEM:
						return null;
					case RUNTIME:
						return Scope.RUNTIME;
					case TEST:
						return null;

					default:
						return null;
				}

			case TEST:
				switch( declaredScope )
				{
					case COMPILE:
						return Scope.TEST;
					case PROVIDED:
					case SYSTEM:
						return null;
					case RUNTIME:
						return Scope.TEST;
					case TEST:
						return null;

					default:
						return null;
				}

			default:
				return null;
		}
	}
}