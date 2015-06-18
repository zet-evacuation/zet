/**
 * OptimalStaticSearchTreeAlgorithm.java
 * Created: 05.07.2012, 11:22:07
 */
package zz_old_de.tu_berlin.math.coga.algorithm.searchtree;

import org.zetool.common.algorithm.Algorithm;
import zz_old_de.tu_berlin.math.coga.datastructure.searchtree.OptimalStaticSearchTree;
import org.zetool.graph.Node;
import java.util.logging.Level;


/**
 *
 * @param <T>
 * @author Jan-Philipp Kappmeier
 */
public class OptimalStaticSearchTreeAlgorithm<T extends Comparable<T>>
        extends Algorithm<OptimalStaticSearchTreeInstance<T>,OptimalStaticSearchTree<T>> {

	private double[][] c;
	private int[][] s;

	public OptimalStaticSearchTreeAlgorithm() {}

	@Override
	protected OptimalStaticSearchTree<T> runAlgorithm( OptimalStaticSearchTreeInstance<T> problem ) {
		OptimalStaticSearchTreeInstance<T> instance = getProblem();
		instance.sort();

		c = new double[instance.size()+1][];
		s = new int[instance.size()+1][];

		OptimalStaticSearchTree<T> ost = new OptimalStaticSearchTree<>( instance );

		for( int i = instance.size()+1; i >= 1; --i ) {
			c[instance.size()+1-i] = new double[i];
			s[instance.size()+1-i] = new int[i];
		}


		LOG.fine( "Interval length 0" );
		for( int j = 0; j <= instance.size(); ++j ) {
			c[j][0] = 0.0;
    }

		for( int interval = 1; interval <= instance.size(); ++interval ) {
			LOG.fine( "Intervallänge " + interval );
			for( int i = 0; i <= instance.size()-interval; ++i ) {
				LOG.finer( "C_" + i + "," + (i+interval) );
				// compute C_j,i d.h. j ist startindex, j+i endindex
				int j = i+interval;
				// compute omega
				double omega = 0;
				for( int k = i+1-1; k < i+interval; ++k ) {
					omega += instance.getProbability( k );
        }

				double min2 = Double.MAX_VALUE;
				int mink = 0;

				for( int k = 1; k <= interval; ++k ) {

					LOG.log( Level.FINER, "Check k = {0} also: {1}", new Object[]{k, k+i});

					double temp2 = c[i][k-1] + c[i+k][interval-k] + omega;

					if( temp2 < min2 ) {
						min2 = temp2;
						mink = i+k;
					}

				}
				c[i][interval] = min2;
				s[i][interval] = mink;
				LOG.log( Level.FINER, "Minimum for k={0}", mink);
			}
		}
		LOG.log( Level.INFO, "Optimale Kosten: {0}", c[0][instance.size()]);
		LOG.log( Level.INFO, "Optimales k: {0}", s[0][instance.size()]);

		LOG.info( "Baue Tree auf" );
		ost.setRoot( s[0][instance.size()], c[0][instance.size()] );

		buildTreeRec( ost, 0, instance.size() );

		return ost;
	}

	private void buildTreeRec( OptimalStaticSearchTree<T> bt, int start, int interval ) {
		// add nodes to the tree
		int mink = s[start][interval];
		Node tempRoot = bt.getNode( mink );

		LOG.log( Level.FINEST, "Performing search for node" + "[{0},{1}] breaking at {2}",
            new Object[]{start, start+interval, mink});

		// sind im Intervall [i,interval]
		// Links: [start,mink-1-start]
		// Rechts: [mink,start+interval-mink]
		if( interval > 1 ) {
			if( start != mink-1 ) {
				// left child exists
				Node leftChild = bt.getNode( s[start][mink-1-start] );
				bt.setLeft( tempRoot, leftChild, c[start][mink-1-start] );
				buildTreeRec( bt, start, mink-1-start );
			}
			if( mink < start+interval ) {
				// right child exists
				Node rightChild = bt.getNode( s[mink][start+interval-mink] );
				bt.setRight( tempRoot, rightChild, c[mink][start+interval-mink] );
				buildTreeRec( bt, mink, start+interval-mink );
			}

		}

	}



	public static void main( String[] arguments ) {
		//Debug.setDefaultLogLevel( Level.ALL );
		OptimalStaticSearchTreeInstance<Integer> instance = new OptimalStaticSearchTreeInstance<>();
		instance.addKeyValuePair( 1, 0.1 );
		instance.addKeyValuePair( 2, 0.1 );
		instance.addKeyValuePair( 3, 0.2 );
		instance.addKeyValuePair( 4, 0.05 );
		instance.addKeyValuePair( 5, 0.3 );
		instance.addKeyValuePair( 6, 0.25 );


		OptimalStaticSearchTreeAlgorithm<Integer> alg = new OptimalStaticSearchTreeAlgorithm<>();
		alg.setProblem( instance );
		alg.run();
		System.out.println( alg.getSolution() );

		instance = new OptimalStaticSearchTreeInstance<>();
		instance.addKeyValuePair( 1, 0.2 );
		instance.addKeyValuePair( 2, 0.05 );
		instance.addKeyValuePair( 3, 0.1 );
		instance.addKeyValuePair( 4, 0.25 );
		instance.addKeyValuePair( 5, 0.1 );
		instance.addKeyValuePair( 6, 0.3 );
//
		alg = new OptimalStaticSearchTreeAlgorithm<>();
		alg.setProblem( instance );
		alg.run();
		System.out.println( alg.getSolution() );

		instance = new OptimalStaticSearchTreeInstance<>();
		instance.addKeyValuePair( 1, 0.020 );
		instance.addKeyValuePair( 2, 0.025 );
		instance.addKeyValuePair( 3, 0.205 );
		instance.addKeyValuePair( 4, 0.5 );
		instance.addKeyValuePair( 5, 0.205 );
		instance.addKeyValuePair( 6, 0.025 );
		instance.addKeyValuePair( 7, 0.02 );

		alg = new OptimalStaticSearchTreeAlgorithm<>();
		alg.setProblem( instance );
		alg.run();
		System.out.println( alg.getSolution() );
		instance = new OptimalStaticSearchTreeInstance<>();
		instance.addKeyValuePair( 4, 0.5 );
		instance.addKeyValuePair( 2, 0.025 );
		instance.addKeyValuePair( 6, 0.025 );
		instance.addKeyValuePair( 3, 0.205 );
		instance.addKeyValuePair( 5, 0.205 );
		instance.addKeyValuePair( 1, 0.02 );
		instance.addKeyValuePair( 7, 0.02 );

		alg = new OptimalStaticSearchTreeAlgorithm<>();
		alg.setProblem( instance );
		alg.run();
		System.out.println( alg.getSolution() );

		instance = new OptimalStaticSearchTreeInstance<>();
		final int c = 15;
		for( int i = 0; i < c; ++i ) {
			instance.addKeyValuePair( i, 1./c );
		}

		alg = new OptimalStaticSearchTreeAlgorithm<>();
		alg.setProblem( instance );
		alg.run();
		System.out.println( alg.getSolution() );

	}
}
