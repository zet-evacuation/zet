package ds.ca.results;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import ds.ca.Cell;
import ds.ca.CellularAutomaton;
import ds.ca.DoorCell;
import ds.ca.DynamicPotential;
import ds.ca.PotentialManager;
import ds.ca.InitialConfiguration;
import ds.ca.Room;
import ds.ca.StaticPotential;
import gui.EditorStart;

/**
 * This class helps you to store all the parts of a simulation that are
 * needed to visualize the simulation at a later point. This is done by
 * recording <code>Action</code>s to a <code>VisualResultsRecording</code>-Object.
 * The actions are stored together with a full deep clone of the initial configuration 
 * of your cellular automaton. The cloning is done in the constructor of this 
 * class, so the automaton is saved in the state that it has when you <b>create</b> 
 * the <code> VisualResultsRecorder</code>. 
 * 
 * Usage: Build your initial configuration and THEN instantiate a new
 * VisualResultsRecorder with it. Call <code>startRecording()</code>. 
 * You can now record Actions by calling <code>recordAction</code>. 
 * The recorded actions are stored based on the simulation time when they occur, 
 * starting at time <code>t=0</code>. Every time your simulation time advances, 
 * call <code>nextTimeStep()</code>. Thus, all actions that are recorded 
 * afterwards will be stored at time <code>t+1</code>. 
 * All actions are stored in the order of their recording.
 * 
 * To replay the simulation, call <code>getRecording()</code> to get all 
 * recorded actions nicely packed in a <code>VisualResultsRecording</code>.
 * 
 * @author Daniel Pluempe
 *
 */
@XStreamAlias( "visual_results" )
public class VisualResultsRecorder {

	/** 
	 * The current simulation time. Serves as a time stamp for the
	 * storage of actions.
	 * */
	@XStreamOmitField
	private int timeStep;
	private static VisualResultsRecorder instance = null;
	/**
	 * A clone of the initial configuration. All actions are stored
	 * based on this configuration. 
	 */
	@XStreamAlias( "initial_configuration" )
	private InitialConfiguration clonedInitialConfig;
	private CellularAutomaton clonedCA;
	/**
	 * Stores a vector of actions for every time step
	 * Each vector holds the actions for its time step in the
	 * order of occurrence.
	 */
	private Vector<Vector<Action>> actions;
	/**
	 * This maps the cells in the original initial configuration to the
	 * corresponding cells in the cloned initial configuration. 
	 * We need this to convert  the incoming actions (which are based on 
	 * the original configuration) to the stored actions (which are based 
	 * on the cloned configuration). 
	 */
	@XStreamOmitField
	private HashMap<Cell, Cell> cellMap;
	@XStreamOmitField
	private HashMap<StaticPotential, StaticPotential> staticPotentialMap;
	private boolean doRecord;

	protected VisualResultsRecorder() {
		reset();
	}

	/**
	 * Creates a new instance of the VisualResultsRecorder and does a deep
	 * copy of the initial configuration for storage. Sets current time step
	 * to zero. 
	 * @param initialConfig The initial configuration of the cellular
	 * automaton. Will be cloned as is for storage.
	 */
	public VisualResultsRecorder( InitialConfiguration initialConfig ) {
		this();
		setInitialConfiguration( initialConfig );
	}

	public void startRecording() {
		if( clonedInitialConfig == null ) {
			throw new RuntimeException( "The initial configuration has not yet been set. " +
							"Please call setInitialConfiguration() at least once before using this method." );

		}
		this.doRecord = true;
	}

	public void stopRecording() {
		this.doRecord = false;
	}

	public void reset() {
		this.actions = new Vector<Vector<Action>>();
		this.actions.add( new Vector<Action>() );
		this.timeStep = 0;
		this.cellMap = new HashMap<Cell, Cell>();
		this.staticPotentialMap = new HashMap<StaticPotential, StaticPotential>();
		this.clonedInitialConfig = null;
		this.clonedCA = null;
		stopRecording();
	}

	public void setInitialConfiguration( InitialConfiguration initialConfig ) {
		reset();
		this.clonedInitialConfig = cloneConfig( initialConfig, cellMap, staticPotentialMap );
		this.clonedCA = new CellularAutomaton( clonedInitialConfig );
	}

