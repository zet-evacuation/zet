/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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

/**
 * Class GLGraphFloorControl
 * Erstellt 08.05.2008, 01:29:54
 */

package gui.visualization.control.graph;

import ds.GraphVisualizationResults;
import ds.graph.IdentifiableCollection;
import ds.graph.Node;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.draw.graph.GLGraphFloor;
import java.util.Iterator;
import de.tu_berlin.math.coga.graph.io.xml.FlowVisualization;

/**
 * The <code>GLGraphFloorControl</code> class represents an floor of the
 * graph network in an MVC-design. It guarantees that the nodes belonging to
 * the floor are created and submitted to the view object.
 * @author Jan-Philipp Kappmeier
 */
//public class GLGraphFloorControl extends AbstractControl<GLGraphFloor, Integer, GraphVisualizationResults, GLNode, GLNodeControl, GLControl>{
public class GLGraphFloorControl extends AbstractZETVisualizationControl<GLNodeControl, GLGraphFloor, GLGraphControl> {

	/**
	 * Creates a new instance of <code>GLGraphFloorControl</code>. Therefore for
	 * any node in the list <code>nodesOnTheFloor</code> the related control and
	 * visualization objects are created.
	 * @param graphVisResult the graph visualization results
	 * @param nodesOnTheFloor the nodes that lie on this floor
	 * @param floor the number of the floor
	 * @param glControl the general control object for visualization
	 */
	public GLGraphFloorControl( GraphVisualizationResults graphVisResult, Iterable<Node> nodesOnTheFloor, Integer floor, GLGraphControl glControl ) {
		super( glControl );
		Iterator<Node> it = nodesOnTheFloor.iterator();
		Node supersink = graphVisResult.getSupersink();
		while( it.hasNext() ) {
			Node n = it.next();
			if( !n.equals(supersink) && floor != 0 )
				add( new GLNodeControl( graphVisResult, n, glControl ) );
		}
		setView( new GLGraphFloor( this ) );
		for( GLNodeControl node : this )
			view.addChild( node.getView() );
	}

	GLGraphFloorControl( FlowVisualization fv, IdentifiableCollection<Node> nodes, GLGraphControl mainControl ) {
		super( mainControl );
		Iterator<Node> it = nodes.iterator();
		Node supersink = fv.getGv().getSinks().get( 0 );
		while( it.hasNext() ) {
			Node n = it.next();
			//if( !n.equals(supersink) )
				add( new GLNodeControl( fv, n, mainControl ) );
		}
		setView( new GLGraphFloor( this ) );
		for( GLNodeControl node : this )
			view.addChild( node.getView() );
	}

	/**
	 * Returns the x-offset of the floor.
	 * @return the x-offset of the floor.
	 */
	public double getXPosition(){
		return 0.0d;
	}
	
	/**
	 * Returns the y-offset of the floor.
	 * @return the y-offset of the floor.
	 */
	public double getYPosition(){
		return 0.0d;
	}
}
