/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

package zet.gui.main.tabs;

import de.tu_berlin.coga.common.localization.Localization;
import de.tu_berlin.math.coga.components.JRuler;
import ds.PropertyContainer;
import de.tu_berlin.coga.zet.model.AssignmentArea;
import de.tu_berlin.coga.zet.model.DelayArea;
import de.tu_berlin.coga.zet.model.EvacuationArea;
import de.tu_berlin.coga.zet.model.Floor;
import de.tu_berlin.coga.zet.model.PlanPolygon;
import de.tu_berlin.coga.zet.model.Room;
import de.tu_berlin.coga.zet.model.StairArea;
import de.tu_berlin.coga.zet.model.TeleportArea;
import de.tu_berlin.coga.zet.model.ZControl;
import de.tu_berlin.coga.zet.model.ZModelRoomEvent;
import event.EventServer;
import gui.GUIControl;
import gui.GUIOptionManager;
import gui.editor.Areas;
import info.clearthought.layout.TableLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import zet.gui.GUILocalization;
import zet.gui.components.model.ComboBoxRenderer;
import zet.gui.components.model.FloorComboBox;
import zet.gui.components.model.RoomComboBoxModel;
import zet.gui.main.tabs.base.AbstractSplitPropertyWindow;
import zet.gui.main.tabs.base.JFloorScrollPane;
import zet.gui.main.tabs.base.JPolygon;
import zet.gui.main.tabs.editor.EditStatus;
import zet.gui.main.tabs.editor.JAssignmentAreaInformationPanel;
import zet.gui.main.tabs.editor.JDefaultInformationPanel;
import zet.gui.main.tabs.editor.JDelayAreaInformationPanel;
import zet.gui.main.tabs.editor.JEdgeInformationPanel;
import zet.gui.main.tabs.editor.JEvacuationAreaInformationPanel;
import zet.gui.main.tabs.editor.JFloor;
import zet.gui.main.tabs.editor.JFloorInformationPanel;
import zet.gui.main.tabs.editor.JInformationPanel;
import zet.gui.main.tabs.editor.JRoomInformationPanel;
import zet.gui.main.tabs.editor.JStairAreaInformationPanel;
import zet.gui.main.tabs.editor.JTeleportAreaInformationPanel;
import zet.gui.main.tabs.editor.SelectedFloorElements;

