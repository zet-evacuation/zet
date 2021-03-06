/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package gui;

import ds.PropertyContainer;
import org.zetool.components.property.PropertyLoadException;
import gui.propertysheet.PropertyTreeModel;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import org.zetool.components.property.PropertyTreeModelLoader;

/**
 * The class {@codeZETProperties} is a central point to ask for properties used by the program. It hides calls
 * to the {@link PropertyContainer}.
 * @author Jan-Philipp Kappmeier
 */
public final class ZETProperties {
	
	/*****************************************************************************
	 *                                                                           *
	 * General properties used everywhere                                        *
	 *                                                                           *
	 ****************************************************************************/
	
	/** The property file that is loaded when the program starts. Can be changed via command line. */
	private static Path currentPropertyFile;
	private static PropertyTreeModel currentPropertyTreeModel;

	public static PropertyTreeModel getCurrentPropertyTreeModel() {
		return currentPropertyTreeModel;
	}

	public static Path getCurrentPropertyFile() {
		return currentPropertyFile;
	}

	public static void setCurrentProperty( Path path ) throws PropertyLoadException, FileNotFoundException {
            PropertyTreeModelLoader loader = new PropertyTreeModelLoader();
		currentPropertyTreeModel = loader.applyParameters( new FileReader(path.toFile()), PropertyContainer.getGlobal());
		currentPropertyFile = path;
	}
	

	/*****************************************************************************
	 *                                                                           *
	 * Properties used by the editor                                             *
	 *                                                                           *
	 ****************************************************************************/
	
	/**
	 * Creates a new instance of {@code ZETProperties}. Hidden constructor as this is a static utility class.
	 */
	private ZETProperties() { }

	/**
	 * Returns {@code true} if the default floor in the editor is hidden. {@code false} otherwise.
	 * @return {@code true} if the default floor in the editor is hidden. {@code false} otherwise
	 */
	public static boolean isDefaultFloorHidden() {
		return PropertyContainer.getGlobal().getAsBoolean( "editor.options.view.hideDefaultFloor" );
	}

	/**
	 * Hides and shows the default flow in the editor.
	 * @param defaultFloorHidden the status of the floor visibility
	 */
	public static void setDefaultFloorHidden( boolean defaultFloorHidden ) {
		PropertyContainer.getGlobal().set( "editor.options.view.hideDefaultFloor", defaultFloorHidden );
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
	public static boolean isShowEye() {
		return PropertyContainer.getGlobal().getAsBoolean( "options.visualization.elements.eye" );
	}
	
	/**
	 * Hides and shows the location of the camera (the eye) in the 2d-view of visualization
	 * @param showEye should the eye be hidden or visible
	 */
	public static void setShowEye( boolean showEye ) {
		PropertyContainer.getGlobal().set( "options.visualization.elements.eye", showEye );
	}
	
	/**
	 * Indicates weather the current frame rate is drawn on the lower left edge during visualization.
	 * @return {@code true} if the current frame rate is drawn on the lower left edge, {@code false} otherwise
	 */
	public static boolean isShowFPS() {
		return PropertyContainer.getGlobal().getAsBoolean( "options.visualization.elements.fps" );
	}
	
	/**
	 * Hides and shows the frame rate during visualization.
	 * @param showFPS the value of the visibility of the frame rate
	 */
	public static void setShowFPS( boolean showFPS ) {
		PropertyContainer.getGlobal().set( "options.visualization.elements.fps", showFPS );
	}
	
	/**
	 * Indicates weather the current time step of the graph is drawn during visualization.
	 * @return {@code true} if the current time step of the graph is drawn, {@code false} otherwise
	 */
	public static boolean isShowTimestepGraph() {
		return PropertyContainer.getGlobal().getAsBoolean( "options.visualization.elements.timestepGraph" );
	}
	
	/**
	 * Hides and shows the time step of the graph during visualization
	 * @param showTimestepGraph decides if the time step should be visible
	 */
	public static void setShowTimestepGraph( boolean showTimestepGraph ) {
		PropertyContainer.getGlobal().set( "options.visualization.elements.eye", showTimestepGraph );
	}

	/**
	 * Indicates weather the current time step of the cellular automaton is drawn during visualization.
	 * @return {@code true} if the current time step of the cellular automaton is drawn, {@code false} otherwise
	 */
	public static boolean isShowTimestepCellularAutomaton() {
		return PropertyContainer.getGlobal().getAsBoolean( "options.visualization.elements.timestepCA" );
	}
	
	/**
	 * Hides and shows the time step of the cellular automaton during visualization.
	 * @param showTimestepCellularAutomaton decides if the time step should be visible
	 */
	public static void setShowTimestepCellularAutomaton( boolean showTimestepCellularAutomaton ) {
		PropertyContainer.getGlobal().set( "options.visualization.elements.eye", showTimestepCellularAutomaton );
	}

	/**
	 * Returns the raster size for the helping raster on the background of any floor.
	 * @return the raster size for the helping raster on the background of any floor
	 */public static int getRasterSize() {
		return PropertyContainer.getGlobal().getAsInt( "editor.options.view.editRasterSize" );
	}

	 /**
		* Returns the size of an smaller helping raster visible on the background of
		* any floor. This raster is only visible, if its size is a divisor of the
		* larger one and strictly smaller.
		* @return the size of an smaller helping raster
		*/
	public static int getRasterSizeSmall() {
		return PropertyContainer.getGlobal().getAsInt( "editor.options.view.editSmallRasterSize" );
	}

	/**
	 * Returns the size of the raster (used to snap the cursor) of the editor.
	 * This does not influence the rasterization to a cellular automaton.
	 * @return the current raster size (as stored in the property container)
	 */
	public static int getRasterSizeSnap() {
		return PropertyContainer.getGlobal().getAsInt( "editor.options.view.editRasterSizeSnap" );
	}
//public static void setRasterSize( int rasterSize ) {
	//	PropertyContainer.getGlobal().set( "editor.options.view.editRasterSize", rasterSize );
	//}

	public static int getDefaultFloorSizeMinX() {
		return PropertyContainer.getGlobal().getAsInt( "editor.options.view.size.minx" );
	}

	public static int getDefaultFloorSizeMaxX() {
		return PropertyContainer.getGlobal().getAsInt( "editor.options.view.size.maxx" );
	}

	public static int getDefaultFloorSizeMinY() {
		return PropertyContainer.getGlobal().getAsInt( "editor.options.view.size.miny" );
	}

	public static int getDefaultFloorSizeMaxY() {
		return PropertyContainer.getGlobal().getAsInt( "editor.options.view.size.maxy" );
	}

}
