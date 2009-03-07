/**
 * Class GLGraphControl
 * Erstellt 02.05.2008, 18:44:28
 */
package gui.visualization.control.graph;

import ds.graph.GraphVisualizationResult;
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