/**
 * <p>One of the main components of the ZET software. This is a component
 * consisting of two parts which are divided by a split, which can be removed
 * to resize the left and right part.</p>
 * <p>The left part contains an {@link JFloor}. This component displays a floor
 * and all the objects on it. These are rooms and areas. For more details about
 * this elements see the documentation of the z-data structure.</p>
 * <p>The right part contains a panel with properties for the currently select
 * element on the floor on the left side. For each type of elements, a different
 * panel with the appropriate properties is visible. These properties include
 * names and several information that can be stored in the z-data structure.</p>
 * @see ds.z.Floor
 * @see zet.gui.components.tabs.editor.JFloor
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class JEditView extends AbstractSplitPropertyWindow<JFloorScrollPane<JFloor>> implements Observer {
	private final EditStatus editStatus;
	private final SelectedFloorElements selection;

	public void setZoomFactor( double zoomFactor ) {
		getLeftPanel().setZoomFactor( zoomFactor );
		getFloor().redisplay();
		//updateFloorView();
	}

	@Override
	public void update( Observable o, Object arg ) {
		if( o == selection ) {
			JPolygon selected = arg == null || !(arg instanceof JPolygon) ? selection.getSelected() : (JPolygon)arg;
			if( selected == null ) {
				setEastPanelType( Panels.Floor );
			} else {
				PlanPolygon<?> p = selected.getPlanPolygon();
				if( selection.getSelectedEdge() != null )
					setEastPanelType( Panels.Edge );
				else if( p instanceof Room )
					setEastPanelType( Panels.Room );
				else if( p instanceof DelayArea )
					setEastPanelType( Panels.DelayArea );
				else if( p instanceof EvacuationArea )
					setEastPanelType( Panels.EvacuationArea );
				else if( p instanceof AssignmentArea )
					setEastPanelType( Panels.AssignmentArea );
				else if( p instanceof StairArea )
					setEastPanelType( Panels.StairArea );
				else if( p instanceof TeleportArea )
					setEastPanelType( Panels.TeleportArea );
				else
					setEastPanelType( Panels.Default );
			}
		}
	}

	/**
	 * An enumeration of all possible panels visible in the edit view on the right part.
	 * @see #setEastPanelType(zet.gui.components.tabs.JEditView.Panels)
	 */
	public enum Panels {
		/** Message code indicating that the delay area panel should be displayed.  */
		InaccessibleArea( new JDefaultInformationPanel() ),
		/** Message code indicating that the delay area panel should be displayed.  */
		DelayArea( new JDelayAreaInformationPanel() ),
		/** Message code indicating that the assignment area panel should be displayed.*/
		AssignmentArea( new JAssignmentAreaInformationPanel() ),
		/** Message code indicating that the room panel should be displayed. */
		Room( new JRoomInformationPanel() ),
		/** Message code indicating that the default panel should be displayed. */
		Default( new JDefaultInformationPanel() ),
		/** Message code indicating that the floor panel should be displayed. */
		Floor( new JFloorInformationPanel() ),
		/** Message code indicating that the evacuation area panel should be displayed. */
		EvacuationArea( new JEvacuationAreaInformationPanel() ),
		/** Message code indicating that the stair area panel should be displayed. */
		StairArea( new JStairAreaInformationPanel() ),
		/** Message code indicating that the stair area panel should be displayed. */
		TeleportArea( new JTeleportAreaInformationPanel() ),
		/** A single edge is select. No area or polygon. */
		Edge( new JEdgeInformationPanel() );

		private Panels( JInformationPanel<? extends Object> panel ) {
			this.panel = Objects.requireNonNull( panel );
		}

		final private JInformationPanel<?> panel;

		public JInformationPanel getPanel() {
			return panel;
		}
	}

	/** The localization class. */
	private Localization loc;
	/** The currently active panel type */
	private static Panels eastPanelType;
	/** The control object for the loaded project. */
	private ZControl projectControl;
	/** The currently visible {@link de.tu_berlin.coga.zet.model.Floor} */
	private Floor currentFloor;
	/** Model for a floor-selector combo box. */
	private FloorComboBox<Floor> floorSelector;
	/** Model for a room-selector combo box. */
	private RoomComboBoxModel roomSelector;
	/** A label that shows a string explaining the floor selection combo box. */
	private JLabel lblFloorSelector;
	/** A label that contains the number of the currently visible floor. */
	private JLabel lblFloorNumber;
	/** A label that shows a string explaining the room selection combo box. */
	private JLabel lblRoomSelector;
	// Components for the east bar
	/**  The CardLayout object of the east subpanel. */
	private CardLayout eastSubBarCardLayout;
	/** The panel representing the variable part of the east bar. */
	private JPanel eastSubBar;

	private boolean disableUpdate = false;
	private final GUIControl guiControl;

	public JEditView( EditStatus editStatus, GUIControl guiControl, SelectedFloorElements selection ) {
		super( new JFloorScrollPane<>( new JFloor( editStatus, guiControl ) ) );
		this.selection = selection;
		//((JRoomInformationPanel)Panels.Room.getPanel()).setSelection( selection );
		EventServer.getInstance().registerListener( getFloor(), ZModelRoomEvent.class );
		loc = GUILocalization.loc;
		this.guiControl = Objects.requireNonNull( guiControl, "GUI control class cannot be null." );
		this.editStatus = Objects.requireNonNull( editStatus, "Edit status object cannot be null." );
		this.getLeftPanel().getHorizontalScrollBar().addAdjustmentListener( adlPlanImage );
		this.getLeftPanel().getVerticalScrollBar().addAdjustmentListener( adlPlanImage );
	}
	private AdjustmentListener adlPlanImage = new AdjustmentListener() {
		@Override
		public void adjustmentValueChanged( AdjustmentEvent e ) {
//			getLeftPanel().getMainComponent().getPlanImage().setVisibleRect( getLeftPanel().getMainComponent().getVisibleRect() );
//			getLeftPanel().getMainComponent().getPlanImage().update();
		}
	};

	/**
	 * Returns the index of the panel currently visible in the east panel.
	 * @return the index of the panel currently visible in the east panel
	 */
	public Panels getEastPanelType() {
		return eastPanelType;
	}

	/**
	 * Sets the panel visible in the east panel.
	 * @param panel the panel
	 */
	private void setEastPanelType( Panels panel ) {
		if( panel == Panels.Edge ) {
			panel.getPanel().update( selection.getSelectedEdge() );
		} else if( panel == Panels.Floor ) {
			Panels.Floor.getPanel().update( currentFloor );
		} else { // selected an area
			panel.getPanel().update( selection.getSelected().getPlanPolygon() );
		}
		eastSubBarCardLayout.show( eastSubBar, panel.toString() );
		eastPanelType = panel;
	}

	/**
	 * Returns the panel that is displayed in the right/eastern part of the
	 * edit view. This panel contains several cards in a card layout that can be
	 * switched and contain elements for the properties of different objects of
	 * z format.
	 * @return the panel containing the different components
	 */
	@Override
	protected JPanel createEastBar() {
		double size[][] = // Columns
						{{10, TableLayout.FILL, 10},
			//Rows
			{10, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.FILL
			}
		};
		JPanel eastPanel = new JPanel( new TableLayout( size ) );

		floorSelector = new FloorComboBox<>();
		floorSelector.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				if( floorSelector.getSelectedItem() == null )
					return;

				final int add = PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) ? 1 : 0;
				lblFloorNumber.setText( String.format( loc.getStringWithoutPrefix( "gui.EditPanel.Default.OnFloor" ), floorSelector.getSelectedIndex() + add ) );
				//btnFloorDown.setEnabled( !(floorSelector.getSelectedIndex() == 0 || floorSelector.getSelectedIndex() == 1 && add == 0) );

