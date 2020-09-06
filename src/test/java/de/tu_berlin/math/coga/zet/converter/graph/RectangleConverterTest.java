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
package de.tu_berlin.math.coga.zet.converter.graph;

import static org.junit.Assert.*;

import org.junit.Test;

import de.zet_evakuierung.network.model.NetworkFlowModel;
import ds.graph.NodeRectangle;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class RectangleConverterTest {

	@Test
	public void testCreation() {
		InstanceGenerator ig = new InstanceGenerator();
		ig.setUpInstance();

		// test
		RectangleConverter conv = new RectangleConverter();
		conv.setProblem( ig.zControl.getProject().getBuildingPlan() );
		conv.run();
		
		assertTrue( conv.checkParallelEdges(conv.getModel()) );
		
		assertEquals( "Number of nodes", 10, conv.getSolution().numberOfNodes() );
		assertEquals( "Number of edges", 35, conv.getSolution().numberOfEdges() );
		
		// check computed distances
		for( Edge edge : conv.getSolution().edges() ) {
			NodeRectangle nrs = conv.mapping.getNodeRectangles().get( edge.start() );
			NodeRectangle nre = conv.mapping.getNodeRectangles().get( edge.end() );
			
			//System.out.println( edge + " - " + nrs.getCenterX() + "," + nrs.getCenterY() + " - " + nre.getCenterX() + "," + nre.getCenterY() );
			
			// eine flusseinheit fließt in 400
			double dist = Math.sqrt( (nrs.getCenterX()-nre.getCenterX())*(nrs.getCenterX()-nre.getCenterX()) + (nrs.getCenterY()-nre.getCenterY())*(nrs.getCenterY()-nre.getCenterY()) )/400;
			
			double tt = conv.getSolution().getExactTransitTime( edge );
			//System.out.println( dist + " - " + tt + " - " + tt_rounded );
			
			if( tt > 0 ) // check triangle equation before rounding
				assertTrue( "Triangle equation", dist <= tt );
		}
		
		// check easy distances between nodes that lie on the grid
		check( 3, 6, 3.0, conv.getSolution() );
		check( 4, 5, 2.0, conv.getSolution() );
		check( 5, 9, 2.0, conv.getSolution() );
	}
	final static double eps = 0.0000001;
	
	private void check( int ni1, int ni2, double distance, NetworkFlowModel nfm ) {
		Node n1 = nfm.getNode( ni1 );
		Node n2 = nfm.getNode( ni2 );
		assertEquals( distance, nfm.getTransitTime( nfm.getEdge( n1, n2) ), eps );
		assertEquals( distance, nfm.getTransitTime( nfm.getEdge( n2, n1) ), eps );
	}
}
