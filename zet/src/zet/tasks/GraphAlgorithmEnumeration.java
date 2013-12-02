/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package zet.tasks;

import algo.graph.dynamicflow.DynamicTransshipmentProblem;
import algo.graph.dynamicflow.QuickestTransshipment;
import algo.graph.dynamicflow.eat.EATransshipmentMinCost;
import algo.graph.dynamicflow.eat.EATransshipmentSSSP;
import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.SuccessiveEarliestArrivalAugmentingPathAlgorithmNoTH;
import algo.graph.dynamicflow.maxflow.MaxFlowOverTime;
import algo.graph.dynamicflow.maxflow.TimeExpandedMaximumFlowOverTime;
import batch.tasks.graph.SuccessiveEarliestArrivalAugmentingPathAlgorithm2Task;
import batch.tasks.graph.SuccessiveEarliestArrivalAugmentingPathAlgorithmCompareTask;
import batch.tasks.graph.SuccessiveEarliestArrivalAugmentingPathOptimizedTask;
import de.tu_berlin.math.coga.algorithm.flowovertime.maxflow.MaximumFlowOverTimeProblem;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.ZETLocalization;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import ds.NetworkFlowModelAlgorithm;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.network.AbstractNetwork;

/**
 * Enumerates the types of graph algorithms and assigns each of them a way
 * to get the associated NetworkFlowModelAlgorithm and a name;
 *
 * @author Timon
 */
