package fr.lteconsulting.pomexplorer.javac;

import java.io.File;
import java.nio.file.Paths;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;

public class JavaSourceAnalyzer
{
	private ClassUsageExtractorVisitor usageExtractor = new ClassUsageExtractorVisitor();

	private int nbDirectoryTraversed = 0;

	private int nbFilesParsed = 0;

	public void analyzeProject( Project project, boolean logFqns, Log log )
	{
		analyzeDirectory( Paths.get( project.getPomFile().getParent(), "src" ).toString(), logFqns, log );
	}

	public void analyzeDirectory( String directory, boolean logFqns, Log log )
	{
		log.html( "processing parsing java directory : " + directory + "<br/>" );
		log.html( "<i><b>warning</b> : the analyzed fqns may contain false positives, because the java parsing is not fine tuned yet.<br/>"
				+ "Also it does not detect references made to inner classes (that can be fixed) and through reflection (cannot be fixed), like Class.forName(...) calls.<br/>"
				+ "Feel free to submit a pull request !</i><br/>" );

		processFile( new File( directory ) );

		log.html( "finished : " + nbDirectoryTraversed + " directories traversed, " + nbFilesParsed + " java files parsed<br/>" );

		if( logFqns )
		{
			log.html( "referenced fqns :<br/>" );
			usageExtractor.getQualifiedNames().stream().filter( s -> s != null ).forEachOrdered( fqn -> log.html( fqn + "<br/>" ) );
		}
	}

	public ClassUsageExtractorVisitor getUsageExtractor()
	{
		return usageExtractor;
	}

	private void processFile( File file )
	{
		if( file == null )
			return;

		if( file.isDirectory() )
		{
			nbDirectoryTraversed++;
			for( File f : file.listFiles() )
				processFile( f );
		}
		else if( file.getName().endsWith( ".java" ) )
		{
			nbFilesParsed++;
			System.out.println( "parsing " + file.getAbsolutePath() );
			processJavaFile( file );
		}
	}

	private void processJavaFile( File file )
	{
		String content = Tools.readFile( file );
		if( content == null )
		{
			System.out.println( "Cannot read file " + file.getAbsolutePath() );
			return;
		}

		ASTParser parser = ASTParser.newParser( AST.JLS8 );
		parser.setSource( content.toCharArray() );
		parser.setKind( ASTParser.K_COMPILATION_UNIT );

		CompilationUnit cu = (CompilationUnit) parser.createAST( null );

		usageExtractor.setParsedFile( file.getAbsolutePath() );
		cu.accept( usageExtractor );
	}
}
