package tasks;

import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.NetworkFlowModel;

/** The superclass of all tasks who execute graph algorithms. This class is
 * useful to access the graph algorithms in a generic way.
 *
 * @author Timon
 */
public abstract class GraphAlgorithmTask implements Runnable {
	protected NetworkFlowModel model;
	protected PathBasedFlowOverTime df;
	
	public GraphAlgorithmTask( NetworkFlowModel model ) {
		if( model == null )
			throw new IllegalArgumentException( "Model is null." );
		this.model = model;
	}
	
	public abstract void run();
	
	public PathBasedFlowOverTime getDynamicFlow() {
		return df;
	}
	public NetworkFlowModel getNetwork () {
		return model;
	}
}
