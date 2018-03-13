package fr.lteconsulting.pomexplorer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import fr.lteconsulting.pomexplorer.graph.ProjectRepository;
import fr.lteconsulting.pomexplorer.graph.relation.BuildDependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import org.junit.Ignore;
import org.junit.Test;

import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.model.GroupArtifact;
import fr.lteconsulting.pomexplorer.model.transitivity.Repository;

public class AnalyzerTest
{

	private static final String PROJECT_A = "fr.lteconsulting:a:1.0-SNAPSHOT";
	private static final String PROJECT_B = "fr.lteconsulting:b:1.0-SNAPSHOT";
	private static final String PROJECT_C = "fr.lteconsulting:c:1.0-SNAPSHOT";
	private static final String PROJECT_D = "fr.lteconsulting:d:1.0-SNAPSHOT";
	private static final String PROJECT_E = "fr.lteconsulting:e:2.0-SNAPSHOT";
	private static final String PROJECT_F = "fr.lteconsulting:f:1.5";

	@Test
	public void test01()
	{
		//arrange
		Session session = new Session();
		//act
		runFullRecursiveAnalysis(session, "testSets/set01");
		//assert
		assertProjects(session, 1);
		assertDependenciesAndBuildDependencies(session, "fr.lteconsulting:pom-explorer:1.1-SNAPSHOT", 14, 2);
		assertNullGavs(session, 1);
	}

	@Test
	public void test02()
	{
		//arrange
		Session session = new Session();
		//act
		runFullRecursiveAnalysis(session, "testSets/set02");
		//assert
		assertProjects(session, 5);
		assertDependencies(session, PROJECT_A, 1);
		assertDependencies(session, PROJECT_B, 1);
		assertDependencies(session, PROJECT_C, 2);
		assertDependencies(session, PROJECT_D, 0);
		assertDependencies(session, PROJECT_E, 0);
		assertNoNullGavs(session);
	}

	@Test
	public void test03()
	{
		//arrange
		Session session = new Session();
		//act
		runFullRecursiveAnalysis(session, "testSets/set03");
		//assert
		assertProjects(session, 4);
		assertDependencies(session, PROJECT_A, 2);
		assertDependencies(session, PROJECT_B, 0);
		assertDependencies(session, PROJECT_C, 1);
		assertDependencies(session, PROJECT_D, 0);
		assertNoNullGavs(session);
	}

	@Test
	public void test04()
	{
		//arrange
		Session session = new Session();
		//act
		runFullRecursiveAnalysis(session, "testSets/set04");
		//assert
		assertProjects(session, 4);
		assertDependencies(session, PROJECT_A, 2);
		assertDependencies(session, PROJECT_B, 0);
		assertDependencies(session, PROJECT_C, 1);
		assertDependencies(session, PROJECT_D, 0);
		assertNoNullGavs(session);
	}

	@Test
	public void test05()
	{
		//arrange
		Session session = new Session();
		//act
		runFullRecursiveAnalysis(session, "testSets/set05");
		//assert
		assertProjects(session, 2);
		assertDependencies(session, PROJECT_A, 1);
		assertParentDependency(session, PROJECT_A, PROJECT_B);
		assertDependencies(session, PROJECT_B, 1);

		List<String> shouldBeMissing = new ArrayList<>();
		shouldBeMissing.add(PROJECT_D);
		shouldBeMissing.add(PROJECT_C);
		shouldBeMissing.add(PROJECT_C);

		session.projects().values().forEach(project ->
		{
			System.out.println("PROJECT " + project);
			System.out.println("DEPENDENCIES");
			session.graph().read().dependencies(project.getGav()).forEach(System.out::println);

			 // Checks that transitive dependencies cannot be resolved
			TransitivityResolver resolver = new TransitivityResolver();
			resolver.getTransitiveDependencyTree(session, project, true, true, null, new PomFileLoader()
			{
				@Override
				public File loadPomFileForGav(Gav gav, List<Repository> additionalRepos, Log log)
				{
					assertTrue(shouldBeMissing.contains(gav.toString()));
					shouldBeMissing.remove(gav.toString());

					return null;
				}
			}, System.out::println);
		});

		assertTrue(shouldBeMissing.isEmpty());
	}

