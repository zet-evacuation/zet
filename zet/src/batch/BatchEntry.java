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
package batch;

import batch.tasks.AssignmentTask;
import batch.tasks.BatchCA2Task;
import gui.editor.properties.PropertyFilesSelectionModel.Property;
import java.util.TreeMap;
import batch.tasks.BatchCATask;
import batch.tasks.BatchEvacuationCATask;
import batch.tasks.BatchGraphTask;
import batch.tasks.BatchLoadProperties;
import batch.tasks.assignment.BestResponseAssignmentTask;
import batch.tasks.ComputeAvgStepPerSecondTask;
import batch.tasks.CreateCellularAutomatonTask;
import batch.tasks.assignment.EarliestArrivalAssignmentTask;
import batch.tasks.MedianTask;
import batch.tasks.assignment.MinCostAssignmentTask;
import batch.tasks.assignment.ReducedEarliestArrivalAssignmentTask;
import batch.tasks.assignment.ShortestPathAssignmentTask;
import batch.tasks.assignment.ShortestPathGraphEarliestArrivalAssignmentTask;
import converter.ZToCAConverter.ConversionNotSupportedException;
import ds.Project;
import ds.PropertyContainer;
import ds.z.Assignment;
import ds.z.ConcreteAssignment;
import ds.ca.CellularAutomaton;
import gui.batch.EvacuationOptimizationType;
import gui.editor.properties.PropertyFilesSelectionModel;
import io.visualization.BuildingResults;
import io.visualization.CAVisualizationResults;
import de.tu_berlin.math.coga.common.localization.Localization;
import statistic.ca.CAStatistic;
import statistic.ca.MultipleCycleCAStatistic;
import batch.tasks.RasterizeTask;
import gui.batch.JBatchProgressDialog;

/**
 * A wrapper class that represents a single entry in the batch list.
 * The entry only stores the cellular automaton and the graph that are needed
 * to run the algorithms on them, the chosen graph algorithm and the name
 * of the entry.
 * 
 * @author Timon, Jan-Philipp Kappmeier
 */
public class BatchEntry {

    private String name;
    private Project project;
    private Assignment assignment;
    private CellularAutomatonAlgorithm cellularAutomatonAlgo;
    private boolean useCa;
    private boolean useGraph;
    private int cycles;
    private double caMaxTime;
    private int graphMaxTime;
    private GraphAlgorithm graphAlgo;
    private Property propertyFile;
    private EvacuationOptimizationType evacuationOptimizationType;
    private int optimizedEvacuationPlanCycles;

    /**
     * Creates a new BatchEntry with reduced information. The other values are
     * calculated or set to default.
     * @param name the name of the batch entry
     * @param project the project assigned to the entry
     * @param cycles the number of times the ca is simulated
     * @param ga the graph algorithm that is called
     * @param caa the cellular automaton algorithm that should be called
     * @throws IllegalArgumentException If the project is null or if it doesn't contain at least one assignment.
     */
    public BatchEntry(String name, Project project, int cycles, GraphAlgorithm ga, CellularAutomatonAlgorithm caa) throws IllegalArgumentException {
        check(project);
        setOptimizedEvacuationPlanCycles(0);
        this.project = project;
        this.setAssignment((project.getCurrentAssignment() != null) ? project.getCurrentAssignment() : project.getAssignments().get(0));
        this.setName(name);
        this.setUseCa(PropertyContainer.getInstance().getAsBoolean("editor.options.visualization.useCa"));
        this.setUseGraph(PropertyContainer.getInstance().getAsBoolean("editor.options.visualization.useGraph"));
        this.setCycles(cycles);
        this.setCaMaxTime(PropertyContainer.getInstance().getAsDouble("algo.ca.maxTime"));
        this.setGraphMaxTime(PropertyContainer.getInstance().getAsInt("algo.graph.MaxFlowOverTime.timeHorizon"));
        this.setGraphAlgo(ga);
        this.setCellularAutomatonAlgo(caa);
        this.useGraph = false;
        this.useCa = true;
        // Initialize the properties with the default property file: TODO: editable by options //
        this.setProperty(PropertyFilesSelectionModel.getInstance().getProperty("properties_test_evacuation.xml")); // was: properties.xml
        this.setEvacuationOptimizationType(EvacuationOptimizationType.None);
    }

