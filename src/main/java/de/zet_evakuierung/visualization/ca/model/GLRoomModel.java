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

import de.zet_evakuierung.visualization.VisualizationModel;
import de.zet_evakuierung.visualization.VisualizationNodeModel;
import io.visualization.CellularAutomatonVisualizationResults;
import org.zet.cellularautomaton.Room;

public class GLRoomModel extends VisualizationModel<CellularAutomatonVisualizationModel> implements VisualizationNodeModel {

    private final double xPosition;
    private final double yPosition;
    private final Room backingRoom;

    public GLRoomModel(CellularAutomatonVisualizationResults caVisResults, Room room,
            CellularAutomatonVisualizationModel visualizationModel) {
        super(visualizationModel);
        backingRoom = room;
        xPosition = caVisResults.get(room).x * visualizationModel.scaling;
        yPosition = caVisResults.get(room).y * visualizationModel.scaling;
    }

    /**
     * Returns the offset of this room. The offset is in real (z-format) coordinates
     *
     * @return the y offset
     */
    @Override
    public double getXPosition() {
        return xPosition;
    }

    /**
     * Returns the offset of this room. The offset is in real (z-format) coordinates
     *
     * @return the x offset
     */
    @Override
    public double getYPosition() {
        return -yPosition;
    }

    public double getWidth() {
        return backingRoom.getWidth() * visualizationModel.scaling * 400;
    }

    public double getHeight() {
        return -backingRoom.getHeight() * visualizationModel.scaling * 400;
    }

}
