package fr.lteconsulting.pomexplorer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.pom.ParsedPomFileImpl;

import fr.lteconsulting.hexa.client.tools.Func1;
import fr.lteconsulting.pomexplorer.Project.DependencyInfo;
import fr.lteconsulting.pomexplorer.depanalyze.DependencyLocation;
import fr.lteconsulting.pomexplorer.depanalyze.Location;
import fr.lteconsulting.pomexplorer.depanalyze.PropertyLocation;

public class Tools
{
	public static GAV string2Gav(String gavString)
	{
		String[] parts = gavString.split(":");
		if (parts.length != 3)
			return null;

		GAV gav = new GAV(parts[0], parts[1], parts[2]);

		return gav;
	}

	public static GAV getParentGav(WorkingSession session, Project project)
	{
		return session.graph().getParent(project.getGav());
	}

	public static Project getParentProject(WorkingSession session, Project project)
	{
		GAV parentGav = getParentGav(session, project);
		if (parentGav == null)
			return null;

		Project parentProject = session.projects().get(parentGav);

		return parentProject;
	}

	/**
	 * GAVs which are dependent of the one in parameter
	 */
	public static Set<GAV> dependentGAVs(WorkingSession session, GAV gav)
	{
		Set<GAV> res = new HashSet<>();

		dependentGAVsRec(session, gav, res);

		return res;
	}

	private static void dependentGAVsRec(WorkingSession session, GAV gav, Set<GAV> list)
	{
		Set<GAV> deps = session.graph().getDependents(gav);
		for (GAV ancestor : deps)
		{
			list.add(ancestor);

			dependentGAVsRec(session, ancestor, list);
		}
	}

	/***
	 * Maven tools
	 */

	public static Set<Location> getImpactedLocationsToChangeGav(WorkingSession session, GAV gav, StringBuilder log,
			boolean logDependency)
	{
		HashSet<Location> locations = new HashSet<>();

		Project project = session.projects().get(gav);
		if (project == null)
		{
			log.append("<b style='color:orange;'>" + gav + "</b> (no project found)<br/>");
		}
		else
		{
			locations.add(new PropertyLocation(project, null, "project.version", gav.getVersion()));
		}

		for (GAV dependency : Tools.dependentGAVs(session, gav))
		{
			Project dependentProject = session.projects().get(dependency);
			if (dependentProject == null)
			{
				log.append("<b style='color:orange;'>" + dependency + "</b> (no project found)<br/>");
				continue;
			}

			String dependencyKind = "?";

			Project specifyingProject = getProjectWhereDependencyIsSpecifiedInHierarchy(session, dependentProject, gav);
			if (specifyingProject != null)
			{
				dependencyKind = specifyingProject == dependentProject ? "D" : "H";
			}
			else
			{
				specifyingProject = getProjectWhereDependencyIsSpecifiedInTransitiveDeps(session, dependency, gav);
				if (specifyingProject != null)
				{
					dependencyKind = "T";
				}
				else
				{
					specifyingProject = getProjectWhereDependencyIsSpecifiedInBuildPlugins(session, dependency, gav);
					if (specifyingProject != null)
						dependencyKind = "P";
				}
			}

			if (specifyingProject == null)
				dependencyKind = "!";

			if (logDependency)
				log.append("[" + dependencyKind + "] " + dependentProject + "<br/>");

			if (specifyingProject == null)
			{
				log.append("(dependency declaration not found although it is in the dependency graph, ignoring)<br/>");
				continue;
			}

			DependencyInfo info = specifyingProject.getDependencies().get(gav);
			if (info == null || info.getUnresolvedGav() == null)
			{
				info = specifyingProject.getPluginDependencies().get(gav);
				if (info == null || info.getUnresolvedGav() == null)
				{
					log.append("(WARNING, dependency not found in specifying project !)<br/>");
					continue;
				}
			}

			List<String> properties = getMavenProperties(info.getUnresolvedGav());
			if (properties.isEmpty() || info.getUnresolvedGav().equals(info.getResolvedGav()))
			{
				locations.add(new DependencyLocation(specifyingProject, info));
			}
			else
			{
				for (String property : properties)
				{
					Project definitionProject = getPropertyDefinitionProject(session, specifyingProject, property);
					if (definitionProject != null)
					{
						locations.add(new PropertyLocation(specifyingProject, info, property, definitionProject
								.getUnresolvedPom().getProperties().getProperty(property)));
					}
					else
					{
						log.append("[ERROR] not found property definition for property " + property + "<br/>");
					}
				}
			}

		}

		return locations;
	}

	private static List<String> getMavenProperties(GAV gav)
	{
		if (gav == null)
			return null;

		ArrayList<String> res = new ArrayList<>();

		if (isMavenVariable(gav.getGroupId()))
			res.add(extractMavenProperty(gav.getGroupId()));

		if (isMavenVariable(gav.getArtifactId()))
			res.add(extractMavenProperty(gav.getArtifactId()));

		if (isMavenVariable(gav.getVersion()))
			res.add(extractMavenProperty(gav.getVersion()));

		return res;
	}

