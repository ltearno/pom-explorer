package fr.lteconsulting.pomexplorer.web.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.lteconsulting.hexa.client.tools.Func;
import fr.lteconsulting.pomexplorer.AppFactory;
import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Dep;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.depanalyze.Location;

public class ReleaseCommand
{
	public String test( Client client, WorkingSession session )
	{
		return gav( client, session, "fr.lteconsulting:hexa.binding:1.2-SNAPSHOT" );
		// return gav( session, "fr.lteconsulting:hexa.css:1.2-SNAPSHOT" );
		// return gav( session, "fr.lteconsulting:hexa.utils:1.2-SNAPSHOT" );
	}

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

	public String gav( final Client client, WorkingSession session, String gavString )
	{
		GAV gav = Tools.string2Gav( gavString );

		final GavValidator validator = new GavValidator()
		{
			@Override
			public GAV onGav( GAV gav )
			{
				if( gav.getVersion().endsWith( "-SNAPSHOT" ) )
					return new GAV( gav.getGroupId(), gav.getArtifactId(), gav.getVersion().substring( 0, gav.getVersion().length() - "-SNAPSHOT".length() ) );

				return gav;
			}
		};

		final StringBuilder res = new StringBuilder();

		res.append( "<b>Releasing</b> project " + gav + "<br/>" );

		final HashSet<Project> projects = new HashSet<>();

		visit( session, gav, new Visitor()
		{
			private boolean isOK( GAV gav )
			{
				return validator.onGav( gav ).equals( gav );
			}

			@Override
			public void projectNotFound( GAV gav, String message )
			{
				if( !isOK( gav ) )
				{
					res.append( "<b style='red'>project should be changed and source files can't be found ! GAV : " + gav + "</b>" );
				}
			}

			@Override
			public void project( VisitKind kind, Project project )
			{
				if( !isOK( project.getGav() ) )
					projects.add( project );
			}
		} );

		res.append( "<br/><b>Summary</b><br/>" );

		List<Change> changes = new ArrayList<>();

		for( Project project : projects )
		{
			String oldValue = project.getGav().toString();
			String newValue = validator.onGav( project.getGav() ).toString();

			res.append( oldValue + " should be changed to " + newValue + "<br/>" );

			Set<Location> locations = Tools.getImpactedLocationsToChangeGav( session, project.getGav(), res, false );
			for( Location l : locations )
				changes.add( new Change( l, oldValue, newValue ) );
		}

		res.append( "<br/><b>Details</b><br/>" );
		for( Change change : changes )
		{
			res.append( "file: " + change.getLocation().getProject().getPomFile().getAbsolutePath() + "<br/>"
					+ "location: " + change.getLocation() + "<br/>"
					+ "change to: " + change.getNewValue() + "<br>"
					+ "<br/>" );
		}

		return res.toString();
	}

	void visit( WorkingSession session, GAV gav, Visitor visitor )
	{
		// find project gav
		Project project = session.getProjects().get( gav );
		if( project == null )
		{
			visitor.projectNotFound( gav, "project not found" );
			return;
		}

		visitor.project( VisitKind.ROOT, project );

		visitChild( session, project, visitor );
	}

	private void visitChild( WorkingSession session, Project project, Visitor visitor )
	{
		// visit hierarchical projects
		GAV parentGAV = Tools.getParentGav( project );
		if( parentGAV == null )
			return;
		Project parentProject = session.getProjects().get( parentGAV );
		if( parentProject == null )
		{
			visitor.projectNotFound( parentGAV, "parent missing for project " + project );
			return;
		}
		visitor.project( VisitKind.HIERARCHYCAL, parentProject );
		visitChild( session, parentProject, visitor );

		// visit transitive projects
		for( Dep dep : session.getGraph().outgoingEdgesOf( project.getGav() ) )
		{
			GAV dependencyGav = session.getGraph().getEdgeTarget( dep );

			Project dependencyProject = session.getProjects().get( dependencyGav );
			if( dependencyProject == null )
			{
				visitor.projectNotFound( dependencyGav, "dependent project missing " + project );
			}
			else
			{
				visitor.project( VisitKind.TRANSITIVE, dependencyProject );
				visitChild( session, dependencyProject, visitor );
			}
		}
	}
}
