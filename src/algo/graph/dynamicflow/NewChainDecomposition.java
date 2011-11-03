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
import ds.graph.ImplicitTimeExpandedResidualNetwork;
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
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Martin Groß
 */
public class NewChainDecomposition extends Algorithm<ChainDecompositionProblem, PathBasedFlowOverTime> {

    private static final boolean DEBUG = false;
    private transient LinkedList<FlowOverTimeEdgeSequence> complete;
    private transient ImplicitTimeExpandedResidualNetwork network;
    private transient IdentifiableObjectMapping<Edge, Queue[]> sequencesUsingEdge;
    private transient IdentifiableObjectMapping<Node, Queue[]> sequencesUsingNode;
    private transient IdentifiableIntegerMapping<Edge> transitTimes;
    private transient Deque<FlowOverTimeEdgeSequence> sequences;

    @Override
    protected PathBasedFlowOverTime runAlgorithm(ChainDecompositionProblem problem) {
        complete = new LinkedList<FlowOverTimeEdgeSequence>();
        network = problem.getNetwork();
        sequencesUsingEdge = new IdentifiableObjectMapping<Edge, Queue[]>(network.edges(), Queue[].class);
        sequencesUsingNode = new IdentifiableObjectMapping<Node, Queue[]>(network.nodes(), Queue[].class);
        transitTimes = problem.getNetwork().getProblem().getTransitTimes();

        for (FlowOverTimeEdgeSequence edgeSequence : problem.getEdgeSequences()) {
            int time = 0;
            for (FlowOverTimeEdge edge : edgeSequence) {
                time += edge.getDelay();
                edge.setTime(time);
                time += transitTimes.get(edge.getEdge());
            }
        }

        // Remove all cycles from the edge sequences and store them for uncrossing
        for (FlowOverTimeEdgeSequence edgeSequence : problem.getEdgeSequences()) {
            FlowOverTimeCycle cycle = extractCycle(edgeSequence);
            while (cycle != null) {
                addSequenceToUsageLists(cycle);
                if (DEBUG) {
                    System.out.println("Cycle: " + cycle);
                }
                cycle = extractCycle(edgeSequence);
            }
        }

        // We have removed the cycles for the edge sequences now; what remains are residual paths
        sequences = new LinkedList(problem.getEdgeSequences());

        while (!sequences.isEmpty()) {
            if (DEBUG) {
                System.out.println("Sequences: " + sequences);
                System.out.println("Complete: " + complete);
                System.out.println("Using 239:" + Arrays.deepToString(sequencesUsingNode.get(new Node(239))));
                //System.out.println("Using 50:" + Arrays.deepToString(sequencesUsingNode.get(new Node(50))));
            }
            FlowOverTimeEdgeSequence sequence = sequences.poll();
            if (DEBUG) {
                System.out.println("Current Sequence: " + sequence);
            }

            // Look for the first reverse edge / node with negative waiting time
            FlowOverTimeEdge currentEdge = null;
            boolean reverseEdge = false, waiting = false;
            int lastArrival = 0;
            for (FlowOverTimeEdge edge : sequence) {
                if (network.isReverseEdge(edge.getEdge())) {
                    currentEdge = edge;
                    reverseEdge = true;
                    break;
                } else if (lastArrival > edge.getTime()) {
                    waiting = true;
                    break;
                }
                currentEdge = edge;
                lastArrival = edge.getTime() + transitTimes.get(edge.getEdge());
            }

            // If we do not find any, then residual path is a normal path
            if (!reverseEdge && !waiting) {
                if (DEBUG) {
                    System.out.println("Normal Path");
                }
                // Register the path for uncrossing purposes
                addSequenceToUsageLists(sequence);
                // Add it to the result
                complete.add(sequence);
            } else if (reverseEdge && !waiting) {
                if (DEBUG) {
                    System.out.println("Reverse Edge found");
                }
                Edge edge = network.reverseEdge(currentEdge.getEdge());
                int normalTime = sequence.lengthUpTo(transitTimes, currentEdge);
                //System.out.println("Partner: " + pathsUsingEdge.get(edge) + " " + edge);
                Queue<FlowOverTimeEdgeSequence> uncrossingPartners = sequencesUsingEdge.get(edge)[normalTime];
                do {
                    FlowOverTimeEdgeSequence partner = uncrossingPartners.peek();
                    FlowOverTimeEdge normalEdge = partner.get(edge, normalTime);
                    uncrossSequences(sequence, partner, currentEdge, normalEdge);
                } while (sequence.getRate() > 0);
            } else {
                if (DEBUG) {
                    System.out.println("Reverse Node Found");
                }
                // Get the first node at which negative waiting occurs
                int t = sequence.lengthUpTo(transitTimes, currentEdge);
                if (DEBUG && sequencesUsingNode.get(currentEdge.getEdge().end()) == null) {
                    System.out.println("No uncrossing partner for node: " + currentEdge.getEdge().end() + " at time " + (t - 1));
                    System.out.println(Arrays.deepToString(sequencesUsingNode.get(currentEdge.getEdge().end())));
                }
                Queue<FlowOverTimeEdgeSequence> uncrossingPartners = sequencesUsingNode.get(currentEdge.getEdge().end())[t - 1];
                if (DEBUG && uncrossingPartners == null) {
                    System.out.println("No uncrossing partner for node: " + currentEdge.getEdge().end() + " at time " + (t - 1));
                    System.out.println(Arrays.deepToString(sequencesUsingNode.get(currentEdge.getEdge().end())));
                }
                if (DEBUG && uncrossingPartners.isEmpty()) {
                    System.out.println("No uncrossing partner for node: " + currentEdge.getEdge().end() + " at time " + (t - 1));
                    System.out.println(Arrays.deepToString(sequencesUsingNode.get(currentEdge.getEdge().end())));
                }
                do {
                    FlowOverTimeEdgeSequence partner = uncrossingPartners.peek();
                    uncrossSequences2(sequence, partner, currentEdge, partner.get(transitTimes, currentEdge.getEdge().end(), t - 1));
                } while (sequence.getRate() > 0);
            }
            if (DEBUG) {
                System.out.println("");
            }
        }

        PathBasedFlowOverTime pathFlow = new PathBasedFlowOverTime();
        for (FlowOverTimeEdgeSequence sequence : complete) {
            pathFlow.addPathFlow(new FlowOverTimePath(sequence));
        }
        return pathFlow;
    }

