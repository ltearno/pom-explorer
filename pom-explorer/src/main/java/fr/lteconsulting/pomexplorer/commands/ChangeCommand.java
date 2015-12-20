package fr.lteconsulting.pomexplorer.commands;

import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.Log;
import fr.lteconsulting.pomexplorer.Project;
import fr.lteconsulting.pomexplorer.Session;
import fr.lteconsulting.pomexplorer.Tools;
import fr.lteconsulting.pomexplorer.change.Change.ChangeCause;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange.DependencyChange;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange.GavChange;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange.ParentChange;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange.PluginChange;
import fr.lteconsulting.pomexplorer.change.graph.GraphChange.RelationChange;
import fr.lteconsulting.pomexplorer.change.graph.GraphChangeProcessing;
import fr.lteconsulting.pomexplorer.change.project.PomChanger;
import fr.lteconsulting.pomexplorer.change.project.ProjectChange;
import fr.lteconsulting.pomexplorer.change.project.ProjectChangeProcessing;
import fr.lteconsulting.pomexplorer.model.DependencyKey;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.model.GroupArtifact;

public class ChangeCommand
{
	@Help( "lists the change set" )
	public void main( Session session, Log log )
	{
		list( session, log );
	}

	@Help( "lists the change set" )
	public void list( Session session, Log log )
	{
		if( session.graphChanges().isEmpty() )
		{
			log.html( "empty graph changeset<br/>" );
		}
		else
		{
			log.html( "session's graph changeset:<br/>" );
			StringBuilder sb = new StringBuilder();
			session.graphChanges().stream().sorted( ( a, b ) -> Gav.alphabeticalComparator.compare( a.getSource(), b.getSource() ) ).forEach( c -> {
				Set<ChangeCause> causes = c.getCauses();

				sb.append( c );
				if( causes != null && !causes.isEmpty() )
				{
					sb.append( "<span style='color:grey;font-size:90%;'>" );
					causes.forEach( ca -> sb.append( " processor:" + ca.getProcessor() + " " + ca.getChange() ) );
					sb.append( "</span>" );
				}
				sb.append( "<br/>" );
			} );
			log.html( sb.toString() );
		}

		if( session.projectChanges().isEmpty() )
		{
			log.html( "empty project changeset<br/>" );
		}
		else
		{
			log.html( "session's project changeset:<br/>" );
			StringBuilder sb = new StringBuilder();
			session.projectChanges().stream().sorted( ( a, b ) -> Project.alphabeticalComparator.compare( a.getProject(), b.getProject() ) ).forEach( c -> {
				Set<ChangeCause> causes = c.getCauses();

				sb.append( c + ", current value: " + c.getCurrentValue() );
				if( causes != null && !causes.isEmpty() )
				{
					sb.append( "<span style='color:grey;font-size:90%;'>" );
					causes.forEach( ca -> sb.append( " processor:" + ca.getProcessor() + " " + ca.getChange() ) );
					sb.append( "</span>" );
				}
				sb.append( "<br/>" );
			} );
			log.html( sb.toString() );
		}
	}

	@Help( "process the graph changes with active processors (propagator, releaser, opener)" )
	public void processChanges( Session session, Log log )
	{
		log.html( "Processing changes...<br/>" );
		GraphChangeProcessing processing = new GraphChangeProcessing();
		Set<GraphChange> processed = processing.process( session, log, session.graphChanges() );
		processed.forEach( change -> session.graphChanges().add( change ) );
		log.html( "Done !<br/>Use the 'change list' command to see the new changesets<br/>" );
	}

