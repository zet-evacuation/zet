/*
 * PreflowPushAlgorithm.java
 *
 */
package algo.graph.staticflow.maxflow;

import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.MaxHeap;
import ds.graph.Node;
import ds.graph.ResidualNetwork;
import ds.graph.flow.MaximumFlow;
import sandbox.Algorithm;

/**
 *
 * @author Martin Groß
 */
public class PreflowPushAlgorithm extends Algorithm<MaximumFlowProblem, MaximumFlow> {

    protected ResidualNetwork residualNetwork;
    protected transient MaxHeap<Node, Integer> activeNodes;
    protected transient IdentifiableIntegerMapping<Node> distanceLabels;
    protected transient IdentifiableIntegerMapping<Node> excess;
    private transient long totalExcess,  startExcess;
    private transient int maxLabel;
    private transient long done;
    private transient int sumOfLabels;
    private final transient int increasesBeforeOutput = 100;
    private transient int progressOutputCounter = increasesBeforeOutput;

    public ResidualNetwork getResidualNetwork() {
        return residualNetwork;
    }

    @Override
    protected MaximumFlow runAlgorithm(MaximumFlowProblem problem) {
        initializeDatastructures();
        initializeProgress();
        preflowPush();
        return new MaximumFlow(getProblem(), residualNetwork.flow());
    }

    protected void initializeDatastructures() {
        activeNodes = new MaxHeap<Node, Integer>(getProblem().getNetwork().numberOfNodes());
        distanceLabels = new IdentifiableIntegerMapping<Node>(getProblem().getNetwork().numberOfNodes());
        excess = new IdentifiableIntegerMapping<Node>(getProblem().getNetwork().numberOfNodes());
        residualNetwork = new ResidualNetwork(getProblem().getNetwork(), getProblem().getCapacities());
        for (Node source : getProblem().getSources()) {
            for (Edge edge : residualNetwork.outgoingEdges(source)) {
                if (!getProblem().getSources().contains(edge.opposite(source))) {
                    augmentFlow(edge, getProblem().getCapacities().get(edge));
                }
            }
            distanceLabels.set(source, getProblem().getNetwork().numberOfNodes());
        }
    }

    protected void initializeProgress() {
        if (!util.ProgressBooleanFlags.ALGO_PROGRESS) {
            return;
        }
        /* Initialize the overall excess and the sum of all labels. */
        totalExcess = 0;
        sumOfLabels = 0;
        for (Node source : getProblem().getSources()) {
            for (Edge edge : residualNetwork.outgoingEdges(source)) {
                if (!getProblem().getSources().contains(edge.opposite(source))) {
                    totalExcess += getProblem().getCapacities().get(edge);
                }
            }
            maxLabel = 0;
            sumOfLabels += distanceLabels.get(source);
        }
        /* The start excess is the excess in the network before the first push operation. */
        /* done is the maximal sum of labels that can be reached. */
        /* Print both numbers if debug is activated. */
        startExcess = totalExcess;
        done = getProblem().getNetwork().numberOfNodes() * getProblem().getNetwork().numberOfNodes() + (startExcess);
        if (util.DebugFlags.PP) {
            System.out.println("Total excess " + totalExcess + " Max Label: " + maxLabel + " Sum of Labels: " + sumOfLabels + " " +
                    (sumOfLabels + (startExcess - totalExcess)) + " of " + done + ".");
        }
        if( util.ProgressBooleanFlags.ALGO_PROGRESS ) {
            if (progressOutputCounter == increasesBeforeOutput) {
                System.out.println("Progress: " + (sumOfLabels + (startExcess - totalExcess)) + " of " + done);
                System.out.flush();
                progressOutputCounter = 0;
            } else {
                progressOutputCounter++;
            }
        }
    }

    protected void preflowPush() {
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
            }
            if (excess.get(node) == 0) {
                activeNodes.extractMax();
            }
        }
    }

    protected void augmentFlow(final Edge edge, int amount) {
        if (excess.get(edge.end()) == 0 && !getProblem().getSinks().contains(edge.end()) && amount > 0) {
            activeNodes.insert(edge.end(), distanceLabels.get(edge.end()));
        }
        residualNetwork.augmentFlow(edge, amount);
        excess.decrease(edge.start(), amount);
        if (getProblem().getSinks().contains(edge.end()) || getProblem().getSources().contains(edge.end())) {
            if (util.ProgressBooleanFlags.ALGO_PROGRESS) {
                totalExcess -= amount;
                /* The total excess changed, so print it if debug is activated. */
                if (util.DebugFlags.PP) {
                    System.out.println("Total excess " + totalExcess + " Max Label: " + maxLabel + " Sum of Labels: " + sumOfLabels + " " +
                            (sumOfLabels + (startExcess - totalExcess)) + " of " + done + ".");
                }
                if (progressOutputCounter == increasesBeforeOutput) {
                    System.out.println("Progress: " + (sumOfLabels + (startExcess - totalExcess)) + " of " + done);
                    System.out.flush();
                    progressOutputCounter = 0;
                } else {
                    progressOutputCounter++;
                }
            }
        } else {
            excess.increase(edge.end(), amount);
        }
    }

    protected boolean isAdmissible(Edge edge) {
        return distanceLabels.get(edge.start()) == distanceLabels.get(edge.end()) + 1;
    }

    protected void push(Edge edge) {
        augmentFlow(edge, Math.min(residualNetwork.residualCapacities().get(edge), excess.get(edge.start())));
    }

    protected void relabel(Node node) {
        int min = Integer.MAX_VALUE;
        for (Edge edge : residualNetwork.outgoingEdges(node)) {
            if (min > distanceLabels.get(edge.end()) + 1) {
                min = distanceLabels.get(edge.end()) + 1;
            }
        }
        updateDistanceLabel(node, min);
    }

    protected void updateDistanceLabel(Node node, int value) {
        if (util.ProgressBooleanFlags.ALGO_PROGRESS) {
            sumOfLabels -= distanceLabels.get(node);
        }
        distanceLabels.set(node, value);
        if (util.ProgressBooleanFlags.ALGO_PROGRESS) {
            sumOfLabels += distanceLabels.get(node);
            /* Print new sum of labels if debug is activated. */
            if (util.DebugFlags.PP && value > maxLabel) {
                System.out.println("Total excess " + totalExcess + " Max Label: " + value + " Sum of Labels: " + sumOfLabels + " " +
                        (sumOfLabels + (startExcess - totalExcess)) + " of " + done + ".");
            }
            if (value > maxLabel) {
                if (progressOutputCounter == increasesBeforeOutput) {
                    System.out.println("Progress: " + (sumOfLabels + (startExcess - totalExcess)) + " of " + done);
                    System.out.flush();
                    progressOutputCounter = 0;
                } else {
                    progressOutputCounter++;
                }
            }
            maxLabel = Math.max(value, maxLabel);
        }
        distanceLabels.set(node, value);
        if (activeNodes.contains(node)) {
            activeNodes.increasePriority(node, value);
        }
    }
}