    protected void addSequenceToUsageLists(FlowOverTimeEdgeSequence sequence) {
        int lastArrival = Integer.MAX_VALUE;
        for (FlowOverTimeEdge edge : sequence) {
            for (int t = lastArrival; t < edge.getTime(); t++) {
                if (!sequencesUsingNode.isDefinedFor(edge.getEdge().start())) {
                    sequencesUsingNode.set(edge.getEdge().start(), new LinkedList[network.timeHorizon()]);
                }
                if (sequencesUsingNode.get(edge.getEdge().start())[t] == null) {
                    sequencesUsingNode.get(edge.getEdge().start())[t] = new LinkedList();
                }
                sequencesUsingNode.get(edge.getEdge().start())[t].add(sequence);
            }
            if (!sequencesUsingEdge.isDefinedFor(edge.getEdge())) {
                sequencesUsingEdge.set(edge.getEdge(), new LinkedList[network.timeHorizon()]);
            }
            if (sequencesUsingEdge.get(edge.getEdge())[edge.getTime()] == null) {
                sequencesUsingEdge.get(edge.getEdge())[edge.getTime()] = new LinkedList();
            }
            sequencesUsingEdge.get(edge.getEdge())[edge.getTime()].add(sequence);
            lastArrival = edge.getTime() + transitTimes.get(edge.getEdge());
        }
        if (sequence instanceof FlowOverTimeCycle && sequence.getFirstEdge().getTime() > lastArrival) {
            for (int t = lastArrival; t < sequence.getFirstEdge().getTime(); t++) {
            if (!sequencesUsingNode.isDefinedFor(sequence.getFirstEdge().getEdge().start())) {
                sequencesUsingNode.set(sequence.getFirstEdge().getEdge().start(), new LinkedList[network.timeHorizon()]);
            }
            if (sequencesUsingNode.get(sequence.getFirstEdge().getEdge().start())[t] == null) {
                sequencesUsingNode.get(sequence.getFirstEdge().getEdge().start())[t] = new LinkedList();
            }
            sequencesUsingNode.get(sequence.getFirstEdge().getEdge().start())[t].add(sequence);
            }
        }
    }

