/*
 *
 * BestResponseDynamics.java
 * Created 04.06.2010, 23:58:56
 */

package evacuationplan;

import ds.ca.Cell;
import ds.ca.CellularAutomaton;
import ds.ca.Individual;
import ds.ca.Room;
import ds.ca.StaticPotential;
import java.util.ArrayList;
import java.util.List;

/**
 * The class {@code BestResponseDynamics} ...
 * @author Joscha Kulbatzki, Jan-Philipp Kappmeier
 */
public class BestResponseDynamics {

	private static final double QUEUEING_TIME_WEIGHT_FACTOR = 0.5;
	private static final double MOVING_TIME_WEIGHT_FACTOR = 0.5;


	/**
	 * Creates a new instance of {@code BestResponseDynamics}.
	 */
	public BestResponseDynamics() {

	}

	public void computeAssignmentBasedOnBestResponseDynamics( CellularAutomaton ca, List<Individual> individuals ) {
		int c = 0;
		while( true ) {
			c++;
			int swapped = 0;
			for( Individual i : individuals ) {
				swapped += computePotential( i.getCell(), ca );
			}
			System.out.println( "Swapped in iteration " + c + ": " + swapped );
			if( swapped == 0 )
				break;
		}
		System.out.println( "Best Response Rounds: " + c );
	}

	public int computePotential( Cell cell, CellularAutomaton ca ) {
		ArrayList<StaticPotential> exits = new ArrayList<StaticPotential>();
		exits.addAll( ca.getPotentialManager().getStaticPotentials() );
		StaticPotential newPot = cell.getIndividual().getStaticPotential();
		double response = Double.MAX_VALUE;
		for( StaticPotential pot : exits )
			if( getResponse( ca, cell, pot ) < response ) {
				response = getResponse( ca, cell, pot );
				newPot = pot;
			}

		StaticPotential oldPot = cell.getIndividual().getStaticPotential();
		cell.getIndividual().setStaticPotential( newPot );
		if( !oldPot.equals( newPot ) )
			return 1;
		else
			return 0;
	}

	private double getResponse( CellularAutomaton ca, Cell cell, StaticPotential pot ) {

		// Constants
		Individual ind = cell.getIndividual();
		double speed = ind.getCurrentSpeed();

		// Exit dependant values
		double distance = Double.MAX_VALUE;
		if( pot.getDistance( cell ) >= 0 )
			distance = pot.getDistance( cell );
		double movingTime = distance / speed;

		double exitCapacity = ca.getExitToCapacityMapping().get( pot ).doubleValue();
		//System.out.println("Exit: " + pot.getID() + " : " + exitCapacity);

		// calculate number of individuals that are heading to the same exit and closer to it
		ArrayList<Individual> otherInds = new ArrayList<Individual>();
		//cell.getRoom().getIndividuals();
		ArrayList<Room> rooms = new ArrayList<Room>();
		rooms.addAll( ca.getRooms() );
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
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "BestResponseDynamics";
	}
}