	@Test
	public void test06()
	{
		//arrange
		Session session = new Session();
		//act
		runFullRecursiveAnalysis(session, "testSets/set06");
		//assert
		assertProjects(session, 4);
		assertDependencies(session, PROJECT_A, 1);
		assertParentDependency(session, PROJECT_A, PROJECT_B);
		assertDependencies(session, PROJECT_B, 1);
		assertDependencies(session, PROJECT_C, 0);
		assertDependencies(session, PROJECT_D, 0);

		session.projects().values().forEach(project ->
		{
			System.out.println("PROJECT " + project);
			System.out.println("DEPENDENCIES");
			session.graph().read().dependencies(project.getGav()).forEach(System.out::println);


			// Checks that transitive dependencies can be resolved
			TransitivityResolver resolver = new TransitivityResolver();
			resolver.getTransitiveDependencyTree(session, project, true, true, null, new PomFileLoader()
			{
				@Override
				public File loadPomFileForGav(Gav gav, List<Repository> additionalRepos, Log log)
				{
					fail("missing gav " + gav + " but should not!");

					return null;
				}
			}, System.out::println);
		});
	}

	@Test
	public void test07()
	{
		//arrange
		Session session = new Session();
		//act
		runFullRecursiveAnalysis(session, "testSets/set07");
		//assert
		assertProjects(session, 2);
		assertDependencies(session, PROJECT_A, 1);
		assertParentDependency(session, PROJECT_A, PROJECT_B);
		assertDependencies(session, PROJECT_B, 0);

		Project project = session.projects().forGav(Gav.parse("fr.lteconsulting:a:1.0-SNAPSHOT"));
		assertNotNull(project);

		Map<GroupArtifact, String> pluginManagement = project.getHierarchicalPluginDependencyManagement(null, null, session.projects(), System.out::println);

		assertEquals(2, pluginManagement.size());
		assertEquals("1.0-SNAPSHOT", pluginManagement.get(new GroupArtifact("fr.lteconsulting", "plugin-a")));
		assertEquals("4", pluginManagement.get(new GroupArtifact("fr.lteconsulting", "plugin-b")));

		Set<Gav> plugins = project.getLocalPluginDependencies(null, session.projects(), System.out::println);

		assertEquals(3, plugins.size());
		assertTrue(plugins.contains(new Gav("fr.lteconsulting", "plugin-a", "1.0-SNAPSHOT")));
		assertTrue(plugins.contains(new Gav("fr.lteconsulting", "plugin-b", "4")));
		assertTrue(plugins.contains(new Gav("fr.lteconsulting", "plugin-c", "5")));
	}

	@Test
	public void test08()
	{
		//arrange
		Session session = new Session();
		//act
		runFullRecursiveAnalysis(session, "testSets/set08");
		//assert
		assertProjects(session, 1);
		assertDependencies(session, PROJECT_A, 1);

		Project project = session.projects().forGav(Gav.parse("fr.lteconsulting:a:1.0-SNAPSHOT"));
		assertNotNull(project);
	}


	@Test
	public void multiModule()
	{
		//arrange
		Session session = new Session();
		//act
		runFullRecursiveAnalysis(session, "testSets/multiModule");
		//assert
		assertProjects(session, 6);
		assertDependencies(session, PROJECT_A, 0);
		assertDependencies(session, PROJECT_B, 2);
		assertParentDependency(session, PROJECT_B, PROJECT_A);
		assertDependencies(session, PROJECT_C, 4);
		assertParentDependency(session, PROJECT_C, PROJECT_A);
		assertDependencies(session, PROJECT_D, 3);
		assertParentDependency(session, PROJECT_D, PROJECT_A);
		assertDependencies(session, PROJECT_E, 1);
		assertDependencies(session, PROJECT_F, 0);
		assertNoNullGavs(session);
	}


