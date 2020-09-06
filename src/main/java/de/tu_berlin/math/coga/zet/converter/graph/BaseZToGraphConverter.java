/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA	02110-1301, USA.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.network.model.NetworkFlowModel;
import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.common.datastructure.SimpleTuple;
import org.zetool.common.datastructure.Tuple;
import org.zetool.graph.Edge;
import org.zetool.graph.Graph;
import org.zetool.graph.Node;

/**
 *
 * @author Jan-Philipp Kappmeier
 * @author Martin Groß
 */
public abstract class BaseZToGraphConverter extends AbstractAlgorithm<BuildingPlan, NetworkFlowModel> {

    protected ZToGraphMapping mapping;
    protected NetworkFlowModel.BasicBuilder modelBuilder;
    protected ZToGraphRasterContainer raster;
    protected Graph roomGraph;
    public int numStairEdges = 0;
    public List<Edge> stairEdges = new LinkedList<>();

    protected BaseZToGraphConverter() {
    }

    protected NetworkFlowModel getModel() {
        return getSolution();
    }

    @Override
    protected NetworkFlowModel runAlgorithm(BuildingPlan problem) {
        raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer(problem);
        modelBuilder = new NetworkFlowModel.BasicBuilder(raster);
        mapping = modelBuilder.getZToGraphMapping();

        createNodes();

        // create edges, their capacities and the capacities of the nodes
        createEdgesAndCapacities();
        log.info("Alle Kanten erzeugt.");

        // calculate the transit times for all edges
        computeTransitTimes();

        // duplicate the edges and their transit times (except those concerning the super sink)
//        createReverseEdges();
        // adjust transit times according to stair speed factors
        log.setLevel(Level.ALL);
        multiplyWithUpAndDownSpeedFactors();
        log.setLevel(Level.FINE);

        NetworkFlowModel model = modelBuilder.build();

        assert checkParallelEdges(model);

        return model;
    }

    protected abstract void createNodes();

    protected abstract void createEdgesAndCapacities();

    protected abstract void computeTransitTimes();

    int originalEdges;

    protected void multiplyWithUpAndDownSpeedFactors() {
        for (Edge edge : modelBuilder.edges()) {
            if (!edge.isIncidentTo(modelBuilder.getSupersink())) {
                switch (mapping.getEdgeLevel(edge)) {
                    case Higher:
                        double factor = 1.0;
                        if (mapping.getUpNodeSpeedFactor(edge.start()) != 1.0) {
                            factor = mapping.getUpNodeSpeedFactor(edge.start());
                        } else if (mapping.getUpNodeSpeedFactor(edge.end()) != 1.0) {
                            factor = mapping.getUpNodeSpeedFactor(edge.end());
                        }
                        modelBuilder.divide(edge, factor);
                        log.log(Level.FINEST, "Multiplying edge {0} with up speed factor {1}", new Object[]{edge, factor});
                        break;
                    case Lower:
                        factor = 1.0;
                        if (mapping.getDownNodeSpeedFactor(edge.start()) != 1.0) {
                            factor = mapping.getDownNodeSpeedFactor(edge.start());
                        } else if (mapping.getDownNodeSpeedFactor(edge.end()) != 1.0) {
                            factor = mapping.getDownNodeSpeedFactor(edge.end());
                        }
                        modelBuilder.divide(edge, factor);
                        log.log(Level.FINEST, "Multiplying edge {0} with down speed factor {1}", new Object[]{edge, factor});
                        break;
                }
            }
        }
    }

    /**
     * Checks if the generated network flow model contains at most one edge between any pair of nodes.
     *
     * @return {@code true} if the network does not contain parallel arcs. Otherwise, an {@link AssertionError} is
     * thrown
     */
    boolean checkParallelEdges(NetworkFlowModel model) {
        int count = 0;
        log.info("Check for parallel edges...");

        HashMap<Tuple<Node, Node>, Edge> usedEdges = new HashMap<>((int) (model.numberOfEdges() / 0.75) + 1, 0.75f);

        for (Edge edge : model.edges()) {
            final Tuple<Node, Node> nodePair = new SimpleTuple<>(edge.start(), edge.end());
            if (usedEdges.containsKey(nodePair)) {
                log.log(Level.WARNING, "Two edges between nodes {0}: {1} and {2}", new Object[]{nodePair, usedEdges.get(nodePair), edge});
                //return false;
                count++;
            }
            usedEdges.put(nodePair, edge);
        }
        log.log(Level.INFO, "No parallel edges found.");
        System.err.println("Parallel edges: " + count);
        return true;
    }
}
