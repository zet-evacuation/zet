/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
package algo.graph.nashflow;

import algo.graph.thinflow.ThinFlowAlgo;
import ds.graph.problem.ThinFlowProblem;
import ds.graph.flow.ThinFlow;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.netflow.ds.network.DynamicNetwork;
import ds.graph.problem.NashFlowProblem;
import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.math.coga.algorithm.shortestpath.RationalDijkstra;
import ds.graph.flow.NashFlow;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Vector;
import java.util.List;
import Jama.Matrix;
import de.tu_berlin.coga.container.mapping.IdentifiableDoubleMapping;

/**
 *
 * @author Sebastian Schenker
 */
public class NashFlowAlgo extends Algorithm<NashFlowProblem, NashFlow> {

	private static final double EPSILON = 0.00001;
	private static final double FIXPOINTACCURACY = 0.001;

	private Vector< double[]> OutFlowValueVector;
	private Vector< double[]> InFlowValueVector;
	private Vector< IdentifiableDoubleMapping<Node>> NodelabelVector;
	private Vector<Double> ThetaVector;
	private Vector<List<List<Edge>>> usedPaths;
	private Vector<HashMap<Edge, Integer>> usedEdgesCountVector;
	private Vector<HashMap<Edge, List<List<Edge>>>> edgeToUsedPathsVector;
	private List<HashMap<List<Edge>, Double>> pathFlowSizeList;

	private void initializeDatastructures() {
		OutFlowValueVector = new Vector<>();
		InFlowValueVector = new Vector<>();
		NodelabelVector = new Vector<>();
		ThetaVector = new Vector<>();
		usedPaths = new Vector<>();
		usedEdgesCountVector = new Vector<>();
		edgeToUsedPathsVector = new Vector<>();
		pathFlowSizeList = new ArrayList<>();
	}

	private void addPathFlowSizeMap( HashMap<List<Edge>, Double> pathFlowSizes ) {
		pathFlowSizeList.add( pathFlowSizes );
	}

	public Vector<IdentifiableDoubleMapping<Node>> getNodelabelVector() {
		return NodelabelVector;
	}

	public List<HashMap<List<Edge>, Double>> getPathFlowSizes() {
		return pathFlowSizeList;
	}

	public Vector<HashMap<Edge, List<List<Edge>>>> getEdgeToUsedPathsVector() {
		return edgeToUsedPathsVector;
	}

	private void addEdgeToUsedPathsMap( HashMap<Edge, List<List<Edge>>> edgeMap ) {
		edgeToUsedPathsVector.add( edgeMap );
	}

	public Vector<HashMap<Edge, Integer>> getUsedEdgesCountVector() {
		return usedEdgesCountVector;
	}

	public Vector<List<List<Edge>>> getUsedPaths() {
		return usedPaths;
	}

	public Vector<double[]> getOutFlowValues() {
		return OutFlowValueVector;
	}

	public Vector<double[]> getInFlowValues() {
		return InFlowValueVector;
	}

	public Vector<Double> getThetas() {
		return ThetaVector;
	}

	private void addUsedPaths( List<List<Edge>> delPaths ) {
		usedPaths.add( delPaths );
	}

	@Override
	public NashFlow runAlgorithm( NashFlowProblem nashprob ) {
		initializeDatastructures();
		try {
			if( nashprob.getInitCap() == Double.MAX_VALUE ) {
				runNashFlowWithUnitThinFlow( nashprob );
			} else if( nashprob.getEdgesToOrigSources() != null ) {
				runNashFlowWithMultipleSources( nashprob );
			} else {
				runNashFlow( nashprob );
			}
		} catch( Exception e ) {
			System.out.println( "Fehler in runNashFlow " + e.toString() );
		}
		return new NashFlow( nashprob, NodelabelVector );
	}

	private Matrix computeSolutionSystemOfLinEquations( double[][] lhs, double[] rhs, int sourcePosition ) throws Exception {
		Matrix A = new Matrix( lhs );
		Matrix b = new Matrix( rhs, rhs.length );

		if( A.getColumnDimension() - A.rank() == 1 ) {
			int[] columnIndecies = new int[A.getColumnDimension() - 1];
			int j = 0;
			for( int i = 0; i <= A.getColumnDimension() - 1; i++ ) {
				if( i != sourcePosition ) {
					columnIndecies[j++] = i;
				}
			}
			A = A.getMatrix( 0, A.getRowDimension() - 1, columnIndecies );
		} else if( A.getColumnDimension() - A.rank() >= 2 ) {
			throw new Exception( "Rangunterschied zu gross" );
		}

		return A.solve( b );
	}

	private double computeAlphaForE_1( HashMap<Edge, Double> transitMap, IdentifiableDoubleMapping<Node> nodelabels,
					IdentifiableDoubleMapping<Node> thinflowNodelabels, ArrayList<Edge> E_1 ) throws Exception {

		double alpha0 = Double.POSITIVE_INFINITY;
		double alpha1 = Double.NEGATIVE_INFINITY;
		Node start, end;
		double lv, lw, llv, llw;
		double alpha_tmp = Double.POSITIVE_INFINITY;
		double alphaE1 = Double.POSITIVE_INFINITY;

		for( Edge e : E_1 ) {
			start = e.start();
			end = e.end();
			lv = nodelabels.get( start );
			lw = nodelabels.get( end );
			llv = thinflowNodelabels.get( start );
			llw = thinflowNodelabels.get( end );

			if( (llw - llv) < -1 * EPSILON ) /*value has to be compared to alpha_E1[0]*/ {
				alpha_tmp = (transitMap.get( e ) - lw + lv) / (llw - llv);

				if( alpha_tmp < alpha0 ) {
					alpha0 = alpha_tmp;
				}
			} else if( (llw - llv) > EPSILON ) /*value has to be compared to alpha_E1[1]*/ {
				alpha_tmp = (transitMap.get( e ) - lw + lv) / (llw - llv);

				if( alpha_tmp > alpha1 ) {
					alpha1 = alpha_tmp;
				}
			} else if( (lw - lv) >= transitMap.get( e ) ) {
				continue;

			} else {
				throw new Exception( "division by zero in computeAlpha" );
			}

			//check if alpha_E1 values are feasible
			if( alpha0 < alpha1 ) {
				throw new Exception( "alpha_E1[0] < alpha_E1[1]" );
			} else if( alpha0 < 0.0 ) {
				throw new Exception( "alpha_E1[0] < 0" );
			} else {
				alphaE1 = alpha0;
			}

		}

		return alphaE1;

	}

