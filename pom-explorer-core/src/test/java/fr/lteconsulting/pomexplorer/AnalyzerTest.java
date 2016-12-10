package fr.lteconsulting.pomexplorer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.model.Gav;

public class AnalyzerTest
{
	@Test
	public void test()
	{
		Session session = new Session();

		PomAnalyzer analyzer = new PomAnalyzer();
		analyzer.analyze( "testSets/set01", true, true, true, null, session, System.out::println );

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
		PomGraphReadTransaction tx = session.graph().read();
		tx.gavs().stream()
				.sorted( ( g1, g2 ) -> g1.toString().compareTo( g2.toString() ) )
				.filter( gav -> gav.getVersion() == null )
				.forEach( gav -> {
					// assertNotNull( gav.getVersion() );
					System.out.println( gav );
				} );
	}

	@Test
	public void test02()
	{
		Session session = new Session();

		PomAnalyzer analyzer = new PomAnalyzer();

		try
		{
			analyzer.analyze( "c:\\Documents\\Repos", true, true, true, null, session, System.out::println );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		// assertEquals( 1, session.projects().size() );

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
					// assertNotNull( gav.getVersion() );
					System.out.println( gav );
				} );
	}
}
