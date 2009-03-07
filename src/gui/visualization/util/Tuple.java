/**
 * Class Tupel
 * Erstellt 11.06.2008, 00:14:08
 */

package gui.visualization.util;

/**
 * Represents a simple tuple of two double values that can be directly accessed.
 * @author Jan-Philipp Kappmeier
 */
public class Tuple {
	/** The first value of the tuple */
	public double x;
	/** The second value of the tuple */
	public double y;

	/** Initializes a new instance of the tuple with two values 
	 * @param x the first value of the tuple
	 * @param y the second value of the tuple
	 */
	public Tuple( double x, double y ) {
		this.x = x;
		this.y = y;
	}
};
