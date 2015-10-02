package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import fr.lteconsulting.pomexplorer.graph.relation.DependencyRelation;
import fr.lteconsulting.pomexplorer.graph.relation.GAVRelation;
import fr.lteconsulting.superman.Superman;

@Superman
public class Builder
{
	private WorkingSession session;

	public void setSession(WorkingSession session)
	{
		this.session = session;
	}

	public void run()
	{
		while (true)
		{
			Project changed = session.projectsWatcher().hasChanged();
			if (changed != null)
			{
				processProjectChange(session, changed);
			}

			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void processProjectChange(WorkingSession session, Project project)
	{
		if (project == null)
			return;

		log(Tools.warningMessage("[live update] project " + project + " has been modified, building it..."));

		buildGAVAndDependents(project.getGav());
	}

	private void buildGAVAndDependents(GAV gav)
	{
		log("<<<build " + gav);

		Project project = session.projects().forGav(gav);
		if (project != null)
		{
			build(project);
		}

		Set<GAV> maintainedGavs = new HashSet<>();
		session.maintainedProjects().stream().map(p -> p.getGav()).forEach(g -> maintainedGavs.add(g));

		log("inspect direct dependencies");
		for (GAVRelation<DependencyRelation> r : session.graph().dependents(gav))
		{
			GAV dependent = r.getSource();
			log("maybe build GAV " + dependent);

			Set<GAV> dependents = dependentsAndSelf(dependent);

			// path from dependent to one of the maintained projects ?
			if (dependentsContainMaintained(dependents, maintainedGavs))
			{
				log("need to build !");
				buildGAVAndDependents(dependent);
			}
			else
			{
				log("do not build because not connected to a maintained project");
			}
		}

		log(gav + " build>>>");
	}

	private boolean dependentsContainMaintained(Set<GAV> dependents, Set<GAV> maintained)
	{
		for (GAV dependent : dependents)
		{
			if (maintained.contains(dependent))
				return true;
		}

		return false;
	}

	private Set<GAV> dependentsAndSelf(GAV gav)
	{
		HashSet<GAV> res = new HashSet<>();
		res.add(gav);
		session.graph().dependentsRec(gav).stream().map(r -> r.getSource()).forEach(g -> res.add(g));
		return res;
	}

	private boolean build(Project project)
	{
		log("building " + project + "...");

		File directory = project.getPomFile().getParentFile();

		log("cd " + directory + "<br/>");
		log("mvn install -N -DskipTests<br/>");

		try
		{
			Thread.sleep(2500);
		}
		catch (InterruptedException e)
		{
		}

		log("build done");

		return true;
	}

	private void log(String message)
	{
		for (Client client : session.getClients())
			client.send(message);
	}
}
