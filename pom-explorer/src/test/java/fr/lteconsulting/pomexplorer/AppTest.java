package fr.lteconsulting.pomexplorer;

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

	public void testOpenVersions()
	{
		checkOpenVersion("15.6.0", "16.6.0-SNAPSHOT");
		checkOpenVersion("15060", "15061-SNAPSHOT");
		checkOpenVersion("15060.0", "15061.0-SNAPSHOT");
		checkOpenVersion("15060.0.5", "15061.0.5-SNAPSHOT");
	}

	private void checkOpenVersion(String closed, String opened)
	{
		GAV newGav = Tools.openGavVersion(new GAV("group", "artifact", closed));
		assertEquals(opened, newGav.getVersion());
	}
}
