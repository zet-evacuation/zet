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
package gui.visualization.control.ca;

import algo.ca.util.PotentialUtils;
import ds.ca.PotentialManager;
import ds.ca.Room;
import ds.ca.Cell;
import ds.ca.DoorCell;
import ds.ca.ExitCell;
import ds.ca.RoomCell;
import ds.ca.SaveCell;
import ds.ca.StairCell;
import ds.ca.StaticPotential;
import io.visualization.CAVisualizationResults;
import gui.visualization.control.GLControl.CellInformationDisplay;
import gui.visualization.control.StepUpdateListener;
import gui.visualization.draw.ca.GLCell;
import gui.visualization.draw.ca.GLDelayCell;
import gui.visualization.draw.ca.GLEvacuationCell;
import gui.visualization.draw.ca.GLIndividual;
import gui.visualization.draw.ca.GLSaveCell;
import gui.visualization.draw.ca.GLStairCell;
import gui.visualization.util.Tuple;
import opengl.drawingutils.GLColor;
import de.tu_berlin.math.coga.common.util.Direction;
import gui.visualization.control.AbstractZETVisualizationControl;
import statistic.ca.CAStatistic;

//public class GLCellControl extends AbstractControl<GLCell, Cell, CAVisualizationResults, GLCell, GLCellControl, GLControl> implements StepUpdateListener {
public class GLCellControl extends AbstractZETVisualizationControl<GLCellControl, GLCell, GLCellularAutomatonControl> implements StepUpdateListener {

	private int floorID;
	private GLRoomControl glRoomControlObject;  // the corresponding GLRoomControl of this object
	private static PotentialManager pm = null;
	private double xPosition;
	private double yPosition;
	private static StaticPotential mergedPotential = null;
	private static StaticPotential activePotential = null;
	private static long MAX_DYNAMIC_POTENTIAL = -1;
	private CellInformationDisplay displayMode = CellInformationDisplay.STATIC_POTENTIAL;
	private Cell controlled;

	public static void invalidateMergedPotential() {
		mergedPotential = null;
	}

	CAStatistic statistic;

	public GLCellControl( CAVisualizationResults caVisResults, Cell cell, GLRoomControl glRoomControl, GLCellularAutomatonControl glControl ) {
		super( glControl );
		this.statistic = caVisResults.statistic;
		this.controlled = cell;
		xPosition = caVisResults.get( cell ).x;
		yPosition = caVisResults.get( cell ).y;
		this.glRoomControlObject = glRoomControl;
		if( mergedPotential == null ) {
			pm = caVisResults.getRecording().getInitialConfig().getPotentialManager();
			//pm = getVisResult().getRecording().getInitialConfig().getPotentialManager();
			mergedPotential = PotentialUtils.mergePotentials( pm.getStaticPotentials() );
			activePotential = mergedPotential;
		}
		MAX_DYNAMIC_POTENTIAL = caVisResults.getRecording().getMaxDynamicPotential();

		floorID = controlled.getRoom().getFloorID();

		GLCell gLCell = null;
		if( cell instanceof DoorCell || cell instanceof RoomCell )
			if( cell.getSpeedFactor() == RoomCell.STANDARD_ROOMCELL_SPEEDFACTOR )
				gLCell = new GLCell( this );
			else
				gLCell = new GLDelayCell( this );
		else if( cell instanceof ExitCell )
			gLCell = new GLEvacuationCell( this );
		else if( cell instanceof SaveCell )
			gLCell = new GLSaveCell( this );
		else if( cell instanceof StairCell )
			gLCell = new GLStairCell( this );
		else
			throw new java.lang.IllegalStateException( "Illegal Cell Type" );
		this.setView( gLCell );
		glControl.cellProgress();
	}

	public GLIndividual getDrawIndividual() {
		return mainControl.getControlledGLIndividual( controlled.getIndividual().getNumber() );
	}

	public int getFloorID() {
		return floorID;
	}

	public double getXPosition() {
		return xPosition;
	}

	public double getYPosition() {
		return -yPosition;
	}

	/**
	 * Returns the x-coordinate of the cell relative to the room.
	 * @return the x-coordinate.
	 */
	public int getX() {
		return controlled.getX();
	}

	/**
	 * Returns the y-coordiante of the cell relative to the room.
	 * @return the y-coordinate
	 */
	public int getY() {
		return controlled.getY();
	}

	// Ich denke daß man das nicht benötigt ;) Aber mal sehen...
	public Room getRoom() { // Room eigentlich DS-interne Klasse?!? Wie sonst Zellen zu Rooms zuordnen?
		return controlled.getRoom();
	}

	// das bräuchte man eigentlich auch nicht, oder?
	public String getFloor() {
		return controlled.getRoom().getFloor();
	}

	public double getSpeedFactor() {
		return controlled.getSpeedFactor();
	}

	/**
	 * Returns the corresponding GLControlRoom-Object which created this GLCellControl Object.
	 * @return the corresponding GLControlRoom-Object which created this GLCellControl Object.
	 */
	public GLRoomControl getGLControlRoom() {
		return this.glRoomControlObject;
	}

