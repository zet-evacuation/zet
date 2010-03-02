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
package ds.ca;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import ds.ca.Individual.DeathCause;
import ds.ca.results.CAStateChangedAction;
import ds.ca.results.DieAction;
import ds.ca.results.ExitAction;
import ds.ca.results.MoveAction;
import ds.ca.results.SwapAction;
import ds.ca.results.VisualResultsRecorder;
import exitdistributions.IndividualToExitMapping;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import util.DebugFlags;

/**
 * This class represents the structure of the cellular automaton. It holds the individuals, the rooms and the floor fields which are
 * important for the behavior of individuals. It also contains an object of the IndividualCreator which is responsible for
 * creating individuals with random attributes based upon the choices made by the user.  
 * @author Matthias Woste, Jan-Philipp Kappmeier
 */
public class CellularAutomaton implements Iterable<Individual> {

	private boolean recordingStarted;

	/** the state of the cellular automaton. */
	public enum State {
		/** if all values and individuals are set, the simulation can be executed. */
		ready,
		/** if a simulation is running. */
		running,
		/** if a simulation is finished. in this case all individuals are removed or in save areas. */
		finish
	}
	/** an ArrayList of all Individual objects in the cellular automaton. */
	private ArrayList<Individual> individuals;
	/** an ArrayList of all Individual objects, which are already out of the simulation because they are evacuated */
	private ArrayList<Individual> evacuatedIndividuals;
	/** an ArrayList of all Individual objects, which are marked as "dead". */
	private ArrayList<Individual> deadIndividuals;
	/** An <code>ArrayList</code> marked to be removed. */
	private ArrayList<Individual> markedForRemoval;
	/** an ArrayList of all ExitCell objects (i.e. all exits) of the building */
	private ArrayList<ExitCell> exits;
	/** an HashMap used to map rooms to identification numbers */
	private HashMap<Integer, Room> rooms;
	/** A mapping floor-id <-> floor-name */
	private HashMap<Integer, String> floorNames;
	/** A mapping floor <-> rooms */
	private HashMap<Integer, ArrayList<Room>> roomsByFloor;
	/** HashMap mapping Rooms to Individuals. */
	//private HashMap<Room, HashSet<Individual>> roomIndividualMap;
	/** HashMap mapping UUIDs of AssignmentTypes to Individuals. */
	private HashMap<UUID, HashSet<Individual>> typeIndividualMap;
	/** Maps name of an assignment types to its unique id*/
	private HashMap<String, UUID> assignmentTypes;
	/** Maps Unique IDs to individuals */
	private HashMap<Integer, Individual> individualsByID;
	/** A mapping that maps individuals to exits. */
	private IndividualToExitMapping individualToExitMapping;
        /** A mapping that maps exits to their capacity */
        private HashMap<StaticPotential, Double> exitToCapacityMapping;
	/** Reference to all Floor Fields */
	private PotentialManager potentialManager;
	/** The current time step */
	private int timeStep;
	/** The minimal number of steps that is needed until all movements are finished. */
	private int neededTime;
	/** The current state of the cellular automaton, e.g. running, stopped, ... */
	private State state;
	private double absoluteMaxSpeed;
	private double secondsPerStep;
	private double stepsPerSecond;
	private int notSaveIndividualsCount = 0;
	private int initialIndividualCount;

	/**
	 * Constructs a CellularAutomaton object with empty default objects.
	 */
	public CellularAutomaton() {
		timeStep = 0;
		individuals = new ArrayList<Individual>();
		individualsByID = new HashMap<Integer, Individual>();
		evacuatedIndividuals = new ArrayList<Individual>();
		deadIndividuals = new ArrayList<Individual>();
		markedForRemoval = new ArrayList<Individual>();
		exits = new ArrayList<ExitCell>();
		rooms = new HashMap<Integer, Room>();
		assignmentTypes = new HashMap<String, UUID>();
//		roomIndividualMap = new HashMap<Room, HashSet<Individual>>();
		typeIndividualMap = new HashMap<UUID, HashSet<Individual>>();
		roomsByFloor = new HashMap<Integer, ArrayList<Room>>();
		potentialManager = new PotentialManager();
		absoluteMaxSpeed = 1;
		secondsPerStep = 1;
		stepsPerSecond = 1;
		state = State.ready;
		floorNames = new HashMap<Integer, String>();
		recordingStarted = false;
	}

