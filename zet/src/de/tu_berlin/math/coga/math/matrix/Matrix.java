/**
 * Matrix.java
 * Created: 04.04.2012, 17:57:56
 */
package de.tu_berlin.math.coga.math.matrix;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Matrix {

	/** Private constructor only. */
	private Matrix() {}

	/**
	 * Multiplicates two 2x2-matrices
	 * @param m1 the first matrix
	 * @param m2 the second matrix
	 * @return the result m1 * m2
	 */
	public static int[][] matrixMultiplication( int[][] m1, int[][] m2 ) {
		int[][] m = new int[2][2];
		m[0][0] = m1[0][0] * m2[0][0] + m1[0][1] * m2[1][0];
		m[0][1] = m1[0][0] * m2[0][1] + m1[0][1] * m2[1][1];
		m[1][0] = m1[1][0] * m2[0][0] + m1[1][1] * m2[1][0];
		m[1][1] = m1[1][0] * m2[0][1] + m1[1][1] * m2[1][1];
		return m;
	}

}
