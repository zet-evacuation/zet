/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.tu_berlin.math.coga.zet.viewer;

import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import org.zetool.netflow.dynamic.earliestarrival.SEAAPAlgorithm;
import event.ProcessUpdateMessage;
import org.zetool.common.algorithm.AbstractAlgorithmEvent;
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

	public void eventOccurred( AbstractAlgorithmEvent event ) {
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
