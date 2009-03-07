/*
 * Created on 23.01.2008
 *
 */
package algo.ca.parameter;

import java.util.Collection;

import ds.ca.Cell;
import ds.ca.Individual;

/**
 * An abstract class defining all methods that parameter sets have to support.
 * @author Daniel Pluempe, Jan-Philipp Kappmeier
 */
public interface ParameterSet {
	/* Updating of dynamic parameters */
	public double updateExhaustion(Individual individual, Cell targetCell);
	public double updatePreferredSpeed(Individual individual);
	public double updatePanic(Individual individual, Cell targetCell, Collection<Cell> preferedCells);

	/* Threshold values for various decisions */
	public double changePotentialThreshold(Individual individual);
	public double idleThreshold(Individual individual);
	public double movementThreshold(Individual individual);

	/* Other dynamic parameters */
	public double effectivePotential(Cell referenceCell, Cell targetCell);

	/* Some constants*/
	public double dynamicPotentialWeight();
	public double staticPotentialWeight();
	public double probabilityDynamicIncrease();
	public double probabilityDynamicDecrease();
	public double probabilityChangePotentialFamiliarityOrAttractivityOfExitRule();
    
	/* Conversion parameters */
	public double getAbsoluteMaxSpeed();
	public double getSpeedFromAge( double pAge );
	public double getSlacknessFromDecisiveness( double pDecisiveness );
	public double getExhaustionFromAge( double pAge);
	public double getReactiontimeFromAge( double pAge);
}
