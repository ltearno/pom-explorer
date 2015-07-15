package fr.pgih;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Tools;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

	public void testOpenVersions()
	{
		checkOpenVersion("15.6.0", "15.6.2-SNAPSHOT");
		checkOpenVersion("15060", "15062-SNAPSHOT");
		checkOpenVersion("15060.0", "15062.0-SNAPSHOT");
		checkOpenVersion("15060.0.0", "15062.0.0-SNAPSHOT");
	}

	private void checkOpenVersion(String closed, String opened)
	{
		GAV newGav = Tools.openGavVersion(new GAV("group", "artifact", closed));
		assertEquals(opened, newGav.getVersion());
	}
}
