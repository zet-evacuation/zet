/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
package de.tu_berlin.math.coga.batch.operations;

import org.zetool.components.batch.operations.AbstractOperation;
import org.zetool.common.algorithm.AbstractAlgorithm;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.model.Project;
import de.tu_berlin.math.coga.zet.converter.AssignmentConcrete;
import org.zetool.components.batch.input.reader.InputFileReader;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.RectangleConverter;
import ds.GraphVisualizationResults;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ConversionOnly extends AbstractOperation<Project,GraphVisualizationResults> {
	private static final Logger log = Logger.getGlobal();
	InputFileReader<Project> input;
  GraphVisualizationResults gvr;
	Project project;

  @Override
	@SuppressWarnings( "unchecked" )
  public boolean consume( InputFileReader<?> o ) {
		if( o.getTypeClass() == Project.class ) {
			input = (InputFileReader<Project>)o;
			return true;
		}
		return false;
  }

  @Override
  public Class<GraphVisualizationResults> produces() {
    return GraphVisualizationResults.class;
  }

  @Override
  public List<Class<?>> getProducts() {
    return Arrays.asList( new Class<?>[] {produces(), BuildingPlan.class} );
  }

  @Override
  public Object getProduct( Class<?> productType ) {
    if( productType == BuildingPlan.class ) {
      return project.getBuildingPlan();
    } else {
      return super.getProduct( productType );
    }
  }

  @Override
  public GraphVisualizationResults getProduced() {
    return gvr;    
  }

  @Override
  public void run() {
    // Convert
		project = input.getSolution();

		System.out.println( project );

		if( !project.getBuildingPlan().isRastered() ) {
			System.out.print( "Building is not rasterized. Rastering... " );
			project.getBuildingPlan().rasterize();
			System.out.println( " done." );
		}

    final AbstractAlgorithm<BuildingPlan,NetworkFlowModel> conv = new RectangleConverter();
		System.out.println( "Selected algorithm: " + conv );

    conv.setProblem( project.getBuildingPlan() );
		conv.run();
		NetworkFlowModel networkFlowModel;


    networkFlowModel = conv.getSolution();

		// convert and create the concrete assignment
		ConcreteAssignment concreteAssignment = AssignmentConcrete.createConcreteAssignment( project.getCurrentAssignment(), 400 );

		GraphAssignmentConverter cav = new GraphAssignmentConverter( networkFlowModel );

		cav.setProblem( concreteAssignment );
		cav.run();
		networkFlowModel = cav.getSolution();


    
    // Generate building results
    
    // Create Graph
    gvr = new GraphVisualizationResults( cav.getSolution(), cav.getSolution().getNodeCoordinates() );
    
  }
  
}
