package fr.lteconsulting.pomexplorer.change.project.processor;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.change.ChangeProcessor;
import fr.lteconsulting.pomexplorer.change.ChangeSet;
import fr.lteconsulting.pomexplorer.change.project.ProjectChange;

public class UselessChangeProcessor implements ChangeProcessor<ProjectChange>
{
	@Override
	public void processChange( Session session, Log log, ProjectChange change, ChangeSet<ProjectChange> changeSet )
	{
		String current = change.getCurrentValue();
		String neew = change.getNewValue();
		if( current != null || neew != null )
		{
			if( current == null || neew == null )
				return;
			if( !current.equals( neew ) )
				return;
		}

		changeSet.removeChange( change );
	}
}
