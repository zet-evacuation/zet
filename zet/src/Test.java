
import algo.graph.staticflow.mincost.MinimumMeanCycleCancelling;
import algo.graph.staticflow.mincost.SuccessiveShortestPath;
import cern.colt.Arrays;
import de.tu_berlin.math.coga.batch.input.reader.DimacsMinimumCostFlowFileReader;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.problem.MinimumCostFlowProblem;
import ds.graph.problem.RawMinimumCostFlowProblem;
import java.io.File;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gross
 */
public class Test {
    /*
    public static void test(File file) {
        
        System.out.print(file.getName() + ": "); 
        DimacsMinimumCostFlowFileReader reader = new DimacsMinimumCostFlowFileReader();
        reader.setFile(file);
        reader.run();
        RawMinimumCostFlowProblem problem = reader.getSolution();
        Converter converter = new Converter();
        converter.setProblem(problem);
        converter.run();
        MinimumCostFlowProblem mcfp = converter.getSolution();
        //SuccessiveShortestPath ssp = new SuccessiveShortestPath(mcfp.getNetwork(), mcfp.getBalances(), mcfp.getCapacities(), mcfp.getCosts());
        //ssp.run();
        MinimumMeanCycleCancelling mmcc = new MinimumMeanCycleCancelling();
        mmcc.setProblem(mcfp);
        mmcc.run();
        IdentifiableIntegerMapping<Edge> flow;
        //flow = ssp.getFlow();
        flow = mmcc.getSolution();
        int costs = 0;
        for (Edge edge : mcfp.getNetwork().edges()) {
            if (flow.get(edge) < 0) System.out.println("Subzero flow!");
            if (flow.get(edge) > mcfp.getCapacities().get(edge)) System.out.println("Capacity Violated!");
            costs += (flow.get(edge) * mcfp.getCosts().get(edge));
        }
        
        for (Node node : mcfp.getNetwork().nodes()) {
            int balance = 0;
            for (Edge edge : mcfp.getNetwork().incomingEdges(node)) {
                balance += flow.get(edge);
            }
            for (Edge edge : mcfp.getNetwork().outgoingEdges(node)) {
                balance -= flow.get(edge);
            }
            if (balance + mcfp.getBalances().get(node) != 0) {
                System.out.println("Balance: " + balance + " " + mcfp.getBalances().get(node));
            }
        }
        System.out.println(costs);        
    }
    
    public static void main(String[] args) {
        File folder = new File("/homes/combi/gross/Judith/src/TestGraphsZib/");        
        //test(folder);
        //if (1 == 1) return; 
        for (File file : folder.listFiles()) {
            test(file);
        }
    }*/
    
}