	@Test
	public void pomDependency()
	{
		//arrange
		Session session = new Session();
		//act
		runFullRecursiveAnalysis(session, "testSets/pomDependency");
		//assert
		assertProjects(session, 6);
		assertDependencies(session, PROJECT_A, 0);
		assertDependencies(session, PROJECT_B, 1);
		assertTransitiveDependency(session, PROJECT_B, 2);
		assertParentDependency(session, PROJECT_B, PROJECT_A);
		assertDependencies(session, PROJECT_C, 4);
		assertParentDependency(session, PROJECT_C, PROJECT_A);
		assertDependencies(session, PROJECT_D, 3);
		assertParentDependency(session, PROJECT_D, PROJECT_A);
		assertDependencies(session, PROJECT_E, 1);
		assertDependencies(session, PROJECT_F, 0);
		assertNoNullGavs(session);
	}

	@Test
	@Ignore("Regression test for #48")
	public void unresolvedParent()
	{
		//arrange
		Session session = new Session();
		//act
		runFullRecursiveAnalysis(session, "testSets/unresolvedParent");
		//assert
		assertProjects(session, 1);
		assertDependencies(session, PROJECT_A, 1);
		assertParentDependency(session, PROJECT_A, PROJECT_C);

		List<String> shouldBeMissing = new ArrayList<>();
		shouldBeMissing.add(PROJECT_D);
		shouldBeMissing.add(PROJECT_C);

		session.projects().values().forEach(project ->
		{
			System.out.println("PROJECT " + project);
			System.out.println("DEPENDENCIES");
			session.graph().read().dependencies(project.getGav()).forEach(System.out::println);

			// Checks that transitive dependencies cannot be resolved
			TransitivityResolver resolver = new TransitivityResolver();
			resolver.getTransitiveDependencyTree(session, project, true, true, null, new PomFileLoader()
			{
				@Override
				public File loadPomFileForGav(Gav gav, List<Repository> additionalRepos, Log log)
				{
					assertTrue(shouldBeMissing.contains(gav.toString()));
					shouldBeMissing.remove(gav.toString());

					return null;
				}
			}, System.out::println);
		});

		assertTrue(shouldBeMissing.isEmpty());
	}

	@Test
	public void pomWithoutGroupId()
	{
		//arrange
		Session session = new Session();
		//act
		PomAnalysis pomAnalysis = runFullRecursiveAnalysis(session, "testSets/pomWithoutGroupId");
		//assert
		assertProjects(session, 0);
		assertPomFilesWithErrors(pomAnalysis, 1);
	}

	@Test
	public void pomWithoutVersion()
	{
		//arrange
		Session session = new Session();
		//act
		PomAnalysis pomAnalysis = runFullRecursiveAnalysis(session, "testSets/pomWithoutVersion");
		//assert
		assertProjects(session, 0);
		assertPomFilesWithErrors(pomAnalysis, 1);
	}


