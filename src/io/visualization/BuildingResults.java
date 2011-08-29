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
package io.visualization;

import opengl.framework.abs.VisualizationResult;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ds.z.PlanPoint;
import ds.z.PlanPolygon.RelativePosition;
import java.util.Vector;

/**
 * @author Daniel Plümpe
 *
 */
public class BuildingResults implements VisualizationResult {

	public static class Wall implements Iterable<Point2D.Double> {

		/** An enumeration describing the different types of walls that can be visualized. */
		public static enum ElementType {

			/** The default wall type, nothing special. */
			SIMPLE,
			/** A passable wall, normally that is a door. */
			PASSABLE,
			/** A wall that belongs to an inaccessible area. */
			INACCESSIBLE
		};
		private Vector<Point2D.Double> points;
		private Floor floor;
		//protected ElementType wallType;
		private Vector<ElementType> wallTypes;
		protected boolean roomIsLeft;
		protected boolean roomIsRight;
		protected boolean barrier = false;

		public Wall( Floor floor ) {
			this.points = new Vector<Point2D.Double>();
			this.wallTypes = new Vector<ElementType>();
			this.floor = floor;
		}

		public boolean isBarrier() {
			return barrier;
		}

		protected void setBarrier( boolean barrier ) {
			this.barrier = barrier;
		}

		public boolean isRoomIsLeft() {
			return roomIsLeft;
		}

		public void setRoomIsLeft( boolean roomIsLeft ) {
			this.roomIsLeft = roomIsLeft;
		}

		public boolean isRoomIsRight() {
			return roomIsRight;
		}

		public void setRoomIsRight( boolean roomIsRight ) {
			this.roomIsRight = roomIsRight;
		}

		public void addPoint( double x, double y, ElementType type ) {
			if( points.size() == 0 ) {
				// nothing
			} else
				wallTypes.add( type );
			points.add( new Point2D.Double( x, y ) );
		}

		/**
		 * 
		 * @param zPoint
		 * @param type
		 */
		public void addPoint( PlanPoint zPoint, ElementType type ) {
			addPoint( zPoint.x, zPoint.y, type );
		}

		public ElementType getWallType( int wallSegment ) {
			return wallTypes.get( wallSegment );
		}

		public Floor getFloor() {
			return floor;
		}

		@Override
		public Iterator<Point2D.Double> iterator() {
			return Collections.unmodifiableList( points ).iterator();
		}
	}

	public static class Floor {

		/** The id of the floor. Normally this is the floor number in the z-format.*/
		private int id;
		/** The name of the floor. */
		private String name;

		public Floor( int id, String name ) {
			this.id = id;
			this.name = name;
		}

		public int id() {
			return id;
		}

		public String name() {
			return name;
		}
	}
	private LinkedList<Wall> walls;
	private HashMap<Integer, Floor> floors;

	public BuildingResults( ds.z.BuildingPlan buildingPlan ) {
		walls = new LinkedList<Wall>();
		floors = new HashMap<Integer, Floor>();

		for( ds.z.Floor zFloor : buildingPlan.getFloors() ) {
			Floor buildingFloor = new Floor( buildingPlan.getFloorID( zFloor ), zFloor.getName() );

			if( zFloor.getRooms().size() > 0 ) {
				if( floors.put( buildingFloor.id(), buildingFloor ) != null )
					throw new RuntimeException( "Error while building the visualisation data out of the building plan: " + "There were two z-floors with the same id. " );

				for( ds.z.Room room : zFloor.getRooms() ) {
					addHeterogeneousEdgeList( room.edgeIterator( false ), room, buildingFloor );
					for( ds.z.Barrier barrier : room.getBarriers() ) {
						Wall w = addHomogeneousEdgeList( barrier.edgeIterator( false ), barrier, buildingFloor, Wall.ElementType.INACCESSIBLE );
						w.setBarrier( true );
					}
					for( ds.z.InaccessibleArea area : room.getInaccessibleAreas() )
						addHomogeneousEdgeList( area.edgeIterator( false ), area, buildingFloor, Wall.ElementType.INACCESSIBLE );
				}
			}
		}
	}

	public void addWall( Wall wall ) {
		walls.add( wall );
	}

	public List<Wall> getWalls() {
		return Collections.unmodifiableList( walls );
	}

	public Collection<Floor> getFloors() {
		return Collections.unmodifiableCollection( floors.values() );
	}

	protected void addHeterogeneousEdgeList( Iterator<? extends ds.z.RoomEdge> edgeIt, ds.z.PlanPolygon room, Floor floor ) {
		Wall curWall = new Wall( floor );

		if( edgeIt.hasNext() ) {
			ds.z.RoomEdge firstEdge = edgeIt.next();
			checkOrientation( firstEdge, room, curWall );
			if( firstEdge.isPassable() ) {
				curWall.addPoint( firstEdge.getSource(), Wall.ElementType.PASSABLE );
				curWall.addPoint( firstEdge.getTarget(), Wall.ElementType.PASSABLE );
			} else {
				curWall.addPoint( firstEdge.getSource(), Wall.ElementType.SIMPLE );
				curWall.addPoint( firstEdge.getTarget(), Wall.ElementType.SIMPLE );
			}
		}

		while( edgeIt.hasNext() ) {
			ds.z.RoomEdge curEdge = edgeIt.next();
			checkOrientation( curEdge, room, curWall );
			if( curEdge.isPassable() )
				curWall.addPoint( curEdge.getTarget(), Wall.ElementType.PASSABLE );
			else
				curWall.addPoint( curEdge.getTarget(), Wall.ElementType.SIMPLE );
		}
		addWall( curWall );
	}

	protected Wall addHomogeneousEdgeList( Iterator<? extends ds.z.Edge> edgeIt, ds.z.PlanPolygon room, Floor floor, Wall.ElementType type ) {
		Wall curWall = new Wall( floor );

		if( edgeIt.hasNext() ) {
			ds.z.Edge firstEdge = edgeIt.next();
			checkOrientation( firstEdge, room, curWall );
			curWall.addPoint( firstEdge.getSource(), type );
			curWall.addPoint( firstEdge.getTarget(), type );
		}

		while( edgeIt.hasNext() ) {
			ds.z.Edge curEdge = edgeIt.next();
			checkOrientation( curEdge, room, curWall );
			curWall.addPoint( curEdge.getTarget(), type );
		}
		addWall( curWall );
		return curWall;
	}

	/**
	 * Checks whether an room is on the right or left side of an edge and stores
	 * the result in an corresponding {@link Wall} object.
	 * @param edge the edge
	 * @param room the room
	 * @param curWall the wall
	 */
	private void checkOrientation( ds.z.Edge edge, ds.z.PlanPolygon room, Wall curWall ) {
		boolean isLeft = room.relativePolygonPosition( edge, RelativePosition.Left );
		boolean isRight = room.relativePolygonPosition( edge, RelativePosition.Right );
		if( isLeft != isRight ) {
			curWall.roomIsLeft = isLeft;
			curWall.roomIsRight = isRight;
		}// else
		//	System.err.println( "Kante konnte nicht genutzt werden!");
	}
}
