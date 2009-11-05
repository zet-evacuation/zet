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
import batch.tasks.GraphAlgorithmTask;
import batch.tasks.graph.MFOTMinCostTask;
import batch.tasks.graph.MFOTimeExpandedTask;
import batch.tasks.graph.QuickestTransshipmentTask;
import batch.tasks.graph.SuccessiveEarliestArrivalAugmentingPathAlgorithm2Task;
import batch.tasks.graph.SuccessiveEarliestArrivalAugmentingPathAlgorithmTask;
import batch.tasks.graph.SuccessiveEarliestArrivalAugmentingPathAlgorithmTask3;

/** Enumerates the types of graph algorithms and assigns each of them a way
 * to get the associated GraphAlgorithmTask and a name;
 *
 * @author Timon
 */
public enum GraphAlgorithm {
	EarliestArrivalTransshipmentSuccessiveShortestPaths( Localization.getInstance().getString( "gui.EATransshipmentSSSP" ) ) {
		public GraphAlgorithmTask createTask( NetworkFlowModel model, int timeHorizon ) {
			return new EATransshipmentSSSPTask( model );
		}
	},
	EarliestArrivalTransshipmentMinCost( Localization.getInstance().getString( "gui.EATransshipmentMinCost" ) ) {
		public GraphAlgorithmTask createTask( NetworkFlowModel model, int timeHorizon ) {
			return new EATransshipmentMinCostTask( model );
		}
	},
	SuccessiveEarliestArrivalAugmentingPathBinarySearch( Localization.getInstance().getString( "gui.SuccEAAugPathBS" ) ) {
		public GraphAlgorithmTask createTask( NetworkFlowModel model, int timeHorizon ) {
			return new SuccessiveEarliestArrivalAugmentingPathAlgorithmTask( model );
		}
	},
	SuccessiveEarliestArrivalAugmentingPath( Localization.getInstance().getString( "gui.SuccEAAugPath" ) ) {
		public GraphAlgorithmTask createTask( NetworkFlowModel model, int timeHorizon ) {
			return new SuccessiveEarliestArrivalAugmentingPathAlgorithm2Task( model );
		}
	},
	MaxFlowOverTimeMinCost( Localization.getInstance().getString( "gui.MaxFlowMinCost" ) ) {
		public GraphAlgorithmTask createTask( NetworkFlowModel model, int timeHorizon ) {
			return new MFOTMinCostTask( model, timeHorizon );
		}
	},
	MaxFlowOverTimeTimeExpanded( Localization.getInstance().getString( "gui.MaxFlowTimeExtended" ) ) {
		public GraphAlgorithmTask createTask( NetworkFlowModel model, int timeHorizon ) {
			return new MFOTimeExpandedTask( model, timeHorizon );
		}
	},
	QuickestTransshipment( Localization.getInstance().getString( "gui.QuickestTransshipment" ) ) {
		public GraphAlgorithmTask createTask( NetworkFlowModel model, int timeHorizon ) {
			return new QuickestTransshipmentTask( model );
		}
	},
	SuccessiveEarliestArrivalAugmentingPathOptimized( Localization.getInstance().getString( "gui.SEAAP" ) ) {
		public GraphAlgorithmTask createTask( NetworkFlowModel model, int timeHorizon ) {
			return new SuccessiveEarliestArrivalAugmentingPathAlgorithmTask3( model );
		}
	},;
	private String name;

	GraphAlgorithm( String name ) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public abstract GraphAlgorithmTask createTask( NetworkFlowModel model, int timeHorizon );
}
