package fr.lteconsulting.pomexplorer.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.BuildDependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.ParentRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.graph.relation.RelationType;
import fr.lteconsulting.pomexplorer.graph.relation.RelationVisitor;
import fr.lteconsulting.pomexplorer.graph.relation.Scope;
import fr.lteconsulting.pomexplorer.model.Dependency;
import fr.lteconsulting.pomexplorer.model.Gav;

public class DependsCommand
{
	@Help( "lists the GAVs directly depending on the one given in parameter" )
	public void on( WorkingSession session, ILogger log, Gav gav )
	{
		PomGraphReadTransaction tx = session.graph().read();

		if( !tx.gavs().contains( gav ) )
		{
			log.html( Tools.warningMessage( "no GAV " + gav + " registered in the GAV graph, maybe you made a typo ?" ) );
			return;
		}

		StringBuilder sb = new StringBuilder();

		Set<Relation> relations = tx.relationsReverse( gav );

		sb.append( "<br/><b>Directly depending on " + gav + "</b>, " + relations.size() + " GAVs :<br/>" );
		sb.append( "([" + RelationType.DEPENDENCY.shortName() + "]=direct dependency, [" + RelationType.PARENT.shortName()
				+ "]=parent's dependency, [" + RelationType.BUILD_DEPENDENCY.shortName() + "]=build dependency)<br/><br/>" );

		Set<Gav> indirectDependents = new HashSet<>();

		relations.stream().sorted( ( a, b ) -> tx.sourceOf( a ).toString().compareTo( tx.sourceOf( b ).toString() ) )
				.forEach( relation ->
				{
					Gav source = tx.sourceOf( relation );

					RelationType type = relation.getRelationType();

					sb.append( "[" + type.shortName() + "] " + source + " " );

					fillTextForDependency( sb, relation );

					sb.append( "<br/>" );

					Set<Relation> indirectRelations = tx.relationsReverseRec( source );
					for( Relation ir : indirectRelations )
						indirectDependents.add( tx.sourceOf( ir ) );
				} );

		sb.append( "<br/><b>Indirectly depending on " + gav + "</b>, " + indirectDependents.size() + " GAVs :<br/>" );
		for( Gav d : indirectDependents )
			sb.append( d + "<br/>" );

		log.html( sb.toString() );
	}

	private void showDirectDependencies( StringBuilder sb, PomGraphReadTransaction tx, Gav gav )
	{
		// show direct relations
		Set<Relation> relations = tx.relations( gav );
		if( relations.isEmpty() )
		{
			sb.append( "no dependencies.<br/>" );
		}
		else
		{
			sb.append( "<br/><b>" + gav + "</b> declares " + relations.size() + " dependencies:<br/>" );
			sb.append( "<i>[" + RelationType.DEPENDENCY.shortName() + "]=dependency, [" + RelationType.PARENT.shortName()
					+ "]=parent, [" + RelationType.BUILD_DEPENDENCY.shortName() + "]=plugin dependency</i><br/>" );
			relations.stream()
					.sorted( ( a, b ) -> tx.targetOf( a ).toString().compareTo( tx.targetOf( b ).toString() ) )
					.forEach( relation ->
					{
						sb.append( "[" + relation.getRelationType().shortName() + "] " );
						sb.append( tx.targetOf( relation ) );
						fillTextForDependency( sb, relation );
						sb.append( "<br/>" );
					} );
		}
	}

	private void showParenChain( StringBuilder sb, PomGraphReadTransaction tx, Gav gav )
	{
		sb.append( "parent chain: " );
		boolean first = true;
		Gav parent = tx.parent( gav );
		if( parent == null )
			sb.append( "-" );

		while( parent != null )
		{
			if( !first )
				sb.append( " -> " );

			first = false;
			sb.append( parent );

			parent = tx.parent( parent );
		}
	}

