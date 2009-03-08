/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * PreflowPush.java
 *
 */
package algo.graph.staticflow.maxflow;

import algo.graph.traverse.BFS;
import ds.graph.Edge;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Node;

/**
 *
 * @author Martin Groß
 */
public class DischargingGlobalHighestLabelPreflowPushAlgorithm extends PreflowPushAlgorithm {

    protected transient IdentifiableObjectMapping<Node, Boolean> neverActive;
    protected transient int relabels;
    protected int globalRelabelingThreshold;   
    protected boolean initializingWithGlobalRelabeling;
    
    public boolean isInitializingWithGlobalRelabeling() {
        return initializingWithGlobalRelabeling;
    }

    public void setInitializingWithGlobalRelabeling(boolean initializingWithGlobalRelabeling) {
        this.initializingWithGlobalRelabeling = initializingWithGlobalRelabeling;
    }

    public int getGlobalRelabelingThreshold() {
        return globalRelabelingThreshold;
    }

    public void setGlobalRelabelingThreshold(int globalRelabelingThreshold) {
        this.globalRelabelingThreshold = globalRelabelingThreshold;
    }
    
    @Override
    protected void initializeDatastructures() {
        super.initializeDatastructures();
        neverActive = new IdentifiableObjectMapping<Node, Boolean>(getProblem().getNetwork().numberOfNodes(), Boolean.class);
        relabels = 0;
        if (globalRelabelingThreshold == 0) {
            globalRelabelingThreshold = getProblem().getNetwork().numberOfNodes();
        }
        for (Node source : getProblem().getSources()) {
            neverActive.set(source, true);
        }        
    }

    @Override
    protected void preflowPush() {
        if (initializingWithGlobalRelabeling) {
            relabelGlobally();
        }        
        while (!activeNodes.isEmpty()) {
            Node node = activeNodes.getMaximumObject();
            boolean hasAdmissibleEdge = false;
            for (Edge edge : residualNetwork.outgoingEdges(node)) {
                if (isAdmissible(edge)) {
                    push(edge);
                    hasAdmissibleEdge = true;
                    break;
                }
            }
            if (!hasAdmissibleEdge) {
                relabel(node);
                relabels++;
                if (relabels == globalRelabelingThreshold) {
                    relabels = 0;
                    relabelGlobally();
                }
            }
            if (excess.get(node) == 0) {
                activeNodes.extractMax();
            }
        }
    }

    protected void relabelGlobally() {
        BFS bfs = new BFS(residualNetwork);
        bfs.run(getProblem().getSinks(), null, false, true);
        for (Node node : residualNetwork.nodes()) {
            if (neverActive.get(node) != null || bfs.distance(node) == Integer.MAX_VALUE) {
                continue;
            }
            updateDistanceLabel(node, bfs.distance(node));
        }
    }
}
