/*
 * Created on 27.06.2008
 *
 */
package ds.ca.results;

import ds.ca.CellularAutomaton;

/**
 * @author Daniel Pluempe
 *
 */
public class CAStateChangedAction extends Action {

	/** The new state of the cellular automaton */
	CellularAutomaton.State newState;

	public CAStateChangedAction( CellularAutomaton.State newState ) {
		this.newState = newState;
	}

	/** {@inheritDoc}
	 * @see ds.za.results.Action#adoptToCA(ds.za.CellularAutomaton)
	 */
	@Override
	Action adoptToCA(  CellularAutomaton targetCA ) throws CADoesNotMatchException {
		return this;
	}

	/** {@inheritDoc}
	 * @param onCA the cellular on which the rule is executed
	 * @throws InconsistentPlaybackStateException if an error during replay occured
	 * @see ds.za.results.Action#execute(ds.za.CellularAutomaton)
	 */
	@Override
	public void execute( CellularAutomaton onCA ) throws InconsistentPlaybackStateException {
		onCA.setState( newState );
	}

	/** {@inheritDoc}
	 * @see ds.za.results.Action#toString()
	 */
	@Override
	public String toString() {
		switch( this.newState ) {
			case finish:
				return "finished";
			case ready:
				return "ready";
			case running:
				return "running";
			default:
				return "unknown state";
		}
	}
}
