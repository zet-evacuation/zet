/**
 * GraphConverterAndShrinker.java
 * Created: 02.08.2012, 15:50:11
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import org.zetool.common.algorithm.AbstractAlgorithm;
import de.zet_evakuierung.model.BuildingPlan;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphConverterAndShrinker extends AbstractAlgorithm<BuildingPlan, NetworkFlowModel> {
	private final AbstractAlgorithm<BuildingPlan, NetworkFlowModel> converter;
	private final AbstractAlgorithm<NetworkFlowModel, NetworkFlowModel> shrinker;

	public GraphConverterAndShrinker( AbstractAlgorithm<BuildingPlan,NetworkFlowModel> converter, AbstractAlgorithm<NetworkFlowModel,NetworkFlowModel> shrinker ) {
		this.converter = converter;
		this.shrinker = shrinker;
	}

	@Override
	protected NetworkFlowModel runAlgorithm( BuildingPlan problem ) {
		converter.setProblem( problem );
		converter.run();
		shrinker.setProblem( converter.getSolution() );
		shrinker.run();
		return shrinker.getSolution();
	}
}
