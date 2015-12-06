package fr.lteconsulting.pomexplorer.oldchanges;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.PomSection;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;
import fr.lteconsulting.pomexplorer.depanalyze.Location;
import fr.lteconsulting.pomexplorer.model.Gav;

@SuppressWarnings( "deprecation" )
public class Changer
{
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

	XPath xPath = XPathFactory.newInstance().newXPath();

	public void doChanges( Session session, ChangeSetManager changes, Log log )
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

			if( builder == null )
				throw new RuntimeException( "Unable to create teh document builder !" );

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

			if( change instanceof PropertyChange )
			{
				PropertyChange pchange = (PropertyChange) change;
				if( "project.version".equals( pchange.getLocation().getPropertyName() ) )
				{
					GavLocation loc = new GavLocation( project, PomSection.PROJECT, project.getGav() );
					Gav newGav = new Gav( loc.getGav().getGroupId(), loc.getGav().getArtifactId(), pchange.getNewValue() );
					doChange( document, new GavChange( loc, newGav ), log );
				}
				else
				{
					doChange( document, (PropertyChange) change, log );
				}
			}
			else if( change instanceof GavChange )
			{
				doChange( document, (GavChange) change, log );
			}

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

	private void doChange( Document document, GavChange change, Log log )
	{
		log.html( "[" + change.getLocation().getSection() + "] " );

		switch( change.getLocation().getSection() )
		{
			case DEPENDENCY:
				replaceDependency( document, change, log );
				break;

			case DEPENDENCY_MNGT:
				replaceDependencyManagement( document, change, log );
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

	private void replaceProject( Document document, GavChange change, Log log )
	{
		Gav replacedGav = change.getLocation().getGav();
		String expression = "/project";

		replaceDependency( document, change, log, replacedGav, expression );
	}

	private void replaceParent( Document document, GavChange change, Log log )
	{
		Gav replacedGav = change.getLocation().getGav();
		String expression = "/project/parent";

		replaceDependency( document, change, log, replacedGav, expression );
	}

	private void replacePlugin( Document document, GavChange change, Log log )
	{
		Gav replacedGav = change.getLocation().getGav();
		String expression = "/project/build/plugins/plugin";

		replaceDependency( document, change, log, replacedGav, expression );
	}

	private void replaceDependency( Document document, GavChange change, Log log )
	{
		Gav replacedGav = change.getLocation().getGav();
		String expression = "/project/dependencies/dependency";

		replaceDependency( document, change, log, replacedGav, expression );
	}

	private void replaceDependencyManagement( Document document, GavChange change, Log log )
	{
		Gav replacedGav = change.getLocation().getGav();
		String expression = "/project/dependencyManagement/dependencies/dependency";

		replaceDependency( document, change, log, replacedGav, expression );
	}

	private void replaceDependency( Document document, GavChange change, Log log, Gav replacedGav, String expression )
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
			Gav gav = getGavFromDependencyNode( depNode );

			if( (gav.getGroupId() == null || replacedGav.getGroupId().equals( gav.getGroupId() )) && replacedGav.getArtifactId().equals( gav.getArtifactId() ) )
			{
				// seems a good take !

				if( !replacedGav.getVersion().equals( gav.getVersion() ) )
				{
					log.html( "<span style='color:orange;'>Found a dependency with the wrong version in the pom file : " + gav + " instead of " + replacedGav + "</span><br/>" );
					continue;
				}

				// replace the version value
				setGavInDependencyNode( change.getNewGav(), depNode );
				log.html( "'" + gav + "' updated to '" + change.getNewGav() + "' in '"
						+ (change.getLocation().getProject() != null ? change.getLocation().getProject().getGav() : "-") + "'<br/>" );
			}
		}
	}

	private void doChange( Document document, PropertyChange change, Log log )
	{
		String property = change.getLocation().getPropertyName();

		NodeList nodeList = null;
		try
		{
			Node propertiesNode = (Node) xPath.compile( "/project/properties" ).evaluate( document, XPathConstants.NODE );
			if( propertiesNode != null )
				nodeList = propertiesNode.getChildNodes();
		}
		catch( XPathExpressionException e )
		{
			e.printStackTrace();
			return;
		}

		if( nodeList == null )
			return;

		boolean found = false;

		for( int i = 0; i < nodeList.getLength(); i++ )
		{
			Node node = nodeList.item( i );
			if( !property.equals( node.getNodeName() ) )
				continue;

			found = true;
			node.setTextContent( change.getNewValue() );
			log.html( "'" + property + "' property updated to '" + change.getNewValue() + "' in '" + change.getLocation().getProject().getGav() + "'<br/>" );
		}

		if( !found )
			log.html( "<span style='color:orange;'>did not find property " + property + " definition in project " + change.getLocation().getProject().getGav() + "</span>" );
	}

	private Gav getGavFromDependencyNode( Node depNode )
	{
		return new Gav( getSubNodeValue( "groupId", depNode ), getSubNodeValue( "artifactId", depNode ), getSubNodeValue( "version", depNode ) );
	}

	private String getSubNodeValue( String subNodeName, Node node )
	{
		Node subNode = DomHelper.getNode( subNodeName, node.getChildNodes() );
		if( subNode == null )
			return null;
		return DomHelper.getNodeValue( subNode );
	}

	private void setGavInDependencyNode( Gav newGav, Node depNode )
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