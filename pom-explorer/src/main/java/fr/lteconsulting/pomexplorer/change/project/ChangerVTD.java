package fr.lteconsulting.pomexplorer.change.project;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import com.ximpleware.AutoPilot;
import com.ximpleware.ModifyException;
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

public class ChangerVTD
{
	VTDGen vg;
	XMLModifier modifier;

	public ChangerVTD()
	{
		vg = new VTDGen();
		modifier = new XMLModifier();
	}

	static abstract class Tree
	{
		public abstract String getXmlChunk();
	}

	static class Node extends Tree
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

	static class Value extends Tree
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

	public void doChanges( Session session, Set<ProjectChange> changes, Log log )
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
						throw new IllegalStateException( "nyi" );
					}

					@Override
					public Tree visit( DependencyManagement dependencyManagement )
					{
						throw new IllegalStateException( "nyi" );
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

				// FileOutputStream fos = new FileOutputStream( new File( pomPath ) );
				// fos.write( vn.getXML().getBytes() );
				// fos.close();
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
}