	@Help( "lists the GAVs that the GAV passed in parameters depends on. -all option to show ALL transitive dependencies (on build and parent gavs)" )
	public void by( WorkingSession session, ILogger log, CommandOptions options, Gav gav )
	{
		PomGraphReadTransaction tx = session.graph().read();

		if( !tx.gavs().contains( gav ) )
		{
			log.html( Tools.warningMessage( "no GAV " + gav + " registered in the GAV graph, maybe you made a typo ?" ) );
			return;
		}

		StringBuilder sb = new StringBuilder();

		sb.append( "<b>" + gav + "'s dependencies:</b><br/>" );

		showParenChain( sb, tx, gav );
		sb.append( "<br/>" );
		showDirectDependencies( sb, tx, gav );
		sb.append( "<br/>" );
		showTransitiveDependencies( sb, tx, gav );
		sb.append( "<br/>" );
		showAllDependencies( gav, tx, sb, options.hasFlag( "all" ) );

		log.html( sb.toString() );
	}

	private void showTransitiveDependencies( StringBuilder sb, PomGraphReadTransaction tx, Gav gav )
	{
		sb.append( "<b>" + gav + "</b> transitive dependencies:<br/>" );

		Map<Gact, List<DepInfo>> data = new HashMap<>();

		for( DependencyRelation dependency : tx.dependencies( gav ) )
			addDependency( new HashSet<>(), 0, dependency.getDependency().getScope(), data, dependency, sb, tx );

		Set<Dependency> compileDependencies = new HashSet<>();
		Set<Dependency> testDependencies = new HashSet<>();
		Set<Dependency> runtimeDependencies = new HashSet<>();
		Set<Dependency> providedDependencies = new HashSet<>();
		Set<Dependency> systemDependencies = new HashSet<>();

		data.entrySet().stream().sorted( ( e, f ) -> {
			return e.getKey().compareTo( f.getKey() );
		} ).forEach( e -> {
			Gact gact = e.getKey();
			List<DepInfo> dependencies = e.getValue();

			DepInfo winner = dependencies.stream().min( ( a, b ) -> Integer.compare( a.level, b.level ) ).get();
			Dependency d = new Dependency( new Gav( gact.groupId, gact.artifactId, winner.version ), winner.scope, gact.classifier, gact.type );

			switch( winner.scope )
			{
				case COMPILE:
					compileDependencies.add( d );
					break;
				case RUNTIME:
					runtimeDependencies.add( d );
					break;
				case PROVIDED:
					providedDependencies.add( d );
					break;
				case TEST:
					testDependencies.add( d );
					break;
				case SYSTEM:
					systemDependencies.add( d );
					break;
				default:
					break;
			}
		} );

		maybeShowDeps( "compile scoped", compileDependencies, sb );
		maybeShowDeps( "runtime scoped", runtimeDependencies, sb );
		maybeShowDeps( "provided scoped", providedDependencies, sb );
		maybeShowDeps( "system scoped", systemDependencies, sb );
		maybeShowDeps( "test scoped", testDependencies, sb );
	}

	private void maybeShowDeps( String title, Set<Dependency> deps, StringBuilder sb )
	{
		if( deps.isEmpty() )
		{
			sb.append( "<b>no " + title + " dependencies.</b><br/>" );
		}
		else
		{
			sb.append( "<b>" + title + "</b> dependencies:<br/>" );
			deps.stream().sorted( Tools.dependencyAlphabeticalComparator ).forEach( d -> sb.append( d + "<br/>" ) );
		}

	}

	private void addDependency( Set<DependencyRelation> visitedRelations, int level, Scope effectiveScope, Map<Gact, List<DepInfo>> data, DependencyRelation dependency, StringBuilder sb, PomGraphReadTransaction tx )
	{
		if( visitedRelations.contains( dependency ) )
		{
			sb.append( Tools.warningMessage( "loop detected when resolving transitive dependency to " + dependency ) );
			return;
		}
		visitedRelations.add( dependency );

		// add itself
		Gact key = getGact( dependency );
		List<DepInfo> deps = data.get( key );
		if( deps == null )
		{
			deps = new ArrayList<>();
			data.put( key, deps );
		}
		deps.add( new DepInfo( level, false, dependency.getDependency().getVersion(), effectiveScope ) );

		// TODO should detect loop by having a set of visited dependency

		// maybe add dependencies
		for( DependencyRelation dep : tx.dependencies( tx.targetOf( dependency ) ) )
		{
			Scope effScope = shouldAddTransitiveDependency( effectiveScope, dep.getDependency().getScope() );
			if( effScope != null )
				addDependency( visitedRelations, level + 1, effScope, data, dep, sb, tx );
		}
	}

