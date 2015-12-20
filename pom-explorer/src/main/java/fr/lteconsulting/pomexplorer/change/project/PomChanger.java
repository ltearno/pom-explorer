package fr.lteconsulting.pomexplorer.change.project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import com.ximpleware.AutoPilot;
import com.ximpleware.ModifyException;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XMLModifier;

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

public class PomChanger
{
	private VTDGen vg;
	private XMLModifier modifier;

	public PomChanger()
	{
		vg = new VTDGen();
		modifier = new XMLModifier();
	}

	private static abstract class Tree
	{
		public abstract String getXmlChunk();
	}

	private static class Node extends Tree
	{
		private final String name;
		private final Tree child;

		public Node( String name, Tree child )
		{
			this.name = name;
			this.child = child;
		}

		@Override
		public String getXmlChunk()
		{
			return "<" + name + ">" + child + "</" + name + ">";
		}

		public String getName()
		{
			return name;
		}

		public Tree getChild()
		{
			return child;
		}
	}

	private static class DependencyNode extends Tree
	{
		private final DependencyKey key;
		private final Node child;

		public DependencyNode( DependencyKey key, Node child )
		{
			this.key = key;
			this.child = child;
		}

		@Override
		public String getXmlChunk()
		{
			StringBuilder sb = new StringBuilder();

			sb.append( "<dependency>" );
			if( "groupId".equals( child.getName() ) )
				sb.append( child.getXmlChunk() );
			else
				sb.append( "<groupId>" + key.getGroupId() + "</groupId>" );

			if( "artifactId".equals( child.getName() ) )
				sb.append( child.getXmlChunk() );
			else
				sb.append( "<artifactId>" + key.getArtifactId() + "</artifactId>" );

			if( "classifier".equals( child.getName() ) )
				sb.append( child.getXmlChunk() );
			else if( key.getClassifier() != null && !key.getClassifier().isEmpty() )
				sb.append( "<classifier>" + key.getClassifier() + "</classifier>" );

			if( "type".equals( child.getName() ) )
				sb.append( child.getXmlChunk() );
			else if( key.getType() != null && !key.getType().isEmpty() )
				sb.append( "<type>" + key.getType() + "</type>" );

			if( "version".equals( child.getName() ) )
				sb.append( child.getXmlChunk() );

			if( "scope".equals( child.getName() ) )
				sb.append( child.getXmlChunk() );

			sb.append( "</dependency>" );
			return sb.toString();
		}

		public DependencyKey getKey()
		{
			return key;
		}

		public Node getChild()
		{
			return child;
		}
	}

	private static class Value extends Tree
	{
		private final String value;

		public Value( String value )
		{
			this.value = value;
		}

		@Override
		public String getXmlChunk()
		{
			return value;
		}

		public String getValue()
		{
			return value;
		}
	}