	private static boolean isMavenVariable(String text)
	{
		return text != null && text.startsWith("${") && text.endsWith("}");
	}

	private static String extractMavenProperty(String variable)
	{
		assert isMavenVariable(variable);
		return variable.substring(2, variable.length() - 1);
	}

	private static Project getPropertyDefinitionProject(WorkingSession session, Project startingProject, String property)
	{
		if (property.startsWith("project."))
			return startingProject;

		// search a property definition in the project. if found, return it
		String value = propertyValue(startingProject, property);
		if (value != null)
			return startingProject;

		// go deeper in hierarchy
		Project parentProject = Tools.getParentProject(session, startingProject);
		if (parentProject != null)
		{
			Project definition = getPropertyDefinitionProject(session, parentProject, property);
			if (definition != null)
				return definition;
		}

		return null;
	}

	private static String propertyValue(Project startingProject, String property)
	{
		Object res = startingProject.getUnresolvedPom().getProperties().get(property);
		if (res instanceof String)
			return (String)res;
		return null;
	}

	private static Project getProjectWhereDependencyIsSpecifiedInHierarchy(WorkingSession session, Project project,
			GAV gav)
	{
		if (project == null)
			return null;

		DependencyInfo info = project.getDependencies().get(gav);

		// if an unresolved gav is found, it means the dependency is written in
		// the pom
		if (info != null && info.getUnresolvedGav() != null)
		{
			return project;
		}
		else
		{
			Project parentProject = Tools.getParentProject(session, project);
			if (parentProject == null)
				return null;

			return getProjectWhereDependencyIsSpecifiedInHierarchy(session, parentProject, gav);
		}
	}

	private static Project getProjectWhereDependencyIsSpecifiedInTransitiveDeps(WorkingSession session, GAV currentGav,
			GAV searchedGav)
	{
		for (GAV dependencyGav : session.graph().getDependencies(currentGav))
		{
			Project project = session.projects().get(dependencyGav);
			if (project != null)
			{
				DependencyInfo info = project.getDependencies().get(searchedGav);

				// if an unresolved gav is found, it means the dependency is written in the pom
				if (info != null && info.getUnresolvedGav() != null)
				{
					return project;
				}
			}

			project = getProjectWhereDependencyIsSpecifiedInTransitiveDeps(session, dependencyGav, searchedGav);
			if (project != null)
				return project;
		}

		return null;
	}

	private static Project getProjectWhereDependencyIsSpecifiedInBuildPlugins(WorkingSession session, GAV currentGav,
			GAV searchedGav)
	{
		Project project = session.projects().get(currentGav);
		if (project == null)
			return null;

		for (Plugin plugin : project.getUnresolvedPom().getBuildPlugins())
		{
			if (plugin.getGroupId().equals(searchedGav.getGroupId())
					&& plugin.getArtifactId().equals(searchedGav.getArtifactId()))
				return project;
		}

		Project parentProject = Tools.getParentProject(session, project);
		if (parentProject == null)
			return null;

		return getProjectWhereDependencyIsSpecifiedInBuildPlugins(session, parentProject.getGav(), searchedGav);
	}

	private static Field modelField;

	public static Model getParsedPomFileModel(ParsedPomFile parsedPomFile)
	{
		if (modelField == null)
		{
			try
			{
				modelField = ParsedPomFileImpl.class.getDeclaredField("model");
				modelField.setAccessible(true);

			}
			catch (NoSuchFieldException | SecurityException | IllegalArgumentException e)
			{
				e.printStackTrace();
				return null;
			}
		}

		try
		{
			Model model = (Model)modelField.get(parsedPomFile);
			return model;
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Collection utilities
	 */

	public static <T> List<T> filter(Iterable<T> list, Func1<T, Boolean> predicate)
	{
		List<T> res = new ArrayList<>();
		if (list == null)
			return res;

		for (T t : list)
			if (predicate.exec(t))
				res.add(t);
		return res;
	}

	public static <T> List<T> filter(T[] list, Func1<T, Boolean> predicate)
	{
		List<T> res = new ArrayList<>();
		if (list == null)
			return res;

		for (T t : list)
			if (predicate.exec(t))
				res.add(t);
		return res;
	}

	public static final Comparator<GAV> gavAlphabeticalComparator = new Comparator<GAV>()
	{
		@Override
		public int compare(GAV o1, GAV o2)
		{
			int r = o1.getGroupId().compareTo(o2.getGroupId());
			if (r != 0)
				return r;

			r = o1.getArtifactId().compareTo(o2.getArtifactId());
			if (r != 0)
				return r;

			if (o1.getVersion() == null && o2.getVersion() == null)
				return 0;
			if (o1.getVersion() == null)
				return -1;
			if (o2.getVersion() == null)
				return 1;

			r = o1.getVersion().compareTo(o2.getVersion());

			return 0;
		}
	};
}
