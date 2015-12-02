package fr.lteconsulting.pomexplorer.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fr.lteconsulting.pomexplorer.Client;
import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;

public class CheckCommand
{
	@Help( "checks some commons points of errors, at least of attention..." )
	public void main( Client client, WorkingSession session, ILogger log )
	{
		List<GAV> gavsWithoutProject = gavsWithoutProject( session );
		log.html( "<b>GAVs without projects</b><br/>" );
		if( gavsWithoutProject.isEmpty() )
		{
			log.html( "No GAV without project.<br/>" );
		}
		else
		{
			log.html( gavsWithoutProject.size() + " GAV(s) without project :" );
			for( GAV gav : gavsWithoutProject )
				log.html( "<br/>" + gav );
		}

		log.html( "<br/><br/><b>Projects without version</b><br/>" );
		for( Project project : session.projects().values() )
		{
			// project version should be null
			if( project.getUnresolvedPom().getModel().getVersion() != null )
				continue;

			// and project should have a parent
			GAV parentProjectGav = session.graph().parent( project.getGav() );
			if( parentProjectGav == null )
				continue;

			log.html( project.toString() + "<br/>" );
		}

		Map<MiniGAV, Set<GAV>> multipleGavs = multipleGavs( session );
		log.html( "<br/><br/><b>Multiple GAVs</b><br/>" );
		if( multipleGavs.isEmpty() )
		{
			log.html( "No GAV with multiple versions.<br/>" );
		}
		else
		{
			log.html( multipleGavs.size() + " GAVs with multiple versions :<br/>" );
			multipleGavs.entrySet().stream().sorted( ( a, b ) -> a.getKey().toString().compareTo( b.getKey().toString() ) )
					.forEach( e ->
					{
						log.html( e.getKey() + " : " );
						boolean coma = false;
						for( GAV gav : e.getValue() )
						{
							if( coma )
								log.html( ", " );
							else
								coma = true;
							log.html( "" + gav.getVersion() );
						}
						log.html( "<br/>" );
					} );
		}

		log.html( "done.<br/>" );
	}

	private Map<MiniGAV, Set<GAV>> multipleGavs( WorkingSession session )
	{
		Map<MiniGAV, Set<GAV>> prov = new HashMap<>();

		for( GAV gav : session.graph().gavs() )
		{
			MiniGAV miniGav = new MiniGAV( gav.getGroupId(), gav.getArtifactId() );
			Set<GAV> list = prov.get( miniGav );
			if( list == null )
			{
				list = new HashSet<>();
				prov.put( miniGav, list );
			}
			list.add( gav );
		}

		Map<MiniGAV, Set<GAV>> res = new HashMap<>();
		for( Entry<MiniGAV, Set<GAV>> e : prov.entrySet() )
		{
			if( e.getValue().size() > 1 )
				res.put( e.getKey(), e.getValue() );
		}

		return res;
	}

	private List<GAV> gavsWithoutProject( WorkingSession session )
	{
		Set<GAV> res = new HashSet<GAV>();

		for( GAV gav : session.graph().gavs() )
		{
			if( !session.projects().contains( gav ) )
				res.add( gav );
		}

		ArrayList<GAV> list = new ArrayList<GAV>();
		list.addAll( res );
		Collections.sort( list, Tools.gavAlphabeticalComparator );

		return list;
	}

	private static class MiniGAV
	{
		String groupId;

		String artifactId;

		public MiniGAV( String groupId, String artifactId )
		{
			super();
			this.groupId = groupId;
			this.artifactId = artifactId;
		}

		@Override
		public String toString()
		{
			return groupId + ":" + artifactId;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
			result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
			return result;
		}

		@Override
		public boolean equals( Object obj )
		{
			if( this == obj )
				return true;
			if( obj == null )
				return false;
			if( getClass() != obj.getClass() )
				return false;
			MiniGAV other = (MiniGAV) obj;
			if( artifactId == null )
			{
				if( other.artifactId != null )
					return false;
			}
			else if( !artifactId.equals( other.artifactId ) )
				return false;
			if( groupId == null )
			{
				if( other.groupId != null )
					return false;
			}
			else if( !groupId.equals( other.groupId ) )
				return false;
			return true;
		}
	}
}