	public InitialConfiguration getInitialConfiguration() {
		return clonedInitialConfig;
	}

	/**
	 * Records an action at the current time step. The action should 
	 * refer to cells in the original initial configuration passed to
	 * the constructor of this class. The method will convert it 
	 * automatically to match the cloned configuration (which has clones
	 * of the original cells). 
	 * The method throws <code>IllegalArgumentExceptions</code> if you
	 * try to pass an action that does not refer to the original configuration.
	 * 
	 * @param action An action that you want to record. The parameters of
	 * the action should refer to the original configuration.
	 */
	public void recordAction( Action action ) throws Action.CADoesNotMatchException {
		if( !EditorStart.useVisualization )
			return;
		if( doRecord ) {
			Action adoptedAction = action.adoptToCA( this.clonedCA );
			actions.get( timeStep ).add( adoptedAction );
		}
	}

	/**
	 * Increases the current time stamp by one. All actions recorded afterwards
	 * will be stored at the new time stamp.
	 */
	public void nextTimestep() {
		if( doRecord ) {
			timeStep++;
			actions.add( new Vector<Action>() );
		}
	}

	/**
	 * Get the current time step.
	 * @return The time step at which all actions are currently being recorded.
	 */
	public int getTimeStep() {
		return timeStep;
	}

	/**
	 * Get the result of the recording. This will include the clone of
	 * the initial configuration and all actions ordered by time steps
	 * and in the order of their recording. The references to cells and
	 * individuals in the returned actions refer to objects in the 
	 * cloned configuration. A new recording is constructed each time
	 * you call this method.
	 * @return A new <code>VisualResultsRecording</code> containing all 
	 * recorded actions and the corresponding configuration. 
	 */
	public VisualResultsRecording getRecording() {
		return new VisualResultsRecording( clonedInitialConfig, actions );
	}

	public static VisualResultsRecorder getInstance() {
		if( instance == null ) {
			instance = new VisualResultsRecorder();
		}

		return instance;
	}