	@Help( "resolve the current graph changeset. That is all the graph changes are converted into project changes and injected in the project changeset." )
	public void resolveChanges( Session session, Log log )
	{
		Set<GraphChange> changes = new HashSet<>( session.graphChanges() );
		session.graphChanges().clear();

		log.html( "resolving " + changes.size() + " graph changes...<br/>" );
		log.html( "<i>project changes will be generated from graph structure changes</i><br/>" );

		for( GraphChange change : changes )
		{
			Project changedProject = session.projects().forGav( change.getSource() );
			if( changedProject == null )
			{
				log.html( Tools.warningMessage( "no project for gav " + change.getSource() + ", change cannot be resolved (" + change + ")." ) );
				continue;
			}

			change.visit( new GraphChange.Visitor()
			{
				@Override
				public void visit( GavChange change )
				{
					Gav newValue = change.getNewValue();
					assert newValue != null;

					String groupId = newValue.getGroupId() != null ? newValue.getGroupId() : null;
					String artifactId = newValue.getArtifactId() != null ? newValue.getArtifactId() : null;
					String version = newValue.getVersion() != null ? newValue.getVersion() : null;

					session.projectChanges().add( ProjectChange.setProject( changedProject, "groupId", groupId ) );
					session.projectChanges().add( ProjectChange.setProject( changedProject, "artifactId", artifactId ) );
					session.projectChanges().add( ProjectChange.setProject( changedProject, "version", version ) );
				}

				@Override
				public void visit( PluginChange change )
				{
					Gav newTarget = change.getNewValue();
					GroupArtifact key = change.getRelationKey();
					assert key != null;

					String groupId = newTarget != null ? newTarget.getGroupId() : null;
					String artifactId = newTarget != null ? newTarget.getArtifactId() : null;
					String version = newTarget != null ? newTarget.getVersion() : null;

					session.projectChanges().add( ProjectChange.setPlugin( changedProject, key, "groupId", groupId ) );
					session.projectChanges().add( ProjectChange.setPlugin( changedProject, key, "artifactId", artifactId ) );
					session.projectChanges().add( ProjectChange.setPlugin( changedProject, key, "version", version ) );
				}

				@Override
				public void visit( DependencyChange change )
				{
					Gav newTarget = change.getNewValue();
					DependencyKey key = change.getRelationKey();
					assert key != null;

					String groupId = newTarget != null ? newTarget.getGroupId() : null;
					String artifactId = newTarget != null ? newTarget.getArtifactId() : null;
					String version = newTarget != null ? newTarget.getVersion() : null;

					session.projectChanges().add( ProjectChange.setDependency( changedProject, key, "groupId", groupId ) );
					session.projectChanges().add( ProjectChange.setDependency( changedProject, key, "artifactId", artifactId ) );
					session.projectChanges().add( ProjectChange.setDependency( changedProject, key, "version", version ) );
				}

				@Override
				public void visit( ParentChange parentChange )
				{
					Gav newParent = parentChange.getNewValue();

					String groupId = newParent != null ? newParent.getGroupId() : null;
					String artifactId = newParent != null ? newParent.getArtifactId() : null;
					String version = newParent != null ? newParent.getVersion() : null;

					session.projectChanges().add( ProjectChange.setParent( changedProject, "groupId", groupId ) );
					session.projectChanges().add( ProjectChange.setParent( changedProject, "artifactId", artifactId ) );
					session.projectChanges().add( ProjectChange.setParent( changedProject, "version", version ) );
				}
			} );
		}

		ProjectChangeProcessing processing = new ProjectChangeProcessing();
		Set<ProjectChange> processedChanges = processing.process( session, log, session.projectChanges() );
		session.projectChanges().clear();
		session.projectChanges().addAll( processedChanges );
		log.html( "Done !<br/>Use the 'change list' command to see the new changesets<br/>" );
	}

	@Help( "applies the project changes in the pom.xml files" )
	public void apply( Session session, Log log )
	{
		log.html( "applying project changes<br/>" );
		PomChanger changer = new PomChanger();
		changer.applyChanges( session, session.projectChanges(), log );
		log.html( "done<br/>" );
	}

	@Help( "clears the graph and project changes list" )
	public void clear( Session session, Log log )
	{
		log.html( "clearing change set<br/>" );
		session.graphChanges().clear();
		session.projectChanges().clear();
		log.html( "done<br/>" );
	}

	@Help( "changes a gav in the graph" )
	public void gav( Session session, Log log, Gav gav, Gav newGav )
	{
		GavChange change = new GavChange( gav, newGav );
		session.graphChanges().add( change );
		log.html( "added change in change set: " + change + "<br/>" );
	}

	@Help( "changes a relation in the graph" )
	public void relation( Session session, Log log, Gav source, String gact, Gav newTarget )
	{
		RelationChange change = RelationChange.create( source, gact, newTarget );
		if( change == null )
		{
			log.html( Tools.warningMessage( "error creating the change !" ) );
		}
		else
		{
			session.graphChanges().add( change );
			log.html( "added change in change set: " + change + "<br/>" );
		}
	}

	@Help( "removes a relation from the graph" )
	public void removeRelation( Session session, Log log, Gav source, String gact )
	{
		RelationChange change = RelationChange.create( source, gact, null );
		if( change == null )
		{
			log.html( Tools.warningMessage( "error creating the change !" ) );
		}
		else
		{
			session.graphChanges().add( change );
			log.html( "added change in change set: " + change + "<br/>" );
		}
	}

	@Help( "sets or adds a dependency to a project" )
	public void setProject(
			Session session,
			Log log,
			FilteredGAVs gavs,
			@Help( "can be <i>project</i>, <i>parent</i>, <i>property</i>, <i>d:group:artifact:classifier:type</i>, <i>dm:group:artifact:classifier:type</i>, <i>p:group:artifact</i> or <i>pm:group:artifact</i>" ) String location,
			@Help( "can be <i>groupId</i>, <i>artifactId</i>, <i>version</i>, <i>scope</i>, or a property name" ) String nodeName,
			String newValue )
	{
		for( Gav gav : gavs.getGavs( session ) )
		{
			Project project = session.projects().forGav( gav );
			if( project == null )
			{
				log.html( "no project for gav " + gav + "<br/>" );
				continue;
			}

			ProjectChange change = ProjectChange.set( project, location, nodeName, newValue );
			if( change == null )
			{
				log.html( "no change added<br/>" );
				return;
			}

			session.projectChanges().add( change );
			log.html( "added change in change set: " + change + "<br/>" );
		}

	}

	@Help( "removes a dependency from a project" )
	public void removeProject( Session session, Log log, FilteredGAVs gavs, String location, String nodeName )
	{
		for( Gav gav : gavs.getGavs( session ) )
		{
			Project project = session.projects().forGav( gav );
			if( project == null )
			{
				log.html( "no project for gav " + gav + "<br/>" );
				continue;
			}

			ProjectChange change = ProjectChange.remove( project, location, nodeName );
			if( change == null )
			{
				log.html( "no change added<br/>" );
				return;
			}

			session.projectChanges().add( change );
			log.html( "added change in change set: " + change + "<br/>" );
		}
	}
}
