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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import algo.graph.reduction.YenKShortestPaths;
import algo.graph.reduction.YenPath;
import de.zet_evakuierung.model.AssignmentArea;
import de.zet_evakuierung.model.PlanPoint;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.network.model.NetworkFlowModel;
import ds.graph.NodeRectangle;
import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.container.collection.ListSequence;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.graph.structure.Forest;

/**
 *
 * @author Marlen Schwengfelder
 */
public class RepeatedShortestPathsShrinker extends AbstractAlgorithm<NetworkFlowModel, NetworkFlowModel> {

    public NetworkFlowModel.BasicBuilder modelBuilder;
    public IdentifiableIntegerMapping TransitForEdge;
    public IdentifiableIntegerMapping currentTransitForEdge;
    IdentifiableIntegerMapping<Edge> currentTransitForEdge2;
    public IdentifiableCollection<Edge> ForestEdges;
    public Forest forest;
    public Edge neu;
    public Edge neureverse;
    public Edge neu2;
    //public int NumEdges = 0;
    //public int NumNodes = 0;
    public int NumCurrentEdges = 0;
    public int NumShortestPaths = 5;
    IdentifiableCollection<Edge> solEdges = new ListSequence<>();
    IdentifiableCollection<Node> solNodes = new ListSequence<>();
    private ListSequence<Edge> currentEdges = new ListSequence<>();
    private ListSequence<Edge> res;

    public Map<Node, Node> newNodeMap = new HashMap<>();

