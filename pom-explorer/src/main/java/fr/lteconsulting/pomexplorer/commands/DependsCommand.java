package fr.lteconsulting.pomexplorer.commands;

import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.ILogger;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;
import fr.lteconsulting.pomexplorer.depanalyze.Location;
import fr.lteconsulting.pomexplorer.graph.PomGraph.PomGraphReadTransaction;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation.RelationType;

public class DependsCommand
{
	@Help( "lists the GAVs directly depending on the one given in parameter" )
	public void on( WorkingSession session, ILogger log, GAV gav )
	{
		PomGraphReadTransaction tx = session.graph().read();
		
		if( !tx.gavs().contains( gav ) )
		{
			log.html( Tools.warningMessage( "no GAV " + gav + " registered in the GAV graph, maybe you made a typo ?" ) );
			return;
		}

		Set<Relation> relations = tx.relationsReverse( gav );

		log.html( "<br/><b>Directly depending on " + gav + "</b>, " + relations.size() + " GAVs :<br/>" );
		log.html( "([" + RelationType.DEPENDENCY.shortName() + "]=direct dependency, [" + RelationType.PARENT.shortName()
				+ "]=parent's dependency, [" + RelationType.BUILD_DEPENDENCY.shortName() + "]=build dependency)<br/><br/>" );

		Set<GAV> indirectDependents = new HashSet<>();

		relations.stream().sorted( ( a, b ) -> a.getSource().toString().compareTo( b.getSource().toString() ) )
				.forEach( relation ->
				{
					GAV source = relation.getSource();

					RelationType type = relation.getRelationType();

					log.html( "[" + type.shortName() + "] " + source + " " );

					fillTextForDependency( session, log, relation );

					log.html( "<br/>" );

					Set<Relation> indirectRelations = tx.relationsReverseRec( source );
					for( Relation ir : indirectRelations )
						indirectDependents.add( ir.getSource() );
				} );

		log.html( "<br/><b>Indirectly depending on " + gav + "</b>, " + indirectDependents.size() + " GAVs :<br/>" );
		for( GAV d : indirectDependents )
			log.html( d + "<br/>" );
	}

	@Help( "lists the GAVs that the GAV passed in parameters depends on" )
	public void by( WorkingSession session, ILogger log, GAV gav )
	{
		PomGraphReadTransaction tx = session.graph().read();
		
		if( !tx.gavs().contains( gav ) )
		{
			log.html( Tools.warningMessage( "no GAV " + gav + " registered in the GAV graph, maybe you made a typo ?" ) );
			return;
		}

		Set<Relation> relations = tx.relations( gav );

		log.html( "<br/><b>" + gav + " directly depends on</b> " + relations.size() + " GAVs :<br/>" );
		log.html( "([" + RelationType.DEPENDENCY.shortName() + "]=direct dependency, [" + RelationType.PARENT.shortName()
				+ "]=parent's dependency, [" + RelationType.BUILD_DEPENDENCY.shortName() + "]=build dependency)<br/><br/>" );

		Set<GAV> indirectDependents = new HashSet<>();

		relations.stream().sorted( ( a, b ) -> a.getTarget().toString().compareTo( b.getTarget().toString() ) )
				.forEach( relation ->
				{
					GAV target = relation.getTarget();
					RelationType type = relation.getRelationType();

					log.html( "[" + type.shortName() + "] " + target + " " );
					fillTextForDependency( session, log, relation );
					log.html( "<br/>" );

					Set<Relation> indirectRelations = tx.relationsRec( target );
					for( Relation ir : indirectRelations )
						indirectDependents.add( ir.getTarget() );
				} );

		log.html( "<br/><b>" + gav + " indirectly depends on</b> " + indirectDependents.size() + " GAVs :<br/>" );
		indirectDependents.stream().sorted( ( a, b ) -> a.toString().compareTo( b.toString() ) )
				.forEach( d -> log.html( d + "<br/>" ) );
	}

	private void fillTextForDependency( WorkingSession session, ILogger log, Relation relation )
	{
		GAV source = relation.getSource();

		Project sourceProject = session.projects().forGav( source );
		if( sourceProject == null )
		{
			log.html( Tools.warningMessage( "(no project for this GAV, dependency locations not analyzed)" ) );
			return;
		}
		
		StringBuilder sb = new StringBuilder();

		sb.append( "declared at : " );
		Location location = Tools.findDependencyLocation( session, log, sourceProject, relation );
		if( location != null )
			sb.append( location.toString() );
		else
			sb.append( Tools.warningMessage( "cannot find dependency location from " + sourceProject + " to "
					+ relation.getTarget() + " (relation of type " + relation.toString() + ")" ) );

		if( location instanceof GavLocation )
		{
			GavLocation gavLocation = (GavLocation) location;
			if( Tools.isMavenVariable( gavLocation.getUnresolvedGav().getVersion() ) )
			{
				String property = Tools.getPropertyNameFromPropertyReference( gavLocation.getUnresolvedGav().getVersion() );
				Project definitionProject = Tools.getPropertyDefinitionProject( session, gavLocation.getProject(), property );
				sb.append( ", property ${" + property + "} defined in project " + definitionProject.getGav() );
			}
		}
		
		log.html( sb.toString() );
	}
}
