/*
 * Sign.java
 * Created: 14.11.2010, 18:43:48
 */

package opengl.bsp;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public enum Sign {
	Negative(-1),
	Zero(0),
	Positive(1);

	int sign;

	private Sign( int sign ) {
		this.sign = sign;
	}
}