    /**
     *
    /**
     * Creates a new BatchEntry.
     * @param name the name of the batch entry
     * @param project the project assigned to the entry
     * @param assignment
     * @param cycles the number of times the ca is simulated
     * @param ga the graph algorithm that is called
     * @param caa the cellular automaton algorithm that should be called
     * @param useCA indicates wheather simulation using ca is performed
     * @param caTime the maximal time used by ca
     * @param useGraph indicates wheather a graph optimization is used, or not
     * @param graphMaxTime the time horizon for the graph algorithms
     * @param eot the evacuation optimization type
     * @param eoRuns the number of evacuation optimization runs
     * @param property the properties used for this batch task
     * @throws IllegalArgumentException If the project is null or if it doesn't contain at least one assignment.
     */
    public BatchEntry(String name, Project project, Assignment assignment, boolean useCA, double caTime, int cycles, GraphAlgorithm ga, CellularAutomatonAlgorithm caa, boolean useGraph, int graphMaxTime, EvacuationOptimizationType eot, int eoRuns, Property property) throws IllegalArgumentException {
//		setOptimizedEvacuationPlanCycles( 0 );
        check(project);
        this.project = project;
        this.setAssignment(assignment);
        this.setName(name);
        this.setUseCa(useCA);
        this.setUseGraph(useGraph);
        this.setCycles(cycles);
        this.setCaMaxTime(caTime);
        this.setGraphMaxTime(graphMaxTime);
        this.setGraphAlgo(ga);
        this.setCellularAutomatonAlgo(caa);
        this.setProperty(property);
        this.setEvacuationOptimizationType(eot);
        this.setOptimizedEvacuationPlanCycles(eoRuns);
    }

	/**
	 * Checks if the project is valid. If it is not valid, an exception is thrown.
	 * The project must not be {@code null} and is not allowed to be empty.
	 * @param p the project
	 */
	private void check( Project p ) {
		if( p == null ) {
			throw new IllegalArgumentException( Localization.getInstance().getString( "batch.ProjectIsNullException" ) );
		} else if( p.getBuildingPlan().isEmpty() ) {
			throw new IllegalArgumentException( Localization.getInstance().getString( "batch.EmptyProjectException" ) );
		}// else if( !p.getBuildingPlan().hasEvacuationAreas() ) {
		//	throw new IllegalArgumentException( Localization.getInstance().getString( "batch.NoEvacAreas" ) );
		//}
	}

