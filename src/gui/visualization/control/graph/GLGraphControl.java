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
 * Class GLGraphControl
 * Erstellt 02.05.2008, 18:44:28
 */
package gui.visualization.control.graph;

import ds.GraphVisualizationResult;
import ds.graph.Network;
import gui.visualization.control.AbstractControl;
import gui.visualization.control.GLControl;
import gui.visualization.draw.graph.GLGraph;
import gui.visualization.draw.graph.GLGraphFloor;
import java.util.HashMap;
import java.util.Iterator;

/**
 *  @author Jan-Philipp Kappmeier
 */
public class GLGraphControl extends AbstractControl<GLGraph, Network, GraphVisualizationResult, GLGraphFloor, GLGraphFloorControl> {

	private HashMap<Integer, GLGraphFloorControl> allFloorsByID;

	public GLGraphControl( GraphVisualizationResult graphVisResult, GLControl glControl) {
		super( graphVisResult.getNetwork(), graphVisResult, glControl );
		allFloorsByID = new HashMap<Integer, GLGraphFloorControl>();
		int floorCount = graphVisResult.getFloorToNodeMapping().size();
		for( int i = 0; i < floorCount; i++ ) {
			if( graphVisResult.getFloorToNodeMapping().get( i ).size() > 0 ) {
				GLGraphFloorControl floorControl = new GLGraphFloorControl( graphVisResult, graphVisResult.getFloorToNodeMapping().get( i ), i, glControl );
				add( floorControl );
				allFloorsByID.put( i, floorControl );
			}
		}
		this.setView( new GLGraph( this ) );
	}

	@Override
	public void clear() {
		allFloorsByID.clear();
		childControls.clear();
	}

	@Override
	public Iterator<GLGraphFloorControl> fullIterator() {
		return allFloorsByID.values().iterator();
	}

	GLGraphFloorControl getFloorControl( Integer floorID ) {
		return this.allFloorsByID.get( floorID );
	}
	
	public void showOnlyFloor( Integer floorID ) {
		childControls.clear();
		childControls.add( allFloorsByID.get( floorID ) );
	}

	public void showAllFloors() {
		childControls.clear();
		childControls.addAll( allFloorsByID.values() );
	}
}