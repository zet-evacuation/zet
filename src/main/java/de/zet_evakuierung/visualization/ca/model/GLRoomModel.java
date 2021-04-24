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

import de.zet_evakuierung.visualization.AbstractVisualizationModel;
import de.zet_evakuierung.visualization.VisualizationNodeModel;
import de.zet_evakuierung.visualization.ca.CellularAutomatonVisualizationProperties;
import io.visualization.CellularAutomatonVisualizationResults;
import org.zet.cellularautomaton.Room;

public class GLRoomModel extends AbstractVisualizationModel<CellularAutomatonVisualizationModel> implements VisualizationNodeModel {

    private final Room backingRoom;
    /**
     * Access to properties of the visualization run.
     */
    private final CellularAutomatonVisualizationProperties properties;
    private final double xPosition;
    private final double yPosition;

    public GLRoomModel(CellularAutomatonVisualizationResults caVisResults, Room room,
            CellularAutomatonVisualizationProperties properties,
            CellularAutomatonVisualizationModel visualizationModel) {
        super(visualizationModel);
        this.properties = properties;
        backingRoom = room;
        xPosition = caVisResults.get(room).x * properties.getScaling();
        yPosition = caVisResults.get(room).y * properties.getScaling();
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
        return backingRoom.getWidth() * properties.getScaling() * 400;
    }

    public double getHeight() {
        return -backingRoom.getHeight() * properties.getScaling() * 400;
    }

}