	/**
	 * Creates a <code>Cellularautomaton</code> from an {@link InitialConfiguration}
	 * that is stored in an visual results recorder. This is used to replay a
	 * simulation.
	 * @param initialConfiguration the initial configuration of the simulation.
	 */
	public CellularAutomaton( InitialConfiguration initialConfiguration ) {
		this();
		stopRecording();
		for( Room room : initialConfiguration.getRooms() ) {
			this.addRoom( room );
		}

		for( Room room : initialConfiguration.getRooms() ) {
			for( Cell cell : room.getAllCells() ) {
				// TODO: remove, wird bereits in addRoom gemacht
				//7if( cell instanceof ExitCell ) {
				//	addExit( (ExitCell) cell );
				//7}

				if( cell.getIndividual() != null ) {
					addIndividual( cell, cell.getIndividual() );
				}
			}
		}

		for( StaticPotential staticPot : initialConfiguration.getPotentialManager().getStaticPotentials() ) {
			this.getPotentialManager().addStaticPotential( staticPot );
		}

		this.potentialManager.setDynamicPotential( initialConfiguration.getPotentialManager().getDynamicPotential() );
		setAbsoluteMaxSpeed( initialConfiguration.getAbsoluteMaxSpeed() );
	}

	/**
	 * Returns the number of cells in the whole cellular automaton
	 * @return the number of cells
	 */
	public int getCellCount() {
		int count = 0;
		for( Room room : getRooms() ) {
			count += room.getCellCount( false );
		}
		return count;
	}

	public void startRecording() {
		recordingStarted = true;
		VisualResultsRecorder.getInstance().setInitialConfiguration( new InitialConfiguration( rooms.values(), potentialManager, absoluteMaxSpeed ) );
		VisualResultsRecorder.getInstance().startRecording();
	}

	public void stopRecording() {
		recordingStarted = false;
		VisualResultsRecorder.getInstance().stopRecording();
	}

	/**
	 * Assigns the UUID to the name of the assignment type
	 * @param uid UUID of the assignment type
	 * @param s name of the assignment type
	 */
	public void setAssignmentType( String s, UUID uid ) {
		assignmentTypes.put( s, uid );
	}

	/**
	 * Returns UUID of the assignment type with the given name
	 * @param name of the assignment type
	 * @return uid UUID of the assignment type
	 */
	public UUID getAssignmentUUIS( String name ) {
		return assignmentTypes.get( name );
	}


	public double getAbsoluteMaxSpeed() {
		return absoluteMaxSpeed;
	}

	public HashMap<String, UUID> getAssignmentTypes() {
		return assignmentTypes;
	}

	/**
	 * Sets the maximal speed that any individual can walk. That means an individual
	 * with speed = 1 moves with 100 percent of the absolute max speed.
	 * @param absoluteMaxSpeed
	 * @throws java.lang.IllegalArgumentException if absoluteMaxSpeed is less or equal to zero
	 */
	public void setAbsoluteMaxSpeed( double absoluteMaxSpeed ) throws java.lang.IllegalArgumentException {
		if( absoluteMaxSpeed <= 0 )
			throw new java.lang.IllegalArgumentException( "Maximal speed must be greater than zero!" );
		this.absoluteMaxSpeed = absoluteMaxSpeed;
		this.stepsPerSecond = absoluteMaxSpeed / 0.4;
		this.secondsPerStep = 0.4 / absoluteMaxSpeed;
		if( recordingStarted )
			VisualResultsRecorder.getInstance().getInitialConfiguration().setAbsoluteMaxSpeed( absoluteMaxSpeed );
	}

	/**
	 * Returns the seconds one step needs.
	 * @return the seconds one step needs
	 */
	public double getSecondsPerStep() {
		return secondsPerStep;
	}

	/**
	 * Returns the number of steps performed by the cellular automaton within one
	 * second. The time dependds of the absolute max speed and is set if
	 * {@link #setAbsoluteMaxSpeed(double)} is called.
	 * @return the number of steps performed by the cellular automaton within one second.
	 */
	public double getStepsPerSecond() {
		return stepsPerSecond;
	}

	/**
	 * Returns the absolute speed of an individual in meter per second depending
	 * on its relative speed which is a fraction between zero and one of the
	 * absolute max speed.
	 * @param relativeSpeed
	 * @return the absolute speed in meter per seconds for a given relative speed.
	 */
	public double absoluteSpeed( double relativeSpeed ) {
		return absoluteMaxSpeed * relativeSpeed;
	}
	
