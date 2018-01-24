package fr.lteconsulting.pomexplorer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.*;

import fr.lteconsulting.pomexplorer.graph.relation.Scope;
import fr.lteconsulting.pomexplorer.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.model.GroupArtifact;
import fr.lteconsulting.pomexplorer.model.transitivity.Repository;

public class AnalyzerTest
{
	@Test
	public void test01()
	{
		Session session = new Session();

		PomAnalysis.runFullRecursiveAnalysis( "testSets/set01", session, null, null, true, System.out::println );

		assertEquals( 1, session.projects().size() );

		System.out.println( "DEPENDENCIES" );
		session.graph().read().dependencies( Gav.parse( "fr.lteconsulting:pom-explorer:1.1-SNAPSHOT" ) ).forEach(System.out::println);

		System.out.println( "BUILD DEPENDENCIES" );
		session.graph().read().buildDependencies( Gav.parse( "fr.lteconsulting:pom-explorer:1.1-SNAPSHOT" ) ).forEach(System.out::println);

		System.out.println( "NULL VERSION GAVS" );
		session.graph().read().gavs().stream()
				.sorted(Comparator.comparing(Gav::toString))
				.filter( gav -> gav.getVersion() == null )
				.forEach(System.out::println);
	}

	@Test
	public void test04()
	{
		Session session = new Session();

		PomAnalysis.runFullRecursiveAnalysis( "testSets/set02", session, null, null, true, System.out::println );

		assertEquals( 5, session.projects().size() );

		session.projects().values().forEach( project ->
		{
			System.out.println( "PROJECT " + project );
		} );
	}

	@Test
	public void test05()
	{
		Session session = new Session();

		PomAnalysis.runFullRecursiveAnalysis( "testSets/set05", session, null, null, true, System.out::println );

		assertEquals( 2, session.projects().size() );

		List<String> shouldBeMissing = new ArrayList<>();
		shouldBeMissing.add( "fr.lteconsulting:d:1.0-SNAPSHOT" );
		shouldBeMissing.add( "fr.lteconsulting:c:1.0-SNAPSHOT" );
		shouldBeMissing.add( "fr.lteconsulting:c:1.0-SNAPSHOT" );

		session.projects().values().forEach( project ->
		{
			System.out.println( "PROJECT " + project );
			System.out.println( "DEPENDENCIES" );
			session.graph().read().dependencies( project.getGav() ).forEach(System.out::println);

			/**
			 * Checks that transitive dependencies cannot be resolved
			 */
			TransitivityResolver resolver = new TransitivityResolver();
			resolver.getTransitiveDependencyTree( session, project, true, true, null, new PomFileLoader()
			{
				@Override
				public File loadPomFileForGav( Gav gav, List<Repository> additionalRepos, Log log )
				{
					assertTrue( shouldBeMissing.contains( gav.toString() ) );
					shouldBeMissing.remove( gav.toString() );

					return null;
				}
			}, System.out::println );
		} );

		assertTrue( shouldBeMissing.isEmpty() );
	}

	@Test
	public void test06()
	{
		Session session = new Session();

		PomAnalysis.runFullRecursiveAnalysis( "testSets/set06", session, null, null, true, System.out::println );

		assertEquals( 4, session.projects().size() );

		session.projects().values().forEach( project ->
		{
			System.out.println( "PROJECT " + project );
			System.out.println( "DEPENDENCIES" );
			session.graph().read().dependencies( project.getGav() ).forEach(System.out::println);

			/**
			 * Checks that transitive dependencies can be resolved
			 */
			TransitivityResolver resolver = new TransitivityResolver();
			resolver.getTransitiveDependencyTree( session, project, true, true, null, new PomFileLoader()
			{
				@Override
				public File loadPomFileForGav( Gav gav, List<Repository> additionalRepos, Log log )
				{
					fail( "missing gav " + gav + " but should not!" );

					return null;
				}
			}, System.out::println );
		} );
	}

