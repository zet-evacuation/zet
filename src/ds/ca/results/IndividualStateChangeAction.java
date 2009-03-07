/*
 * Created on 04.06.2008
 *
 */
package ds.ca.results;

import ds.ca.CellularAutomaton;
import ds.ca.Individual;

/**
 * @author Daniel Pluempe
 */
public class IndividualStateChangeAction extends Action {

	private Individual individual;
	private double panic;
	private double exhaustion;
	private double currentSpeed;
	private boolean isAlarmed;

	public IndividualStateChangeAction( Individual individual ) {
		this( individual,
						individual.getPanic(),
						individual.getExhaustion(),
						individual.getCurrentSpeed(),
						individual.isAlarmed() );
	}

	protected IndividualStateChangeAction( Individual individual, double panic, double exhaustion, double currentSpeed, boolean isAlarmed ) {
		this.individual = individual;
		this.panic = panic;
		this.exhaustion = exhaustion;
		this.currentSpeed = currentSpeed;
		this.isAlarmed = isAlarmed;
	}

	/**
	 * {@inheritDoc }
	 * @see ds.za.results.Action#adoptToCA(ds.za.CellularAutomaton)
	 */
	@Override
	Action adoptToCA(  CellularAutomaton targetCA ) throws CADoesNotMatchException {
		Individual adaptedIndividual = targetCA.getIndividual( individual.getNumber() );
		if( adaptedIndividual == null )
			throw new CADoesNotMatchException( this, "Could not find the individual with the unique id " + individual.getNumber() );
		return new IndividualStateChangeAction( adaptedIndividual, panic, exhaustion, currentSpeed, isAlarmed );
	}

	/**
	 * {@inheritDoc }
	 * @param onCA 
	 * @throws InconsistentPlaybackStateException 
	 * @see ds.za.results.Action#execute(ds.za.CellularAutomaton)
	 */
	@Override
	public void execute( CellularAutomaton onCA ) throws InconsistentPlaybackStateException {
		individual.setPanic( panic );
		individual.setExhaustion( exhaustion );
		individual.setCurrentSpeed( currentSpeed );
		if( isAlarmed && !individual.isAlarmed() )
			individual.setAlarmed( true );
	}

	/**
	 * {@inheritDoc }
	 * @see ds.za.results.Action#toString()
	 */
	@Override
	public String toString() {
		return "The state of the individual " + individual.getNumber() + " changes to: " + " Panic: " + panic + ", Exhaustion: " + exhaustion + ", Speed: " + currentSpeed + ", Alarmed: " + (isAlarmed ? "yes" : "no");
	}
}
