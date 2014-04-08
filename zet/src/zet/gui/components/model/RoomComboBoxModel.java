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
/**
 * Class RoomComboBoxModel
 * Erstellt 29.04.2008, 21:30:27
 */
package zet.gui.components.model;

import de.tu_berlin.coga.zet.model.Floor;
import de.tu_berlin.coga.zet.model.Room;
import de.tu_berlin.coga.zet.model.ZControl;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * This class serves as a model for the JComboBox that contains the current rooms.
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class RoomComboBoxModel extends DefaultComboBoxModel<Room> {
	private List<Room> entries = new LinkedList<>();
	private boolean initializing;
	private FloorComboBox<Floor> floorSelector;
	private boolean disableUpdate = false;

	public RoomComboBoxModel( ZControl zcontrol, FloorComboBox<Floor> floorSelector ) {
		super();
		this.floorSelector = floorSelector;
	}

	public void displayRoomsForCurrentFloor() {
		if( disableUpdate )
			return;
		initializing = true;
		try {
			clear();
			if( floorSelector.getSelectedItem() != null )
				for( Room r : ((Floor)floorSelector.getSelectedItem()).getRooms() ) {
					addElement( r );
					entries.add( r );
				}
		} finally {
			initializing = false;
		}
	}

	public void clear() {
		entries.clear();

		removeAllElements();
	}

	@Override
	public void setSelectedItem( Object object ) {
		if( !initializing )
			super.setSelectedItem( object );
	}
}
