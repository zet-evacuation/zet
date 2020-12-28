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
package de.zet_evakuierung.visualization.ca.model;

import java.util.List;

import de.zet_evakuierung.visualization.VisualizationNodeModel;
import de.zet_evakuierung.visualization.ca.draw.GLCAFloor;
import gui.visualization.control.AbstractZETVisualizationControl;
import io.visualization.CellularAutomatonVisualizationResults;

public class GLFloorModel extends AbstractZETVisualizationControl<Void, GLCAFloor, CellularAutomatonVisualizationModel>
        implements VisualizationNodeModel {

    private double xPosition = 0.0d;
    private double yPosition = 0.0d;

    private int floorNumber = 0;

    public GLFloorModel(CellularAutomatonVisualizationResults caVisResults, int floorID, CellularAutomatonVisualizationModel visualizationModel) {
        super(visualizationModel);

        xPosition = caVisResults.get(floorID).x;
        yPosition = caVisResults.get(floorID).y;

        this.floorNumber = floorID;
    }

    /**
     * Returns the offset of this floor. The offset is in real (z-format) coordinates
     *
     * @return the y offset
     */
    @Override
    public double getXPosition() {
        return xPosition;
    }

    /**
     * Returns the offset of this floor. The offset is in real (z-format) coordinates
     *
     * @return the x offset
     */
    @Override
    public double getYPosition() {
        return -yPosition;
    }

    /**
     * Returns the floor number of the floor. This corresponds to the level, the lowest floor is identified by 0.
     *
     * @return the floor number
     */
    public int getFloorNumber() {
        return floorNumber;
    }

    public List<GLIndividualModel> getIndividualControls() {
        return visualizationModel.getIndividualControls();
    }

    public double getZPosition() {
        return floorNumber * visualizationModel.defaultFloorHeight * visualizationModel.scaling;
    }
}
