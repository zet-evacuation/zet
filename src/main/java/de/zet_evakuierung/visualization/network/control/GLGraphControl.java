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
package de.zet_evakuierung.visualization.network.control;

import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import de.zet_evakuierung.visualization.network.draw.GLGraph;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Node;
import org.zetool.opengl.framework.abs.AbstractControl;
import org.zetool.opengl.framework.abs.DrawableControlable;
import org.zetool.opengl.helper.Frustum;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLGraphControl extends AbstractControl<GLSimpleNodeControl, GLGraph> implements DrawableControlable {
	protected NodePositionMapping nodePositionMapping;
	protected DirectedGraph graph;

	public GLGraphControl( DirectedGraph graph, NodePositionMapping nodePositionMapping ) {
		this( graph, nodePositionMapping, true );
	}

public GLGraphControl( DirectedGraph graph, NodePositionMapping nodePositionMapping, boolean setUpNodes ) {
		this.nodePositionMapping = nodePositionMapping;
		this.graph = graph;
		if( setUpNodes )
			setUpNodes();
	}

	protected void setUpNodes() {
		for( Node n : graph.nodes() ) {
			GLSimpleNodeControl nodeControl = new GLSimpleNodeControl( graph, n, nodePositionMapping );
			add( nodeControl );
		}

		this.setView( new GLGraph( this ) );
		for( GLSimpleNodeControl nodeControl : this )
			view.addChild( nodeControl.getView() );
	}

	public void setFrustum( Frustum frustum ) {

	}

	public Frustum getFrustum() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public void update() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public void delete() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public void addTime( long timeNanoSeconds ) {

	}

	public void setTime( long time ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public void resetTime() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public boolean isFinished() {
		return false;
	}

}
