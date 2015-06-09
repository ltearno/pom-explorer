package fr.lteconsulting.pomexplorer.changes.processor;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.PomSection;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.changes.GavChange;
import fr.lteconsulting.pomexplorer.changes.IChangeSet;

/**
 * When one wants to change a project version, maybe the project's version is
 * defined by the project's parent's version. In this case, the change should be
 * mutated to change the parent's version !
 * 
 * @author Arnaud
 *
 */
public class ProjectVersionProcessor extends AbstractGavChangeProcessor
{
	@Override
	public void processChange( WorkingSession session, StringBuilder log, GavChange change, IChangeSet changeSet )
	{
		// change should be a PROJECT GavChange
		if( change.getLocation().getSection() != PomSection.PROJECT )
			return;

		GAV projectGav = change.getLocation().getGav();
		Project project = session.projects().get( projectGav );
		if( project == null )
		{
			if( log != null )
				log.append( Tools.warningMessage( "cannot find project for gav " + projectGav ) );
			return;
		}

		// project version should be null
		if( project.getUnresolvedPom().getModel().getVersion() != null )
			return;

		// and project should have a parent
		GAV parentProjectGav = session.graph().parent( projectGav );
		if( parentProjectGav == null )
			return;

		// in this case:
		
		// TODO should be only done if the version changes. And the GAV change may apply partilally on the current project if it concerns its groupId or artifactId

		// remove the change from the change set
		changeSet.removeChange( change );
		
		// add changing the parent's version in the change set
		GAV parentProjectGavModified = new GAV( parentProjectGav.getGroupId(), parentProjectGav.getArtifactId(), change.getNewGav().getVersion() );
		changeSet.addChange( new GavChange( session.projects().get( parentProjectGav ), PomSection.PROJECT, parentProjectGav, parentProjectGavModified ) );
	}
}
