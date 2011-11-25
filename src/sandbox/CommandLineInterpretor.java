/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * CommandLineInterpretor.java
 *
 */
package sandbox;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import gui.propertysheet.PropertyTreeModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import algo.graph.exitassignment.ExitAssignment;
import algo.graph.exitassignment.ShortestPathExitAssignment;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import old.ZToGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridGraphConverter;
import ds.z.Project;
import ds.PropertyContainer;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.ProjectLoader;
import ds.z.Assignment;
import ds.z.ConcreteAssignment;

/**
 *
 * @author Martin Groß
 */
public class CommandLineInterpretor {

    private BufferedWriter log;
    private File logFile;
    
    public CommandLineInterpretor() {
        logFile = new File("c:\\algos.csv");
    }
    
    public void startLogging() {
        if (logFile != null) {
            try {
                log = new BufferedWriter(new FileWriter(logFile));
                log("Instanz, Projekt, Properties, Rastern, NFM, C. Ass., C. Ass. NFM, TH Est., LB, UB, EAT, n, m, B, PD, TH Real");
                logNewLine();                
            } catch (IOException ex) {
                Logger.getLogger(CommandLineInterpretor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void log(String text) {
        if (log != null) {
            try {
                log.write(text);
            } catch (IOException ex) {
                Logger.getLogger(CommandLineInterpretor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void logNewLine() {
        if (log != null) {
            try {
                log.newLine();
            } catch (IOException ex) {
                Logger.getLogger(CommandLineInterpretor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void stopLogging() {
        if (log != null) {
            try {
                log.flush();
                log.close();
            } catch (IOException ex) {
                Logger.getLogger(CommandLineInterpretor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void interpret(String string) {

    }
    
    private static PrintStream oldOut = System.out;
    private static PrintStream oldErr = System.err;
    
    public void iterateFiles(File root, File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                iterateFiles(root, file);
            } else if (file.getName().endsWith(".zet")) {
                String name = file.getAbsolutePath().substring(root.getAbsolutePath().length() + 1);
                log(name);
                oldOut.print("Processing " + name + ": ");
                oldErr = System.err;
                try {
                    System.setOut(new PrintStream(file.getName().substring(0, file.getName().length() - 4) + ".algolog"));
                    System.setErr(new PrintStream(file.getName().substring(0, file.getName().length() - 4) + ".algoerr"));
                    runAlgorithm(file);
                    System.setOut(oldOut);
                    System.setErr(oldErr);
                } catch (Exception e) {
                    System.setOut(oldOut);
                    System.setErr(oldErr);
                    e.printStackTrace();
                    System.exit(1);
                }
                oldOut.println();
                logNewLine();
            }
        }
    }

    public void runAlgorithm(File file) throws IOException {
        long start;
        start = System.currentTimeMillis();
        Project project;
        try {
					project = ProjectLoader.load( file );
            //project = Project.load(file);
        } catch (Exception e) {
            if (log != null) log.write(", exception");
            return; 
        }
        if (log != null) {
            log.write(", " + (System.currentTimeMillis() - start));
        } else {
            System.out.println("Loading the project took " + (System.currentTimeMillis() - start) + " ms.");
        }

        start = System.currentTimeMillis();
        try {
            PropertyTreeModel propertyTreeModel = PropertyContainer.loadConfigFile(new File("./properties/properties.xml") );
            PropertyContainer.getInstance().applyParameters( propertyTreeModel );
        } catch (Exception e) {
            if (log != null) log.write(", exception");
            return; 
        }
        if (log != null) {
            log.write(", " + (System.currentTimeMillis() - start));
        } else {
            System.out.println("Loading the properties took " + (System.currentTimeMillis() - start) + " ms.");
        }

        Assignment assignment = null;
        ConcreteAssignment concreteAssignment = null;
        try {
            assignment = (project.getCurrentAssignment () != null) ? project.getCurrentAssignment () : project.getAssignments().get(0);
        } catch (Exception e) {
            if (log != null) log.write(", exception");
            return; 
        }            

        start = System.currentTimeMillis();
        try {
            project.getBuildingPlan().rasterize();
        } catch (Exception e) {
            if (log != null) log.write(", exception");
            return;
        }
        if (log != null) {
            log.write(", " + (System.currentTimeMillis() - start));
        } else {
            System.out.println("Rastering took " + (System.currentTimeMillis() - start) + " ms.");
        }
        start = System.currentTimeMillis();
        NetworkFlowModel model = new NetworkFlowModel();
        try {
            ZToGraphConverter.convertBuildingPlan(project.getBuildingPlan(), model);
        } catch (Exception e) {
            if (log != null) log.write(", exception");
            return; 
        }
        if (log != null) {
            log.write(", " + (System.currentTimeMillis() - start));
        } else {
            System.out.println("Converting the model took " + (System.currentTimeMillis() - start) + " ms.");
        }            

        start = System.currentTimeMillis();
        try {
            concreteAssignment = assignment.createConcreteAssignment(400);
        } catch (Exception e) {
            if (log != null) log.write(", exception");
            return; 
        }
        if (log != null) {
            log.write(", " + (System.currentTimeMillis() - start));
        } else {
            System.out.println("Creating the concrete assignment took " + (System.currentTimeMillis() - start) + " ms." );
        }
        
        start = System.currentTimeMillis();
        try {
          GraphAssignmentConverter ac = new GraphAssignmentConverter( model );
					ac.setProblem( concreteAssignment );
					ac.run();
					//ZToNonGridGraphConverter.convertConcreteAssignment(concreteAssignment, model);
        } catch (Exception e) {
            if (log != null) log.write(", exception");
            return; 
        }
        if (log != null) {
            log.write(", " + (System.currentTimeMillis() - start));
        } else {
            System.out.println("Converting the assignment took " + (System.currentTimeMillis() - start) + " ms.");
        }        

        //System.out.println(model.getNetwork().numberOfNodes() + " " + model.getNetwork().numberOfEdges());
        
        start = System.currentTimeMillis();
        //MaximumFlowProblem problem = new MaximumFlowProblem(model.getNetwork(), model.getEdgeCapacities(), model.getSources(), model.getSinks());
        //Algorithm<MaximumFlowProblem, MaximumFlow> algo = new DischargingGlobalGapHighestLabelPreflowPushAlgorithm();
        /*StaticTransshipment algo = new StaticTransshipment(model.getNetwork(), model.getEdgeCapacities(), model.getCurrentAssignment());
        SuccessiveShortestPath algo = new SuccessiveShortestPath(model.getNetwork(), model.getCurrentAssignment(), model.getEdgeCapacities(), model.getTransitTimes());
        MinimumMeanCycleCancelling algo = new MinimumMeanCycleCancelling(model.getNetwork(), model.getEdgeCapacities(), model.getTransitTimes(), model.getCurrentAssignment());
        MaxFlowOverTime algo = new MaxFlowOverTime(model.getNetwork(), model.getEdgeCapacities(), model.getSinks(), model.getSources(), 240, model.getTransitTimes());
        TimeExpandedMaximumFlowOverTime algo = new TimeExpandedMaximumFlowOverTime(model.getNetwork(), model.getEdgeCapacities(), model.getTransitTimes(), model.getSources(), model.getSinks(), 7);
        DynamicTransshipment algo = new DynamicTransshipment(model.getNetwork(), model.getTransitTimes(), model.getEdgeCapacities(), model.getNodeCapacities(), model.getCurrentAssignment(), 27);*/
        //QuickestTransshipment algo = new QuickestTransshipment(model.getNetwork(), model.getTransitTimes(), model.getEdgeCapacities(), model.getCurrentAssignment());
        /*
        Algorithm<NetworkFlowModel, ExitAssignment> spea = new ShortestPathExitAssignment();
        spea.setProblem(model);
        spea.run();
        System.out.println(spea.getSolution());
        System.out.println(spea.getRuntimeAsString());        
        */
        
        Algorithm<NetworkFlowModel, ExitAssignment> mctea = new ShortestPathExitAssignment();
        mctea.setProblem(model);
        mctea.run();        
        System.out.println(mctea.getSolution());
        System.out.println(mctea.getRuntimeAsString());
        
        //System.out.println(spea.getSolution().difference(mctea.getSolution()));
        //System.out.println(mctea.getSolution().difference(spea.getSolution()));
        /*
        Algorithm<NetworkFlowModel, ExitAssignment> eatea = new EarliestArrivalTransshipmentExitAssignment();
        eatea.setProblem(model);
        eatea.run();        
        System.out.println(eatea.getSolution());
        System.out.println(eatea.getRuntimeAsString());        
        */
        //ExitAssignments ea = new ExitAssignments();
        //ea.shortestPaths(model);
        //algo.run();
        //algo.setProblem(problem);
        if (log != null) {
            log.write(", " + (System.currentTimeMillis() - start) + ", ");
        } else {
            System.out.println("Computed " + " in " + (System.currentTimeMillis() - start) + " ms.");
        }
        
        /*
        start = System.currentTimeMillis();
        EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem(model.getEdgeCapacities(), model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), 0, model.getTransitTimes(), model.getCurrentAssignment());            
        LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
        estimator.setProblem(problem);
        estimator.run();
        if (log != null) {
            log.write(", " + (System.currentTimeMillis() - start) + ", " + estimator.getSolution().getLowerBound() + ", " + estimator.getSolution().getUpperBound());
        } else {
            System.out.println("Estimated " + estimator.getSolution() + " in " + (System.currentTimeMillis() - start) + " ms.");
        }        
        
        start = System.currentTimeMillis();
        problem = new EarliestArrivalFlowProblem(model.getEdgeCapacities(), model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), estimator.getSolution().getUpperBound(), model.getTransitTimes(), model.getCurrentAssignment());
        //SEAAPAlgorithm algo = new SEAAPAlgorithm(false);
        //algo.setProblem(problem);
        //algo.run();
          EATransshipmentSSSP algo = new EATransshipmentSSSP(model.getNetwork(), model.getTransitTimes(), model.getEdgeCapacities(), model.getCurrentAssignment());
          algo.run();      
        if (log != null) {
            log.write(", " + (System.currentTimeMillis() - start) + ", " + problem.getNetwork().numberOfNodes() + ", " + problem.getNetwork().numberOfEdges() + ", " + problem.getTotalSupplies());
        } else {
            System.out.println("SEAAP needed " + (System.currentTimeMillis() - start) + " ms.");
        }       
          */
/*
        start = System.currentTimeMillis();
        FlowOverTime flow = new FlowOverTime(algo.drn, algo.paths);
        if (log != null) {
            log.write(", " + (System.currentTimeMillis() - start) + ", " + flow.getTimeHorizon());
        } else {
            System.out.println("PD needed " + (System.currentTimeMillis() - start) + " ms.");
        }                       
*/        
        //System.out.println(String.format("Sent %1$s of %2$s flow units in %3$s time units successfully.", algo.getSolution().getFlowAmount(), problem.getTotalSupplies(), algo.getSolution().getTimeHorizon()));
        //System.out.println(String.format("Sending the flow units required %1$s ms.", algo.getRuntime() / 1000000));        
    }

    public static void main(String[] args) throws IOException {
        File file = new File(args[0]);
        System.out.println(file.getAbsolutePath());
        CommandLineInterpretor cli = new CommandLineInterpretor();
        if (file.isDirectory()) {
            cli.startLogging();
            try {
                cli.iterateFiles(file, file);
            } finally {
                cli.stopLogging();
            }
        } else {
            cli.runAlgorithm(file);  
        }
    }
}
