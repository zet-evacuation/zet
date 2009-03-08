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
/**
 * Class GLGraphFloorControl
 * Erstellt 08.05.2008, 01:29:54
 */

package gui.visualization.control.graph;

import ds.graph.GraphVisualizationResult;
import ds.graph.Node;
import gui.visualization.control.AbstractControl;
import gui.visualization.control.GLControl;
import gui.visualization.draw.graph.GLGraphFloor;
import gui.visualization.draw.graph.GLNode;
import java.util.Iterator;

/**
 * The <code>GLGraphFloorControl</code> class represents an floor of the
 * graph network in an MVC-design. It guarantees that the nodes belonging to
 * the floor are created and submitted to the view object.
 * @author Jan-Philipp Kappmeier
 */
public class GLGraphFloorControl extends AbstractControl<GLGraphFloor, Integer, GraphVisualizationResult, GLNode, GLNodeControl>{

	/**
	 * Creates a new instance of <code>GLGraphFloorControl</code>. Therefore for
	 * any node in the list <code>nodesOnTheFloor</code> the related control and
	 * visualization objects are created.
	 * @param graphVisResult the graph visualization results
	 * @param nodesOnTheFloor the nodes that lie on this floor
	 * @param floor the number of the floor
	 * @param glControl the general control object for visualization
	 */
	public GLGraphFloorControl( GraphVisualizationResult graphVisResult, Iterable<Node> nodesOnTheFloor, Integer floor, GLControl glControl ) {
		super( floor, graphVisResult, glControl );
		Iterator<Node> it = nodesOnTheFloor.iterator();
		Node supersink = graphVisResult.getSupersink();
		while( it.hasNext() ) {
			Node n = it.next();
			if( !n.equals(supersink) )
				add( new GLNodeControl( graphVisResult, n, glControl ) );
		}
		setView( new GLGraphFloor( this ) );
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
