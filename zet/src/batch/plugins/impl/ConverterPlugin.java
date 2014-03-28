/**
 * ConverterPlugin.java
 * Created: 28.03.2014, 17:26:39
 */
package batch.plugins.impl;

import batch.plugins.AlgorithmicPlugin;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import ds.z.Project;
import net.xeoh.plugins.base.annotations.PluginImplementation;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class ConverterPlugin implements AlgorithmicPlugin<Project, NetworkFlowModel> {

	@Override
	public String getName() {
		return "Default ZET converter";
	}

	@Override
	public Class<Project> accepts() {
		return Project.class;
	}

	@Override
	public Class<NetworkFlowModel> generates() {
		return NetworkFlowModel.class;
	}

	@Override
	public Algorithm<Project, NetworkFlowModel> getAlgorithm() {

		return null;
	}

}
