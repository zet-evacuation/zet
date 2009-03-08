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
/**
 * Class RoomComboBoxModel
 * Erstellt 29.04.2008, 21:30:27
 */

package gui.components;

import ds.Project;
import ds.z.Floor;
import ds.z.Room;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
	/**
	 * This class serves as a model for the JComboBox that contains the current rooms.
	 */
	public class RoomComboBoxModel extends DefaultComboBoxModel implements ds.z.event.ChangeListener {
		private List<Room> entries = new LinkedList<Room>();
		private boolean initializing;
		private FloorComboBoxModel floorSelector;
		private boolean disableUpdate = false;
		
		public RoomComboBoxModel( Project project, FloorComboBoxModel floorSelector ) {
			super();
			this.floorSelector = floorSelector;
		}

		@Override
		public void stateChanged( ds.z.event.ChangeEvent e ) {
			if( ( (Floor)floorSelector.getSelectedItem() ).getRooms().size() == entries.size() )
				// Roomname changed
				if( e.getSource() instanceof Room ) {
					int index = entries.indexOf( (Room)e.getSource() );
					this.fireContentsChanged( this, index, index );
				}
			else
				// Number of rooms changed
				displayRoomsForCurrentFloor();
		}

		public void displayRoomsForCurrentFloor() {
			if( disableUpdate )
				return;
			initializing = true;
			try {
				clear();
				
				for( Room r : ( (Floor)floorSelector.getSelectedItem() ).getRooms() ) {
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
			if( !initializing ) {
				super.setSelectedItem( object );
			}
		}
	}
