/**
 * ConverterPlugin.java
 * Created: 28.03.2014, 17:26:39
 */
package batch.plugins.impl;

import batch.plugins.AlgorithmicPlugin;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.RectangleConverter;
import ds.z.BuildingPlan;
import net.xeoh.plugins.base.annotations.PluginImplementation;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class RectangleConverterPlugin implements AlgorithmicPlugin<BuildingPlan, NetworkFlowModel> {

	@Override
	public String getName() {
		return "Default ZET Rectangle Converter";
	}

	@Override
	public Class<BuildingPlan> accepts() {
		return BuildingPlan.class;
	}

	@Override
	public Class<NetworkFlowModel> generates() {
		return NetworkFlowModel.class;
	}

	@Override
	public Algorithm<BuildingPlan, NetworkFlowModel> getAlgorithm() {
		return new RectangleConverter();
	}
}