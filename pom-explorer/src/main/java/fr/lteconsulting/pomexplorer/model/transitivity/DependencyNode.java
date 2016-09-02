package fr.lteconsulting.pomexplorer.model.transitivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.lteconsulting.hexa.client.tools.Action1;
import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Profile;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.model.DependencyKey;
import fr.lteconsulting.pomexplorer.model.GroupArtifact;
import fr.lteconsulting.pomexplorer.model.VersionScope;

public class DependencyNode
{
	private final Project project;
	private final DependencyKey gact;
	private final VersionScope vs;

	private Map<DependencyKey, DependencyManagement> dependencyManagement;
	private Set<GroupArtifact> exclusions;

	private DependencyNode parent;
	private List<DependencyNode> children;

	public DependencyNode( Project project, DependencyKey gact, VersionScope vs )
	{
		this.project = project;
		this.gact = gact;
		this.vs = vs;
	}

	public void collectDependencyManagement( boolean online, Map<String, Profile> profiles, Log log )
	{
		dependencyManagement = project.getLocalDependencyManagement( null, online, profiles, log );
	}

	public DependencyKey getKey()
	{
		return gact;
	}

	public VersionScope getVs()
	{
		return vs;
	}

	public Project getProject()
	{
		return project;
	}

	public void addExclusions( Set<GroupArtifact> exclusions )
	{
		if( exclusions == null )
			return;

		if( this.exclusions == null )
			this.exclusions = new HashSet<>( exclusions );
		else
			this.exclusions.addAll( exclusions );
	}

	public boolean isRoot()
	{
		return parent == null;
	}

	public DependencyNode getRootNode()
	{
		DependencyNode node = this;
		while( node.parent != null )
			node = node.parent;
		return node;
	}

	public int getLevel()
	{
		int level = 0;
		DependencyNode current = parent;
		while( current != null )
		{
			level++;
			current = current.parent;
		}
		return level;
	}

	public DependencyNode getParent()
	{
		return parent;
	}

	Map<GroupArtifact, DependencyNode> cachedNodesByGA;

	public void addChild( DependencyNode child )
	{
		child.parent = this;

		if( children == null )
			children = new ArrayList<>();
		children.add( child );

		// signaler au parent qu'on est la
		DependencyNode root = getRootNode();
		if( root.cachedNodesByGA == null )
			root.cachedNodesByGA = new HashMap<>();
		root.cachedNodesByGA.put( new GroupArtifact( gact.getGroupId(), gact.getArtifactId() ), child );
	}

	public void removeFromParent()
	{
		if( parent != null )
		{
			parent.children.remove( this );
			parent = null;
		}
	}

	public List<DependencyNode> getChildren()
	{
		return children;
	}

	public void visitDepth( Action1<DependencyNode> visitor )
	{
		if( children == null )
			return;

		for( DependencyNode child : children )
		{
			visitor.exec( child );
			child.visitDepth( visitor );
		}
	}

	public void print()
	{
		print( 0 );
	}

	public DependencyManagement getLocalManagement( DependencyKey key )
	{
		if( dependencyManagement == null )
			return null;
		return dependencyManagement.get( key );
	}

	public DependencyManagement getTopLevelManagement( DependencyKey key )
	{
		DependencyManagement res = null;
		if( parent != null )
			res = parent.getTopLevelManagement( key );

		if( res == null && dependencyManagement != null )
			return dependencyManagement.get( key );

		return res;
	}

	public DependencyNode searchNodeForGroupArtifact( GroupArtifact ga )
	{
		DependencyNode root = getRootNode();

		if( root.cachedNodesByGA == null )
			return null;

		DependencyNode res = root.cachedNodesByGA.get( ga );
		if( res != null && res.parent == null )
			return null; // the node has been detached since then
		return res;
	}

	public boolean isExcluded( GroupArtifact ga )
	{
		if( exclusions == null )
			return false;
		if( exclusions.contains( ga ) )
			return true;
		if( exclusions.contains( new GroupArtifact( "*", "*" ) ) )
			return true;
		if( exclusions.contains( new GroupArtifact( ga.getGroupId(), "*" ) ) )
			return true;
		if( exclusions.contains( new GroupArtifact( "*", ga.getArtifactId() ) ) )
			return true;
		return false;
	}

	void print( int level )
	{
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < level; i++ )
			sb.append( "   " );
		sb.append( gact + " " + vs );
		System.out.println( sb.toString() );
		if( children != null )
		{
			for( DependencyNode child : children )
				child.print( level + 1 );
		}
	}
}