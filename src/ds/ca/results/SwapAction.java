/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/**
 * Class SwapAction
 * Erstellt 08.07.2008, 00:43:13
 */
package ds.ca.results;

import ds.ca.evac.EvacCell;
import ds.ca.evac.EvacuationCellularAutomaton;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SwapAction extends Action {

	/** The cell from where individual 1 moves */
	protected EvacCell cell1;
	/** The cell from where individual 2 moves */
	protected EvacCell cell2;
	/** The (exact) time, when individual 1 will arrive at cell 2*/
	protected double arrivalTime1;
	/** The (exact) time, when individual 2 will arrive at cell 1*/
	protected double arrivalTime2;
	/** The (exact) time, when individual 1 starts moving to cell 2*/
	protected double startTime1;
	/** The (exact) time, when individual 2 starts moving to cell 1*/
	protected double startTime2;
	/** The number of individual 1 that is moved */
	private int individualNumber1;
	/** The number of individual 2 that is moved */
	private int individualNumber2;

	/**
	 * Creates a new instance of a move action. This action starts at the cell
	 * from where the individual leaves and ends at the final point of the 
	 * individual's movement. The action is performed by the individual
	 * standing on the start cell.
	 * @param cell1 The cell from where one individual starts to move
	 * @param cell2 The cell from where the other individual starts to move
	 */
	public SwapAction( EvacCell cell1, EvacCell cell2 ) {
		this( cell1, cell2, cell1.getIndividual().getStepEndTime(), cell1.getIndividual().getStepStartTime(),
						cell1.getIndividual().getNumber(),
						cell2.getIndividual().getStepEndTime(),
						cell2.getIndividual().getStepStartTime(),
						cell2.getIndividual().getNumber() );
		if( cell1.getIndividual() == null )
			throw new IllegalArgumentException( "The starting cell must not be empty!" );
		if( cell2.getIndividual() == null )
			throw new IllegalArgumentException( "The starting cell must not be empty!" );
	}
	
	private SwapAction(
					EvacCell cell1,
					EvacCell cell2,
					double getStepEndTime1,
					double getStepStartTime1,
					int getNumber1,
					double getStepEndTime2,
					double getStepStartTime2,
					int getNumber2 ) {
		this.cell1 = cell1;
		this.cell2 = cell2;
		this.arrivalTime1 = getStepEndTime1;
		this.startTime1 = getStepStartTime1;
		this.individualNumber1 = getNumber1;
		this.arrivalTime2 = getStepEndTime2;
		this.startTime2 = getStepStartTime2;
		this.individualNumber2 = getNumber2;
	}

	public EvacCell cell1() {
		return cell1;
	}

	public EvacCell cell2() {
		return cell2;
	}

	public int getIndividualNumber1() {
		return individualNumber1;
	}

	public int getIndividualNumber2() {
		return individualNumber2;
	}

	public double arrivalTime1() {
		return arrivalTime1;
	}

	public double arrivalTime2() {
		return arrivalTime2;
	}

	public double startTime1() {
		return startTime1;
	}

	public double startTime2() {
		return startTime2;
	}

	@Override
	public void execute( ds.ca.evac.EvacuationCellularAutomaton onCA ) throws InconsistentPlaybackStateException {
		if( cell1.getIndividual() == null )
			throw new InconsistentPlaybackStateException( onCA.getTimeStep(), this, "There is no Individual on cell 1." );
		if( cell2.getIndividual() == null )
			throw new InconsistentPlaybackStateException( onCA.getTimeStep(), this, "There is no Individual on cell 2." );

		//cell1.getRoom().swapIndividuals( cell1, cell2 );
		onCA.swapIndividuals( cell1, cell2 );
	}

	@Override
	public String toString() {
		String representation = "";

		representation += cell1.getIndividual() + " moves from ";
		representation += cell1 + " to ";
		representation += cell2 + ". \n";
		representation += cell2.getIndividual() + " moves from ";
		representation += cell2 + " to ";
		representation += cell1 + ". \n";

		return representation;
	}

	@Override
	public Action adoptToCA( EvacuationCellularAutomaton targetCA ) throws CADoesNotMatchException {
		EvacCell newCell1 = adoptCell( cell1, targetCA );
		if( newCell1 == null )
			throw new CADoesNotMatchException( this, "Could not find cell 1 " + cell1 + " in the new CA." );
		EvacCell newCell2 = adoptCell( cell2, targetCA );
		if( cell2 == null )
			throw new CADoesNotMatchException( this, "Could not find cell 2 " + cell2 + " in the new CA." );

		return new SwapAction( newCell1, newCell2, this.arrivalTime1, this.startTime1,
						this.individualNumber1,
						this.arrivalTime2,
						this.startTime1,
						this.individualNumber2 );
	}
}
