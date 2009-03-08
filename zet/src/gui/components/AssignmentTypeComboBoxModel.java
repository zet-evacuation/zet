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
 * Class AssignmentTypeComboBoxModel
 * Erstellt 29.04.2008, 21:30:15
 */
package gui.components;

import ds.Project;
import ds.z.Assignment;
import ds.z.AssignmentArea;
import ds.z.AssignmentType;
import ds.z.event.ChangeListener;
import gui.editor.JFloor;
import gui.editor.JPolygon;
import javax.swing.DefaultComboBoxModel;

/**
 * This class serves as a model for the JComboBox that contains the current assignment types.
 * @author Jan-Philipp Kappmeier
 */
public class AssignmentTypeComboBoxModel extends DefaultComboBoxModel implements ChangeListener {
	Assignment oldAssignment = null;
	Project myProject;
	boolean itemChange = false;
	JFloor floorPanel;

	public AssignmentTypeComboBoxModel( Project project ) {
		super();
		myProject = project;
	}

	@Override
	public void stateChanged( ds.z.event.ChangeEvent e ) {
		if( (e.getSource() instanceof Project ||
						e.getSource() instanceof Assignment ||
						e.getSource() instanceof AssignmentType) && !itemChange ) {
			displayAssignmentTypesForCurrentProject();
		}
	}

	public void displayAssignmentTypesForCurrentProject() {
		//if( myProject != null && myProject.getCurrentAssignment().equals( oldAssignment )  )
		//	return;

		clear();

		if( myProject != null && myProject.getCurrentAssignment() != null ) {
			for( AssignmentType a : myProject.getCurrentAssignment().getAssignmentTypes() ) {
				addElement( a );			// This will only enter me into the list, if i'm not already entered
			}
			myProject.addChangeListener( this );
		}
	}

	public void clear() {
		if( myProject != null ) {
			myProject.removeChangeListener( this );
		}
		removeAllElements();
	}

	@Override
	public void setSelectedItem( Object object ) {
		super.setSelectedItem( object );

		if( floorPanel != null && floorPanel.getSelectedPolygons() != null ) {
			itemChange = true;
			//AssignmentType old = ( (AssignmentArea)floorPanel.getSelectedPolygon().getPlanPolygon() ).getAssignmentType();
			for( JPolygon poly : floorPanel.getSelectedPolygons() ) {
				if( poly.getPlanPolygon() instanceof AssignmentArea ) {
					((AssignmentArea) poly.getPlanPolygon()).setAssignmentType( (AssignmentType) object );
				}
			}
			//AssignmentType mewA = ( (AssignmentArea)floorPanel.getSelectedPolygon().getPlanPolygon() ).getAssignmentType();
			itemChange = false;
		}
	}

	public void setProject( Project p ) {
		if( myProject != null ) {
			myProject.removeChangeListener( this );
		}
		myProject = p;
	}

	public void setFloorPanel( JFloor floorPanel ) {
		this.floorPanel = floorPanel;
	}
}