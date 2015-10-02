package fr.lteconsulting.pomexplorer.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.lteconsulting.pomexplorer.GitTools;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class GitCommand
{
	@Help( "displays the list of git repos found" )
	public String main( WorkingSession session, CommandOptions options )
	{
		return status( session, options );
	}

	@Help( "displays the list of git repos found" )
	public String status( WorkingSession session, CommandOptions options )
	{
		return status( session, options, null );
	}

	@Help( "displays the list of git repos found. Filtered by repository path or contained projects' gavs." )
	public String status( WorkingSession session, CommandOptions options, String filter )
	{
		StringBuilder log = new StringBuilder();

		log.append( "List git repositories :<br/>" );
		log.append( "<i>Those marked with [*] have not a clean head</i><br/><br/>" );

		session.repositories().values().stream()
				.filter( r -> filter == null || r.getPath().toFile().getAbsolutePath().toLowerCase().contains( filter.toLowerCase() ) || r.getProjects().stream().anyMatch( p -> p.getGav().toString().toLowerCase().contains( filter.toLowerCase() ) ) )
				.sorted( ( a, b ) -> a.getPath().compareTo( b.getPath() ) ).forEachOrdered( repo -> {
					repo.getStatus( log, options.getFlag( "v" ) );
				} );

		return log.toString();
	}

	@Help( "displays the list of git repos, together with the projects they contain" )
	public String projects( WorkingSession session )
	{
		StringBuilder log = new StringBuilder();

		log.append( "List git repositories :<br/>" );

		Map<String, List<Project>> groups = session.projects().values().stream().collect( Collectors.groupingBy( project -> {
			String res = GitTools.findGitRoot( project.getPomFile().getParent() );
			return res != null ? res : "_no_repository_";
		} ) );

		groups.keySet().stream().sorted().forEachOrdered( repo -> {
			List<Project> projects = groups.get( repo );
			log.append( "= Repository : '" + repo + "' contains " + projects.size() + " projects :<br/>" );
			for( Project project : projects )
				log.append( project + "<br/>" );
			log.append( "<br/>" );
		} );

		return log.toString();
	}
}
