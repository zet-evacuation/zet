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

package zet.gui.components.tabs;

import ds.z.Floor;
import zet.gui.components.tabs.base.JFloorScrollPane;
import zet.gui.components.tabs.base.AbstractSplitPropertyWindow;
import javax.swing.JButton;
import javax.swing.JPanel;

import statistic.ca.CAStatistic;
import zet.gui.components.tabs.quickVisualization.JRasterFloor;

import javax.swing.JComboBox;
import zet.gui.components.model.FloorComboBoxModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ds.PropertyContainer;
import gui.GUIControl;
import gui.visualization.control.GLControl;
import zet.gui.components.model.ComboBoxRenderer;
import java.awt.Component;
import javax.swing.JList;
import java.util.Collections;
import javax.swing.JLabel;
import zet.gui.GUILocalization;
import ds.z.ZControl;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JQuickVisualizationView extends AbstractSplitPropertyWindow<JFloorScrollPane<JRasterFloor>> {
	GUILocalization loc;

        private JComboBox QuickfloorSelector;
	private FloorComboBoxModel QuickfloorSelectorModel;
        private int selectedFloor = 0;
        private final GUIControl guiControl;
        private Floor currentFloor;
        private Collections ProjectFloors;
        public GLControl control;
        private JLabel lblFloorSelector;
        private int row;
        private JLabel lblFloorNumber;
        private ZControl projectControl;
        private boolean disableUpdate = false;

   
	public JQuickVisualizationView(GUIControl guiControl ) {

                
		super( new JFloorScrollPane<JRasterFloor>( new JRasterFloor() ) );
                this.guiControl = guiControl;
                loc = GUILocalization.getSingleton();
	}

        public void updateQuickFloorlist() {
		QuickfloorSelectorModel.clear();
		QuickfloorSelectorModel.displayFloors( projectControl.getProject() );
	}

        public void changeQuickFloor( Floor floor ) {
		QuickfloorSelectorModel.setSelectedItem( floor );
	}

	public void updateQuickFloorView() {
		if( this.disableUpdate )
			return;
		currentFloor = (Floor)QuickfloorSelectorModel.getSelectedItem();
		getLeftPanel().getMainComponent().displayFloor( currentFloor );
		
	}
		
	

	public void displayFloor( Floor floor ) {
		getLeftPanel().getMainComponent().displayFloor( floor );
	}



        public void update() {
             QuickfloorSelector.removeAllItems();
		for( Floor f : projectControl.getProject().getBuildingPlan().getFloors() )
			 
				QuickfloorSelector.addItem(f);
	}

        


	/**
	 * Returns a panel displayed on the right side of the window.
	 * @return a panel displayed on the right side of the window
	 */
	@Override
	protected JPanel createEastBar() {
		JPanel panel = new JPanel();
		JButton button = new JButton( "Klick mich" );
		panel.add( button );
                control = new GLControl();
                 /** try */
                
                //ProjectFloors = new Collections();
               
		QuickfloorSelectorModel = new FloorComboBoxModel();
                QuickfloorSelector = new JComboBox(QuickfloorSelectorModel);
		QuickfloorSelector.setModel( QuickfloorSelectorModel );
               // QuickfloorSelectorModel.displayFloors( projectControl.getProject() );


		QuickfloorSelector.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
                            if( QuickfloorSelector.getSelectedItem() != null )
;//					((Floor)cbxFloors.getSelectedItem()).removeChangeListener( roomSelector );
				else
					return;

				final int add = PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) ? 1 : 0;
                                //lblFloorNumber.setText( String.format( loc.getStringWithoutPrefix( "gui.EditPanel.Default.OnFloor" ), QuickfloorSelector.getSelectedIndex() + add ) );

				Floor dspFloor = (Floor)QuickfloorSelector.getSelectedItem();
				currentFloor = dspFloor;
				updateQuickFloorView();

				}

			});

                   	QuickfloorSelector.setRenderer( new ComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus ); // Needed for correct displaying! Forget return
				if( value != null )
					setText( ((Floor)value).getName() );
				return this;
			}
		} );
                int row = 1;
                loc = GUILocalization.getSingleton();
                lblFloorSelector = new JLabel( loc.getString( "Floors" ) + ":" );
		panel.add( lblFloorSelector, "1, " + row++ );
		panel.add( QuickfloorSelector, "1, " + row++ );
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
		lblFloorSelector.setText( loc.getString( "Floors" ) + ":" );

		loc.clearPrefix();

		
	}
	
	public void setCAStatistic(CAStatistic cas){
		getLeftPanel().getMainComponent().setCAStatistic(cas);
	}


        final public void displayProject( ZControl projectControl ) {
		
		if( projectControl != null ) {

                        getLeftPanel().getMainComponent().setZcontrol( projectControl );
			getLeftPanel().getMainComponent().displayFloor(null);
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
		QuickfloorSelector.setSelectedIndex( id );
	}

}


