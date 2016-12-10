package fr.lteconsulting.pomexplorer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.model.Gav;
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
		session.graph().read().dependencies( Gav.parse( "fr.lteconsulting:pom-explorer:1.1-SNAPSHOT" ) ).forEach( relation -> {
			System.out.println( relation );
		} );

		System.out.println( "BUILD DEPENDENCIES" );
		session.graph().read().buildDependencies( Gav.parse( "fr.lteconsulting:pom-explorer:1.1-SNAPSHOT" ) ).forEach( relation -> {
			System.out.println( relation );
		} );

		System.out.println( "NULL VERSION GAVS" );
		session.graph().read().gavs().stream()
				.sorted( ( g1, g2 ) -> g1.toString().compareTo( g2.toString() ) )
				.filter( gav -> gav.getVersion() == null )
				.forEach( gav -> {
					System.out.println( gav );
				} );
	}

	@Test
	public void test04()
	{
		Session session = new Session();

		PomAnalysis.runFullRecursiveAnalysis( "testSets/set02", session, null, null, true, System.out::println );

		assertEquals( 5, session.projects().size() );

		session.projects().values().forEach( project -> {
			System.out.println( "PROJECT " + project );
		} );
	}

	@Test
	public void test05()
	{
		Session session = new Session();

		PomAnalysis.runFullRecursiveAnalysis( "testSets/set05", session, null, null, true, System.out::println );

		assertEquals( 2, session.projects().size() );

		session.projects().values().forEach( project -> {
			System.out.println( "PROJECT " + project );
			System.out.println( "DEPENDENCIES" );
			session.graph().read().dependencies( project.getGav() ).forEach( dependency -> {
				System.out.println( dependency );
			} );

			TransitivityResolver resolver = new TransitivityResolver();
			resolver.getTransitiveDependencyTree( session, project, true, true, null, new PomFileLoader()
			{
				@Override
				public File loadPomFileForGav( Gav gav, List<Repository> additionalRepos, Log log )
				{
					System.out.println( "MISSING " + gav );
					return null;
				}
			}, System.out::println );
		} );
	}

	@Test
	public void test02()
	{
		Session session = new Session();

		try
		{
			PomAnalysis.runFullRecursiveAnalysis( "c:\\Documents\\Repos", session, new DefaultPomFileLoader( session, true ), null, true, System.out::println );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		System.out.println( "DEPENDENCIES" );
		session.graph().read().dependencies( Gav.parse( "fr.lteconsulting:pom-explorer:1.1-SNAPSHOT" ) ).forEach( relation -> {
			System.out.println( relation );
		} );

		System.out.println( "BUILD DEPENDENCIES" );
		session.graph().read().buildDependencies( Gav.parse( "fr.lteconsulting:pom-explorer:1.1-SNAPSHOT" ) ).forEach( relation -> {
			System.out.println( relation );
		} );

		System.out.println( "NULL VERSION GAVS" );
		PomGraphReadTransaction tx = session.graph().read();
		tx.gavs().stream()
				.sorted( ( g1, g2 ) -> g1.toString().compareTo( g2.toString() ) )
				.filter( gav -> gav.getVersion() == null )
				.forEach( gav -> {
					System.out.println( gav );
				} );
	}

	@Test
	public void test03()
	{
		Session session = new Session();

		Log log = new Log()
		{
			@Override
			public void html( String log )
			{
				System.out.println( log );
			}
		};

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
				.sorted( ( g1, g2 ) -> g1.toString().compareTo( g2.toString() ) )
				.forEach( gav -> {
					System.out.println( gav );
				} );
	}
}
