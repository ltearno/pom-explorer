package fr.lteconsulting.pomexplorer.commands;

import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.GAV;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.WorkingSession;
import fr.lteconsulting.pomexplorer.depanalyze.GavLocation;
import fr.lteconsulting.pomexplorer.depanalyze.Location;
import fr.lteconsulting.pomexplorer.graph.relation.GAVRelation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation;
import fr.lteconsulting.pomexplorer.graph.relation.Relation.RelationType;

public class DependsCommand
{
	@Help( "lists the GAVs directly depending on the one given in parameter" )
	public String on( WorkingSession session, String gavString )
	{
		GAV gav = Tools.string2Gav( gavString );
		if( gav == null )
			return "specify the GAV with the group:artifact:version format please";

		StringBuilder res = new StringBuilder();

		Set<GAVRelation<Relation>> relations = session.graph().relationsReverse( gav );

		res.append( "<br/><b>Directly depending on " + gav + "</b>, " + relations.size() + " GAVs :<br/>" );
		res.append( "([" + RelationType.DEPENDENCY.shortName() + "]=direct dependency, [" + RelationType.PARENT.shortName() + "]=parent's dependency, [" + RelationType.BUILD_DEPENDENCY.shortName() + "]=build dependency)<br/><br/>" );

		Set<GAV> indirectDependents = new HashSet<>();
		

		for( GAVRelation<Relation> relation : relations )
		{
			GAV source = relation.getSource();
			
			RelationType type = relation.getRelation().getRelationType();

			res.append( "[" + type.shortName() + "] " + source + " " );
			
			fillTextForDependency( session, res, relation );
			
			res.append( "<br/>" );

			Set<GAVRelation<Relation>> indirectRelations = session.graph().relationsReverseRec( source );
			for( GAVRelation<Relation> ir : indirectRelations )
				indirectDependents.add( ir.getSource() );
		}

		res.append( "<br/><b>Indirectly depending on " + gav + "</b>, " + indirectDependents.size() + " GAVs :<br/>" );
		for( GAV d : indirectDependents )
		{
			res.append( d + "<br/>" );
		}

		return res.toString();
	}

	@Help( "lists the GAVs that the GAV passed in parameters depends on" )
	public String by( WorkingSession session, String gavString )
	{
		GAV gav = Tools.string2Gav( gavString );
		if( gav == null )
			return "specify the GAV with the group:artifact:version format please";

		StringBuilder res = new StringBuilder();

		Set<GAVRelation<Relation>> relations = session.graph().relations( gav );

		res.append( "<br/><b>" + gav + " directly depends on</b> " + relations.size() + " GAVs :<br/>" );
		res.append( "([" + RelationType.DEPENDENCY.shortName() + "]=direct dependency, [" + RelationType.PARENT.shortName() + "]=parent's dependency, [" + RelationType.BUILD_DEPENDENCY.shortName() + "]=build dependency)<br/><br/>" );

		Set<GAV> indirectDependents = new HashSet<>();

		for( GAVRelation<Relation> relation : relations )
		{
			GAV target = relation.getTarget();
			RelationType type = relation.getRelation().getRelationType();

			res.append( "[" + type.shortName() + "] " + target + " " );
			fillTextForDependency( session, res, relation );
			res.append( "<br/>" );

			Set<GAVRelation<Relation>> indirectRelations = session.graph().relationsRec( target );
			for( GAVRelation<Relation> ir : indirectRelations )
				indirectDependents.add( ir.getTarget() );
		}

		res.append( "<br/><b>" + gav + " indirectly depends on</b> " + indirectDependents.size() + " GAVs :<br/>" );
		for( GAV d : indirectDependents )
		{
			res.append( d + "<br/>" );
		}

		return res.toString();
	}

	private void fillTextForDependency( WorkingSession session, StringBuilder res, GAVRelation<Relation> relation )
	{
		GAV source = relation.getSource();
		
		Project sourceProject = session.projects().forGav( source );
		if( sourceProject == null )
		{
			res.append( Tools.warningMessage( "(no project for this GAV, dependency locations not analyzed)" ) );
			return;
		}
	
		res.append( "declared at : " );
		Location location = Tools.findDependencyLocation( session, res, sourceProject, relation );
		res.append( location );
	
		if( location instanceof GavLocation )
		{
			GavLocation gavLocation = (GavLocation) location;
			if( Tools.isMavenVariable( gavLocation.getUnresolvedGav().getVersion() ) )
			{
				String property = Tools.getPropertyNameFromPropertyReference( gavLocation.getUnresolvedGav().getVersion() );
				Project definitionProject = Tools.getPropertyDefinitionProject( session, gavLocation.getProject(), property );
				res.append( ", property ${" + property + "} defined in project " + definitionProject.getGav() );
			}
		}
	}
}