	private double computeAlphaForGminusGtheta( HashMap<Edge, Double> transitMap, IdentifiableDoubleMapping<Node> nodelabels,
					IdentifiableDoubleMapping<Node> thinflowNodelabels, ArrayList<Edge> E_GminusGtheta ) throws Exception {

		double alpha0 = Double.POSITIVE_INFINITY;
		double alpha1 = Double.NEGATIVE_INFINITY;
		Node start, end;
		double lv, lw, llv, llw;
		double alpha_tmp = Double.POSITIVE_INFINITY;
		double alphaGminusGtheta = Double.POSITIVE_INFINITY;

		for( Edge e : E_GminusGtheta ) {
			start = e.start();
			end = e.end();
			lv = nodelabels.get( start );
			lw = nodelabels.get( end );
			llv = thinflowNodelabels.get( start );
			llw = thinflowNodelabels.get( end );

			if( (llw - llv) < -1 * EPSILON ) /*value has to be compared to alpha[1] */ {
				alpha_tmp = (transitMap.get( e ) - lw + lv) / (llw - llv);
				if( alpha_tmp > alpha1 ) {
					alpha1 = alpha_tmp;
				}

			} else if( (llw - llv) > EPSILON ) /* value has to be compared to alpha[0] */ {
				alpha_tmp = (transitMap.get( e ) - lw + lv) / (llw - llv);
				if( alpha_tmp < alpha0 ) {
					alpha0 = alpha_tmp;
				}
			}

			if( alpha0 < alpha1 ) {
				throw new Exception( "alpha_GminusGtheta[0] < alpha_GminusGtheta[1]" );
			} else if( alpha0 < 0.0 ) {
				throw new Exception( "alpha_GminusGtheta[0] < 0" );
			} else {
				alphaGminusGtheta = alpha0;
			}

		}

		return alphaGminusGtheta;
	}

	private double computeAlpha( HashMap<Edge, Double> transitMap, IdentifiableDoubleMapping<Node> nlabel,
					IdentifiableDoubleMapping<Node> thinlabel, ArrayList<Edge> E1, ArrayList<Edge> GminusGtheta ) throws Exception {

		//minimum of alphaE1 and alphaGminusGtheta
		double alphaE1 = computeAlphaForE_1( transitMap, nlabel, thinlabel, E1 );

		double alphaGminusGtheta = computeAlphaForGminusGtheta( transitMap, nlabel, thinlabel, GminusGtheta );

		if( alphaGminusGtheta < alphaE1 ) {
			return alphaGminusGtheta;
		} else {
			return alphaE1;
		}

	}

	private void computeUsedEdgesPathsMaps( List<Edge> edges, List<List<Edge>> usedPaths ) {
		HashMap<Edge, Integer> usedEdgesCount = new HashMap<Edge, Integer>();
		HashMap<Edge, List<List<Edge>>> edgeToUsedPaths = new HashMap<Edge, List<List<Edge>>>();
		for( Edge edge : edges ) {
			usedEdgesCount.put( edge, 0 );
			edgeToUsedPaths.put( edge, new ArrayList<List<Edge>>() );
		}
		for( List<Edge> edgelist : usedPaths ) {
			for( Edge ed : edgelist ) {
				usedEdgesCount.put( ed, usedEdgesCount.get( ed ) + 1 );
				edgeToUsedPaths.get( ed ).add( edgelist );
			}
		}
		addUsedEdgesCountMap( usedEdgesCount );
		addEdgeToUsedPathsMap( edgeToUsedPaths );
	}

	private void addUsedEdgesCountMap( HashMap<Edge, Integer> edgeMap ) {
		usedEdgesCountVector.add( edgeMap );
	}

