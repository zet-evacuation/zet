/*
 * ZETProperties.java
 * Created 09.09.2009, 14:56:29
 */

package gui;

import ds.PropertyContainer;

/**
 * The class <code>ZETProperties</code> is a central point to ask for properties used by the program. It hides calls
 * to the {@link PropertyContainer}. The setters automatically send messages that allow listerners to update themselves.
 * @author Jan-Philipp Kappmeier
 */
public final class ZETProperties {

	/*****************************************************************************
	 *                                                                           *
	 * Properties used by the editor                                             *
	 *                                                                           *
	 ****************************************************************************/
	
	/**
	 * Creates a new instance of <code>ZETProperties</code>. Hidden constructor as this is a static utility class.
	 */
	private ZETProperties() { }

	/**
	 * Returns {@code true} if the default floor in the editor is hidden. {@code false} otherwise.
	 * @return {@code true} if the default floor in the editor is hidden. {@code false} otherwise
	 */
	public final static boolean isDefaultFloorHidden() {
		return PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" );
	}

	/**
	 * Hides and shows the default flow in the editor.
	 * @param defaultFloorHidden the status of the floor visiblity
	 */
	public final static void setDefaultFloorHidden( boolean defaultFloorHidden ) {
		PropertyContainer.getInstance().set( "editor.options.view.hideDefaultFloor", defaultFloorHidden );
	}
	
	/*****************************************************************************
	 *                                                                           *
	 * Properties used by the visualization                                      *
	 *                                                                           *
	 ****************************************************************************/
	
	/**
	 * Returns {@code true} if the location of the camera (the eye) is visible in 2d-view of visualization.
	 * @return {@code true} if the location of the camera (the eye) is visible in 2d-view of visualization
	 */
	public final static boolean isShowEye() {
		return PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.eye" );
	}
	
	/**
	 * Hides and shows the location of the camera (the eye) in the 2d-view of visualization
	 * @param showEye should the eye be hidden or visible
	 */
	public final static void setShowEye( boolean showEye ) {
		PropertyContainer.getInstance().set( "options.visualization.elements.eye", showEye );
	}
	
	/**
	 * Indicates weather the current framerate is drawn on the lower left edge during visualization. 
	 * @return {@code true} if the current framerate is drawn on the lower left edge, {@code false} otherwise
	 */
	public final static boolean isShowFPS() {
		return PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.fps" );
	}
	
	/**
	 * Hides and shows the framerate during visualization.
	 * @param showFPS the value of the visibility of the framerate 
	 */
	public final static void setShowFPS( boolean showFPS ) {
		PropertyContainer.getInstance().set( "options.visualization.elements.fps", showFPS );
	}
	
	/**
	 * Indicates weather the current timestep of the graph is drawn during visualization.
	 * @return {@code true} if the current timestep of the graph is drawn, {@code false} otherwise
	 */
	public final static boolean isShowTimestepGraph() {
		return PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.timestepGraph" );
	}
	
	/**
	 * Hides and shows the timestep of the graph during visualization
	 * @param showTimestepGraph decides if the timestep should be visible
	 */
	public final static void setShowTimestepGraph( boolean showTimestepGraph ) {
		PropertyContainer.getInstance().set( "options.visualization.elements.eye", showTimestepGraph );
	}

	/**
	 * Indicates weather the current timestep of the cellular automaton is drawn during visualization.
	 * @return {@code true} if the current timestep of the cellular automaton is drawn, {@code false} otherwise
	 */
	public final static boolean isShowTimestepCellularAutomaton() {
		return PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.timestepCA" );
	}
	
	/**
	 * Hides and shows the timestep of the cellular automaton during visualization.
	 * @param showTimestepGraph decides if the timestep should be visible
	 */
	public final static void setShowTimestepCellularAutomaton( boolean showTimestepCellularAutomaton ) {
		PropertyContainer.getInstance().set( "options.visualization.elements.eye", showTimestepCellularAutomaton );
	}
}
