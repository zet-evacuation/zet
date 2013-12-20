/**
 * SimpleMatrix.java
 * Created: 26.11.2013, 10:31:00
 */
package de.tu_berlin.math.coga.math.matrix;


/**
 * Simple datastructure for a {@literal m \times n} matrix.
 * @author Jan-Philipp Kappmeier
 */
public class SimpleMatrix {
	private final int m; // rows
	private final int n; // columns
	private final double[][] matrix;

	/**
	 * Private constructor for efficient creation of results of arithmetic
	 * operations avoiding array copy.
	 */
	private SimpleMatrix( int m, int n, double[][] matrix ) {
		this.m = m;
		this.n = n;
		this.matrix = matrix;
	}

	/**
	 * Creates a unit square matrix with {@code n} rows and columns.
	 * @param n the dimension
	 */
	public SimpleMatrix( int n ) {
		matrix = new double[n][n];
		this.m = n;
		this.n = n;
		for( int i = 0; i < n; ++i )
			for( int j = 0; j < n; ++j )
				matrix[i][j] = i == j ? 1 : 0;
	}

	/**
	 * Creates a unit matrix with {@code n} rows and {@code m} columns.
	 * @param m
	 * @param n
	 */
	public SimpleMatrix( int m, int n ) {
		this.m = m;
		this.n = n;
		matrix = new double[m][n];
		for( int i = 0; i < m; ++i )
			for( int j = 0; j < n; ++j )
				matrix[i][j] = i == j ? 1 : 0;
	}

	/**
	 * Constructor creating a copy of a matrix provided as two dimensional array.
	 * @param matrix
	 */
	public SimpleMatrix( double[][] matrix ) {
		m = matrix.length;
		n = matrix[0].length;
		this.matrix = new double[m][n];
		arrayCopy( matrix, this.matrix );
	}

	private static void arrayCopy( double[][] source, double[][] dest ) {
		for( int i = 0; i < source.length; i++ )
			System.arraycopy( source[i], 0, dest[i], 0, source[i].length );
	}

	/**
	 * Implementation of matrix addition. Adds two matrices with the same
	 * dimensionality and returns the new matrix.
	 *
	 * @param B
	 * @return
	 */
	public SimpleMatrix add( SimpleMatrix B ) {
		if( m != B.m || n != B.n )
			throw new IllegalArgumentException( "Dimensions are not equal!" );
		double[][] newMatrix = new double[m][n];
		for( int i = 0; i < m; ++i )
			for( int j = 0; j < n; ++j )
				newMatrix[i][j] = matrix[i][j] + B.matrix[i][j];
		SimpleMatrix C = new SimpleMatrix( m, n, newMatrix );
		return C;
	}

	/**
	 * Implementation of simple matrix multiplication.
	 *
	 * @param B
	 * @return
	 */
	public SimpleMatrix mult( SimpleMatrix B ) {
		if( n != B.m || m != B.n )
			throw new IllegalArgumentException( "Dimensions do not fit!" );
		int newm = m;
		int newn = B.n;
		double[][] newMatrix = new double[newm][newn];

		for( int i = 0; i < newm; ++i )
			for( int j = 0; j < newn; ++j ) {
				// ith row * jth column
				double val = 0;
				for( int k = 0; k < n; ++k )
					val += matrix[i][k] * B.matrix[k][j];
				newMatrix[i][j] = val;
			}

		SimpleMatrix C = new SimpleMatrix( newm, newn, newMatrix );
		return C;
	}

	/**
	 * Scalar multiplication with an element of the field.
	 *
	 * @param s the scalar
	 * @return the matrix containing the old matrix multiplicated with a scalar
	 * value.
	 */
	public SimpleMatrix scalar( double s ) {
		double[][] newMatrix = new double[m][n];
		for( int i = 0; i < m; ++i )
			for( int j = 0; j < n; ++j )
				newMatrix[i][j] = matrix[i][j] * s;
		return new SimpleMatrix( m, n, newMatrix );
	}

	/**
	 * Computes the transposed matrix of the given matrix.
	 * @return the transposed matrix.
	 */
	public SimpleMatrix transpose() {
		int newn = m;
		int newm = n;
		double[][] newMatrix = new double[newm][newn];
		for( int i = 0; i < m; ++i )
			for( int j = 0; j < n; ++j )
				newMatrix[j][i] = matrix[i][j];
		return new SimpleMatrix( newm, newn, newMatrix );
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < m; ++i ) {
			for( int j = 0; j < n - 1; ++j )
				sb.append( matrix[i][j] ).append( " " );
			sb.append( matrix[i][n - 1] ).append( "\n" );
		}
		return sb.toString();
	}

	public static void main( String... args ) {
		SimpleMatrix M1 = new SimpleMatrix( 3 );
		System.out.println( M1 );

		SimpleMatrix M2 = new SimpleMatrix( 1 );
		System.out.println( M2 );

		SimpleMatrix M3 = new SimpleMatrix( 3, 4 );
		System.out.println( M3 );

		SimpleMatrix M4 = new SimpleMatrix( 4, 3 );
		System.out.println( M4 );

		SimpleMatrix M1_2 = M1.add( M1 );
		System.out.println( M1_2 );

		double[][] matrix = new double[2][3];
		matrix[0][0] = 3;
		matrix[0][1] = 2;
		matrix[0][2] = 1;
		matrix[1][0] = 1;
		matrix[1][1] = 0;
		matrix[1][2] = 2;

		SimpleMatrix PM1 = new SimpleMatrix( matrix );
		matrix[0][0] = 9000;
		System.out.println( PM1 );

		matrix = new double[3][2];
		matrix[0][0] = 1;
		matrix[0][1] = 2;
		matrix[1][0] = 0;
		matrix[1][1] = 1;
		matrix[2][0] = 4;
		matrix[2][1] = 0;

		SimpleMatrix PM2 = new SimpleMatrix( matrix );
		System.out.println( PM2 );

		SimpleMatrix MResult = PM1.mult( PM2 );
		System.out.println( MResult );
	}

}
