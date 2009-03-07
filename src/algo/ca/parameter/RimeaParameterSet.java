/**
 * Class RimeaParameterSet
 * Erstellt 13.10.2008, 21:28:10
 */

package algo.ca.parameter;

import ds.ca.Cell;
import ds.ca.Individual;
import java.util.Collection;
import util.random.distributions.UniformDistribution;

/**
 * A {@link ParameterSet} that sets the parameter of the simulation to values
 * that allow passing the RIMEA tests.
 * @author Jan-Philipp Kappmeier
 */
public class RimeaParameterSet extends DefaultParameterSet {
	
	public RimeaParameterSet() {
		super();
		//PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_TO_PROB_OF_POTENTIAL_CHANGE_RATIO" );
		//SLACKNESS_TO_IDLE_RATIO = PropertyContainer.getInstance().getAsDouble( "algo.ca.SLACKNESS_TO_IDLE_RATIO" );
		//PANIC_DECREASE_FACTOR_IF_HAPPY = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_DECREASE_FACTOR_IF_HAPPY" );
		//PANIC_INCREASE_FACTOR_IF_UNHAPPY = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_INCREASE_FACTOR_IF_UNHAPPY" );
		//PANIC_WEIGHT_ON_SPEED = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_WEIGHT_ON_SPEED" );
		//PANIC_WEIGHT_ON_POTENTIALS = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_WEIGHT_ON_POTENTIALS" );
		//EXHAUSTION_WEIGHT_ON_SPEED = PropertyContainer.getInstance().getAsDouble( "algo.ca.EXHAUSTION_WEIGHT_ON_SPEED" );
		//PANIC_THRESHOLD = PropertyContainer.getInstance().getAsDouble( "algo.ca.PANIC_THRESHOLD" );
	}

	@Override
	public double updateExhaustion( Individual individual, Cell targetCell ) {
		individual.setExhaustion( 0 );
		return 0;
	}
	
	@Override
	public double updatePanic( Individual individual, Cell targetCell, Collection<Cell> preferedCells ) {
		individual.setPanic( 0 );
		return 0;
	}
	
	@Override
	public double updatePreferredSpeed( Individual i ) {
		i.setCurrentSpeed( i.getMaxSpeed() );
		return i.getMaxSpeed();
	}
	
	@Override
	public double getReactiontimeFromAge( double age ) {
		UniformDistribution uniform = new UniformDistribution( 10, 101 );
		return uniform.getNextRandom();
	}
}
