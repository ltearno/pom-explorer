package fr.lteconsulting.pomexplorer;

import java.io.File;
import java.util.List;

import fr.lteconsulting.pomexplorer.model.Gav;
import fr.lteconsulting.pomexplorer.model.transitivity.Repository;

/**
 * During the batch analysis, it may be required to
 * fecth additional projects (to resolve parent chains
 * and bom imports).
 * 
 * <p>
 * In such case, this interface allows the PomAnalysis
 * object to query missing pom files.
 */
public interface PomFileLoader
{
	File loadPomFileForGav( Gav gav, List<Repository> additionalRepos, Log log );
}