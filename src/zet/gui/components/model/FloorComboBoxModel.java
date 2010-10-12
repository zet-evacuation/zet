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
 * Class FloorComboBoxModel
 * Erstellt 29.04.2008, 21:29:56
 */

package zet.gui.components.model;

import ds.Project;
import ds.PropertyContainer;
import ds.z.Floor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;

/**
 * This class serves as a model for the JComboBox that contains the current floors.
 * 
 * ## This class is used in too many different environments. Sometimes it contains Strings, 
 * ## sometimes Floors, and there are other inconsitencies. Thus this class should be re-integrated 
 * ## (as a nested class) into the classes who use it.
 * 
 * @author Jan-Philipp Kappmeier
 */
public class FloorComboBoxModel extends DefaultComboBoxModel {
	private List<Floor> entries = new LinkedList<Floor>();
	RoomComboBoxModel roomSelector = null;
	private HashMap<Integer, Integer> idToIndex;

	// TODO-Event
//	@Override
//	public void stateChanged( ds.z.event.ChangeEvent e ) {
//		if( e.getSource() instanceof Floor ) {
//			// Floorname possibly changed
//			int index = entries.indexOf( (Floor) e.getSource() );
//			this.fireContentsChanged( this, index, index );
//		}
//	}

	public void displayFloors( Project p ) {
		clear();

		if( p != null ) {
			for( Floor f : p.getBuildingPlan().getFloors() ) {
				if( PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) ) {
					if( !f.equals( p.getBuildingPlan().getDefaultFloor() ) ) {
						addElement( f );
						entries.add( f );
//						f.addChangeListener( this );
					}
				} else {
					addElement( f );
					entries.add( f );//
//					f.addChangeListener( this );
				}
			}
		}	
	}

	public void displayFloors( Map<Integer, String> floors ) {
		clear();
		idToIndex = new HashMap<Integer, Integer>();
		int index = 0;
		for( int a : floors.keySet() ) {
			idToIndex.put( index++, a );
			addElement( floors.get( a ) );
		}
	}
	
	public int getFloorIDFromIndex( int index ) {
		if( idToIndex != null && index >= 0 )
			return idToIndex.get( index );
		else
			return -1;
	}

	public void clear() {
//		if( getSelectedItem() != null  && getSelectedItem () instanceof Floor)
//			((Floor) getSelectedItem()).removeChangeListener( roomSelector );

//		for( Floor f : entries )
//			f.removeChangeListener( this );
		entries.clear();

		removeAllElements();
	}

	public void setRoomSelector( RoomComboBoxModel roomSelector ) {
		this.roomSelector = roomSelector;
	}
}