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
 * Class AssignmentTypeComboBoxModel
 * Created 29.04.2008, 21:30:15
 */
package zet.gui.components.model;

import de.tu_berlin.coga.zet.model.Assignment;
import de.tu_berlin.coga.zet.model.AssignmentType;
import de.tu_berlin.coga.zet.model.ZControl;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import zet.gui.main.tabs.editor.JFloor;

/**
 * This class serves as a model for the {@link JComboBox} that contains the
 * current assignment types.
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class AssignmentTypeComboBoxModel extends DefaultComboBoxModel<AssignmentType> {
	Assignment oldAssignment = null;
	ZControl zcontrol;
	boolean itemChange = false;
	JFloor floorPanel;

	public AssignmentTypeComboBoxModel( ZControl zcontrol ) {
		super();
		this.zcontrol = zcontrol;
	}

	// TODO-Event
//	@Override
//	public void stateChanged( ds.z.event.ChangeEvent e ) {
//		if( (e.getSource() instanceof Project ||
//						e.getSource() instanceof Assignment ||
//						e.getSource() instanceof AssignmentType) && !itemChange ) {
//			displayAssignmentTypesForCurrentProject();
//		}
//	}

	public void displayAssignmentTypesForCurrentProject() {
		//if( zcontrol != null && zcontrol.getCurrentAssignment().equals( oldAssignment )  )
		//	return;

		clear();

		if( zcontrol != null && zcontrol.getProject().getCurrentAssignment() != null ) {
			for( AssignmentType a : zcontrol.getProject().getCurrentAssignment().getAssignmentTypes() )
				addElement( a );			// This will only enter me into the list, if i'm not already entered
		}
	}

	public void clear() {
		if( zcontrol != null ) {
//			zcontrol.removeChangeListener( this );
		}
		removeAllElements();
	}

	@Override
	public void setSelectedItem( Object object ) {
		super.setSelectedItem( object );

//		if( floorPanel != null && floorPanel.getSelectedPolygons() != null ) {
//			itemChange = true;
//			//AssignmentType old = ( (AssignmentArea)floorPanel.getSelectedPolygon().getPlanPolygon() ).getAssignmentType();
//			for( JPolygon poly : floorPanel.getSelectedPolygons() ) {
//				if( poly.getPlanPolygon() instanceof AssignmentArea ) {
//					((AssignmentArea) poly.getPlanPolygon()).setAssignmentType( (AssignmentType) object );
//				}
//			}
//			//AssignmentType mewA = ( (AssignmentArea)floorPanel.getSelectedPolygon().getPlanPolygon() ).getAssignmentType();
//			itemChange = false;
//		}
	}

	public void setControl( ZControl zcontrol ) {
//		if( zcontrol != null ) {
//			zcontrol.removeChangeListener( this );
//		}
		this.zcontrol = zcontrol;
	}

	public void setFloorPanel( JFloor floorPanel ) {
		this.floorPanel = floorPanel;
	}
	/** Prohibits serialization. */
	private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
		throw new UnsupportedOperationException( "Serialization not supported" );
	}
}