/*
 * SimpleParameterSet.java
 *
 * Created on 26.01.2008, 15:20:20
 */

package algo.ca.parameter;

import ds.ca.Cell;
import ds.ca.Individual;
import ds.ca.StaticPotential;
import java.util.Collection;

/**
 * @author Jan-Philipp Kappmeier
 */
public class SimpleParameterSet extends AbstractDefaultParameterSet {

	@Override
	public double changePotentialThreshold( Individual individual ) {
		return 0;
	}

	/**
	 * 
	 * @param referenceCell
	 * @param targetCell
	 * @return
	 */
	@Override
	public double effectivePotential( Cell referenceCell, Cell targetCell ) {
		StaticPotential staticPotential = referenceCell.getIndividual().getStaticPotential();
		final double statPotlDiff = staticPotential.getPotential( referenceCell ) - staticPotential.getPotential( targetCell );
		return statPotlDiff;
	}

	@Override
	public double idleThreshold( Individual i ) {
		return i.getSlackness() * 0.4;
	}

	@Override
	public double movementThreshold( Individual i ) {
		double individualSpeed = i.getCurrentSpeed();
		double cellSpeed = i.getCell().getSpeedFactor();
		return individualSpeed * cellSpeed;
	}

	@Override
	public double updateExhaustion( Individual individual, Cell targetCell ) {
		return 0;
	}

	@Override
	public double updatePanic( Individual individual, Cell targetCell, Collection<Cell> preferedCells ) {
		return 0;
	}

	@Override
	public double updatePreferredSpeed( Individual individual ) {
		return 0;
	}

	public double getAbsoluteMaxSpeed() {
		return 1.8;
	}

	public double getSpeedFromAge( double pAge ) {
		return 1;
	}

	public double getSlacknessFromDecisiveness( double pDecisiveness ) {
		return (1-pDecisiveness)*0.25;
	}
	public double getExhaustionFromAge( double pAge){
		return 0.1;
	}
	public double getReactiontimeFromAge( double pAge){
		return 5;
	}
}
