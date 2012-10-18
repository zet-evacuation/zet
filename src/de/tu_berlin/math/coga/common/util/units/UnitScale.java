/**
 * UnitScale.java
 * Created: 18.10.2012, 12:39:43
 */

package de.tu_berlin.math.coga.common.util.units;

/**
 * The {@code UnitScale} interface is upposed to provide conversion opportunities
 * between different multiples of values in a specific unit. It is supposed to
 * be implemented by enums which may for example specify different multiples
 * of time or lengths.
 * @param <T> the unit for which the scales are implemented (e. g. time).
 * @author Jan-Philipp Kappmeier
 */
public interface UnitScale<T> {
	public boolean isInRange( double value );

	public T getBetterUnit( double value );

	public double getBetterUnitValue( double value );

	public T getSmaller();

	public T getLarger();	

	public String getName();

	public String getLongName();

	public double getRange();
}
