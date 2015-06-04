package fr.lteconsulting.pomexplorer.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.lteconsulting.hexa.client.tools.Func;
import fr.lteconsulting.pomexplorer.AppFactory;
import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.depanalyze.Location;
import fr.lteconsulting.pomexplorer.depanalyze.PropertyLocation;
import fr.lteconsulting.pomexplorer.graph.relation.GAVRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

public class ReleaseCommand
{
	interface Visitor
	{
		void project( VisitKind kind, Project project );

		void projectNotFound( GAV gav, String message );
	}

	public enum VisitKind
	{
		ROOT,
		HIERARCHYCAL,
		TRANSITIVE;
	}

	interface GavValidator
	{
		/**
		 * Returns the GAV the project should be moved to.
		 * 
		 * Return the given gav if no transformation is desired.
		 */
		GAV onGav( GAV gav );
	}

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

	static class Change
	{
		private final Location location;
		private final String oldValue;
		private final String newValue;

		public Change( Location location, String oldValue, String newValue )
		{
			this.location = location;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		public Location getLocation()
		{
			return location;
		}

		public String getOldValue()
		{
			return oldValue;
		}

		public String getNewValue()
		{
			return newValue;
		}
	}

	private final static String SNAPSHOT_SUFFIX = "-SNAPSHOT";

	private GAV onGav( GAV gav )
	{
		if( !isOK( gav ) )
			return new GAV( gav.getGroupId(), gav.getArtifactId(), gav.getVersion().substring( 0, gav.getVersion().length() - SNAPSHOT_SUFFIX.length() ) );

		return gav;
	}

	private boolean isOK( GAV gav )
	{
		return !gav.getVersion().endsWith( SNAPSHOT_SUFFIX );
	}

	@Help( "releases a gav. All dependencies are also released" )
	public String gav( final Client client, WorkingSession session, String gavString )
	{
		GAV gav = Tools.string2Gav( gavString );
		if( gav == null )
			return "specify the GAV with the group:artifact:version format please";

		final StringBuilder res = new StringBuilder();

		res.append( "<b>Releasing</b> project " + gav + "<br/>" );
		res.append( "All dependencies will be updated to a release version.<br/><br/>" );
		
		List<Change> changes = new ArrayList<>();

		changes.add( new Change( new PropertyLocation( session.projects().get( gav ), null, "project.version", gav.getVersion() ), gav.getVersion(), onGav(gav).getVersion() ) );

		Set<GAVRelation<Relation>> relations = session.graph().relationsRec( gav );
		for( GAVRelation<Relation> r : relations )
		{
			if( isOK( r.getTarget() ) )
				continue;

			GAV source = r.getSource();
			GAV from = r.getTarget();
			GAV to = onGav( r.getTarget() );

			res.append( from + " should be changed to " + to + " in gav " + source + "<br/>" );

			Project project = session.projects().get( source );
			if( project == null )
			{
				res.append( "<span style='color:orange;'>Project not found for this GAV ! " + source + "</span><br/>" );
				continue;
			}

			Location dependencyLocation = Tools.findDependencyLocation( session, project, r );
			if( dependencyLocation == null )
			{
				res.append( "<span style='color:red;'>Cannot find the location of dependency to " + r.getTarget() + " in this project " + project + "</span><br/>" );
				continue;
			}

			changes.add( new Change( dependencyLocation, from.getVersion(), to.getVersion() ) );
		}
		
		for(Change c : changes)
		{
			res.append( "file: " + c.getLocation().getProject().getPomFile().getAbsolutePath() + "<br/>" + "location: " + c.getLocation() + "<br/>" + "change to: " + c.getNewValue() + "<br>" + "<br/>" );			
		}

		return res.toString();
	}
}
