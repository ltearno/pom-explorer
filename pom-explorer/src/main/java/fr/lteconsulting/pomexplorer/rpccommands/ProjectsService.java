package fr.lteconsulting.pomexplorer.rpccommands;

import java.util.List;
import java.util.stream.Collectors;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.uirpc.ProjectDto;

public class ProjectsService
{
	public Object list( Client client, Log log, String query )
	{
		// project filter, just a POC !
		List<ProjectDto> result = client.getCurrentSession().projects().values().stream().filter( ( p ) -> p.getGav().toString().contains( query ) )
				.limit( 200 ).sorted( Project.alphabeticalComparator )
				.map( ( p ) -> ProjectDto.fromProject( client.getCurrentSession(), p ) ).filter( ( p ) -> p != null ).collect( Collectors.toList() );

		return result;
	}
}
