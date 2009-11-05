/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/**
 * Class VisualizationOptionManager
 * Created 08.05.2008, 02:27:00
 */

package gui.visualization;

import ds.PropertyContainer;
import gui.visualization.control.GLControl;
import java.awt.Color;
import opengl.drawingutils.GLColor;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class VisualizationOptionManager {

	/**
	 * Utility class hidden private constructor
	 */
	private VisualizationOptionManager() {
	}

	public static double getGraphHeight() {
		return PropertyContainer.getInstance().getAsDouble( "options.visualization.appearance.graphHeight" );
	}

	public static double getFloorDistance() {
		return PropertyContainer.getInstance().getAsDouble( "options.visualization.appearance.floorDistance" );
	}

	public static double getIndividualHeight() {
		return PropertyContainer.getInstance().getAsDouble( "options.visualization.appearance.individualHeight" );
	}

	public static double getIndividualRadius() {
		return PropertyContainer.getInstance().getAsDouble( "options.visualization.appearance.individualRadius" );
	}

	public static double getWallHeight() {
		return PropertyContainer.getInstance().getAsDouble( "options.visualization.appearance.wallHeight" );
	}

	public static GLColor getIndividualColor() {
		return new GLColor( 0, 108, 255 );
	//return new GLColor( Color.yellow );
	}

	public static boolean animateMovements() {
		return true;
	}

	public static GLColor getCellFloorColor() {
		return new GLColor( Color.darkGray );
	//return new GLColor( 200, 200, 200 );
	}

	public static GLColor getSaveCellFloorColor() {
		return new GLColor( Color.yellow );
	}

	public static GLColor getEvacuationCellFloorColor() {
		return new GLColor( Color.green );
	}

	public static GLColor getEvacuationNodeColor() {
		return new GLColor( 10, 170, 80, 255 );
	}

	public static GLColor getSourceNodeColor() {
		return new GLColor( 0, 6, 255 );
	}

	public static GLColor getDeletedSourceNodeColor() {
		return new GLColor( 140, 0, 200 );
	}

	public static GLColor getNodeBorderColor() {
		return new GLColor( Color.white );
	}

	public static GLColor getNodeColor() {
//		return new GLColor( 240, 240, 110 );
		return new GLColor( 250, 250, 100 );
	//return new GLColor( Color.gray, 1.0 );
	//return new GLColor( Color.yellow, 0.3 );
	}

	public static GLColor getEdgeColor() {
		return new GLColor( 240, 240, 190, 140 );
	//return new GLColor( Color.yellow, 0.3 );                
	}

	public static GLColor getFlowNodeColor() {
		return new GLColor( 130, 185, 255 );
	//return new GLColor( Color.red );
	}

	public static GLColor getFlowUnitEndColor() {
		GLColor flowColor = getFlowNodeColor();
		float r, g, b, a;
		r = (float) flowColor.getRed() * (float) 0.8;
		g = (float) flowColor.getGreen() * (float) 0.8;
		b = (float) flowColor.getBlue() * (float) 0.8;
		a = (float) flowColor.getAlpha() * (float) 0.8;
		GLColor result = new GLColor( r, g, b, a );
		return result;
	}

	public static GLColor getStairCellFloorColor() {
		return new GLColor( Color.pink );
	}

	public static GLColor getDelayCellFloorColor() {
		return new GLColor( Color.red );
	}

	public static GLColor getCellWallColor() {
		return new GLColor( Color.lightGray );
	}

	/**
	 * Sets the color that is used to display a color gradient for some cell status.
	 * The low color is the color used if the value is zero.
	 * @param cid the status type
	 * @return the low color for the given status
	 */
	public static GLColor getCellInformationLowColor( GLControl.CellInformationDisplay cid ) {
		switch( cid ) {
			case DYNAMIC_POTENTIAL:
				return getCellFloorColor();
			case STATIC_POTENTIAL:
				return new GLColor( Color.white );
			case UTILIZATION:
				return getCellFloorColor();
			case WAITING:
				return new GLColor( Color.green );
			default:
				return getCellFloorColor();
		}
	}

	/**
	 * Sets the color that is used to display a color gradient for some cell status.
	 * The high color is the color used if the value is maximal.
	 * @param cid the status type
	 * @return the low color for the given status
	 */
	public static GLColor getCellInformationHighColor( GLControl.CellInformationDisplay cid ) {
		switch( cid ) {
			case DYNAMIC_POTENTIAL:
				return new GLColor( Color.red );
			case STATIC_POTENTIAL:
				return new GLColor( Color.blue );
			case UTILIZATION:
				return new GLColor( Color.orange );
			case WAITING:
				return new GLColor( Color.red );
			default:
				return getCellFloorColor();
		}
	}

	public static GLColor getInvalidPotentialColor() {
		return new GLColor( 130, 55, 101 );
	}

	public static boolean getAlwaysDisplayCellType() {
		return true;
	}
	
	/**
	 * Decides wheather a little gap between the cells is visible, or not. The
	 * gaps form a grid on the cells. Uses the value set in the {@link PropertyContainer}
	 * by the "options.visualization.view.grid"-value in zoptions.xml.
	 * @return true if the grid is visible, otherwise false
	 */
	public static boolean showSpaceBetweenCells() {
		return PropertyContainer.getInstance().getAsBoolean( "options.visualization.view.grid" );
	}
	
	/**
	 * Decides wheather the cells of the cellular automaton are smoothed, or not.
	 * If they are smoothed the colors of a cell is a mixture of the neighbour cells.
	 * Uses the value set in the {@link PropertyContainer}
	 * by the "options.visualization.view.smooth"-value in zoptions.xml.
	 * @return true if the cells are drawn smooth, false otherwise
	 */
	public static boolean smoothCellVisualization() {
		return PropertyContainer.getInstance().getAsBoolean( "options.visualization.view.smooth" );
	}
	public static GLColor getCellSeperationColor() {
		return new GLColor( Color.black );
	}

	public static QualityPreset getQualityPreset() {
		return QualityPreset.VeryHighQuality;
	}
}
