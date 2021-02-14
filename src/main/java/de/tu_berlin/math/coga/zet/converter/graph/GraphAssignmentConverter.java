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
package de.tu_berlin.math.coga.zet.converter.graph;

import java.util.HashMap;
import java.util.List;

import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.model.Person;
import de.zet_evakuierung.model.PlanPoint;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.network.model.NetworkFlowModel;
import ds.graph.NodeRectangle;
import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.graph.Node;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphAssignmentConverter extends AbstractAlgorithm<ConcreteAssignment, NetworkFlowModel> {

    private NetworkFlowModel model;

    public GraphAssignmentConverter(NetworkFlowModel model) {
        this.model = model;
    }

    @Override
    protected NetworkFlowModel runAlgorithm(ConcreteAssignment problem) {
        model = convertConcreteAssignment(problem);
        return model;
    }

    /**
     * Converts a concrete assignment into an assignment for graphs. The concrete assignments provides a list of all
     * persons, their associated rooms and positions on the plan. These coordinates are translated into local
     * coordinates of the room they inhabit. The associated nodes of the individual room raster squares are then
     * provided with the proper number of persons and the node assignment is afterwards set to the network flow model.
     * Additionally the super sink node is given a negative assignment in the amount of the number of people in the
     * building.
     *
     * @param assignment The concrete assignment to be converted
     * @param model The network flow model to which the converted assignment has to be written
     */
    private NetworkFlowModel convertConcreteAssignment(ConcreteAssignment assignment) {
        NetworkFlowModel.AssignmentBuilder modelBuilder = new NetworkFlowModel.AssignmentBuilder(model); // this resets the assignment
        ZToGraphMapping mapping = model.getZToGraphMapping();
        ZToGraphRasterContainer raster = mapping.getRaster();

        // the new converted node assignment
        List<Person> persons = assignment.getPersons();

        HashMap<Room, Double> roomMaxTime = new HashMap<>();
        HashMap<Node, Integer> nodeAssignment = new HashMap<>();

        // for every person do
        for (int i = 0; i < persons.size(); i++) {
            Room room = persons.get(i).getRoom();
            Node node = getNode(persons.get(i), raster);

            //max-in-room-assignment
            final double maxTimeForRoom = roomMaxTime.getOrDefault(room, 0d);
            roomMaxTime.put(room, Math.max(maxTimeForRoom, persons.get(i).getReaction()));
            final int count = nodeAssignment.getOrDefault(node, 0);
            nodeAssignment.put(node, count + 1);
        }

        for (int i = 0; i < persons.size(); i++) {
            Room room = persons.get(i).getRoom();
            Node node = getNode(persons.get(i), raster);
            int count = nodeAssignment.get(node);
            double delay = roomMaxTime.get(room);
            // Delay in sekunden
            double factor = 1 / 0.26425707443;

            double commonDelay = delay * factor;
            double personalDelay = persons.get(i).getReaction();
            Node assignmentNode = modelBuilder.increaseNodeAssignment(node, commonDelay + personalDelay);
            NodeRectangle rect = new NodeRectangle(400 * count, -800, 400 * (count + 1), -400);
            mapping.setNodeRectangle(assignmentNode, rect);
        }

        return modelBuilder.build();

    }

    private Node getNode(Person p, ZToGraphRasterContainer raster) {
        Room room = p.getRoom();
        ZToGraphRoomRaster roomRaster = raster.getRasteredRoom(room);

        // calculate the coordinates of the person inside of it's room
        PlanPoint pos = p.getPosition();
        int XPos = pos.getXInt();
        int YPos = pos.getYInt();
        // get the square the person is located
        ZToGraphRasterSquare square = roomRaster.getSquareWithGlobalCoordinates(XPos, YPos);
        // get the square's associated node
        return square.getNode();
    }
}