	public void runNashFlow( NashFlowProblem nashprob ) throws Exception {

		double theta = 0.0;

		Node source = nashprob.getSource();
		DynamicNetwork workingGraph = nashprob.getGraph();

		IdentifiableDoubleMapping<Node> nodelabels = new IdentifiableDoubleMapping<>( workingGraph.nodeCount() );
		IdentifiableDoubleMapping<Edge> waitingtimes = new IdentifiableDoubleMapping<>( workingGraph.edgeCount() );

		/*convert (TransitTimes) DoubleMap to (transitMap) Java map*/
		HashMap<Edge, Double> transitMap = new HashMap<>( workingGraph.edgeCount() );
		for( Edge e : workingGraph.edges() ) {
			waitingtimes.set( e, 0.0 ); //set waiting time of every edge to zero
			transitMap.put( e, nashprob.getEdgeTransitTime( e ) );
		}


		/*shortest path values between source and other nodes*/
		RationalDijkstra dijk = new RationalDijkstra( workingGraph, transitMap, source );
		/*compute distances*/
		dijk.run();
		/*map for distances*/
		Map<Node, Double> distanceMap = new HashMap<>( workingGraph.nodeCount() );
		distanceMap = dijk.getDistances();

		/*set Nodelabels equal to distances*/
		for( Node n : workingGraph.nodes() ) {
			nodelabels.set( n, distanceMap.get( n ) );
		}

		double initCap = nashprob.getInitCap();

		Node start, end;

		ArrayList<Edge> E_1 = new ArrayList<>( workingGraph.edgeCount() );
		ArrayList<Edge> E_GminusGtheta = new ArrayList<>( workingGraph.edgeCount() );

		int iteration = 0;

		while( true ) {

			//create shortest path network G_theta
			DynamicNetwork spNetwork = new DynamicNetwork( workingGraph );

			E_GminusGtheta.clear();

			for( Edge e : workingGraph.edges() ) {
				start = e.start();
				end = e.end();

				if( ((nodelabels.get( end ) - nodelabels.get( start ) + EPSILON)) < transitMap.get( e )
								&& waitingtimes.get( e ) <= 0.0 ) {

					spNetwork.removeEdge( e );
					E_GminusGtheta.add( e );
				}
			}

			IdentifiableDoubleMapping spNetworkCapacities = new IdentifiableDoubleMapping( spNetwork.edgeCount() );
			IdentifiableDoubleMapping thinflowCapacities = new IdentifiableDoubleMapping( spNetwork.edgeCount() + E_1.size() );
			for( Edge ed : spNetwork.edges() ) {
				spNetworkCapacities.set( ed, nashprob.getEdgeCapacity( ed ) );
				thinflowCapacities.set( ed, nashprob.getEdgeCapacity( ed ) );
			}

			/*restate thinflow with resetting into thinflow with resetting on empty set*/
			DynamicNetwork thinflowGraph = new DynamicNetwork( spNetwork );

			HashMap<Edge, Node> identMap = new HashMap<>(); /*map to identify new edge with start node*/

			HashMap<Node, ArrayList<Edge>> identEdgeMap = new HashMap<>();
			HashMap<Edge, Edge> newedgeToOrigedgeMap = new HashMap<>(); /*map for indentification in thinflow-Algorithm*/

			int num = workingGraph.edgeCount();


			/*create identEdgeMap*/
			for( Edge e : E_1 ) {
				start = e.start();
				identEdgeMap.put( start, new ArrayList<>() );
			}

			for( Edge e : E_1 ) {
				start = e.start();
				end = e.end();
				if( start != source ) {
					Edge ne = new Edge( num++, source, end );

					identMap.put( ne, start );
					identEdgeMap.get( start ).add( ne );
					thinflowGraph.addEdge( ne );
					thinflowCapacities.set( ne, spNetworkCapacities.get( e ) );
					thinflowGraph.removeEdge( e );
					newedgeToOrigedgeMap.put( ne, e );
				}
			}

			/*compute thinflow of value u_0 (initial capacity)*/
			ThinFlowProblem tfprob = new ThinFlowProblem( thinflowGraph, thinflowCapacities, source, nashprob.getSink(), initCap );
			ThinFlowAlgo tfalg = new ThinFlowAlgo();
          //tfalg.setProblem(tfprob);
			//tfalg.run();

			ThinFlow tf = null;

          //ThinFlow(new ThinFlowProblem(thinflowGraph,thinflowCapacities,source,nashprob.getSink()),newedgeToOrigedgeMap);
			boolean fixpointDifference = true;
			IdentifiableDoubleMapping<Edge> thinflowFlowvalues = null;
			IdentifiableDoubleMapping<Node> thinflowNodelabels = null;

          //tf.setNodeDemand(source(),initCap);
			//tf.setNodeDemand(sink(),-1*initCap);
			//DoubleMap<Node> nodeDem = new DoubleMap(tf.getProblem().getNodeDemands());
			IdentifiableDoubleMapping<Node> nodeDem = new IdentifiableDoubleMapping( tfprob.getNodeDemands() );

			while( fixpointDifference ) {

				fixpointDifference = false;

				tfalg.setProblem( tfprob );
				tfalg.run();
				tf = tfalg.getSolution();


				/*get Nodelabels, EdgeFlow values*/
				thinflowFlowvalues = tf.getEdgeFlowValues();
				thinflowNodelabels = tf.getNodeLabels();

				for( Node s : identEdgeMap.keySet() ) {

					if( Math.abs( Math.abs( tf.getProblem().getNodeDemand( s ) ) - NodeOutFlowValue( s, identEdgeMap.get( s ), thinflowFlowvalues ) ) > FIXPOINTACCURACY ) {
						fixpointDifference = true;
					}
				}

				tfprob.setNodeDemand( source, initCap );
				tfprob.setNodeDemand( nashprob.getSink(), -1 * initCap );
				for( Edge e : identMap.keySet() ) {
					start = identMap.get( e );
					tfprob.setNodeDemand( start, 0.0 );
				}

				/*change node demands according to restating of thinflow with resetting into tf without resetting*/
				for( Edge e : identMap.keySet() ) {
					start = identMap.get( e );

					if( start != source ) {

						tfprob.setNodeDemand( start, tf.getProblem().getNodeDemand( start ) - 1 * thinflowFlowvalues.get( e ) );

						tfprob.setNodeDemand( source, tf.getProblem().getNodeDemand( source ) + thinflowFlowvalues.get( e ) );
					}

				}
			}

			/*check if there are nodelabels less than 1.0 and if there are paths without
			 * resetting to these nodes */
			FlowDecomposition pathComp = new FlowDecomposition( workingGraph.nodes(), tfalg.getUsedEdges(), newedgeToOrigedgeMap, tf.getEdgeFlowValues(), getProblem().getOrigSource(), getProblem().getSink(), getProblem().getInitCap() );
			pathComp.computePathAndFlowSizes();


			/* compute used paths */
			List<List<Edge>> usedpaths = pathComp.getPaths();
			computeUsedEdgesPathsMaps( workingGraph.edges(), usedpaths );

			addUsedPaths( usedpaths );
			addPathFlowSizeMap( pathComp.getPathFlowSizes() );

			getProblem().setNodeLabelDerivations( thinflowNodelabels );
			thinflowNodelabels = getProblem().getNodeLabelDerivations();

			if( !E_1.isEmpty() ) {

				NodePartition partition = new NodePartition( spNetwork.nodes(), thinflowNodelabels );

             //partition.printArray();
				LabelMatrix lbmatrix = new LabelMatrix( partition, spNetwork.edges(), E_1, spNetworkCapacities, nodeDem );
				lbmatrix.computeCoefficientMatrix();

				Matrix x = computeSolutionSystemOfLinEquations( lbmatrix.getCoeffMatrix(), lbmatrix.getRHS(), partition.getNodePosition( getProblem().getSource() ) );

				for( Node n : partition.getPartitionArray()[partition.getNodePosition( getProblem().getSource() )] ) {
					thinflowNodelabels.set( n, 0.0 );
				}

				if( x.getRowDimension() == partition.getPartitionArray().length - 1 ) {

					int j = 0;
					for( int i = 0; i < partition.getPartitionArray().length; i++ ) {

						if( i == partition.getNodePosition( getProblem().getSource() ) ) {

							for( Node n : partition.getPartitionArray()[i] ) {
								thinflowNodelabels.set( n, 1.0 );
							}
						} else {

							for( Node n : partition.getPartitionArray()[i] ) {
								thinflowNodelabels.set( n, x.get( j, 0 ) );
							}
							j++;
						}
					}
				} else {
					throw new Exception( "Laenge stimmen nicht ueberein" );
				}

			}

			//compute Alpha
			double alpha = computeAlpha( transitMap, nodelabels, thinflowNodelabels, E_1, E_GminusGtheta );

			// Update parameters //
			ThetaVector.add( iteration, new Double( theta ) );
			NodelabelVector.add( iteration, new IdentifiableDoubleMapping( nodelabels ) );
			OutFlowValueVector.add( iteration, computeOutFlowValues( spNetwork, thinflowFlowvalues, thinflowNodelabels ) );
			InFlowValueVector.add( iteration, computeInFlowValues( spNetwork, thinflowFlowvalues, thinflowNodelabels ) );

			theta += alpha;
			iteration++;

			if( alpha == Double.MAX_VALUE || alpha == Double.POSITIVE_INFINITY ) {
				break;
			}

			//update Nodelabels
			for( Node n : spNetwork.nodes() ) {
				nodelabels.set( n, nodelabels.get( n ) + alpha * thinflowNodelabels.get( n ) );
			}

			//update waiting times for edges in Gtheta
			double waittime;
			E_1.clear();
			for( Edge e : spNetwork.edges() ) {
				start = e.start();
				end = e.end();

				if( start == getProblem().getSource() ) {
					waitingtimes.set( e, 0.0 );
				} else if( (waittime = nodelabels.get( end ) - nodelabels.get( start ) - transitMap.get( e )) > EPSILON ) {
					waitingtimes.set( e, waittime );
					E_1.add( e );

				} else {
					waitingtimes.set( e, 0.0 );
				}

			}

		}

		System.out.println( "Nodelabels" + NodelabelVector.toString() );
		System.out.println( "Theta " + ThetaVector.toString() );
		System.out.println( "InFlowvalues" );
		printFlowValueVector( InFlowValueVector );
		System.out.println( "OutFlowvalues" );
		printFlowValueVector( OutFlowValueVector );

	}

