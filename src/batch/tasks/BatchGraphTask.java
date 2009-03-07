/**
 * Class BatchGraphTask
 * Erstellt 22.07.2008, 01:06:15
 */
package batch.tasks;

import tasks.GraphAlgorithmTask;
import batch.BatchResultEntry;
import batch.GraphAlgorithm;
import converter.ZToGraphConverter;
import ds.Project;
import ds.graph.GraphVisualizationResult;
import ds.graph.NetworkFlowModel;
import ds.z.Assignment;
import ds.z.ConcreteAssignment;

/**
 * A task that executes a graph algorithm.
 * @author Jan-Philipp Kappmeier
 */
public class BatchGraphTask implements Runnable {

	/** The graph algorithm enumeration object. */
	private GraphAlgorithm graphAlgo;
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
	public BatchGraphTask( GraphAlgorithm graphAlgo, BatchResultEntry res, int runNumber, int maxTime, Project project, Assignment assignment, ConcreteAssignment[] concreteAssignments ) {
		this.graphAlgo = graphAlgo;
		this.res = res;
		this.runNumber = runNumber;
		this.maxTime = maxTime;
		this.project = project;
		this.assignment = assignment;
		this.concreteAssignments = concreteAssignments;
	}

	/**
	 * Runs a graph algorithm. At first the {@link ds.graph.NetworkFlowModel}
	 * is created. After that the algorithm stored in the submitted
	 * {@link GraphAlgorithm} is executed. After execution the results are stored in an
	 * {@link BatchResultEntry}.
	 */
	public void run() {
		//Build Graph
		NetworkFlowModel nfo = new NetworkFlowModel();
		res.setNetworkFlowModel( nfo );
		ZToGraphConverter.convertBuildingPlan( project.getPlan(), nfo );

		//Run graph algo on the last CA instance if possible
		ConcreteAssignment concreteAssignment;
		if( runNumber < 0 )
			concreteAssignment = assignment.createConcreteAssignment( 400 );
		else
			concreteAssignment = concreteAssignments[runNumber];

		ZToGraphConverter.convertConcreteAssignment( concreteAssignment, nfo );
		GraphAlgorithmTask gt = null;
		gt = graphAlgo.createTask( nfo, maxTime );
		gt.run();

		res.setFlow( gt.getDynamicFlow() );
		res.setGraphVis( new GraphVisualizationResult( nfo, gt.getDynamicFlow() ) );
		
		// Forget the used batch result entry. This is necessary in case that the batch entries
		// are stored on disk. Then this reference will inhibit the deletion of the batch result entry
		res = null;
	}
}
