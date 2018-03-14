package fr.lteconsulting.pomexplorer;

import fr.lteconsulting.pomexplorer.graph.relation.Scope;
import fr.lteconsulting.pomexplorer.model.DependencyKey;
import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.model.GroupArtifact;
import fr.lteconsulting.pomexplorer.model.VersionScope;
import fr.lteconsulting.pomexplorer.model.transitivity.DependencyManagement;
import fr.lteconsulting.pomexplorer.model.transitivity.DependencyNode;
import fr.lteconsulting.pomexplorer.model.transitivity.RawDependency;
import fr.lteconsulting.pomexplorer.model.transitivity.Repository;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

public class TransitivityResolver
{
	private static class Cache
	{
        private Predicate<GroupArtifact> excludeFilter;
		private final Map<Integer, TransitivityProjectInformation> cache = new HashMap<>();

		public TransitivityProjectInformation getInformation( Project project )
		{
			TransitivityProjectInformation res = cache.get( System.identityHashCode( project ) );

			if( res == null )
			{
				res = new TransitivityProjectInformation( project );
				cache.put( System.identityHashCode( project ), res );
			}

			return res;
		}

        public void setExcludeFilter(Predicate<GroupArtifact> excludeFilter)
        {
            this.excludeFilter = excludeFilter;
        }
	}

	private final Cache cache = new Cache();

    public void setExcludeFilter(Predicate<GroupArtifact> excludeFilter)
    {
        cache.setExcludeFilter(excludeFilter);
    }

	public DependencyNode getTransitiveDependencyTree( Session session, Project project, boolean full, boolean online, Map<String, Profile> profiles, PomFileLoader loader, Log log )
	{
		TransitivityProjectInformation cached = cache.getInformation( project );

		return cached.getTransitiveDependencyTree( session, full, online, profiles, loader, log );
	}

	private static class TransitivityProjectInformation
	{
		private final Project project;
		private Predicate<GroupArtifact> excludeFilter;

		private DependencyNode partialTree = null;
		private DependencyNode fullTree = null;

		public TransitivityProjectInformation( Project project )
		{
			this.project = project;
		}

		public void setExcludeFilter(Predicate<GroupArtifact> excludeFilter)
        {
            this.excludeFilter = excludeFilter;
        }

		public DependencyNode getTransitiveDependencyTree( Session session, boolean full, boolean online, Map<String, Profile> profiles, PomFileLoader loader, Log log )
		{
			if( full && fullTree != null )
				return fullTree;

			if( !full && partialTree != null )
				return partialTree;

			Queue<DependencyNode> nodeQueue = new LinkedList<>();

			Gav gav = project.getGav();

			DependencyKey key = new DependencyKey( gav.getGroupId(), gav.getArtifactId(), null, project.getMavenProject().getPackaging() );
			VersionScope vs = new VersionScope( gav.getVersion(), null, Scope.COMPILE );

			DependencyNode rootNode = new DependencyNode( project, key, vs );
			nodeQueue.add( rootNode );
			buildDependencyTree( nodeQueue, full, online, session, profiles, loader, log );

			if( full )
				fullTree = rootNode;
			else
				partialTree = rootNode;

			return rootNode;
		}

		private boolean isGroupArtifactExcluded( DependencyNode node, GroupArtifact ga )
		{
			if(excludeFilter!=null && !excludeFilter.test(ga))
				return false;

			while( node != null )
			{
				if( node.isExcluded( ga ) )
					return true;
				node = node.getParent();
			}

			return false;
		}

		private List<Repository> getProjectRepositories( Session session, Project project, Log log )
		{
			List<Repository> res = null;

			Project current = project;
			while( current != null )
			{
				if( current.getMavenProject().getRepositories() != null && !current.getMavenProject().getRepositories().isEmpty() )
				{
					if( res == null )
						res = new ArrayList<>();

					for( org.apache.maven.model.Repository r : current.getMavenProject().getRepositories() )
					{
						res.add( new Repository( r.getId(), r.getUrl() ) );
					}
				}
				current = session.projects().getParentProject( current );
			}

			return res;
		}

		private Map<DependencyKey, RawDependency> getHierarchicalDependencies( Session session, Project project, Map<DependencyKey, RawDependency> res, boolean online, Map<String, Profile> profiles, Log log )
		{
			Project current = project;

			boolean versionCanBeSelfManaged = true;
			while( current != null )
			{
				res = current.getLocalDependencies( res, profiles, session.projects(), log , versionCanBeSelfManaged);
				current = session.projects().getParentProject( current );
				versionCanBeSelfManaged = false;
			}

			return res;
		}

