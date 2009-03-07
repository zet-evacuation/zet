/*
 * BaseRoom.java
 * Created on 18.12.2007, 11:37:51
 */

package ds.z;

/**
 *
 * @param <T> The type of the edges (walls) of this room.
 * @author Jan-Philipp Kappmeier
 */
public class BaseRoom<T extends RoomEdge> extends PlanPolygon<T> {
	BaseRoom( Class<T> edgeClassType ) {
		super( edgeClassType );
	}
}
