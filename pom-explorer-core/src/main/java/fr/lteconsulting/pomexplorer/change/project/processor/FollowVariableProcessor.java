package fr.lteconsulting.pomexplorer.change.project.processor;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.change.ChangeProcessor;
import fr.lteconsulting.pomexplorer.change.ChangeSet;
import fr.lteconsulting.pomexplorer.change.project.ProjectChange;
import fr.lteconsulting.pomexplorer.change.project.SetPropertyCaller;

public class FollowVariableProcessor implements ChangeProcessor<ProjectChange>
{
	@Override
	public void processChange( Session session, Log log, ProjectChange change, ChangeSet<ProjectChange> changeSet )
	{
		// if current value is a maven variable (complete variable interpolation like xxx-${var1}-kjhkjh is not supported yet)
		// we suppress the change and create a change on the property defining the value, plus a log
		String currentValue = change.getCurrentValue();
		if( Tools.isNonResolvedValue( currentValue ) )
		{
			changeSet.removeChange( change );

			if( !Tools.isMavenVariable( currentValue ) )
			{
				log.html( Tools.warningMessage( "the current value is interpolated, this is not supported, so the change is abandonned" ) );
				return;
			}

			String propertyName = Tools.getPropertyNameFromPropertyReference( currentValue );
			Project definitionProject = change.getProject().getPropertyDefinitionProject( propertyName );
			if( definitionProject == null )
			{
				log.html( Tools.warningMessage( "cannot find where the property " + currentValue + " is defined ! abandonning change" ) );
				return;
			}

			changeSet.addChange( SetPropertyCaller.withProject( definitionProject ).withPropertyName( propertyName ).withNewValue( change.getNewValue() ).call() );
		}
	}
}
