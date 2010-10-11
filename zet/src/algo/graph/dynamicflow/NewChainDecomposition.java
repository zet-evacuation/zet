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
 * ChainDecomposition.java
 *
 */
package algo.graph.dynamicflow;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.DynamicResidualNetwork;
import ds.graph.Edge;
import ds.graph.IdentifiableConstantMapping;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.flow.FlowOverTimeCycle;
import ds.graph.flow.FlowOverTimeEdge;
import ds.graph.flow.FlowOverTimeEdgeSequence;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.flow.PathBasedFlowOverTime;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Martin Groß
 */
public class NewChainDecomposition extends Algorithm<ChainDecompositionProblem, PathBasedFlowOverTime> {

    private static final boolean DEBUG = true;
    private transient LinkedList<FlowOverTimeEdgeSequence> complete;
    private transient DynamicResidualNetwork network;
    private transient IdentifiableObjectMapping<Edge, Queue[]> pathsUsingEdge;
    private transient IdentifiableObjectMapping<Node, Queue[]> pathsUsingNode;
    private transient IdentifiableIntegerMapping<Edge> transitTimes;
    private transient Deque<FlowOverTimeEdgeSequence> sequences;

    @Override
    protected PathBasedFlowOverTime runAlgorithm(ChainDecompositionProblem problem) {
        complete = new LinkedList<FlowOverTimeEdgeSequence>();
        network = problem.getNetwork();
        pathsUsingEdge = new IdentifiableObjectMapping<Edge, Queue[]>(network.edges(), Queue[].class);
        pathsUsingNode = new IdentifiableObjectMapping<Node, Queue[]>(network.nodes(), Queue[].class);
        transitTimes = problem.getNetwork().transitTimes();

        // Remove all cycles from the edge sequences and store them for uncrossing
        for (FlowOverTimeEdgeSequence edgeSequence : problem.getEdgeSequences()) {
            FlowOverTimeCycle cycle = extractCycle(edgeSequence);
            while (cycle != null) {
                addSequenceToUsageLists(cycle);
                System.out.println("Cycle: " + cycle);
                cycle = extractCycle(edgeSequence);
            }
        }

        // We have removed the cycles for the edge sequences now; what remains are residual paths
        sequences = new LinkedList(problem.getEdgeSequences());
        while (!sequences.isEmpty()) {
            System.out.println("Sequences: " + sequences);
            System.out.println("Complete: " + complete);
            FlowOverTimeEdgeSequence sequence = sequences.poll();

            // Look for the first reverse edge / node with negative waiting time
            FlowOverTimeEdge reverseEdge = null;
            boolean waiting = false;
            for (FlowOverTimeEdge edge : sequence) {
                if (network.isReverseEdge(edge.getEdge())) {
                    reverseEdge = edge;
                    break;
                } else if (sequence.delay(edge) < 0) {
                    reverseEdge = edge;
                    waiting = true;
                    break;
                }
            }

            // If we do not find any, then residual path is a normal path
            if (reverseEdge == null && !waiting) {
                // Register the path for uncrossing purposes
                addSequenceToUsageLists(sequence);
                // Add it to the result
                complete.add(sequence);
            } else if (reverseEdge != null && !waiting) {
                Edge edge = network.reverseEdge(reverseEdge.getEdge());
                int normalTime = sequence.lengthUpTo(transitTimes, reverseEdge);
                //System.out.println("Partner: " + pathsUsingEdge.get(edge) + " " + edge);
                Queue<FlowOverTimeEdgeSequence> uncrossingPartners = pathsUsingEdge.get(edge)[normalTime];
                do {
                    FlowOverTimeEdgeSequence partner = uncrossingPartners.peek();
                    FlowOverTimeEdge normalEdge = partner.get(transitTimes, edge, normalTime);
                    uncrossSequences(sequence, partner, reverseEdge, normalEdge);
                } while (sequence.getRate() > 0);
            } else {
                // Get the first node at which negative waiting occurs
                int t = sequence.lengthUntil(transitTimes, reverseEdge) - reverseEdge.getDelay();
                Queue<FlowOverTimeEdgeSequence> uncrossingPartners = pathsUsingNode.get(reverseEdge.getEdge().start())[t - 1];
                do {
                    FlowOverTimeEdgeSequence partner = uncrossingPartners.peek();
                    uncrossSequences(sequence, partner, reverseEdge.getEdge().start(), reverseEdge, partner.get(transitTimes, reverseEdge.getEdge().start(), t - 1), t);
                } while (sequence.getRate() > 0);
            }
            System.out.println("");
        }

        PathBasedFlowOverTime pathFlow = new PathBasedFlowOverTime();
        for (FlowOverTimeEdgeSequence sequence : complete) {
            pathFlow.addPathFlow(new FlowOverTimePath(sequence));
        }
        return pathFlow;
    }

