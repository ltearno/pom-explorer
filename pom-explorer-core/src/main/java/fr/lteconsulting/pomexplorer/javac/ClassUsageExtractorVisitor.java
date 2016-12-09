package fr.lteconsulting.pomexplorer.javac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;

public class ClassUsageExtractorVisitor extends ASTVisitor
{
	private String currentFilePath;
	private Set<String> qualifiedNames = new HashSet<>();

	private Map<String, List<String>> fqnUsage = new HashMap<>();

	public Set<String> getQualifiedNames()
	{
		return qualifiedNames;
	}

	public Map<String, List<String>> getFqnUsage()
	{
		return fqnUsage;
	}

	public void setParsedFile( String path )
	{
		currentFilePath = path;
	}

	@Override
	public boolean visit( ImportDeclaration node )
	{
		maybeAddName( node.getName().getFullyQualifiedName() );
		return true;
	}

	@Override
	public boolean visit( QualifiedName node )
	{
		maybeAddName( node.getFullyQualifiedName() );
		return true;
	}

	private void maybeAddName( String name )
	{
		String partToCheck;

		int dot = name.lastIndexOf( '.' );
		if( dot < 0 )
			partToCheck = name;
		else
			partToCheck = name.substring( dot + 1 );

		if( partToCheck.isEmpty() )
			return;

		String firstLetter = partToCheck.substring( 0, 1 );
		if( !firstLetter.toUpperCase().equals( firstLetter ) )
			return;

		List<String> users = fqnUsage.get( name );
		if( users == null )
		{
			users = new ArrayList<>();
			fqnUsage.put( name, users );
		}

		users.add( currentFilePath );

		qualifiedNames.add( name );
	}
}