package fr.pgih;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DirectedMultigraph;

import com.mxgraph.layout.mxFastOrganicLayout;

/**
 * Hello world!
 */
public class App
{
	public static void main(String[] args)
	{
		App app = new App();
		app.run();
	}

	private void run()
	{
		DirectedGraph<GAV, Dep> g = new DirectedMultigraph<GAV, Dep>(Dep.class);

		// processFile(new File("C:\\tmp\\hexa.tools"), g);
		processFile(new File("C:\\gr"), g);

		// GAV v;
		// TopologicalOrderIterator<GAV, Dep> orderIterator;
		//
		// orderIterator = new TopologicalOrderIterator<>(g);
		// System.out.println("\nOrdering:");
		// while (orderIterator.hasNext())
		// {
		// v = orderIterator.next();
		// System.out.println(v);
		// }
		//
		// System.out.println(g.toString());

		System.out.println("There are " + gavs.size() + " gavs");

		StrongConnectivityInspector<GAV, Dep> conn = new StrongConnectivityInspector<>(g);
		System.out.println("There are " + conn.stronglyConnectedSets().size() + " strongly connected components");

		ConnectivityInspector<GAV, Dep> ccon = new ConnectivityInspector<>(g);
		System.out.println("There are " + ccon.connectedSets().size() + " weakly connected components");
		for (Set<GAV> comp : ccon.connectedSets())
		{
			System.out.println("  - " + comp.toString());
		}

		CycleDetector<GAV, Dep> cycles = new CycleDetector<GAV, Dep>(g);
		System.out.println("Is there cycles ? " + cycles.detectCycles());

		JGraphXAdapter<GAV, Dep> ga = new JGraphXAdapter<>(g);
		GraphFrame frame = new GraphFrame(ga);

		mxFastOrganicLayout layout = new mxFastOrganicLayout(ga);
		layout.setUseBoundingBox(true);
		layout.setForceConstant(200);
		// mxCircleLayout layout = new mxCircleLayout(ga);
		layout.execute(ga.getDefaultParent());
	}

	private void processFile(File file, DirectedGraph<GAV, Dep> g)
	{
		if (file == null)
			return;

		if (file.isDirectory())
		{
			String name = file.getName();
			if ("target".equalsIgnoreCase(name) || "src".equalsIgnoreCase(name))
				return;

			for (File f : file.listFiles())
				processFile(f, g);
		}
		else if (file.getName().equalsIgnoreCase("pom.xml"))
		{
			processPom(file, g);
		}
	}

