/*
 * IndividualDistanceComparator.java
 * Created 23.09.2009, 23:56:18
 */

package algo.ca.util;

import ds.ca.evac.Individual;
import java.util.Comparator;

/**
 * The class {@code IndividualDistanceComparator} compares two individuals
 * in means of their distance to the exit using their currently selected
 * potential field.
 * @param <E> the compared object class, that must extend {@link ds.ca.Individual}
 * @author Jan-Philipp Kappmeier
 */
public class IndividualDistanceComparator<E extends Individual> implements Comparator<E> {

	/**
	 * Creates a new instance of {@code IndividualDistanceComparator}. No
	 * initialization is needed.
	 */
	public IndividualDistanceComparator() { }

	/**
	 * <p>Compares two individuals in means of the distance. The distance is the
	 * value of the potantial of the cell on which the individual stands.</p>
	 * <p>An example:<br>
	 * Individual 1 has a distance of 20 and individual 2 has a distance of 100.
	 * Then individual 1 is nearer to the exit than individual 2. The returned
	 * value is (20 - 100) and thus negative.</p>
	 * @param i1 the first individual
	 * @param i2 the second individual
	 * @return the difference in distance between the two individauls
	 */
	public int compare( Individual i1, Individual i2 ) {
		return i1.getStaticPotential().getTruePotential( i1.getCell() ) - i2.getStaticPotential().getTruePotential( i2.getCell() );
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "IndividualDistanceComparator";
	}
}
