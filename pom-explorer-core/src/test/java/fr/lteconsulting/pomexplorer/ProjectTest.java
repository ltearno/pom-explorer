package fr.lteconsulting.pomexplorer;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ProjectTest
{
	private Project testee = new Project( new File( "dummy" ), true );
	private Log log = mock( Log.class );

	@Test
	public void interpolateValueEx_NormalValue_ReturnsValue()
	{
		//arrange
		String value = "test";
		ProjectContainer projectContainer = mock( ProjectContainer.class );
		//act
		ValueResolution result = testee.interpolateValueEx( value, projectContainer, log );
		//assert
		assertThat( result.getResolved() ).isEqualTo( value );
	}

	@Test
	public void interpolateValueEx_Property_ReturnsValueOfProperty()
	{
		//arrange
		String property = "${tutu}";
		ProjectContainer projectContainer = mock( ProjectContainer.class );
		Project testee = new Project( new File( "testSets/dependencyWithExclusion/d.pom" ), true );
		testee.readPomFile();
		//act
		ValueResolution result = testee.interpolateValueEx( property, projectContainer, log );
		//assert
		assertThat( result.getResolved() ).isEqualTo( "no..." );
	}


	@Test
	public void interpolateValueEx_PropertyReferencingSingleProperty_ReturnsValueOfRef()
	{
		//arrange
		String property = "${testSingle}";
		ProjectContainer projectContainer = mock( ProjectContainer.class );
		Project testee = new Project( new File( "testSets/dependencyWithExclusion/d.pom" ), true );
		testee.readPomFile();
		//act
		ValueResolution result = testee.interpolateValueEx( property, projectContainer, log );
		//assert
		assertThat( result.getResolved() ).isEqualTo( "1.0" );
	}

	@Test
	public void interpolateValueEx_PropertyReferencingSinglePropertyAndText_ReturnsValueOfRefAndText()
	{
		//arrange
		String property = "${testSingleWithText}";
		ProjectContainer projectContainer = mock( ProjectContainer.class );
		Project testee = new Project( new File( "testSets/dependencyWithExclusion/d.pom" ), true );
		testee.readPomFile();
		//act
		ValueResolution result = testee.interpolateValueEx( property, projectContainer, log );
		//assert
		assertThat( result.getResolved() ).isEqualTo( "1.0-SNAPSHOT" );
	}

	@Test
	public void interpolateValueEx_PropertyReferencingMultiProperties_ReturnsValueOfRefs()
	{
		//arrange
		String property = "${testMulti}";
		ProjectContainer projectContainer = mock( ProjectContainer.class );
		Project testee = new Project( new File( "testSets/dependencyWithExclusion/d.pom" ), true );
		testee.readPomFile();
		//act
		ValueResolution result = testee.interpolateValueEx( property, projectContainer, log );
		//assert
		assertThat( result.getResolved() ).isEqualTo( "1.02.0" );
	}

	@Test
	public void interpolateValueEx_PropertyReferencingMultiPropertiesAndText_ReturnsValueOfRefsAndText()
	{
		//arrange
		String property = "${testMultiWithText}";
		ProjectContainer projectContainer = mock( ProjectContainer.class );
		Project testee = new Project( new File( "testSets/dependencyWithExclusion/d.pom" ), true );
		testee.readPomFile();
		//act
		ValueResolution result = testee.interpolateValueEx( property, projectContainer, log );
		//assert
		assertThat( result.getResolved() ).isEqualTo( "1.0.1.0-SNAPSHOT" );
	}

	@Test
	public void interpolateValueEx_PropertyRefAnotherRefAnotherWithMultiPropertiesAndText_ReturnsValueOfRefs()
	{
		//arrange
		String property = "${testRef}";
		ProjectContainer projectContainer = mock( ProjectContainer.class );
		Project testee = new Project( new File( "testSets/dependencyWithExclusion/d.pom" ), true );
		testee.readPomFile();
		//act
		ValueResolution result = testee.interpolateValueEx( property, projectContainer, log );
		//assert
		assertThat( result.getResolved() ).isEqualTo( "1.0.1.0-SNAPSHOT" );
	}

	@Test
	public void interpolateValueEx_UnknownProperty_ReturnsNullAndUnresolvedPropertyContainsIt()
	{
		//arrange
		String propertyName = "unknown";
		String property = "${" + propertyName + "}";
		ProjectContainer projectContainer = mock( ProjectContainer.class );
		Project testee = new Project( new File( "testSets/dependencyWithExclusion/d.pom" ), true );
		testee.readPomFile();
		//act
		ValueResolution result = testee.interpolateValueEx( property, projectContainer, log );
		//assert
		assertThat( result.getResolved() ).isEqualTo( "null" );
		assertThat( testee.getUnresolvedProperties() ).containsExactly( propertyName );
	}

	@Test
	public void interpolateValueEx_PropertyReferencingUnknownProperty_ReturnsNullAndUnresolvedPropertyContainsIt()
	{
		//arrange
		String property = "${" + "unknownRef" + "}";
		ProjectContainer projectContainer = mock( ProjectContainer.class );
		Project testee = new Project( new File( "testSets/dependencyWithExclusion/d.pom" ), true );
		testee.readPomFile();
		//act
		ValueResolution result = testee.interpolateValueEx( property, projectContainer, log );
		//assert
		assertThat( result.getResolved() ).isEqualTo( "null" );
		assertThat( testee.getUnresolvedProperties() ).containsExactly( "unknown" );
	}

	//TODO Regressiontest of https://github.com/ltearno/pom-explorer/issues/65
	@Test
	@Ignore
	public void interpolateValueEx_DollarCurlyBraceButNotAProperty_ReturnsValue()
	{
		//arrange
		String value = "${hello";
		ProjectContainer projectContainer = mock( ProjectContainer.class );
		Project testee = new Project( new File( "testSets/dependencyWithExclusion/d.pom" ), true );
		testee.readPomFile();
		//act
		ValueResolution result = testee.interpolateValueEx( value, projectContainer, log );
		//assert
		assertThat( result.getResolved() ).isEqualTo( value );
	}
}
