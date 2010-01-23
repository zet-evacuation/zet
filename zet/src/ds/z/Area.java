/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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

/*
 * Area.java
 * Created on 26. November 2007, 21:32
 */

package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import localization.Localization;

/**
 *
 * @param <T> the edge type
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias( "area" )
public abstract class Area<T extends Edge> extends PlanPolygon<T> {
	private Room associatedRoom;

	Area( Class<T> edgeClassType, Room associatedPolygon ) {
		super( edgeClassType );
		setAssociatedRoom( associatedPolygon );
	}

	/**
	 * Deletes the area.
	 */
	@Override
	public void delete() {
		getAssociatedRoom().deleteArea( this );
		associatedRoom = null;
		
		// SHORT NOTE: In constructor: Call superconstruktor at the begin
		//             In  destructor: Call  superdestruktor at the end
		// MORE DETAIL:
		// super.delete must be called at last, because otherwise the polygon 
		// will have been deleted at the time when you want to tell the associated
		// room that you want to delete this area. this will lead to an exception,
		// because Room.deleteArea() uses the equals method to determine which
		// area it must delete. as we would have deleted the polygon edges before
		// the call to Room.deleteArea() this equals method will throw a 
		// null pointer exception.
		super.delete();
	}
	
	/**
	 * Returns the associatedRoom of the area.
	 * @return associatedRoom of the area.
	 */
	public Room getAssociatedRoom() {
		return associatedRoom;
	}

	/**
	 * This operation takes care of setting the room that is associated to this
	 * area. It also conserves the consistence with the area lists in the rooms.
	 * @param room is the associated room of the area.
	 * @throws java.lang.IllegalArgumentException when val = null.
	 */
	public void setAssociatedRoom( Room room ) throws IllegalArgumentException {
		if( room == null ) {
			throw new IllegalArgumentException( Localization.getInstance().getString("ds.z.Area.NoRoomException") );
		}

		if(associatedRoom != null ) {
			associatedRoom.deleteArea( this );
		}
		associatedRoom= room;
		associatedRoom.addArea( this );
	}

	@Override
	public boolean equals( Object o ) {
		if( o instanceof Area ) {
			Area p = (Area)o;
			return super.equals( p ) && ( (associatedRoom == null ) ? p.getAssociatedRoom() == null :associatedRoom.equals( p.getAssociatedRoom() ) );
		} else {
			return false;
		}
	}
        
    /**
     * cleanUp in Areas.
     * Deletes all "thorns"(=pairs of neighboured edges, one from a to b and the other from b to a),
     * deletes all edges that are made up of the same two points and
     * combines all pairs of edges that are combineable (=all x-coordinates OR all y-coordinates
     * of both edges are the same).
     */
        void cleanUpForAreas() {
            
            // cleanUp for Areas
            PlanPoint p1,p2,p3;
            Edge e1,e2,temp1,temp2;
            e1=getFirstEdge();
            e2=(e1.getTarget().getNextEdge());
            p1=e1.getSource();
            p2=e1.getTarget();
            p3=e2.getTarget();
            
            boolean lastStepsBegin = false;
            boolean lastSteps =false;
            
            boolean combineable = false;
            
            while (getNumberOfEdges()>1) {
                // for the breaking condition:
                if (e2.equals(getLastEdge())) {
                    lastStepsBegin=true;
                }
                if (lastStepsBegin && e1.equals(getLastEdge())) {
                    lastSteps=true;
                }

                               
                
                // if the edges e1 and e2 are a "thorn":
                if (p1.equals(p3)) {
                    temp1=e1.getSource().getPreviousEdge();
                    temp2=e2.getTarget().getNextEdge();
                    combineEdges((T)e1,(T)e2,false);
                    if (getNumberOfEdges() == 0) {
                        break;
                    }
                    else {
                        e1=temp1;
                        e2=temp2;
                        p1=e1.getSource();
                        p2=e1.getTarget();
                        p3=e2.getTarget();
                    }
                    continue;
                }
                
                
                // if the points of one edge are the same:
                // edge e1:
                if (p1.equals(p2)) {
                    e1=e1.getSource().getPreviousEdge();
                    e1.getTarget().getNextEdge().delete();
                    p1=e1.getSource();
                    continue;
                }
                // edge e2:
                if (p2.equals(p3)) {
                    e2=e2.getTarget().getNextEdge();
                    e2.getSource().getPreviousEdge().delete();
                    p3=e2.getTarget();
                    continue;
                }
                
                // if the edges are combineable
                combineable = ( ( (p1.getXInt()==p2.getXInt())&&(p2.getXInt()==p3.getXInt()) )  ||  ( (p1.getYInt()==p2.getYInt())&&(p2.getYInt()==p3.getYInt()) ) );

                if (combineable) {
                    e1=(Edge)combineEdges((T)e1,(T)e2,false);
                    e2=e1.getTarget().getNextEdge();
                    p2=e1.getTarget();
                    p3=e2.getTarget();
                }
                //if none of the cases above is true
                else {
                    if (lastSteps) {
                        break;
                    }
                    else {
                        e1=e1.getTarget().getNextEdge();
                        e2=e2.getTarget().getNextEdge();
                        p1=e1.getSource();
                        p2=e1.getTarget();
                        p3=e2.getTarget();
                    }
                }

                 
            }
            

                
            

            
        }
       
}
