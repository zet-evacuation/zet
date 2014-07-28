/**
 * GridConverterPlugin.java
 * Created: 03.04.2014, 17:11:37
 */
package batch.plugins.impl.converter;

import batch.plugins.AlgorithmicPlugin;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.graph.GridGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.coga.zet.model.BuildingPlan;
import net.xeoh.plugins.base.annotations.PluginImplementation;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class GridConverterPlugin implements AlgorithmicPlugin<BuildingPlan, NetworkFlowModel> {

	@Override
	public String getName() {
		return "Default ZET Grid Converter";
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
		return new GridGraphConverter();
	}
}