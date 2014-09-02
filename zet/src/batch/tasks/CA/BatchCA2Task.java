/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
/**
 * Class BatchCA2Task
 * Erstellt 23.11.2008, 23:03:13
 */

package batch.tasks.CA;

import algo.ca.framework.EvacuationCellularAutomatonAlgorithm;
import algo.graph.exitassignment.ExitAssignment;
import batch.BatchResultEntry;
import batch.tasks.AssignmentTask;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter.ConversionNotSupportedException;
import ds.PropertyContainer;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.results.VisualResultsRecorder;
import de.tu_berlin.coga.zet.model.Assignment;
import de.tu_berlin.coga.zet.model.AssignmentType;
import de.tu_berlin.coga.zet.model.ConcreteAssignment;
import de.tu_berlin.coga.zet.model.Project;
import exitdistributions.ExitDistributionZToCAConverter;
import io.visualization.EvacuationSimulationResults;
import java.util.TreeMap;
import statistic.ca.CAStatistic;
import zet.tasks.CellularAutomatonAlgorithms;

/**
 * A task that is called during the batch execution. It performs one run of a
 * {@link EvacuationCellularAutomaton} using an {@link ExitAssignment}. The automaton is
 * created before.
 * @author Jan-Philipp Kappmeier
 */
public class BatchCA2Task implements Runnable {
	/** The {@link de.tu_berlin.coga.zet.model.Project} */
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
	private CellularAutomatonAlgorithms cellularAutomatonAlgo;
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
	public BatchCA2Task( Project project, Assignment assignment, BatchResultEntry res, AssignmentTask exitAssignmentTask, ConcreteAssignment[] concreteAssignments, int runNumber, CellularAutomatonAlgorithms cellularAutomatonAlgo, TreeMap<Integer, Integer> median ) {
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
		EvacuationCellularAutomaton ca2 = null;
		//try {
			//ca2 = ExitDistributionZToCAConverter.getInstance().convertAndApplyConcreteAssignment( project.getBuildingPlan(), exitAssignmentTask.getExitAssignment(), concreteAssignments[runNumber], res.getNetworkFlowModel().getZToGraphMapping().getRaster() );
		//	res.setCellularAutomaton(runNumber, ca2 );
		//} catch( ConversionNotSupportedException ex ) {
			System.err.println( "ConversionNotSupportedException ist aufgetreten. Dies sollte eigentlich nicht passieren..." );
		//	return;
		//}


		for (AssignmentType at : assignment.getAssignmentTypes ()) {
			ca2.setAssignmentType (at.getName (), at.getUid ());
		}

		EvacuationCellularAutomatonAlgorithm caAlgo;
		//caAlgo = cellularAutomatonAlgo.createTask( ca2 );
		double caMaxTime = PropertyContainer.getInstance ().getAsDouble ("algo.ca.maxTime");
		//caAlgo.setMaxTimeInSeconds (caMaxTime);

		long start;
		long end;

		//Run the CA
		start = System.currentTimeMillis ();
		//caAlgo.getCellularAutomaton ().startRecording ();
		//caAlgo.run ();	// hier wird initialisiert
		//caAlgo.getCellularAutomaton ().stopRecording ();
		end = System.currentTimeMillis ();
		//System.out.println ("Laufzeit CA:" + (end - start) + " ms");

		// Get the results
		CAStatistic statistic = null;//new CAStatistic (caAlgo.getCaController ().getCaStatisticWriter ().getStoredCAStatisticResults ());
		res.setCellularAutomatonStatistic (runNumber, statistic);
    // TODO RASTEvacuationSimulationResultssults visEvacuationSimulationResultsionResults ( VisualResultsRecorder.getInstance ().getRecording (), nEvacuationSimulationResultslizationREvacuationSimulationResultsVisualizationResults ( VisualResultsRecorder.getInstance ().getRecording (), ZToCAConverter.getInstance ().getLatestMapping ());
   	//visres.statistic = statistic;

		//res.setCellularAutomatonVisualization (runNumber, visres );

		// Gather median information
		//median.put (new Integer (caAlgo.getCellularAutomaton ().getTimeStep ()), runNumber);

		// Forget the used batch result entry. This is necessary in case that the batch entries
		// are stored on disk. Then this reference will inhibit the deletion of the batch result entry
		res = null;
	}
}
