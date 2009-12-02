/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
package algo.graph.dynamicflow;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ds.graph.Localization;

import algo.graph.Flags;

//import batch.tasks.AlgorithmTask;
import algo.graph.util.GraphInstanceChecker;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import algo.graph.Flags;

/**
 * The class <code>Transshipment</code> implements a binary search to compute the minimal
 * time horizon needed to get a transshipment with given properties. The wished transshipment
 * is defined and calculated by the generic class <code>TT</code> that has to inherit the
 * class <code>TransshipmentWithTimeHorizon</code>. 
 * @param <TT> The type of the algorithm used to compute the transshipments for each time step.
 */
public abstract class Transshipment<TT extends TransshipmentWithTimeHorizon> extends DynamicFlowAlgorithm{
	
	/** The supplies used by the algorithm. */ 
	IdentifiableIntegerMapping<Node> supplies;
	/** Class type of the specific transshipment algorithm. */
	Class<? extends TransshipmentWithTimeHorizon> standardTHTAlgorithm, additionalTHTAlgorithm;
	/** Node capacities */
	IdentifiableIntegerMapping<Node> nodeCapacities;
	
	public Transshipment(Network network,
			IdentifiableIntegerMapping<Edge> transitTimes,
			IdentifiableIntegerMapping<Edge> edgeCapacities,
			IdentifiableIntegerMapping<Node> nodeCapacities,
			IdentifiableIntegerMapping<Node> supplies,
			Class<? extends TransshipmentWithTimeHorizon> standardTHTAlgorithm,
			Class<? extends TransshipmentWithTimeHorizon> additionalTHTAlgorithm) {
		super(network, transitTimes, edgeCapacities);
		this.supplies = supplies;
		this.standardTHTAlgorithm = standardTHTAlgorithm;
		this.additionalTHTAlgorithm = additionalTHTAlgorithm;
		this.nodeCapacities = nodeCapacities;
	}
	
	/**
	 * Private method that calculates the result of the specific transshipment
	 * algorithm by creating a new instance of it, catching exceptions and 
	 * running it. 
	 * @param network Network to use.
	 * @param transitTimes Transit times for all edges in the network.
	 * @param edgeCapacities Edge capacities of all edges in the network.
	 * @param supplies Supplies for all nodes in the network.
	 * @param timeHorizon Time horizon that shall be tested.
	 * @return The result of the specific transshipment algorithm on the given input.
	 */
	protected PathBasedFlowOverTime useTransshipmentAlgorithm(Network network,
			IdentifiableIntegerMapping<Edge> transitTimes,
			IdentifiableIntegerMapping<Edge> edgeCapacities,
			IdentifiableIntegerMapping<Node> nodeCapacities,
			IdentifiableIntegerMapping<Node> supplies, int timeHorizon,
			Class<? extends TransshipmentWithTimeHorizon> algoClass){
		TransshipmentWithTimeHorizon transshipmentAlgorithm;
		try {
			Constructor<? extends TransshipmentWithTimeHorizon> constructor = algoClass.getConstructor(network.getClass(), transitTimes.getClass(), edgeCapacities.getClass(), IdentifiableIntegerMapping.class, supplies.getClass(), Integer.class);
			transshipmentAlgorithm = constructor.newInstance(network, transitTimes, edgeCapacities, nodeCapacities, supplies, timeHorizon);
		} catch (NoSuchMethodException e){
			throw new AssertionError("NoSuchMethod Exception: "+e.getMessage());
		} catch (IllegalAccessException i){
			throw new AssertionError("IllegalAccess Exception: "+i.getMessage());
		} catch (InvocationTargetException i){
			throw new AssertionError("InvocationTarget Exception: "+i.getMessage());
		} catch (InstantiationException i){
			throw new AssertionError("Instantiation Exception: "+i.getMessage());
		}
		transshipmentAlgorithm.run();
		if (!transshipmentAlgorithm.isProblemSolved()){
			throw new AssertionError (Localization.getInstance (
					).getString ("algo.graph.dynamicflow.RunNotCalledException"));
		}
		if (!transshipmentAlgorithm.isPathBasedFlowAvailable()){
			throw new AssertionError(Localization.getInstance (
			).getString ("algo.graph.dynamicflow.NoPathBasedFlowException"));
		}
		return transshipmentAlgorithm.getResultFlowPathBased();
	}

