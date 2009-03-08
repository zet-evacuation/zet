/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/**
 * Class BatchCATask
 * Erstellt 20.07.2008, 23:54:19
 */
package batch.tasks;

import algo.ca.EvacuationCellularAutomatonAlgorithm;
import batch.BatchResultEntry;
import batch.CellularAutomatonAlgorithm;
import converter.ZToCAConverter;
import converter.ZToCAConverter.ConversionNotSupportedException;
import ds.Project;
import ds.PropertyContainer;
import ds.z.Assignment;
import ds.z.AssignmentType;
import ds.z.ConcreteAssignment;
import ds.ca.CellularAutomaton;
import ds.ca.results.VisualResultsRecorder;
import io.visualization.CAVisualizationResults;
import java.util.TreeMap;
import statistic.ca.CAStatistic;

/**
 * A task that is called during the batch execution. It performs one run of a
 * {@link CellularAutomaton}. The automaton is created before.
 * @author Jan-Philipp Kappmeier
 */
public class BatchCATask implements Runnable {

	/** The cellular automaton algorithm enumeration object. */
	private CellularAutomatonAlgorithm cellularAutomatonAlgo;
	/** The batch object which stores the calculated results. */
	private BatchResultEntry res;
	/** The number of the run, used for accessing the result in {@link res} */
	private int runNumber;
	/** A map storing statistics with run number as key. */
	private TreeMap<Integer, Integer> median;
	/** The {@link ds.z.Project} */
	private Project project;
	/** The used assignment for the ca run. */
	private Assignment assignment;
	/** The array with concrete assignments. */
	private ConcreteAssignment[] concreteAssignments;

	/**
	 * Initializes a new instance of this task.
	 * @param cellularAutomatonAlgo the cellular automaton enumeration containing the ca algorithm that should be used
	 * @param res the object containing which stores the results.
	 * @param runNumber the number of the run, used to access the results
	 * @param median a map storing the statistic, used to calculate median
	 * @param project the project on which the ca runs
	 * @param assignment the selected assignment
	 * @param concreteAssignments the array with calculated concrete assignments
	 */
	public BatchCATask (CellularAutomatonAlgorithm cellularAutomatonAlgo, BatchResultEntry res,
			int runNumber, TreeMap<Integer, Integer> median, Project project, Assignment assignment,
			ConcreteAssignment[] concreteAssignments) {
		this.cellularAutomatonAlgo = cellularAutomatonAlgo;
		this.res = res;
		this.runNumber = runNumber;
		this.median = median;
		this.assignment = assignment;
		this.project = project;
		this.concreteAssignments = concreteAssignments;
	}

	/**
	 * Runs a cellular automaton. At first, the automaton is created. After that
	 * the algorithm stored in the submitted {@link CellularAutomatonAlgorithm} is
	 * executed. After execution the results are stored in an
	 * {@link BatchResultEntry}.
	 */
	public void run () {
		EvacuationCellularAutomatonAlgorithm caAlgo;
		CellularAutomaton ca;
		try {
			ca = ZToCAConverter.getInstance ().convert (project.getPlan ());
		} catch (ConversionNotSupportedException e) {
			e.printStackTrace ();
			return;
		}
		res.setCellularAutomaton (runNumber, ca);
		for (AssignmentType at : assignment.getAssignmentTypes ()) {
			ca.setAssignmentType (at.getName (), at.getUid ());
		}
		ConcreteAssignment concreteAssignment;
		concreteAssignment = assignment.createConcreteAssignment (400);
		concreteAssignments[runNumber] = concreteAssignment;
		ZToCAConverter.applyConcreteAssignment (concreteAssignment);

		caAlgo = cellularAutomatonAlgo.createTask (ca);
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