    protected void removeSequenceFromUsageLists(FlowOverTimeEdgeSequence sequence) {
        int lastArrival = Integer.MAX_VALUE;
        for (FlowOverTimeEdge edge : sequence) {
            for (int t = lastArrival; t < edge.getTime(); t++) {
                sequencesUsingNode.get(edge.getEdge().start())[t].remove(sequence);
            }
            sequencesUsingEdge.get(edge.getEdge())[edge.getTime()].remove(sequence);
            lastArrival = edge.getTime() + network.transitTime(edge.getEdge());
        }
    }

    protected FlowOverTimeCycle extractCycle(FlowOverTimeEdgeSequence edgeSequence) {
        // Check whether a node is visited twice
        IdentifiableIntegerMapping<Node> arrivalTime = new IdentifiableIntegerMapping<Node>(network.nodes());
        arrivalTime.initializeWith(-1);
        Node node = null;
        int lastArrivalTime = 0;
        for (FlowOverTimeEdge edge : edgeSequence) {
            Edge e = edge.getEdge();
            if (arrivalTime.get(e.start()) != -1) {
                node = e.start();
                break;
            }
            arrivalTime.set(e.start(), lastArrivalTime);
            lastArrivalTime = edge.getTime() + transitTimes.get(edge.getEdge());
        } // The first node that is visited twice is now stored in node, if there is such a node
        if (node != null) {
            // Split the sequence where the node is visited the first and the second time
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
            edgeSequence.append(sequenceParts[2]);//, time - arrivalTime.get(node) + sequenceParts[2].delay(sequenceParts[2].getFirst()));
            // Return the cycle
            return new FlowOverTimeCycle(sequenceParts[1], 0);
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
            if (DEBUG && complete.contains(partner)) {
                System.out.println(" Removing from complete: " + partner);
            }
            complete.remove(partner);
        }
        if (!start2.isEmpty() && start2.getRate() > 0) {
            addSequenceToUsageLists(start2);
            if (DEBUG) {
                System.out.println(" Adding to sequences: " + start2);
            }
            sequences.addFirst(start2);
        }
        if (!start1.isEmpty() && start2.getRate() > 0) {
            if (DEBUG) {
                System.out.println(" Adding to sequences: " + start1);
            }
            addSequenceToUsageLists(start1);
            sequences.addFirst(start1);
        }
    }