	public void stepUpdate() {
		// Update the floor colors if in an mode that can change every step
		if( displayMode == CellInformationDisplay.DYNAMIC_POTENTIAL || displayMode == CellInformationDisplay.UTILIZATION || displayMode == CellInformationDisplay.WAITING )
			getView().update();
	}

	/**
	 * Returns the absolute position of the cell in the graphics world
	 * @return the absolute position. of the cell in the graphics world
	 */
	public Tuple getAbsolutePosition() {
		return new Tuple( this.getXPosition() + this.getGLControlRoom().getXPosition() + this.getGLControlRoom().getGLCAFloorControl().getXPosition(), this.getYPosition() + this.getGLControlRoom().getYPosition() + this.getGLControlRoom().getGLCAFloorControl().getYPosition() );
	}

	public static void setActivePotential( StaticPotential activePotential ) {
		GLCellControl.activePotential = activePotential;
	}

	public static StaticPotential getMergedPotential() {
		return mergedPotential;
	}

	/**
	 * Returns some values that can be used to display the status of the controlled
	 * cell, such as potentials or utilization.
	 * @param cid the status type that is used
	 * @return the value of t he status at the current time
	 */
	public long getCellInformation( CellInformationDisplay cid ) {
		switch( cid ) {
			case DYNAMIC_POTENTIAL:
				return pm.getDynamicPotential().getPotential( controlled );
			case STATIC_POTENTIAL:
				return activePotential.getPotential( controlled );
			case UTILIZATION:
				//return 0;
				// TODO statistic visualization
				//return mainControl.getCAStatistic().getCellStatistic().getCellUtilization( controlled, (int) mainControl.getStep() );
				return statistic.getCellStatistic().getCellUtilization( controlled, (int) mainControl.getStep() );
			case WAITING:
				//return 0;
				// TODO statistic visualization
				//return mainControl.getCAStatistic().getCellStatistic().getCellWaitingTime( controlled, (int) mainControl.getStep() );
				return statistic.getCellStatistic().getCellWaitingTime( controlled, (int) mainControl.getStep() );
			default:
				return 0;
		}
	}

	/**
	 * Returns the maximal values that are reached at the current time for some
	 * status properties of the controlled cell, such as potentials or utilization.
	 * @param cid the status type that is used
	 * @return the maximal value of the status at the current time
	 */
	public long getMaxCellInformation( CellInformationDisplay cid ) {
		switch( cid ) {
			case DYNAMIC_POTENTIAL:
				return MAX_DYNAMIC_POTENTIAL;
			case STATIC_POTENTIAL:
				return activePotential.getMaxPotential();
			case UTILIZATION:
				return 0;
				// TODO opengL statistic visualization
				//return mainControl.getCAStatistic().getCellStatistic().getMaxUtilization();
			case WAITING:
				return 0;
				// TODO opengL statistic visualization
				//return mainControl.getCAStatistic().getCellStatistic().getMaxWaiting();
			default:
				return 0;
		}
	}

	public CellInformationDisplay getDisplayMode() {
		return displayMode;
	}

	public boolean isPotentialValid() {
		return activePotential.hasValidPotential( controlled );
	}

	void setPotentialDisplay( CellInformationDisplay potentialDisplay ) {
		displayMode = potentialDisplay;
	}

	/**
	 * Creates a mixed colour for the cell. The direction indicates for which edge
	 * of the cell the colour is calculated.
	 * @param direction the edge of the cell
	 * @return the mixed color for that edge
	 */
	public GLColor mixColorWithNeighbours( Direction direction ) {
		//Cell cell = getControlled();

		Cell[] c = new Cell[3];
		GLCellControl cc;
		double r = getView().getColor().getRed();
		double g = getView().getColor().getGreen();
		double b = getView().getColor().getBlue();
		int count = 1;


		switch( direction ) {
			case TopLeft:
				c[0] = controlled.getNeighbour( Direction.Top );
				c[1] = controlled.getNeighbour( Direction.TopLeft );
				c[2] = controlled.getNeighbour( Direction.Left );
				break;
			case TopRight:
				c[0] = controlled.getNeighbour( Direction.Top );
				c[1] = controlled.getNeighbour( Direction.TopRight );
				c[2] = controlled.getNeighbour( Direction.Right );
				break;
			case DownRight:
				c[0] = controlled.getNeighbour( Direction.Down );
				c[1] = controlled.getNeighbour( Direction.DownLeft );
				c[2] = controlled.getNeighbour( Direction.Left );
				break;
			case DownLeft:
				c[0] = controlled.getNeighbour( Direction.Down );
				c[1] = controlled.getNeighbour( Direction.DownRight );
				c[2] = controlled.getNeighbour( Direction.Right );
				break;
			default:
				return new GLColor( 1, 1, 1 );
		}
		for( int i = 0; i < 3; i++ )
			if( c[i] != null ) {
				count++;
				cc = getGLControlRoom().getCellControl( c[i] );
				r += cc.getView().getColor().getRed();
				g += cc.getView().getColor().getGreen();
				b += cc.getView().getColor().getBlue();
			}
		r = r / count;
		g = g / count;
		b = b / count;
		return new GLColor( r, g, b, 1 );
	}
}