    @Override
    protected NetworkFlowModel runAlgorithm(NetworkFlowModel problem) {
        ZToGraphMapping originalMapping = problem.getZToGraphMapping();

//        mapping = new ZToGraphMapping();
//                ZToGraphMapping newmapping = new ZToGraphMapping();
//        model = new NetworkFlowModel();
        ListSequence<Edge> super_edges = new ListSequence<>();
//
//        raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer( problem );
//        mapping.setRaster( raster );
//        model.setZToGraphMapping( mapping );
//
//                //DynamicNetwork newgraph = new DynamicNetwork();
//
//        super.createNodes();
//        super.createEdgesAndCapacities();
//        super.computeTransitTimes();
//        super.multiplyWithUpAndDownSpeedFactors();
//        //model.setTransitTimes( exactTransitTimes.round() );
//        model.roundTransitTimes();
//        createReverseEdges( model );

        for (Edge e : problem.graph().edges()) {
            if (e.isIncidentTo(problem.getSupersink())) {
                super_edges.add(e);
            }
        }

        modelBuilder = new NetworkFlowModel.BasicBuilder(problem.getZToGraphMapping().raster);
        ZToGraphMapping newMapping = modelBuilder.getZToGraphMapping();

        Node Super = problem.getSupersink();
        //newgraph.addNode(Super);
        //NumNodes++;
        //minspanmodel.setSupersink(Super);
        newMapping.setNodeSpeedFactor(Super, 1);
        newMapping.setNodeRectangle(Super, new NodeRectangle(0, 0, 0, 0));
        newMapping.setFloorForNode(Super, -1);

        //model.setNetwork( model.getGraph().getAsStaticNetwork() );
        YenKShortestPaths yen = new YenKShortestPaths(problem);
        Node exit = null;
        List<ZToGraphRoomRaster> rasteredRooms = problem.getZToGraphMapping().getRaster().getAllRasteredRooms();
        for (ZToGraphRoomRaster room : rasteredRooms) {

            int colCount = room.getColumnCount();
            int rowCount = room.getRowCount();

            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    ZToGraphRasterSquare square = room.getSquare(col, row);

                    // todo: parameter
                    if (square.isSave() && square.isExit()) {
                        exit = square.getNode();
                    }// end if safe
                }
            }
        }

        int[][] used = new int[problem.numberOfNodes()][problem.numberOfNodes()];
        for (int i = 0; i < problem.numberOfNodes(); i++) {
            for (int j = 0; j < problem.numberOfNodes(); j++) {
                used[i][j] = 0;
            }
        }

        List<YenPath> found = new LinkedList<>();
        //stores the number of created nodes for each assignment area
        Map<Node, AssignmentArea> NodesForArea = new HashMap<>();
        Map<AssignmentArea, Integer> numNodesForArea = new HashMap<>(1);

        for (Node source : problem.getSources()) {
            //Room r = originalMapping.getNodeRoomMapping().get(source);
            Room r = originalMapping.getRoom(source);
            PlanPoint pos = new PlanPoint((int) originalMapping.getNodeRectangles().get(source).getCenterX(), (int) -originalMapping.getNodeRectangles().get(source).getCenterY());
            //System.out.println("found planpoint: " + pos);

            for (AssignmentArea a : r.getAssignmentAreas()) {

                if (a.contains(pos)) {
                    NodesForArea.put(source, a);
                    if (numNodesForArea.containsKey(a)) {
                        int current = numNodesForArea.get(a);
                        numNodesForArea.put(a, current + 1);
                    } else {
                        numNodesForArea.put(a, 1);
                    }
                    break;
                }
            }
        }
        for (Node source : problem.getSources()) {
            int supply = NodesForArea.get(source).getEvacuees() / numNodesForArea.get(NodesForArea.get(source));
            //System.out.println("Node: " + source + " supply: " + supply);
            if (supply > 0) {
                found.addAll(yen.getShortestPaths(source, problem.getSupersink(), supply));
            } else {
                found.addAll(yen.getShortestPaths(source, problem.getSupersink(), 1));
            }

        }

        for (YenPath y : found) {
            //System.out.println("Pfad: " + y.toString());
            for (int i = 0; i < y.getNodes().size() - 1; i++) {
                if (used[y.getNodes().get(i).id()][y.getNodes().get(i + 1).id()] == 0 && y.getNodes().get(i).id() != 0 && y.getNodes().get(i + 1).id() != 0) {
                    Edge n = problem.graph().getEdge(y.getNodes().get(i), y.getNodes().get(i + 1));
                    //Edge e = new Edge(NumEdges++,y.getNodes().get(i),y.getNodes().get(i+1));
                    //System.out.println("Transit: " + model.getTransitTime(n));
                    solEdges.add(n);
                    used[y.getNodes().get(i).id()][y.getNodes().get(i + 1).id()] = 1;
                    used[y.getNodes().get(i + 1).id()][y.getNodes().get(i).id()] = 1;
                    if (!solNodes.contains(y.getNodes().get(i))) {
                        solNodes.add(y.getNodes().get(i));
                    }
                    if (!solNodes.contains(y.getNodes().get(i + 1))) {
                        solNodes.add(y.getNodes().get(i + 1));
                    }
                }
            }
        }

        for (Node node : solNodes) {
            //Node new_node = new Node(NumNodes++);

            Node new_node = modelBuilder.newNode();
            //newgraph.addNode(new_node);
            newNodeMap.put(node, new_node);
            if (problem.getSources().contains(node)) {
                modelBuilder.addSource(new_node);
            }
            //System.out.println("new Node: " + new_node + "for old: " + node);
            if (node.id() != 0) {
                newMapping.setNodeRectangle(new_node, originalMapping.getNodeRectangles().get(node));
                newMapping.setFloorForNode(new_node, problem.getZToGraphMapping().getNodeFloorMapping().get(node));
                //newMapping.setIsEvacuationNode( new_node,problem.getZToGraphMapping().getIsEvacuationNode(node));
                //newMapping.setIsSourceNode(new_node, problem.getZToGraphMapping().getIsSourceNode(node));
                newMapping.setDeletedSourceNode(new_node, problem.getZToGraphMapping().getIsDeletedSourceNode(node));
                modelBuilder.setNodeCapacity(new_node, problem.getNodeCapacity(node));
                newMapping.setNodeSpeedFactor(new_node, originalMapping.getNodeSpeedFactor(node));
                newMapping.setNodeUpSpeedFactor(new_node, originalMapping.getUpNodeSpeedFactor(node));
                newMapping.setNodeDownSpeedFactor(new_node, originalMapping.getDownNodeSpeedFactor(node));
            }
        }

        for (Edge edge : solEdges) {
            Edge orig = problem.getEdge(edge.start(), edge.end());
            //Edge new_edge = new Edge(NumEdges++,newNodeMap.get(edge.start()),newNodeMap.get(edge.end()));
            //System.out.println("neue Kante: " + new_edge + "for: " + orig);
            Edge new_edge = modelBuilder.newEdge(newNodeMap.get(edge.start()), newNodeMap.get(edge.end()));

            //newgraph.addEdge(new_edge);
            modelBuilder.setEdgeCapacity(new_edge, problem.getEdgeCapacity(orig));
            modelBuilder.setExactTransitTime(new_edge, problem.getTransitTime(orig));
            newMapping.setEdgeLevel(new_edge, originalMapping.getEdgeLevel(orig));
            modelBuilder.setExactTransitTime(new_edge, problem.getTransitTime(orig));
        }

        for (Edge e : super_edges) {
            if (newNodeMap.containsKey(e.start())) {
                //Edge new_edge = new Edge(NumEdges++,newNodeMap.get(e.start()),minspanmodel.getSupersink());
                //System.out.println("superEdge: " + new_edge + "for: " + e);
                //newgraph.addEdge(new_edge);
                Edge new_edge = modelBuilder.newEdge(newNodeMap.get(e.start()), modelBuilder.getSupersink());

                Edge orig = problem.getEdge(e.start(), e.end());
                modelBuilder.setExactTransitTime(new_edge, problem.getTransitTime(orig));
                modelBuilder.setEdgeCapacity(new_edge, Integer.MAX_VALUE);
                modelBuilder.setExactTransitTime(new_edge, problem.getTransitTime(orig));
                newMapping.setEdgeLevel(new_edge, originalMapping.getEdgeLevel(orig));
            }
        }

        newMapping.raster = originalMapping.getRaster();
        for (ZToGraphRoomRaster room : rasteredRooms) {
            int numCol = room.getColumnCount();
            int numRow = room.getRowCount();
            for (int i = 0; i < numCol; i++) {
                for (int j = 0; j < numRow; j++) {
                    ZToGraphRasterSquare square = room.getSquare(i, j);
                    square.mark();
                    Node old = square.getNode();
                    square.setNode(newNodeMap.get(old));
                }
            }
        }

        //minspanmodel.setCurrentAssignment(model.getCurrentAssignment());
        //minspanmodel.setSources(model.getSources());
        //minspanmodel.setNetwork(newgraph);
        //values from mapping of original graph
        newMapping.exitName = originalMapping.exitName;

        /*for (Node n: minspanmodel.getGraph().nodes())
     {
     System.out.println("Nodes: " + n);
     }
     for (Edge e: minspanmodel.getGraph().edges())
     {
     System.out.println("Kante: " + e + "Cap: " + minspanmodel.getEdgeCapacity(e) + "Tran: " + minspanmodel.getTransitTime(e));
     }*/
        //minspanmodel.setNetwork(newgraph);
        //minspanmodel.setNetwork( minspanmodel.getGraph().getAsStaticNetwork());
        //System.out.println("Number of Created Repeated Shortest Paths Edges: " + minspanmodel.getGraph().edgeCount());
        return modelBuilder.build();
    }

}
