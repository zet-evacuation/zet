/*
 * Created on 18.06.2008
 *
 */
package gui.visualization.control.building;

import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.AbstractControl;
import gui.visualization.control.GLControl;
import gui.visualization.draw.building.GLWall;
import io.visualization.BuildingResults;
import io.visualization.BuildingResults.Wall;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import opengl.drawingutils.GLVector;

/**
 * @author Daniel Pluempe
 */
public class GLWallControl extends AbstractControl<GLWall, BuildingResults.Wall, BuildingResults, GLWall, GLWallControl> {

	private LinkedList<GLVector> basePoints;

	/**
	 * @param controlled
	 * @param visResult
	 * @param mainControl
	 */
	public GLWallControl( Wall controlled, BuildingResults visResult, GLControl mainControl ) {
		super( controlled, visResult, mainControl );
		basePoints = new LinkedList<GLVector>();
		final int floor = getControlled().getFloor().id();
		final double height = (floor - 1) * (VisualizationOptionManager.getFloorDistance() + VisualizationOptionManager.getFloorHeight());

		for( Point2D.Double point : getControlled() ) {
			basePoints.add( new GLVector( point.x, (-1) * point.y, height ) );
		}

		this.setView( new GLWall( this ) );
		mainControl.wallProgress();
	}

	public List<GLVector> getBasePoints() {
		return Collections.unmodifiableList( basePoints );
	}
	
	/**
	 * Checks if the room is on the left side of the wall.
	 * @return true if the room is on the left side, false otherwise.
	 */
	public boolean isRoomLeft() {
		return getControlled().isRoomIsLeft();
	}

	/**
	 * Checks if the room is on the right side of the wall.
	 * @return true if the room is on the right side, false otherwise
	 */
	public boolean isRoomRight() {
		return getControlled().isRoomIsRight();
	}
	
	/**
	 * Returns the {@link Wall.WallType} of the controlled class.
	 * @param segmentNumber the segment of the wall which type should be returned
	 * @return the wall type of the wall segment in the controlled class.
	 */
	public Wall.WallType getWallType( int segmentNumber ) {
		return getControlled().getWallType( segmentNumber );
	}
}
