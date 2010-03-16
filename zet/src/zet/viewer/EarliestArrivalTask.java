/**
 * EarliestArrivalTask.java
 * Created: 16.03.2010, 12:27:50
 */
package zet.viewer;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
import batch.tasks.ProcessUpdateMessage;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmListener;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmProgressEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmStartedEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmTerminatedEvent;
import ds.graph.flow.PathBasedFlowOverTime;
import javax.swing.SwingWorker;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EarliestArrivalTask extends SwingWorker<PathBasedFlowOverTime, ProcessUpdateMessage> implements AlgorithmListener {
	EarliestArrivalFlowProblem eafp;
	SEAAPAlgorithm algo = new SEAAPAlgorithm();
	PathBasedFlowOverTime df;

	EarliestArrivalTask( EarliestArrivalFlowProblem eafp ) {
		this.eafp = eafp;
	}

	public void addAlgorithmListener( AlgorithmListener listener ) {
		algo.addAlgorithmListener( listener );
	}

	public EarliestArrivalFlowProblem getEarliestArrivalFlowProblem() {
		return eafp;
	}

	public PathBasedFlowOverTime getFlowOverTime() {
		return df;
	}

	public int getNeededTimeHorizon() {
		return algo.getSolution().getTimeHorizon();
	}

	@Override
	protected PathBasedFlowOverTime doInBackground() throws Exception {
		algo.setProblem( eafp );
		algo.addAlgorithmListener( this );
		algo.run();
		df = algo.getSolution().getPathBased();
		return df;
	}

	@Override
	protected void done() {
		try {
			this.firePropertyChange( null, this, this );
		} catch( Exception ignore ) {
		}

	}

	public void eventOccurred( AlgorithmEvent event ) {
		if( event instanceof AlgorithmProgressEvent ) {
			int progress = ((int)(((AlgorithmProgressEvent)event).getProgress()*100));
			publish( new ProcessUpdateMessage( progress, "Flow Computation", "", "" ) );
		}	else if( event instanceof AlgorithmTerminatedEvent) {
			publish( new ProcessUpdateMessage( 100, "Flow Computation", "", "" ) );
		} else if( event instanceof AlgorithmStartedEvent ) {
			publish( new ProcessUpdateMessage( 0, "Flow Computation", "", "") );
		}
	}
}