	private Scope shouldAddTransitiveDependency( Scope source, Scope dep )
	{
		switch( source )
		{
			case COMPILE:
				switch( dep )
				{
					case COMPILE:
						return Scope.COMPILE;
					case PROVIDED:
					case SYSTEM:
						return null;
					case RUNTIME:
						return Scope.RUNTIME;
					case TEST:
						return null;

					default:
						return null;
				}

			case PROVIDED:
			case SYSTEM:
				switch( dep )
				{
					case COMPILE:
						return source;
					case PROVIDED:
					case SYSTEM:
						return null;
					case RUNTIME:
						return source;
					case TEST:
						return null;

					default:
						return null;
				}

			case RUNTIME:
				switch( dep )
				{
					case COMPILE:
						return Scope.RUNTIME;
					case PROVIDED:
					case SYSTEM:
						return null;
					case RUNTIME:
						return Scope.RUNTIME;
					case TEST:
						return null;

					default:
						return null;
				}

			case TEST:
				switch( dep )
				{
					case COMPILE:
						return Scope.TEST;
					case PROVIDED:
					case SYSTEM:
						return null;
					case RUNTIME:
						return Scope.TEST;
					case TEST:
						return null;

					default:
						return null;
				}

			default:
				return null;
		}
	}

	static class DepInfo
	{
		final int level;
		// TODO : take that in account ! scope and version specified in dependency management for their values
		final boolean fromDepMngt;
		final String version;
		final Scope scope;

		public DepInfo( int level, boolean fromDepMngt, String version, Scope scope )
		{
			this.level = level;
			this.fromDepMngt = fromDepMngt;
			this.version = version;
			this.scope = scope;
		}

		@Override
		public String toString()
		{
			return version + ":" + scope + "(level " + level + ")";
		}
	}

	private void showAllDependencies( Gav gav, PomGraphReadTransaction tx, StringBuilder sb, boolean showAll )
	{
		sb.append( "<b>" + gav + "</b> dependencies required for building:<br/>" );
		if( showAll )
		{
			sb.append( "<i>dependencies with multiple versions are written in bold</i><br/>" );

			Map<Gact, Set<String>> data = new HashMap<>();
			tx.relationsRec( gav ).stream()
					.forEach( relation -> {
						Gact key = getGact( tx, relation );
						Set<String> versions = data.get( key );
						if( versions == null )
						{
							versions = new HashSet<>();
							data.put( key, versions );
						}

						versions.add( tx.targetOf( relation ).getVersion() );
					} );
			data.entrySet().stream().sorted( ( e, f ) -> {
				return e.getKey().compareTo( f.getKey() );
			} ).forEach( e -> {
				Gact gact = e.getKey();
				Set<String> versions = e.getValue();
				int nb = versions.size();

				sb.append( (nb > 1 ? "<b>" : "") + gact + (nb > 1 ? "</b>" : "") + ":" );
				List<String> list = new ArrayList<>( versions );
				Collections.sort( list );
				for( int i = 0; i < nb; i++ )
					sb.append( (i > 0 ? ", " : "") + list.get( i ) );
				sb.append( "<br/>" );
			} );
		}
		else
		{
			sb.append( "<i>you can show all dependencies with the `-all` option.</i><br/>" );
		}
	}

	private static class Gact implements Comparable<Gact>
	{
		private final String groupId;
		private final String artifactId;
		private final String classifier;
		private final String type;

		public Gact( String groupId, String artifactId, String classifier, String type )
		{
			this.groupId = groupId;
			this.artifactId = artifactId;
			this.classifier = classifier;
			this.type = type;
		}