	/**
	 * Increases the actual timeStep of the CellularAutomaton about one.
	 */
	public void nextTimeStep() {
		timeStep++;
		VisualResultsRecorder.getInstance().nextTimestep();
	}

	/**
	 * Gets the actual timeStep of the CellularAutomaton.
	 * @return the timeStep
	 */
	public int getTimeStep() {
		return timeStep;
	}

	/**
	 * Returns an ArrayList of all exists of the building
	 * @return the ArrayList of exits
	 */
	public List<ExitCell> getExits() {
		return Collections.unmodifiableList( exits );
	}

	/**
	 * Returns the object which organizes all potentials
	 * @return PotentialManager object
	 */
	public PotentialManager getPotentialManager() {
		return potentialManager;
	}

	/**
	 * Returns all Individuals in the given AssignmentType
	 * @param id UUID of AssignmentType
	 * @return HashSet of Individuals
	 */
	public HashSet<Individual> getIndividualsInAssignmentType( UUID id ) {
		return typeIndividualMap.get( id );
	}

	/**
	 * Returns an ArrayList of all rooms of the building
	 * @return the ArrayList of rooms
	 */
	public Collection<Room> getRooms() {
		return Collections.unmodifiableCollection( rooms.values() );
	}

	/**
	 * Returns the mapping between individuals and exit cells.
	 * @return the mapping between individuals and exit cells
	 */
	public IndividualToExitMapping getIndividualToExitMapping() {
		return individualToExitMapping;
	}

	/**
	 * Sets a mapping between individuals and exit cells
	 * @param individualToExitMapping the mapping
	 */
	public void setIndividualToExitMapping( IndividualToExitMapping individualToExitMapping ) {
		this.individualToExitMapping = individualToExitMapping;
	}

	public HashMap<StaticPotential, Double> getExitToCapacityMapping() {
		return exitToCapacityMapping;
	}

	public void setExitToCapacityMapping( HashMap<StaticPotential, Double> exitToCapacityMapping ) {
		this.exitToCapacityMapping = exitToCapacityMapping;
	}

	/**
	 * Adds an Individual object to the List of all individuals of the cellular
	 * automaton and puts this individual into the two mappings of rooms and
	 * assignment types.
	 * @param c the Cell on which the individual stands
	 * @param i the Individual object
	 * @throws IllegalArgumentException if the the specific individual exits already in the list individuals
	 * @throws IllegalStateException if an individual is added after the simulation has been startet.
	 */
	public void addIndividual( Cell c, Individual i ) throws IllegalArgumentException {
		if( this.state != State.ready )
			throw new IllegalStateException( "Individual added after simulation has started." );
		if( individuals.contains( i ) ) {
			throw new IllegalArgumentException( "Individual with id " + i.id() + " exists already in list individuals." );
		} else {
			individuals.add( i );
			individualsByID.put( i.getNumber(), i );
			if( typeIndividualMap.get( i.getUid() ) == null ) {
				typeIndividualMap.put( i.getUid(), new HashSet<Individual>() );
			}
			if( !typeIndividualMap.get( i.getUid() ).contains( i ) ) {
				typeIndividualMap.get( i.getUid() ).add( i );
			}
		}
		try {
		c.getRoom().addIndividual( c, i );
		} catch( Exception ex ) {
			int k = 1;
			k++;
		}
		
		// assign shortest path potential to individual, so it is not null.
		int currentMin = -1;
		for( StaticPotential sp : potentialManager.getStaticPotentials() ) {
			if( currentMin == -1 ) {
				i.setStaticPotential( sp );
				currentMin = sp.getPotential( c );
			} else if( sp.getPotential( c ) > -1 && sp.getPotential( c ) < currentMin ) {
				currentMin = sp.getPotential( c );
				i.setStaticPotential( sp );
			}
		}
	}


	/**
	 * Removes an individual from the list of all individuals of the building
	 * @param i specifies the Individual object which has to be removed from the list
	 * @throws IllegalArgumentException if the the specific individual does not exist in the list individuals
	 */
	private void removeIndividual( Individual i ) throws IllegalArgumentException {
		i.getCell().removeIndividual();
		//i.getCell().getRoom().removeIndividual( i );
		if( !individuals.remove( i ) )
			throw new IllegalArgumentException( "Specified individual is not in list individuals." );
	}

