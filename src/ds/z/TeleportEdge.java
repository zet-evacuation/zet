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
 * TeleportEdge.java
 * Created on 26. November 2007, 21:32
 */
package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/** A specialized case of a RoomEdge, where we do not want to model a door, but rather a stair,
 *  that is, the linkTarget of the Edge is set to another Edge which must be located on a different floor.
 * @author Timon Kelter
 */
@XStreamAlias ("teleportEdge")
public class TeleportEdge extends RoomEdge {
	/** This is a helper flag that is used during methods were the link target of this edge
	 * should not be transformed to a standard edge when deleting this edge. This behaviour
	 * is useful in methods like Room.replaceEdge.
	 */
	@XStreamOmitField ()
	private transient boolean revertLinkTargetOnDelete = true;

	TeleportEdge (PlanPoint p1, PlanPoint p2) {
		super (p1, p2);
		ensureMatchWithLinkTarget = false;
	}

	public TeleportEdge (PlanPoint p1, PlanPoint p2, Room p) {
		super (p1, p2, p);
		ensureMatchWithLinkTarget = false;
	}

	public TeleportEdge (PlanPoint p1, PlanPoint p2, Room p, TeleportEdge t) {
		super (p1, p2, p);

		// This must precede the setLinkTarget operation
		ensureMatchWithLinkTarget = false;
		setLinkTarget (t);
	}

	@Override
	public TeleportEdge getLinkTarget () {
		return (TeleportEdge) super.getLinkTarget ();
	}

	/** {@inheritDoc}
	 * 
	 * In addition to the superclass' delete method this one also must take care
	 * of reverting the linkTarget TeleportEdge to a normal RoomEdge. This overwrites 
	 * the behaviour defined in {@link RoomEdge#delete} 
	 */
	@Override ()
	public void delete () {
		if (revertLinkTargetOnDelete && linkTarget != null) {
			// Save old values of linkTarget
			Room r = linkTarget.getRoom ();
			PlanPoint p1 = linkTarget.getSource ();
			PlanPoint p2 = linkTarget.getTarget ();

			// Delete linkTarget
			linkTarget.linkTarget = null; // Prevent endless recursion
			linkTarget.delete ();
			linkTarget = null;

			// Create replacement for linkTarget
			new RoomEdge (p1, p2, r);
		}

		super.delete ();
	}

	/**
	 * It is allowed to set a linkTarget, which is not valid for a moment.
	 */
	@Override
	public void setLinkTarget (RoomEdge val) {
		if (val != null) {
			if (!(val instanceof TeleportEdge)) {
				throw new IllegalArgumentException (ZLocalization.getSingleton (
						).getString("ds.z.TeleportEdge.OnlyTeleportEdgesAsTarget"));
			} else if (val.length () != length ()) {
				throw new IllegalArgumentException (ZLocalization.getSingleton (
						).getString("ds.z.TeleportEdge.DifferentLengthLinkTarget"));
			}
		}
		super.setLinkTarget (val);
	}
	
	/** @return The helper flag {@link #revertLinkTargetOnDelete} */
	protected boolean revertLinkTargetOnDelete () { return revertLinkTargetOnDelete; }
	/** Sets the helper flag {@link #revertLinkTargetOnDelete} */
	protected void setRevertLinkTargetOnDelete (boolean b) { revertLinkTargetOnDelete = b; }
	
	/** {@inheritDoc} */
	@Override
	public void makeImpassable () {
		Room myRoom = getRoom ();
		
		delete (); // Replaces the edge at the link target
		myRoom.close ();
	}
}