	@Test
	public void localTest1()
	{
		Session session = new Session();

		try {
			String directory;
			// directory = "c:\\Documents\\Repos\\formation-programmation-java\\projets\\javaee\\cartes-webapp";
			directory = "c:\\Documents\\Repos";
			PomAnalysis.runFullRecursiveAnalysis(directory, session, new DefaultPomFileLoader(session, true), null, true, System.out::println);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("DEPENDENCIES");
		session.graph().read().dependencies(Gav.parse("fr.lteconsulting:pom-explorer:1.1-SNAPSHOT")).forEach(System.out::println);

		System.out.println("BUILD DEPENDENCIES");
		session.graph().read().buildDependencies(Gav.parse("fr.lteconsulting:pom-explorer:1.1-SNAPSHOT")).forEach(System.out::println);

		System.out.println("NULL VERSION GAVS");
		PomGraphReadTransaction tx = session.graph().read();
		tx.gavs().stream()
				.sorted(Comparator.comparing(Gav::toString))
				.filter(gav -> gav.getVersion() == null)
				.forEach(gav ->
				{
					System.out.println(gav);
					tx.dependenciesRec(gav).stream().forEach(r -> System.out.println(" - " + r));
				});
	}

	@Test
	public void localTest2()
	{
		Session session = new Session();

		Log log = System.out::println;

		DefaultPomFileLoader pomLoader = new DefaultPomFileLoader(session, true);

		PomAnalysis analyzis = new PomAnalysis(session, pomLoader, null, false, log);

		analyzis.addDirectory("c:\\Documents\\Repos");
		analyzis.addDirectory("C:\\Users\\Arnaud\\.m2\\repository");

		analyzis.loadProjects();

		analyzis.completeLoadedProjects();

		analyzis.addCompletedProjectsToSession();

		analyzis.addCompletedProjectsToGraph();

		System.out.println("GAVS");
		PomGraphReadTransaction tx = session.graph().read();
		tx.gavs().stream()
				.sorted(Comparator.comparing(Gav::toString))
				.forEach(System.out::println);
	}


	private PomAnalysis runFullRecursiveAnalysis(Session session, String testSet)
	{
		return PomAnalysis.runFullRecursiveAnalysis(testSet, session, null, null, true, System.out::println);
	}

	private void assertProjects(Session session, int numberOfProjects)
	{
		ProjectRepository projects = session.projects();
		projects.values().forEach(project -> System.out.println("PROJECT " + project));
		assertEquals("number of projects", numberOfProjects, projects.size());
	}


	private void assertPomFilesWithErrors(PomAnalysis pomAnalysis, int numberOfPomFiles)
	{
		System.out.println("ERRONEOUS POM FILES");
		List<PomReadingException> files = pomAnalysis.getErroneousPomFiles();
		files.forEach(System.out::println);
		assertEquals("number of projects with errors", numberOfPomFiles, files.size());
	}

	private void assertDependenciesAndBuildDependencies(Session session, String gavString, int numberOfDependencies, int numberOfBuildDependencies)
	{
		assertDependencies(session, gavString, numberOfDependencies, "DEPENDENCIES RESULT FOR " + gavString + "\ndependencies:");
		assertBuildDependencies(session, gavString, numberOfBuildDependencies);
	}

	private void assertDependencies(Session session, String gavString, int numberOfDependencies)
	{
		assertDependencies(session, gavString, numberOfDependencies, "DEPENDENCIES OF " + gavString);
	}
	private void assertTransitiveDependency(Session session, String gavString, int numberOfDependencies)
	{
		System.out.println("TRANSITIVE DEPENDENCIES OF " + gavString);
		Set<DependencyRelation> dependencies = session.graph().read().dependenciesRec(Gav.parse(gavString));
		dependencies.forEach(System.out::println);
		assertEquals("transitive dependencies of " + gavString, numberOfDependencies, dependencies.size());
	}

	private void assertDependencies(Session session, String gavString, int numberOfDependencies, String message)
	{
		System.out.println(message);
		Set<DependencyRelation> dependencies = session.graph().read().dependencies(Gav.parse(gavString));
		dependencies.forEach(System.out::println);
		assertEquals("dependencies of " + gavString, numberOfDependencies, dependencies.size());
	}

	private void assertBuildDependencies(Session session, String gavString, int numberOfDependencies)
	{
		System.out.println("build dependencies:");
		Set<BuildDependencyRelation> buildDependencies = session.graph().read().buildDependencies(Gav.parse(gavString));
		buildDependencies.forEach(System.out::println);
		assertEquals("build dependencies of " + gavString, numberOfDependencies, buildDependencies.size());
	}

	private void assertParentDependency(Session session, String gavString, String parentGav)
	{
		Gav parent = session.graph().read().parent(Gav.parse(gavString));
		assertEquals("parent dependency of " + gavString, parent, Gav.parse(parentGav));
	}

	private void assertNoNullGavs(Session session)
	{
		assertNullGavs(session, 0);
	}

	private void assertNullGavs(Session session, int numberOfNullGavs)
	{
		System.out.println("NULL VERSION GAVS");
		List<Gav> nullGavs = session.graph().read().gavs().stream()
				.filter(gav -> gav.getVersion() == null)
				.sorted(Comparator.comparing(Gav::toString))
				.collect(Collectors.toList());
		nullGavs.forEach(System.out::println);
		assertEquals("number of null gavs", numberOfNullGavs, nullGavs.size());
	}
}
