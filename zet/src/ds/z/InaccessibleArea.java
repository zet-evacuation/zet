/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * InaccessibleArea.java
 * Created on 26. November 2007, 21:32
 */

package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import ds.z.Barrier;
import java.util.ArrayList;

/**
 * Implements a single InaccessibleArea. This is an area, which can not be
 * entered.
 * @author Gordon Schlechter
 */
@XStreamAlias("inaccessibleArea")
public class InaccessibleArea extends Area<Edge> {
    
    /**
     * Creates a new instance of {@link InaccessibleArea } contained in a
     * specified {@link ds.z.Room}.
     * @param room the room
     */
  public InaccessibleArea( Room room ) {
      super( Edge.class, room );
  }
  
	/** This method copies the current polygon without it's edges. Every other setting, as f.e. the floor
	 * for Rooms or the associated Room for Areas is kept as in the original polygon. */
	@Override
	protected PlanPolygon<Edge> createPlainCopy () {
		return new InaccessibleArea (getAssociatedRoom ());
	}
	
    /**
     * special cleanUp for InaccessibleAreas.
     * Deletes all "thorns"(=pairs of neighboured edges, one from a to b and the other from b to a),
     * but converts them to Barriers (if the Barrier is inside the room and outside the area).
     * Deletes all edges that are made up of the same two points and
     * combines all pairs of edges that are combineable
     * (=all x-coordinates OR all y-coordinates of both edges are the same).
     * If this reduces the edges length, it converts the deleted part in a barrier
     * (if the Barrier is inside the room and outside the area).
     * @return true, if at least one part of this inaccessible area was converted to a barrier by this method
     */
  boolean cleanUpForInaccessibleAreas() {
            
//ToDo: bearbeiten, auch den Kommentar!!!
            PlanPoint p1,p2,p3;
            Edge e1,e2,temp1,temp2;
            e1=getFirstEdge();
            e2=(e1.getTarget().getNextEdge());
            p1=e1.getSource();
            p2=e1.getTarget();
            p3=e2.getTarget();
            
            boolean lastStepsBegin = false;
            boolean lastSteps =false;
            
            Barrier b =null;
            ArrayList<PlanPoint> barrierPoints=new ArrayList<PlanPoint>();
            boolean convertedToBarrier=false;
            boolean createBarrier=false;
            
            boolean xCoordinatesTheSame=false;
            boolean yCoordinatesTheSame=false;
            boolean xCoordinateOfP2NotBetweenThoseOfP1AndP3=false;
            boolean yCoordinateOfP2NotBetweenThoseOfP1AndP3=false;
            
            while (getNumberOfEdges()>1) {
                // for the breaking condition:
                if (e2.equals(getLastEdge())) {
                    lastStepsBegin=true;
                }
                if (lastStepsBegin && e1.equals(getLastEdge())) {
                    lastSteps=true;
                }
                
                // if the edges e1 and e2 are a "thorn":
                // create a barrier of the thorn if it is inside the room and outside the area

                if (p1.equals(p3)) {
                    //creates a barrier object
                    b=new Barrier(getAssociatedRoom());
                    barrierPoints.add(p1);
                    barrierPoints.add(p2);
                    b.replace(barrierPoints);
                    // deletes the thorn
                    temp1=e1.getSource().getPreviousEdge();
                    temp2=e2.getTarget().getNextEdge();
                    combineEdges((Edge)e1,(Edge)e2,false);
                    // if the barrier is inside the room and outside the inaccessible area:
                    // all is good!
                    if (!this.contains(b) && getAssociatedRoom().contains(b)) {
                        convertedToBarrier=true;
                    }
                    // else: delete the barrier!!
                    else {
                        b.delete();
                    }
                    
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
                xCoordinatesTheSame =  ( (p1.getXInt()==p2.getXInt())&&(p2.getXInt()==p3.getXInt()) );
                yCoordinatesTheSame = ( (p1.getYInt()==p2.getYInt())&&(p2.getYInt()==p3.getYInt()) );
                xCoordinateOfP2NotBetweenThoseOfP1AndP3 =  ( (p2.getXInt()>p1.getXInt()) && (p2.getXInt()>p3.getXInt()) ) || ( (p2.getXInt()<p1.getXInt()) && (p2.getXInt()<p3.getXInt()) );
                yCoordinateOfP2NotBetweenThoseOfP1AndP3 =  ( (p2.getYInt()>p1.getYInt()) && (p2.getYInt()>p3.getYInt()) ) || ( (p2.getYInt()<p1.getYInt()) && (p2.getYInt()<p3.getYInt()) );

                if (xCoordinatesTheSame || yCoordinatesTheSame) {
                    
                    if (xCoordinatesTheSame){
                        if (yCoordinateOfP2NotBetweenThoseOfP1AndP3) {
                            createBarrier=true;
                        }
                    }
                    
                    if (yCoordinatesTheSame){
                        if (xCoordinateOfP2NotBetweenThoseOfP1AndP3) {
                            createBarrier=true;
                        }
                    }
                    
                    if (createBarrier) {
                        b=new Barrier(getAssociatedRoom());
                        if (e1.length()<e2.length()) {
                            barrierPoints.add(p1);
                            barrierPoints.add(p2);
                        }
                        else {
                            barrierPoints.add(p2);
                            barrierPoints.add(p3);
                        }
                        b.replace(barrierPoints);
                    }
                        
                    e1=(Edge)combineEdges(e1,e2,false);
                    e2=e1.getTarget().getNextEdge();
                    p2=e1.getTarget();
                    p3=e2.getTarget();
                    
                    if (createBarrier) {
                        if (!this.contains(b) && getAssociatedRoom().contains(b)) {
                            convertedToBarrier=true;
                        }
                        // else: delete the barrier!!
                        else {
                            b.delete();
                        }
                        createBarrier=false;
                    }
                    
                    
                }//end if the edges are combineable
                
                
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
                

                 
            }//end while
            
            return convertedToBarrier;  
        }
  
  

  
  
}
