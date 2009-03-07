package exitdistributions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import algo.graph.exitassignment.ExitAssignment;
import converter.ZToCAConverter;
import converter.ZToGraphRasterContainer;
import ds.z.BuildingPlan;
import ds.z.ConcreteAssignment;
import ds.ca.CellularAutomaton;
import ds.ca.Individual;
import ds.ca.StaticPotential;
import ds.ca.TargetCell;
import evacuationplan.BidirectionalNodeCellMapping;
import evacuationplan.BidirectionalNodeCellMapping.CAPartOfMapping;

public class ExitDistributionBasedCAFactory extends ZToCAConverter {
	
	/**
	 * The (only) instance of this ExitDistributionBasedCAFactory. It is created at first need.
	 */
	private static ExitDistributionBasedCAFactory instance=null;
	/**
	 * A mapping of individuals to exits that is created according to an {@link algo.graph.exitassignment.ExitAssignment}.
	 */
	private static GraphBasedIndividualToExitMapping graphBasedIndividualToExitMaping = null;
	
	/**
	 * An empty constructor.
	 */
	protected ExitDistributionBasedCAFactory(){		
	}
	
	/**
	 * Creates a new cellular automaton from a building plan, 
	 * an exit assignment and a concrete assignment. 
	 * @param buildingPlan The building in Z-Format.
	 * @param exitAssignment An exit assignment computed by a graph algorithm that gives an exit distribution for each source node. The exit assignment must belong to the same building and concrete assignment.
	 * @param concreteAssignment All individuals and their positions.
	 * @param graphRaster 
	 * @return A cellular automation that realizes the exit distribution given by <code>exitAssignment</code>.
	 * @throws converter.ZToCAConverter.ConversionNotSupportedException
	 */
	public CellularAutomaton convertAndApplyConcreteAssignment( BuildingPlan buildingPlan, ExitAssignment exitAssignment, ConcreteAssignment concreteAssignment, ZToGraphRasterContainer graphRaster ) throws converter.ZToCAConverter.ConversionNotSupportedException {
		CellularAutomaton ca = super.convert(buildingPlan);
		CAPartOfMapping caPartOfMapping = this.getLatestCAPartOfNodeCellMapping();
		applyConcreteAssignment(concreteAssignment);
		BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping(graphRaster, caPartOfMapping);
		graphBasedIndividualToExitMaping = new GraphBasedIndividualToExitMapping(ca, nodeCellMapping, exitAssignment);
		graphBasedIndividualToExitMaping.calculate();
		ca.setIndividualToExitMapping( graphBasedIndividualToExitMaping );
		return ca;
	}
	
	/**
	 * The usual convert method may not be used because an exit assignment is also needed.
	 */
	@Override
	public CellularAutomaton convert( BuildingPlan buildingPlan) throws ConversionNotSupportedException {
		throw new UnsupportedOperationException("Use the convert-method that additionaly takes a transshipment.");
	}
	
/*	*//**
	 * Empty method to stop the converter from calculating static potentials. 
	 * Potentials are added separately.
	 *//*
	@Override
	protected void calculateAndAddStaticPotentials(CellularAutomaton convertedCA ){		
	}*/

	/**
	 * Returns the (only) instance of the <code>ExitDistributionBasedCAFactory</code>.
	 * @return the (only) instance of the <code>ExitDistributionBasedCAFactory</code>.
	 */
	public static ExitDistributionBasedCAFactory getInstance(){
		if (instance==null){
			instance = new ExitDistributionBasedCAFactory();
		}
		return instance;
	}
	
	/**
	 * Returns the <code>GraphBasedIndividualToExitMapping</code> object created
	 * during the last call of <code>convertAndApplyConcreteAssignment</code>.
	 * @return the <code>GraphBasedIndividualToExitMapping</code> object created
	 * during the last call of <code>convertAndApplyConcreteAssignment</code>.
	 */
	public GraphBasedIndividualToExitMapping getLatestMappingInstance(){
		return graphBasedIndividualToExitMaping;
	}
}