	/**
	 * Move the individual standing on the "from"-Cell to the "to"-Cell.
	 * @param from The cell on which the individual, which shall be moved, stays.
	 * @param to The destination-cell for the moving individual.
	 * @throws java.lang.IllegalArgumentException if the individual should be moved
	 * from an empty Cell, which is not occupied by an Individual, or if the
	 * ''to''-Cell is already occupied by another individual.
	 */
	public void moveIndividual( Cell from, Cell to ) throws java.lang.IllegalArgumentException {
		if (DebugFlags.EVAPLANCHECKER)
			System.out.println("Individual "+from.getIndividual().id()+" moving from cell ("+from.x+","+from.y+") to cell ("+to.x+","+to.y+")");
		if( from.getIndividual() == null )
			throw new IllegalArgumentException( "No Individual standing on the ''from''-Cell!" );
		if( from.equals( to ) ) {
		  VisualResultsRecorder.getInstance().recordAction(new MoveAction(from, from, from.getIndividual()));
		  return;
		}
		if( to.getIndividual() != null )
			throw new IllegalArgumentException( "Another Individual already standing on the ''to''-Cell!" );
		
		VisualResultsRecorder.getInstance().recordAction(new MoveAction(from, to, from.getIndividual()));
		if( from.getRoom().equals( to.getRoom() ) ) {
			from.getRoom().moveIndividual( from, to );
		} else {
			Individual i = from.getIndividual();
			from.getRoom().removeIndividual( i );
			to.getRoom().addIndividual( to, i );
		}
	}
	
	public void swapIndividuals( Cell cell1, Cell cell2 ) {
		if( DebugFlags.EVAPLANCHECKER )
			System.out.println( "Individual " + cell1.getIndividual().id() + "from cell (" + cell1.x + "," + cell1.y + ")" + "and Individual " + cell2.getIndividual().id() + "from cell (" + cell2.x + "," + cell2.y + ") swap position.");
		if( cell1.getIndividual() == null )
			throw new IllegalArgumentException( "No Individual standing on cell #1!" );
		if( cell2.getIndividual() == null )
			throw new IllegalArgumentException( "No Individual standing on cell #2!" );
		if( cell1.equals( cell2 ) )
			throw new IllegalArgumentException( "The cells are equal. Can't swap on equal cells." );
		if( util.DebugFlags.CA_SWAP ) {
			System.out.println( "Swap findet statt:" );
			System.out.println( "Individual " + cell1.getIndividual().id() + " geht auf "+ cell2.coordToString() );
			System.out.println( "Individual " + cell2.getIndividual().id() + " geht auf "+ cell1.coordToString() );
		}
		VisualResultsRecorder.getInstance().recordAction( new SwapAction( cell1, cell2 ) );
		if( cell1.getRoom().equals( cell2.getRoom() ) ) {
			cell1.getRoom().swapIndividuals( cell1, cell2 );
		} else {
			Individual c1i = cell1.getIndividual();
			Individual c2i = cell2.getIndividual();
			cell1.getRoom().removeIndividual( c1i );
			cell2.getRoom().removeIndividual( c2i );
			cell1.getRoom().addIndividual( cell1, c2i );
			cell2.getRoom().addIndividual( cell2, c1i );
		}
	}

	public Individual getIndividual( Integer id ) {
		return individualsByID.get( id );
	}

	/**
	 * Removes an individual from the list of all individuals of the building
	 * and adds it to the list of individuals, which are out of the simulation because the are evacuated.
	 * @throws java.lang.IllegalArgumentException if the the specific individual does not exist in the list individuals
	 * @param i specifies the Individual object which has to be removed from the list and added to the other list
	 */
	public void setIndividualEvacuated( Individual i ) throws IllegalArgumentException {
		if( !individuals.remove( i ) )
			throw new IllegalArgumentException( "Specified individual is not in list individuals." );
		VisualResultsRecorder.getInstance().recordAction( new ExitAction( (ExitCell) i.getCell() ) );
		Cell evacCell = i.getCell();
		
		//i.getCell().takeIndividualOut();
		//i.getCell().removeIndividual();
		//i.getCell().getRoom().takeIndividualOut( i );
		i.getCell().getRoom().removeIndividual( i );
		i.setCell( evacCell );
		evacuatedIndividuals.add( i );
		i.setEvacuated();
	}

	public void markIndividualForRemoval( Individual i ) {
		markedForRemoval.add( i );
	}

