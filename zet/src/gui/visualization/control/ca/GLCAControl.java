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

/**
 * Class GLCAControl
 * Erstellt 02.05.2008, 18:44:21
 */

package gui.visualization.control.ca;

import batch.tasks.AlgorithmTask;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.math.Conversion;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import ds.ca.CellularAutomaton;
import ds.ca.Individual;
import ds.ca.results.Action;
import ds.ca.results.DieAction;
import ds.ca.results.InconsistentPlaybackStateException;
import ds.ca.results.MoveAction;
import ds.ca.results.SwapAction;
import ds.ca.results.VisualResultsRecording;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.draw.ca.GLCA;
import static gui.visualization.control.GLControl.CellInformationDisplay;
import gui.visualization.draw.ca.GLIndividual;
import io.visualization.CAVisualizationResults;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import opengl.framework.abs.Controlable;
import util.DebugFlags;

/**
 *  @author Jan-Philipp Kappmeier
 */
//public class GLCAControl extends AbstractControl<GLCA, CellularAutomaton, CAVisualizationResults, GLCAFloor, GLCAFloorControl, GLControl> {
public class GLCAControl extends AbstractZETVisualizationControl<GLCAFloorControl, GLCA, GLCAControl> implements Controlable {

	// general control stuff
	private HashMap<Integer, GLCAFloorControl> allFloorsByID;
	ArrayList<GLIndividual> glIndividuals;
	ArrayList<GLIndividualControl> individuals;
	CAVisualizationResults visResults;
	private VisualResultsRecording visRecording;
	private CellularAutomaton ca;

	// timing stuff
	private double realStepCA;
	private double secondsPerStepCA;
	private long nanoSecondsPerStepCA;
	private long stepCA;
	private long timeSinceLastStepCA = 0;
	private long time;
	/** The status of the simulation, true if all is finished */
	private boolean finished = false;


	// ca visualization stuff
	private int cellCount;
	private int cellsDone;

	// Individual stuff
	//private List<GLIndividual> individuals;

	private int recordingCount;
	private int recordingDone;

	public GLCAControl( CAVisualizationResults caVisResults, CellularAutomaton ca ) {
		super();
		mainControl = this;

		this.ca = ca;

		cellCount = ca.getCellCount();
		cellsDone = 0;
		AlgorithmTask.getInstance().setProgress( 0, Localization.getInstance().getStringWithoutPrefix( "batch.tasks.progress.createCellularAutomatonVisualizationDatastructure" ), "" );

		recordingCount = caVisResults.getRecording().length();
		recordingDone = 0;

			allFloorsByID = new HashMap<Integer, GLCAFloorControl>();
		glIndividuals = new ArrayList<GLIndividual>();
		this.visResults = caVisResults;

		for( int floorID : ca.getFloors().keySet() ) {
			add( new GLCAFloorControl( caVisResults, ca.getRoomsOnFloor( floorID ), floorID, mainControl ) );
		}

		this.setView( new GLCA( this ) );
		for( GLCAFloorControl floor : this )
			view.addChild( floor.getView() );

		showAllFloors();

		if( DebugFlags.VIS_CA )
			System.out.println( "Beginne Indivduen-Bewegungen zu konvertieren ..." );
		convertIndividualMovements();
		if( DebugFlags.VIS_CA )
			System.out.println( "Individuen-Bewegungen konvertiert." );

		visRecording = caVisResults.getRecording();

		// Set up timing:
			secondsPerStepCA = ca.getSecondsPerStep();
			nanoSecondsPerStepCA = Math.round( secondsPerStepCA * Conversion.secToNanoSeconds );
			stepCA = 0;
			caVisResults.getRecording().rewind();
			for( Action action : caVisResults.getRecording().nextActions() ) {
				try {
					action.execute( ca );
					if( action instanceof MoveAction ) {
						System.out.println( action );
					}
				} catch( InconsistentPlaybackStateException e ) {
					e.printStackTrace();
				}
			}

			// füge alle zellen in die update-liste hinzu
			//cells = new ArrayList<GLCellControl>();
			for( GLCAFloorControl floor : this ) {
				//for( GLRoomControl room : floor ) {
					//for( GLCellControl cell : room ) {
						//cells.add( cell );	// TODO use addAll
					//}
				//}
				floor.getView().setIndividuals( getIndividualControls() );
			}


	}

