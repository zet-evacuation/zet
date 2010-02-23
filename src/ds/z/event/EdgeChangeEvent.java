///* zet evacuation tool copyright (c) 2007-10 zet evacuation team
// *
// * This program is free software; you can redistribute it and/or
// * as published by the Free Software Foundation; either version 2
// * of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
// */
//package ds.z.event;
//
//import ds.z.Edge;
//import ds.z.PlanPoint;
//
///** This event is thrown whenever a change to a PlanPoint, that belongs to an Edge
// * is made. In this case the Edge catches the PlanPoint's event and rethrows an
// * EdgeChangeEvent.
// *
// * @author Timon Kelter
// */
//public class EdgeChangeEvent extends ChangeEvent {
//	protected PlanPoint target;
//
//	/** @param source The edge that was changed
//	 * @param target This field gives the user the affected PlanPoint, of it is
//	 * null, if both PlanPoints have been edited. */
//	public EdgeChangeEvent (Edge source, PlanPoint target) {
//		this (source, target, null);
//	}
//	/** @param source The edge that was changed
//	 * @param target This field gives the user the affected PlanPoint, of it is
//	 * null, if both PlanPoints have been edited.
//	 * @param message A message that describes the event
//	 */
//	public EdgeChangeEvent (Edge source, PlanPoint target, String message) {
//		super (source, message);
//		this.target = target;
//	}
//
//	/** The edge that was changed */
//	public Edge getEdge () {
//		return (Edge)source;
//	}
//
//	/** This field gives the user the affected PlanPoint, of it is
//	 * null, if both PlanPoints have been edited. */
//	public PlanPoint getTarget () {
//		return target;
//	}
//}