/**
 * GraphShrinker.java
 * Created: 02.08.2012, 15:50:11
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.z.BuildingPlan;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphShrinker extends Algorithm<BuildingPlan, NetworkFlowModel> {
	private final Algorithm<BuildingPlan, NetworkFlowModel> converter;
	private final Algorithm<NetworkFlowModel, NetworkFlowModel> shrinker;

	public GraphShrinker( Algorithm<BuildingPlan,NetworkFlowModel> converter, Algorithm<NetworkFlowModel,NetworkFlowModel> shrinker ) {
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
