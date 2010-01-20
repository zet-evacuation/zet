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
/*
 * QuickestTransshipment.java
 * Created on 23.01.2008, 23:27:12
 */

package batch.tasks.graph;

import ds.NetworkFlowModelAlgorithm;
import de.tu_berlin.math.coga.common.algorithm.Transformation;
import batch.tasks.*;
import algo.graph.dynamicflow.QuickestTransshipment;
import ds.NetworkFlowModel;
import ds.graph.flow.PathBasedFlowOverTime;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class QuickestTransshipmentTask extends NetworkFlowModelAlgorithm {
					

	@Override
	protected PathBasedFlowOverTime runAlgorithm(NetworkFlowModel model) {
		QuickestTransshipment algo = new QuickestTransshipment( model.getNetwork(), model.getTransitTimes(), model.getEdgeCapacities(), model.getCurrentAssignment() );
		algo.run();
		if (!algo.isProblemSolved() || !algo.isPathBasedFlowAvailable()){
			throw new AssertionError("Either algorithm has not run or path based flow is not available.");
		}
		          return  algo.getResultFlowPathBased();

	}
}