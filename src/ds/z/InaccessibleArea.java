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
 * InaccessibleArea.java
 * Created on 26. November 2007, 21:32
 */
package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;

/**
 * Implements a single InaccessibleArea. This is an area, which can not be
 * entered.
 * @author Gordon Schlechter
 */
@XStreamAlias( "inaccessibleArea" )
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
	protected PlanPolygon<Edge> createPlainCopy() {
		return new InaccessibleArea( getAssociatedRoom() );
	}

	@Override
	public AreaTypes getAreaType() {
		return AreaTypes.Inaccessible;
	}
}
