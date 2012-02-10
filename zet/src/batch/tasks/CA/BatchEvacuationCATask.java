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
/**
 * Class BatchEvacuationCATask
 * Erstellt 22.07.2008, 23:44:41
 */
package batch.tasks.CA;

import io.visualization.CAVisualizationResults;

import java.util.TreeMap;

import algo.ca.EvacuationPlanSwapCellularAutomatonInOrder;
import batch.BatchResultEntry;
import zet.tasks.CellularAutomatonAlgorithmEnumeration;
import zet.tasks.GraphAlgorithmEnumeration;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter.ConversionNotSupportedException;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.z.Project;
import ds.PropertyContainer;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.results.VisualResultsRecorder;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.z.Assignment;
import ds.z.AssignmentType;
import ds.z.ConcreteAssignment;
import evacuationplan.FlowBasedCAFactory;

/**
 * A task that is called during the batch execution. It performs one run of a
 * {@link EvacuationCellularAutomaton}. The automaton is created before.
 * @author Jan-Philipp Kappmeier
 */
public class BatchEvacuationCATask implements Runnable {

	/** The cellular automaton algorithm enumeration object. */
	private CellularAutomatonAlgorithmEnumeration cellularAutomatonAlgo;
	/** The graph algorithm enumeration object. */
	private GraphAlgorithmEnumeration graphAlgo;
	/** The number of the run, used for accessing the result in {@link res} */
	private int maxTime;
	/** The concrete assignment. */
	private ConcreteAssignment[] concreteAssignments;
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

	/**
	 * Initializes a new instance of this task.
	 * @param cellularAutomatonAlgo the cellular automaton enumeration containing the ca algorithm that should be used
	 * @param graphAlgo the graph algorithm automaton enumeration containing the graph algorithm that should be used
	 * @param res the object containing which stores the results
	 * @param runNumber the number of the run, used to access the results
	 * @param maxTime the maximal time for the graph algorithm, if needed
	 * @param median a map storing the statistic, used to calculate median
	 * @param project the project on which the ca runs
	 * @param assignment the selected assignment
	 * @param concreteAssignments the concrete assignments that were already used for the cellular automaton
	 */
	public BatchEvacuationCATask (CellularAutomatonAlgorithmEnumeration cellularAutomatonAlgo, GraphAlgorithmEnumeration graphAlgo,
			BatchResultEntry res, int runNumber, int maxTime, TreeMap<Integer, Integer> median,
			Project project, Assignment assignment, ConcreteAssignment[] concreteAssignments) {
		this.cellularAutomatonAlgo = cellularAutomatonAlgo;
		this.graphAlgo = graphAlgo;
		this.res = res;
		this.runNumber = runNumber;
		this.maxTime = maxTime;
		this.median = median;
		this.assignment = assignment;
		this.concreteAssignments = concreteAssignments;
		this.project = project;
	}

	/**
	 * Runs a cellular automaton. At first, the automaton is created. After that
	 * the algorithm stored in the submitted {@link CellularAutomatonAlgorithmEnumeration} is
	 * executed. After execution the results are stored in an
	 * {@link BatchResultEntry}.
	 */
	public void run () {
		// Create CA with evacuation plans according to calculated transshipment

		//NetworkFlowModel nfo = res.getNetworkFlowModel ();
		NetworkFlowModel nfo = new NetworkFlowModel();
		res.setNetworkFlowModel( nfo );
		//ZToGraphConverter.convertBuildingPlan( project.getBuildingPlan(), nfo );

		ConcreteAssignment concreteAssignment;
		if (concreteAssignments == null) {
			concreteAssignment = assignment.createConcreteAssignment (400);
		} else {
			concreteAssignment = concreteAssignments[runNumber];
		}

		//ZToGraphConverter.convertConcreteAssignment( concreteAssignment, nfo );
		Algorithm<NetworkFlowModel, PathBasedFlowOverTime> gt = null;
		gt = graphAlgo.createTask (nfo, maxTime);
                gt.setProblem(nfo);
		gt.run ();

		
		EvacuationCellularAutomaton ca;
		try {
//			ca = FlowBasedCAFactory.getFlowBasedCAFactoryInstance ().convertAndApplyConcreteAssignment (project.getBuildingPlan (), gt.getDynamicFlow (), concreteAssignment, nfo.getZToGraphMapping ().
			ca = FlowBasedCAFactory.getFlowBasedCAFactoryInstance ().convertAndApplyConcreteAssignment (project.getBuildingPlan (), gt.getSolution (), concreteAssignment, nfo.getZToGraphMapping ().
					getRaster ());
		} catch (ConversionNotSupportedException ex) {
			ex.printStackTrace ();
			return;
		}

		res.setCellularAutomaton (runNumber, ca);
		for (AssignmentType at : assignment.getAssignmentTypes ()) {
			ca.setAssignmentType (at.getName (), at.getUid ());
		}

		EvacuationPlanSwapCellularAutomatonInOrder caAlgo = null;//new EvacuationPlanSwapCellularAutomatonInOrder (ca, FlowBasedCAFactory.getFlowBasedCAFactoryInstance ().getLatestCheckerInstance ());

		//Run the CA
		long start = System.currentTimeMillis ();
		// THREAD start
		//caAlgo.setMaxTimeInSeconds( caMaxTime );
		double caMaxTime = PropertyContainer.getInstance ().getAsDouble ("algo.ca.maxTime");
		caAlgo.setMaxTimeInSeconds (caMaxTime);

		caAlgo.getCellularAutomaton ().startRecording ();
		caAlgo.run ();
		caAlgo.getCellularAutomaton ().stopRecording ();
		// THREAD end
		long end = System.currentTimeMillis ();
		System.out.println ("Laufzeit optimierter CA:" + (end - start) + " ms");

		// Get the results
//		res.setCellularAutomatonStatistic (runNumber, new CAStatistic (caAlgo.getCaController ().getCaStatisticWriter ().			getStoredCAStatisticResults ()));
		res.setCellularAutomatonVisualization (runNumber, new CAVisualizationResults (VisualResultsRecorder.getInstance ().
				getRecording (), FlowBasedCAFactory.getFlowBasedCAFactoryInstance ().getMapping ()));

		// Gather median information
		median.put (new Integer (caAlgo.getCellularAutomaton ().getTimeStep ()), runNumber);
		
		// Forget the used batch result entry. This is necessary in case that the batch entries
		// are stored on disk. Then this reference will inhibit the deletion of the batch result entry
		res = null;
	}
}
