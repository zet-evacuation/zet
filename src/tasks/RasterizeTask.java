/*
 * RasterizeTask.java
 * Created on 24.01.2008, 21:02:06
 */

package tasks;

import ds.Project;
import gui.JEditor;

/**
 * Performs the rasterization of a {@link ds.z.BuildingPlan}.
 * @author Jan-Philipp Kappmeier
 */
public class RasterizeTask implements Runnable {
	/** The project that should be rastered. */
	private Project project;
	
	/** Creates a new instance of the rasterization task. */
	public RasterizeTask( Project p ) {
		if( p == null ) {
			throw new java.lang.IllegalArgumentException ("Project is null.");
		}
		project = p;
	}
	
	/** Performs rasterization. */
	public void run() {
		try {
			project.getPlan().rasterize();
		} catch( Exception ex ) {
			JEditor.sendError( ex.getLocalizedMessage () );
		}
	}	
}
