package fr.lteconsulting.pomexplorer.change.project;

import fr.lteconsulting.pomexplorer.change.ChangeProcessing;
import fr.lteconsulting.pomexplorer.change.project.processor.DefaultProcessor;
import fr.lteconsulting.pomexplorer.change.project.processor.FollowVariableProcessor;
import fr.lteconsulting.pomexplorer.change.project.processor.UselessChangeProcessor;

public class ProjectChangeProcessing extends ChangeProcessing<ProjectChange>
{
	public ProjectChangeProcessing()
	{
		processors.add( new DefaultProcessor() );
		processors.add( new FollowVariableProcessor() );
		processors.add( new UselessChangeProcessor() );
	}
}