	public void runNashFlowWithUnitThinFlow( NashFlowProblem nashprob ) throws Exception {
		System.out.println( "NASHFLOW WITH UNIT THINFLOW" );
		double theta = 0.0;

		Node source = nashprob.getSource();

		DynamicNetwork workingGraph = nashprob.getGraph();

		IdentifiableDoubleMapping<Node> nodelabels = new IdentifiableDoubleMapping<>( workingGraph.nodeCount() );
		IdentifiableDoubleMapping<Edge> waitingtimes = new IdentifiableDoubleMapping<>( workingGraph.edgeCount() );

		/*convert (TransitTimes) DoubleMap to (transitMap) Java map*/
		HashMap<Edge, Double> transitMap = new HashMap<>( workingGraph.edgeCount() );
		for( Edge e : workingGraph.edges() ) {
			waitingtimes.set( e, 0.0 ); //set waiting time of every edge to zero
			transitMap.put( e, nashprob.getEdgeTransitTime( e ) );
		}

		/*shortest path values between source and other nodes*/
		RationalDijkstra dijk = new RationalDijkstra( workingGraph, transitMap, source );
		/*compute distances*/
		dijk.run();
		/*map for distances*/
		Map<Node, Double> distanceMap = new HashMap<>( workingGraph.nodeCount() );
		distanceMap = dijk.getDistances();


		/*set Nodelabels equal to distances*/
		for( Node n : workingGraph.nodes() ) {
			nodelabels.set( n, distanceMap.get( n ) );
		}

		Node start, end;

		ArrayList<Edge> E_1 = new ArrayList( workingGraph.edgeCount() );
		ArrayList<Edge> E_GminusGtheta = new ArrayList( workingGraph.edgeCount() );

		int iteration = 0;

		while( true ) {

			//create shortest path network G_theta
			DynamicNetwork spNetwork = new DynamicNetwork( workingGraph );

			E_GminusGtheta.clear();

			for( Edge e : workingGraph.edges() ) {
				start = e.start();
				end = e.end();

				if( ((nodelabels.get( end ) - nodelabels.get( start ) + EPSILON)) < transitMap.get( e )
								&& waitingtimes.get( e ) <= 0.0 ) {

					spNetwork.removeEdge( e );
					E_GminusGtheta.add( e );
				}
			}

			IdentifiableDoubleMapping spNetworkCapacities = new IdentifiableDoubleMapping( spNetwork.edgeCount() );
			IdentifiableDoubleMapping thinflowCapacities = new IdentifiableDoubleMapping( spNetwork.edgeCount() + E_1.size() );
			for( Edge ed : spNetwork.edges() ) {
				spNetworkCapacities.set( ed, nashprob.getEdgeCapacity( ed ) );
				thinflowCapacities.set( ed, nashprob.getEdgeCapacity( ed ) );
			}


			/*restate thinflow with resetting into thinflow with resetting on empty set*/
			DynamicNetwork thinflowGraph = new DynamicNetwork( spNetwork );

			HashMap<Edge, Node> identMap = new HashMap<>(); /*map to identify new edge with start node*/

			HashMap<Node, ArrayList<Edge>> identEdgeMap = new HashMap<>();
			HashMap<Edge, Edge> newedgeToOrigedgeMap = new HashMap<>(); /*map for indentification in thinflow-Algorithm*/

			int num = workingGraph.edgeCount();


			/*create identEdgeMap*/
			for( Edge e : E_1 ) {
				start = e.start();
				identEdgeMap.put( start, new ArrayList<Edge>() );
			}

			for( Edge e : E_1 ) {
				start = e.start();
				end = e.end();
				if( start != source ) {
					Edge ne = new Edge( num++, source, end );

					identMap.put( ne, start );
					identEdgeMap.get( start ).add( ne );
					thinflowGraph.addEdge( ne );
					thinflowCapacities.set( ne, spNetworkCapacities.get( e ) );
					thinflowGraph.removeEdge( e );
					newedgeToOrigedgeMap.put( ne, e );
				}
			}


			/*compute thinflow of value 1*/
			System.out.println( "thinflowGraph = " + thinflowGraph.deepToString() );
			ThinFlowProblem tfprob = new ThinFlowProblem( thinflowGraph, thinflowCapacities, source, nashprob.getSink() );
			ThinFlowAlgo tfalg = new ThinFlowAlgo();

			ThinFlow tf = null;

			boolean fixpointDifference = true;
			IdentifiableDoubleMapping<Edge> thinflowFlowvalues = null;
			IdentifiableDoubleMapping<Node> thinflowNodelabels = null;

			//DoubleMap<Node> nodeDem = new DoubleMap(tf.getProblem().getNodeDemands());
			IdentifiableDoubleMapping<Node> nodeDem = new IdentifiableDoubleMapping<>( tfprob.getNodeDemands() );

			System.out.println( "enter fixpointdifference in nashflowalgo" );

			while( fixpointDifference ) {
				fixpointDifference = false;
				System.out.println( "thinflowproblem = " + tfprob.getNodeDemands() );
				tfalg.setProblem( tfprob );
				tfalg.run();
				tf = tfalg.getSolution();

				/*get Nodelabels, EdgeFlow values*/
				thinflowFlowvalues = tf.getEdgeFlowValues();
				thinflowNodelabels = tf.getNodeLabels();

				for( Node s : identEdgeMap.keySet() ) {
					if( Math.abs( Math.abs( tf.getProblem().getNodeDemand( s ) ) - NodeOutFlowValue( s, identEdgeMap.get( s ), thinflowFlowvalues ) ) > FIXPOINTACCURACY ) {
						fixpointDifference = true;
					}
				}

				tfprob.setNodeDemand( source, 1.0 );
				tfprob.setNodeDemand( nashprob.getSink(), -1.0 );
				for( Edge e : identMap.keySet() ) {
					start = identMap.get( e );
					tfprob.setNodeDemand( start, 0.0 );
				}
				/*change node demands according to restating of thinflow with resetting into tf without resetting*/

				for( Edge e : identMap.keySet() ) {
					start = identMap.get( e );

					if( start != source ) {

						tfprob.setNodeDemand( start, tf.getProblem().getNodeDemand( start ) - 1 * thinflowFlowvalues.get( e ) );

						tfprob.setNodeDemand( source, tf.getProblem().getNodeDemand( source ) + thinflowFlowvalues.get( e ) );
					}

				}
				System.out.println( "fixpoint = " + fixpointDifference );

			}

			System.out.println( "thinflowFlowValues = " + thinflowFlowvalues.toString() );
			System.out.println( "thinflowNodelabels = " + thinflowNodelabels.toString() );

			FlowDecomposition pathComp = new FlowDecomposition( workingGraph.nodes(), tfalg.getUsedEdges(), newedgeToOrigedgeMap, tf.getEdgeFlowValues(), getProblem().getSource(), getProblem().getSink(), 1.0 );
			pathComp.computePathAndFlowSizes();


			/* compute used paths */
			List<List<Edge>> usedpaths = pathComp.getPaths();
			computeUsedEdgesPathsMaps( workingGraph.edges(), usedpaths );

			addUsedPaths( usedpaths );
			addPathFlowSizeMap( pathComp.getPathFlowSizes() );

			getProblem().setNodeLabelDerivations( thinflowNodelabels );
			thinflowNodelabels = getProblem().getNodeLabelDerivations();

			if( !E_1.isEmpty() ) {

				NodePartition partition = new NodePartition( spNetwork.nodes(), thinflowNodelabels );

             //partition.printArray();
				LabelMatrix lbmatrix = new LabelMatrix( partition, spNetwork.edges(), E_1, spNetworkCapacities, nodeDem );
				lbmatrix.computeCoefficientMatrix();
				Matrix x = computeSolutionSystemOfLinEquations( lbmatrix.getCoeffMatrix(), lbmatrix.getRHS(), partition.getNodePosition( getProblem().getSource() ) );

				for( Node n : partition.getPartitionArray()[partition.getNodePosition( getProblem().getSource() )] ) {
					thinflowNodelabels.set( n, 0.0 );
				}

				if( x.getRowDimension() == partition.getPartitionArray().length - 1 ) {

					int j = 0;
					for( int i = 0; i < partition.getPartitionArray().length; i++ ) {

						if( i == partition.getNodePosition( getProblem().getSource() ) ) {

							for( Node n : partition.getPartitionArray()[i] ) {
								thinflowNodelabels.set( n, 0.0 );
							}
						} else {

							for( Node n : partition.getPartitionArray()[i] ) {
								thinflowNodelabels.set( n, x.get( j, 0 ) );
							}
							j++;
						}
					}
				} else {
					throw new Exception( "Laenge stimmen nicht ueberein" );
				}

			}

			//compute Alpha
			double alpha = computeAlpha( transitMap, nodelabels, thinflowNodelabels, E_1, E_GminusGtheta );
			System.out.println( "alpha = " + alpha );

			// Update parameters //
			ThetaVector.add( iteration, new Double( theta ) );
			NodelabelVector.add( iteration, new IdentifiableDoubleMapping( nodelabels ) );
			OutFlowValueVector.add( iteration, computeOutFlowValues( spNetwork, thinflowFlowvalues, thinflowNodelabels ) );
			InFlowValueVector.add( iteration, computeInFlowValues( spNetwork, thinflowFlowvalues, thinflowNodelabels ) );

			theta += alpha;
			iteration++;

			if( alpha == Double.MAX_VALUE || alpha == Double.POSITIVE_INFINITY ) {
				break;
			}

			System.out.println( "thnodelabels = " + thinflowNodelabels.toString() );
			//update Nodelabels
			for( Node n : spNetwork.nodes() ) {
				System.out.println( "Node " + n.toString() + " - nlabel =  " + nodelabels.get( n ) + " - tflabel = " + thinflowNodelabels.get( n ) );
				nodelabels.set( n, nodelabels.get( n ) + alpha * thinflowNodelabels.get( n ) );
			}

			//update waiting times for edges in Gtheta
			double waittime;
			E_1.clear();
			for( Edge e : spNetwork.edges() ) {
				start = e.start();
				end = e.end();

				if( start == getProblem().getSource() ) {
					waitingtimes.set( e, 0.0 );
				} else if( (waittime = nodelabels.get( end ) - nodelabels.get( start ) - transitMap.get( e )) > EPSILON ) {
					waitingtimes.set( e, waittime );
					E_1.add( e );

				} else {
					waitingtimes.set( e, 0.0 );
				}

			}
			System.out.println( "neue Iteration " );
		}

		System.out.println( "Nodelabels" + NodelabelVector.toString() );
		System.out.println( "Theta " + ThetaVector.toString() );
		System.out.println( "InFlowvalues" );
		printFlowValueVector( InFlowValueVector );
		System.out.println( "OutFlowvalues" );
		printFlowValueVector( OutFlowValueVector );

	}