public enum GraphAlgorithmEnumeration {
	/**
	 * Calls the {@link EATransshipmentSSSP} algorithm to solve the problem.
	 */
	EarliestArrivalTransshipmentSuccessiveShortestPaths( ZETLocalization.getSingleton().getString( "gui.EATransshipmentSSSP" ) ) {
		@Override
		public NetworkFlowModelAlgorithm createTask( NetworkFlowModel model, final int timeHorizon ) {
			NetworkFlowModelAlgorithm nfma = new NetworkFlowModelAlgorithm() {
				@Override
				protected PathBasedFlowOverTime runAlgorithm( NetworkFlowModel model ) {
					EATransshipmentSSSP algo = new EATransshipmentSSSP();
					
					DynamicTransshipmentProblem dtp = new DynamicTransshipmentProblem( model.edgeCapacities(), (AbstractNetwork)model.graph(), null, timeHorizon, model.transitTimes(), model.currentAssignment() );
					algo.setProblem( dtp );
					algo.run();
					if( !algo.isProblemSolved() || !algo.isPathBasedFlowAvailable() )
						throw new AssertionError( "Either algorithm has not run or path based flow is not available." );
					return algo.getResultFlowPathBased();
				}
			};
			return nfma;
		}
	},
	/**
	 * Calls the {@link EATransshipmentMinCost} algorithm to solve this.
	 */
	EarliestArrivalTransshipmentMinCost( ZETLocalization.getSingleton().getString( "gui.EATransshipmentMinCost" ) ) {
		@Override
		public NetworkFlowModelAlgorithm createTask( NetworkFlowModel model, final int timeHorizon ) {
			NetworkFlowModelAlgorithm nfma = new NetworkFlowModelAlgorithm() {
				@Override
				protected PathBasedFlowOverTime runAlgorithm( NetworkFlowModel model ) {
					EATransshipmentMinCost algo = new EATransshipmentMinCost();
					DynamicTransshipmentProblem dtp = new DynamicTransshipmentProblem( model.edgeCapacities(), (AbstractNetwork)model.graph(), null, timeHorizon, model.transitTimes(), model.currentAssignment() );
					//DynamicTransshipmentProblem dtp = new DynamicTransshipmentProblem( model.getEdgeCapacities(), model.getNetwork(), null, timeHorizon, model.getTransitTimes(), model.getCurrentAssignment() );
					algo.setProblem( dtp );
					algo.run();
					if( !algo.isProblemSolved() || !algo.isPathBasedFlowAvailable() )
						throw new AssertionError( "Either algorithm has not run or path based flow is not available." );
					return algo.getResultFlowPathBased();
				}
			};
			return nfma;
		}
	},
	/**
	 * Calls the {@link SuccessiveEarliestArrivalAugmentingPathAlgorithmNoTH} algorithm to solve
	 * an earliest arrival s-t-problem using binary search.
	 */
	SuccessiveEarliestArrivalAugmentingPathBinarySearch( ZETLocalization.getSingleton().getString( "gui.SuccEAAugPathBS" ) ) {
		@Override
		public NetworkFlowModelAlgorithm createTask( NetworkFlowModel model, final int timeHorizon ) {
			return new NetworkFlowModelAlgorithm() {
				@Override
				protected PathBasedFlowOverTime runAlgorithm( NetworkFlowModel model ) {
					SuccessiveEarliestArrivalAugmentingPathAlgorithmNoTH algo = new SuccessiveEarliestArrivalAugmentingPathAlgorithmNoTH();
					//DynamicTransshipmentProblem dtp = new DynamicTransshipmentProblem( model.edgeCapacities(), (AbstractNetwork)model.graph(), null, timeHorizon, model.transitTimes(), model.currentAssignment() );
					//DynamicTransshipmentProblem dtp = new DynamicTransshipmentProblem( model.getEdgeCapacities(), model.getNetwork(), null, timeHorizon, model.getTransitTimes(), model.getCurrentAssignment() );
					EarliestArrivalFlowProblem eafp;
					eafp = model.getEAFP( timeHorizon );
					algo.setProblem( eafp );
					algo.run();
					if( !algo.isProblemSolved() || !algo.isPathBasedFlowAvailable() )
						throw new AssertionError( "Either algorithm has not run or path based flow is not available." );
					return algo.getResultFlowPathBased();
				}
			};
		}
	},
	SuccessiveEarliestArrivalAugmentingPath( ZETLocalization.getSingleton().getString( "gui.SuccEAAugPath" ) ) {
		@Override
		public NetworkFlowModelAlgorithm createTask( NetworkFlowModel model, int timeHorizon ) {
			return new SuccessiveEarliestArrivalAugmentingPathAlgorithm2Task();
		}
	},
	MaxFlowOverTimeMinCost( ZETLocalization.getSingleton().getString( "gui.MaxFlowMinCost" ) ) {
		@Override
		public NetworkFlowModelAlgorithm createTask( NetworkFlowModel model, final int timeHorizon ) {
			return new NetworkFlowModelAlgorithm() {
				@Override
				protected PathBasedFlowOverTime runAlgorithm( NetworkFlowModel model ) {
					MaxFlowOverTime maxFlowOverTimeAlgo = new MaxFlowOverTime();
					maxFlowOverTimeAlgo.setProblem( new MaximumFlowOverTimeProblem( (AbstractNetwork)model.graph(), model.edgeCapacities(), model.transitTimes(), model.getSinks(), model.getSources(), timeHorizon ) );
					maxFlowOverTimeAlgo.run();
					return maxFlowOverTimeAlgo.getSolution();
				}
			};
		}
	},
	/**
	 * Calls the {@link TimeExpandedMaximumFlowOverTime} algorithm.
	 */
	MaxFlowOverTimeTimeExpanded( ZETLocalization.getSingleton().getString( "gui.MaxFlowTimeExtended" ) ) {
		@Override
		public NetworkFlowModelAlgorithm createTask( NetworkFlowModel model, final int timeHorizon ) {
			return new NetworkFlowModelAlgorithm() {
				@Override
				protected PathBasedFlowOverTime runAlgorithm( NetworkFlowModel model ) {
					TimeExpandedMaximumFlowOverTime maxFlowOverTimeAlgo = new TimeExpandedMaximumFlowOverTime();
					maxFlowOverTimeAlgo.setProblem( new MaximumFlowOverTimeProblem( (AbstractNetwork)model.graph(), model.edgeCapacities(), model.transitTimes(), model.getSinks(), model.getSources(), timeHorizon ) );
					//maxFlowOverTimeAlgo.setProblem( new MaximumFlowOverTimeProblem( model.getNetwork(), model.getEdgeCapacities(), model.getTransitTimes(), model.getSources(), model.getSinks(), timeHorizon ) );
					maxFlowOverTimeAlgo.run();
					return maxFlowOverTimeAlgo.getSolution();
				}
			};
		}
	},
	/**
	 * Calls the {@link QuickestTransshipment} algorithm to compute a quickest transshipment.
	 */
	QuickestTransshipment( ZETLocalization.getSingleton().getString( "gui.QuickestTransshipment" ) ) {
		public NetworkFlowModelAlgorithm createTask( NetworkFlowModel model, final int timeHorizon ) {
			return new NetworkFlowModelAlgorithm() {
				@Override
				protected PathBasedFlowOverTime runAlgorithm( NetworkFlowModel model ) {
					QuickestTransshipment algo = new QuickestTransshipment();
					DynamicTransshipmentProblem dtp = new DynamicTransshipmentProblem( model.edgeCapacities(), (AbstractNetwork)model.graph(), null, timeHorizon, model.transitTimes(), model.currentAssignment() );
					//DynamicTransshipmentProblem dtp = new DynamicTransshipmentProblem( model.getEdgeCapacities(), model.getNetwork(), null, timeHorizon, model.getTransitTimes(), model.getCurrentAssignment() );
					algo.setProblem( dtp );
					algo.run();
					if( !algo.isProblemSolved() || !algo.isPathBasedFlowAvailable() )
						throw new AssertionError( "Either algorithm has not run or path based flow is not available." );
					return algo.getResultFlowPathBased();
				}
			};
		}
	},
	SuccessiveEarliestArrivalAugmentingPathOptimized( ZETLocalization.getSingleton().getString( "gui.SEAAP" ) ) {
		@Override
		public Algorithm<NetworkFlowModel, PathBasedFlowOverTime> createTask( NetworkFlowModel model, int timeHorizon ) {
			return new SuccessiveEarliestArrivalAugmentingPathOptimizedTask();
		}
	},
	SuccessiveEarliestArrivalAugmentingPathOptimizedCompare( ZETLocalization.getSingleton().getString( "gui.SEAAP" ) ) {
		@Override
		public Algorithm<NetworkFlowModel, PathBasedFlowOverTime> createTask( NetworkFlowModel model, int timeHorizon ) {
			return new SuccessiveEarliestArrivalAugmentingPathAlgorithmCompareTask( timeHorizon );
		}
	};
	private String name;

	private GraphAlgorithmEnumeration( String name ) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public abstract Algorithm<NetworkFlowModel, PathBasedFlowOverTime> createTask( NetworkFlowModel model, int timeHorizon );
}
