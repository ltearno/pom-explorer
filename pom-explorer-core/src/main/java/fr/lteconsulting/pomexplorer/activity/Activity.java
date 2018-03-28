package fr.lteconsulting.pomexplorer.activity;

/**
 * An {@link Activity} is an object with private data which are accessed only
 * during scheduled processing. It implements an interface with wich it has a
 * corresponding message queue. All calls are processed asynchronously.
 * 
 * <p>
 * So all private data are free from lock, as long as they are not leaked outside of course !
 * 
 * <p>
 * Methods marked with the @Direct annotation are called directly from the calling thread
 */
//FIXME either garbage or not complete yet
//FIXME see javadoc "Methods marked with the @Direct annotation" -> @Direct does not exist
public class Activity
{
	/**
	 * Maybe it feeds fork join task pool for each message
	 */
}
