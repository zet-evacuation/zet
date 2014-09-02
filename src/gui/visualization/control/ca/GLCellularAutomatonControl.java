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

/**
 * Class GLCellularAutomatonControl
 * Created 02.05.2008, 18:44:21
 */

package gui.visualization.control.ca;

import de.tu_berlin.math.coga.math.Conversion;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.evac.Individual;
import ds.ca.results.DieAction;
import ds.ca.results.MoveAction;
import ds.ca.results.SwapAction;
import ds.ca.results.EvacuationRecording;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.draw.ca.GLCA;
import static gui.visualization.control.ZETGLControl.CellInformationDisplay;
import gui.visualization.draw.ca.GLIndividual;
import io.visualization.EvacuationSimulationResults;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.media.opengl.GL;
import opengl.framework.abs.DrawableControlable;
import opengl.helper.Frustum;

/**
 *  @author Jan-Philipp Kappmeier
 */
public class GLCellularAutomatonControl extends AbstractZETVisualizationControl<GLCAFloorControl, GLCA, GLCellularAutomatonControl> implements DrawableControlable {

	// general control stuff
	private HashMap<Integer, GLCAFloorControl> allFloorsByID;
	ArrayList<GLIndividual> glIndividuals;
	ArrayList<GLIndividualControl> individuals;
	EvacuationSimulationResults visResults;
	//private EvacuationCellularAutomaton ca;
	// timing stuff
	private double realStep;
	private double secondsPerStep;
	private long nanoSecondsPerStep;
	private long step;
	private long time;
	/** The status of the simulation, true if all is finished */
	private boolean finished = false;
	// ca visualization stuff
	private int cellCount;
	private int cellsDone;
	private int recordingCount;
	private int recordingDone;
	private int maxReactionTime;
	boolean containsRecording = false;
	double scaling = 1;
	double defaultFloorHeight = 10;

	private EvacuationSimulationResults caVisResults;
	
	public GLCellularAutomatonControl( EvacuationSimulationResults caVisResults ) {
		super();
		this.caVisResults = caVisResults;
	}
	
	public void build() {
		containsRecording = caVisResults.getRecording() != null;
		mainControl = this;
		cellCount = caVisResults.getCa().getCellCount();
		cellsDone = 0;
		//AlgorithmTask.getInstance().setProgress( 0, DefaultLoc.getSingleton().getStringWithoutPrefix( "batch.tasks.progress.createCellularAutomatonVisualizationDatastructure" ), "" );

		recordingCount = containsRecording ? caVisResults.getRecording().length() : 0;
		recordingDone = 0;

		allFloorsByID = new HashMap<>();
		glIndividuals = new ArrayList<>();
		this.visResults = caVisResults;

		List<String> floors = caVisResults.getCa().getFloors();
		for( int i = 0; i < floors.size(); ++i )
			add( new GLCAFloorControl( caVisResults, caVisResults.getCa().getRoomsOnFloor( i ), i, mainControl ) );

		this.setView( new GLCA( this ) );
		for( GLCAFloorControl floor : this )
			view.addChild( floor.getView() );

		showAllFloors();

		// Set up timing:
		secondsPerStep = caVisResults.getCa().getSecondsPerStep();
		nanoSecondsPerStep = Math.round( secondsPerStep * Conversion.secToNanoSeconds );
		step = 0;
		if( containsRecording ) {
			convertIndividualMovements();
			caVisResults.getRecording().rewind();
			for( GLCAFloorControl floor : this )
				floor.getView().setIndividuals( getIndividualControls() );
		} else {
			individuals = new ArrayList<>();
		}
		
		// set up individual heights
		GLIndividual.individualHeight = VisualizationOptionManager.getIndividualHeight() * mainControl.scaling;
		GLIndividual.individualRadius = VisualizationOptionManager.getIndividualRadius() * mainControl.scaling;
	}

	public double getSecondsPerStep() {
		return secondsPerStep;
	}

	public List<GLIndividual> getIndividuals() {
		return Collections.unmodifiableList( glIndividuals );
	}

	public Collection<GLCAFloorControl> getAllFloors() {
		return allFloorsByID.values();
	}

	public void showOnlyFloor( Integer floorID ) {
		childControls.clear();
		//childControls.add( allFloorsByID.get( floorID == 0 && allFloorsByID.get( floorID ) == null ? 1 : floorID ) ); // add the floor if possible, otherwise the first
		childControls.add( allFloorsByID.get( floorID ) ); // add the floor if possible, otherwise the first
		view.clear();
		for( GLCAFloorControl floor : this )
			view.addChild( floor.getView() );
	}

	public void showAllFloors() {
		childControls.clear();
		childControls.addAll( allFloorsByID.values() );
		view.clear();
		for( GLCAFloorControl floor : this )
			view.addChild( floor.getView() );		
	}

	@Override
	public void add( GLCAFloorControl childControl ) {
		super.add( childControl );
		System.out.println( "FLoor hinzugefügt" );
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
		for( GLCAFloorControl floorControl : allFloorsByID.values() )
			floorControl.setPotentialDisplay( potentialDisplay );
	}

	GLCAFloorControl getFloorControl( Integer floorID ) {
		return this.allFloorsByID.get( floorID );
	}

	private GLCellControl getCellControl( ds.ca.evac.EvacCell cell ) {
		GLCAFloorControl floor = getFloorControl( cell.getRoom().getFloorID() );
		GLRoomControl room = floor.getRoomControl( cell.getRoom() );
		return room.getCellControl( cell );
	}

