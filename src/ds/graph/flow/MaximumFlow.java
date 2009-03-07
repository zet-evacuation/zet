/*
 * MaximumFlow.java
 *
 */

package ds.graph.flow;

import algo.graph.staticflow.maxflow.MaximumFlowProblem;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Node;

/**
 *
 * @author Martin Groß
 */
public class MaximumFlow extends Flow {
    
    private MaximumFlowProblem problem;
    
    public MaximumFlow(MaximumFlowProblem problem, IdentifiableIntegerMapping<Edge> flow) {
        super(flow);
        this.problem = problem;
    }

    public MaximumFlowProblem getProblem() {
        return problem;
    }
    
    public int getFlowValue() {
        int result = 0;
        for (Node source : problem.getSources()) {
            for (Edge edge : problem.getNetwork().outgoingEdges(source)) {
                result += get(edge);
            }
            for (Edge edge : problem.getNetwork().incomingEdges(source)) {
                result -= get(edge);
            }
        }
        return result;        
    }
}
