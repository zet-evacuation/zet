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

/*
 * GLBuildingControl.java
 * Created on 19.06.2008
 */
package gui.visualization.control.building;

import gui.visualization.control.AbstractZETVisualizationControl;
import java.util.ArrayList;
import java.util.HashMap;

import io.visualization.BuildingResults;
import io.visualization.BuildingResults.Wall;
import gui.visualization.control.GLControl;
import gui.visualization.draw.building.GLBuilding;

/**
 * A control class that allows hiding and showing of walls on different floors.
 * @author Jan-Philipp Kappmeier, Daniel Plümpe
 */
//public class GLBuildingControl extends AbstractControl<GLBuilding, BuildingResults, BuildingResults, GLWall, GLWallControl, GLControl> {
public class GLBuildingControl extends AbstractZETVisualizationControl<GLWallControl, GLBuilding> {

	private HashMap<Integer, ArrayList<GLWallControl>> allFloorsByID;

	/**
	 * Creates a new object of this control class. The wall objects (a control and
	 * the corrisponding view object) are created and stored in datastructures to
	 * easy assign them by their floor id. Note that no default floor is enabled!
	 * @param visResult
	 * @param mainControl
	 */
	public GLBuildingControl( BuildingResults visResult, GLControl mainControl ) {
		super( mainControl );
		allFloorsByID = new HashMap<Integer, ArrayList<GLWallControl>>();
		for( Wall wall : visResult.getWalls() ) {
				if( !allFloorsByID.containsKey( wall.getFloor().id() ) )
					allFloorsByID.put( wall.getFloor().id(), new ArrayList<GLWallControl>() );
				final GLWallControl child = new GLWallControl( wall, mainControl );
				add( child );
				allFloorsByID.get( wall.getFloor().id() ).add( child );
		}
		setView( new GLBuilding( this ) );
		for( GLWallControl wall : this )
			view.addChild( wall.getView() );
	}

	/**
	 * Enables the walls on a specified floor only.
	 * @param floorID the specified floor as its id in the visual results.
	 */
	public void showOnlyFloor( Integer floorID ) {
		childControls.clear();
		ArrayList<GLWallControl> floor = allFloorsByID.get( floorID );
		if( floor != null ) {
			childControls.addAll( floor );
		}
		getView().update();
	}

	/**
	 * Enables the walls on all floors.
	 */
	public void showAllFloors() {
		childControls.clear();
		for( ArrayList<GLWallControl> floor : allFloorsByID.values() )
			childControls.addAll( floor );
		getView().update();
	}
	
	/**
	 * Hides all walls.
	 */
	public void hideAll() {
		childControls.clear();
		getView().update();
	}
}
