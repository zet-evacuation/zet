/**
 * Class RimeaParameterSet
 * Erstellt 13.10.2008, 21:28:10
 */

package algo.ca.parameter;

import ds.ca.Individual;

/**
 * A {@link ParameterSet} that sets the parameter of the simulation to values
 * that allow passing the RIMEA tests.
 * @author Melanie Schmidt
 */
public class RimeaParameterSetNoReactionTime extends RimeaParameterSet {
	
	/**
	 * 
	 * @param age
	 * @return
	 */
	@Override
	public double getReactiontimeFromAge( double age ) {
		return 0.0;
	}
	
	/**
	 * 
	 * @param individual
	 * @return
	 */
	@Override
	public double idleThreshold(Individual individual){
		return 0.0;
	}
	
	@Override
	public double getSpeedFromAge( double pAge ) {
		return 0.595;
	}
}
