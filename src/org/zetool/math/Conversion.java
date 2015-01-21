/**
 * Conversion.java
 * Created: Mar 9, 2010,2:31:17 PM
 */
package org.zetool.math;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Conversion {
	public final static long secToNanoSeconds = 1000000000L;
	public final static double nanoSecondsToSec = 1.0 / secToNanoSeconds;
	public static final double ANGLE2DEG = java.lang.Math.PI / 180.0;
	public static final double DEG2ANGLE = 180.0 / java.lang.Math.PI;
}