	public void runNashFlowWithMultipleSources( NashFlowProblem nashprob ) throws Exception {
		System.out.println( "NASHFLOW WITH MULTIPLE SOURCES" );
		double theta = 0.0;

		Node source = nashprob.getSource();

		DynamicNetwork workingGraph = nashprob.getGraph();

		IdentifiableDoubleMapping<Node> nodelabels = new IdentifiableDoubleMapping<>( workingGraph.nodeCount() );
		IdentifiableDoubleMapping<Edge> waitingtimes = new IdentifiableDoubleMapping<>( workingGraph.edgeCount() );

		/*convert (TransitTimes) DoubleMap to (transitMap) Java map*/
		HashMap<Edge, Double> transitMap = new HashMap<>( workingGraph.edgeCount() );
		for( Edge e : workingGraph.edges() ) {
			waitingtimes.set( e, 0.0 ); //set waiting time of every edge to zero
			transitMap.put( e, nashprob.getEdgeTransitTime( e ) );
		}

		/*shortest path values between source and other nodes*/
		RationalDijkstra dijk = new RationalDijkstra( workingGraph, transitMap, source );
		/*compute distances*/
		dijk.run();
		/*map for distances*/
		Map<Node, Double> distanceMap = new HashMap<>( workingGraph.nodeCount() );
		distanceMap = dijk.getDistances();


		/*set Nodelabels equal to distances*/
		for( Node n : workingGraph.nodes() ) {
			nodelabels.set( n, distanceMap.get( n ) );
		}

		Node start, end;

		ArrayList<Edge> E_1 = new ArrayList( workingGraph.edgeCount() );
		ArrayList<Edge> E_GminusGtheta = new ArrayList( workingGraph.edgeCount() );

		int iteration = 0;

		while( true ) {

			//create shortest path network G_theta
			DynamicNetwork spNetwork = new DynamicNetwork( workingGraph );

			E_GminusGtheta.clear();

			for( Edge e : workingGraph.edges() ) {
				start = e.start();
				end = e.end();

				if( ((nodelabels.get( end ) - nodelabels.get( start ) + EPSILON)) < transitMap.get( e )
								&& waitingtimes.get( e ) <= 0.0 ) {

					spNetwork.removeEdge( e );
					E_GminusGtheta.add( e );
				}
			}

			IdentifiableDoubleMapping spNetworkCapacities = new IdentifiableDoubleMapping( spNetwork.edgeCount() );
			IdentifiableDoubleMapping thinflowCapacities = new IdentifiableDoubleMapping( spNetwork.edgeCount() + E_1.size() );
			for( Edge ed : spNetwork.edges() ) {
				spNetworkCapacities.set( ed, nashprob.getEdgeCapacity( ed ) );
				thinflowCapacities.set( ed, nashprob.getEdgeCapacity( ed ) );
			}


			/*restate thinflow with resetting into thinflow with resetting on empty set*/
			DynamicNetwork thinflowGraph = new DynamicNetwork( spNetwork );

			HashMap<Edge, Node> identMap = new HashMap<Edge, Node>(); /*map to identify new edge with start node*/

			HashMap<Node, ArrayList<Edge>> identEdgeMap = new HashMap<Node, ArrayList<Edge>>();
			HashMap<Edge, Edge> newedgeToOrigedgeMap = new HashMap<Edge, Edge>(); /*map for indentification in thinflow-Algorithm*/

			int num = workingGraph.edgeCount();


			/*create identEdgeMap*/
			for( Edge e : E_1 ) {
				start = e.start();
				identEdgeMap.put( start, new ArrayList<Edge>() );
			}

			for( Edge e : E_1 ) {
				start = e.start();
				end = e.end();
				if( start != source && !nashprob.getOriginalSources().contains( start ) ) {
					Edge ne = new Edge( num++, source, end );

					identMap.put( ne, start );
					identEdgeMap.get( start ).add( ne );
					thinflowGraph.addEdge( ne );
					thinflowCapacities.set( ne, spNetworkCapacities.get( e ) );
					thinflowGraph.removeEdge( e );
					newedgeToOrigedgeMap.put( ne, e );
				}
			}


			/*compute thinflow of value 1*/
			ThinFlowProblem tfprob = new ThinFlowProblem( thinflowGraph, thinflowCapacities, source, nashprob.getSink() );
			ThinFlowAlgo tfalg = new ThinFlowAlgo();

			ThinFlow tf = null;

			boolean fixpointDifference = true;
			IdentifiableDoubleMapping<Edge> thinflowFlowvalues = null;
			IdentifiableDoubleMapping<Node> thinflowNodelabels = null;

			//DoubleMap<Node> nodeDem = new DoubleMap(tf.getProblem().getNodeDemands());
			IdentifiableDoubleMapping<Node> nodeDem = new IdentifiableDoubleMapping( tfprob.getNodeDemands() );

			System.out.println( "enter fixpointdifference in nashflowalgo" );

			while( fixpointDifference ) {
				fixpointDifference = false;
				tfalg.setProblem( tfprob );
				tfalg.run();
				tf = tfalg.getSolution();

				/*get Nodelabels, EdgeFlow values*/
				thinflowFlowvalues = tf.getEdgeFlowValues();
				thinflowNodelabels = tf.getNodeLabels();

				for( Node s : identEdgeMap.keySet() ) {
					if( Math.abs( Math.abs( tf.getProblem().getNodeDemand( s ) ) - NodeOutFlowValue( s, identEdgeMap.get( s ), thinflowFlowvalues ) ) > FIXPOINTACCURACY ) {
						fixpointDifference = true;
					}
				}

				tfprob.setNodeDemand( source, 1.0 );
				tfprob.setNodeDemand( nashprob.getSink(), -1.0 );
				for( Edge e : identMap.keySet() ) {
					start = identMap.get( e );
					tfprob.setNodeDemand( start, 0.0 );
				}
				/*change node demands according to restating of thinflow with resetting into tf without resetting*/

				for( Edge e : identMap.keySet() ) {
					start = identMap.get( e );

					if( start != source ) {

						tfprob.setNodeDemand( start, tf.getProblem().getNodeDemand( start ) - 1 * thinflowFlowvalues.get( e ) );

						tfprob.setNodeDemand( source, tf.getProblem().getNodeDemand( source ) + thinflowFlowvalues.get( e ) );
					}

				}

			}

			FlowDecomposition pathComp = new FlowDecomposition( workingGraph.nodes(), tfalg.getUsedEdges(), newedgeToOrigedgeMap, tf.getEdgeFlowValues(), getProblem().getSource(), getProblem().getSink(), 1.0 );
			pathComp.computePathAndFlowSizes();


			/* compute used paths */
			List<List<Edge>> usedpaths = pathComp.getPaths();
			computeUsedEdgesPathsMaps( workingGraph.edges(), usedpaths );

			addUsedPaths( usedpaths );
			addPathFlowSizeMap( pathComp.getPathFlowSizes() );

			getProblem().setNodeLabelDerivations( thinflowNodelabels );
			thinflowNodelabels = getProblem().getNodeLabelDerivations();

			if( !E_1.isEmpty() ) {

				NodePartition partition = new NodePartition( spNetwork.nodes(), thinflowNodelabels );

             //partition.printArray();
				LabelMatrix lbmatrix = new LabelMatrix( partition, spNetwork.edges(), E_1, spNetworkCapacities, nodeDem );
				lbmatrix.computeCoefficientMatrix();
				Matrix x = computeSolutionSystemOfLinEquations( lbmatrix.getCoeffMatrix(), lbmatrix.getRHS(), partition.getNodePosition( getProblem().getSource() ) );

				for( Node n : partition.getPartitionArray()[partition.getNodePosition( getProblem().getSource() )] ) {
					thinflowNodelabels.set( n, 0.0 );
				}

				if( x.getRowDimension() == partition.getPartitionArray().length - 1 ) {

					int j = 0;
					for( int i = 0; i < partition.getPartitionArray().length; i++ ) {

						if( i == partition.getNodePosition( getProblem().getSource() ) ) {

							for( Node n : partition.getPartitionArray()[i] ) {
								thinflowNodelabels.set( n, 0.0 );
							}
						} else {

							for( Node n : partition.getPartitionArray()[i] ) {
								thinflowNodelabels.set( n, x.get( j, 0 ) );
							}
							j++;
						}
					}
				} else {
					throw new Exception( "Laenge stimmen nicht ueberein" );
				}

			}

			//compute Alpha
			double alpha = computeAlpha( transitMap, nodelabels, thinflowNodelabels, E_1, E_GminusGtheta );

			// Update parameters //
			ThetaVector.add( iteration, new Double( theta ) );
			NodelabelVector.add( iteration, new IdentifiableDoubleMapping( nodelabels ) );
			OutFlowValueVector.add( iteration, computeOutFlowValues( spNetwork, thinflowFlowvalues, thinflowNodelabels ) );
			InFlowValueVector.add( iteration, computeInFlowValues( spNetwork, thinflowFlowvalues, thinflowNodelabels ) );

			theta += alpha;
			iteration++;

          //Test if demand of originial sources is fulfilled
			if( alpha == Double.MAX_VALUE || alpha == Double.POSITIVE_INFINITY ) {
				break;
			}

			//update Nodelabels
			for( Node n : spNetwork.nodes() ) {
				nodelabels.set( n, nodelabels.get( n ) + alpha * thinflowNodelabels.get( n ) );
			}

			//update waiting times for edges in Gtheta
			double waittime;
			E_1.clear();
			for( Edge e : spNetwork.edges() ) {
				start = e.start();
				end = e.end();

				if( start == getProblem().getSource() ) {
					waitingtimes.set( e, 0.0 );
				} else if( (waittime = nodelabels.get( end ) - nodelabels.get( start ) - transitMap.get( e )) > EPSILON ) {
					waitingtimes.set( e, waittime );
					E_1.add( e );

				} else {
					waitingtimes.set( e, 0.0 );
				}

			}

		}

		System.out.println( "Nodelabels" + NodelabelVector.toString() );
		System.out.println( "Theta " + ThetaVector.toString() );
		System.out.println( "InFlowvalues" );
		printFlowValueVector( InFlowValueVector );
		System.out.println( "OutFlowvalues" );
		printFlowValueVector( OutFlowValueVector );

	}

