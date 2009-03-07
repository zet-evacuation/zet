package ds.ca.results;

import ds.ca.Cell;
import ds.ca.CellularAutomaton;
import ds.ca.ExitCell;
import ds.ca.Individual;
import ds.ca.results.Action.CADoesNotMatchException;

/**
 * Represents the fact that an individual leaves the simulation. Note that this
 * action starts and ends on the same cell. The performing individual is the
 * individual that occupies the exit cell.
 * 
 * @author Daniel Pluempe
 */
public class ExitAction extends Action {

	/** The cell where an individual leaves the simulation */
	protected ExitCell exit;

	/**
	 * Creates a new Exit action. 
	 * @param exit The cell from where the individual leaves
	 * the system.
	 */
	public ExitAction( ExitCell exit ) {
		this.exit = exit;
	}

	@Override
	public void execute( ds.ca.CellularAutomaton onCA ) throws InconsistentPlaybackStateException {
		if( exit.getIndividual() == null )
			throw new InconsistentPlaybackStateException( "Could not evacuate an individual from cell " + exit + "(" + exit.hashCode() + ") because there was none." );
		onCA.setIndividualEvacuated( exit.getIndividual() );
	}

	@Override
	public String toString() {
		String representation = "An individual leaves the simulation from cell " + exit;
		return representation;
	}

	@Override
	Action adoptToCA(  CellularAutomaton targetCA ) throws CADoesNotMatchException {
		Cell newExit = adoptCell( exit, targetCA );
		if( newExit == null )
			throw new CADoesNotMatchException( this, "Could not find the exit " + exit + " that this action uses in the new CA." );
		return new ExitAction( (ExitCell) newExit );
	}
}

