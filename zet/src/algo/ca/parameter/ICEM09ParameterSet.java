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
package algo.ca.parameter;

import de.tu_berlin.math.coga.zet.ZETLocalization;
import ds.PropertyContainer;
import ds.ca.evac.EvacCell;
import ds.ca.evac.Individual;
import ds.ca.evac.StaticPotential;
import java.util.Collection;

/**
 * @author Sylvie Temme
 *
 */
public class ICEM09ParameterSet extends AbstractDefaultParameterSet {
	/* im AbstractDefaultParameterSet: 
	final protected double DYNAMIC_POTENTIAL_WEIGHT;
	final protected double STATIC_POTENTIAL_WEIGHT;
	final protected double PROB_DYNAMIC_POTENTIAL_INCREASE;
	final protected double PROB_DYNAMIC_POTENTIAL_DECREASE;
	final protected double PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT;
	final protected double ABSOLUTE_MAX_SPEED;
	 */

	final protected double PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO;
	final protected double SLACKNESS_TO_IDLE_RATIO;
	final protected double PANIC_DECREASE;
	final protected double PANIC_INCREASE;
	final protected double PANIC_WEIGHT_ON_SPEED;
	final protected double PANIC_WEIGHT_ON_POTENTIALS;
	final protected double EXHAUSTION_WEIGHT_ON_SPEED;
	final protected double PANIC_THRESHOLD;

	/**
	 * Initializes the default parameter set and loads some constants from the property container.
	 */
	public ICEM09ParameterSet() {
		/* im AbstractDefaultParameterSet:
		DYNAMIC_POTENTIAL_WEIGHT = PropertyContainer.getInstance().getAsDouble( "algo.ca.DYNAMIC_POTENTIAL_WEIGHT" );
		STATIC_POTENTIAL_WEIGHT = PropertyContainer.getInstance().getAsDouble( "algo.ca.STATIC_POTENTIAL_WEIGHT" );
		PROB_DYNAMIC_POTENTIAL_INCREASE = PropertyContainer.getInstance().getAsDouble( "algo.ca.PROB_DYNAMIC_POTENTIAL_INCREASE" );
		PROB_DYNAMIC_POTENTIAL_DECREASE = PropertyContainer.getInstance().getAsDouble( "algo.ca.PROB_DYNAMIC_POTENTIAL_DECREASE" );
		PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT = PropertyContainer.getInstance().getAsDouble( "algo.ca.PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT" );
		ABSOLUTE_MAX_SPEED = PropertyContainer.getInstance().getAsDouble( "algo.ca.ABSOLUTE_MAX_SPEED" );
		 */
		PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO" );
		SLACKNESS_TO_IDLE_RATIO = PropertyContainer.getInstance().getAsDouble( "algo.ca.SLACKNESS_TO_IDLE_RATIO" );
		PANIC_DECREASE = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_DECREASE" );
		PANIC_INCREASE = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_INCREASE" );
		PANIC_WEIGHT_ON_SPEED = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_WEIGHT_ON_SPEED" );
		PANIC_WEIGHT_ON_POTENTIALS = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_WEIGHT_ON_POTENTIALS" );
		EXHAUSTION_WEIGHT_ON_SPEED = PropertyContainer.getInstance().getAsDouble( "algo.ca.EXHAUSTION_WEIGHT_ON_SPEED" );
		PANIC_THRESHOLD = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_THRESHOLD" );
	}

	////* Some constants*////
	/* im AbstractDefaultParameterSet:
	@Override
	public double dynamicPotentialWeight(){
	return DYNAMIC_POTENTIAL_WEIGHT;
	}
	@Override
	public double staticPotentialWeight(){
	return STATIC_POTENTIAL_WEIGHT;
	}
	@Override
	public double probabilityDynamicIncrease(){
	return PROB_DYNAMIC_POTENTIAL_INCREASE;
	}
	@Override
	public double probabilityDynamicDecrease(){
	return PROB_DYNAMIC_POTENTIAL_DECREASE;
	}
	@Override
	public double probabilityChangePotentialFamiliarityOrAttractivityOfExitRule(){
	return PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT;
	}
	 */
	public double getAbsoluteMaxSpeed() {
		return ABSOLUTE_MAX_SPEED;
	}

	protected double slacknessToIdleRatio() {
		return SLACKNESS_TO_IDLE_RATIO;
	}

	protected double panicToProbOfPotentialChangeRatio() {
		return PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO;
	}

	protected double getPanicIncrease() {
		return PANIC_INCREASE;
	}

	protected double getPanicDecrease() {
		return PANIC_DECREASE;
	}

	protected double panicWeightOnSpeed() {
		return PANIC_WEIGHT_ON_SPEED;
	}

	protected double exhaustionWeightOnSpeed() {
		return EXHAUSTION_WEIGHT_ON_SPEED;
	}

	@Override
	public double effectivePotential( EvacCell referenceCell, EvacCell targetCell ) {
		if( referenceCell.getIndividual() == null )
			throw new IllegalArgumentException( ZETLocalization.getSingleton().getString( "algo.ca.parameter.NoIndividualOnReferenceCellException" ) );
		StaticPotential staticPotential = referenceCell.getIndividual().getStaticPotential();
		final double statPotlDiff = staticPotential.getPotential( referenceCell ) - staticPotential.getPotential( targetCell );
		return statPotlDiff;
	}

	////* Conversion parameters *////
	public double getSpeedFromAge( double pAge ) {
		return 0.595;
	}

	public double getSlacknessFromDecisiveness( double pDecisiveness ) {
		return 0;
	}

	public double getExhaustionFromAge( double pAge ) {
		return 0;
	}

	public double getReactionTimeFromAge( double pAge ) {
		return 0;
	}

	@Override
	public double getReactionTime( ) {
		return 1;
	}

	//////////////////////////////////ab hier: nicht benutzt////////////////////////////////////////////////////////
	////* Updating of dynamic parameters *////
	@Override
	public double updateExhaustion( Individual individual, EvacCell targetCell ) {
		throw new IllegalStateException( "Methode aus PaperParameterSet wurde aufgerufen!" );
		//return 0;
	}

	@Override
	public double updatePreferredSpeed( Individual individual ) {
		throw new IllegalStateException( "Methode aus PaperParameterSet wurde aufgerufen!" );
		//return 0;
	}

	@Override
	public double updatePanic( Individual individual, EvacCell targetCell, Collection<EvacCell> preferedCells ) {
		throw new IllegalStateException( "Methode aus PaperParameterSet wurde aufgerufen!" );
		//return 0;
	}

	////* Threshold values for various decisions *////
	@Override
	public double changePotentialThreshold( Individual individual ) {
		throw new IllegalStateException( "Methode aus PaperParameterSet wurde aufgerufen!" );
		//return 0;
	}

	@Override
	public double idleThreshold( Individual individual ) {
		throw new IllegalStateException( "Methode aus PaperParameterSet wurde aufgerufen!" );
		//return 0;
	}

	@Override
	public double movementThreshold( Individual individual ) {
		throw new IllegalStateException( "Methode aus PaperParameterSet wurde aufgerufen!" );
		//return 0;
	}
}