    /** Executes the chosen graph algorithm on the network flow model that
     * is stored in this batch entry and executes a given number of runs of
     * the given cellular automaton. If cycles is greater or equal to the
     * optimizedEvacuationPlanCycles, then all optimal cycles use the same
     * concrete assignments as the standard ca runs, else new concrete assignments
     * are generated only for the optimal computations.
     *
     * @return The results of the batch entry execution. These object in slot 1 is always
     * the result set for the unoptimized ca's (if present, else it is null) and the object
     * in slot 2 is always the result set for the optimized evacuations (if present, else it is null)
     * @throws ConversionNotSupportedException is possibly thrown by the underlying CA
     */
    public BatchResultEntry[] execute() throws ConversionNotSupportedException {
        System.out.println("BatchEntry");
        //System.setOut(null);
        Runtime runtime = Runtime.getRuntime();
        long memStart = (runtime.totalMemory() - runtime.freeMemory());
        JBatchProgressDialog bpd = new JBatchProgressDialog();

		boolean useCaRes = (useCa && cycles > 0) || (useGraph && optimizedEvacuationPlanCycles <= 0);
		boolean useOptRes = useGraph && optimizedEvacuationPlanCycles > 0;
		
		BatchResultEntry ca_res = new BatchResultEntry( name, new BuildingResults( project.getBuildingPlan() ) );
		BatchResultEntry opt_res = new BatchResultEntry( name + (useCaRes ? 
			" " + Localization.getInstance().getString( "gui.editor.JEditor.optimizedEvacuation" )
			: ""), new BuildingResults( project.getBuildingPlan() ) );

        // Try to load the file
        BatchLoadProperties blp;
        if (propertyFile == null) {
            blp = new BatchLoadProperties(null, caMaxTime);
        } else {
            blp = new BatchLoadProperties(propertyFile.getFile(), caMaxTime);
        }
        bpd.addTask(Localization.getInstance().getString("batch.LoadProperties"), blp);

        /* look how many ca's will be calculated */
        ConcreteAssignment[] concreteAssignments;
        //if( useCa )
        concreteAssignments = new ConcreteAssignment[Math.max(cycles, optimizedEvacuationPlanCycles)];
        //else
        //	concreteAssignments = new ConcreteAssignment[1];

        if (useCaRes) {
            ca_res.cas = new CellularAutomaton[cycles];
            ca_res.caStatistics = new CAStatistic[cycles];
            ca_res.caVis = new CAVisualizationResults[cycles];
        } else {
            ca_res.cas = null;
            ca_res.caStatistics = null;
            ca_res.caVis = null;
        }
        if (useOptRes) {
            opt_res.cas = new CellularAutomaton[optimizedEvacuationPlanCycles];
            opt_res.caStatistics = new CAStatistic[optimizedEvacuationPlanCycles];
            opt_res.caVis = new CAVisualizationResults[optimizedEvacuationPlanCycles];
        } else {
            opt_res.cas = null;
            opt_res.caStatistics = null;
            opt_res.caVis = null;
        }

		// CA-Part
		if( useCa ) {
			TreeMap<Integer, Integer> findMedian = new TreeMap<Integer, Integer>();
			for( int i = 0; i < cycles; i++ ) {
				CreateCellularAutomatonTask ccat = new CreateCellularAutomatonTask( ca_res, i, project );
				bpd.addTask( "Konvertiere zellulären Automat " + (i+1), ccat );
				BatchCATask bct = new BatchCATask( cellularAutomatonAlgo, ca_res, i, findMedian, project, assignment, concreteAssignments );
				bpd.addTask( String.format( Localization.getInstance().getString( "batch.CaCount" ), i + 1, getCycles() ), bct );
			}
			MedianTask mt = new MedianTask( ca_res, findMedian );
			bpd.addTask( Localization.getInstance().getString( "batch.ComputeMedian" ), mt );


            ComputeAvgStepPerSecondTask ct = new ComputeAvgStepPerSecondTask(ca_res);
            bpd.addTask(Localization.getInstance().getString("batch.ComputeCAStepAvg"), ct);
        }

        //Graph Part
        if (useGraph) {
            if (optimizedEvacuationPlanCycles <= 0) {
                int assignmentNumber = useCa ? 0 : -1;
                BatchGraphTask bgt = new BatchGraphTask(graphAlgo, ca_res, assignmentNumber, graphMaxTime,
                        project, assignment, concreteAssignments);
                bpd.addTask(Localization.getInstance().getString("batch.GraphAlgo"), bgt);
            } else if (optimizedEvacuationPlanCycles > 0) {
                // Run Graph algorithm

                BatchGraphTask bgt = new BatchGraphTask(graphAlgo, opt_res, -1, graphMaxTime, project, assignment, concreteAssignments);
                bpd.addTask(Localization.getInstance().getString("batch.GraphAlgo"), bgt);

                // Add concrete assignments (if we have too few CAs)
                for (int i = useCa ? cycles : 0; i < concreteAssignments.length; i++) {
                    concreteAssignments[i] = assignment.createConcreteAssignment(400);
                }

                TreeMap<Integer, Integer> findMedian = new TreeMap<Integer, Integer>();
                for (int i = 0; i < optimizedEvacuationPlanCycles; i++) {
                    // In case of multiple cycles we have to recompute the graph results and
                    // the evacuation plan assignments because every cycle works on another
                    // concreteAssignment
                    // Get the appropriate evacuation plan assignment
                    AssignmentTask mat = null;
                    switch (evacuationOptimizationType) {
                        case PersonalEvacuationPlan:
                            // Nothing to do - the ca itself does everything in this case
                            break;
                        case MinCost:
                            mat = new MinCostAssignmentTask(project, assignment, opt_res, concreteAssignments, i);
                            bpd.addTask(Localization.getInstance().getString("batch.evacuationPlan.MinCost"), mat);
                            break;
                        case EarliestArrivalTransshipment:
                            mat = new EarliestArrivalAssignmentTask(project, assignment, opt_res, concreteAssignments, i);
                            bpd.addTask(Localization.getInstance().getString("batch.evacuationPlan.EarliestArrival"), mat);
                            break;
                        case ReducedGraphEAT:
                            mat = new ReducedEarliestArrivalAssignmentTask(project, assignment, opt_res, concreteAssignments, i);
                            bpd.addTask(Localization.getInstance().getString("batch.evacuationPlan.ReducedEAT"), mat);
                            break;
                        case ShortestPathGraphEAT:
                            mat = new ShortestPathGraphEarliestArrivalAssignmentTask(project, assignment, opt_res, concreteAssignments, i);
                            bpd.addTask(Localization.getInstance().getString("batch.evacuationPlan.SPG_EAT"), mat);
                            break;
                        case ShortestPaths:
                            mat = new ShortestPathAssignmentTask(project, assignment, opt_res, concreteAssignments, i);
                            bpd.addTask(Localization.getInstance().getString("batch.evacuationPlan.ShortestPaths"), mat);
                            break;
                        case BestResponse:
                            break;
                    }

                    // Run CA task
                    switch (evacuationOptimizationType) {
                        case PersonalEvacuationPlan:
                            BatchEvacuationCATask bct = new BatchEvacuationCATask(cellularAutomatonAlgo, graphAlgo, opt_res, i,
                                    graphMaxTime, findMedian, project, assignment, concreteAssignments);
                            bpd.addTask(String.format(Localization.getInstance().getString("batch.EvacCount"),
                                    i + 1, optimizedEvacuationPlanCycles), bct);
                            break;
                        case MinCost:
                        case EarliestArrivalTransshipment:
                        case ReducedGraphEAT:
                        case ShortestPathGraphEAT:
                        case ShortestPaths:
                            BatchCA2Task bct2 = new BatchCA2Task(project, assignment, opt_res, mat, concreteAssignments,
                                    i, cellularAutomatonAlgo, findMedian);
                            bpd.addTask(String.format(Localization.getInstance().getString("batch.EvacCount"),
                                    i + 1, optimizedEvacuationPlanCycles), bct2);
                            break;
                        case BestResponse:
                            BestResponseAssignmentTask brat = new BestResponseAssignmentTask(project, opt_res, concreteAssignments, i, findMedian, assignment, cellularAutomatonAlgo);
                            bpd.addTask(String.format(Localization.getInstance().getString("batch.EvacCount"), i + 1, optimizedEvacuationPlanCycles), brat);
                            break;
                    }
                }

                MedianTask mt = new MedianTask(opt_res, findMedian);
                bpd.addTask(Localization.getInstance().getString("batch.ComputeMedian"), mt);

                ComputeAvgStepPerSecondTask ct = new ComputeAvgStepPerSecondTask(opt_res);
                bpd.addTask(Localization.getInstance().getString("batch.ComputeCAStepAvg"), ct);
            }
        }

        bpd.start();
        bpd.setVisible(true);

        BatchResultEntry[] result = new BatchResultEntry[2];
        // Compute final statistics after ALL CAs have been created
        if (useCaRes) {
            if (useCa) {
                ca_res.mccaStatistic = new MultipleCycleCAStatistic(ca_res.caStatistics);
            }
            result[0] = ca_res;
        }
        if (useOptRes) {
            opt_res.mccaStatistic = new MultipleCycleCAStatistic(opt_res.caStatistics);
            result[1] = opt_res;
        }
        long memEnd = (runtime.totalMemory() - runtime.freeMemory());
        System.out.println("Speicher für Lauf: " + (memEnd - memStart) + " Bytes");
        return result;
    }

