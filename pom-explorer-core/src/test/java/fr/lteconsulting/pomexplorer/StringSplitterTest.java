package fr.lteconsulting.pomexplorer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import fr.lteconsulting.pomexplorer.tools.StringSplitter;

public class StringSplitterTest
{
	@Test
	public void test()
	{
		StringSplitter splitter = new StringSplitter();

		assertEquals( Arrays.asList(), splitter.split( "" ) );
		assertEquals( Arrays.asList(), splitter.split( "  " ) );
		assertEquals( Arrays.asList( "gav", "list", "toto.titi" ), splitter.split( "gav list toto.titi" ) );
		assertEquals( Arrays.asList( "gav", "list", "toto.titi" ), splitter.split( "gav list  toto.titi" ) );
		assertEquals( Arrays.asList( "gav", "list", "toto.titi" ), splitter.split( " gav list  toto.titi  " ) );
		assertEquals( Arrays.asList( "gav", "list", "toto.titi" ), splitter.split( "gav list \"toto.titi\"" ) );
		assertEquals( Arrays.asList( "gav", "list", " toto.titi" ), splitter.split( "  gav  list \" toto.titi\"  " ) );
		assertEquals( Arrays.asList( "toto.titi", "toto.titi" ), splitter.split( "\"toto.titi\" \"toto.titi\" " ) );
	}
}