	public double getSecondsPerStepCA() {
		return secondsPerStepCA;
	}

	public List<GLIndividual> getIndividuals() {
		return Collections.unmodifiableList( glIndividuals );
	}

	public Collection<GLCAFloorControl> getAllFloors() {
		return allFloorsByID.values();
	}

	public void showOnlyFloor( Integer floorID ) {
		childControls.clear();
		if( floorID == 0 && allFloorsByID.get( floorID) == null )
			childControls.add( allFloorsByID.get( 1 ) );
		else
			childControls.add( allFloorsByID.get( floorID ) );
	}

	public void showAllFloors() {
		childControls.clear();
		childControls.addAll( allFloorsByID.values() );
	}

	@Override
	public void add( GLCAFloorControl childControl ) {
		super.add( childControl );
		allFloorsByID.put( childControl.getFloorNumber(), childControl );
	}

	@Override
	public void clear() {
		allFloorsByID.clear();
		childControls.clear();
	}

	@Override
	public Iterator<GLCAFloorControl> fullIterator() {
		return allFloorsByID.values().iterator();
	}

	public void setPotentialDisplay( CellInformationDisplay potentialDisplay ) {
		for( GLCAFloorControl floorControl : allFloorsByID.values() ) {
			floorControl.setPotentialDisplay( potentialDisplay );
		}
	}

	GLCAFloorControl getFloorControl( Integer floorID ) {
		return this.allFloorsByID.get( floorID );
	}

	private GLCellControl getCellControl( ds.ca.Cell cell ) {
		GLCAFloorControl floor = getFloorControl( cell.getRoom().getFloorID() );
		GLRoomControl room = floor.getRoomControl( cell.getRoom() );
		return room.getCellControl( cell );
	}

	private void convertIndividualMovements() {
		VisualResultsRecording recording = visResults.getRecording();
		CellularAutomaton ca = new CellularAutomaton( recording.getInitialConfig() );
		individuals = new ArrayList<GLIndividualControl>( ca.getIndividuals().size() );
		for( int k = 0; k < ca.getIndividuals().size(); k++ ) {
			individuals.add( null );
		}
		for( Individual individual : ca.getIndividuals() ) {
			GLIndividualControl control = new GLIndividualControl( individual, mainControl );
			individuals.set( individual.getNumber() - 1, control );
		}

		recording.rewind();
		
		while( recording.hasNext() ) {
			recording.nextActions();
			Vector<MoveAction> movements = recording.filterActions( MoveAction.class );
			for( MoveAction movement : movements ) {
				GLCellControl fromCell = getCellControl( movement.from() );
				GLCellControl endCell = getCellControl( movement.to() );
				double arrivalTime = movement.arrivalTime();
				double startTime = movement.startTime();
				individuals.get( movement.getIndividualNumber() - 1 ).addHistoryTriple( fromCell, endCell, startTime, arrivalTime );
			}
			Vector<SwapAction> swaps = recording.filterActions( SwapAction.class );
			for( SwapAction swap: swaps ) {
				GLCellControl cell1 = getCellControl( swap.cell1() );
				GLCellControl cell2 = getCellControl( swap.cell2() );
				double arrivalTime1 = swap.arrivalTime1();
				double startTime1 = swap.startTime1();
				double arrivalTime2 = swap.arrivalTime2();
				double startTime2 = swap.startTime2();
				individuals.get( swap.getIndividualNumber1() - 1 ).addHistoryTriple( cell1, cell2, startTime1, arrivalTime1 );
				individuals.get( swap.getIndividualNumber1() - 1 ).addHistoryTriple( cell2, cell1, startTime2, arrivalTime2 );
			}
			Vector<DieAction> deaths = recording.filterActions( DieAction.class );
			for( DieAction death: deaths) {
				GLCellControl cell = getCellControl( death.placeOfDeath() );
				individuals.get(  death.getIndividualNumber() - 1 ).addHistoryTriple( cell, cell, 0, 0 );
			}
			mainControl.recordingProgress();
		}
		recording.rewind();
		for( int k = 0; k < ca.getIndividuals().size(); k++ ) {
			glIndividuals.add( individuals.get( k ).getView() );
		}
	}

