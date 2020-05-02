package batch.plugins.impl.converter;

import batch.plugins.AlgorithmPlugin;
import org.zetool.common.algorithm.AbstractAlgorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.RectangleConverter;
import de.zet_evakuierung.model.BuildingPlan;
import net.xeoh.plugins.base.annotations.PluginImplementation;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class RectangleConverterPlugin implements AlgorithmPlugin<BuildingPlan, NetworkFlowModel> {

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
	public AbstractAlgorithm<BuildingPlan, NetworkFlowModel> getAlgorithm() {
		return new RectangleConverter();
	}

  @Override
  public String toString() {
    return getName();
  }
}