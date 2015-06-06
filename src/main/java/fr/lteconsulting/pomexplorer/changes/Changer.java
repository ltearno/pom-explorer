package fr.lteconsulting.pomexplorer.changes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.depanalyze.Location;

public class Changer
{
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	XPath xPath = XPathFactory.newInstance().newXPath();

	public void doChanges( Set<Change<? extends Location>> changes, StringBuilder log )
	{
		for( Change<? extends Location> change : changes )
		{
			Project project = change.getLocation().getProject();
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

			Document document = null;

			try
			{
				document = builder.parse( new FileInputStream( pomPath ) );
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

			if( change instanceof GavChange )
				doChange( document, (GavChange) change, log );
			else if( change instanceof PropertyChange )
				doChange( (PropertyChange) change );

			OutputFormat format = new OutputFormat( document );
			format.setIndenting( true );
			XMLSerializer serializer = null;
			try
			{
				serializer = new XMLSerializer( new FileOutputStream( new File( pomPath ) ), format );
			}
			catch( FileNotFoundException e1 )
			{
				e1.printStackTrace();
				continue;
			}
			try
			{
				serializer.serialize( document );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	private void doChange( Document document, GavChange change, StringBuilder log )
	{
		log.append( "[" + change.getLocation().getSection() + "] " );

		switch( change.getLocation().getSection() )
		{
			case DEPENDENCY:
				replaceDependency( document, change, log );
				break;

			case DEPENDENCY_MNGT:
				break;

			case PARENT:
				replaceParent( document, change, log );
				break;

			case PLUGIN:
				replacePlugin( document, change, log );
				break;

			case PLUGIN_MNGT:
				break;

			case PROJECT:
				replaceProject( document, change, log );
				break;

			default:
				break;
		}
	}

	private void replaceProject( Document document, GavChange change, StringBuilder log )
	{
		GAV replacedGav = change.getLocation().getGav();
		String expression = "/project";

		replaceDependency( document, change, log, replacedGav, expression );
	}

	private void replaceParent( Document document, GavChange change, StringBuilder log )
	{
		GAV replacedGav = change.getLocation().getGav();
		String expression = "/project/parent";

		replaceDependency( document, change, log, replacedGav, expression );
	}

	private void replacePlugin( Document document, GavChange change, StringBuilder log )
	{
		GAV replacedGav = change.getLocation().getGav();
		String expression = "/project/build/plugins/plugin";

		replaceDependency( document, change, log, replacedGav, expression );
	}

	private void replaceDependency( Document document, GavChange change, StringBuilder log )
	{
		GAV replacedGav = change.getLocation().getGav();
		String expression = "/project/dependencies/dependency";

		replaceDependency( document, change, log, replacedGav, expression );
	}

	private void replaceDependency( Document document, GavChange change, StringBuilder log, GAV replacedGav, String expression )
	{
		NodeList nodeList = null;
		try
		{
			nodeList = (NodeList) xPath.compile( expression ).evaluate( document, XPathConstants.NODESET );
		}
		catch( XPathExpressionException e )
		{
			e.printStackTrace();
			return;
		}

		for( int i = 0; i < nodeList.getLength(); i++ )
		{
			Node depNode = nodeList.item( i );
			GAV gav = getGavFromDependencyNode( depNode );

			if( (gav.getGroupId() == null || replacedGav.getGroupId().equals( gav.getGroupId() )) && replacedGav.getArtifactId().equals( gav.getArtifactId() ) )
			{
				// seems a good take !

				if( !replacedGav.getVersion().equals( gav.getVersion() ) )
				{
					log.append( "<span style='color:orange;'>Found a dependency with the wrong version in the pom file : " + gav + " instead of " + replacedGav + "</span>" );
					continue;
				}

				// replace the version value
				setGavInDependencyNode( change.getNewGav(), depNode );
				log.append( "'" + gav + "' updated to '" + change.getNewGav() + "' in '" + (change.getLocation().getProject() != null ? change.getLocation().getProject().getGav() : "-") + "'<br/>" );
			}
		}
	}

	private void doChange( PropertyChange change )
	{
	}

	private GAV getGavFromDependencyNode( Node depNode )
	{
		return new GAV( getSubNodeValue( "groupId", depNode ), getSubNodeValue( "artifactId", depNode ), getSubNodeValue( "version", depNode ) );
	}

	private String getSubNodeValue( String subNodeName, Node node )
	{
		Node subNode = DomHelper.getNode( subNodeName, node.getChildNodes() );
		if( subNode == null )
			return null;
		return DomHelper.getNodeValue( subNode );
	}

	private void setGavInDependencyNode( GAV newGav, Node depNode )
	{
		NodeList list = depNode.getChildNodes();
		for( int i = 0; i < list.getLength(); i++ )
		{
			Node node = list.item( i );
			switch( node.getNodeName() )
			{
				case "groupId":
					node.setTextContent( newGav.getGroupId() );
					break;
				case "artifactId":
					node.setTextContent( newGav.getArtifactId() );
					break;
				case "version":
					node.setTextContent( newGav.getVersion() );
					break;
			}
		}
	}
}

class DomHelper
{
	protected static Node getNode( String tagName, NodeList nodes )
	{
		for( int x = 0; x < nodes.getLength(); x++ )
		{
			Node node = nodes.item( x );
			if( node.getNodeName().equalsIgnoreCase( tagName ) )
			{
				return node;
			}
		}

		return null;
	}

	protected static String getNodeValue( Node node )
	{
		NodeList childNodes = node.getChildNodes();
		for( int x = 0; x < childNodes.getLength(); x++ )
		{
			Node data = childNodes.item( x );
			if( data.getNodeType() == Node.TEXT_NODE )
				return data.getNodeValue();
		}
		return "";
	}

	protected String getNodeValue( String tagName, NodeList nodes )
	{
		for( int x = 0; x < nodes.getLength(); x++ )
		{
			Node node = nodes.item( x );
			if( node.getNodeName().equalsIgnoreCase( tagName ) )
			{
				NodeList childNodes = node.getChildNodes();
				for( int y = 0; y < childNodes.getLength(); y++ )
				{
					Node data = childNodes.item( y );
					if( data.getNodeType() == Node.TEXT_NODE )
						return data.getNodeValue();
				}
			}
		}
		return "";
	}

	protected String getNodeAttr( String attrName, Node node )
	{
		NamedNodeMap attrs = node.getAttributes();
		for( int y = 0; y < attrs.getLength(); y++ )
		{
			Node attr = attrs.item( y );
			if( attr.getNodeName().equalsIgnoreCase( attrName ) )
			{
				return attr.getNodeValue();
			}
		}
		return "";
	}

	protected String getNodeAttr( String tagName, String attrName, NodeList nodes )
	{
		for( int x = 0; x < nodes.getLength(); x++ )
		{
			Node node = nodes.item( x );
			if( node.getNodeName().equalsIgnoreCase( tagName ) )
			{
				NodeList childNodes = node.getChildNodes();
				for( int y = 0; y < childNodes.getLength(); y++ )
				{
					Node data = childNodes.item( y );
					if( data.getNodeType() == Node.ATTRIBUTE_NODE )
					{
						if( data.getNodeName().equalsIgnoreCase( attrName ) )
							return data.getNodeValue();
					}
				}
			}
		}

		return "";
	}
}