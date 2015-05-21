package fr.lteconsulting.pomexplorer;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.task.AddScopedDependenciesTask;
import org.jboss.shrinkwrap.resolver.impl.maven.task.ConfigureSettingsFromFileTask;

public class Toto
{
	public static void toto(File pom)
	{
		MavenWorkingSession mm = new MavenWorkingSessionImpl();
		mm = new ConfigureSettingsFromFileTask(new File("C:\\pgih\\maven\\3.2.1\\conf\\settings.xml")).execute(mm);

		mm = new AddScopedDependenciesTask(ScopeType.COMPILE, ScopeType.IMPORT, ScopeType.SYSTEM, ScopeType.RUNTIME)
				.execute(mm);

		try
		{
			mm.loadPomFromFile(pom);
			ParsedPomFile pompom = mm.getParsedPomFile();

			System.out.println(pompom.getGroupId() + ":" + pompom.getArtifactId() + ":" + pompom.getVersion() + ":"
					+ pompom.getPackagingType().getId() + ":" + pompom.getPackagingType().getExtension() + ":"
					+ pompom.getPackagingType().getClassifier());
			for (MavenDependency dep : pompom.getDependencies())
			{
				System.out.println("   " + dep.getGroupId() + ":" + dep.getArtifactId() + ":" + dep.getVersion() + ":"
						+ dep.getPackaging() + ":" + dep.getClassifier() + ":" + dep.getScope());
			}
		}
		catch (InvalidConfigurationFileException e)
		{
			System.out.println("Cannot load this pom file : " + pom.getAbsolutePath());
		}
	}
}
