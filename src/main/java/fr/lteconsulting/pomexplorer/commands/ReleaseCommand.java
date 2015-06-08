package fr.lteconsulting.pomexplorer.commands;

import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.hexa.client.tools.Func;
import fr.lteconsulting.pomexplorer.AppFactory;
import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.PomSection;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.changes.Change;
import fr.lteconsulting.pomexplorer.changes.GavChange;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;
import fr.lteconsulting.pomexplorer.depanalyze.Location;
import fr.lteconsulting.pomexplorer.graph.relation.GAVRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

public class ReleaseCommand
{
	interface Task extends Func<String>
	{
	}

	static class ChangeVersionTask implements Task
	{
		private final GAV gav;
		private final Client client;

		public ChangeVersionTask( GAV gav, Client client )
		{
			this.gav = gav;
			this.client = client;
		}

		@Override
		public String exec()
		{
			return AppFactory.get().commands().takeCommand( client, "de on " + gav.toString() );
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((gav == null) ? 0 : gav.hashCode());
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
			ChangeVersionTask other = (ChangeVersionTask) obj;
			if( gav == null )
			{
				if( other.gav != null )
					return false;
			}
			else if( !gav.equals( other.gav ) )
				return false;
			return true;
		}
	}

	private final static String SNAPSHOT_SUFFIX = "-SNAPSHOT";

	private void releaseGav( Client client, WorkingSession session, GAV gav, Set<Change<? extends Location>> changes, StringBuilder log )
	{
		log.append( "<b>Releasing</b> project " + gav + "<br/>" );
		log.append( "All dependencies will be updated to a release version.<br/><br/>" );

		GavLocation loc = GavLocation.createProjectGavLocation(session, gav, log);
		changes.add(new GavChange(loc, releasedGav(loc.getGav())));

		Set<GAVRelation<Relation>> relations = session.graph().relationsRec( gav );
		for( GAVRelation<Relation> r : relations )
		{
			if( r.getTarget().getVersion() == null )
			{
				log.append( "<span style='color:orange;'>No target version (" + r.getTarget() + ") !</span><br/>" );
				continue;
			}

			if( isReleased( r.getTarget() ) )
				continue;

			GAV source = r.getSource();
			GAV to = releasedGav( r.getTarget() );

			Project project = session.projects().get( source );
			if( project == null )
			{
				log.append( "<span style='color:orange;'>Project not found for this GAV ! " + source + "</span><br/>" );
				continue;
			}

			GavLocation targetLoc = GavLocation.createProjectGavLocation(session, r.getTarget(), log);
			changes.add(new GavChange(targetLoc, releasedGav(targetLoc.getGav())));

			Location dependencyLocation = Tools.findDependencyLocation( session, project, r );
			if( dependencyLocation == null )
			{
				log.append( "<span style='color:red;'>Cannot find the location of dependency to " + r.getTarget() + " in this project " + project + "</span><br/>" );
				continue;
			}

			Change<? extends Location> c = Change.create( dependencyLocation, to );
			changes.add( c );
		}

		Tools.printChangeList( log, changes );

		log.append( "<br/><b>Completing release</b>, by updating projects dependent on those just released<br/><br/>" );

		Set<Change<? extends Location>> newChanges = new HashSet<>();

		for( Change<? extends Location> c : changes )
		{
			if( !(c.getLocation() instanceof GavLocation) )
				continue;

			GavLocation location = (GavLocation) c.getLocation();

			if( location.getSection() == PomSection.PROJECT )
			{
				Tools.changeGav(client, session, location.getGav(), ((GavChange)c).getNewGav(), newChanges,
						new HashSet<GAV>());
			}
		}

		Tools.printChangeList( log, newChanges );

		changes.addAll( newChanges );
	}

	@Help( "releases a gav, all dependencies are also released. GAVs depending on released GAVs are updated." )
	public String gav( CommandOptions options, final Client client, WorkingSession session, String gavString )
	{
		GAV gav = Tools.string2Gav( gavString );
		if( gav == null )
			return "specify the GAV with the group:artifact:version format please";

		final StringBuilder log = new StringBuilder();
		Set<Change<? extends Location>> changes = new HashSet<>();

		releaseGav( client, session, gav, changes, log );

		CommandTools.maybeApplyChanges(session, options, log, changes);

		return log.toString();
	}

	@Help( "releases all gavs, all dependencies are also released. GAVs depending on released GAVs are updated." )
	public String allGavs( CommandOptions options, final Client client, WorkingSession session )
	{
		final StringBuilder log = new StringBuilder();
		Set<Change<? extends Location>> changes = new HashSet<>();

		for( GAV gav : session.graph().gavs() )
		{
			if( gav.getVersion() == null )
			{
				log.append( "<span style='color:orange;'>No target version (" + gav + ") !</span><br/>" );
				continue;
			}

			if( isReleased( gav ) )
				continue;

			releaseGav( client, session, gav, changes, log );
		}

		CommandTools.maybeApplyChanges(session, options, log, changes);

		return log.toString();
	}

	private boolean isReleased( GAV gav )
	{
		return !gav.getVersion().endsWith( SNAPSHOT_SUFFIX );
	}

	private GAV releasedGav( GAV gav )
	{
		if( !isReleased( gav ) )
			return new GAV( gav.getGroupId(), gav.getArtifactId(), gav.getVersion().substring( 0, gav.getVersion().length() - SNAPSHOT_SUFFIX.length() ) );

		return gav;
	}
}
