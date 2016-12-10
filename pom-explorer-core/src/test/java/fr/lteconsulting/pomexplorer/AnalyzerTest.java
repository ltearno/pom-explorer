package fr.lteconsulting.pomexplorer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;

public class AnalyzerTest
{
	@Test
	public void test()
	{
		Session session = new Session();

		PomAnalyzer analyzer = new PomAnalyzer();
		analyzer.analyze( "testSets/set01", true, true, true, null, session, System.out::println );

		PomGraphReadTransaction tx = session.graph().read();
		tx.gavs().stream()
				.sorted( ( g1, g2 ) -> g1.toString().compareTo( g2.toString() ) )
				.forEach( gav -> System.out.println( gav ) );

		assertEquals( 1, session.projects().size() );
	}
}