		private void buildDependencyTree( Queue<DependencyNode> nodeQueue, boolean full, boolean online, Session session, Map<String, Profile> profiles, PomFileLoader loader, Log log )
		{
			int neededLevels = full ? -1 : 1;

			while( !nodeQueue.isEmpty() )
			{
				DependencyNode node = nodeQueue.poll();

				if( neededLevels >= 0 && node.getLevel() >= neededLevels )
					continue;

				node.collectDependencyManagement( profiles, session.projects(), log );

				Map<DependencyKey, RawDependency> localDependencies = getHierarchicalDependencies( session, node.getProject(), null, online, profiles, log );
				if( localDependencies == null )
					continue;

				for( Entry<DependencyKey, RawDependency> e : localDependencies.entrySet() )
				{
					DependencyKey dependencyKey = e.getKey();
					RawDependency dependency = e.getValue();
					if( dependency.isOptional() && !node.isRoot() )
						continue;

					GroupArtifact ga = new GroupArtifact( dependencyKey.getGroupId(), dependencyKey.getArtifactId() );
					if( isGroupArtifactExcluded( node, ga ) )
						continue;

					DependencyNode existingNode = node.searchNodeForGroupArtifact( ga );
					if( existingNode != null )
					{
						if( existingNode.getLevel() <= node.getLevel() + 1 )
							continue;
						else
							existingNode.removeFromParent();
					}

					final Optional<VersionScope> optionalVs = getVersionScopeFromDependencyManagement( node, dependencyKey, dependency );
					final VersionScope vs;
					if(optionalVs.isPresent() )
					{
						vs = optionalVs.get();
					}
					else
					{
						vs = dependency.getVs();
						if( node.isRoot() )
						{
							if(vs.getScope() == null) {
								vs.setScope( Scope.COMPILE );
							}
						}
						else
						{
							Scope scope = Scope.getScopeTransformation( node.getVs().getScope(), dependency.getVs().getScope() );
							if(scope == null )
								continue;
							vs.setScope( scope );
						}
					}

					Scope scope = vs.getScope();
					assert scope != null;
					assert vs.getVersion() != null : "null version of dependency " + dependencyKey + " -> " + dependency + " (for project " + project + ")";

					if( scope == Scope.IMPORT || scope == Scope.SYSTEM )
						continue;

					// get remote repositories
					List<Repository> additionalRepos = getProjectRepositories( session, node.getProject(), log );

					Gav dependencyGav = new Gav( dependencyKey.getGroupId(), dependencyKey.getArtifactId(), vs.getVersion() );

					Project childProject = null;

					if( neededLevels < 0 || node.getLevel() >= neededLevels )
					{
						childProject = session.projects().forGav( dependencyGav );
						if( childProject == null )
						{
							File pomFile = loader.loadPomFileForGav( dependencyGav, additionalRepos, log );
							if( pomFile == null )
							{
								log.html( Tools.errorMessage( "cannot fetch project " + dependencyGav + " referenced in " + node.getProject() ) );
								continue;
							}

							PomAnalysis analysis = new PomAnalysis( session, loader, null, false, log );
							analysis.addFile( pomFile );

							Set<Project> loadedProjects = analysis.loadProjects();
							if( loadedProjects.size() != 1 )
							{
								log.html( Tools.errorMessage( "cannot fetch project " + dependencyGav + " referenced in " + node.getProject() ) );
								continue;
							}

							childProject = loadedProjects.iterator().next();

							analysis.completeLoadedProjects();
							analysis.addCompletedProjectsToSession();

							Set<Project> addedToGraph = analysis.addCompletedProjectsToGraph();
							if( !addedToGraph.contains( childProject ) )
							{
								log.html( Tools.errorMessage( "cannot fetch project " + dependencyGav + " referenced in " + node.getProject() ) );
								continue;
							}
						}
						if( childProject == null )
						{
							// TODO : use specified repositories if needed !
							log.html( Tools.warningMessage( "cannot fetch project " + dependencyGav + " referenced in " + node.getProject() + (dependency.isOptional() ? " (this is an optional dependency)" : "") ) );
							continue;
						}
					}

					DependencyNode child = new DependencyNode( childProject, dependencyKey, vs );
					child.addExclusions( dependency.getExclusions() );

					node.addChild( child );

					DependencyManagement dm = node.getLocalManagement( dependencyKey );
					if( dm != null )
						child.addExclusions( dm.getExclusions() );

					//FIXME is always false
					if( scope == Scope.SYSTEM )
						continue;

					// TODO it seems to me that transitive dependency policy only
					// applies to jar artifacts, is that true ??
					// if( "jar".equals( dependencyKey.getType() ) )
					nodeQueue.add( child );
				}
			}
		}
	}

	private static Optional<VersionScope> getVersionScopeFromDependencyManagement( DependencyNode node, DependencyKey dependencyKey, RawDependency dependency )
	{
		DependencyManagement dependencyManagement = node.getTopLevelManagement( dependencyKey );
		DependencyManagement localManagement = node.getLocalManagement( dependencyKey );
		if( dependencyManagement != null && dependencyManagement.getVs().getVersion() != null )
		{
			// si le top level management est le notre, c'est la version
			// déclarée qui prend le pas
			if( dependencyManagement == localManagement && dependency.getVs().getVersion() != null )
			{
				return Optional.of(dependency.getVs());
			}
			else
			{
				return Optional.of(dependencyManagement.getVs());
			}
		}
		return Optional.empty();
	}
}
