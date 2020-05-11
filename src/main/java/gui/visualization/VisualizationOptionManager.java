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
package gui.visualization;

import ds.PropertyContainer;
import gui.visualization.control.ZETGLControl;
import java.awt.Color;
import org.zetool.opengl.drawingutils.GLColor;

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
        return PropertyContainer.getGlobal().getAsDouble("options.visualization.appearance.graphHeight");
    }

    //TODO: rename, as this is not visualization option but zet option!
    public static double getFloorDistance() {
        return PropertyContainer.getGlobal().getAsDouble("options.visualization.appearance.floorDistance") * 10;
    }

    public static double getIndividualHeight() { // the values in the xml-file are in cm, the computations in the z-format are done in mm
        return PropertyContainer.getGlobal().getAsDouble("options.visualization.appearance.individualHeight");
    }

    public static double getIndividualRadius() { // the values in the xml-file are in cm, the computations in the z-format are done in mm
        return PropertyContainer.getGlobal().getAsDouble("options.visualization.appearance.individualRadius");
    }

    public static double getWallHeight() { // the values in the xml-file are in cm, the computations in the z-format are done in mm
        return PropertyContainer.getGlobal().getAsDouble("options.visualization.appearance.wallHeight") * 10;
    }

    public static boolean animateMovements() {
        return true;
    }

    public static Color getBackground() {
        return PropertyContainer.getGlobal().getAsColor("options.visualization.appeareance.colors.backgroundColor");
    }

    public static GLColor getIndividualColor() {
        return new GLColor(PropertyContainer.getGlobal().getAsColor("options.visualization.appeareance.colors.individualColor"));
    }

    public static GLColor getCellFloorColor() {
        return new GLColor(PropertyContainer.getGlobal().getAsColor("options.visualization.appeareance.colors.floorColor"));
    }

    public static GLColor getSaveCellFloorColor() {
        return new GLColor(PropertyContainer.getGlobal().getAsColor("options.visualization.appeareance.colors.safeAreaColor"));
    }

    public static GLColor getEvacuationCellFloorColor() {
        return new GLColor(PropertyContainer.getGlobal().getAsColor("options.visualization.appeareance.colors.evacuationColor"));
    }

    public static double getNodeRadius() {
        return PropertyContainer.getGlobal().getAsDouble("options.visualization.appearance.nodeRadius");
    }

    public static GLColor getEvacuationNodeColor() {
        return new GLColor(PropertyContainer.getGlobal().getAsColor("options.visualization.appeareance.colors.evacuationColor"));
    }

    public static GLColor getSourceNodeColor() {
        return new GLColor(PropertyContainer.getGlobal().getAsColor("options.visualization.appeareance.colors.sourceColor"));
    }

    public static GLColor getDeletedSourceNodeColor() {
        return new GLColor(140, 0, 200);
    }

    public static GLColor getNodeBorderColor() {
        return new GLColor(Color.white);
    }

    public static GLColor getNodeColor() {
        //return new GLColor( 221, 125, 42 );	// color used for the test evacuation report
        return new GLColor(PropertyContainer.getGlobal().getAsColor("options.visualization.appeareance.colors.nodeColor"));
    }

    public static GLColor getEdgeColor() {
        //return new GLColor( 240, 240, 190, 140 ); // original zet-color
        //return new GLColor( 75, 77, 73, 255);
        return new GLColor(PropertyContainer.getGlobal().getAsColor("options.visualization.appeareance.colors.edgeColor"));
        //return new GLColor( Color.darkGray );
    }

    public static GLColor getFlowNodeColor() {
        return new GLColor(PropertyContainer.getGlobal().getAsColor("options.visualization.appeareance.colors.flowColor"));
    }

    public static GLColor getFlowUnitColor() {
        return getFlowNodeColor();
    }

    /**
     * Returns the color used for the front- and backside of the flow unit.
     *
     * @return the color used for the front- and backside of the flow unit
     */
    public static GLColor getFlowUnitEndColor() {
        GLColor flowColor = getFlowUnitColor();
        float r, g, b, a;
        r = (float) flowColor.getRed() * (float) 0.8;
        g = (float) flowColor.getGreen() * (float) 0.8;
        b = (float) flowColor.getBlue() * (float) 0.8;
        a = (float) flowColor.getAlpha() * (float) 0.8;
        GLColor result = new GLColor(r, g, b, a);
        return result;
    }

    public static GLColor getStairCellFloorColor() {
        return new GLColor(PropertyContainer.getGlobal().getAsColor("options.visualization.appeareance.colors.stairAreaColor"));
    }

    public static GLColor getDelayCellFloorColor() {
        return new GLColor(PropertyContainer.getGlobal().getAsColor("options.visualization.appeareance.colors.delayAreaColor"));
    }

    public static GLColor getCellWallColor() {
        return new GLColor(PropertyContainer.getGlobal().getAsColor("options.visualization.appeareance.colors.wallColor"));
    }

    /**
     * Sets the color that is used to display a color gradient for some cell status. The low color is the color used if
     * the value is zero.
     *
     * @param cid the status type
     * @return the low color for the given status
     */
    public static GLColor getCellInformationLowColor(ZETGLControl.CellInformationDisplay cid) {
        switch (cid) {
            case DynamicPotential:
                return getCellFloorColor();
            case StaticPotential:
                return new GLColor(Color.white);
            case Utilization:
                return getCellFloorColor();
            case Waiting:
                return new GLColor(Color.green);
            default:
                return getCellFloorColor();
        }
    }

    /**
     * Sets the color that is used to display a color gradient for some cell status. The high color is the color used if
     * the value is maximal.
     *
     * @param cid the status type
     * @return the low color for the given status
     */
    public static GLColor getCellInformationHighColor(ZETGLControl.CellInformationDisplay cid) {
        switch (cid) {
            case DynamicPotential:
                return new GLColor(Color.red);
            case StaticPotential:
                return new GLColor(Color.blue);
            case Utilization:
                return new GLColor(Color.orange);
            case Waiting:
                return new GLColor(Color.red);
            default:
                return getCellFloorColor();
        }
    }

    public static GLColor getInvalidPotentialColor() {
        return new GLColor(130, 55, 101);
    }

    public static boolean getAlwaysDisplayCellType() {
        return true;
    }

    /**
     * Decides wheather a little gap between the cells is visible, or not. The gaps form a grid on the cells. Uses the
     * value set in the {@link PropertyContainer} by the "options.visualization.view.grid"-value in zoptions.xml.
     *
     * @return true if the grid is visible, otherwise false
     */
    public static boolean showSpaceBetweenCells() {
        //return PropertyContainer.getGlobal().getAsBoolean( "options.visualization.view.grid" );
        return false;
    }

    /**
     * Decides wheather the cells of the cellular automaton are smoothed, or not. If they are smoothed the colors of a
     * cell is a mixture of the neighbour cells. Uses the value set in the {@link PropertyContainer} by the
     * "options.visualization.view.smooth"-value in zoptions.xml.
     *
     * @return true if the cells are drawn smooth, false otherwise
     */
    public static boolean smoothCellVisualization() {
        return PropertyContainer.getGlobal().getAsBoolean("options.visualization.view.smooth");
    }

    public static GLColor getCellSeperationColor() {
        return new GLColor(Color.lightGray);
    }

    /**
     * Returns the currenly active quality preset.
     *
     * @return the currenly active quality preset
     */
    public static QualityPreset getQualityPreset() {
        return QualityPreset.MediumQuality;
        //return PropertyContainer.getGlobal().getAs("options.visualization.qualitySetting", QualityPreset.class);
    }
}
