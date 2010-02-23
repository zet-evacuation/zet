/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * PreflowPush.java
 *
 */
package algo.graph.staticflow.maxflow;

import ds.graph.Edge;
import ds.graph.Node;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
public class DischargingGlobalGapHighestLabelPreflowPushAlgorithm extends DischargingGlobalHighestLabelPreflowPushAlgorithm {

    protected List<Node>[] buckets;

    @Override
    protected void initializeDatastructures() {
        super.initializeDatastructures();
        buckets = new List[2 * getProblem().getNetwork().numberOfNodes() + 1];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new LinkedList<Node>();
        }
        for (Node node : getProblem().getNetwork().nodes()) {
            buckets[0].add(node);
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
                if (relabels == getGlobalRelabelingThreshold()) {
                    relabels = 0;
                    relabelGlobally();
                }
            }
            if (excess.get(node) == 0) {
                activeNodes.extractMax();
            }
            findGaps();
        }
    }

    protected void findGaps() {
        int firstEmpty = Integer.MAX_VALUE;
        for (int i = 1; i < getProblem().getNetwork().numberOfNodes(); i++) {
            if (buckets[i].isEmpty() && i < firstEmpty) {
                firstEmpty = i;
                break;
            }
        }
        if (firstEmpty < 0 || firstEmpty > getProblem().getNetwork().numberOfNodes()) {
            return;
        }
        for (int i = firstEmpty + 1; i < getProblem().getNetwork().numberOfNodes(); i++) {
            while (!buckets[i].isEmpty()) {
                Node node = buckets[i].get(0);
                neverActive.set(node, true);
                updateDistanceLabel(node, getProblem().getNetwork().numberOfNodes());
            }
        }
    }

    @Override
    protected void updateDistanceLabel(Node node, int value) {
        buckets[distanceLabels.get(node)].remove(node);
        distanceLabels.set(node, value);
        buckets[value].add(node);
        if (activeNodes.contains(node)) {
            activeNodes.increasePriority(node, value);
        }
    }
}
