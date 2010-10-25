/**
 * PartialAugmentRelabelAlgorithm.java
 * Created: Oct 5, 2010,5:17:40 PM
 */
package algo.graph.staticflow.maxflow;

import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.flow.MaximumFlow;
import ds.graph.problem.MaximumFlowProblem;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PartialAugmentRelabelAlgorithm extends PushRelabel {

	@Override
	protected MaximumFlow runAlgorithm( MaximumFlowProblem problem ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	protected int push( Edge e ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	protected int relabel( Node v ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	protected void computeMaxFlow() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	protected void makeFeasible() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

}
