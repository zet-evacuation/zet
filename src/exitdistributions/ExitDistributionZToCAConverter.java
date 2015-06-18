/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package exitdistributions;

import algo.graph.exitassignment.ExitAssignment;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterContainer;
import ds.ca.evac.EvacuationCellularAutomaton;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ConvertedCellularAutomaton;
import evacuationplan.BidirectionalNodeCellMapping.CAPartOfMapping;

public class ExitDistributionZToCAConverter extends ZToCAConverter {
//public class ExitDistributionZToCAConverter extends ZToCAConverter {

  /** The instance of this singleton ExitDistributionZToCAConverter. It is created at first need. */
	//private static ExitDistributionZToCAConverter instance=null;
  /** A mapping of individuals to exits that is created according to an
   * {@link algo.graph.exitassignment.ExitAssignment}. */
  private GraphBasedIndividualToExitMapping graphBasedIndividualToExitMaping = null;

  /**
   * An empty constructor.
   */
  public ExitDistributionZToCAConverter() {
  }

  ConcreteAssignment concreteAssignment;
  @Override
	protected ConvertedCellularAutomaton runAlgorithm( BuildingPlan problem ) {

    System.out.println( "Convert Cellular automaton" );

    ConvertedCellularAutomaton cca = super.runAlgorithm( problem );

		CAPartOfMapping caPartOfMapping = this.getLatestCAPartOfNodeCellMapping();
//		applyConcreteAssignment(concreteAssignment);
//		BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping(graphRaster, caPartOfMapping);
//		graphBasedIndividualToExitMaping = new GraphBasedIndividualToExitMapping(ca, nodeCellMapping, exitAssignment);
//		graphBasedIndividualToExitMaping.calculate();
//		ca.setIndividualToExitMapping( graphBasedIndividualToExitMaping );


    return cca;
  }

  /**
   * Creates a new cellular automaton from a building plan, an exit assignment and a concrete assignment.
   * @param buildingPlan The building in Z-Format.
   * @param exitAssignment An exit assignment computed by a graph algorithm that gives an exit distribution for each
   * source node. The exit assignment must belong to the same building and concrete assignment.
   * @param concreteAssignment All individuals and their positions.
   * @param graphRaster
   * @return A cellular automation that realizes the exit distribution given by {@code exitAssignment}.
   * @throws converter.ZToCAConverter.ConversionNotSupportedException
   */
  public EvacuationCellularAutomaton convertAndApplyConcreteAssignment( BuildingPlan buildingPlan, ExitAssignment exitAssignment, ConcreteAssignment concreteAssignment, ZToGraphRasterContainer graphRaster ) throws de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter.ConversionNotSupportedException {
		//EvacuationCellularAutomaton ca = super.convert(buildingPlan);
//		CAPartOfMapping caPartOfMapping = this.getLatestCAPartOfNodeCellMapping();
//		applyConcreteAssignment(concreteAssignment);
//		BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping(graphRaster, caPartOfMapping);
//		graphBasedIndividualToExitMaping = new GraphBasedIndividualToExitMapping(ca, nodeCellMapping, exitAssignment);
//		graphBasedIndividualToExitMaping.calculate();
//		ca.setIndividualToExitMapping( graphBasedIndividualToExitMaping );
//		return ca;
    return new EvacuationCellularAutomaton();
  }

  /**
   * The usual convert method may not be used because an exit assignment is also needed.
   */
//	@Override
//	public EvacuationCellularAutomaton convert( BuildingPlan buildingPlan) throws ConversionNotSupportedException {
//		throw new UnsupportedOperationException("Use the convert-method that additionaly takes a transshipment.");
//	}

  /*	*/
  /**
   * Empty method to stop the converter from calculating static potentials. Potentials are added separately.
   *//*
   @Override
   protected void computeAndAddStaticPotentials(EvacuationCellularAutomaton convertedCA ){
   }*/

  /**
   * Returns the (only) instance of the {@code ExitDistributionZToCAConverter}.
   * @return the (only) instance of the {@code ExitDistributionZToCAConverter}.
   */
//  public static ExitDistributionZToCAConverter getInstance() {
//    if( instance == null ) {
//      instance = new ExitDistributionZToCAConverter();
//    }
//    return instance;
//  }

  /**
   * Returns the {@code GraphBasedIndividualToExitMapping} object created during the last call of
   * {@code convertAndApplyConcreteAssignment}.
   * @return the {@code GraphBasedIndividualToExitMapping} object created during the last call of
   * {@code convertAndApplyConcreteAssignment}.
   */
  public GraphBasedIndividualToExitMapping getLatestMappingInstance() {
    return graphBasedIndividualToExitMaping;
  }
}
