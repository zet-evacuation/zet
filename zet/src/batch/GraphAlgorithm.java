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
package batch;

import localization.Localization;
import ds.NetworkFlowModel;
import batch.tasks.graph.EATransshipmentMinCostTask;
import batch.tasks.graph.EATransshipmentSSSPTask;
import ds.NetworkFlowModelAlgorithm;
import batch.tasks.graph.MFOTMinCostTask;
import batch.tasks.graph.MFOTimeExpandedTask;
import batch.tasks.graph.QuickestTransshipmentTask;
import batch.tasks.graph.SuccessiveEarliestArrivalAugmentingPathAlgorithm2Task;
import batch.tasks.graph.SuccessiveEarliestArrivalAugmentingPathAlgorithmTask;
import batch.tasks.graph.SuccessiveEarliestArrivalAugmentingPathAlgorithmTask3;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.flow.PathBasedFlowOverTime;

/** Enumerates the types of graph algorithms and assigns each of them a way
 * to get the associated NetworkFlowModelAlgorithm and a name;
 *
 * @author Timon
 */
public enum GraphAlgorithm {

    EarliestArrivalTransshipmentSuccessiveShortestPaths(Localization.getInstance().getString("gui.EATransshipmentSSSP")) {

        public NetworkFlowModelAlgorithm createTask(NetworkFlowModel model, int timeHorizon) {
            return new EATransshipmentSSSPTask();
        }
    },
    EarliestArrivalTransshipmentMinCost(Localization.getInstance().getString("gui.EATransshipmentMinCost")) {

        public NetworkFlowModelAlgorithm createTask(NetworkFlowModel model, int timeHorizon) {
            return new EATransshipmentMinCostTask();
        }
    },
    SuccessiveEarliestArrivalAugmentingPathBinarySearch(Localization.getInstance().getString("gui.SuccEAAugPathBS")) {

        public NetworkFlowModelAlgorithm createTask(NetworkFlowModel model, int timeHorizon) {
            return new SuccessiveEarliestArrivalAugmentingPathAlgorithmTask();
        }
    },
    SuccessiveEarliestArrivalAugmentingPath(Localization.getInstance().getString("gui.SuccEAAugPath")) {

        public NetworkFlowModelAlgorithm createTask(NetworkFlowModel model, int timeHorizon) {
            return new SuccessiveEarliestArrivalAugmentingPathAlgorithm2Task();
        }
    },
    MaxFlowOverTimeMinCost(Localization.getInstance().getString("gui.MaxFlowMinCost")) {

        public NetworkFlowModelAlgorithm createTask(NetworkFlowModel model, int timeHorizon) {
            return new MFOTMinCostTask(timeHorizon);
        }
    },
    MaxFlowOverTimeTimeExpanded(Localization.getInstance().getString("gui.MaxFlowTimeExtended")) {

        public NetworkFlowModelAlgorithm createTask(NetworkFlowModel model, int timeHorizon) {
            return new MFOTimeExpandedTask(timeHorizon);
        }
    },
    QuickestTransshipment(Localization.getInstance().getString("gui.QuickestTransshipment")) {

        public NetworkFlowModelAlgorithm createTask(NetworkFlowModel model, int timeHorizon) {
            return new QuickestTransshipmentTask();
        }
    },
    SuccessiveEarliestArrivalAugmentingPathOptimized(Localization.getInstance().getString("gui.SEAAP")) {

        public Algorithm<NetworkFlowModel, PathBasedFlowOverTime> createTask(NetworkFlowModel model, int timeHorizon) {
            return new SuccessiveEarliestArrivalAugmentingPathAlgorithmTask3();
        }
    },;
    private String name;

    private GraphAlgorithm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract Algorithm<NetworkFlowModel, PathBasedFlowOverTime> createTask(NetworkFlowModel model, int timeHorizon);
}