    /** @return the selected cellular automaton algorithm */
    public CellularAutomatonAlgorithm getCellularAutomatonAlgo() {
        return cellularAutomatonAlgo;
    }

    /**
     * Sets the selected cellular automaton algorithm
     * @param cellularAutomatonAlgo the selected algorithm
     */
    public void setCellularAutomatonAlgo(CellularAutomatonAlgorithm cellularAutomatonAlgo) {
        this.cellularAutomatonAlgo = cellularAutomatonAlgo;
    }

    /** @return Which one of the available graph algos will be used with this batch entry. */
    public GraphAlgorithm getGraphAlgo() {
        return graphAlgo;
    }

    /** @return the selected property. */
    public PropertyFilesSelectionModel.Property getProperty() {
        return propertyFile;
    }

    /** @return The name of the batch entry (Just for the user). */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGraphAlgo(GraphAlgorithm graphAlgo) {
        this.graphAlgo = graphAlgo;
    }

    /** @param propertyFile the selected property */
    public void setProperty(PropertyFilesSelectionModel.Property propertyFile) {
        this.propertyFile = propertyFile;
    }

    /** @return If this BatchEntry shall simulate the given project using a CA. */
    public boolean getUseCa() {
        return useCa;
    }

    public void setUseCa(boolean useCa) {
        this.useCa = useCa;
    }

