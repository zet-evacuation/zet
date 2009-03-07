/**
 * Class BatchCA2Task
 * Erstellt 23.11.2008, 23:03:13
 */

package batch.tasks;

import algo.ca.EvacuationCellularAutomatonAlgorithm;
import algo.graph.exitassignment.MinimumCostTransshipmentExitAssignment;
import batch.BatchResultEntry;
import batch.CellularAutomatonAlgorithm;
import converter.ZToCAConverter;
import converter.ZToCAConverter.ConversionNotSupportedException;
import ds.Project;
import ds.PropertyContainer;
import ds.ca.CellularAutomaton;
import ds.ca.results.VisualResultsRecorder;
import ds.z.Assignment;
import ds.z.AssignmentType;
import ds.z.ConcreteAssignment;
import exitdistributions.ExitDistributionBasedCAFactory;
import io.visualization.CAVisualizationResults;
import java.util.TreeMap;
import statistic.ca.CAStatistic;

/**
 * A task that is called during the batch execution. It performs one run of a
 * {@link CellularAutomaton} using an {@link ExitAssignment}. The automaton is
 * created before.
 * @author Jan-Philipp Kappmeier
 */
public class BatchCA2Task implements Runnable {
	/** The {@link ds.z.Project} */
	private Project project;
	/** The used assignment for the ca run. */
	private Assignment assignment;
	/** The batch object which stores the calculated results. */
	private BatchResultEntry res;
	/** A task that must be executed befor this that calculates the exit assignment. */
	AssignmentTask exitAssignmentTask;
	/** The number of the run, used for accessing the result in {@link res} */
	private int runNumber;
	/** The cellular automaton algorithm enumeration object. */
	private CellularAutomatonAlgorithm cellularAutomatonAlgo;
	/** A map storing statistics with run number as key. */
	private TreeMap<Integer, Integer> median;
	/** The array with concrete assignments. */
	private ConcreteAssignment[] concreteAssignments;

	/**
	 * Initializes a new instance of this task.
	 * @param cellularAutomatonAlgo the cellular automaton enumeration containing the ca algorithm that should be used
	 * @param res the object containing which stores the results.
	 * @param runNumber the number of the run, used to access the results
	 * @param exitAssignmentTask a task calculating an exit assignment. Must be executed befor this task is executed
	 * @param median a map storing the statistic, used to calculate median
	 * @param project the project on which the ca runs
	 * @param assignment the selected assignment
	 * @param concreteAssignments the array with calculated concrete assignments
	 */
	public BatchCA2Task( Project project, Assignment assignment, BatchResultEntry res, AssignmentTask exitAssignmentTask, ConcreteAssignment[] concreteAssignments, int runNumber, CellularAutomatonAlgorithm cellularAutomatonAlgo, TreeMap<Integer, Integer> median ) {
		this.project = project;
		this.assignment = assignment;
		this.res = res;
		this.exitAssignmentTask = exitAssignmentTask;
		this.concreteAssignments = concreteAssignments;
		this.cellularAutomatonAlgo = cellularAutomatonAlgo;
		this.runNumber = runNumber;
		this.median = median;
	}

	/**
	 * Runs this task. A new cellular automaton instance is created, the exit
	 * assignment assigned and the cellular automaton is executed.
	 */
	public void run() {
		CellularAutomaton ca2;
		try {
			ca2 = ExitDistributionBasedCAFactory.getInstance().convertAndApplyConcreteAssignment( project.getPlan(), exitAssignmentTask.getExitAssignment(), concreteAssignments[runNumber], res.getNetworkFlowModel().getZToGraphMapping().getRaster() );
			res.setCellularAutomaton(runNumber, ca2 );
		} catch( ConversionNotSupportedException ex ) {
			System.err.println( "ConversionNotSupportedException ist aufgetreten. Dies sollte eigentlich nicht passieren..." );
			return;
		}	

	
		for (AssignmentType at : assignment.getAssignmentTypes ()) {
			ca2.setAssignmentType (at.getName (), at.getUid ());
		}

		EvacuationCellularAutomatonAlgorithm caAlgo;
		caAlgo = cellularAutomatonAlgo.createTask( ca2 );
		double caMaxTime = PropertyContainer.getInstance ().getAsDouble ("algo.ca.maxTime");
		caAlgo.setMaxTimeInSeconds (caMaxTime);

		long start;
		long end;

		//Run the CA
		start = System.currentTimeMillis ();
		caAlgo.getCellularAutomaton ().startRecording ();
		caAlgo.run ();	// hier wird initialisiert
		caAlgo.getCellularAutomaton ().stopRecording ();
		end = System.currentTimeMillis ();
		System.out.println ("Laufzeit CA:" + (end - start) + " ms");

		// Get the results
		res.setCellularAutomatonStatistic (runNumber, new CAStatistic (caAlgo.getCaController ().getCaStatisticWriter ().
				getStoredCAStatisticResults ()));
		res.setCellularAutomatonVisualization (runNumber, new CAVisualizationResults (
				VisualResultsRecorder.getInstance ().getRecording (),
				ZToCAConverter.getInstance ().getLatestMapping ()));

		// Gather median information
		median.put (new Integer (caAlgo.getCellularAutomaton ().getTimeStep ()), runNumber);
		
		// Forget the used batch result entry. This is necessary in case that the batch entries
		// are stored on disk. Then this reference will inhibit the deletion of the batch result entry
		res = null;
	}
}
