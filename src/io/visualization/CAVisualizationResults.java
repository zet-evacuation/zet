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
 * Class CAVisualizationMapping
 * Created 08.05.2008, 01:40:12
 */
package io.visualization;

import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.evac.PotentialManager;
import ds.ca.results.VisualResultsRecording;
import java.util.HashMap;
import opengl.framework.abs.VisualizationResult;
import statistic.ca.CAStatistic;

/**
 * A data structure containing all information about a run of the cellular
 * automaton, including the (real z-format) positions of the cells and the
 * visual recorder.
 * @author Jan-Philipp Kappmeier
 */
public class CAVisualizationResults implements VisualizationResult {

	/** The recording of a simulation. */
	private VisualResultsRecording visRecording;
	/**
	 * A mapping from a cell to its offset relative to the room containing it.
	 * (The offset of the room is NOT included!). 
	 */
	private HashMap<ds.ca.evac.Cell, Vector3> caCellToZOffsetMapping;
	/**
	 * A mapping from a room to its offset relative to the floor containing it.
	 * (The offset of the floor is NOT included!).
	 */
	private HashMap<ds.ca.evac.Room, Vector3> caRoomToZOffsetMapping;
	/**
	 * A mapping from a floor (given by ID) to its offset relative to the
	 * origin of the z-project.
	 */
	private HashMap<Integer, Vector3> caFloorToZOffsetMapping;

	private PotentialManager pm;

	//TODO correct
	public CAStatistic statistic;

	/**
	 * Creates the visualization results. Takes a ca data structure, a
	 * visual recording object, a ZToCAMapping and creates all necessary objects.
	 * @param visRecording 
	 * @param caMapping
	 */
	public CAVisualizationResults( VisualResultsRecording visRecording, ZToCAMapping caMapping ) {
		caCellToZOffsetMapping = new HashMap<ds.ca.evac.Cell, Vector3>();
		caRoomToZOffsetMapping = new HashMap<ds.ca.evac.Room, Vector3>();
		caFloorToZOffsetMapping = new HashMap<Integer, Vector3>();

		this.visRecording = visRecording;

		caMapping = caMapping.adoptToCA( new EvacuationCellularAutomaton( visRecording.getInitialConfig() ) );
		pm = visRecording.getInitialConfig().getPotentialManager();

		convertMapping( caMapping );
	}

	public CAVisualizationResults( ZToCAMapping caMapping, PotentialManager pm ) {
		caCellToZOffsetMapping = new HashMap<ds.ca.evac.Cell, Vector3>();
		caRoomToZOffsetMapping = new HashMap<ds.ca.evac.Room, Vector3>();
		caFloorToZOffsetMapping = new HashMap<Integer, Vector3>();
		convertMapping( caMapping );
		this.pm = pm;
	}

	private void convertMapping( ZToCAMapping caMapping ) {
		for( Integer floorID : caMapping.getCAFloors() ) {
			ds.z.Floor zFloor = caMapping.get( floorID );
			double xOffset = zFloor.getxOffset();
			double yOffset = zFloor.getyOffset();

			caFloorToZOffsetMapping.put( floorID, new Vector3( xOffset, yOffset, 0 ) );
		}

		for( ds.ca.evac.Room room : caMapping.getCARooms() ) {
			ds.z.Room zRoom = caMapping.get( room ).getRoom();
			double xOffset = zRoom.getxOffset();
			double yOffset = zRoom.getyOffset();

			caRoomToZOffsetMapping.put( room, new Vector3( xOffset, yOffset, 0 ) );
		}

		for( ds.ca.evac.Cell cell : caMapping.getCACells() ) {
			de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterSquare zRasterSquare = caMapping.get( cell );

			double xOffset = zRasterSquare.getRelativeX();
			double yOffset = zRasterSquare.getRelativeY();

			caCellToZOffsetMapping.put( cell, new Vector3( xOffset, yOffset, 0 ) );
		}
	}

	public Vector3 get( ds.ca.evac.Cell cell ) {
		if( caCellToZOffsetMapping.get( cell ) == null )
			return new Vector3( 0.0d, 0.0d, 0.0d );
		return caCellToZOffsetMapping.get( cell );
	}

	public Vector3 get( ds.ca.evac.Room room ) {
		if( caRoomToZOffsetMapping.get( room ) == null )
			return new Vector3( 0.0d, 0.0d, 0.0d );

		return caRoomToZOffsetMapping.get( room );
	}

	public Vector3 get( Integer floorID ) {
		if( caFloorToZOffsetMapping.get( floorID ) == null )
			return new Vector3( 0.0d, 0.0d, 0.0d );
		return caFloorToZOffsetMapping.get( floorID );
	}

	public VisualResultsRecording getRecording() {
		return this.visRecording;
	}

	public PotentialManager getPotentialManager() {
		return pm;
	}
}
