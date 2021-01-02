/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.visualization;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import de.zet_evakuierung.model.FloorInterface;
import de.zet_evakuierung.model.PlanPoint;
import de.zet_evakuierung.model.RelativePosition;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.visualization.building.model.GLWallModel;

/**
 * @author Daniel R. Schmidt
 *
 */
public class BuildingResults {

	public static class Wall implements GLWallModel, Iterable<Point2D.Double> {

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
			this.points = new Vector<>();
			this.wallTypes = new Vector<>();
			this.floor = floor;
		}

        @Override
		public boolean isBarrier() {
			return barrier;
		}

		protected void setBarrier( boolean barrier ) {
			this.barrier = barrier;
		}

		/**
		 * Checks if the room is on the left side of the wall.
		 *
		 * @return true if the room is on the left side, false otherwise.
		 */
        @Override
		public boolean isRoomLeft() {
			return roomIsLeft;
		}

		public void setRoomIsLeft( boolean roomIsLeft ) {
			this.roomIsLeft = roomIsLeft;
		}

		/**
		 * Checks if the room is on the right side of the wall.
		 *
		 * @return true if the room is on the right side, false otherwise
		 */
        @Override
		public boolean isRoomRight() {
			return roomIsRight;
		}

		public void setRoomIsRight( boolean roomIsRight ) {
			this.roomIsRight = roomIsRight;
		}

		public void addPoint( double x, double y, ElementType type ) {
			if( points.isEmpty() ) {
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

		/**
		 * Returns the {@link io.visualization.BuildingResults.Wall.ElementType} of the controlled class.
 		 *
		 * @param wallSegment the segment of the wall which type should be returned
		 * @return the wall type of the wall segment in the controlled class.
		 */
        @Override
		public ElementType getWallType( int wallSegment ) {
			return wallTypes.get( wallSegment );
		}

        @Override
		public Floor getFloor() {
			return floor;
		}

		@Override
		public Iterator<Point2D.Double> iterator() {
			return Collections.unmodifiableList( points ).iterator();
		}
	}

	public static class Floor implements FloorInterface {
		/** The id of the floor. Normally this is the floor number in the z-format.*/
		private int id;
		/** The getName of the floor. */
		private String name;

		public Floor( int id, String name ) {
			this.id = id;
			this.name = name;
		}

		public int id() {
			return id;
		}

        @Override
		public String getName() {
			return name;
		}

    @Override
    public List<Room> getRooms() {
      throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int roomCount() {
      throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<Room> iterator() {
      throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

        @Override
        public Rectangle getLocation() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
	}
	private LinkedList<Wall> walls;
	private HashMap<Integer, Floor> floors;

	public BuildingResults( de.zet_evakuierung.model.BuildingPlan buildingPlan ) {
		walls = new LinkedList<>();
		floors = new HashMap<>();

		for( FloorInterface zFloor : buildingPlan.getFloors() ) {
			Floor buildingFloor = new Floor( buildingPlan.getFloorID( zFloor ), zFloor.getName() );

			if( zFloor.getRooms().size() >= 0 ) { // TODO: remove as not necessary
				if( floors.put( buildingFloor.id(), buildingFloor ) != null )
					throw new RuntimeException( "Error while building the visualisation data out of the building plan: " + "There were two z-floors with the same id. " );

				for( de.zet_evakuierung.model.Room room : zFloor.getRooms() ) {
					addHeterogeneousEdgeList( room.getPolygon().edgeIterator( false ), room.getPolygon(), buildingFloor );
					for( de.zet_evakuierung.model.Barrier barrier : room.getBarriers() ) {
						Wall w = addHomogeneousEdgeList( barrier.edgeIterator( false ), barrier, buildingFloor, Wall.ElementType.INACCESSIBLE );
						w.setBarrier( true );
					}
					for( de.zet_evakuierung.model.InaccessibleArea area : room.getInaccessibleAreas() )
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

	protected void addHeterogeneousEdgeList( Iterator<? extends de.zet_evakuierung.model.RoomEdge> edgeIt, de.zet_evakuierung.model.PlanPolygon<?> room, Floor floor ) {
		Wall curWall = new Wall( floor );

		if( edgeIt.hasNext() ) {
			de.zet_evakuierung.model.RoomEdge firstEdge = edgeIt.next();
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
			de.zet_evakuierung.model.RoomEdge curEdge = edgeIt.next();
			checkOrientation( curEdge, room, curWall );
			if( curEdge.isPassable() )
				curWall.addPoint( curEdge.getTarget(), Wall.ElementType.PASSABLE );
			else
				curWall.addPoint( curEdge.getTarget(), Wall.ElementType.SIMPLE );
		}
		addWall( curWall );
	}

	protected final Wall addHomogeneousEdgeList( Iterator<? extends de.zet_evakuierung.model.PlanEdge> edgeIt, de.zet_evakuierung.model.PlanPolygon<?> room, Floor floor, Wall.ElementType type ) {
		Wall curWall = new Wall( floor );

		if( edgeIt.hasNext() ) {
			de.zet_evakuierung.model.PlanEdge firstEdge = edgeIt.next();
			checkOrientation( firstEdge, room, curWall );
			curWall.addPoint( firstEdge.getSource(), type );
			curWall.addPoint( firstEdge.getTarget(), type );
		}

		while( edgeIt.hasNext() ) {
			de.zet_evakuierung.model.PlanEdge curEdge = edgeIt.next();
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
	private void checkOrientation( de.zet_evakuierung.model.PlanEdge edge, de.zet_evakuierung.model.PlanPolygon<?> room, Wall curWall ) {
		boolean isLeft = room.relativePolygonPosition( edge, RelativePosition.Left );
		boolean isRight = room.relativePolygonPosition( edge, RelativePosition.Right );
		if( isLeft != isRight ) {
			curWall.roomIsLeft = isLeft;
			curWall.roomIsRight = isRight;
		}// else
		//	System.err.println( "Kante konnte nicht genutzt werden!");
	}
}
