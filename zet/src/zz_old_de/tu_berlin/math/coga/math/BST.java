/**
 * BST.java
 * Created: Aug 11, 2010,5:03:34 PM
 */
package zz_old_de.tu_berlin.math.coga.math;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BST {

	static double[] p = {0.15,0.1,0.05,0.1,0.2};
	static int[][] root;
	static double e[][];

	//static double b[] = {0.1, 0.1, 0.2, 0.05, 0.3, 0.25};
	//static double b[] = {1./16,1./16,1./16,1./16,1./8,1./8,1./2 };
	static int h[] = {1, 10, 100, 10000, 1000, 10, 1};
	//static double b[] = {1./32,1./32,1./16,1./16,1./8,1./8,1./4+1./32 };
	static double b[] = {1./32,1./16,1./8,1./2+1./16,1./8,1./16,1./32 };


	public static void main( String[] arguments ) {
		System.out.println("t");
		root = new int[8][8];
		e = new double[6][6];

		int n = 6;

		int sum = 0;
		for( int i = 0; i < h.length; ++i )
			sum += h[i];
		double dsum = 0;
		for( int i = 0; i < h.length; ++i )
			dsum += b[i];
		System.out.println( "Summe: " + dsum );
//		for( int i = 0; i < h.length; ++i )
//			b[i] = h[i] / (double)sum;
		
		double C[][] = new double[8][8];
		double w[][] = new double[8][8];

		// Init
		for( int i = 1; i <= 7; ++i ) {
			C[i][i] = 0;
			//w[i-1][i] = b[i-1];
		}

		// iteriere über die intervalllängen von 1 bis 5:
		for( int l = 1; l <= 7; ++l ) {
			System.out.println( "Check Intervallänge " + l );

			for( int i = 0; i <= 7 - l; ++i ) {
				int j = i + l;
				w[i][j] = w[i][j-1] + b[j-1];
				double min = Double.POSITIVE_INFINITY;

				int mink = 0;
				//System.out.println( "Using w(" + i + ","+j +") = " + w[i][j] );
				for( int k = i+1; k <= j; ++k ) {
					//System.out.println( "Co")
					// compute minimum

					double t = w[i][j] + C[i][k-1] + C[k][j];

					if( t < min ) {
						min = t;
						root[i][j] = k;
						mink = k;
					}
				}
				C[i][j] = min;
				System.out.println( "Setze C" + i + "," + j + " = " + min + " bei k = " + mink );

			}

		}
		System.out.println( "Wurzel des Baumes ist " + root[0][7] );
		computeTree( 0, 7 );
	}

	static int computeTree ( int start, int end ) {
		final int node = root[start][end];

		if( start == end )
			return start;
		if( start == end - 1 )
			return end;

		if( node > start + 1 ) {
			// left part:
			int l1 = start;
			int l2 = node - 1;
			int left = computeTree( l1, l2 );
			System.out.println( "Left of " + node + ": " + left );
		}

		if( node != end ) {
			// right part
			int r1 = node;
			int r2 = end;

			int right = computeTree( r1, r2 );
			System.out.println( "Right of " + node + ": " + right );
		}
		return node;

	}

}
