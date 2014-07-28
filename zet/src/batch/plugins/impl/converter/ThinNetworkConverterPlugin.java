/**
 * ThinNetworkConverterPlugin.java
 * Created: 03.04.2014, 17:11:46
 */
package batch.plugins.impl.converter;

import batch.plugins.AlgorithmicPlugin;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.ThinNetworkConverter;
import de.tu_berlin.coga.zet.model.BuildingPlan;
import net.xeoh.plugins.base.annotations.PluginImplementation;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class ThinNetworkConverterPlugin implements AlgorithmicPlugin<BuildingPlan, NetworkFlowModel> {

	@Override
	public String getName() {
		return "ZET Thin Network Converter";
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
		return new ThinNetworkConverter();
	}
}