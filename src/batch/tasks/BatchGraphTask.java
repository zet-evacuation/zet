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
 * Class BatchGraphTask
 * Erstellt 22.07.2008, 01:06:15
 */
package batch.tasks;

import batch.BatchResultEntry;
import zet.tasks.GraphAlgorithmEnumeration;
import batch.tasks.graph.SuccessiveEarliestArrivalAugmentingPathOptimizedTask;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmListener;
import ds.z.Project;
import ds.GraphVisualizationResults;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.z.Assignment;
import ds.z.ConcreteAssignment;

/**
 * A task that executes a graph algorithm.
 * @author Jan-Philipp Kappmeier
 */
public class BatchGraphTask implements Runnable {

    /** The graph algorithm enumeration object. */
    private GraphAlgorithmEnumeration graphAlgo;
    /** The batch object which stores the calculated results. */
    private BatchResultEntry res;
    /** The number of the run, used for accessing the result in {@link res} */
    private int runNumber;
    /** The maximal time for the graph algorithm, if used. */
    private int maxTime;
    /** The {@link ds.z.Project} */
    private Project project;
    /** The used assignment for the ca run. */
    private Assignment assignment;
    /** The concrete assignment. */
    private ConcreteAssignment[] concreteAssignments = null;

    /**
     * Initializes a new instance of this task.
     * @param graphAlgo the graph algorithm automaton enumeration containing the graph algorithm that should be used
     * @param res the object containing which stores the results
     * @param runNumber the number of the run, used to access the results
     * @param maxTime the maximal time for the algorithm, if needed
     * @param project the project on which the ca runs
     * @param assignment the selected assignment
     * @param concreteAssignments the concrete assignments that were already calculated for the cellular automaton. can be null.
     */
    public BatchGraphTask(GraphAlgorithmEnumeration graphAlgo, BatchResultEntry res, int runNumber, int maxTime, Project project, Assignment assignment, ConcreteAssignment[] concreteAssignments) {
        this.graphAlgo = graphAlgo;
        this.res = res;
        this.runNumber = runNumber;
        this.maxTime = maxTime;
        this.project = project;
        this.assignment = assignment;
        this.concreteAssignments = concreteAssignments;
    }
    public AlgorithmListener listener;

	/**
	 * Runs a graph algorithm. At first the {@link ds.NetworkFlowModel}
	 * is created. After that the algorithm stored in the submitted
	 * {@link GraphAlgorithmEnumeration} is executed. After execution the results are stored in an
	 * {@link BatchResultEntry}.
	 */
	public void run() {
		//Build Graph
		NetworkFlowModel nfo = new NetworkFlowModel();
		res.setNetworkFlowModel( nfo );
		//ZToGraphConverter.convertBuildingPlan( project.getBuildingPlan(), nfo );
        //Run graph algo on the last CA instance if possible
        ConcreteAssignment concreteAssignment;
        if (runNumber < 0) {
            concreteAssignment = assignment.createConcreteAssignment(400);
            
        } else {
            concreteAssignment = concreteAssignments[runNumber];

            
        }
      //  ZToGraphConverter.convertConcreteAssignment(concreteAssignment, nfo);
        Algorithm<NetworkFlowModel, PathBasedFlowOverTime> gt = null;
        gt = graphAlgo.createTask(nfo, maxTime);
        gt.setProblem(nfo);
        if (gt instanceof SuccessiveEarliestArrivalAugmentingPathOptimizedTask && listener != null) {
            ((SuccessiveEarliestArrivalAugmentingPathOptimizedTask) gt).addAlgorithmListener(listener);
        }
        gt.run();

        res.setFlow(gt.getSolution());
        res.setGraphVis(new GraphVisualizationResults(nfo, gt.getSolution()));
        //res.setCompVis(new CompareVisualizationResults(gt.));
        // Forget the used batch result entry. This is necessary in case that the batch entries
        // are stored on disk. Then this reference will inhibit the deletion of the batch result entry
        res = null;
    }
}
