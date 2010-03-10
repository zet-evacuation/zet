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

import batch.tasks.AlgorithmTask;
import de.tu_berlin.math.coga.common.localization.Localization;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.draw.building.GLBuilding;
import io.visualization.BuildingResults;
import io.visualization.BuildingResults.Wall;
import java.util.ArrayList;
import java.util.HashMap;
import opengl.framework.abs.Controlable;

/**
 * A control class that allows hiding and showing of walls on different floors.
 * @author Jan-Philipp Kappmeier, Daniel Plümpe
 */
//public class GLBuildingControl extends AbstractControl<GLBuilding, BuildingResults, BuildingResults, GLWall, GLWallControl, GLControl> {
public class GLBuildingControl extends AbstractZETVisualizationControl<GLWallControl, GLBuilding, GLBuildingControl> implements Controlable {

	private int wallCount;
	private int wallsDone;
	private HashMap<Integer, ArrayList<GLWallControl>> allFloorsByID;

	/**
	 * Creates a new object of this control class. The wall objects (a control and
	 * the corrisponding view object) are created and stored in datastructures to
	 * easy assign them by their floor id. Note that no default floor is enabled!
	 * @param visResult
	 * @param mainControl
	 */
	public GLBuildingControl( BuildingResults visResult ) {
		super();
		mainControl = this;
		AlgorithmTask.getInstance().setProgress( 1, Localization.getInstance().getStringWithoutPrefix( "batch.tasks.progress.createBuildingVisualizationDataStructure" ), "" );
		wallCount = visResult.getWalls().size();
		wallsDone = 0;
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
		if( floor != null )
			childControls.addAll( floor );
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

	/**
	 * <p>This method increases the number of cells that are created and
	 * calculates a new progress. The progress will at most reach 99% so that
	 * after all objects are created a final "Done" message can be submitted.</p>
	 * <p>Note that before this method can be used in the proper way the private
	 * variable <code>wallsDone</code> and <code>WallCount</code> should be
	 * initialized correct. However, it is guaranteed to calculate a value from
	 * 0 to 99.
	 */
	public void wallProgress() {
		wallsDone++;
		int progress = Math.max( 0, Math.min( (int) Math.round( ((double) wallsDone / wallCount) * 100 ), 99 ) );
		AlgorithmTask.getInstance().setProgress( progress, "Erzeuge Gebäude...", "Wand " + wallsDone + " von " + wallCount + " erzeugt." );
	}

	/**
	 * Does nothing, as the building is static at the moment.
	 * @param timeNanoSeconds the time that has passed.
	 */
	@Override
	public final void addTime( long timeNanoSeconds ) {
	}

	/**
	 * Returns {@code true} as the building is static.
	 * @return {@code true}
	 */
	public final boolean isFinished() {
		return true;
	}

	/**
	 * Does nothing, as the building is static at the moment.
	 * @param timeNanoSeconds the time that has passed.
	 */
	@Override
	public void setTime( long time ) {
	}

	/**
	 * Does nothing, as the building is static at the moment.
	 * @param timeNanoSeconds the time that has passed.
	 */
	@Override
	public void resetTime() {
	}
}
