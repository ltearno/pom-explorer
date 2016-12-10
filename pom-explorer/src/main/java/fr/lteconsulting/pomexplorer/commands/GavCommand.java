package fr.lteconsulting.pomexplorer.commands;

import java.io.File;
import java.util.Set;

import fr.lteconsulting.pomexplorer.ApplicationSession;
import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.DefaultPomFileLoader;
import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.PomAnalysis;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.tools.FilteredGAVs;

public class GavCommand
{
	@Help( "list the session's GAVs" )
	public void main( ApplicationSession session, Log log )
	{
		list( session, log );
	}

	@Help( "list the session's GAVs" )
	public void list( ApplicationSession session, Log log )
	{
		list( session, null, log );
	}

	@Help( "list the session's GAVs, with filtering" )
	public void list( ApplicationSession session, FilteredGAVs gavFilter, Log log )
	{
		StringBuilder sb = new StringBuilder();

		sb.append( "<br/>GAV list filtered with '" + (gavFilter != null ? gavFilter.getFilter() : "no filter") + "' :<br/>" );
		if( gavFilter != null )
		{
			gavFilter.getGavs( session.session() ).stream().sorted( ( g1, g2 ) -> g1.toString().compareTo( g2.toString() ) ).forEach( gav -> sb.append( gav + "<br/>" ) );
		}
		else
		{
			PomGraphReadTransaction tx = session.graph().read();

			tx.gavs().stream().sorted( ( g1, g2 ) -> g1.toString().compareTo( g2.toString() ) ).forEach( gav -> sb.append( gav + "<br/>" ) );
		}

		log.html( sb.toString() );
	}

	@Help( "analyze all the gav's dependencies and add them in the pom graph." )
	public void add( ApplicationSession session, Log log, Client client, Gav gav )
	{
		log.html( "analyzing " + gav + "...<br/>" );
		DefaultPomFileLoader loader = new DefaultPomFileLoader( session.session(), true );

		File pomFile = loader.loadPomFileForGav( gav, null, log );
		if( pomFile == null )
		{
			log.html( Tools.errorMessage( "cannot fetch project " + gav ) );
			return;
		}

		PomAnalysis analysis = new PomAnalysis( session.session(), loader, null, false, log );
		analysis.addFile( pomFile );
		analysis.loadProjects();
		analysis.completeLoadedProjects();
		analysis.addCompletedProjectsToSession();
		Set<Project> addedToGraph = analysis.addCompletedProjectsToGraph();

		log.html( "project " + gav + " fetched successfully, " + addedToGraph.size() + " project added to graph.<br/>" );
	}

	@Help( "analyze gavs which have no associated project" )
	public void resolve( ApplicationSession session, Log log, Client client )
	{
		DefaultPomFileLoader loader = new DefaultPomFileLoader( session.session(), true );

		PomAnalysis analysis = new PomAnalysis( session.session(), loader, null, false, log );

		session.graph().read().gavs().stream().filter( gav -> session.projects().forGav( gav ) == null ).forEach( gav -> {
			log.html( "fetching pom file for " + gav + "...<br/>" );

			File pomFile = loader.loadPomFileForGav( gav, null, log );
			if( pomFile == null )
			{
				log.html( Tools.errorMessage( "cannot fetch project " + gav ) );
				return;
			}

			analysis.addFile( pomFile );
		} );

		analysis.loadProjects();
		analysis.completeLoadedProjects();
		analysis.addCompletedProjectsToSession();
		Set<Project> addedToGraph = analysis.addCompletedProjectsToGraph();

		log.html( "finished, " + addedToGraph.size() + " project added to graph.<br/>" );
	}
}
