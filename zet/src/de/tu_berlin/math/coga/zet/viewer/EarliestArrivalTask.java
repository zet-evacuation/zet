/**
 * EarliestArrivalTask.java
 * Created: 16.03.2010, 12:27:50
 */
package de.tu_berlin.math.coga.zet.viewer;

import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import org.zetool.netflow.dynamic.earliestarrival.SEAAPAlgorithm;
import event.ProcessUpdateMessage;
import org.zetool.common.algorithm.AlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmListener;
import org.zetool.common.algorithm.AlgorithmProgressEvent;
import org.zetool.common.algorithm.AlgorithmStartedEvent;
import org.zetool.common.algorithm.AlgorithmTerminatedEvent;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import javax.swing.SwingWorker;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EarliestArrivalTask extends SwingWorker<PathBasedFlowOverTime, ProcessUpdateMessage> implements AlgorithmListener {
	EarliestArrivalFlowProblem eafp;
	SEAAPAlgorithm algo = new SEAAPAlgorithm();
	PathBasedFlowOverTime df;

	public EarliestArrivalTask( EarliestArrivalFlowProblem eafp ) {
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
