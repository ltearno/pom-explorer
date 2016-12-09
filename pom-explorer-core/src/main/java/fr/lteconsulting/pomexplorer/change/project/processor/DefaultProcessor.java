package fr.lteconsulting.pomexplorer.change.project.processor;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.change.ChangeProcessor;
import fr.lteconsulting.pomexplorer.change.ChangeSet;
import fr.lteconsulting.pomexplorer.change.project.ProjectChange;

// - change a project's version when it is defined by the parent's version => change the parent version.
// - same for groupId...
// TODO : if groupId is defined by the parent, change the parent instead
// TODO : if the version is defined by the parent, change the parent version instead
public class DefaultProcessor implements ChangeProcessor<ProjectChange>
{
	@Override
	public void processChange( Session session, Log log, ProjectChange change, ChangeSet<ProjectChange> changeSet )
	{
	}
}