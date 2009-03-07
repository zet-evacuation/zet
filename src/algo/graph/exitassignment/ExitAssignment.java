/*
 * ExitAssignment.java
 *
 */

package algo.graph.exitassignment;

import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Node;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
public class ExitAssignment extends IdentifiableObjectMapping<Node, List> {

    protected Iterable<Node> domain;
    protected IdentifiableIntegerMapping<Node> evacuees;
    protected int numberOfEvacuees;
    
    public ExitAssignment(Iterable<Node> domain) {
        super(domain, List.class);
        this.domain = domain;
        evacuees = new IdentifiableIntegerMapping<Node>(domain);
        numberOfEvacuees = 0;
    }

    public void assignIndividualToExit(Node node, Node exit) {
        if (!isDefinedFor(node)) {
            set(node, new LinkedList<Node>());
        }
        get(node).add(exit);
        evacuees.increase(exit, 1);
        numberOfEvacuees++;
    }        
    
    @Override
    public List<Node> get(Node node) {
        return super.get(node);
    }
    
    public ExitAssignment difference(ExitAssignment exitAssignment) {
        ExitAssignment result = new ExitAssignment(domain);
        for (Node node : domain) {
            if (isDefinedFor(node)) {
                for (Node exit : get(node)) {
                    result.assignIndividualToExit(node, exit);
                }
                if (exitAssignment.isDefinedFor(node)) {
                    result.get(node).removeAll(exitAssignment.get(node));
                }
            }
        }
        return result;
    }
    
    public IdentifiableIntegerMapping<Node> evacuees() {
        return evacuees;
    }
    
    @Override
    public String toString() {
        return numberOfEvacuees + " Individuen evakuiert:\n" + evacuees().toString() + "\n" + super.toString();
    }
}