    protected void join(FlowOverTimeEdgeSequence base,
            FlowOverTimeEdgeSequence appendix,
            FlowOverTimeEdgeSequence baseTimeReference,
            FlowOverTimeEdgeSequence appendixTimeReference) {
        //System.out.println("  Join:\n   " + base + "\n   " + baseTimeReference + "\n   " + appendix + "\n   " + appendixTimeReference);
        base.append(appendix);
        /*if (!appendix.isEmpty()) {
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
        }*/
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
            start1.append(end2);
            start1.append(start2);
            start1.append(end1);
            start2.clear();
        } else {
            join(start1, end2, sequenceWithReverseEdge, partner);
            join(start2, end1, partner, sequenceWithReverseEdge);
        }
        adjustRates(sequenceWithReverseEdge, partner, start1, start2);
    }

    private void uncrossSequences2(FlowOverTimeEdgeSequence pathWithReverseNode, FlowOverTimeEdgeSequence partner, FlowOverTimeEdge reverseNodeEdge, FlowOverTimeEdge partnerEdge) {
        if (DEBUG) {
            System.out.println("Path is: " + pathWithReverseNode);
            System.out.println("Partner is: " + partner);
        }
        FlowOverTimeEdgeSequence start1 = pathWithReverseNode.subsequence(null, reverseNodeEdge, false, true);//
        FlowOverTimeEdgeSequence start2 = partner.subsequence(null, partnerEdge);// new FlowOverTimeEdgeSequence(getPathUntil(partner, reverseNode, t - 1));
        FlowOverTimeEdgeSequence end1 = pathWithReverseNode.subsequence(reverseNodeEdge, null);
        FlowOverTimeEdgeSequence end2 = partner.subsequence(partnerEdge, null, true, false);
        if (DEBUG) {
            System.out.println(" Path is split to:\n  " + start1 + "\n  " + end1);
            System.out.println(" Partner is split to:\n  " + start2 + "\n  " + end2);
        }
        start1.append(end2);
        start2.append(end1);
        adjustRates(pathWithReverseNode, partner, start1, start2);
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
        /*
        ResidualNetworkOverTime rn = new ResidualNetworkOverTime(
                network,
                IdentifiableConstantMapping.UNIT_EDGE_MAPPING,
                IdentifiableConstantMapping.UNIT_NODE_MAPPING,
                IdentifiableConstantMapping.UNIT_EDGE_MAPPING,
                new LinkedList<Node>(),
                IdentifiableConstantMapping.UNIT_NODE_MAPPING,
                20);

        FlowOverTimeEdgeSequence sequence = new FlowOverTimeEdgeSequence();
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(0), rn.getNode(1)), 0, 0));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(1), rn.getNode(6)), 1, 2));
        //sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(2), rn.getNode(6)), 0, 3));
        /*
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(0), rn.getNode(1)), 0, 0));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(1), rn.getNode(2)), 1, 2));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(2), rn.getNode(3)), 0, 3));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(3), rn.getNode(1)), 0, 4));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(1), rn.getNode(4)), 1, 6));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(4), rn.getNode(5)), 0, 7));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(5), rn.getNode(1)), 0, 8));
        sequence.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(1), rn.getNode(6)), 0, 9));*/
/*
        FlowOverTimeEdgeSequence sequence2 = new FlowOverTimeEdgeSequence();
        sequence2.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(0), rn.getNode(1)), 1, 1));
        sequence2.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(1), rn.getNode(6)), -1, 1));

        /*
        sequence2.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(0), rn.getNode(2)), 2, 2));
        sequence2.add(new FlowOverTimeEdge(rn.reverseEdge(rn.getEdge(rn.getNode(1), rn.getNode(2))), 0, 3));
        sequence2.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(1), rn.getNode(6)), 0, 2));*/
/*
        FlowOverTimeEdgeSequence sequence3 = new FlowOverTimeEdgeSequence();
        sequence3.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(0), rn.getNode(5)), 7, 7));
        sequence3.add(new FlowOverTimeEdge(rn.reverseEdge(rn.getEdge(rn.getNode(4), rn.getNode(5))), 0, 8));
        sequence3.add(new FlowOverTimeEdge(rn.getEdge(rn.getNode(4), rn.getNode(6)), 0, 7));

        LinkedList<FlowOverTimeEdgeSequence> edgeSequences = new LinkedList<FlowOverTimeEdgeSequence>();
        edgeSequences.add(sequence);
        edgeSequences.add(sequence2);
        //edgeSequences.add(sequence3);


        ChainDecompositionProblem problem = new ChainDecompositionProblem(edgeSequences, rn);

        NewChainDecomposition test = new NewChainDecomposition();
        test.setProblem(problem);
        test.run();
        System.out.println("Solution: " + test.getSolution());*/
    }
}
