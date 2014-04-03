/**
 * EvacuationCellState.java
 * Created: 25.10.2012, 15:01:36
 */
package algo.ca.framework;

import ds.ca.evac.Individual;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationCellState {
	Individual individual;

	public EvacuationCellState( Individual individual ) {
		this.individual = individual;
	}

	public Individual getIndividual() {
		return individual;
	}

	public void setIndividual( Individual individual ) {
		this.individual = individual;
	}
}