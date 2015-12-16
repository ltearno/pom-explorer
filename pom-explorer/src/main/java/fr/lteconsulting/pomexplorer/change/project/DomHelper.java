package fr.lteconsulting.pomexplorer.change.project;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DomHelper
{
	protected static String getSubNodeValue( String subNodeName, Node node )
	{
		Node subNode = getNode( subNodeName, node.getChildNodes() );
		if( subNode == null )
			return null;
		return DomHelper.getNodeValue( subNode );
	}

	protected static void setSubNodeValue( Document document, Node node, String subNodeName, String value )
	{
		Node subNode = getNode( subNodeName, node.getChildNodes() );
		if( subNode == null )
		{
			subNode = document.createElement( subNodeName );
			node.appendChild( subNode );
		}
		subNode.setTextContent( value );
	}

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