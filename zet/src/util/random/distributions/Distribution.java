/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * Distribution.java
 * Created on 26. November 2007, 21:32
 */

package util.random.distributions;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import io.z.DistributionConverter;
import io.z.XMLConverter;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a random variable with an arbitrary distribution that can take values between a
 * given minimum and maximum values. The abstract {@link #getNextRandom()}method needs to be
 * implemented in subclasses to retain realizations of the variable.
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias ("distribution")
@XMLConverter (DistributionConverter.class)
public abstract class Distribution implements Serializable, Cloneable {
	/** The listeners of this distibution. */
//	@XStreamOmitField ()
//	private transient ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener> ();

	/** The minimal value that this distribution can take. */
	@XStreamAsAttribute ()
	private double min;
	/** The maximal value that this distribution can take. */
	@XStreamAsAttribute ()
	private double max;

	/**
	 * Creates a new instance of <code>Distribution</code>
	 * @param min the minimal value that can be taken by the random variable
	 * @param max the maximal value that can be taken by the random variable
	 * @throws java.lang.IllegalArgumentException if min is smaller than max
	 */
	public Distribution ( double min, double max ) throws IllegalArgumentException {
		setParameter ( min, max );
	}

//	/**
//	 * {@inheritDoc}
//	 * @param e the submitted event
//	 */
//	public void throwChangeEvent (ChangeEvent e) {
//		for (ChangeListener c : changeListeners) {
//			c.stateChanged (e);
//		}
//	}

//	/**
//	 * {@inheritDoc}
//	 * @param c the listener to register
//	 */
//	public void addChangeListener (ChangeListener c) {
//		if (!changeListeners.contains (c)) {
//			changeListeners.add (c);
//		}
//	}

//	/**
//	 * {@inheritDoc}
//	 * @param c the litener to remove
//	 */
//	public void removeChangeListener (ChangeListener c) {
//		changeListeners.remove (c);
//	}

	/**
	 * Returns the value of the density function for this probability distribution
	 * at a given point.
	 * @param x the point
	 * @return the value of the density function
	 */
	abstract public double getDensityAt( double x );

	/**
	 * Returns the currently set maximal value that the random variable can take.
	 * @return the maximal value
	 */
	public double getMax () {
		return max;
	}

	/**
	 * Returns the currently set minimal value that the random variable can take.
	 * @return the minimal value
	 */
	public double getMin () {
		return min;
	}

	/**
	 * Sets a new maximal value for the random variable.
	 * @param val the maximal value that can be taken by the random variable
	 * @throws IllegalArgumentException if min is smaller than the new value for max
	 */
	public void setMax ( double val ) throws IllegalArgumentException {
		setParameter ( min, val );
	}

	/**
	 * Sets a new minimal value for the random variable.
	 * @param val the minimal value that can be taken by the random variable
	 * @throws IllegalArgumentException if max is smaller than the new value for min
	 */
	public void setMin ( double val ) throws IllegalArgumentException {
		setParameter ( val, max );
	}

	/**
	 * Sets both bounding parameters for the distribution at the same time.
	 * @param min the minimal value that can be taken by the random variable
	 * @param max the maximal value that can be taken by the random variable
	 * @throws IllegalArgumentException if max is smaller than the new value for min or vice versa
	 */
	public void setParameter ( double min, double max ) throws IllegalArgumentException {
		if( min > max ) {
			throw new IllegalArgumentException ( "Minimum value is greater than maximum." );
		}
		this.min = min;
		this.max = max;
//		throwChangeEvent (new ChangeEvent (this));
	}

	/**
	 * Gets the next random number according to this distribution. The generated value
	 * has to be between the values returned by {@link #getMin()} and {@link #getMax()}.
	 * @return the random value
	 */
	public abstract double getNextRandom ();
}
