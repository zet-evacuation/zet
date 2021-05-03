/*
 * zet evacuation tool copyright © 2007-20 zet evacuation team
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
package de.zet_evakuierung.visualization.ca.draw;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import de.zet_evakuierung.visualization.ca.CellularAutomatonVisualizationProperties;
import de.zet_evakuierung.visualization.ca.model.CellularAutomatonVisualizationModel;
import de.zet_evakuierung.visualization.ca.model.CellularAutomatonVisualizationModelContainer;
import de.zet_evakuierung.visualization.ca.model.GLCellModel;
import de.zet_evakuierung.visualization.ca.model.GLFloorModel;
import de.zet_evakuierung.visualization.ca.model.GLRoomModel;
import de.zet_evakuierung.visualization.ca.model.GLRootModel;
import org.zet.cellularautomaton.DoorCell;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.SaveCell;
import org.zet.cellularautomaton.StairCell;
import org.zet.cellularautomaton.TeleportCell;
import org.zetool.common.util.Direction8;
import org.zetool.opengl.drawingutils.GLColor;

/**
 * Container creating and giving Access to the created OpenGL visualization view objects.
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLCellularAutomatonViews {

    private final Map<GLFloorModel, GLCAFloor> floorViews;
    private final Map<GLCellModel, GLCell> cellViews;
    private final GLCA rootView;

    private GLCellularAutomatonViews(GLCellularAutomatonViewFactory builder) {
        floorViews = builder.floorViews;
        cellViews = builder.cellViews;
        rootView = builder.rootView;
    }

    /**
     * Returns the {@link GLCA root view} for the {@link GLRootModel model root} of the visualization hierarchy.
     *
     * @return the root view object
     */
    public GLCA getView() {
        return rootView;
    }

    public GLCAFloor getView(GLFloorModel model) {
        return floorViews.get(model);
    }

    public static GLCellularAutomatonViews createInstance(CellularAutomatonVisualizationModel visualizationModel,
            CellularAutomatonVisualizationProperties properties, MultiFloorEvacuationCellularAutomaton cellularAutomaton,
            CellularAutomatonVisualizationModelContainer cellularAutomatonModel) {
        GLCellularAutomatonViewFactory factory = new GLCellularAutomatonViewFactory(visualizationModel, properties,
                cellularAutomaton, cellularAutomatonModel);
        factory.createViews();
        return new GLCellularAutomatonViews(factory);
    }

    public Iterable<GLCell> cellViews() {
        return cellViews.values();
    }

    /**
     * Utility factory class creating the view instances.
     */
    private static class GLCellularAutomatonViewFactory {

        private final CellularAutomatonVisualizationModel visualizationModel;
        /**
         * Properties for the visualization.
         */
        private final CellularAutomatonVisualizationProperties properties;
        private final MultiFloorEvacuationCellularAutomaton cellularAutomaton;
        private final CellularAutomatonVisualizationModelContainer cellularAutomatonModel;
        private Map<GLFloorModel, GLCAFloor> floorViews;
        private Map<GLCellModel, GLCell> cellViews;
        private GLCA rootView;

        GLCellularAutomatonViewFactory(CellularAutomatonVisualizationModel visualizationModel,
                CellularAutomatonVisualizationProperties properties,
                MultiFloorEvacuationCellularAutomaton cellularAutomaton,
                CellularAutomatonVisualizationModelContainer cellularAutomatonModel) {
            this.visualizationModel = visualizationModel;
            this.properties = properties;
            this.cellularAutomaton = cellularAutomaton;
            this.cellularAutomatonModel = cellularAutomatonModel;
        }

        void createViews() {
            this.floorViews = new HashMap<>(cellularAutomatonModel.getFloorCount());
            this.cellViews = new HashMap<>(visualizationModel.getCellCount());

            // Set this view.
            this.rootView = new GLCA();

            // Create the view hierarchy
            for (int i = 0; i < cellularAutomatonModel.getFloorCount(); ++i) {
                GLFloorModel floorModel = cellularAutomatonModel.getFloorModel(i);
                GLCAFloor floorView = new GLCAFloor(floorModel, visualizationModel.getIndividuals());
                floorViews.put(floorModel, floorView);
                rootView.addChild(floorView);
                createRoomViews(i, floorView);
            }
        }

        private void createRoomViews(int floorId, GLCAFloor parentFloor) {
            for (Room room : cellularAutomaton.getRoomsOnFloor(floorId)) {
                GLRoomModel roomModel = cellularAutomatonModel.getRoomModel(room);
                GLRoom roomView = new GLRoom(roomModel, properties);
                parentFloor.addChild(roomView);
                createCellViews(room, roomView);
            }
        }

        private void createCellViews(Room roomModel, GLRoom parentRoom) {
            for (EvacCell cell : roomModel.getAllCells()) {
                GLCellModel cellModel = cellularAutomatonModel.getCellModel(cell);
                GLCell cellView = createCell(cell, cellModel);
                cellViews.put(cellModel, cellView);
                parentRoom.addChild(cellView);
            }
        }

        private GLCell createCell(EvacCell cell, GLCellModel cellModel) {
            Function<Direction8, GLColor> neighborColor = (Direction8 direction) -> {
                EvacCellInterface neighborCell = cell.getNeighbor(direction);
                GLCellModel cellModel1 = cellularAutomatonModel.getCellModel(neighborCell);
                GLCell cellView = cellViews.get(cellModel1);
                return cellView.getColor();
            };

            if (cell instanceof DoorCell || cell instanceof RoomCell) {
                if (cell.getSpeedFactor() == RoomCell.STANDARD_ROOMCELL_SPEEDFACTOR) {
                    return new GLCell(cellModel, properties, neighborColor);
                } else {
                    return new GLDelayCell(cellModel, properties, neighborColor);
                }
            } else if (cell instanceof ExitCell) {
                return new GLEvacuationCell(cellModel, properties, neighborColor);
            } else if (cell instanceof SaveCell) {
                return new GLSaveCell(cellModel, properties, neighborColor);
            } else if (cell instanceof StairCell) {
                return new GLStairCell(cellModel, properties, neighborColor);
            } else if (cell instanceof TeleportCell) {
                return new GLSaveCell(cellModel, properties, neighborColor);
            } else {
                throw new java.lang.IllegalStateException("Illegal Cell Type");
            }
        }
    }
}
