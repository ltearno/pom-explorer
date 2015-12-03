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
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;

public class CheckCommand
{
	@Help( "checks some commons points of errors, at least of attention..." )
	public void main( Client client, WorkingSession session, ILogger log )
	{
		StringBuilder sb = new StringBuilder();
		
		List<GAV> gavsWithoutProject = gavsWithoutProject( session );
		sb.append( "<b>GAVs without projects</b><br/>" );
		if( gavsWithoutProject.isEmpty() )
		{
			sb.append( "No GAV without project.<br/>" );
		}
		else
		{
			sb.append( gavsWithoutProject.size() + " GAV(s) without project :" );
			for( GAV gav : gavsWithoutProject )
				sb.append( "<br/>" + gav );
		}

		PomGraphReadTransaction tx = session.graph().read();
		
		sb.append( "<br/><br/><b>Projects without version</b><br/>" );
		for( Project project : session.projects().values() )
		{
			// project version should be null
			if( project.getMavenProject().getModel().getVersion() != null )
				continue;

			// and project should have a parent
			GAV parentProjectGav = tx.parent( project.getGav() );
			if( parentProjectGav == null )
				continue;

			sb.append( project.toString() + "<br/>" );
		}

		Map<MiniGAV, Set<GAV>> multipleGavs = multipleGavs( session );
		sb.append( "<br/><br/><b>Multiple GAVs</b><br/>" );
		if( multipleGavs.isEmpty() )
		{
			sb.append( "No GAV with multiple versions.<br/>" );
		}
		else
		{
			sb.append( multipleGavs.size() + " GAVs with multiple versions :<br/>" );
			multipleGavs.entrySet().stream().sorted( ( a, b ) -> a.getKey().toString().compareTo( b.getKey().toString() ) )
					.forEach( e ->
					{
						sb.append( e.getKey() + " : " );
						boolean coma = false;
						for( GAV gav : e.getValue() )
						{
							if( coma )
								sb.append( ", " );
							else
								coma = true;
							sb.append( "" + gav.getVersion() );
						}
						sb.append( "<br/>" );
					} );
		}

		sb.append( "done.<br/>" );
		
		log.html( sb.toString() );
	}

	private Map<MiniGAV, Set<GAV>> multipleGavs( WorkingSession session )
	{
		PomGraphReadTransaction tx = session.graph().read();
		Map<MiniGAV, Set<GAV>> prov = new HashMap<>();

		for( GAV gav : tx.gavs() )
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
		PomGraphReadTransaction tx = session.graph().read();
		Set<GAV> res = new HashSet<GAV>();

		for( GAV gav : tx.gavs() )
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
