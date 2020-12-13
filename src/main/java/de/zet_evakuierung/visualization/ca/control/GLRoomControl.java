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
package de.zet_evakuierung.visualization.ca.control;

import java.util.HashMap;

import de.zet_evakuierung.visualization.ca.draw.GLRoom;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.control.ZETGLControl.CellInformationDisplay;
import io.visualization.CellularAutomatonVisualizationResults;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Room;

public class GLRoomControl extends AbstractZETVisualizationControl<GLCellControl, GLRoom, CellularAutomatonVisualizationModel> {

    private final HashMap<org.zet.cellularautomaton.EvacCellInterface, GLCellControl> cellControls = new HashMap<>();
    private final GLCAFloorControl glCAFloorControlObject;  // the corresponding GLCAFloorControl of this object
    private final double xPosition;
    private final double yPosition;
    Room controlled;

    public GLRoomControl(CellularAutomatonVisualizationResults caVisResults, Room room, GLCAFloorControl glCAFloorControl, CellularAutomatonVisualizationModel visualizationModel) {
        super(visualizationModel);
        controlled = room;
        xPosition = caVisResults.get(room).x * visualizationModel.scaling;
        yPosition = caVisResults.get(room).y * visualizationModel.scaling;
        this.glCAFloorControlObject = glCAFloorControl;

        for (EvacCell cell : room.getAllCells()) {
            GLCellControl cellControl = new GLCellControl(caVisResults, cell, this, visualizationModel);
            cellControls.put(cell, cellControl);
            add(cellControl);
    }
        this.setView(new GLRoom(this));
        for (GLCellControl cell : this) {
            view.addChild(cell.getView());
        }
    }

    /**
     * Returns the offset of this room. The offset is in real (z-format) coordinates
     *
     * @return the y offset
     */
    public double getXPosition() {
        return xPosition;
    }

    /**
     * Returns the offset of this room. The offset is in real (z-format) coordinates
     *
     * @return the x offset
     */
    public double getYPosition() {
        return -yPosition;
    }

    GLCellControl getCellControl(EvacCellInterface cell) {
        return cellControls.get(cell);
    }

    public double getWidth() {
        return controlled.getWidth() * visualizationModel.scaling * 400;
    }

    public double getHeight() {
        return controlled.getHeight() * visualizationModel.scaling * 400;
    }

    /**
     * Returns the corresponding GLCAFloorControl-Object which created this GLRoomControl Object.
     *
     * @return The corresponding GLCAFloorControl-Object which created this GLRoomControl Object.
     */
    public GLCAFloorControl getGLCAFloorControl() {
        return this.glCAFloorControlObject;
    }

    void setPotentialDisplay(CellInformationDisplay potentialDisplay) {
        for (GLCellControl cellControl : cellControls.values()) {
            cellControl.setPotentialDisplay(potentialDisplay);
}
    }
}