	private void adjustSourceDemands( HashMap<Node, Double> sourcedem, Double alpha, double[] inflowvalues, List<Edge> edgesToOrigSources ) {
		for( Edge edge : edgesToOrigSources ) {
			Node origsource = edge.end();
			sourcedem.put( origsource, sourcedem.get( origsource ) - alpha * inflowvalues[edge.id()] );
		}
	}

	private boolean testIfSourceDemandIsFulfilled( Node origsource, HashMap<Node, Double> sourcedem ) {
		Double epsilon = 0.0001;

		if( sourcedem.get( origsource ) < epsilon ) {
			return true;
		} else {
			return false;
		}
	}

	private double[] computeInFlowValues( DynamicNetwork spNetwork, IdentifiableDoubleMapping<Edge> thinflowValues, IdentifiableDoubleMapping<Node> thinflowNodelabels ) {
		double[] inflows = new double[getProblem().getGraph().edgeCount()];
		Node start;
		for( Edge e : spNetwork.edges() ) {
			start = e.start();
			if( start == getProblem().getSource() ) {
				continue;
			}
			inflows[e.id()] = thinflowValues.get( e ) / thinflowNodelabels.get( start );

		}

		return inflows;
	}

	private double[] computeOutFlowValues( DynamicNetwork spNetwork, IdentifiableDoubleMapping<Edge> thinflowValues, IdentifiableDoubleMapping<Node> thinflowNodelabels ) {
		double[] outflows = new double[getProblem().getGraph().edgeCount()];
		Node end;
		for( Edge e : spNetwork.edges() ) {
			end = e.end();
			if( thinflowNodelabels.get( end ) > EPSILON ) {
				outflows[e.id()] = thinflowValues.get( e ) / thinflowNodelabels.get( end );
			} else {
				outflows[e.id()] = 0.0;
			}
		}
		return outflows;
	}

	private void printFlowValueVector( Vector< double[]> flowVector ) {
		for( int i = 0; i < flowVector.size(); i++ ) {
			System.out.print( "i = " + i + "(" );
			double[] array = flowVector.get( i );
			for( int j = 0; j < array.length; j++ ) {
				System.out.print( " " + array[j] );
			}
			System.out.println( ")" );
		}
	}

	private double NodeOutFlowValue( Node n, ArrayList<Edge> edgelist, IdentifiableDoubleMapping<Edge> flowvalues ) {
		double flow = 0.0;
		for( Edge e : edgelist ) {

			flow += flowvalues.get( e );
		}
		return flow;
	}

}
