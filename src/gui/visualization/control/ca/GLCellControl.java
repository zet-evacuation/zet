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
import gui.visualization.control.AbstractControl;
import gui.visualization.control.GLControl.CellInformationDisplay;
import gui.visualization.control.GLControl;
import gui.visualization.control.StepUpdateListener;
import gui.visualization.draw.ca.GLCell;
import gui.visualization.draw.ca.GLDelayCell;
import gui.visualization.draw.ca.GLEvacuationCell;
import gui.visualization.draw.ca.GLIndividual;
import gui.visualization.draw.ca.GLSaveCell;
import gui.visualization.draw.ca.GLStairCell;
import gui.visualization.util.Tuple;
import opengl.drawingutils.GLColor;
import util.Direction;

public class GLCellControl extends AbstractControl<GLCell, Cell, CAVisualizationResults, GLCell, GLCellControl> implements StepUpdateListener {
	private int floorID;
	
	private GLRoomControl glRoomControlObject;  // the corresponding GLRoomControl of this object
	private static PotentialManager pm = null;
					
	private double xPosition;
	private double yPosition;
	
	private static StaticPotential mergedPotential = null;
	private static StaticPotential activePotential = null;
	
	private static long MAX_DYNAMIC_POTENTIAL = -1;
	
	private CellInformationDisplay displayMode = CellInformationDisplay.STATIC_POTENTIAL;
	
	public static void invalidateMergedPotential() {
		mergedPotential = null;
	}
	
	public GLCellControl( CAVisualizationResults caVisResults, Cell cell, GLRoomControl glRoomControl, GLControl glControl ){ 
		super( cell, caVisResults, glControl );
		xPosition = caVisResults.get(cell).x;
		yPosition = caVisResults.get(cell).y;
		this.glRoomControlObject = glRoomControl;
		if( mergedPotential == null ) {
			pm = getVisResult().getRecording().getInitialConfig().getPotentialManager();
			mergedPotential = PotentialUtils.mergePotentials( pm.getStaticPotentials() );
			activePotential = mergedPotential;
		}
		MAX_DYNAMIC_POTENTIAL = caVisResults.getRecording().getMaxDynamicPotential();
		
		floorID = getControlled().getRoom().getFloorID();

		GLCell gLCell = null;
		if( cell instanceof DoorCell || cell instanceof RoomCell ) {
			if( cell.getSpeedFactor() == RoomCell.STANDARD_ROOMCELL_SPEEDFACTOR )
				gLCell = new GLCell( this );
			else
				gLCell = new GLDelayCell( this );
		} else if( cell instanceof ExitCell )
			gLCell = new GLEvacuationCell( this );
		else if( cell instanceof SaveCell)
			gLCell = new GLSaveCell( this );
		else if( cell instanceof StairCell )
			gLCell = new GLStairCell( this );
		else
			throw new java.lang.IllegalStateException( "Illegal Cell Type" );
		this.setView( gLCell );
		glControl.cellProgress();
	}

	public GLIndividual getDrawIndividual() {
		return mainControl.getIndividualControl( getControlled().getIndividual().getNumber() );
	}
	
	public int getFloorID() {
		return floorID;
	}
	
	public double getXPosition(){
	    return xPosition;
	}
	
	public double getYPosition(){
	    return -yPosition;
	}
	
	/**
	 * Returns the x-coordinate of the cell relative to the room.
	 * @return the x-coordinate.
	 */
    public int getX() {
    	return getControlled().getX();
    }
    
	/**
	 * Returns the y-coordiante of the cell relative to the room.
	 * @return the y-coordinate
	 */
    public int getY() {
    	return getControlled().getY();
    }
    
		// Ich denke daß man das nicht benötigt ;) Aber mal sehen...
    public Room getRoom() { // Room eigentlich DS-interne Klasse?!? Wie sonst Zellen zu Rooms zuordnen?
    	return getControlled().getRoom();
    }
    
		// das bräuchte man eigentlich auch nicht, oder?
    public String getFloor() {
    	return getControlled().getRoom().getFloor();
    }
    
    public double getSpeedFactor() {
    	return getControlled().getSpeedFactor();
    }
	
	/**
	 * Returns the corresponding GLControlRoom-Object which created this GLCellControl Object.
	 * @return the corresponding GLControlRoom-Object which created this GLCellControl Object.
	 */
	public GLRoomControl getGLControlRoom() {
		return this.glRoomControlObject;
	}
	
	public void stepUpdate( ) {
		// Update the floor colors if in an mode that can change every step
		if( displayMode == CellInformationDisplay.DYNAMIC_POTENTIAL || displayMode == CellInformationDisplay.UTILIZATION || displayMode == CellInformationDisplay.WAITING )
			getView().update();
	}
	
	/**
	 * Returns the absolute position of the cell in the graphics world
	 * @return
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
				return pm.getDynamicPotential().getPotential( getControlled() );
			case STATIC_POTENTIAL:
				return activePotential.getPotential(getControlled());
			case UTILIZATION:
				return mainControl.getCAStatistic().getCellStatistic().getCellUtilization( getControlled(), (int)mainControl.getCaStep() );
			case WAITING:
				return mainControl.getCAStatistic().getCellStatistic().getCellWaitingTime( getControlled(), (int)mainControl.getCaStep() );
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
				return mainControl.getCAStatistic().getCellStatistic().getMaxUtilization();
			case WAITING:
				return mainControl.getCAStatistic().getCellStatistic().getMaxWaiting();
			default:
				return 0;
		}		
	}
	
	public CellInformationDisplay getDisplayMode() {
		return displayMode;
	}

	public boolean isPotentialValid() {
		return activePotential.hasValidPotential( getControlled() );
	}

	void setPotentialDisplay( CellInformationDisplay potentialDisplay ) {
		displayMode = potentialDisplay;
	}
	
	public GLColor bla( Direction direction ) {
		Cell cell = getControlled();
		
		Cell[] c = new Cell[3];
		GLCellControl cc;
		double r = getView().getColor().getRed();
		double g = getView().getColor().getGreen();
		double b = getView().getColor().getBlue();
		int count = 1;
		
		
		switch( direction ) {
			case UPPER_LEFT:
				c[0] = cell.getNeighbour( Direction.UP );
				c[1] = cell.getNeighbour( Direction.UPPER_LEFT );
				c[2] = cell.getNeighbour( Direction.LEFT );
				break;
			case UPPER_RIGHT:
				c[0] = cell.getNeighbour( Direction.UP );
				c[1] = cell.getNeighbour( Direction.UPPER_RIGHT );
				c[2] = cell.getNeighbour( Direction.RIGHT );
				break;
			case LOWER_RIGHT:
				c[0] = cell.getNeighbour( Direction.DOWN );
				c[1] = cell.getNeighbour( Direction.LOWER_LEFT );
				c[2] = cell.getNeighbour( Direction.LEFT );
				break;
			case LOWER_LEFT:
				c[0] = cell.getNeighbour( Direction.DOWN );
				c[1] = cell.getNeighbour( Direction.LOWER_RIGHT );
				c[2] = cell.getNeighbour( Direction.RIGHT );
				break;
			default:
				return new GLColor( 1, 1, 1 );
		}
		for( int i = 0; i < 3; i++) {
			if( c[i] != null ) {
				count++;
				cc = getGLControlRoom().getCellControl( c[i] );
				r += cc.getView().getColor().getRed();
				g += cc.getView().getColor().getGreen();
				b += cc.getView().getColor().getBlue();
			}
		}
		r = r / count;
		g = g / count;
		b = b / count;
		return new GLColor( r, g, b, 1 );
	}
}