        @Override
        protected PathBasedFlowOverTime runAlgorithm(DynamicFlowProblem problem) {
            //network = problem.getNetwork();
            //edgeCapacities = problem.getCapacities();
            //transitTimes = problem.getTransitTimes();
            runAlgorithm();
            return resultFlowPathBased;
        }

	/**
	 * This method performs binary search to find a minimal time horizon.
	 * For each time step, the time-expanded network is created and the algorithm
	 * defined by the object <code>transshipmentAlgorithm</code> is used to check
	 * whether the time horizon is sufficient.
	 * @param network The network.
	 * @param transitTimes The transit times for all edges in the network.
	 * @param edgeCapacities The capacities of all edges in the network.
	 * @param supplies Supplies and demands of all nodes in the network.
	 * @param transshipmentAlgorithm The algorithm used for each time step.
	 * @return
	 */
	public void runAlgorithm() {
		if (Flags.ALGO_PROGRESS){
			System.out.println("Progress: Transshipment algorithm was started.");
			System.out.flush();
		}
                fireEvent("Transshipment algorithm started.");
		//AlgorithmTask.getInstance().publish( "Transshipment algorithm started.", "" );
		
		if (Flags.MEL){
			System.out.println("Eingabe: ");
			System.out.println("Network: "+network);
			System.out.println("Edge capacities:" +edgeCapacities);
			System.out.println("Supplies: "+supplies);
		}
		
		if (GraphInstanceChecker.emptySupplies(network, supplies)){
			if (Flags.MEL)
				System.out.println("No individuals - no flow.");
			resultFlowPathBased = new PathBasedFlowOverTime();
			return;
		}
		
		/* Calculate an upper bound for the time horizon. */
		int upperBound;		
		upperBound = TransshipmentBoundEstimator.calculateBound(network, transitTimes, edgeCapacities, supplies);
		
		/* Short debug output telling the computed upper bound. */
		if (Flags.ALGO_PROGRESS){
			System.out.println("Progress: The upper bound for the time horizon was calculated.");
		}
		if (Flags.TRANSSHIPMENT_SHORT){
			System.out.println("Upper bound for time horizon: " + (upperBound-1) );
		}
		
		/* Initialization */
		int left=1, right=upperBound;		
		PathBasedFlowOverTime transshipmentWithoutTimeHorizon = null;
		
		/* Do geometric search: */
		
		if (Flags.ALGO_PROGRESS){
			System.out.println("Progress: Now testing time horizon 1.");
		}
		//AlgorithmTask.getInstance().publish( "Uppder bound for the time horizon was calculated.", "Now testing time horizon 1." );
                fireEvent("Upper bound for the time horizon was calculated. Now testing time horizon 1.");
		/* Use the specific transshipment algorithm to check whether testTimeHorizon is sufficient. */
		PathBasedFlowOverTime dynamicTransshipment = useTransshipmentAlgorithm(network, transitTimes, edgeCapacities, nodeCapacities, supplies, 1, standardTHTAlgorithm);
		
		boolean found = false;
		int nonFeasibleT = 0;
		int feasibleT = -1;

 		if (dynamicTransshipment == null)
			nonFeasibleT = 1;
 		else {
 			nonFeasibleT = 0;
 			feasibleT = 1;
 			found = true;
 		}
 		
		while (!found) {
			int testTimeHorizon = (nonFeasibleT*2);
			if (testTimeHorizon >= upperBound){
				feasibleT = upperBound;
				found = true;
			} else {
				if (Flags.ALGO_PROGRESS){
					System.out.println("Progress: Now testing time horizon "+testTimeHorizon+".");
				}
				//AlgorithmTask.getInstance().publish( "Now testing time horizon " + testTimeHorizon + ".", "" );
                                fireEvent("Now testing time horizon " + testTimeHorizon + ".");
                                System.out.println(System.currentTimeMillis() + " ms");
				dynamicTransshipment = useTransshipmentAlgorithm(network, transitTimes, edgeCapacities, nodeCapacities, supplies, testTimeHorizon, standardTHTAlgorithm);
				if (dynamicTransshipment == null)
					nonFeasibleT = testTimeHorizon;
				else {
					feasibleT = testTimeHorizon;
					found = true;
				}
			}
		}
		
		left = nonFeasibleT;
		right = Math.min(feasibleT+1, upperBound);
		
		/* Do binary search: */
		do {
			
			/* Compute the middle of the search intervall. */
			int testTimeHorizon = (left + right) / 2;
			
			if (Flags.ALGO_PROGRESS){
				System.out.println("Progress: Now testing time horizon "+testTimeHorizon+".");
			}
			//AlgorithmTask.getInstance().publish( "Now testing time horizon " + testTimeHorizon + ".", "" );
                        fireEvent("Now testing time horizon " + testTimeHorizon + ".");
			
			/* Use the specific transshipment algorithm to check whether testTimeHorizon is sufficient. */
			dynamicTransshipment = useTransshipmentAlgorithm(network, transitTimes, edgeCapacities, nodeCapacities, supplies, testTimeHorizon, standardTHTAlgorithm);
	        
			/* If the time horizon is sufficient, adjust left border, else adjust right border of the intervall.*/
			if (dynamicTransshipment == null)
	        	left = testTimeHorizon;
	        else {
	        	right = testTimeHorizon;
	        	transshipmentWithoutTimeHorizon = dynamicTransshipment;
	        }

			
		} while (left < right - 1); /* Stop if the borders reach each other. */

		/* If a transshipment was found print the result. */
		if (left == right - 1 && transshipmentWithoutTimeHorizon != null) {
			if (Flags.ALGO_PROGRESS){
				System.out.println("Progress: Transshipment algorithm has finished. Time horizon: "+right);
			}
			//AlgorithmTask.getInstance().publish("Solution found.", "The optimal time horizon is: " + right +" (estimated upper bound: "+(upperBound-1)+")");
                        fireEvent("Solution found. The optimal time horizon is: " + right +" (estimated upper bound: "+(upperBound-1)+")");
			if (Flags.TRANSSHIPMENT_SHORT) {
				System.out.println("The optimal time horizon is: " + right +" (estimated upper bound: "+(upperBound-1)+")");
			}
			if (Flags.TRANSSHIPMENT_LONG){
				System.out.println("A transshipment with time horizon ("+(upperBound-1)+")"+ + right + ": ");
				System.out.println(transshipmentWithoutTimeHorizon);
			}
			if (Flags.TRANSSHIPMENT_RESULT_FLOW  ){
				System.out.println(transshipmentWithoutTimeHorizon);
			}
		} else {
			//AlgorithmTask.getInstance().publish("No solution found.","");
                        fireEvent("No solution found.");
			if (Flags.TRANSSHIPMENT_SHORT){
				System.out.println("No solution found.");
			}
			if (Flags.ALGO_PROGRESS){
				System.out.println("Progress: Transshipment algorithm has finished. No solution.");
			}
			throw new AssertionError("No solution found. Upper bound wrong?");
		}		
		
		/* if an additional algorithm was set, it is applied for the optimal time horizon. 
		 * The new flow is than the result flow. */
		if (left == right-1 && transshipmentWithoutTimeHorizon != null){
			if (additionalTHTAlgorithm != null && additionalTHTAlgorithm != standardTHTAlgorithm){
				transshipmentWithoutTimeHorizon = useTransshipmentAlgorithm(network, transitTimes, edgeCapacities, nodeCapacities, supplies, right, additionalTHTAlgorithm);
				if (Flags.TRANSSHIPMENT_SHORT){
					System.out.println("Additional run with additional transshipment algorithm has finished.");
				}
				if (Flags.ALGO_PROGRESS){
					System.out.println("Progress: Additional transshipment algorithm has finished and the new solution was set.");
				}
				//AlgorithmTask.getInstance().publish( 100, "Run with additional transshipment algorithm has finished.", "The new solution was set." );
                                fireProgressEvent( 100, "Run with additional transshipment algorithm has finished. The new solution was set." );
			}
		}
		
		/* May be null if upperBound was not sufficient. This should not happen! 
		 * Else the optimal transshipment is saved. */ 
		resultFlowPathBased = transshipmentWithoutTimeHorizon;
	}	
}
