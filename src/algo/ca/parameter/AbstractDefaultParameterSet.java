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
/*
 * Created on 23.01.2008
 *
 */
package algo.ca.parameter;

import java.util.Collection;

import ds.PropertyContainer;
import ds.ca.Cell;
import ds.ca.Individual;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Daniel Pluempe
 *
 */
public abstract class AbstractDefaultParameterSet implements ParameterSet {
	final protected double DYNAMIC_POTENTIAL_WEIGHT;
	final protected double STATIC_POTENTIAL_WEIGHT;
	final protected double PROB_DYNAMIC_POTENTIAL_INCREASE;
	final protected double PROB_DYNAMIC_POTENTIAL_DECREASE;
	final protected double PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT;
	final protected double ABSOLUTE_MAX_SPEED;

	/**
	 * Initializes the default parameter set and loads some constants from the property container.
	 */
	public AbstractDefaultParameterSet() {
		DYNAMIC_POTENTIAL_WEIGHT = PropertyContainer.getInstance().getAsDouble( "algo.ca.DYNAMIC_POTENTIAL_WEIGHT" );
		STATIC_POTENTIAL_WEIGHT = PropertyContainer.getInstance().getAsDouble( "algo.ca.STATIC_POTENTIAL_WEIGHT" );
		PROB_DYNAMIC_POTENTIAL_INCREASE = PropertyContainer.getInstance().getAsDouble( "algo.ca.PROB_DYNAMIC_POTENTIAL_INCREASE" );
		PROB_DYNAMIC_POTENTIAL_DECREASE = PropertyContainer.getInstance().getAsDouble( "algo.ca.PROB_DYNAMIC_POTENTIAL_DECREASE" );
		PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT = PropertyContainer.getInstance().getAsDouble( "algo.ca.PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT" );
		ABSOLUTE_MAX_SPEED = PropertyContainer.getInstance().getAsDouble( "algo.ca.ABSOLUTE_MAX_SPEED" );
	}

	/**
	 * {@inheritDoc}
	 * @return the dynamic potential weight
	 * @see algo.ca.parameter.ParameterSet#dynamicPotentialWeight()
	 */
	@Override
	public double dynamicPotentialWeight() {
		return DYNAMIC_POTENTIAL_WEIGHT;
	}

	/**
	 * {@inheritDoc}
	 * @return the static potential weight
	 * @see algo.ca.parameter.ParameterSet#staticPotentialWeight()
	 */
	@Override
	public double staticPotentialWeight() {
		return STATIC_POTENTIAL_WEIGHT;
	}

	@Override
	public double probabilityDynamicDecrease() {
		return PROB_DYNAMIC_POTENTIAL_DECREASE;
	}

	@Override
	public double probabilityDynamicIncrease() {
		return PROB_DYNAMIC_POTENTIAL_INCREASE;
	}

	@Override
	public double probabilityChangePotentialFamiliarityOrAttractivityOfExitRule() {
		return PROB_FAMILIARITY_OR_ATTRACTIVITY_OF_EXIT;
	}

	@Override
	public abstract double changePotentialThreshold( Individual individual );

	@Override
	public abstract double effectivePotential( Cell referenceCell, Cell targetCell );

	@Override
	public abstract double idleThreshold( Individual individual );

	@Override
	public abstract double movementThreshold( Individual individual );

	@Override
	public abstract double updateExhaustion( Individual individual, Cell targetCell );

	@Override
	public abstract double updatePanic( Individual individual, Cell targetCell, Collection<Cell> preferedCells );

	@Override
	public abstract double updatePreferredSpeed( Individual individual );

//	public double getReactionTime() {
//		return 45;
//	}

	/**
	 * Creates a {@link ParameterSet} of a specified subclass.
	 * @param parameterSetName the subclass
	 * @return the object of the subclass type.
	 */
	public static AbstractDefaultParameterSet createParameterSet( String parameterSetName ) {
		Class<?> parameterSetClass = null;
		AbstractDefaultParameterSet parameterSet = null;
		try {
			parameterSetClass = Class.forName( "algo.ca.parameter." + parameterSetName );
			parameterSet = (AbstractDefaultParameterSet) parameterSetClass.getConstructor().newInstance();
		} catch( ClassNotFoundException e ) {
			e.printStackTrace();
		} catch( NoSuchMethodException e ) {
			e.printStackTrace();
		} catch( InstantiationException e ) {
			e.printStackTrace();
		} catch( IllegalAccessException e ) {
			e.printStackTrace();
		} catch( IllegalArgumentException e ) {
			e.printStackTrace();
		} catch( InvocationTargetException e ) {
			e.printStackTrace();
		}
		return parameterSet;
	}
}
