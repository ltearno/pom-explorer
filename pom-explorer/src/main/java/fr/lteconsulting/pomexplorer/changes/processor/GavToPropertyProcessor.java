package fr.lteconsulting.pomexplorer.changes.processor;

import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.PomSection;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.changes.GavChange;
import fr.lteconsulting.pomexplorer.changes.IChangeSet;
import fr.lteconsulting.pomexplorer.changes.PropertyChange;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;
import fr.lteconsulting.pomexplorer.depanalyze.PropertyLocation;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.model.Gav;

/**
 * Sometimes a GavChange consist in fact to change property values. This
 * processor manages those cases
 */
public class GavToPropertyProcessor extends AbstractGavChangeProcessor
{
	@Override
	protected void processChange( WorkingSession session, ILogger log, GavChange change, IChangeSet changeSet )
	{
		GavLocation depLoc = change.getLocation();

		if( depLoc.getUnresolvedGav() == null )
			return;

		if( Tools.isMavenVariable( depLoc.getUnresolvedGav().getVersion() ) )
		{
			changeSet.invalidateChange( change );

			String property = Tools.getPropertyNameFromPropertyReference( depLoc.getUnresolvedGav().getVersion() );

			if( "project.version".equals( property ) )
			{
				Gav projectGav = depLoc.getProject().getGav();
				GavLocation projectLoc = new GavLocation( depLoc.getProject(), PomSection.PROJECT, projectGav );
				changeSet.addChange( new GavChange( projectLoc, new Gav( projectGav.getGroupId(), projectGav.getArtifactId(), change.getNewGav().getVersion() ) ), change );
				return;
			}

			if( "project.parent.version".equals( property ) )
			{
				PomGraphReadTransaction tx = session.graph().read();

				Gav parentProjectGav = tx.parent( depLoc.getProject().getGav() );
				Project parentProject = session.projects().forGav( parentProjectGav );
				if( parentProject == null )
					return;

				GavLocation projectLoc = new GavLocation( depLoc.getProject(), PomSection.PARENT, parentProjectGav );
				changeSet.addChange( new GavChange( projectLoc, new Gav( parentProjectGav.getGroupId(), parentProjectGav.getArtifactId(), change.getNewGav().getVersion() ) ), change );
				return;
			}

			log.html( Tools.warningMessage( "updating the '" + property + "' property" ) );

			Project definitionProject = depLoc.getProject().getPropertyDefinitionProject( session, property );
			if( definitionProject != null )
			{
				changeSet.addChange( new PropertyChange( new PropertyLocation( definitionProject, depLoc, property, definitionProject.getMavenProject().getProperties().getProperty( property ) ), change.getNewGav().getVersion() ), change );
			}
		}

	}
}