	@Test
	public void test07()
	{
		Session session = new Session();

		PomAnalysis.runFullRecursiveAnalysis( "testSets/set07", session, null, null, true, System.out::println );

		assertEquals( 2, session.projects().size() );

		Project project = session.projects().forGav( Gav.parse( "fr.lteconsulting:a:1.0-SNAPSHOT" ) );
		assertNotNull( project );

		Map<GroupArtifact, String> pluginManagement = project.getHierarchicalPluginDependencyManagement( null, null, session.projects(), System.out::println );

		assertEquals( 2, pluginManagement.size() );
		assertEquals( "1.0-SNAPSHOT", pluginManagement.get( new GroupArtifact( "fr.lteconsulting", "plugin-a" ) ) );
		assertEquals( "4", pluginManagement.get( new GroupArtifact( "fr.lteconsulting", "plugin-b" ) ) );

		Set<Gav> plugins = project.getLocalPluginDependencies( null, session.projects(), System.out::println );

		assertEquals( 3, plugins.size() );
		assertTrue( plugins.contains( new Gav( "fr.lteconsulting", "plugin-a", "1.0-SNAPSHOT" ) ) );
		assertTrue( plugins.contains( new Gav( "fr.lteconsulting", "plugin-b", "4" ) ) );
		assertTrue( plugins.contains( new Gav( "fr.lteconsulting", "plugin-c", "5" ) ) );
	}

	@Test
	public void test08()
	{
		Session session = new Session();

		PomAnalysis.runFullRecursiveAnalysis( "testSets/set08", session, null, null, true, System.out::println );

		assertEquals( 1, session.projects().size() );

		Project project = session.projects().forGav( Gav.parse( "fr.lteconsulting:a:1.0-SNAPSHOT" ) );
		assertNotNull( project );
	}

	@Test
	public void test02()
	{
		Session session = new Session();

		try
		{
			String directory;
			// directory = "c:\\Documents\\Repos\\formation-programmation-java\\projets\\javaee\\cartes-webapp";
			directory = "c:\\Documents\\Repos";
			PomAnalysis.runFullRecursiveAnalysis( directory, session, new DefaultPomFileLoader( session, true ), null, true, System.out::println );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		System.out.println( "DEPENDENCIES" );
		session.graph().read().dependencies( Gav.parse( "fr.lteconsulting:pom-explorer:1.1-SNAPSHOT" ) ).forEach(System.out::println);

		System.out.println( "BUILD DEPENDENCIES" );
		session.graph().read().buildDependencies( Gav.parse( "fr.lteconsulting:pom-explorer:1.1-SNAPSHOT" ) ).forEach(System.out::println);

		System.out.println( "NULL VERSION GAVS" );
		PomGraphReadTransaction tx = session.graph().read();
		tx.gavs().stream()
				.sorted(Comparator.comparing(Gav::toString))
				.filter( gav -> gav.getVersion() == null )
				.forEach( gav ->
				{
					System.out.println( gav );
					tx.dependenciesRec( gav ).stream().forEach( r -> System.out.println( " - " + r ) );
				} );
	}

	@Test
	public void test03()
	{
		Session session = new Session();

		Log log = System.out::println;

		DefaultPomFileLoader pomLoader = new DefaultPomFileLoader( session, true );

		PomAnalysis analyzis = new PomAnalysis( session, pomLoader, null, false, log );

		analyzis.addDirectory( "c:\\Documents\\Repos" );
		analyzis.addDirectory( "C:\\Users\\Arnaud\\.m2\\repository" );

		analyzis.loadProjects();

		analyzis.completeLoadedProjects();

		analyzis.addCompletedProjectsToSession();

		analyzis.addCompletedProjectsToGraph();

		System.out.println( "GAVS" );
		PomGraphReadTransaction tx = session.graph().read();
		tx.gavs().stream()
				.sorted(Comparator.comparing(Gav::toString))
				.forEach(System.out::println);
	}
}
