/**
 * Class BatchLoadProperties
 * Erstellt 23.07.2008, 11:09:44
 */

package batch.tasks;

import ds.PropertyContainer;
import gui.editor.properties.PropertyTreeModel;
import java.io.File;

/**
 * Loads some properties from a property-file into the
 * {@link ds.PropertyContainer}.
 * @author Jan-Philipp Kappmeier
 */
public class BatchLoadProperties implements Runnable {
	/** The property-file */
	private File propertyFile;
	/** Indicates whether some information are loaded static and not from the given file*/
	private boolean loadStatic;
	/** Static parameter for the max time in seconds of the cellular automaton */
	private double caMaxTime;
	
	/**
	 * Creates a new instance of this task which does not load any static information
	 * into the {@link ds.PropertyContainer}.
	 * @param propertyFile the property file
	 */
	public BatchLoadProperties( File propertyFile ) {
		loadStatic = false;
		this.propertyFile = propertyFile;
	}

	/**
	 * Creates a new instance with some additional static information that is loded
	 * into the {@link ds.PropertyContainer}
	 * @param propertyFile the property file
	 * @param caMaxTime the maximal execution time in seconds for the ca
	 */
	public BatchLoadProperties( File propertyFile, double caMaxTime ) {
		loadStatic = true;
		this.propertyFile = propertyFile;
		this.caMaxTime = caMaxTime;
	}

	/**
	 * Executes the task and loads the properties.
	 */
	public void run() {
		if( propertyFile != null ) {
			try {
			PropertyTreeModel propertyTreeModel = PropertyContainer.loadConfigFile( propertyFile );
			PropertyContainer.getInstance().applyParameters( propertyTreeModel );
			} catch (Exception eee) {}
		}
		if( loadStatic ) {
			PropertyContainer.getInstance().set( "algo.ca.maxTime", caMaxTime );
		}
	}
}