//				// TODO call the method somehow with the control class...
					// JEditor.getInstance().enableMenuFloorDown( !(cbxFloors.getSelectedIndex() == 0 || cbxFloors.getSelectedIndex() == 1 && add == 0) );
//				//btnFloorUp.setEnabled( !(floorSelector.getSelectedIndex() == floorSelector.getItemCount() - 1 || floorSelector.getSelectedIndex() == 0 && add == 0) );
//				//JEditor.getInstance().enableMenuFloorUp( !(cbxFloors.getSelectedIndex() == cbxFloors.getItemCount() - 1 || cbxFloors.getSelectedIndex() == 0 && add == 0) );
				setFloor( (Floor)floorSelector.getSelectedItem() );

				//updateFloorView();
				getLeftPanel().getTopRuler().setWidth( currentFloor.getWidth() );
				getLeftPanel().getLeftRuler().setHeight( currentFloor.getHeight() );
				getLeftPanel().getTopRuler().offset = zet.util.ConversionTools.roundScale3( currentFloor.getxOffset() / 1000.0 - 0.8 );
				getLeftPanel().getLeftRuler().offset = zet.util.ConversionTools.roundScale3( currentFloor.getyOffset() / 1000.0 - 0.8 );

//				// Title of the window
				guiControl.setZETWindowTitle( getAdditionalTitleBarText() );

				// Finally change the floor
				changeFloor( currentFloor );
			}
		} );

		final JComboBox<Room> cbxRooms = new JComboBox<>();
		roomSelector = new RoomComboBoxModel( projectControl, floorSelector );
		cbxRooms.setModel( roomSelector );
		cbxRooms.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				getFloor().showPolygon( (Room)cbxRooms.getSelectedItem() );
			}
		} );
		cbxRooms.setRenderer( new ComboBoxRenderer<Room>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent( JList<? extends Room> list, Room value, int index, boolean isSelected, boolean cellHasFocus ) {
				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );	// Needed for correct displaying! Forget return
				if( value != null )
					setText( value.getName() );
				else
					setText( "" );
				return this;
			}
			/** Prohibits serialization. */
			private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
				throw new UnsupportedOperationException( "Serialization not supported" );
			}
		} );

		int row = 1;

		loc = GUILocalization.loc;
		loc.setPrefix( "gui.EditPanel." );
		lblFloorSelector = new JLabel( loc.getString( "Default.Floors" ) + ":" );
		eastPanel.add( lblFloorSelector, "1, " + row++ );
		eastPanel.add( floorSelector, "1, " + row++ );
		lblFloorNumber = new JLabel( "1" );
		eastPanel.add( lblFloorNumber, "1, " + row++ );
		row++;
		lblRoomSelector = new JLabel( loc.getString( "Default.Rooms" ) + ":" );
		eastPanel.add( lblRoomSelector, "1, " + row++ );
		eastPanel.add( cbxRooms, "1, " + row++ );
		row++;

		eastSubBarCardLayout = new CardLayout();
		eastSubBar = new JPanel( eastSubBarCardLayout );
		for( Panels panel : Panels.values() )
			eastSubBar.add( panel.getPanel(), panel.toString() );
		eastPanel.add( eastSubBar, "1, " + row++ );

		loc.clearPrefix();
		return eastPanel;
	}

	@Override
	public void localize() {
		// Title of the window
		// Localization of child components
		getLeftPanel().localize();

		loc.setPrefix( "gui.EditPanel." );
		// Localization of own components
		lblFloorSelector.setText( loc.getString( "Default.Floors" ) + ":" );
		lblFloorNumber.setText( String.format( loc.getStringWithoutPrefix( "gui.EditPanel.Default.OnFloor" ), floorSelector.getSelectedIndex() + (PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) ? 1 : 0) ) );
		lblRoomSelector.setText( loc.getString( "Default.Rooms" ) + ":" );

		loc.clearPrefix();
		for( Panels p : Panels.values() )
			p.getPanel().localize();
	}

	/**
	 * Returns the current title bar text of the window, so it can be set if
	 * the editor panel is displayed inside an window.
	 * @return the text
	 */
	@Override
	protected String getAdditionalTitleBarText() {
		return projectControl.getProject().getProjectFile() != null ? "[" + currentFloor.getName() + "]" : "[" + currentFloor.getName() + "]";
	}

	/**
	 * Changes the select displayed floor. This includes changing the GUI so
	 * that it displays the new floor.
	 * @param floor the new floor that is shown
	 */
	public void changeFloor( Floor floor ) {
		floorSelector.setSelectedItem( floor );
	}

	public void displayProject() {
		displayProject( projectControl );
	}

	private void setFloor( Floor floor ) {
		currentFloor = floor;
		EventServer.getInstance().unregisterListener( getFloor(), ZModelRoomEvent.class );
		JFloor newFloor = new JFloor( editStatus, guiControl, floor );
		editStatus.controlFloor( newFloor, floor );
		getLeftPanel().setMainComponent( newFloor );
		EventServer.getInstance().registerListener( getFloor(), ZModelRoomEvent.class );
		roomSelector.displayRoomsForCurrentFloor();
		Panels.Floor.getPanel().update( floor );
		eastSubBarCardLayout.show( eastSubBar, Panels.Floor.toString() );
	}

	/**
	 * Sets the z {@link de.tu_berlin.coga.zet.model.Project} that is displayed in the edit view.
	 * @param projectControl
	 */
	final public void displayProject( ZControl projectControl ) {
		this.projectControl = projectControl;

		updateFloorList();

		int floorId = 0;
		if( !PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) )
			if( projectControl.getProject().getBuildingPlan().getFloors().size() >= 2 )
				floorId = 1;

		// We create a new JFloor object, deregister the old one, and push the new one to the controller
		setFloor( floorId );

		// Setup the popup menus for the new project
		if( projectControl != null ) {
			guiControl.getPolygonPopup().recreate( projectControl.getProject().getCurrentAssignment() );
			guiControl.getEdgePopup().recreate();
			guiControl.getPointPopup().recreate();
		}

		// Give control over the project to the panels
		for( Panels p : Panels.values() )
			p.getPanel().setControl( projectControl, guiControl );
	}

	/**
	 * Returns the GUI component of the floor.
	 * @return the GUI component of the floor
	 */
	final public JFloor getFloor() {
		return getLeftPanel().getMainComponent();
	}

	/**
	 * Returns the z format floor that is currently visible.
	 * @return the z format floor that is currently visible
	 */
	public Floor getCurrentFloor() {
		return currentFloor;
	}

	/**
	 * Sets the select floor to a specified id
	 * @param id the floor id
	 */
	public void setFloor( int id ) {
		floorSelector.setSelectedIndex( id );
	}

	/**
	 * Returns the the z format floor that is currently visible. If the evacuation floor
	 * is hidden, the first floor is returned with index 0.
	 * @return the z format floor that is currently visible
	 */
	public int getFloorID() {
		return floorSelector.getSelectedIndex();
	}

	public ZControl getProjectControl() {
		return projectControl;
	}

	/**
	 * Updates the GUI if a new project has been loaded. Loads new combo boxes,
	 * list boxes etc.
	 */
	public void update() {
	}

	public void updateFloorView() {
		if( this.disableUpdate )
			return;
		currentFloor = (Floor)floorSelector.getSelectedItem();
//		getLeftPanel().getMainComponent().displayFloor( currentFloor );
		updateRoomList();
	}

	/**
	 * Displays the floor with name {@code floorName}
	 */
	public final void updateFloorList() {
		floorSelector.clear();
		floorSelector.displayFloors( projectControl.getProject().getBuildingPlan(), PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) );
	}

	/**
	 * Resets the room list on the current floor.
	 */
	public void updateRoomList() {
		roomSelector.clear();
		roomSelector.displayRoomsForCurrentFloor();
	}

	public void setFloorNameFocus() {
		//txtFloorName.requestFocusInWindow();
		//txtFloorName.setSelectionStart( 0 );
		//txtFloorName.setSelectionEnd( txtFloorName.getText().length() );
	}

	public void setRoomNameFocus() {
		//txtRoomName.requestFocusInWindow();
		//txtRoomName.setSelectionStart( 0 );
		//txtRoomName.setSelectionEnd( txtRoomName.getText().length() );
	}
	/**
	 *
	 * @param mode
	 */
	public void changeAreaView( ArrayList<Areas> mode ) {
		// EnumSet.copyOf will not work for empty lists
		GUIOptionManager.setAreaVisibility( mode.isEmpty() ? EnumSet.noneOf( Areas.class ) : EnumSet.copyOf( mode ) );
		updateFloorView();
	}

	public void stateChanged() {
		final JRuler topRuler = getLeftPanel().getTopRuler();
		final JRuler leftRuler = getLeftPanel().getLeftRuler();
		Floor floor = getCurrentFloor();
		topRuler.setWidth( floor.getWidth() );
		leftRuler.setHeight( floor.getHeight() );
		topRuler.offset = zet.util.ConversionTools.roundScale3( floor.getxOffset() / 1000.0 - 0.8 );
		leftRuler.offset = zet.util.ConversionTools.roundScale3( floor.getyOffset() / 1000.0 - 0.8 );
		topRuler.repaint();
		leftRuler.repaint();
	}

	/**
	 * This is a helper method for other GUI objects who need to transform
	 * points that are given in their own coordinate space into the coordinate
	 * space of the Floor.
	 * @param source The Component in whose coordinate space the Point "toConvert"
	 * is specified. It must be an object which is located directly or indirectly
	 * upon the JEditorPanel's JFloor object.
	 * @param toConvert The point to convert
	 * @return The same point as "toConvert", but relative to the surrounding
	 * JFloor object.
	 */
	public Point convertPointToFloorCoordinates( Component source, Point toConvert ) {
		return SwingUtilities.convertPoint( source, toConvert, getFloor() );
	}
	/** Prohibits serialization. */
	private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
		throw new UnsupportedOperationException( "Serialization not supported" );
	}
}
