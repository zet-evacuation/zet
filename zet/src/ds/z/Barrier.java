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
/*
 * Barrier.java
 * Created on 17.12.2007, 21:08:19
 */
package ds.z;

import ds.z.exception.PolygonNotClosedException;
import ds.z.exception.PolygonNotRasterizedException;
import java.util.ArrayList;

/**
 * The {@code Barrier} class represents a special inaccessible area, that
 * is not closed but only an arbitrary polygonial chain. However, it can be
 * closed.
 * @author Jan-Philipp Kappmeier
 */
public class Barrier extends InaccessibleArea {
	
	/**
	 * Creates a new instance of {@code Barrier} in a specified room.
	 * @param room the room
	 */
	public Barrier( Room room ) {
		super( room );
	}
	
	/**
	 * Checks if this {@link PlanPolygon} describing the {@code Barrier} is
	 * valid. That means, that it is simple and has no self-cuts. If any invalid
	 * positions are found, an exception is thrown. If the param rasterized is
	 * true, it also checks if the polygon is really rasterized with a call of
	 * {@link PlanPolygon#checkRasterized() }).<b>Note</b> that the polygon can be
	 * open, contrary to {@link ds.z.InaccessibleArea}.
	 * <p>The runtime of this operation is O(n), where {@code n} is the
	 * number of edges.</p>
	 * @param rasterized indicates that the {@link BuildingPlan} should be
	 * rasterized
	 * @throws ds.z.exception.PolygonNotRasterizedException if the polygon is not
	 * rasterized but should be
	 */
	@Override
	public void check( boolean rasterized ) throws PolygonNotRasterizedException {
		try {
			super.check( rasterized );
		} catch( PolygonNotClosedException e ) {
			// do nothing, as it is allowed not to be closed
		}
	}
        
    	
	/** This method copies the current polygon without it's edges. Every other setting, as f.e. the floor
	 * for Rooms or the associated Room for Areas is kept as in the original polygon. */
	@Override
	protected PlanPolygon<Edge> createPlainCopy () {
		return new Barrier (getAssociatedRoom ());
	}
	
	/**
	 * Closes the barrier by adding edges back to the start node, so that the barrier 
	 * will seem as a single line.
	 */
	@Override
	public void close () throws IllegalArgumentException, IllegalStateException {
		if (!isClosed ()) {
			ArrayList<PlanPoint> points = new ArrayList<PlanPoint> (getPolygonPoints ());
			
			// Use the list stacklike
			for (int i = points.size () - 2; i >= 0; i--) {
				// Copy the PlanPoints - Otherwise we will get errors because we are 
				// trying to use the points twice in the polygon
				newEdge (getEnd (), new PlanPoint (points.get (i)));
			}
		}
	}

	@Override
	public AreaTypes getAreaType() {
		return AreaTypes.Barrier;
	}
}