package fr.lteconsulting.pomexplorer.commands;

import java.util.Set;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.PomSection;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.changes.Change;
import fr.lteconsulting.pomexplorer.changes.ChangeSetManager;
import fr.lteconsulting.pomexplorer.changes.GavChange;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;
import fr.lteconsulting.pomexplorer.depanalyze.Location;
import fr.lteconsulting.pomexplorer.graph.relation.GAVRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;

public class ReleaseCommand
{
	@Help( "releases a gav, all dependencies are also released. GAVs depending on released GAVs are updated." )
	public void gav( ILogger log, CommandOptions options, final Client client, WorkingSession session, GAV gav )
	{
		log.html( "<b>Releasing</b> project " + gav + "<br/>" );
		log.html( "All dependencies will be updated to a release version.<br/><br/>" );

		ChangeSetManager changes = new ChangeSetManager();

		releaseGav( client, session, gav, changes, log );

		changes.resolveChanges( session, log );

		Tools.printChangeList( log, changes );

		CommandTools.maybeApplyChanges( session, options, log, changes );
	}

	@Help( "releases all gavs, all dependencies are also released. GAVs depending on released GAVs are updated." )
	public void allGavs( final ILogger log, CommandOptions options, Client client, WorkingSession session )
	{
		ChangeSetManager changes = new ChangeSetManager();

		for( GAV gav : session.graph().gavs() )
		{
			if( gav.getVersion() == null )
			{
				log.html( Tools.warningMessage( "no target version (" + gav + ") !" ) );
				continue;
			}

			if( Tools.isReleased( gav ) )
				continue;

			releaseGav( client, session, gav, changes, log );

		}

		changes.resolveChanges( session, log );

		Tools.printChangeList( log, changes );

		CommandTools.maybeApplyChanges( session, options, log, changes );
	}

	private void releaseGav( Client client, WorkingSession session, GAV gav, ChangeSetManager changes, ILogger log )
	{
		String causeMessage = "release of " + gav;

		if( !Tools.isReleased( gav ) )
		{
			GavLocation loc = new GavLocation( session.projects().forGav( gav ), PomSection.PROJECT, gav );
			changes.addChange( new GavChange( loc, Tools.releasedGav( loc.getGav() ) ), causeMessage );
		}

		Set<GAVRelation<Relation>> relations = session.graph().relationsRec( gav );
		for( GAVRelation<Relation> r : relations )
		{
			if( r.getTarget().getVersion() == null )
			{
				log.html( "<span style='color:orange;'>No target version (" + r.getTarget() + ") !</span><br/>" );
				continue;
			}

			if( Tools.isReleased( r.getTarget() ) )
				continue;

			GAV source = r.getSource();
			GAV to = Tools.releasedGav( r.getTarget() );

			Project project = session.projects().forGav( source );
			if( project == null )
			{
				log.html( Tools.warningMessage( "Project not found for this GAV ! " + source ) );
				continue;
			}

			GavLocation targetLoc = new GavLocation( session.projects().forGav( r.getTarget() ), PomSection.PROJECT, r.getTarget() );
			changes.addChange( new GavChange( targetLoc, Tools.releasedGav( targetLoc.getGav() ) ), causeMessage );

			Location dependencyLocation = Tools.findDependencyLocation( session, log, project, r );
			if( dependencyLocation == null )
			{
				log.html( Tools.errorMessage( "Cannot find the location of dependency to " + r.getTarget() + " in this project " + project ) );
				continue;
			}

			changes.addChange( Change.create( dependencyLocation, to ), causeMessage );
		}
	}
}
