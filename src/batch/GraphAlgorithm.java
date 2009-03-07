package batch;

import localization.Localization;
import ds.graph.NetworkFlowModel;
import tasks.EATransshipmentMinCostTask;
import tasks.EATransshipmentSSSPTask;
import tasks.GraphAlgorithmTask;
import tasks.MFOTMinCostTask;
import tasks.MFOTimeExpandedTask;
import tasks.QuickestTransshipmentTask;
import tasks.SuccessiveEarliestArrivalAugmentingPathAlgorithm2Task;
import tasks.SuccessiveEarliestArrivalAugmentingPathAlgorithmTask;
import tasks.SuccessiveEarliestArrivalAugmentingPathAlgorithmTask3;

/** Enumerates the types of graph algorithms and assigns each of them a way
 * to get the associated GraphAlgorithmTask and a name;
 *
 * @author Timon
 */
public enum GraphAlgorithm {
  EarliestArrivalTransshipmentSuccessiveShortestPaths (Localization.getInstance().getString("gui.EATransshipmentSSSP")) {
		public GraphAlgorithmTask createTask (NetworkFlowModel model, int timeHorizon) {
			return new EATransshipmentSSSPTask (model);
		}
	}, 
	EarliestArrivalTransshipmentMinCost (Localization.getInstance().getString("gui.EATransshipmentMinCost")) {
		public GraphAlgorithmTask createTask (NetworkFlowModel model, int timeHorizon) {
			return new EATransshipmentMinCostTask (model);
		}
	}, 
	SuccessiveEarliestArrivalAugmentingPathBinarySearch (Localization.getInstance().getString("gui.SuccEAAugPathBS")) {
		public GraphAlgorithmTask createTask (NetworkFlowModel model, int timeHorizon) {
			return new SuccessiveEarliestArrivalAugmentingPathAlgorithmTask (model);
		}
	}, 
	SuccessiveEarliestArrivalAugmentingPath (Localization.getInstance().getString("gui.SuccEAAugPath")) {
		public GraphAlgorithmTask createTask (NetworkFlowModel model, int timeHorizon) {
			return new SuccessiveEarliestArrivalAugmentingPathAlgorithm2Task (model);
		}
	}, 
	MaxFlowOverTimeMinCost (Localization.getInstance().getString("gui.MaxFlowMinCost")) {
		public GraphAlgorithmTask createTask (NetworkFlowModel model, int timeHorizon) {
			return new MFOTMinCostTask (model, timeHorizon);
		}
	}, 
	MaxFlowOverTimeTimeExpanded (Localization.getInstance().getString("gui.MaxFlowTimeExtended")) {
		public GraphAlgorithmTask createTask (NetworkFlowModel model, int timeHorizon) {
			return new MFOTimeExpandedTask (model, timeHorizon);
		}
	}, 
	QuickestTransshipment (Localization.getInstance().getString("gui.QuickestTransshipment")) {
		public GraphAlgorithmTask createTask (NetworkFlowModel model, int timeHorizon) {
			return new QuickestTransshipmentTask (model);
		}
	},
	SuccessiveEarliestArrivalAugmentingPathOptimized (Localization.getInstance().getString("gui.SEAAP")) {
		public GraphAlgorithmTask createTask (NetworkFlowModel model, int timeHorizon) {
			return new SuccessiveEarliestArrivalAugmentingPathAlgorithmTask3 (model);
		}
	},        
        ;
	
	private String name;
	
	GraphAlgorithm (String name) {
		this.name = name;
	}
	
	public String getName () {
		return name;
	}
	
	@Override
	public String toString () {
		return name;
	}
	
	public abstract GraphAlgorithmTask createTask (NetworkFlowModel model, int timeHorizon);
}