		@Override
		public String toString()
		{
			return groupId + ":" + artifactId + (classifier != null ? (":" + classifier) : "") + ":" + type;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
			result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
			result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals( Object obj )
		{
			if( this == obj )
				return true;
			if( obj == null )
				return false;
			if( getClass() != obj.getClass() )
				return false;
			Gact other = (Gact) obj;
			if( artifactId == null )
			{
				if( other.artifactId != null )
					return false;
			}
			else if( !artifactId.equals( other.artifactId ) )
				return false;
			if( classifier == null )
			{
				if( other.classifier != null )
					return false;
			}
			else if( !classifier.equals( other.classifier ) )
				return false;
			if( groupId == null )
			{
				if( other.groupId != null )
					return false;
			}
			else if( !groupId.equals( other.groupId ) )
				return false;
			if( type == null )
			{
				if( other.type != null )
					return false;
			}
			else if( !type.equals( other.type ) )
				return false;
			return true;
		}

		@Override
		public int compareTo( Gact o )
		{
			int res = groupId.compareTo( o.groupId );
			if( res != 0 )
				return res;
			res = Tools.compareStrings( artifactId, o.artifactId );
			if( res != 0 )
				return res;
			res = Tools.compareStrings( classifier, o.classifier );
			if( res != 0 )
				return res;
			res = Tools.compareStrings( type, o.type );
			return res;
		}
	}

	private Gact getGact( PomGraphReadTransaction tx, Relation relation )
	{
		Gav gav = tx.targetOf( relation );
		if( relation instanceof DependencyRelation )
			return getGact( (DependencyRelation) relation );
		else if( relation instanceof BuildDependencyRelation )
			return new Gact( gav.getGroupId(), gav.getArtifactId(), null, null );
		return new Gact( gav.getGroupId(), gav.getArtifactId(), null, "pom" );
	}

	private Gact getGact( DependencyRelation relation )
	{
		Gav gav = relation.getDependency();

		return new Gact( gav.getGroupId(), gav.getArtifactId(), relation.getDependency().getClassifier(), relation.getDependency().getType() );
	}

	private void fillTextForDependency( StringBuilder log, Relation relation )
	{
		relation.visit( new RelationVisitor()
		{
			@Override
			public void onParentRelation( ParentRelation relation )
			{
			}

			@Override
			public void onDependencyRelation( DependencyRelation relation )
			{
				String classifier = relation.getDependency().getClassifier();
				log.append( ":" + relation.getDependency().getScope() + ":" + relation.getDependency().getType()
						+ (classifier != null ? (":" + classifier) : "") );
			}

			@Override
			public void onBuildDependencyRelation( BuildDependencyRelation relation )
			{
			}
		} );

		// TODO make that better

		// PomGraphReadTransaction tx = session.graph().read();
		//
		// Gav source = tx.sourceOf( relation );
		//
		// Project sourceProject = session.projects().forGav( source );
		// if( sourceProject == null )
		// {
		// log.html( Tools.warningMessage( "(no project for this GAV, dependency locations not analyzed)" ) );
		// return;
		// }
		//
		// StringBuilder sb = new StringBuilder();
		//
		// relation.visit( new RelationVisitor()
		// {
		// @Override
		// public void onParentRelation( ParentRelation relation )
		// {
		// }
		//
		// @Override
		// public void onDependencyRelation( DependencyRelation relation )
		// {
		// }
		//
		// @Override
		// public void onBuildDependencyRelation( BuildDependencyRelation relation )
		// {
		// }
		// } );
		//
		// sb.append( "declared at : " );
		// Location location = sourceProject.findDependencyLocation( session, log, relation );
		// if( location != null )
		// sb.append( location.toString() );
		// else
		// sb.append( Tools.warningMessage( "cannot find dependency location from " + sourceProject + " to " + tx.targetOf( relation ) + " (relation of type " +
		// relation.toString() + ")" ) );
		//
		// if( location instanceof GavLocation )
		// {
		// GavLocation gavLocation = (GavLocation) location;
		// if( Tools.isMavenVariable( gavLocation.getUnresolvedGav().getVersion() ) )
		// {
		// String property = Tools.getPropertyNameFromPropertyReference( gavLocation.getUnresolvedGav().getVersion() );
		// Project definitionProject = gavLocation.getProject().getPropertyDefinitionProject( session, property );
		// sb.append( ", property ${" + property + "} defined in project " + definitionProject.getGav() );
		// }
		// }
		//
		// log.html( sb.toString() );
	}
}
