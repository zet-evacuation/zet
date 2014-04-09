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
 * Class JQuickVisualizationView
 * Created 30.04.2008, 09:51:45
 */

package zet.gui.main.tabs;

import de.tu_berlin.coga.zet.model.Floor;
import ds.PropertyContainer;
import de.tu_berlin.coga.zet.model.ZControl;
import gui.GUIControl;
import gui.visualization.control.ZETGLControl;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import javax.swing.JPanel;
import javax.swing.JLabel;
import statistic.ca.CAStatistic;
import zet.gui.main.tabs.base.JFloorScrollPane;
import zet.gui.main.tabs.base.AbstractSplitPropertyWindow;
import zet.gui.main.tabs.quickVisualization.JRasterFloor;
import zet.gui.GUILocalization;
import zet.gui.components.model.FloorComboBox;
import zet.tasks.DisplayFloorTask;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JQuickVisualizationView extends AbstractSplitPropertyWindow<JFloorScrollPane<JRasterFloor>> {
	GUILocalization loc;
	private FloorComboBox<Floor> quickfloorSelector;
	private int selectedFloor = 0;
	private final GUIControl guiControl;
	private Floor currentFloor;
	private Collections ProjectFloors;
	public ZETGLControl control;
	private JLabel lblFloorSelector;
	private int row;
	private JLabel lblFloorNumber;
	private ZControl projectControl;
	private boolean disableUpdate = false;

	public JQuickVisualizationView( GUIControl guiControl ) {
		super( new JFloorScrollPane<>( new JRasterFloor() ) );
		this.guiControl = guiControl;
		loc = GUILocalization.getSingleton();
	}

	public void updateQuickFloorlist() {
		quickfloorSelector.clear();
		quickfloorSelector.displayFloors( projectControl.getProject().getBuildingPlan(), PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) );
	}

	public void changeQuickFloor( Floor floor ) {
		quickfloorSelector.setSelectedItem( floor );
	}

	public void updateQuickFloorView() {
		if( this.disableUpdate )
			return;
		// todo better. do not repaint everything if update is called
		displayFloor( currentFloor );
	}

	public void displayFloor( Floor floor ) {
		//getLeftPanel().getMainComponent().displayFloor( floor );
		DisplayFloorTask tt = new DisplayFloorTask( getLeftPanel().getMainComponent(), floor );
		guiControl.executeTask( tt );
	}

	public void update() {
		quickfloorSelector.removeAllItems();
		for( Floor f : projectControl.getProject().getBuildingPlan().getFloors() )
			quickfloorSelector.addItem( f );
	}

	/**
	 * Returns a panel displayed on the right side of the window.
	 * @return a panel displayed on the right side of the window
	 */
	@Override
	protected JPanel createEastBar() {
		JPanel panel = new JPanel();
		control = new ZETGLControl();
		quickfloorSelector = new FloorComboBox<>();
		//quickfloorSelector.setModel( quickfloorSelectorModel );

		quickfloorSelector.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				if( quickfloorSelector.getSelectedItem() == null )
					return;

				final int add = PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) ? 1 : 0;

				Floor dspFloor = (Floor) quickfloorSelector.getSelectedItem();
				currentFloor = dspFloor;
				updateQuickFloorView();
			}
		} );

		int row = 1;
		loc = GUILocalization.getSingleton();
		lblFloorSelector = new JLabel( loc.getString( "Floors" ) + ":" );
		panel.add( lblFloorSelector, "1, " + row++ );
		panel.add( quickfloorSelector, "1, " + row++ );
		return panel;
	}

	/**
	 * Returns the text for the title bar.
	 * @return the text for the title bar
	 */
	@Override
	protected String getAdditionalTitleBarText() {


		return "";
	}

	/**
	 * 
	 */
	public void localize() {

		// Title of the window
		// Localization of child components
		getLeftPanel().localize();

		loc.setPrefix( "gui.EditPanel." );
		// Localization of own components
		lblFloorSelector.setText( loc.getString( "Etagen" ) + ":" );

		loc.clearPrefix();

		
	}
	
	public void setCAStatistic(CAStatistic cas){
		getLeftPanel().getMainComponent().setCAStatistic(cas);
	}

	final public void displayProject( ZControl projectControl ) {

		if( projectControl != null ) {
			getLeftPanel().getMainComponent().setZcontrol( projectControl );
			getLeftPanel().getMainComponent().displayFloor( null );
		}

		this.projectControl = projectControl;


		updateQuickFloorlist();

		if( PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) )
			if( projectControl.getProject().getBuildingPlan().getFloors().size() >= 2 )
				changeQuickFloor( projectControl.getProject().getBuildingPlan().getFloors().get( 1 ) );
			else
				changeQuickFloor( projectControl.getProject().getBuildingPlan().getFloors().get( 0 ) );
	}

	public ZControl getProjectControl() {
		return projectControl;
	}

	public void displayProject() {
		displayProject( projectControl );
	}

	public Floor getCurrentFloor() {
		return currentFloor;
	}

	public void setFloor( int id ) {
		quickfloorSelector.setSelectedIndex( id );
	}

}


