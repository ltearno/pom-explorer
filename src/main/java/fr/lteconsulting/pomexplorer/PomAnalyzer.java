package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jgrapht.DirectedGraph;

public class PomAnalyzer
{
	public void analyze( String directory, WorkingSession session )
	{
		processFile( new File( directory ), session );
	}

	private void processFile( File file, WorkingSession session )
	{
		if( file == null )
			return;

		if( file.isDirectory() )
		{
			String name = file.getName();
			if( "target".equalsIgnoreCase( name ) || "src".equalsIgnoreCase( name ) )
				return;

			for( File f : file.listFiles() )
				processFile( f, session );
		}
		else if( file.getName().equalsIgnoreCase( "pom.xml" ) )
		{
			processPom( file, session );
		}
	}

	private void processPom( File pom, WorkingSession session )
	{
		System.out.println( "\n# Analysing pom file " + pom.getAbsolutePath() );
		System.out.println( "## Non-resolving analysis" );

		MavenProject project = loadProject( pom );

		System.out.println( project.getGroupId() + ":" + project.getArtifactId() + ":" + project.getVersion() + ":" + project.getPackaging() );

		GAV gav = session.ensureArtifact( project.getGroupId(), project.getArtifactId(), project.getVersion() );

		Parent parent = project.getModel().getParent();
		if( parent != null )
		{
			System.out.println( "   PARENT : " + parent.getId() + ":" + parent.getRelativePath() );
		}

		Properties ptties = project.getProperties();
		if( ptties != null )
		{
			for( Entry<Object, Object> e : ptties.entrySet() )
			{
				System.out.println( "   PPTY: " + e.getKey() + " = " + e.getValue() );
			}
		}
		
		DirectedGraph<GAV, Dep> g = session.getGraph();

		if( project.getDependencyManagement() != null )
		{
			for( Dependency dependency : project.getDependencyManagement().getDependencies() )
			{
				System.out.println( "   MNGT: " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + ":" + dependency.getClassifier() + ":" + dependency.getScope() );

				GAV depGav = session.ensureArtifact( dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion() );

				if( gav != null && depGav != null )
					g.addEdge( gav, depGav, new Dep( dependency.getScope(), dependency.getClassifier() ) );
			}
		}

		for( Dependency dependency : project.getDependencies() )
		{
			System.out.println( "   " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + ":" + dependency.getClassifier() + ":" + dependency.getScope() );

			GAV depGav = session.ensureArtifact( dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion() );

			if( gav != null && depGav != null )
				g.addEdge( gav, depGav, new Dep( dependency.getScope(), dependency.getClassifier() ) );
		}

		System.out.println( "## Resolving analysis" );
		Toto.toto( pom );
	}

	private MavenProject loadProject( File pom )
	{
		Model model = null;
		FileReader reader = null;
		MavenXpp3Reader mavenreader = new MavenXpp3Reader();
		try
		{
			reader = new FileReader( pom );
		}
		catch( FileNotFoundException e1 )
		{
		}
		try
		{
			model = mavenreader.read( reader );
			model.setPomFile( pom );
		}
		catch( IOException | XmlPullParserException e1 )
		{
		}
		MavenProject project = new MavenProject( model );

		return project;
	}
}