    protected void addSequenceToUsageLists(FlowOverTimeEdgeSequence path) {
        int time = 0;
        if (path instanceof FlowOverTimeCycle) {
            time = ((FlowOverTimeCycle) path).getOffset();
        }
        for (FlowOverTimeEdge edge : path) {
            for (int t = time; t < time + edge.getDelay(); t++) {
                if (!pathsUsingNode.isDefinedFor(edge.getEdge().start())) {
                    pathsUsingNode.set(edge.getEdge().start(), new LinkedList[network.getTimeHorizon()]);
                }
                if (pathsUsingNode.get(edge.getEdge().start())[t] == null) {
                    pathsUsingNode.get(edge.getEdge().start())[t] = new LinkedList();
                }
                pathsUsingNode.get(edge.getEdge().start())[t].add(path);
            }
            time += edge.getDelay();
            if (!pathsUsingEdge.isDefinedFor(edge.getEdge())) {
                pathsUsingEdge.set(edge.getEdge(), new LinkedList[network.getTimeHorizon()]);
            }
            if (pathsUsingEdge.get(edge.getEdge())[time] == null) {
                pathsUsingEdge.get(edge.getEdge())[time] = new LinkedList();
            }
            pathsUsingEdge.get(edge.getEdge())[time].add(path);
            time += network.transitTimes().get(edge.getEdge());
        }
    }

    protected void removeSequenceFromUsageLists(FlowOverTimeEdgeSequence sequence) {
        int time = 0;
        if (sequence instanceof FlowOverTimeCycle) {
            time = ((FlowOverTimeCycle) sequence).getOffset();
        }
        for (FlowOverTimeEdge edge : sequence) {
            for (int t = time; t < time + edge.getDelay(); t++) {
                pathsUsingNode.get(edge.getEdge().start())[t].remove(sequence);
            }
            time += sequence.delay(edge);
            pathsUsingEdge.get(edge.getEdge())[time].remove(sequence);
            time += network.transitTimes().get(edge.getEdge());
        }
    }

    protected FlowOverTimeCycle extractCycle(FlowOverTimeEdgeSequence edgeSequence) {
        // Check whether a node is visited twice
        IdentifiableIntegerMapping<Node> arrivalTime = new IdentifiableIntegerMapping<Node>(network.nodes());
        arrivalTime.initializeWith(-1);
        Node node = null;
        int time = 0;
        for (FlowOverTimeEdge edge : edgeSequence) {
            Edge e = edge.getEdge();
            if (arrivalTime.get(e.start()) != -1) {
                node = e.start();
                break;
            }
            arrivalTime.set(e.start(), time);
            time += edge.getDelay();
            time += transitTimes.get(edge.getEdge());
        } // The first node that is visited twice is now stored in node, if there is such a node
        if (node != null) {
            // Split the sequence at the points where the node is visited the first and second times
            FlowOverTimeEdgeSequence[] sequenceParts = new FlowOverTimeEdgeSequence[3];
            for (int i = 0; i < 3; i++) {
                sequenceParts[i] = new FlowOverTimeEdgeSequence();
            }
            int index = 0;
            for (FlowOverTimeEdge edge : edgeSequence) {
                if (edge.getEdge().start() == node && index < 2) {
                    index++;
                }
                sequenceParts[index].add(edge);
            }
            // Remove the part in between the occurences of the node
            edgeSequence.clear();
            edgeSequence.append(sequenceParts[0]);
            edgeSequence.append(sequenceParts[2], time - arrivalTime.get(node) + sequenceParts[2].delay(sequenceParts[2].getFirst()));
            // Return the cycle
            //return new FlowOverTimeCycle(sequences[1], arrivalTime.get(node));
            sequenceParts[1].getFirstEdge().setDelay(sequenceParts[1].getFirstEdge().getDelay() + arrivalTime.get(node) - time);
            return new FlowOverTimeCycle(sequenceParts[1], time);
        } else {
            return null;
        }
    }