    /** @return If this BatchEntry shall compute an optimal solution using graph algorithms. */
    public boolean getUseGraph() {
        return useGraph;
    }

    public void setUseGraph(boolean useGraph) {
        this.useGraph = useGraph;
    }

    /** @return How many cycles of the corresponding CA shall be performed. */
    public int getCycles() {
        return cycles;
    }

    /** @throws IllegalArgumentException When cycles is equal to zero or less than zero. */
    public void setCycles(int cycles) throws IllegalArgumentException {
        if (cycles <= 0) {
            throw new IllegalArgumentException(Localization.getInstance().getString("batch.NumberOfCyclesException"));
        }
        this.cycles = cycles;
    }

    /** @return Which assignment (must be in the project) shall be used for the batch entry. */
    public Assignment getAssignment() {
        return assignment;
    }

    /** @throws IllegalArgumentException If assignment is null or not in the project. */
    public void setAssignment(Assignment assignment) throws IllegalArgumentException {
        if (assignment == null || !project.getAssignments().contains(assignment)) {
            throw new IllegalArgumentException(Localization.getInstance().getString("batch.AssignmentIsNullException"));
        }
        this.assignment = assignment;
    }

    public Project getProject() {
        return project;
    }

    /** @return The maximum time for the simulation. All individual who fail to be
     * evacuated within this caMaxTime will be counted as dead. */
    public double getCaMaxTime() {
        return caMaxTime;
    }

    /** @throws IllegalArgumentException If the caMaxTime is less than or equal to zero. */
    public void setCaMaxTime(double caMaxTime) {
        if (caMaxTime <= 0) {
            throw new IllegalArgumentException(Localization.getInstance().getString("batch.caMaxTimeException"));
        }
        this.caMaxTime = caMaxTime;
    }

    /** @return The maximum time for the optimization. This time is not used by all graph algorithms. */
    public int getGraphMaxTime() {
        return graphMaxTime;
    }

    /** @throws IllegalArgumentException If the graphMaxTime is less than or equal to zero. */
    public void setGraphMaxTime(int graphMaxTime) {
        if (graphMaxTime <= 0) {
            throw new IllegalArgumentException(Localization.getInstance().getString("batch.caMaxTimeException"));
        }
        this.graphMaxTime = graphMaxTime;
    }

    /** @return How many simulation cycles shall be performed using the optimal
     * evacuation plan determined by the selected graph algorithm. */
    public int getOptimizedEvacuationPlanCycles() {
        return optimizedEvacuationPlanCycles;
    }

    /** @param optimizedEvacuationPlanCycles How many simulation cycles shall be performed
     * using the optimal evacuation plan determined by the selected graph algorithm.
     *
     * This parameter has no effect if the current Entry doesn't use a graph algorithm.
     * @throws IllegalArgumentException When cycles is less than zero. */
    public void setOptimizedEvacuationPlanCycles(int optimizedEvacuationPlanCycles) {
        if (optimizedEvacuationPlanCycles < 0) {
            throw new IllegalArgumentException(Localization.getInstance().getString("batch.NumberOfCyclesException"));
        }
        this.optimizedEvacuationPlanCycles = optimizedEvacuationPlanCycles;
    }

    public EvacuationOptimizationType getEvacuationOptimizationType() {
        return evacuationOptimizationType;
    }

    public void setEvacuationOptimizationType(EvacuationOptimizationType evacuationOptimizationType) {
        this.evacuationOptimizationType = evacuationOptimizationType;
    }
}
