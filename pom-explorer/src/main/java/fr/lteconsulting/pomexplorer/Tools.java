package fr.lteconsulting.pomexplorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.pom.ParsedPomFileImpl;

import fr.lteconsulting.hexa.client.tools.Func1;
import fr.lteconsulting.pomexplorer.changes.Change;
import fr.lteconsulting.pomexplorer.changes.ChangeSetManager;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;
import fr.lteconsulting.pomexplorer.depanalyze.Location;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

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

	public static void printChangeList(ILogger log, ChangeSetManager changes)
	{
		log.html("<br/>Change list...<br/><br/>");

		List<Change<? extends Location>> changeList = new ArrayList<>();
		for (Change<? extends Location> c : changes)
			changeList.add(c);

		Collections.sort(changeList, new Comparator<Change<? extends Location>>()
		{
			@Override
			public int compare(Change<? extends Location> o1, Change<? extends Location> o2)
			{
				Project p1 = o1.getLocation().getProject();
				Project p2 = o2.getLocation().getProject();

				if (p1 == null && p2 == null)
					return 0;
				if (p1 == null)
					return -1;
				if (p2 == null)
					return 1;

				return p1.getPomFile().getAbsolutePath().compareTo(p2.getPomFile().getAbsolutePath());
			}
		});

		for (Change<? extends Location> c : changeList)
		{
			log.html(c.toString());
		}
	}

	/***
	 * Maven tools
	 */

	public static Set<Location> getDirectDependenciesLocations(WorkingSession session, ILogger log, GAV gav)
	{
		Set<Location> set = new HashSet<>();

		Set<Relation> relations = session.graph().relationsReverse(gav);
		for (Relation relation : relations)
		{
			GAV updatedGav = relation.getSource();

			Project updatedProject = session.projects().forGav(updatedGav);
			if (updatedProject == null)
			{
				if (log != null)
					log.html(Tools.warningMessage("Cannot find project for GAV " + updatedGav
							+ " which dependency should be modified ! skipping."));
				continue;
			}

			Location dependencyLocation = Tools.findDependencyLocation(session, log, updatedProject, relation);
			if (dependencyLocation == null)
			{
				if (log != null)
					log.html(Tools.errorMessage("Cannot find the location of dependency to " + relation.getTarget()
							+ " in this project " + updatedProject));
				continue;
			}

			set.add(dependencyLocation);
		}

		return set;
	}

	public static List<String> getMavenProperties(GAV gav)
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

	public static boolean isMavenVariable(String text)
	{
		return text != null && text.startsWith("${") && text.endsWith("}");
	}

	private static String extractMavenProperty(String variable)
	{
		assert isMavenVariable(variable);
		return variable.substring(2, variable.length() - 1);
	}

	public static Project getPropertyDefinitionProject(WorkingSession session, Project startingProject, String property)
	{
		if (property.startsWith("project."))
			return startingProject;

		// search a property definition in the project. if found, return it
		String value = propertyValue(startingProject, property);
		if (value != null)
			return startingProject;

		// go deeper in hierarchy
		GAV parentGav = session.graph().parent(startingProject.getGav());
		Project parentProject = null;
		if (parentGav != null)
			parentProject = session.projects().forGav(parentGav);

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

	public static Location findDependencyLocation(WorkingSession session, ILogger log, Project project, Relation relation)
	{
		if (project.getGav().equals(relation.getTarget()))
			return new GavLocation(project, PomSection.PROJECT, project.getGav());

		Location dependencyLocation = null;

		switch (relation.getRelationType())
		{
			case DEPENDENCY:
				dependencyLocation = findDependencyLocationInDependencies(session, log, project, relation.getTarget());
				break;

			case BUILD_DEPENDENCY:
				dependencyLocation = findDependencyLocationInPlugins(session, project, relation.getTarget());
				break;

			case PARENT:
				dependencyLocation = new GavLocation(project, PomSection.PARENT, relation.getTarget(), relation.getTarget());
				break;
		}

		return dependencyLocation;
	}

	public static String getPropertyNameFromPropertyReference(String name)
	{
		if (!(name.startsWith("${") && name.endsWith("}")))
			return name;

		return name.substring(2, name.length() - 1);
	}

	public static GavLocation findDependencyLocationInDependencies(WorkingSession session, ILogger log, Project project,
			GAV searchedDependency)
	{
		if (project == null)
			return null;

		// dependencies
		GavLocation info = project.getDependencies().get(searchedDependency);
		if (info != null && info.getUnresolvedGav() != null && info.getUnresolvedGav().getVersion() != null)
			return info;

		// dependency management
		GavLocation locationInDepMngt = findDependencyLocationInDependencyManagement(session, project,
				searchedDependency, log);
		if (locationInDepMngt != null)
			return locationInDepMngt;

		// parent
		GAV parentGav = session.graph().parent(project.getGav());
		if (parentGav != null)
		{
			Project parentProject = session.projects().forGav(parentGav);
			if (parentProject == null)
			{
				log.html(Tools.warningMessage("Cannot find the '" + project.getGav() + "' parent project '" + parentGav
						+ "' to examine where the dependency '" + searchedDependency + "' is defined."));
				return null;
			}

			GavLocation locationInParent = findDependencyLocationInDependencies(session, log, parentProject,
					searchedDependency);
			if (locationInParent != null)
				return locationInParent;
		}

		return null;
	}

	public static GavLocation findDependencyLocationInDependencyManagement(WorkingSession session, Project project,
			GAV searchedDependency, ILogger log)
	{
		if (project.getUnresolvedPom().getDependencyManagement() == null)
			return null;
		if (project.getUnresolvedPom().getDependencyManagement().getDependencies() == null)
			return null;
		for (Dependency d : project.getUnresolvedPom().getDependencyManagement().getDependencies())
		{
			if ("import".equals(d.getScope()) && "pom".equals(d.getType()))
			{
				String version;
				if (Tools.isMavenVariable(d.getVersion()))
					version = resolveProperty(session, project, Tools.undecorateMavenVariable(d.getVersion()), log);
				else
					version = d.getVersion();

				GAV bomGav = new GAV(d.getGroupId(), d.getArtifactId(), version);

				Project bomProject = session.projects().forGav(bomGav);
				if (bomProject != null)
				{
					GavLocation inBom = findDependencyLocationInDependencyManagement(session,
							bomProject, searchedDependency, log);
					if (inBom != null)
						return inBom;
				}
				else
				{
					log.html(Tools.warningMessage("cannot find the project " + bomGav
							+ " which is imported as a bom in the project " + project
							+ ". This prevents BOM dependency analysis to find dependency to " + searchedDependency));
				}
			}

			if (searchedDependency.getGroupId().equals(d.getGroupId())
					&& searchedDependency.getArtifactId().equals(d.getArtifactId()))
			{
				GAV g = new GAV(d.getGroupId(), d.getArtifactId(), d.getVersion());
				return new GavLocation(project, PomSection.DEPENDENCY_MNGT, searchedDependency, g);
			}
		}

		return null;
	}

	public static String undecorateMavenVariable(String propertyDeclaration)
	{
		return propertyDeclaration.replace("${", "").replace("}", "");
	}

	public static String resolveProperty(WorkingSession session, Project project, String propertyName, ILogger log)
	{
		if (project == null)
			return null;

		String property = project.getUnresolvedPom().getProperties().getProperty(propertyName);
		if (property != null)
			return property;

		GAV parentGAV = session.graph().parent(project.getGav());
		if (parentGAV == null)
			return null;

		Project parentProject = session.projects().forGav(parentGAV);
		if (parentProject == null)
		{
			log.html(Tools.warningMessage("cannot find the project for GAV : " + parentGAV
					+ ". This is preventing from resolving the property " + propertyName + " in project " + project));
			return null;
		}

		return resolveProperty(session, parentProject, propertyName, log);
	}

	public static GavLocation findDependencyLocationInPlugins(WorkingSession session, Project project, GAV searchedPlugin)
	{
		if (project == null)
			return null;

		GavLocation info = project.getPluginDependencies().get(searchedPlugin);
		if (info != null)
			return info;

		// TODO search in the plugin management section

		// find in parent
		return findDependencyLocationInPlugins(session,
				session.projects().forGav(session.graph().parent(project.getGav())), searchedPlugin);
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

	public static String logMessage(String message)
	{
		return "<span style=''>" + message + "</span><br/>";
	}

	public static String warningMessage(String message)
	{
		return "<span style='color:orange;'>" + message + "</span><br/>";
	}

	public static String successMessage(String message)
	{
		return "<span style='color:green;'>" + message + "</span><br/>";
	}

	public static String buildMessage(String message)
	{
		return "<span style='color:grey;font-size:90%;'>" + message + "</span><br/>";
	}

	public static String errorMessage(String message)
	{
		return "<span style='color:red;'>" + message + "</span><br/>";
	}

	/**
	 * 
	 */

	private final static String SNAPSHOT_SUFFIX = "-SNAPSHOT";

	public static boolean isReleased(GAV gav)
	{
		return !gav.getVersion().endsWith(SNAPSHOT_SUFFIX);
	}

	public static GAV releasedGav(GAV gav)
	{
		if (!isReleased(gav))
			return new GAV(gav.getGroupId(), gav.getArtifactId(), gav.getVersion().substring(0,
					gav.getVersion().length() - SNAPSHOT_SUFFIX.length()));

		return gav;
	}

	public static GAV openGavVersion(GAV gav)
	{
		if (!isReleased(gav))
			return gav;

		String version = gav.getVersion();

		int major = 0;
		int minor = 0;
		int patch = 0;

		String[] parts = version.split("\\.");
		if (parts.length > 0)
		{
			try
			{
				major = Integer.parseInt(parts[0]);
			}
			catch (Exception e)
			{
			}
		}
		if (parts.length > 1)
		{
			try
			{
				minor = Integer.parseInt(parts[1]);
			}
			catch (Exception e)
			{
			}
		}
		if (parts.length > 2)
		{
			try
			{
				patch = Integer.parseInt(parts[2]);
			}
			catch (Exception e)
			{
			}
		}

		// new version, hard coded major version upgrade !
		major++;

		if (parts.length == 3)
			version = String.format("%1d.%1d.%1d", major, minor, patch);
		else if (parts.length == 2)
			version = String.format("%1d.%1d", major, minor);
		else if (parts.length == 1)
			version = String.format("%1d", major);
		else
			version += "-open";

		return gav.copyWithVersion(version + SNAPSHOT_SUFFIX);
	}

	/**
	 * Reads a whole file into a String assuming the file is UTF-8 encoded
	 */
	public static String readFile(File file)
	{
		try
		{
			return new Scanner(file, "UTF-8").useDelimiter("\\A").next();
		}
		catch (FileNotFoundException e)
		{
			return null;
		}
	}

	public static List<String> readFileLines(String path)
	{
		ArrayList<String> res = new ArrayList<String>();

		File file = new File(path);
		if (!file.exists())
			return res;

		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

			String str;

			while ((str = in.readLine()) != null)
			{
				res.add(str);
			}

			in.close();
		}
		catch (Exception e)
		{
		}

		return res;
	}

	public static void dumpStacktrace(Exception e, ILogger log)
	{
		Throwable t = e;
		if (t instanceof InvocationTargetException)
			t = ((InvocationTargetException)t).getTargetException();

		log.html(t.toString() + "<br/>");
		for (StackTraceElement st : t.getStackTrace())
			log.html(st.toString() + "<br/>");
	}
}