    protected void adjustRates(FlowOverTimeEdgeSequence sequenceWithReverseEdge,
            FlowOverTimeEdgeSequence partner,
            FlowOverTimeEdgeSequence start1,
            FlowOverTimeEdgeSequence start2) {
        int rate = Math.min(sequenceWithReverseEdge.getRate(), partner.getRate());
        start1.setRate(rate);
        start2.setRate(rate);
        if (DEBUG) {
            System.out.println(" The new paths are:\n  " + start1.getRate() + ": " + start1 + "\n  " + start2.getRate() + ": " + start2);
        }
        sequenceWithReverseEdge.setRate(sequenceWithReverseEdge.getRate() - rate);
        partner.setRate(partner.getRate() - rate);
        if (partner.getRate() == 0) {
            removeSequenceFromUsageLists(partner);
            if (complete.contains(partner)) {
                System.out.println(" Removing from complete: " + partner);
            }
            complete.remove(partner);
        }
        if (!start2.isEmpty()) {
            addSequenceToUsageLists(start2);
            System.out.println(" Adding to sequences: " + start2);
            sequences.addFirst(start2);
        }
        if (!start1.isEmpty()) {
            System.out.println(" Adding to sequences: " + start1);
            addSequenceToUsageLists(start1);
            sequences.addFirst(start1);
        }
    }

    protected void join(FlowOverTimeEdgeSequence base,
            FlowOverTimeEdgeSequence appendix,
            FlowOverTimeEdgeSequence baseTimeReference,
            FlowOverTimeEdgeSequence appendixTimeReference) {
        System.out.println("  Join:\n   " + base + "\n   " + baseTimeReference + "\n   " + appendix + "\n   " + appendixTimeReference);
        if (!appendix.isEmpty()) {
            int appendixStart = appendixTimeReference.lengthUntil(transitTimes, appendix.getFirstEdge());
            int baseEnd;
            if (baseTimeReference != null) {
                if (!base.isEmpty()) {
                    baseEnd = baseTimeReference.lengthUpTo(transitTimes, base.getLastEdge());
                } else {
                    baseEnd = baseTimeReference.getFirstEdge().getDelay();
                }
            } else {
                baseEnd = base.length(transitTimes);
            }
            System.out.println("    " + appendixStart + " " + baseEnd);
            base.append(appendix, appendixStart - baseEnd);
        }
    }

