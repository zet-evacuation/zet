/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * Created 18.06.2008
 *
 */
package gui.visualization.control.building;

import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.draw.building.GLWall;
import io.visualization.BuildingResults.Wall;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.zetool.opengl.drawingutils.GLVector;
import org.zetool.opengl.framework.abs.Controlable;

/**
 * @author Daniel R. Schmidt, Jan-Philipp Kappmeier
 */
public class GLWallControl extends AbstractZETVisualizationControl<GLWallControl, GLWall, GLBuildingControl> implements Controlable {

	private LinkedList<GLVector> basePoints;
	Wall controlled;

	/**
	 * @param controlled
	 * @param mainControl
	 */
	public GLWallControl( Wall controlled, GLBuildingControl mainControl ) {
		super( mainControl );
		this.controlled = controlled;
		basePoints = new LinkedList<>();
		final int floor = controlled.getFloor().id();
		final double height = floor * VisualizationOptionManager.getFloorDistance();

		for( Point2D.Double point : controlled ) {
			basePoints.add( new GLVector( point.x * mainControl.scaling, (-1) * point.y * mainControl.scaling, height * mainControl.scaling ) );
		}

		this.setView( new GLWall( this ) );
		mainControl.wallProgress();
	}

	public List<GLVector> getBasePoints() {
		return Collections.unmodifiableList( basePoints );
	}

	public boolean isBarrier() {
		return controlled.isBarrier();
	}
	
	/**
	 * Checks if the room is on the left side of the wall.
	 * @return true if the room is on the left side, false otherwise.
	 */
	public boolean isRoomLeft() {
		return controlled.isRoomLeft();
	}

	/**
	 * Checks if the room is on the right side of the wall.
	 * @return true if the room is on the right side, false otherwise
	 */
	public boolean isRoomRight() {
		return controlled.isRoomRight();
	}
	
	/**
	 * Returns the {@link io.visualization.BuildingResults.Wall.ElementType} of
	 * the controlled class.
	 * @param segmentNumber the segment of the wall which type should be returned
	 * @return the wall type of the wall segment in the controlled class.
	 */
	public Wall.ElementType getWallType( int segmentNumber ) {
		return controlled.getWallType( segmentNumber );
	}

	@Override
	public void addTime( long timeNanoSeconds ) { }

	@Override
	public void setTime( long time ) { }

	@Override
	public void resetTime() { }

	@Override
	public void delete() {
		controlled = null;
		view.delete();
	}

	@Override
	public boolean isFinished() {
		return true;
	}
}