	public void removeMarkedIndividuals() {
		for( Individual i : markedForRemoval )
			//if( i.getStepEndTime() <= getTimeStep() ) {
				setIndividualEvacuated( i );
			//	markedForRemoval.remove( i );
		
		markedForRemoval.clear();
	}

	/**
	 * Sets the specified individual to the status safe and sets the correct 
	 * sefety time.
	 * @param i
	 */
	public void setIndividualSave( Individual i ) {
		notSaveIndividualsCount--;
		i.setSafe( true );
		i.setSafetyTime( (int)Math.ceil(i.getStepEndTime()) );
	}
	
	/**
	 * Removes an individual from the list of all individuals of the building
	 * and adds it to the list of individuals, which are "dead".
	 * @throws java.lang.IllegalArgumentException if the the specific individual does not exist in the list individuals
	 * @param i specifies the Individual object which has to be removed from the list and added to the other list
	 * @param cause the dead cause of the individual
	 */
	public void setIndividualDead( Individual i, DeathCause cause ) throws IllegalArgumentException {
		if( !individuals.remove( i ) )
			throw new IllegalArgumentException( "Specified individual is not in list individuals." );
		//i.getCell().takeIndividualOut();
		//i.getCell().getRoom().takeIndividualOut( i );
		VisualResultsRecorder.getInstance().recordAction( new DieAction( i.getCell(), cause, i.getNumber() ) );
		Cell c = i.getCell();
		c.getRoom().removeIndividual( i );
		i.setCell( c );
		deadIndividuals.add( i );
		notSaveIndividualsCount--;
		i.die( cause );
	}

	/**
	 * Returns the number of individuals that were in the cellular automaton when
	 * the simulation starts.
	 * @return the number of individuals
	 */
	public int getInitialIndividualCount() {
		return initialIndividualCount;
	}

	/**
	 * Returns the number of individuals that currently in the cellular automaton.
	 * @return the number of individuals in the cellular automaton
	 */
	public int getIndividualCount() {
		return individuals.size();
	}

	/**
	 * Calculates the number of individuals that died by a specified death cause.
	 * @param deathCause the death cause
	 * @return the number of individuals died by the death cause
	 */
	public int getDeadIndividualCount( DeathCause deathCause ) {
		int count = 0;
		for( Individual i : getDeadIndividuals() )
			if( i.getDeathCause().compareTo( deathCause ) == 0 )
				count++;
		return count;
	}

	public int getNotSafeIndividualsCount() {
		return notSaveIndividualsCount;
	}

	/**
	 * Adds a room to the List of all rooms of the building
	 * @param room the Room object to be added
	 * @throws IllegalArgumentException Is thrown if the the specific room exists already in the list rooms
	 */
	public void addRoom( Room room ) throws IllegalArgumentException {
		if( rooms.containsKey( room ) ) {
			throw new IllegalArgumentException( "Specified room exists already in list rooms." );
		} else {
			rooms.put( room.getID(), room );
			Integer floorID = room.getFloorID();
			if( roomsByFloor.get( floorID ) == null ) {
				roomsByFloor.put( floorID, new ArrayList<Room>() );
				floorNames.put( floorID, room.getFloor() );
			}
			roomsByFloor.get( floorID ).add( room );
			
			// try to add exits
			for( Cell cell : room.getAllCells() ) {
				if( cell instanceof ExitCell ) {

			if( exits.contains( cell ) ) {
				throw new IllegalArgumentException( "Specified exit exists already in list exits." );
			} else {
				exits.add( (ExitCell) cell );
			}
				
				}
			}
			
		}
	}

	/**
	 * Removes a room from the list of all rooms of the building
	 * @param room specifies the Room object which has to be removed from the list
	 * @throws IllegalArgumentException Is thrown if the the specific room does not exist in the list rooms
	 */
	public void removeRoom( Room room ) throws IllegalArgumentException {
		if( rooms.remove( room.getID() ) == null ) {
			throw new IllegalArgumentException( "Specified room is not in list rooms." );
		}
		rooms.remove( room.getID() );
	}