	private void convertIndividualMovements() {
		EvacuationRecording recording = visResults.getRecording();
		EvacuationCellularAutomaton ca = new EvacuationCellularAutomaton( recording.getInitialConfig() );
		individuals = new ArrayList<GLIndividualControl>( ca.getIndividuals().size() );
		for( int k = 0; k < ca.getIndividuals().size(); k++ )
			individuals.add( null );
		for( Individual individual : ca.getIndividuals() ) {
			GLIndividualControl control = new GLIndividualControl( individual, mainControl );
			individuals.set( individual.getNumber() - 1, control );
		}

		recording.rewind();

		while(recording.hasNext()) {
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
			for( SwapAction swap : swaps ) {
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
			// Individuen, die schon von anfang an tot sind:
			for( DieAction death : deaths ) {
				GLCellControl cell = getCellControl( death.placeOfDeath() );
				individuals.get( death.getIndividualNumber() - 1 ).addHistoryTriple( cell, cell, 0, 0 );
			}
			mainControl.recordingProgress();
		}
		recording.rewind();
		for( int k = 0; k < ca.getIndividuals().size(); k++ )
			glIndividuals.add( individuals.get( k ).getView() );
	}

	public final List<GLIndividualControl> getIndividualControls() {
		return Collections.unmodifiableList( individuals );
	}

	/**
	 * <p>This method increases the number of cells that are created and
	 * calculates a new progress. The progress will at most reach 99% so that
	 * after all objects are created a final "Done" message can be submitted.</p>
	 * <p>Note that before this method can be used in the proper way the private
	 * variable {@code cellsDone} and {@code cellCount} should be
	 * initialized correct. However, it is guaranteed to calculate a value from
	 * 0 to 99.
	 */
	public void cellProgress() {
		cellsDone++;
		int progress = Math.max( 0, Math.min( (int) Math.round( ((double) cellsDone / cellCount) * 100 ), 99 ) );
		//AlgorithmTask.getInstance().setProgress( progress, "Erzeuge Zellen...", "Zelle " + cellsDone + " von " + cellCount + " erzeugt." );
	}

	/**
	 * <p>This method increases the number of individuals that are created and
	 * calculates a new progress. The progress will at most reach 99% so that
	 * after all objects are created a final "Done" message can be submitted.</p>
	 * <p>Note that before this method can be used in the proper way the private
	 * variable {@code cellsDone} and {@code cellCount} should be
	 * initialized correct. However, it is guaranteed to calculate a value from
	 * 0 to 99.
	 */
	public void recordingProgress() {
		recordingDone++;
		int progress = Math.max( 0, Math.min( (int) Math.round( ((double) recordingDone / recordingCount) * 100 ), 99 ) );
		//AlgorithmTask.getInstance().setProgress( progress, "Erzeuge Individuen-Bewegungen...", "Recording-Schritt " + recordingDone + " von " + recordingCount + " abgearbeitet." );
	}

	double speedFactor = 1;

	@Override
	public void addTime( long timeNanoSeconds ) {
		time += timeNanoSeconds * speedFactor;
		realStep = ((double) time / (double) nanoSecondsPerStep);
		final long stepOld = step;
		step = (long) realStep;
		long elapsedSteps = stepOld - step;

		if( elapsedSteps > 0 )
			for( GLCAFloorControl floor : this )
				for( GLRoomControl room : floor )
					for( GLCellControl cell : room )
						cell.stepUpdate();

			finished = step > recordingCount;
	}

	@Override
	public void setTime( long timeNanoSeconds ) {
		time = timeNanoSeconds;
		realStep = ((double) time / (double) nanoSecondsPerStep);
		step = (long) realStep;

		for( GLCAFloorControl floor : this )
			for( GLRoomControl room : floor )
				for( GLCellControl cell : room )
					cell.stepUpdate();

		finished = step > recordingCount;
	}

	public double getTimeInSeconds() {
		return time * Conversion.nanoSecondsToSec;
	}

	@Override
	public void resetTime() {
		setTime( 0 );
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
	public double getStep() {
		return realStep;
	}

	/**
	 * Sets a factor that is multiplicated with the visualization speed. Use
	 * {@code 1.0} for normal (real-time) speed.
	 * @param speedFactor the speed factor
	 */
	public void setSpeedFactor( double speedFactor ) {
		//secondsPerStep = ca.getSecondsPerStep();
		//nanoSecondsPerStep = (long) (Math.round( secondsPerStep * Conversion.secToNanoSeconds ) / speedFactor);
		this.speedFactor = speedFactor;
	}

	public long getNanoSecondsPerStep() {
		return nanoSecondsPerStep;
	}

	public long getStepCount() {
		return recordingCount;
	}

	Frustum frustum;

	public void setFrustum( Frustum frustum ) {
		this.frustum = frustum;
		for( GLIndividual individual : glIndividuals )
			individual.setFrustum( frustum );
	}

	public Frustum getFrustum() {
		return frustum;
	}

	public void draw( GL gl ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public void update() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public void delete() {
		for( GLCAFloorControl floor : this ) {
			//floor.delete();
		}
		view.delete();
		//ca = null;
    caVisResults = null;
	}

	public void setScaling( double scaling ) {
		this.scaling = scaling;
	}

	public void setDefaultFloorHeight( double defaultFloorHeight ) {
		this.defaultFloorHeight = defaultFloorHeight;
	}
}