    private void uncrossSequences(FlowOverTimeEdgeSequence sequenceWithReverseEdge, FlowOverTimeEdgeSequence partner, FlowOverTimeEdge reverseEdge, FlowOverTimeEdge normalEdge) {
        if (DEBUG) {
            System.out.println("Path is: " + sequenceWithReverseEdge);
            System.out.println("Partner is: " + partner);
        }
        FlowOverTimeEdgeSequence start1 = sequenceWithReverseEdge.subsequence(null, reverseEdge);
        FlowOverTimeEdgeSequence start2 = partner.subsequence(null, normalEdge);
        FlowOverTimeEdgeSequence end1 = sequenceWithReverseEdge.subsequence(reverseEdge, null);
        FlowOverTimeEdgeSequence end2 = partner.subsequence(normalEdge, null);
        if (DEBUG) {
            System.out.println(" Path is split to:\n  " + start1 + "\n  " + end1);
            System.out.println(" Partner is split to:\n  " + start2 + "\n  " + end2);
        }
        //Edge lastEdge = network.reverseEdge(reverseEdge.getEdge());
        while (!start2.isEmpty() && !end1.isEmpty() && network.reverseEdge(start2.getLastEdge().getEdge()).equals(end1.getFirstEdge().getEdge())) {
            if (DEBUG) {
                System.out.println(" Cleaning: " + sequenceWithReverseEdge.lengthUpTo(transitTimes, end1.getFirstEdge()) + " " + partner.lengthUntil(transitTimes, start2.getLastEdge()));
            }
            if (sequenceWithReverseEdge.lengthUpTo(transitTimes, end1.getFirstEdge()) != partner.lengthUntil(transitTimes, start2.getLastEdge())) {
                break;
            }
            //lastEdge = start2.getLastEdge().getEdge();
            start2.removeLast();
            end1.removeFirst();
        }
        if (DEBUG) {
            System.out.println(" Path is cleaned to:\n  " + start1 + "\n  " + end1);
            System.out.println(" Partner is cleaned to:\n  " + start2 + "\n  " + end2);
        }
        if (partner instanceof FlowOverTimeCycle) {
            join(end2, start2, partner, partner);
            join(start1, end2, sequenceWithReverseEdge, partner);
            //join(start1, start2, sequenceWithReverseEdge, partner);
            join(start1, end1, null, sequenceWithReverseEdge);
        } else {
            /*
            if (!end2.isEmpty()) {
                int secondStart = partner.lengthUntil(transitTimes, end2.getFirstEdge());
                int firstEnd;
                if (!start1.isEmpty()) {
                    firstEnd = sequenceWithReverseEdge.lengthUpTo(transitTimes, start1.getLastEdge());
                } else {
                    firstEnd = getArrivalTime(sequenceWithReverseEdge, reverseEdge.getEdge().start());
                }
                start1.append(end2, secondStart - firstEnd);
            }*/
            join(start1, end2, sequenceWithReverseEdge, partner);
            join(start2, end1, partner, sequenceWithReverseEdge);
            /*
            if (!end1.isEmpty()) {
                int secondStart = sequenceWithReverseEdge.lengthUntil(transitTimes, end1.getFirstEdge());
                int firstEnd = 0;
                if (!start2.isEmpty()) {
                    firstEnd = partner.lengthUpTo(transitTimes, start2.getLastEdge());
                } else {
                    firstEnd = getArrivalTime(partner, lastEdge.start());
                }
                start2.append(end1, secondStart - firstEnd);
            }*/
        }
        adjustRates(sequenceWithReverseEdge, partner, start1, start2);
    }

