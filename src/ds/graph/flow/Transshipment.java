/*
 * Transshipment.java
 *
 */
package ds.graph.flow;

import algo.graph.staticflow.TransshipmentProblem;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Node;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
public class Transshipment extends Flow {

    private TransshipmentProblem problem;
    private transient boolean changed;
    private transient boolean feasible;    
    private transient int flow;
    private transient List<Node> sinks;
    private transient List<Node> sources;

    public Transshipment(TransshipmentProblem problem, Iterable<Edge> edges) {
        super(edges);
        this.changed = false;
        this.feasible = true;
        this.flow = 0;
        this.problem = problem;
        this.sinks = new LinkedList<Node>();
        this.sources = new LinkedList<Node>();
    }

    public Transshipment(TransshipmentProblem problem, IdentifiableIntegerMapping<Edge> flow) {
        super(flow);
        this.changed = false;
        this.feasible = true;
        this.flow = 0;
        this.problem = problem;
        this.sinks = new LinkedList<Node>();
        this.sources = new LinkedList<Node>();        
        update();
    }

    @Override
    public void decrease(Edge identifiableObject, int amount) {
        super.decrease(identifiableObject, amount);
        changed();
    }

    @Override
    public void increase(Edge identifiableObject, int amount) {
        super.increase(identifiableObject, amount);
        changed();
    }

    @Override
    public void set(Edge identifiableObject, int value) {
        super.set(identifiableObject, value);
        changed();
    }

    public TransshipmentProblem getProblem() {
        return problem;
    }

    public int getFlowValue() {
        if (changed) {
            update();
        }
        return flow;
    }

    public List<Node> getSinks() {
        if (changed) {
            update();
        }        
        return sinks;
    }

    public List<Node> getSources() {
        if (changed) {
            update();
        }        
        return sources;
    }

    public boolean isFeasible() {
        if (changed) {
            update();
        }
        return feasible;
    }

    private void changed() {
        changed = true;
    }

    private void update() {
        feasible = true;
        flow = 0;
        sinks.clear();
        sources.clear();
        for (Node node : problem.getNetwork().nodes()) {
            int balance = 0;
            for (Edge edge : problem.getNetwork().outgoingEdges(node)) {
                balance += get(edge);
            }
            for (Edge edge : problem.getNetwork().incomingEdges(node)) {
                balance -= get(edge);
            }
            if (balance > 0) {
                sources.add(node);
                flow += balance;
            } else if (balance < 0) {
                sinks.add(node);
            }
            if (balance != problem.getSupplies().get(node)) {
                feasible = false;
            }
        }
        changed = false;
    }
}
