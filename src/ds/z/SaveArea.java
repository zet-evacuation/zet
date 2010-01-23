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
 * SaveArea.java
 * Created on 26. November 2007, 21:32
 */

package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Represents a SaveArea. 
 * A SaveArea is an area, where the evacuees are save, but they have still an 
 * influence on other evacuees. Every SaveArea is associated to exactly one 
 * {@link Room} at every time.
 */
@XStreamAlias("saveArea")
public class SaveArea extends Area<Edge> {
    
    /**
     * Creates a new instance of {@link SaveArea }.
     * Sets room.
     * @param room to which the area belongs.
     */
  public SaveArea(Room room) {
      super ( Edge.class, room);
  }
  
	/** This method copies the current polygon without it's edges. Every other setting, as f.e. the floor
	 * for Rooms or the associated Room for Areas is kept as in the original polygon. */
	@Override
	protected PlanPolygon<Edge> createPlainCopy () {
		return new SaveArea (getAssociatedRoom ());
	}
}
