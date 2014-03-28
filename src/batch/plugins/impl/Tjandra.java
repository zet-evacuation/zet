/**
 * Tjandra.java
 * Created: 27.03.2014, 15:02:45
 */
package batch.plugins.impl;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
import batch.plugins.AlgorithmicPlugin;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.common.util.Formatter;
import de.tu_berlin.math.coga.common.util.units.TimeUnits;
import ds.graph.flow.PathBasedFlowOverTime;
import net.xeoh.plugins.base.annotations.PluginImplementation;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class Tjandra implements AlgorithmicPlugin<EarliestArrivalFlowProblem, PathBasedFlowOverTime> {

	@Override
	public String getName() {
		return "Tjandra";
	}

	@Override
	public Class<EarliestArrivalFlowProblem> accepts() {
		return EarliestArrivalFlowProblem.class;
	}

	@Override
	public Class<PathBasedFlowOverTime> generates() {
		return PathBasedFlowOverTime.class;
	}

	@Override
	public Algorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime> getAlgorithm() {
		Algorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime> algo = new Algorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime>() {

			@Override
			protected PathBasedFlowOverTime runAlgorithm( EarliestArrivalFlowProblem problem ) {
				// TODO this is copied from the transformation Task
			SEAAPAlgorithm algo = new SEAAPAlgorithm();
			algo.setProblem( problem );
			algo.run();

			PathBasedFlowOverTime df = algo.getSolution().getPathBased();
			String result = String.format( "Sent %1$s of %2$s flow units in %3$s time units successfully.", algo.getSolution().getFlowAmount(), getAlgorithm().getProblem().getTotalSupplies(), algo.getSolution().getTimeHorizon() );
			System.out.println( result );
			System.out.println( "Total cost: " + algo.getSolution().getTotalCost() );
			//AlgorithmTask.getInstance().publish(100, result, "");
			System.out.println( "Sending the flow units required " + Formatter.formatUnit( getAlgorithm().getRuntime(), TimeUnits.MilliSeconds ) );
			return df;
			}
		};
		return algo;
	}
}