	public void applyChanges( Session session, Set<ProjectChange> changes, Log log )
	{
		for( ProjectChange change : changes )
		{
			try
			{
				Project project = change.getProject();
				if( project == null )
					continue;

				String pomPath = project.getPomFile().getAbsolutePath();

				boolean parsed = vg.parseFile( pomPath, false );
				if( !parsed )
					throw new RuntimeException( "Unable to parse " + pomPath );

				Tree tree = change.getLocation().visit( new Location.Visitor<Tree>()
				{
					@Override
					public Tree visit( Parent parent )
					{
						return new Node( "project", new Node( "parent", new Node( change.getNodeName(), new Value( change.getNewValue() ) ) ) );
					}

					@Override
					public Tree visit( fr.lteconsulting.pomexplorer.change.project.Location.Project project )
					{
						return new Node( "project", new Node( change.getNodeName(), new Value( change.getNewValue() ) ) );
					}

					@Override
					public Tree visit( Property property )
					{
						return new Node( "project", new Node( "properties", new Node( change.getNodeName(), new Value( change.getNewValue() ) ) ) );
					}

					@Override
					public Tree visit( Dependency dependency )
					{
						return new Node( "project",
								new Node( "dependencies",
										new DependencyNode( dependency.getKey(),
												new Node( change.getNodeName(),
														new Value( change.getNewValue() ) ) ) ) );
					}

					@Override
					public Tree visit( DependencyManagement dependencyManagement )
					{
						return new Node( "project",
								new Node( "dependencyManagement",
										new Node( "dependencies",
												new DependencyNode( dependencyManagement.getKey(),
														new Node( change.getNodeName(),
																new Value( change.getNewValue() ) ) ) ) ) );
					}

					@Override
					public Tree visit( Plugin plugin )
					{
						throw new IllegalStateException( "nyi" );
					}

					@Override
					public Tree visit( PluginManagement pluginManagement )
					{
						throw new IllegalStateException( "nyi" );
					}
				} );

				VTDNav vn = vg.getNav();
				modifier.bind( vn );

				applyChanges( tree, vn );

				System.out.println( new String( vn.getXML().getBytes() ) );

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				modifier.output( bos );
				System.out.println( new String( bos.toByteArray() ) );

				FileOutputStream fos = new FileOutputStream( new File( pomPath ) );
				modifier.output( fos );
				fos.close();
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}

	private void applyChanges( Tree tree, VTDNav nav ) throws ModifyException, UnsupportedEncodingException
	{
		if( tree instanceof Value )
			applyChanges( (Value) tree, nav );
		else if( tree instanceof Node )
			applyChanges( (Node) tree, nav );
		else if( tree instanceof DependencyNode )
			applyChanges( (DependencyNode) tree, nav );
	}

	private void applyChanges( Value value, VTDNav nav ) throws ModifyException, UnsupportedEncodingException
	{
		modifier.updateToken( nav.getText(), value.getValue() );
	}

	private void applyChanges( Node node, VTDNav nav )
	{
		if( node == null )
			return;

		try
		{
			nav.push();

			AutoPilot ap = new AutoPilot( nav );
			ap.selectElement( node.getName() );
			if( ap.iterate() )
			{
				applyChanges( node.getChild(), nav );
			}
			else
			{
				nav.pop();
				nav.push();

				modifier.insertAfterHead( node.getXmlChunk() );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			nav.pop();
		}
	}

	private static class GactCollect
	{
		String groupId;
		String artifactId;
		String classifier;
		String type;

		void update( VTDNav nav )
		{
			try
			{
				String nodeName = nav.toNormalizedString( nav.getCurrentIndex() );
				String value = nav.toNormalizedString( nav.getText() );

				switch( nodeName )
				{
					case "groupId":
						groupId = value;
						break;
					case "artifactId":
						artifactId = value;
						break;
					case "classifier":
						classifier = value;
						break;
					case "type":
						type = value;
						break;
				}
			}
			catch( NavException e )
			{
				e.printStackTrace();
			}
		}

		boolean match( DependencyKey key )
		{
			if( !stringsEqual( key.getGroupId(), groupId ) )
				return false;
			if( !stringsEqual( key.getArtifactId(), artifactId ) )
				return false;
			if( !stringsEqual( key.getClassifier(), classifier ) )
				return false;
			if( !stringsEqual( key.getType(), type, "jar" ) )
				return false;
			return true;
		}

		public static boolean stringsEqual( String a, String b )
		{
			return stringsEqual( a, b, null );
		}

		public static boolean stringsEqual( String a, String b, String def )
		{
			if( a != null && (a.trim().isEmpty() || a.equals( def )) )
				a = null;
			if( b != null && (b.trim().isEmpty() || b.equals( def )) )
				b = null;

			if( a == b )
				return true;
			if( a == null || b == null )
				return false;

			return a.equals( b );
		}
	}

	private void applyChanges( DependencyNode node, VTDNav nav )
	{
		if( node == null )
			return;

		try
		{
			nav.push();

			// trouver un noeud "dependency"
			// avec les sous noeud gact correspondant à la key

			AutoPilot ap = new AutoPilot( nav );
			ap.selectElement( "dependency" );
			while( ap.iterate() )
			{
				nav.push();

				// passer en revue tous les sous noeuds et voir si ils matchent
				GactCollect collect = new GactCollect();
				int dir = VTDNav.FIRST_CHILD;
				while( nav.toElement( dir ) )
				{
					dir = VTDNav.NEXT_SIBLING;

					collect.update( nav );
				}

				nav.pop();

				if( collect.match( node.getKey() ) )
				{
					applyChanges( node.getChild(), nav );
					return;
				}
			}

			{
				nav.pop();
				nav.push();

				modifier.insertAfterHead( node.getXmlChunk() );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			nav.pop();
		}
	}
}