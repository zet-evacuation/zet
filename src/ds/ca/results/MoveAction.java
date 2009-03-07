package ds.ca.results;

import ds.ca.Cell;
import ds.ca.CellularAutomaton;
import ds.ca.Individual;

/**
 * Represents the fact that an individual moves from one cell to another.
 * @author Daniel Pluempe
 *
 */
public class MoveAction extends Action {

	/** The cell from where the individual moves */
	protected Cell from;
	/** The cell to where the individual moves */
	protected Cell to;
	/** The (exact) time, when the individual will arrive at the 
	 *  target cell
	 */
	protected double arrivalTime;
	/** The (exact) time, when the individual starts moving to the
	 *  target cell
	 */
	protected double startTime;
	/** The number of the individual that is moved */
	private int individualNumber;

	/**
	 * Creates a new instance of a move action. This action starts at the cell
	 * from where the individual leaves and ends at the final point of the 
	 * individual's movement. The action is performed by the individual
	 * standing on the start cell.
	 * @param from The cell from where the individual starts to move
	 * @param to The cell where the individual arrives
	 * @param individual the individual that is moved
	 */
	public MoveAction( Cell from, Cell to, Individual individual ) {
		this( from, to, individual.getStepEndTime(), individual.getStepStartTime(), individual.getNumber() );
		if( from.getIndividual() == null ) {
			throw new IllegalArgumentException( "The starting cell must not be empty!" );
		}

		if( to.getIndividual() != null && !(to == from) ) {
			throw new IllegalArgumentException( "The taget cell is not empty!" );
		}
	}

	protected MoveAction( Cell from, Cell to, double arrivalTime, double startTime, int individualNumber ) {
		this.from = from;
		this.to = to;
		this.arrivalTime = arrivalTime;
		this.startTime = startTime;
		this.individualNumber = individualNumber;
	}

	public Cell from() {
		return from;
	}

	public int getIndividualNumber() {
		return individualNumber;
	}

	public Cell to() {
		return to;
	}

	public double arrivalTime() {
		return arrivalTime;
	}

	public double startTime() {
		return startTime;
	}

	@Override
	public void execute( ds.ca.CellularAutomaton onCA ) throws InconsistentPlaybackStateException {
		if( from.getIndividual() == null ) {
			throw new InconsistentPlaybackStateException(
							onCA.getTimeStep(),
							this,
							"Cannot move individual because it is not there." );
		}

		if( !(to == from) && to.getIndividual() != null ) {
			throw new InconsistentPlaybackStateException(
							onCA.getTimeStep(),
							this,
							"Cannot move individual because there is an individual on the target cell." );
		}

		//from.getRoom().moveIndividual( from, to );
		onCA.moveIndividual( from, to );
	}

	@Override
	public String toString() {
		String representation = "";

		representation += from.getIndividual() + " moves from ";
		representation += from + " to ";
		representation += to + ".";

		return representation;
	}

	@Override
	public Action adoptToCA( CellularAutomaton targetCA ) throws CADoesNotMatchException {
		Cell newFrom = adoptCell( from, targetCA );
		if( newFrom == null ) {
			throw new CADoesNotMatchException(
							this,
							"Could not find the starting cell " + from + " in the new CA." );
		}


		Cell newTo = adoptCell( to, targetCA );
		if( to == null ) {
			throw new CADoesNotMatchException(
							this,
							"Could not find the ending cell " + to + " in the new CA." );
		}

		return new MoveAction( newFrom, newTo, this.arrivalTime, this.startTime, this.individualNumber );
	}
}

