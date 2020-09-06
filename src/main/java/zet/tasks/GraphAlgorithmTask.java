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
package zet.tasks;

import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.common.algorithm.AbstractAlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmListener;
import de.zet_evakuierung.network.model.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.graph.RectangleConverter;
import ds.GraphVisualizationResults;
import ds.PropertyContainer;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.model.Project;
import de.tu_berlin.math.coga.zet.converter.AssignmentConcrete;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphAlgorithmTask extends AbstractAlgorithm<Project, GraphVisualizationResults> implements AlgorithmListener {

	GraphAlgorithmEnumeration graphAlgorithm;
	NetworkFlowModel networkFlowModel;
	AbstractAlgorithm<BuildingPlan,NetworkFlowModel> conv = new RectangleConverter();

	public GraphAlgorithmTask( GraphAlgorithmEnumeration graphAlgorithm ) {
		this.graphAlgorithm = graphAlgorithm;
	}

	@Override
	protected GraphVisualizationResults runAlgorithm( Project project ) {
		if( networkFlowModel == null ) {
			conv.setProblem( project.getBuildingPlan() );
			conv.run();
			networkFlowModel = conv.getSolution();
		}

		// convert and create the concrete assignment
		ConcreteAssignment concreteAssignment = AssignmentConcrete.createConcreteAssignment( project.getCurrentAssignment(), 400 );
		
		GraphAssignmentConverter cav = new GraphAssignmentConverter( networkFlowModel );
		
		cav.setProblem( concreteAssignment );
		cav.run();
		networkFlowModel = cav.getSolution();


		// call the graph algorithm
		int maxTime = (int) PropertyContainer.getGlobal().getAsDouble( "algo.ca.maxTime" );
		AbstractAlgorithm<NetworkFlowModel, PathBasedFlowOverTime> gt;
		gt = graphAlgorithm.createTask( cav.getSolution(), maxTime );
		gt.setProblem( cav.getSolution() );
		gt.addAlgorithmListener( this );
		gt.run();

		// create graph vis result
		GraphVisualizationResults gvr = new GraphVisualizationResults( cav.getSolution(), gt.getSolution() );
		return gvr;
	}

	public NetworkFlowModel getNetworkFlowModel() {
		return networkFlowModel;
	}

	public void setConv( AbstractAlgorithm<BuildingPlan,NetworkFlowModel> conv ) {
		this.conv = conv;
	}
	
	public void setNetworkFlowModel( NetworkFlowModel networkFlowModel) {
		this.networkFlowModel = networkFlowModel;
	}

	@Override
	public void eventOccurred( AbstractAlgorithmEvent event ) {

	}
}
