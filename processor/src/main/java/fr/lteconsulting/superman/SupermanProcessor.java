package fr.lteconsulting.superman;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes( SupermanProcessor.AnnotationFqn )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
public class SupermanProcessor extends AbstractProcessor
{
	public final static String AnnotationFqn = "fr.lteconsulting.superman.Superman";

	@Override
	public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv )
	{
		for( TypeElement element : ElementFilter.typesIn( roundEnv.getElementsAnnotatedWith( processingEnv.getElementUtils().getTypeElement( AnnotationFqn ) ) ) )
		{
			processType( element );
		}

		return true;
	}

	private void processType( TypeElement element )
	{
		String template = readResource( "fr/lteconsulting/superman/Superman.txt" );

		// le type est une interface.

		String packageName = ((PackageElement) element.getEnclosingElement()).getQualifiedName().toString();
		String supermanName = element.getSimpleName() + "Superman";

		// ecrire la clase qui l'implémente
		StringBuilder delegate = new StringBuilder();
		StringBuilder methods = new StringBuilder();

		int id = -1;
		for( Element child : element.getEnclosedElements() )
		{
			if( child.getKind() != ElementKind.METHOD )
				continue;

			id++;

			ExecutableElement method = (ExecutableElement) child;

			String returnTypeFqn = method.getReturnType().toString();

			delegate.append( "            case " + id + ":\n" );
			delegate.append( "                return implementation." + method.getSimpleName() + "( " );
			int pi = 0;
			for( VariableElement p : method.getParameters() )
			{
				if( pi > 0 )
					delegate.append( ", " );
				delegate.append( "(" + p.asType() + ") message.getParameters()[" + pi + "]" );
				pi++;
			}
			delegate.append( " );\n" );

			methods.append( "        @Override\n" );
			methods.append( "        public " + returnTypeFqn + " " + method.getSimpleName() + "(" );
			pi = 0;
			for( VariableElement p : method.getParameters() )
			{
				if( pi == 0 )
					methods.append( " " );
				if( pi > 0 )
					methods.append( ", " );
				methods.append( p.asType() + " " + p.getSimpleName() );
				pi++;
			}
			if( pi > 0 )
				methods.append( " " );
			methods.append( ")\n" );
			methods.append( "        {\n" );
			methods.append( "            return (" + returnTypeFqn + ") sendMessage( new Supermessage( " + id + ", new Object[] {" );
			pi = 0;
			for( VariableElement p : method.getParameters() )
			{
				if( pi == 0 )
					methods.append( " " );
				if( pi > 0 )
					methods.append( ", " );
				methods.append( p.getSimpleName().toString() );
				pi++;
			}
			if( pi > 0 )
				methods.append( " " );
			methods.append( "} ) );\n" );
			methods.append( "	    }\n\n" );
		}

		template = template.replaceAll( "PACKAGE", packageName );
		template = template.replaceAll( "CLASS_NAME", supermanName );
		template = template.replaceAll( "INTERFACE", element.getSimpleName().toString() );
		template = template.replaceAll( "DELEGATE", delegate.toString() );
		template = template.replaceAll( "METHODS", methods.toString() );

		try
		{
			JavaFileObject jfo = processingEnv.getFiler().createSourceFile( packageName + "." + supermanName, element );

			OutputStream os = jfo.openOutputStream();
			PrintWriter pw = new PrintWriter( os );
			pw.print( template );
			pw.close();
			os.close();

			processingEnv.getMessager().printMessage( Kind.MANDATORY_WARNING, "Superman généré !", element );
		}
		catch( IOException e )
		{
			e.printStackTrace();
			processingEnv.getMessager().printMessage( Kind.ERROR, "Superman non généré !" + e, element );
		}

	}

	private static String readResource( String fqn )
	{
		try
		{
			return new Scanner( SupermanProcessor.class.getClassLoader().getResourceAsStream( fqn ) ).useDelimiter( "\\A" ).next();
		}
		catch( Exception e )
		{
			return null;
		}
	}
}
