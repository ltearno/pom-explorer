package fr.lteconsulting.pomexplorer;

import fr.lteconsulting.pomexplorer.model.Gav;

public interface ProjectContainer
{
	Project forGav( Gav gav );

	default ProjectContainer combine( ProjectContainer container )
	{
		return ( gav ) -> {
			Project res = forGav( gav );
			if( res == null )
				res = container.forGav( gav );
			return res;
		};
	}
	
	default Project getParentProject( Project project )
	{
		if( project.getParentGav() == null )
			return null;

		return forGav( project.getParentGav() );
	}
}