	private void processPom(File pom, DirectedGraph<GAV, Dep> g)
	{
		System.out.println("\n# Analysing pom file " + pom.getAbsolutePath());

		System.out.println("## Non-resolving analysis");
		MavenProject project = loadProject(pom);
		System.out.println(project.getGroupId() + ":" + project.getArtifactId() + ":" + project.getVersion() + ":"
				+ project.getPackaging());

		GAV gav = ensureArtifact(project.getGroupId(), project.getArtifactId(), project.getVersion(), g);

		Parent parent = project.getModel().getParent();
		if (parent != null)
		{
			System.out.println("   PARENT : " + parent.getId() + ":" + parent.getRelativePath());
		}

		Properties ptties = project.getProperties();
		if (ptties != null)
		{
			for (Entry<Object, Object> e : ptties.entrySet())
			{
				System.out.println("   PPTY: " + e.getKey() + " = " + e.getValue());
			}
		}
		
		if (project.getDependencyManagement() != null)
		{
			for (Dependency dependency : project.getDependencyManagement().getDependencies())
			{
				System.out.println("   MNGT: " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":"
						+ dependency.getVersion() + ":" + dependency.getClassifier() + ":" + dependency.getScope());

				GAV depGav = ensureArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), g);

				if (gav != null && depGav != null)
					g.addEdge(gav, depGav, new Dep(dependency.getScope(), dependency.getClassifier()));
			}
		}

		for (Dependency dependency : project.getDependencies())
		{
			System.out.println("   " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":"
					+ dependency.getVersion() + ":" + dependency.getClassifier() + ":" + dependency.getScope());

			GAV depGav = ensureArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), g);

			if (gav != null && depGav != null)
				g.addEdge(gav, depGav, new Dep(dependency.getScope(), dependency.getClassifier()));
		}

		System.out.println("## Resolving analysis");
		Toto.toto(pom);

		// Model model = null;
		// FileReader reader = null;
		// MavenXpp3Reader mavenreader = new MavenXpp3Reader();
		// try
		// {
		// reader = new FileReader(pom);
		// }
		// catch (FileNotFoundException e1)
		// {
		// }
		// try
		// {
		// model = mavenreader.read(reader);
		// model.setPomFile(pom);
		// }
		// catch (IOException | XmlPullParserException e1)
		// {
		// }
		// MavenProject project = new MavenProject(model);

		/** Aether */
		// DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		// locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
		// locator.addService(TransporterFactory.class, FileTransporterFactory.class);
		// locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
		// RepositorySystem rs = locator.getService(RepositorySystem.class);
		//
		// DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		// LocalRepository localRepo = new LocalRepository("target/local-repo");
		// RepositorySystemSession rss = session.setLocalRepositoryManager(rs.newLocalRepositoryManager(session,
		// localRepo));
		//
		// Dependency dependency = new Dependency(new DefaultArtifact("org.apache.maven:maven-profile:2.2.1"), "compile");
		// RemoteRepository central = new RemoteRepository.Builder("central", "default", "http://repo1.maven.org/maven2/")
		// .build();
		//
		// CollectRequest collectRequest = new CollectRequest();
		// collectRequest.setRoot(dependency);
		// collectRequest.addRepository(central);
		// DependencyNode node = null;
		// try
		// {
		// node = rs.collectDependencies(session, collectRequest).getRoot();
		// }
		// catch (DependencyCollectionException e)
		// {
		// return;
		// }
		//
		// DependencyRequest dependencyRequest = new DependencyRequest();
		// dependencyRequest.setRoot(node);
		//
		// try
		// {
		// rs.resolveDependencies(session, dependencyRequest);
		// }
		// catch (DependencyResolutionException e)
		// {
		// // TODO Gérer l'exception DependencyResolutionException
		// }
		//
		// PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
		// node.accept(nlg);
		// System.out.println(nlg.getClassPath());

		// rs.resolveDependencies(rss, new )
		//
		// System.out.println("process pom " + pom.getAbsolutePath());
		//
		//
		//
		// DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		//
		// DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		//
		// // attention double fqn
		// RepositorySystem repoSystem = locator.getService(RepositorySystem.class);
		//
		// // repoSystem.resolve(new DefaultDependencyResolutionRequest(project, session));
		//
		// // MavenRepositorySystemSession
		// // DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();
		// try
		// {
		// session.setLocalRepositoryManager(new SimpleLocalRepositoryManagerFactory().newInstance(session,
		// new LocalRepository("c:\\pgih\\repository")));
		// }
		// catch (NoLocalRepositoryManagerException e1)
		// {
		// System.out.println(e1);
		// }
		//
		// DefaultProjectDependenciesResolver resolver = new DefaultProjectDependenciesResolver();
		// try
		// {
		// resolver.resolve(new DefaultDependencyResolutionRequest(project, session));
		// }
		// catch (DependencyResolutionException e)
		// {
		// System.out.println(e);
		// }
	}

	HashMap<String, GAV> gavs = new HashMap<>();

	private GAV ensureArtifact(String groupId, String artifactId, String version, DirectedGraph<GAV, Dep> g)
	{
		if (!groupId.startsWith("fr."))
			return null;

		String sig = groupId + ":" + artifactId + ":" + version;
		GAV gav = gavs.get(sig);

		if (gav == null)
		{
			gav = new GAV(groupId, artifactId, version);
			gavs.put(sig, gav);
			g.addVertex(gav);
		}

		return gav;
	}

	private MavenProject loadProject(File pom)
	{
		Model model = null;
		FileReader reader = null;
		MavenXpp3Reader mavenreader = new MavenXpp3Reader();
		try
		{
			reader = new FileReader(pom);
		}
		catch (FileNotFoundException e1)
		{
		}
		try
		{
			model = mavenreader.read(reader);
			model.setPomFile(pom);
		}
		catch (IOException | XmlPullParserException e1)
		{
		}
		MavenProject project = new MavenProject(model);
	
		return project;
	}
}