    private void uncrossSequences(FlowOverTimeEdgeSequence pathWithReverseNode, FlowOverTimeEdgeSequence partner, Node reverseNode, FlowOverTimeEdge reverseNodeEdge, FlowOverTimeEdge partnerEdge, int t) {
        FlowOverTimeEdgeSequence start1 = pathWithReverseNode.subsequence(null, reverseNodeEdge);//
        FlowOverTimeEdgeSequence start2 = partner.subsequence(null, partnerEdge);// new FlowOverTimeEdgeSequence(getPathUntil(partner, reverseNode, t - 1));
        FlowOverTimeEdgeSequence end1 = pathWithReverseNode.subsequence(reverseNodeEdge, null, true, false);
        FlowOverTimeEdgeSequence end2 = partner.subsequence(partnerEdge, null, true, false);
        if (DEBUG) {
            System.out.println(" Path is split to:\n  " + start1 + "\n  " + end1);
            System.out.println(" Partner is split to:\n  " + start2 + "\n  " + end2);
        }
        if (!end2.isEmpty()) {
            int time = partner.lengthUntil(transitTimes, end2.getFirstEdge()) - pathWithReverseNode.lengthUntil(transitTimes, reverseNodeEdge) - reverseNodeEdge.getDelay();
            start1.append(end2, time);
        }
        if (!end1.isEmpty()) {
            int time = 0;
            if (!start2.isEmpty()) {
                time = pathWithReverseNode.lengthUntil(transitTimes, end1.getFirstEdge()) - partner.lengthUpTo(transitTimes, start2.getLastEdge());
                if (DEBUG) {
                    System.out.println(" Delay: " + time + " " + pathWithReverseNode.lengthUntil(transitTimes, end1.getFirstEdge()) + " " + partner.lengthUpTo(transitTimes, start2.getLastEdge()));
                }
            } else {
                time = pathWithReverseNode.lengthUntil(transitTimes, end1.getFirstEdge()) - getArrivalTime(partner, reverseNode);
            }
            start2.append(end1, time);
        }
        adjustRates(pathWithReverseNode, partner, start1, start2);
    }

    protected int getArrivalTime(FlowOverTimeEdgeSequence path, Node node) {
        int time = 0;
        for (FlowOverTimeEdge e : path) {
            if (e.getEdge().start().equals(node)) {
                return time;
            }
            time += path.delay(e);
            time += network.transitTimes().get(e.getEdge());
        }
        return -1;
    }

    public static void main(String[] args) {
        Network network = new Network(10, 90);
        for (Node start : network.nodes()) {
            for (Node end : network.nodes()) {
                if (start == end) {
                    continue;
                }
                network.createAndSetEdge(start, end);
            }
        }
        DynamicResidualNetwork rn = new DynamicResidualNetwork(
                network,
                IdentifiableConstantMapping.UNIT_EDGE_MAPPING,
                IdentifiableConstantMapping.UNIT_NODE_MAPPING,
                IdentifiableConstantMapping.UNIT_EDGE_MAPPING,
                new LinkedList<Node>(),
                IdentifiableConstantMapping.UNIT_NODE_MAPPING,
                20);
        FlowOverTimeEdgeSequence sequence = new FlowOverTimeEdgeSequence();
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(0), rn.getNode(1)), 0));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(1), rn.getNode(2)), 1));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(2), rn.getNode(3)), 0));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(3), rn.getNode(1)), 0));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(1), rn.getNode(4)), 1));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(4), rn.getNode(5)), 0));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(5), rn.getNode(1)), 0));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(1), rn.getNode(6)), 0));

        FlowOverTimeEdgeSequence sequence2 = new FlowOverTimeEdgeSequence();
        sequence2.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(0), rn.getNode(2)), 2));
        sequence2.add(new FlowOverTimeEdge(rn.reverseEdge(rn.getEdge(rn.getNode(1), rn.getNode(2))), 0));
        sequence2.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(1), rn.getNode(6)), 0));

        FlowOverTimeEdgeSequence sequence3 = new FlowOverTimeEdgeSequence();
        sequence3.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(0), rn.getNode(5)), 7));
        sequence3.add(new FlowOverTimeEdge(rn.reverseEdge(rn.getEdge(rn.getNode(4), rn.getNode(5))), 0));
        sequence3.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(4), rn.getNode(6)), 0));

        LinkedList<FlowOverTimeEdgeSequence> edgeSequences = new LinkedList<FlowOverTimeEdgeSequence>();
        edgeSequences.add(sequence);
        edgeSequences.add(sequence2);
        edgeSequences.add(sequence3);
        ChainDecompositionProblem problem = new ChainDecompositionProblem(edgeSequences, rn);

        NewChainDecomposition test = new NewChainDecomposition();
        test.setProblem(problem);
        test.run();
        System.out.println("Solution: " + test.getSolution());
    }
}
