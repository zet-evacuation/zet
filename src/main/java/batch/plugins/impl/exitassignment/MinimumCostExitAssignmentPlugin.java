/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package batch.plugins.impl.exitassignment;

import algo.graph.exitassignment.ExitAssignment;
import algo.graph.exitassignment.MinimumCostTransshipmentExitAssignment;
import algo.graph.exitassignment.ShortestPathExitAssignment;
import batch.plugins.AlgorithmPlugin;
import org.zetool.common.algorithm.AbstractAlgorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class MinimumCostExitAssignmentPlugin extends ShortestPathExitAssignment implements AlgorithmPlugin<NetworkFlowModel, ExitAssignment> {

	@Override
	public String getName() {
		return "Shortest Paths based Exit Assignment";
	}

	@Override
	public Class<NetworkFlowModel> accepts() {
		return NetworkFlowModel.class;
	}

	@Override
	public Class<ExitAssignment> generates() {
		return ExitAssignment.class;
	}

	@Override
	public AbstractAlgorithm<NetworkFlowModel, ExitAssignment> getAlgorithm() {    
    return new MinimumCostTransshipmentExitAssignment();
	}

  @Override
  public String toString() {
    return getName();
  }
}
