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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import ds.ca.CellularAutomaton;
import ds.ca.Individual;
import ds.ca.results.DieAction;
import ds.ca.results.MoveAction;
import ds.ca.results.SwapAction;
import ds.ca.results.VisualResultsRecording;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.control.GLControl;
import gui.visualization.draw.ca.GLCA;
import static gui.visualization.control.GLControl.CellInformationDisplay;
import gui.visualization.draw.ca.GLIndividual;
import io.visualization.CAVisualizationResults;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import util.DebugFlags;

/**
 *  @author Jan-Philipp Kappmeier
 */
//public class GLCAControl extends AbstractControl<GLCA, CellularAutomaton, CAVisualizationResults, GLCAFloor, GLCAFloorControl, GLControl> {
public class GLCAControl extends AbstractZETVisualizationControl<GLCAFloorControl, GLCA> {

	private HashMap<Integer, GLCAFloorControl> allFloorsByID;
	ArrayList<GLIndividual> glIndividuals;
	ArrayList<GLIndividualControl> individuals;
	CAVisualizationResults visResults;

	public GLCAControl( CAVisualizationResults caVisResults, CellularAutomaton ca, GLControl glControl ) {
		super( glControl );
		allFloorsByID = new HashMap<Integer, GLCAFloorControl>();
		glIndividuals = new ArrayList<GLIndividual>();
		this.visResults = caVisResults;

		for( int floorID : ca.getFloors().keySet() ) {
			add( new GLCAFloorControl( caVisResults, ca.getRoomsOnFloor( floorID ), floorID, glControl ) );
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
	}

	public List<GLIndividual> getIndividuals() {
		return Collections.unmodifiableList( glIndividuals );
	}

	public Collection<GLCAFloorControl> getAllFloors() {
		return allFloorsByID.values();
	}

	public void showOnlyFloor( Integer floorID ) {
		childControls.clear();
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
			((GLControl)mainControl).recordingProgress();
		}
		recording.rewind();
		for( int k = 0; k < ca.getIndividuals().size(); k++ ) {
			glIndividuals.add( individuals.get( k ).getView() );
		}
	}

	public final List<GLIndividualControl> getIndividualControls() {
		return Collections.unmodifiableList( individuals );
	}
}
