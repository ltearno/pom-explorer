package fr.lteconsulting.pomexplorer.change.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.change.project.Location.Dependency;
import fr.lteconsulting.pomexplorer.change.project.Location.DependencyManagement;
import fr.lteconsulting.pomexplorer.change.project.Location.Parent;
import fr.lteconsulting.pomexplorer.change.project.Location.Plugin;
import fr.lteconsulting.pomexplorer.change.project.Location.PluginManagement;
import fr.lteconsulting.pomexplorer.change.project.Location.Property;
import fr.lteconsulting.pomexplorer.model.DependencyKey;

@SuppressWarnings( "deprecation" )
public class Changer
{
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

	XPath xPath = XPathFactory.newInstance().newXPath();

	public void doChanges( Session session, Set<ProjectChange> changes, Log log )
	{
		for( ProjectChange change : changes )
		{
			Project project = change.getProject();
			if( project == null )
				continue;

			String pomPath = project.getPomFile().getAbsolutePath();

			DocumentBuilder builder = null;
			try
			{
				builder = builderFactory.newDocumentBuilder();
			}
			catch( ParserConfigurationException e )
			{
				e.printStackTrace();
			}

			if( builder == null )
				throw new RuntimeException( "Unable to create the document builder !" );

			Document document = null;

			try
			{
				FileInputStream fis = new FileInputStream( pomPath );
				document = builder.parse( fis );
				fis.close();
			}
			catch( SAXException e )
			{
				e.printStackTrace();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}

			if( document == null )
				continue;

			doChange( document, change, log );

			OutputFormat format = new OutputFormat( document );
			format.setIndenting( true );
			XMLSerializer serializer = null;
			try
			{
				FileOutputStream fos = new FileOutputStream( new File( pomPath ) );
				serializer = new XMLSerializer( fos, format );
				serializer.serialize( document );
				fos.close();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	private void doChange( Document document, ProjectChange change, Log log )
	{
		fr.lteconsulting.pomexplorer.change.project.Location location = change.getLocation();
		log.html( "[" + location + "] " );

		location.visit( new fr.lteconsulting.pomexplorer.change.project.Location.Visitor<Void>()
		{
			@Override
			public Void visit( Parent parent )
			{
				setNode( document, "/project/parent", change.getNodeName(), change.getNewValue(), log );
				return null;
			}

			@Override
			public Void visit( fr.lteconsulting.pomexplorer.change.project.Location.Project project )
			{
				setNode( document, "/project", change.getNodeName(), change.getNewValue(), log );
				return null;
			}

			@Override
			public Void visit( Property property )
			{
				setNode( document, "/project/properties", change.getNodeName(), change.getNewValue(), log );
				return null;
			}

			@Override
			public Void visit( Dependency dependency )
			{
				setDependencyNode( document, log, "project/dependencies", dependency.getKey(), change.getNodeName(), change.getNewValue() );
				return null;
			}

			@Override
			public Void visit( DependencyManagement dependencyManagement )
			{
				setDependencyNode( document, log, "project/dependencyManagement/dependencies", dependencyManagement.getKey(), change.getNodeName(), change.getNewValue() );
				return null;
			}

			@Override
			public Void visit( Plugin plugin )
			{
				throw new IllegalStateException( "nyi" );
			}

			@Override
			public Void visit( PluginManagement pluginManagement )
			{
				throw new IllegalStateException( "nyi" );
			}
		} );
	}

	private void setDependencyNode( Document document, Log log, String expression, DependencyKey key, String nodeName, String value )
	{
		NodeList nodeList = getNodes( document, expression );
		if( nodeList == null )
			throw new IllegalStateException( "nyi" );

		Node depNode = getDependencyNode( nodeList, key );
		if( depNode == null )
			throw new IllegalStateException( "nyi" );

		DomHelper.setSubNodeValue( document, depNode, nodeName, value );

		log.html( "dependency '" + key + "' updated '+nodeName+' to '" + value + "'<br/>" );
	}

	private void setNode( Document document, String expression, String nodeName, String nodeValue, Log log )
	{
		NodeList nodeList = getNodes( document, expression );
		if( nodeList == null )
			throw new IllegalStateException( "nyi" );

		Node node = findChildNodeByName( nodeList, nodeName );
		if( node == null )
		{
			log.html( "<span style='color:orange;'>did not find node " + nodeName + " in " + expression + " definition</span>" );
			return;
		}

		node.setTextContent( nodeValue );
		log.html( "'" + nodeName + "' updated to '" + nodeValue + "' in '" + expression + "'<br/>" );
	}

	private NodeList getNodes( Document document, String expression )
	{
		NodeList nodeList = null;
		try
		{
			Node propertiesNode = (Node) xPath.compile( expression ).evaluate( document, XPathConstants.NODE );
			if( propertiesNode != null )
				nodeList = propertiesNode.getChildNodes();
		}
		catch( XPathExpressionException e )
		{
			e.printStackTrace();
		}

		return nodeList;
	}

	private Node getDependencyNode( NodeList nodeList, DependencyKey key )
	{
		if( nodeList == null )
			return null;

		for( int i = 0; i < nodeList.getLength(); i++ )
		{
			Node depNode = nodeList.item( i );
			DependencyKey depKey = getDependencyKeyFromDependencyNode( depNode );

			if( key.equals( depKey ) )
				return depNode;
		}

		return null;
	}

	private Node findChildNodeByName( NodeList nodeList, String nodeName )
	{
		if( nodeList == null )
			return null;

		for( int i = 0; i < nodeList.getLength(); i++ )
		{
			Node node = nodeList.item( i );
			if( nodeName.equals( node.getNodeName() ) )
				return node;
		}

		return null;
	}

	private DependencyKey getDependencyKeyFromDependencyNode( Node depNode )
	{
		return new DependencyKey(
				DomHelper.getSubNodeValue( "groupId", depNode ),
				DomHelper.getSubNodeValue( "artifactId", depNode ),
				DomHelper.getSubNodeValue( "classifier", depNode ),
				DomHelper.getSubNodeValue( "type", depNode ) );
	}
}