	/**
	 * This method recognizes clusters of neighbouring ExitCells (that means ExitCells 
	 * lying next to another ExitCell) and returns an ArrayList, which contains one
	 * ArrayList of ExitCells for each Cluster of ExitCells. 
	 * @return An ArrayList, which contains one ArrayList of ExitCells for 
	 * each Cluster of ExitCells.
	 */
	public ArrayList<ArrayList<ExitCell>> clusterExitCells() {
		HashSet<ExitCell> alreadySeen = new HashSet<ExitCell>();
		ArrayList<ArrayList<ExitCell>> allClusters = new ArrayList<ArrayList<ExitCell>>();
		List<ExitCell> allExitCells = this.getExits();
		for( ExitCell e : allExitCells ) {
			if( !alreadySeen.contains( e ) ) {
				ArrayList<ExitCell> singleCluster = new ArrayList<ExitCell>();
				singleCluster = this.findExitCellCluster( e, singleCluster, alreadySeen );
				allClusters.add( singleCluster );
			}
		}
		return allClusters;
	}

	/**
	 * Private sub-method for finding a Cluster of neighbouring ExitCells recursively.
	 * @param currentCell The cell from which the algorithm starts searching 
	 * neighbouring ExitCells.
	 * @param cluster An empty ArrayList, in which the cluster will be created.
	 * @param alreadySeen A HashSet storing all already clustered ExitCells to prevent
	 * them of being clustered a second time.
	 * @return Returns one Cluster of neighbouring ExitCells as an ArrayList.
	 */
	private ArrayList<ExitCell> findExitCellCluster( ExitCell currentCell, ArrayList<ExitCell> cluster, HashSet<ExitCell> alreadySeen ) {
		if( !alreadySeen.contains( currentCell ) ) {
			cluster.add( currentCell );
			alreadySeen.add( currentCell );
			ArrayList<Cell> cellNeighbours = currentCell.getAllNeighbours();
			ArrayList<ExitCell> neighbours = new ArrayList<ExitCell>();
			for( Cell c : cellNeighbours ) {
				if( c instanceof ExitCell ) {
					neighbours.add( (ExitCell) c );
				}
			}
			for( ExitCell c : neighbours ) {
				cluster = this.findExitCellCluster( c, cluster, alreadySeen );
			}
		}
		return cluster;
	}

	public String graphicalToString() {
		String result = "";
		for( Room room : rooms.values() ) {
			result += room.graphicalToString() + "\n\n";
		}
		return result;
	}

	public double getStaticPotential( Cell cell, int id ) {
		return potentialManager.getStaticPotential( id ).getPotential( cell );
	}

	public int getDynamicPotential( Cell cell ) {
		return potentialManager.getDynamicPotential().getPotential( cell );
	}

	public void setDynamicPotential( Cell cell, double value ) {
		potentialManager.getDynamicPotential().setPotential( cell, value );
	}

	/**
	 * Returns the current state of the cellular automaton.
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * Sets the current state of the cellular automaton.
	 * @param state the new state
	 */
	public void setState( State state ) {
		this.state = state;
		VisualResultsRecorder.getInstance().recordAction( new CAStateChangedAction( state ) );
	}
	
	public void start() {
		setState( State.running );
		notSaveIndividualsCount = individuals.size();
		initialIndividualCount = individuals.size();
	}
	
	public void stop() {
		setState( State.finish );
	}

	/**
	 * Resets the cellular automaton in order to let it run again. All individuals
	 * are deleted, timestamp and lists are resettet and the status is set to
	 * ready. The recording of actions is stopped; Call <code>startRecording()</code>
	 * after you placed individuals in the ca to start recording again.
	 */
	public void reset() {
		VisualResultsRecorder.getInstance().stopRecording();
		VisualResultsRecorder.getInstance().reset();
		Individual[] individualsCopy = individuals.toArray( new Individual[individuals.size()] );

		for( Individual individual : individualsCopy ) {
			removeIndividual( individual );
		}
		//for( Room room : getRooms() ) {
		//	room.clear();
		//}
		deadIndividuals.clear();
		evacuatedIndividuals.clear();
		individuals.clear();
//		roomIndividualMap.clear();
		typeIndividualMap.clear();

		timeStep = 0;
		state = State.ready;
	}

	/**
	 * Returns a view of all individuals.
	 * @return the view
	 */
	public List<Individual> getIndividuals() {
		return Collections.unmodifiableList( individuals );
	}

	public List<Individual> getNotDeadIndividuals() {
		List<Individual> NotDeadIndividuals = Collections.unmodifiableList( individuals );
		for( Individual i : evacuatedIndividuals ) {
			NotDeadIndividuals.add( i );
		}
		return NotDeadIndividuals;
	}

	public int individualCount() {
		return individuals.size();
	}

