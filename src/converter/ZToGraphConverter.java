package converter;

import ds.graph.NetworkFlowModel;
import ds.z.BuildingPlan;
import ds.z.ConcreteAssignment;

public class ZToGraphConverter {
	
	static enum GraphConverter { 
		/** 
		 * The original converter, divides rooms in rectangles and generates 
		 * one node for each rectangle. 
		 * */
		RECTANGLE_BASED_CONVERTER,	
		
		/**
		 * The converter by Melanie, generates one node for each raster square.
		 */
		
		EXACT_CONVERTER_BY_MELANIE,
		
		/** 
		 * The converter by Martin, generates one node for each raster square. 
		 * */
		EXACT_CONVERTER_BY_MARTIN		
	}
	
	private static final GraphConverter USED_CONVERTER = GraphConverter.RECTANGLE_BASED_CONVERTER; 
	
	public static void convertBuildingPlan(BuildingPlan plan,
			NetworkFlowModel model) {
		switch (USED_CONVERTER) {
		case RECTANGLE_BASED_CONVERTER:
			ZToNonGridGraphConverter.convertBuildingPlan(plan, model);
			break;
		case EXACT_CONVERTER_BY_MELANIE:
			ZToGridGraphConverterAlt2.convertBuildingPlan(plan, model);
			break;
		case EXACT_CONVERTER_BY_MARTIN:
			ZToGridGraphConverterAlt3.convertBuildingPlan(plan, model);
			break;
		default:
			throw new AssertionError(
					"I cannot handle the converter type"
							+ USED_CONVERTER
							+ ". You need to extend the switch-statement in this method.");
		}
	}
	
	public static void convertConcreteAssignment(ConcreteAssignment assignment, NetworkFlowModel model) {
		switch (USED_CONVERTER) {
		case RECTANGLE_BASED_CONVERTER:
			ZToNonGridGraphConverter.convertConcreteAssignment(assignment, model);
			break;
		case EXACT_CONVERTER_BY_MELANIE:
			ZToGridGraphConverterAlt2.convertConcreteAssignment(assignment,
					model);
			break;
		case EXACT_CONVERTER_BY_MARTIN:
			ZToGridGraphConverterAlt3.convertConcreteAssignment(assignment, model);
			break;
		default:
			throw new AssertionError(
					"I cannot handle the converter type"
							+ USED_CONVERTER
							+ ". You need to extend the switch-statement in this method.");
		}
	}
	
	

}