	public final List<GLIndividualControl> getIndividualControls() {
		return Collections.unmodifiableList( individuals );
	}


	/**
	 * <p>This method increases the number of cells that are created and
	 * calculates a new progress. The progress will at most reach 99% so that
	 * after all objects are created a final "Done" message can be submitted.</p>
	 * <p>Note that before this method can be used in the proper way the private
	 * variable <code>cellsDone</code> and <code>cellCount</code> should be
	 * initialized correct. However, it is guaranteed to calculate a value from
	 * 0 to 99.
	 */
	public void cellProgress() {
		cellsDone++;
		int progress = Math.max( 0, Math.min( (int) Math.round( ((double) cellsDone / cellCount) * 100 ), 99 ) );
		AlgorithmTask.getInstance().setProgress( progress, "Erzeuge Zellen...", "Zelle " + cellsDone + " von " + cellCount + " erzeugt." );
	}

	/**
	 * <p>This method increases the number of individuals that are created and
	 * calculates a new progress. The progress will at most reach 99% so that
	 * after all objects are created a final "Done" message can be submitted.</p>
	 * <p>Note that before this method can be used in the proper way the private
	 * variable <code>cellsDone</code> and <code>cellCount</code> should be
	 * initialized correct. However, it is guaranteed to calculate a value from
	 * 0 to 99.
	 */
	public void recordingProgress() {
		recordingDone++;
		int progress = Math.max( 0, Math.min( (int) Math.round( ((double) recordingDone / recordingCount) * 100 ), 99 ) );
		AlgorithmTask.getInstance().setProgress( progress, "Erzeuge Individuen-Bewegungen...", "Recording-Schritt " + recordingDone + " von " + recordingCount + " abgearbeitet." );
	}

	@Override
	public void addTime( long timeNanoSeconds ) {
		timeSinceLastStepCA += timeNanoSeconds;
//		time += timeNanoSeconds;

		if( timeSinceLastStepCA >= nanoSecondsPerStepCA ) {
				long elapsedSteps = (timeSinceLastStepCA / nanoSecondsPerStepCA);
				stepCA += elapsedSteps;
				for( int i = 1; i <= elapsedSteps; i++ ) {
					ca.nextTimeStep();
					if( visRecording.hasNext() ) {
						try {
							Vector<Action> actions = visRecording.nextActions();
							for( Action action : actions )
								action.execute( ca );
						} catch( InconsistentPlaybackStateException ex ) {
							ex.printStackTrace();
						}
					}
				}
				timeSinceLastStepCA = timeSinceLastStepCA % nanoSecondsPerStepCA; //elapsedTime -  step*nanoSecondsPerStep;
			for( GLCAFloorControl floor : this ) {
				for( GLRoomControl room : floor ) {
					for( GLCellControl cell : room ) {
						//cells.add( cell );	// TODO use addAll
						cell.stepUpdate();
					}
				}
				//floor.getView().setIndividuals( getIndividualControls() );
			}

				//				for( GLCellControl cell : cells ) {
//					cell.stepUpdate();
//				}
			}
			realStepCA = stepCA + (double)timeSinceLastStepCA/nanoSecondsPerStepCA;
			if( ca.getState() == CellularAutomaton.State.finish ) {
				finished = true;
			}

	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	public final GLIndividual getControlledGLIndividual( int number ) {
		return glIndividuals.get( number - 1 );
	}

	/**
	 * Returns the current step of the cellular automaton. The step counter is
	 * stopped if the cellular automaton is finished.
	 * @return the current step of the cellular automaton
	 */
	public double getCaStep() {
		return realStepCA;
	}


	/**
	 * Sets a factor that is multiplicated with the visualization speed. Use
	 * <code>1.0</code> for normal (real-time) speed.
	 * @param speedFactor the speed factor
	 */
	public void setSpeedFactor( double speedFactor ) {
		secondsPerStepCA = ca.getSecondsPerStep();
		nanoSecondsPerStepCA = (long)(Math.round( secondsPerStepCA * Conversion.secToNanoSeconds ) / speedFactor);
	}

}
