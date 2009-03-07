package evacuationplan;

import java.util.List;

import converter.ZToCAConverter;
import converter.ZToGraphRasterContainer;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.z.BuildingPlan;
import ds.z.ConcreteAssignment;
import ds.ca.CellularAutomaton;
import ds.ca.Individual;
import evacuationplan.BidirectionalNodeCellMapping.CAPartOfMapping;

public class FlowBasedCAFactory extends ZToCAConverter {
	
	private static FlowBasedCAFactory instance=null;
	private static CAPathPassabilityChecker lastChecker=null; 
	
	protected FlowBasedCAFactory(){
		
	}
	
	/**
	 * Creates a new cellular automaton from a building plan, 
	 * a transshipment and a concrete assignment. Within the cellular automaton,
	 * each individual gets its own potential that is conform to one of the
	 * dynamic path flows in the transshipment.
	 * @param buildingPlan The building in Z-Format.
	 * @param transshipment A transshipment calculated for the graph in the same 
	 * building with the same concrete assignment.
	 * @param concreteAssignment All individuals and their positions.
	 * @return A cellular automation realizing evacuation plans according to the transshipment.
	 * @throws ConversionNotSupportedException
	 */
	public CellularAutomaton convertAndApplyConcreteAssignment( BuildingPlan buildingPlan, PathBasedFlowOverTime transshipment, ConcreteAssignment concreteAssignment, ZToGraphRasterContainer graphRaster ) throws ConversionNotSupportedException {
		CellularAutomaton ca = super.convert(buildingPlan);
		CAPartOfMapping caPartOfMapping = this.getLatestCAPartOfNodeCellMapping();
		applyConcreteAssignment(concreteAssignment);
		BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping(graphRaster, caPartOfMapping);
		CAPathPassabilityChecker checker = new CAPathPassabilityChecker(ca, nodeCellMapping, transshipment);
		lastChecker = checker;
		List<Individual> individualList = ca.getIndividuals();
		for (Individual ind : individualList){
			EvacPotential ep = checker.getPotential(ind);
			ca.getPotentialManager().addStaticPotential(ep);
			ind.setStaticPotential(ep);
		}
		return ca;
	}
	
	/**
	 * The usual convert method may not be used because a transshipment is also needed.
	 */
	@Override
	public CellularAutomaton convert( BuildingPlan buildingPlan) throws ConversionNotSupportedException {
		throw new UnsupportedOperationException("Use the convert-method that additionaly takes a transshipment.");
	}
	
	/**
	 * Empty method to stop the converter from calculating static potentials. 
	 * Potentials are added separately.
	 */
	@Override
	protected void calculateAndAddStaticPotentials(CellularAutomaton convertedCA ){		
	}

	public static FlowBasedCAFactory getFlowBasedCAFactoryInstance(){
		if (instance==null){
			instance = new FlowBasedCAFactory();
		}
		return instance;
	}
	
	/**
	 * Returns the <code>CAPathPassabilityChecker</code> object created
	 * during the last call of <code>convertAndApplyConcreteAssignment</code>.
	 * @return the <code>CAPathPassabilityChecker</code> object created
	 * during the last call of <code>convertAndApplyConcreteAssignment</code>.
	 */
	public CAPathPassabilityChecker getLatestCheckerInstance(){
		return lastChecker;
	}
	
}
