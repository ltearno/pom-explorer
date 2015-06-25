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
	public String main( WorkingSession session )
	{
		StringBuilder log = new StringBuilder();

		log.append( "List git repositories :<br/>" );

		session.projects().values().stream().map( project -> GitTools.findGitRoot( project.getPomFile().getParent() ) ).filter( s -> s != null ).distinct().sorted().forEachOrdered( repoPath -> log.append( repoPath + "<br/>" ) );

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
			log.append( "= Repository : '" + repo + "' contains projects :<br/>" );
			System.out.println( repo );
			List<Project> projects = groups.get( repo );
			for( Project project : projects )
				log.append( project + "<br/>" );
			log.append( "<br/>" );
		} );

		return log.toString();
	}
}
