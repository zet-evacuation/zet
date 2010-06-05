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
package batch.tasks.assignment;

import algo.ca.EvacuationCellularAutomatonAlgorithm;
import batch.BatchResultEntry;
import converter.ZToCAConverter;
import converter.ZToCAConverter.ConversionNotSupportedException;
import ds.Project;
import ds.PropertyContainer;
import ds.ca.CellularAutomaton;
import ds.ca.results.VisualResultsRecorder;
import ds.z.Assignment;
import ds.z.AssignmentType;
import ds.z.ConcreteAssignment;
import io.visualization.CAVisualizationResults;
import java.util.TreeMap;
import statistic.ca.CAStatistic;
import batch.CellularAutomatonAlgorithm;
import batch.tasks.AssignmentTask;
import evacuationplan.BestResponseDynamics;
import exitdistributions.ExitCapacityBasedCAFactory;

/**
 *  
 */
public class BestResponseAssignmentTask extends AssignmentTask {
	/** The {@link ds.z.Project} */
	private Project project;
	/** The batch object which stores the calculated results. */
	private BatchResultEntry res;
	/** The concrete assignments. */
	private ConcreteAssignment[] concreteAssignments;
	/** The run number. */
	private int runNumber;
	private TreeMap<Integer, Integer> median;
	private Assignment assignment;
	private CellularAutomatonAlgorithm cellularAutomatonAlgo;

	/**
	 * Initializes a new instance of this task.
	 * @param res the object containing which stores the results
	 * @param runNumber the number of the run, used to access the results
	 * @param project the project on which the ca runs
	 * @param assignment the selected assignment
	 * @param concreteAssignments the concrete assignments that were already calculated for the cellular automaton. can be null.
	 */
	public BestResponseAssignmentTask( Project project, BatchResultEntry res, ConcreteAssignment[] concreteAssignments, int runNumber, TreeMap<Integer, Integer> median, Assignment assignment, CellularAutomatonAlgorithm cellularAutomatonAlgo ) {
		this.project = project;
		this.res = res;
		this.concreteAssignments = concreteAssignments;
		this.runNumber = runNumber;
		this.median = median;
		this.assignment = assignment;
		this.cellularAutomatonAlgo = cellularAutomatonAlgo;
	}

	/**
	 * Calculates the exit assignment with minimum cost flow. After that it can
	 * be delivered using {@link #getExitAssignment()}.
	 */
	public void run() {

		CellularAutomaton ca2;
		try {
			ca2 = ExitCapacityBasedCAFactory.getInstance().convertAndApplyConcreteAssignment( project.getBuildingPlan(), res.getNetworkFlowModel(), concreteAssignments[runNumber], res.getNetworkFlowModel().getZToGraphMapping().getRaster() );
			res.setCellularAutomaton( runNumber, ca2 );
		} catch( ConversionNotSupportedException ex ) {
			System.err.println( "ConversionNotSupportedException ist aufgetreten. Dies sollte eigentlich nicht passieren..." );
			return;
		}


		for( AssignmentType at : assignment.getAssignmentTypes() )
			ca2.setAssignmentType( at.getName(), at.getUid() );

		EvacuationCellularAutomatonAlgorithm caAlgo;
		caAlgo = cellularAutomatonAlgo.createTask( ca2 );
		double caMaxTime = PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		caAlgo.setMaxTimeInSeconds( caMaxTime );

		long start;
		long end;

		//Run the CA
		start = System.currentTimeMillis();
		caAlgo.getCellularAutomaton().startRecording();

		caAlgo.initialize();

		// perform initial best response dynamics exit selection
		BestResponseDynamics brd = new BestResponseDynamics();
		brd.computeAssignmentBasedOnBestResponseDynamics( caAlgo.getCellularAutomaton(), caAlgo.getIndividuals() );


		caAlgo.run();	// hier wird initialisiert
		caAlgo.getCellularAutomaton().stopRecording();
		end = System.currentTimeMillis();
		//System.out.println ("Laufzeit CA:" + (end - start) + " ms");

		// Get the results
		res.setCellularAutomatonStatistic( runNumber, new CAStatistic( caAlgo.getCaController().getCaStatisticWriter().
						getStoredCAStatisticResults() ) );
		res.setCellularAutomatonVisualization( runNumber, new CAVisualizationResults(
						VisualResultsRecorder.getInstance().getRecording(),
						ZToCAConverter.getInstance().getLatestMapping() ) );

		// Gather median information
		median.put( new Integer( caAlgo.getCellularAutomaton().getTimeStep() ), runNumber );

		// Forget the used batch result entry. This is necessary in case that the batch entries
		// are stored on disk. Then this reference will inhibit the deletion of the batch result entry
		res = null;
	}


}