	/**
	 * Returns a view of all evacuated individuals.
	 * @return the view
	 */
	public List<Individual> getEvacuatedIndividuals() {
		return Collections.unmodifiableList( evacuatedIndividuals );
	}

	public int evacuatedIndividualsCount() {
		return evacuatedIndividuals.size();
	}

	/**
	 * Returns a view of all dead individuals.
	 * @return the view
	 */
	public List<Individual> getDeadIndividuals() {
		return Collections.unmodifiableList( deadIndividuals );
	}

	public int deadIndividualsCount() {
		return deadIndividuals.size();
	}

	public Iterator<Individual> iterator() {
		return individuals.iterator();
	}

	public Room getRoom( int id ) {
		return rooms.get( id );
	}

	/**
	 * Returns a collection containing all floor ids.
	 * @return the collection of floor ids
	 */
	public Map<Integer, String> getFloors() {
		return Collections.unmodifiableMap( floorNames );
	}

	/**
	 * Returns the name of the floor with a specified id. The id corresponds to
	 * the floor numbers in the z-format.
	 * @param id the floor id
	 * @return the floors name
	 */
	public String getFloorName( int id ) {
		return floorNames.get( id );
	}

	/**
	 * Returns a collection of all rooms on a specified floor.
	 * @param floorID the floor id
	 * @return the collection of rooms
	 */
	public Collection<Room> getRoomsOnFloor( Integer floorID ) {
		return roomsByFloor.get( floorID );
	}

	// TODO: alter stuff
	
//	public void applyIndividuals( Individual[] individuals ) {
//		for( int i = 0; i < individuals.length; i++ ) {
//			individuals[i].getCell().setIndividual( individuals[i] );
//			addIndividual( individuals[i] );
//		}
//	}

	/**
	 * The individual standing on the DoorCell "exit" leaves this
	 * room and enters new room belonging to the DoorCell which
	 * is connected to the DoorCell "exit".
	 * @param exit The DoorCell from which the individual belonging
	 * to exit changes the room.
	 * @throws java.lang.IllegalArgumentException if the <code>DoorCell</code> is
	 * not occupied by an individual.
	 */
//	public void changeRoom( DoorCell exit ) throws IllegalArgumentException {
//		if( exit.getIndividual() == null ) {
//			throw new IllegalArgumentException( "No Individual standing on this DoorCell!" );
		// Kompilerfehler
		//Room newRoom = (exit.getNextDoor()).getRoom();
		//newRoom.enter(exit.getRoom().leave(exit));
//		}
//	}
	/**
	 * Returns all Individuals staying the given room.
	 * @param r Romm
	 * @return HashSet of Individuals
	 */
	//public HashSet<Individual> getIndividualsInRoom( Room r ) {
	//	return roomIndividualMap.get( r );
	//}
	/**
	 * Adds an ExitCell to the List of all exits of the building
	 * @param exit the Cell object which has to be of type ExitCell
	 * @throws IllegalArgumentException Is thrown if the the specific exit is already in the list exits
	 */
//	public void addExit( ExitCell exit ) throws IllegalArgumentException {
//		System.out.println( "Add Exit ist ausgeführt worden!" );
//		if( exits.contains( exit ) ) {
//			throw new IllegalArgumentException( "Specified exit exists already in list exits." );
//		} else {
//			exits.add( exit );
//		}
//	}
	// THIS method is only package wide known. it is not used and makes imho no sense ;)
	/**
	 * Removes an exit from the list of all exists of the building
	 * @param exit specifies the ExitCell which has to be removed from the list
	 * @throws IllegalArgumentException if the the specific exit cell does not exist in the list exits
	 */
	void removeExit( ExitCell exit ) throws IllegalArgumentException {
		if( !exits.remove( exit ) ) {
			throw new IllegalArgumentException( "Specified exit is not in the list of known exits." );
		}
	}

	// TODO: move to the ca controller. (it is a dynamic simulation feature)
	/**
	 * Gets the minimal number of time steps that are neccessary to finish all
	 * (virually not discrete) movements.
	 * @return the time
	 */
	public int getNeededTime() {
		return neededTime;
	}

	/**
	 * Sets a new minimal number of time steps that are neccessary to finish all
	 * (virually non discrete) movements.
	 * @param neededTime the timestep
	 */
	public void setNeededTime( int neededTime) {
		this.neededTime = neededTime;
	}

}
