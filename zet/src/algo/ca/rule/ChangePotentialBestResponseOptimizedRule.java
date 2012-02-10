/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
package algo.ca.rule;

import java.util.ArrayList;
import ds.ca.evac.Cell;
import ds.ca.evac.Individual;
import ds.ca.evac.StaticPotential;
import ds.ca.evac.Room;
import evacuationplan.BestResponseDynamics;

/**
 *
 * @author Joscha Kulbatzki, Jan-Philipp Kappmeier
 */
public class ChangePotentialBestResponseOptimizedRule extends AbstractPotentialChangeRule {

	private static final double QUEUEING_TIME_WEIGHT_FACTOR = 0.5;
	private static final double MOVING_TIME_WEIGHT_FACTOR = 0.5;
	private static final int TIME_STEP_LIMIT_FOR_NASH_EQUILIBRIUM = 25;

	/**
	 * 
	 * @param cell
	 * @return true if the potential change rule can be used
	 */
	@Override
	public boolean executableOn( Cell cell ) {
		int timeStep = esp.eca.getTimeStep();
		return ((timeStep < TIME_STEP_LIMIT_FOR_NASH_EQUILIBRIUM) & (cell.getIndividual() != null)) ? true : false;

	}

	private double getResponse( Cell cell, StaticPotential pot ) {

		// Constants
		Individual ind = cell.getIndividual();
		double speed = ind.getCurrentSpeed();

		// Exit dependant values
		double distance = Double.MAX_VALUE;
		if( pot.getDistance( cell ) >= 0 )
			distance = pot.getDistance( cell );
		double movingTime = distance / speed;

		double exitCapacity = esp.eca.getExitToCapacityMapping().get( pot ).doubleValue();
		//System.out.println("Exit: " + pot.getID() + " : " + exitCapacity);

		// calculate number of individuals that are heading to the same exit and closer to it
		ArrayList<Individual> otherInds = new ArrayList<Individual>();
		//cell.getRoom().getIndividuals();
		ArrayList<Room> rooms = new ArrayList<Room>();
		rooms.addAll( esp.eca.getRooms() );
		for( Room room : rooms )
			for( Individual i : room.getIndividuals() )
				otherInds.add( i );

		int queueLength = 0;
		if( otherInds != null )
			for( Individual otherInd : otherInds )
				if( !otherInd.equals( ind ) )
					if( otherInd.getStaticPotential() == pot )
						if( otherInd.getStaticPotential().getDistance( otherInd.getCell() ) >= 0 )
							if( otherInd.getStaticPotential().getDistance( otherInd.getCell() ) < distance )
								queueLength++;
		//System.out.println("Potential = " + pot.getID());
		//System.out.println("Queue / Kapa = " + queueLength + " / " + exitCapacity + " = " + (queueLength / exitCapacity));
		//System.out.println("Dist / Speed = " + distance + " / " + speed + " = " + (distance / speed));

		// calculateEstimatedEvacuationTime
		return responseFunction1( queueLength, exitCapacity, movingTime );

	}

	private double responseFunction1( int queueLength, double exitCapacity, double movingTime ) {
		return (QUEUEING_TIME_WEIGHT_FACTOR * (queueLength / exitCapacity)) + (MOVING_TIME_WEIGHT_FACTOR * movingTime);
	}

	/**
	 * 
	 * @param cell
	 */
	@Override
	protected void onExecute( Cell cell ) {
		// perform initial best response dynamics exit selection
		BestResponseDynamics brd = new BestResponseDynamics();
		brd.computePotential( cell, esp.eca );
	}
}