	/**
	 * This does a deep copy of an initial configuration for storage. 
	 * All objects contained in the configuration are copied. Also, all
	 * references are updated, so that they refer to the copies rather
	 * than to the original objects. Thus, the copy is completely 
	 * independent of the original.
	 * 
	 * @param orig The initial configuration that you want to clone
	 * @param cellMapping A HashMap mapping the cells in the original configuration
	 * to their copies. If this is <code>null</code>, a new HashMap will be 
	 * created. Else, the mapping is added to the existing entries in the
	 * HashMap. Existing entries may be overwritten.
	 * @return A deep copy of <code>orig</code>.
	 */
	private static InitialConfiguration cloneConfig( InitialConfiguration orig,
					HashMap<Cell, Cell> cellMapping, HashMap<StaticPotential, StaticPotential> potentialMapping ) {

		if( cellMapping == null ) {
			cellMapping = new HashMap<Cell, Cell>();
		}


		// First of all, clone all rooms and update the door links
		// Store cloned rooms in clonedRooms
		Vector<Room> clonedRooms = new Vector<Room>();
		// This maps the original rooms to the cloned ones
		HashMap<Room, Room> roomMap = new HashMap<Room, Room>();

		// Clone the rooms and store the mapping between the
		// original and the cloned cells
		for( Room room : orig.getRooms() ) {
			HashMap<Cell, Cell> addMapping = new HashMap<Cell, Cell>();
			Room clone = cloneRoom( room, addMapping );
			clonedRooms.add( clone );
			roomMap.put( room, clone );
			cellMapping.putAll( addMapping );
		}

		for( Room room : clonedRooms ) {
			for( DoorCell door : room.getDoors() ) {
				//door.removeAllNextDoors();
				door.removeAllNextDoorsWithoutNotify();
			}
		}

		// To update the door links, iterate over all doors and
		// move the nextDoor-link of each cloned door to the clone of
		// the original nextDoor
		for( Room room : orig.getRooms() ) {
			for( DoorCell oldDoor : room.getDoors() ) {
				DoorCell clonedDoor = (DoorCell) cellMapping.get( oldDoor );
				for( int i = 0; i < oldDoor.nrOfNextDoors(); i++ ) {
					DoorCell oldNextDoor = oldDoor.getNextDoor( i );
					DoorCell newNextDoor = (DoorCell) cellMapping.get( oldNextDoor );
					clonedDoor.addNextDoor( newNextDoor );
				}
			}
		}

		// Now we can clone the potentials. To do so, iterate over
		// all cells and all potentials. The cells are iterated room
		// by room

		PotentialManager globals = orig.getPotentialManager();
		Collection<StaticPotential> statics = globals.getStaticPotentials();
		PotentialManager clonedGlobals = new PotentialManager();
		if( statics != null ) {
			for( StaticPotential pot : statics ) {
				StaticPotential clone = new StaticPotential();
				clonedGlobals.addStaticPotential( clone );
				potentialMapping.put( pot, clone );
//            }

//            for(StaticPotential pot : statics){
				//StaticPotential clone = clonedGlobals.getStaticPotential(pot.getID());
				for( Room room : orig.getRooms() ) {
					// For every room do:
					for( int x = 0; x < room.getWidth(); x++ ) {
						for( int y = 0; y < room.getHeight(); y++ ) {
							// For every cell in the room do:
							Cell cell = room.getCell( x, y );
							if( cell != null && pot.contains( cell ) ) {
								clone.setPotential( cellMapping.get( cell ), pot.getPotentialDouble( cell ) );
							}
						}
					}
				}
			}
		}

		// At last we need to clone the dynamic potential. This is done by 
		// iterating over all cells again.
		DynamicPotential dynOrig = orig.getPotentialManager().getDynamicPotential();
		DynamicPotential dynClone = new DynamicPotential();
		if( dynOrig != null ) {
			for( Room room : orig.getRooms() ) {
				// For every room do:
				for( int x = 0; x < room.getWidth(); x++ ) {
					for( int y = 0; y < room.getHeight(); y++ ) {
						// For every cell in the room do:
						Cell cell = room.getCell( x, y );
						if( cell != null && dynOrig.contains( cell ) ) {
							dynClone.setPotential( cellMapping.get( cell ), dynOrig.getPotential( cell ) );
						}
					}
				}
			}
		}

		clonedGlobals.setDynamicPotential( dynClone );

		// Now store the new potentials in the individuals:

		for( Room room : orig.getRooms() ) {
			for( int x = 0; x < room.getWidth(); x++ ) {
				for( int y = 0; y < room.getHeight(); y++ ) {
					Cell cell = room.getCell( x, y );
					if( cell != null && cell.getIndividual() != null ) {
						Cell clonedCell = cellMapping.get( cell );
						StaticPotential origPot = cell.getIndividual().getStaticPotential();
						clonedCell.getIndividual().setStaticPotential( potentialMapping.get( origPot ) );
					}
				}
			}
		}

		return new InitialConfiguration( clonedRooms, clonedGlobals, orig.getAbsoluteMaxSpeed() );
	}

	/**
	 * Does a deep copy of a room and updates all internal references.
	 * @param room The room you want to clone
	 * @param cellMapping A HashMap mapping the cells in <code>room</code> to
	 * those in its clone. If this is <code>null</code>, a new mapping is
	 * created. Else, existing entries may be overwritten.
	 * @return A deep copy of <code>room</code>
	 */
	private static Room cloneRoom( Room room, HashMap<Cell, Cell> cellMapping ) {
		if( cellMapping == null ) {
			cellMapping = new HashMap<Cell, Cell>();
		}

		Room rClone = room.clone();
		rClone.clear();
		for( int x = 0; x < room.getWidth(); x++ ) {
			for( int y = 0; y < room.getHeight(); y++ ) {
				Cell orig = room.getCell( x, y );
				if( orig != null ) {
					Cell cClone = cloneCell( orig );
					rClone.setCell( cClone );
					cellMapping.put( orig, cClone );
				}
			}
		}

		return rClone;
	}

	/**
	 * Copies a cell and sets a copy of the original cell's individual
	 * on the copied cell (if present).
	 * @param orig The original cell
	 * @return A cloned cell with a cloned individual
	 */
	private static Cell cloneCell( Cell orig ) {
		Cell clone = (Cell) orig.clone( true );
		return clone;
	}
}

