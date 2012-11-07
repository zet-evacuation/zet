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
 * Class JEditView
 * Created 29.04.2008, 21:06:42
 */
package zet.gui.main.tabs;

import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import de.tu_berlin.math.coga.components.JRuler;
import de.tu_berlin.math.coga.components.framework.Button;
import ds.PropertyContainer;
import ds.z.AssignmentType;
import ds.z.DelayArea;
import ds.z.EvacuationArea;
import ds.z.Floor;
import ds.z.PlanPolygon;
import ds.z.Room;
import ds.z.StairPreset;
import ds.z.TeleportArea;
import ds.z.ZControl;
import ds.z.ZLocalization;
import ds.z.ZModelRoomEvent;
import event.EventServer;
import gui.GUIControl;
import gui.GUIOptionManager;
import gui.ZETLoader;
import gui.ZETProperties;
import gui.editor.Areas;
import info.clearthought.layout.TableLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import zet.gui.GUILocalization;
import zet.gui.components.model.AssignmentTypeComboBoxModel;
import zet.gui.components.model.ComboBoxRenderer;
import zet.gui.components.model.FloorComboBox;
import zet.gui.components.model.RoomComboBoxModel;
import zet.gui.main.JZetWindow;
import zet.gui.main.tabs.base.AbstractSplitPropertyWindow;
import zet.gui.main.tabs.base.JFloorScrollPane;
import zet.gui.main.tabs.editor.EditModeOld;
import zet.gui.main.tabs.editor.EditStatus;
import zet.gui.main.tabs.editor.JFloor;

/**
 * <p>One of the main components of the ZET software. This is a component
 * consisting of two parts which are divided by a split, which can be removed
 * to resize the left and right part.</p>
 * <p>The left part contains an {@link JFloor}. This component displays a floor
 * and all the objects on it. These are rooms and areas. For more details about
 * this elements see the documentation of the z-data structure.</p>
 * <p>The right part contains a panel with properties for the currently selected
 * element on the floor on the left side. For each type of elements, a different
 * panel with the appropriate properties is visible. These properties include
 * names and several information that can be stored in the z-data structure.</p>
 * @see ds.z.Floor
 * @see zet.gui.components.tabs.editor.JFloor
 * @author Jan-Philipp Kappmeier
 */
public class JEditView extends AbstractSplitPropertyWindow<JFloorScrollPane<JFloor>> {
	private JLabel lblEdgeType;
	private JLabel lblEdgeLength;
	private JLabel lblEdgeExitName;
	private JTextField txtEdgeExitName;
	private final EditStatus editStatus;

	public void setZoomFactor( double zoomFactor ) {
		getLeftPanel().setZoomFactor( zoomFactor );
		getFloor().redisplay();
		//updateFloorView();
	}

	/**
	 * An enumeration of all possible panels visible in the edit view on the right part.
	 * @see #setEastPanelType(zet.gui.components.tabs.JEditView.Panels)
	 */
	public enum Panels {
		/** Message code indicating that the delay area panel should be displayed.  */
		DelayArea,
		/** Message code indicating that the assignment area panel should be displayed.*/
		AssignmentArea,
		/** Message code indicating that the room panel should be displayed. */
		Room,
		/** Message code indicating that the default panel should be displayed. */
		Default,
		/** Message code indicating that the floor panel should be displayed. */
		Floor,
		/** Message code indicating that the evacuation area panel should be displayed. */
		EvacuationArea,
		/** Message code indicating that the stair area panel should be displayed. */
		StairArea,
		/** Message code indicating that the stair area panel should be displayed. */
		TeleportArea,
		/** A single edge is selected. No area or polygon. */
		Edge;
	}
	/** The localization class. */
	private GUILocalization loc;
	/** The currently active panel type */
	private static Panels eastPanelType;
	/** The control object for the loaded project. */
	private ZControl projectControl;
	/** The currently visible {@link ds.z.Floor} */
	private Floor currentFloor;
	/** Model for a floor-selector combo box. */
	private FloorComboBox<Floor> floorSelector;
	/** Model for a room-selector combo box. */
	private RoomComboBoxModel roomSelector;
	/** Model for a assignmentType-selector combo box. */
	private zet.gui.components.model.AssignmentTypeComboBoxModel assignmentTypeSelector;
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
	private JLabel lblRoomSize;
	private JLabel lblRoomSizeDesc;
	private JComboBox cbxDelayType;
	private JTextField txtDelayFactor;
	private JTextField txtStairFactorUp;
	private JTextField txtStairFactorDown;
	private JTextField txtRoomName;
	private JTextField txtNumberOfPersons;
	private JTextField txtFloorName;
	private JLabel lblAssignmentType;
	private JLabel lblAssignmentEvacueeNumber;
	private JButton btnAssignmentSetDefaultEvacuees;
	private JLabel lblAreaSize;
	private JLabel lblAreaSizeDesc;
	private JLabel lblMaxPersonsDesc;
	private JLabel lblMaxPersons;
	private JLabel lblMaxPersonsWarning;
	private JLabel lblPreferredExit;
	private JComboBox cbxPreferredExit;
	private JButton btnDelaySetDefault;
	private JLabel lblDelayFactor;
	private JLabel lblStairFactorUp;
	private JLabel lblStairFactorDown;
	/** A label for the stair speed preset. */
	private JLabel lblStairPreset;
	/** A selection box for the stair speed presets. */
	private JComboBox cbxStairPresets;
	/** A label describing the current preset. */
	private JLabel lblStairPresetDescription;
	private JLabel lblDelayType;
	private JLabel lblEvacuationAttractivity;
	private JTextField txtEvacuationAttractivity;
	private JLabel lblFloorName;
	private JButton btnFloorUp;
	private JButton btnFloorDown;
	private JLabel lblRoomName;
	private boolean disableUpdate = false;
	private JLabel lblEvacuationAreaName;
	private JTextField txtEvacuationAreaName;
	private JLabel lblFloorSize;
	private JLabel lblFloorSizeDesc;
	private final GUIControl guiControl;
	private PlanPolygon<?> selectedPolygon;


	/** Describes the teleportation area name field. */
	private JLabel lblTeleportAreaName;
	/** The name filed for a teleportation area. */
	private JTextField txtTeleportAreaName;

	/** Describes the target area combo box.  */
	private JLabel lblTargetArea;
	/** A combo box selecting the target area of a target area. */
	private JComboBox cbxTargetArea;

	/** A combo box selecting the possible exits for a teleportation area. */
	private JComboBox cbxTargetExit;
	/** Describes the target exit combo box. */
	private JLabel lblTargetExit;
	private JLabel lblFloorxOffset;
	private JLabel lblFlooryOffset;
	private JLabel lblFloorWidth;
	private JLabel lblFloorHeight;
	private JTextField txtFloorxOffset;
	private JTextField txtFlooryOffset;
	private JTextField txtFloorWidth;
	private JTextField txtFloorHeight;

	final NumberFormat nfFloat = DefaultLoc.getSingleton().getFloatConverter();
	final NumberFormat nfInteger = DefaultLoc.getSingleton().getIntegerConverter();
	private JButton deleteRoom;
	private JButton moveRoom;

	public JEditView( EditStatus editStatus, GUIControl guiControl ) {
		super( new JFloorScrollPane<>( new JFloor( editStatus, guiControl ) ) );
		EventServer.getInstance().registerListener( getFloor(), ZModelRoomEvent.class );
		loc = GUILocalization.getSingleton();
		this.guiControl = Objects.requireNonNull( guiControl, "GUI control class cannot be null." );
		this.editStatus = Objects.requireNonNull( editStatus, "Edit status object cannot be null." );
		final JFloor centerPanel = this.getLeftPanel().getMainComponent();
//		centerPanel.addActionListener( new ActionListener() {
//			@Override
//			public void actionPerformed( ActionEvent e ) {
//				if( centerPanel.getSelectedPolygons().size() != 1 ) {
//					if( centerPanel.getSelectedEdge() != null ) {
//						setEastPanelType( Panels.Edge );
//					} else
//						setEastPanelType( Panels.Floor );
//				} else {
//					final PlanPolygon p = centerPanel.getSelectedPolygons().get( 0 ).getPlanPolygon();
//					if( p instanceof Room )
//						setEastPanelType( Panels.Room );
//					else if( p instanceof DelayArea )
//						setEastPanelType( Panels.DelayArea );
//					else if( p instanceof EvacuationArea )
//						setEastPanelType( Panels.EvacuationArea );
//					else if( p instanceof AssignmentArea )
//						setEastPanelType( Panels.AssignmentArea );
//					else if( p instanceof StairArea )
//						setEastPanelType( Panels.StairArea );
//					else if( p instanceof TeleportArea )
//						setEastPanelType( Panels.TeleportArea );
//					else
//						setEastPanelType( Panels.Default );
//				}
//			}
//		} );
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
//		selectedPolygon = (panel == Panels.Default || panel == Panels.Floor || panel == Panels.Edge ) ? null : getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
//		switch( panel ) {
//			case DelayArea:
//				txtDelayFactor.setText( nfFloat.format( ((DelayArea)selectedPolygon).getSpeedFactor() ) );
//				cbxDelayType.setSelectedItem( ((DelayArea)selectedPolygon).getDelayType() );
//				eastSubBarCardLayout.show( eastSubBar, "delayArea" );
//				break;
//			case StairArea:
//				txtStairFactorUp.setText( nfFloat.format( ((StairArea)selectedPolygon).getSpeedFactorUp() ) );
//				txtStairFactorDown.setText( nfFloat.format( ((StairArea)selectedPolygon).getSpeedFactorDown() ) );
//				eastSubBarCardLayout.show( eastSubBar, "stairArea" );
//				break;
//			case AssignmentArea:
//				txtNumberOfPersons.setText( nfInteger.format( ((AssignmentArea)selectedPolygon).getEvacuees() ) );
//				assignmentTypeSelector.setSelectedItem( ((AssignmentArea)selectedPolygon).getAssignmentType() );
//				eastSubBarCardLayout.show( eastSubBar, "assignmentArea" );
//				double area = Math.round( selectedPolygon.areaMeter() * 100 ) / 100.0;
//				lblAreaSize.setText( nfFloat.format( area ) + " m²" );
//				if( projectControl.getProject().getBuildingPlan().isRastered() ) {
//					lblMaxPersons.setText( nfInteger.format( ((AssignmentArea)selectedPolygon).getMaxEvacuees() ) );
//					lblMaxPersonsWarning.setText( "" );
//				} else {
//					double persons = Math.round( (area / (0.4 * 0.4)) * 100 ) / 100.0;
//					lblMaxPersons.setText( nfFloat.format( persons ) );
//					lblMaxPersonsWarning.setText( loc.getString( "gui.EditPanel.Assignment.AreaWarning" ) );
//				}
//				if( ((AssignmentArea)selectedPolygon).getExitArea() == null )
//					cbxPreferredExit.setSelectedIndex( -1 );
//				else
//					cbxPreferredExit.setSelectedItem( ((AssignmentArea)selectedPolygon).getExitArea() );
//				break;
//			case Room:
//				txtRoomName.setText( ((Room)selectedPolygon).getName() );
//				eastSubBarCardLayout.show( eastSubBar, "room" );
//				double areaRoom = Math.round( selectedPolygon.areaMeter() * 100 ) / 100.0;
//				lblRoomSize.setText( nfFloat.format( areaRoom ) + " m²" );
//				break;
//			case Default:
//				eastSubBarCardLayout.show( eastSubBar, "default" );
//				break;
//			case Floor:
//				txtFloorName.setText( this.currentFloor.getName() );
//				txtFloorxOffset.setText( Integer.toString( currentFloor.getxOffset() ) );
//				txtFlooryOffset.setText( Integer.toString( currentFloor.getyOffset() ) );
//				txtFloorWidth.setText( Integer.toString( currentFloor.getWidth() ) );
//				txtFloorHeight.setText( Integer.toString( currentFloor.getHeight() ) );
//
//				double areaFloor = 0;
//				for( PlanPolygon<?> p : currentFloor )
//					areaFloor += p.areaMeter();
//
//				lblFloorSize.setText( nfFloat.format( areaFloor ) + " m²" );
//
//				eastSubBarCardLayout.show( eastSubBar, "floor" );
//				break;
//			case EvacuationArea:
//				txtEvacuationAreaName.setText( ((EvacuationArea)selectedPolygon).getName() );
//				txtEvacuationAttractivity.setText( nfInteger.format( ((EvacuationArea)selectedPolygon).getAttractivity() ) );
//				eastSubBarCardLayout.show( eastSubBar, "evacuation" );
//				break;
//			case TeleportArea:
//				txtTeleportAreaName.setText( ((TeleportArea)selectedPolygon).getName() );
//				if( ((TeleportArea)selectedPolygon).getExitArea() == null )
//					cbxTargetExit.setSelectedIndex( -1 );
//				else
//					cbxTargetExit.setSelectedItem( ((TeleportArea)selectedPolygon).getExitArea() );
//
//				if( ((TeleportArea)selectedPolygon).getTargetArea() == null )
//					cbxTargetArea.setSelectedIndex( -1 );
//				else
//					cbxTargetArea.setSelectedItem( ((TeleportArea)selectedPolygon).getTargetArea() );
//				eastSubBarCardLayout.show( eastSubBar, "teleport" );
//				break;
//			case Edge:
//				Edge e = getLeftPanel().getMainComponent().getSelectedEdge();
//				txtEdgeExitName.setEnabled( false );
//				lblEdgeExitName.setText( "" );
//				if( e instanceof RoomEdge ) {
//					if( e instanceof TeleportEdge ) {
//						TeleportEdge te = (TeleportEdge)e;
//						if( ((Room)te.getLinkTarget().getAssociatedPolygon()).getAssociatedFloor() instanceof DefaultEvacuationFloor ) {
//							lblEdgeType.setText( "Ausgang" );
//							txtEdgeExitName.setEnabled( true );
//							// we have an evacuation exit
//							Room r = (Room)te.getLinkTarget().getAssociatedPolygon();
//							EvacuationArea ea = r.getEvacuationAreas().get( 0 );
//							lblEdgeExitName.setText( "Ausgang" );
//							txtEdgeExitName.setText( ea.getName() );
//						}
//						else
//							lblEdgeType.setText( "Stockwerkübergang" );
//					} else {
//						if( ((RoomEdge)e).isPassable() ) {
//							lblEdgeType.setText( "Durchgang" );
//						} else {
//							lblEdgeType.setText( "Wand" );
//						}
//					}
//				} else {
//					// easy peasy, this is an area boundry
//					lblEdgeType.setText( "Area-Begrenzung" );
//				}
//
//				lblEdgeLength.setText( "Breite: " + (e.length())*0.001  + "m" );
//
//				eastSubBarCardLayout.show( eastSubBar, "edge" );
//				break;
//			default:
//				guiControl.showErrorMessage( loc.getString( "gui.editor.JEditorPanel.WrongPanelID" ), loc.getString( "gui.NotGraveError" ) );
//				return;
//		}
//		eastPanelType = panel;
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
				btnFloorDown.setEnabled( !(floorSelector.getSelectedIndex() == 0 || floorSelector.getSelectedIndex() == 1 && add == 0) );

				// TODO call the method somehow with the control class...
				//JEditor.getInstance().enableMenuFloorDown( !(cbxFloors.getSelectedIndex() == 0 || cbxFloors.getSelectedIndex() == 1 && add == 0) );
				btnFloorUp.setEnabled( !(floorSelector.getSelectedIndex() == floorSelector.getItemCount() - 1 || floorSelector.getSelectedIndex() == 0 && add == 0) );
				//JEditor.getInstance().enableMenuFloorUp( !(cbxFloors.getSelectedIndex() == cbxFloors.getItemCount() - 1 || cbxFloors.getSelectedIndex() == 0 && add == 0) );
				Floor dspFloor = (Floor)floorSelector.getSelectedItem();
				currentFloor = dspFloor;
				updateFloorView();
				getLeftPanel().getTopRuler().setWidth( dspFloor.getWidth() );
				getLeftPanel().getLeftRuler().setHeight( dspFloor.getHeight() );
				getLeftPanel().getTopRuler().offset = zet.util.ConversionTools.roundScale3( dspFloor.getxOffset() / 1000.0 - 0.8 );
				getLeftPanel().getLeftRuler().offset = zet.util.ConversionTools.roundScale3( dspFloor.getyOffset() / 1000.0 - 0.8 );

				// FloorName
				txtFloorName.setText( dspFloor.getName() );
				// Title of the window
				guiControl.setZETWindowTitle( getAdditionalTitleBarText() );
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
			@Override
			public Component getListCellRendererComponent( JList<? extends Room> list, Room value, int index, boolean isSelected, boolean cellHasFocus ) {
				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );	// Needed for correct displaying! Forget return
				if( value != null )
					setText( value.getName() );
				return this;
			}
		} );

		int row = 1;

		loc = GUILocalization.getSingleton();
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
		JPanel card;

		card = getEastFloorPanel();
		eastSubBar.add( card, "floor" );

		card = getEastDelayAreaPanel();
		eastSubBar.add( card, "delayArea" );

		card = getEastStairAreaPanel();
		eastSubBar.add( card, "stairArea" );

		card = getEastAssignmentAreaPanel();
		eastSubBar.add( card, "assignmentArea" );

		card = getEastRoomPanel();
		eastSubBar.add( card, "room" );

		card = getEastDefaultPanel();
		eastSubBar.add( card, "default" );

		card = getEastEvacuationAreaPanel();
		eastSubBar.add( card, "evacuation" );

		card = getEastTeleportAreaPanel();
		eastSubBar.add( card, "teleport" );

		card = getEastEdgePanel();
		eastSubBar.add( card, "edge" );

		eastPanel.add( eastSubBar, "1, " + row++ );

		loc.clearPrefix();
		return eastPanel;
	}

	private JPanel getEastAssignmentAreaPanel() {
		double size[][] = {
			{ // Columns
				TableLayout.FILL},
			{ //Rows
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20, // Assignment type
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20, // Number of evacuees
				TableLayout.PREFERRED, 20, // Button for default number of evacuees
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20, // Preferred exit
				TableLayout.PREFERRED, TableLayout.PREFERRED, 10, // Area
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20, // Max number of persons for area
				TableLayout.PREFERRED, // Warning
				TableLayout.FILL // Fill the rest of space
			}
		};

		JPanel eastPanel = new JPanel( new TableLayout( size ) );
		int row = 0;

		// AssignmentType-Selector
		lblAssignmentType = new JLabel( loc.getString( "Assignment.Type" ) );
		eastPanel.add( lblAssignmentType, "0, " + row++ );
		assignmentTypeSelector = new AssignmentTypeComboBoxModel( projectControl );
		assignmentTypeSelector.setFloorPanel( this.getLeftPanel().getMainComponent() );
		JComboBox cbxAssignmentType = new JComboBox( assignmentTypeSelector );
		cbxAssignmentType.setRenderer( new ComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent( JList list, Object value,
							int index, boolean isSelected, boolean cellHasFocus ) {
				JLabel me = (JLabel)super.getListCellRendererComponent( list, value, index,
								isSelected, cellHasFocus );

				if( value != null )
					setText( ((AssignmentType)value).getName() );
				return this;
			}
		} );
		eastPanel.add( cbxAssignmentType, "0, " + row++ );
		row++;

		// Number of Evacuees
		lblAssignmentEvacueeNumber = new JLabel( loc.getString( "Assignment.Persons" ) );
		eastPanel.add( lblAssignmentEvacueeNumber, "0, " + row++ );
		txtNumberOfPersons = new JTextField( "10" );
		txtNumberOfPersons.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtNumberOfPersons.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
					;
//					try {
//						int persons = Math.min( nfInteger.parse( txtNumberOfPersons.getText() ).intValue(), ((AssignmentArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).getMaxEvacuees() );
//						((AssignmentArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setEvacuees( persons );
//					} catch( ParseException | IllegalArgumentException ex ) {
//						ZETLoader.sendError( ex.getLocalizedMessage() );
//					}
			}
		} );
		eastPanel.add( txtNumberOfPersons, "0, " + row++ );
		row++;
		btnAssignmentSetDefaultEvacuees = Button.newButton( loc.getString( "Assignment.SetDefaultEvacuees" ), loc.getString( "Assignment.SetDefaultEvacuees.ToolTip" ) );
		btnAssignmentSetDefaultEvacuees.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				//AssignmentArea a = (AssignmentArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
				//int persons = Math.min( a.getAssignmentType().getDefaultEvacuees(), a.getMaxEvacuees() );
				//a.setEvacuees( persons );
				//txtNumberOfPersons.setText( nfInteger.format( a.getEvacuees() ) );
			}
		} );
		eastPanel.add( btnAssignmentSetDefaultEvacuees, "0, " + row++ );
		row++;

		// Preferred-Exit-Selector
		lblPreferredExit = new JLabel( loc.getString( "Assignment.PreferredExit" ) );
		eastPanel.add( lblPreferredExit, "0, " + row++ );
		cbxPreferredExit = new JComboBox();
		cbxPreferredExit.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged( ItemEvent e ) {
				if( cbxPreferredExit.getSelectedIndex() == -1 )
					return;
//				if( getLeftPanel().getMainComponent().getSelectedPolygons().size() > 0 ) {
//					AssignmentArea a = (AssignmentArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
//					a.setExitArea( (EvacuationArea)cbxPreferredExit.getSelectedItem() );
//				}
			}
		} );
		cbxPreferredExit.setRenderer( new ComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				if( value != null )
					setText( ((EvacuationArea)value).getName() );
				else
					setText( "" );
				return this;
			}
		} );
		eastPanel.add( cbxPreferredExit, "0, " + row++ );
		row++;

		lblAreaSizeDesc = new JLabel( loc.getString( "Assignment.Area" ) );
		lblAreaSize = new JLabel( "" );
		eastPanel.add( lblAreaSizeDesc, "0, " + row++ );
		eastPanel.add( lblAreaSize, "0, " + row++ );
		row++;

		lblMaxPersonsDesc = new JLabel( loc.getString( "Assignment.MaxPersons" ) );
		lblMaxPersons = new JLabel( "" );
		eastPanel.add( lblMaxPersonsDesc, "0, " + row++ );
		eastPanel.add( lblMaxPersons, "0, " + row++ );
		row++;

		lblMaxPersonsWarning = new JLabel( loc.getString( "Assignment.AreaWarning" ) );
		eastPanel.add( lblMaxPersonsWarning, "0, " + row++ );
		row++;

		return eastPanel;
	}

	private JPanel getEastDefaultPanel() {
		double size[][] = // Columns
						{{10, TableLayout.FILL, 10},
			//Rows
			{TableLayout.FILL}
		};
		JPanel eastPanel = new JPanel( new TableLayout( size ) );
		return eastPanel;
	}

	private JPanel getEastDelayAreaPanel() {
		double size[][] = // Columns
						{{TableLayout.FILL},
			//Rows
			{TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, 20, TableLayout.FILL
			}
		};

		JPanel eastPanel = new JPanel( new TableLayout( size ) );

		int row = 0;

		// Delay-Selector
		lblDelayType = new JLabel( loc.getString( "Delay.Type" ) + ":" );
		eastPanel.add( lblDelayType, "0, " + row++ );
		cbxDelayType = new JComboBox( new DefaultComboBoxModel( DelayArea.DelayType.values() ) );
		cbxDelayType.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged( ItemEvent e ) {
				if( e.getStateChange() == ItemEvent.SELECTED )
					;
//					((DelayArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setDelayType( (DelayArea.DelayType)e.getItem() );
			}
		} );
		cbxDelayType.setRenderer( new ComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
				//JLabel me = (JLabel)
				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				if( value != null )
					setText( ((DelayArea.DelayType)value).description );
				return this;
			}
		} );
		eastPanel.add( cbxDelayType, "0, " + row++ );
		row++;

		// Delay-Factor
		lblDelayFactor = new JLabel( loc.getString( "Delay.Factor" ) );
		eastPanel.add( lblDelayFactor, "0, " + row++ );
		txtDelayFactor = new JTextField( " " );
		txtDelayFactor.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtDelayFactor.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
;
//					try {
//						((DelayArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setSpeedFactor( nfFloat.parse( txtDelayFactor.getText() ).doubleValue() );
//					} catch( ParseException ex ) {
//						ZETLoader.sendError( loc.getString( "gui.error.NonParsableFloatString" ) );
//					} catch( IllegalArgumentException ex ) {
//						ZETLoader.sendError( ex.getLocalizedMessage() );
//					}
			}
		} );
		eastPanel.add( txtDelayFactor, "0, " + row++ );
		row++;

		btnDelaySetDefault = Button.newButton( loc.getString( "Delay.TypeDefault" ), loc.getString( "Delay.TypeDefault.ToolTip" ) );
		btnDelaySetDefault.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
//				DelayArea a = (DelayArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
//				a.setSpeedFactor( a.getDelayType().defaultSpeedFactor );
//				txtDelayFactor.setText( nfFloat.format( a.getSpeedFactor() ) );
			}
		} );
		eastPanel.add( btnDelaySetDefault, "0, " + row++ );

		return eastPanel;
	}

	private JPanel getEastStairAreaPanel() {
		double size[][] = // Columns
						{{TableLayout.FILL},
			//Rows
			{TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.FILL
			}
		};

		JPanel eastPanel = new JPanel( new TableLayout( size ) );

		int row = 0;

		// DelayFactor for going upwards
		lblStairFactorUp = new JLabel( loc.getString( "Stair.FactorUp" ) + ":" );
		eastPanel.add( lblStairFactorUp, "0, " + row++ );
		txtStairFactorUp = new JTextField( " " );
		txtStairFactorUp.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtStairFactorUp.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
					;
//					try {
//						((StairArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setSpeedFactorUp( nfFloat.parse( txtStairFactorUp.getText() ).doubleValue() );
//					} catch( ParseException ex ) {
//						ZETLoader.sendError( loc.getString( "gui.error.NonParsableFloatString" ) );
//					} catch( IllegalArgumentException ex ) {
//						ZETLoader.sendError( ex.getLocalizedMessage() );
//					}
			}
		} );
		eastPanel.add( txtStairFactorUp, "0, " + row++ );
		row++;

		// DelayFactor for going downwards
		lblStairFactorDown = new JLabel( loc.getString( "Stair.FactorDown" ) + ":" );
		eastPanel.add( lblStairFactorDown, "0, " + row++ );
		txtStairFactorDown = new JTextField( " " );
		txtStairFactorDown.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtStairFactorDown.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
					;
//					try {
//						((StairArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setSpeedFactorDown( nfFloat.parse( txtStairFactorDown.getText() ).doubleValue() );
//					} catch( ParseException ex ) {
//						ZETLoader.sendError( loc.getString( "gui.error.NonParsableFloatString" ) );
//					} catch( IllegalArgumentException ex ) {
//						ZETLoader.sendError( ex.getLocalizedMessage() );
//					}
			}
		} );
		eastPanel.add( txtStairFactorDown, "0, " + row++ );
		row++;

		// Add combo box with presets
		lblStairPreset = new JLabel( loc.getString( "Stair.Preset" ) + ":" );
		cbxStairPresets = new JComboBox();
		cbxStairPresets.addItem( StairPreset.Indoor );
		cbxStairPresets.addItem( StairPreset.Outdoor );
		eastPanel.add( lblStairPreset, "0, " + row++ );
		eastPanel.add( cbxStairPresets, "0, " + row++ );
		cbxStairPresets.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent arg0 ) {
				StairPreset sp = (StairPreset)cbxStairPresets.getSelectedItem();
				NumberFormat nf = loc.getFloatConverter();
				txtStairFactorUp.setText( nf.format( sp.getSpeedFactorUp() ) );
				txtStairFactorDown.setText( nf.format( sp.getSpeedFactorDown() ) );
			}
		});
		lblStairPresetDescription = new JLabel( ZLocalization.getSingleton().getString( ((StairPreset)cbxStairPresets.getSelectedItem()).getText() ) ) ;
		eastPanel.add( lblStairPresetDescription, "0, " + row++ );
		row++;

		return eastPanel;
	}

	private JPanel getEastEvacuationAreaPanel() {
		double size[][] = // Columns
						{{TableLayout.FILL},
			//Rows
			{TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, 20, TableLayout.FILL
			}
		};

		JPanel eastPanel = new JPanel( new TableLayout( size ) );

		int row = 0;
		lblEvacuationAreaName = new JLabel( loc.getString( "Evacuation.Name" ) );
		eastPanel.add( lblEvacuationAreaName, "0, " + row++ );
		txtEvacuationAreaName = new JTextField();
		txtEvacuationAreaName.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtEvacuationAreaName.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
;
//					((EvacuationArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setName( txtEvacuationAreaName.getText() );
			}
		} );
		eastPanel.add( txtEvacuationAreaName, "0, " + row++ );
		row++;

		// Attractivity
		lblEvacuationAttractivity = new JLabel( loc.getString( "Evacuation.Attractivity" ) );
		eastPanel.add( lblEvacuationAttractivity, "0, " + row++ );

		txtEvacuationAttractivity = new JTextField( " " );
		txtEvacuationAttractivity.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtEvacuationAttractivity.addKeyListener( new KeyAdapter() {
			@Override
			public void keyReleased( KeyEvent e ) {
//				try {
//					((EvacuationArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setAttractivity( nfInteger.parse(
//									txtEvacuationAttractivity.getText() ).intValue() );
//				} catch( ParseException ex ) {
//					ZETLoader.sendError( loc.getString( "gui.error.NonParsableNumberString" ) );
//				} catch( IllegalArgumentException ex ) {
//					ZETLoader.sendError( ex.getLocalizedMessage() );
//				}
			}
		} );
		eastPanel.add( txtEvacuationAttractivity, "0, " + row++ );
		row++;

		return eastPanel;
	}

	private JPanel getEastFloorPanel() {
		int space = 16;
		double size[][] = // Columns
						{{TableLayout.FILL, space, TableLayout.FILL},
			//Rows
			{
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				20,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				20,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				TableLayout.PREFERRED,
				TableLayout.FILL
			}
		};

		JPanel eastPanel = new JPanel( new TableLayout( size ) );

		lblFloorName = new JLabel( loc.getString( "Floor.Name" ) );
		eastPanel.add( lblFloorName, "0,0,2,0" );
		txtFloorName = new JTextField();
		txtFloorName.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtFloorName.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
					boolean success = projectControl.renameFloor( currentFloor, txtFloorName.getText() );
					if( !success )
						guiControl.alertError( "Floor with that name already exists" );
					else
						updateFloorView();
				}
			}
		} );
		eastPanel.add( txtFloorName, "0,1,2,1" );

		ActionListener aclFloor = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				switch( e.getActionCommand() ) {
					case "down":
						guiControl.moveFloorDown();
						break;
					case "up":
						guiControl.moveFloorUp();
						break;
					default:
						ZETLoader.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
						break;
				}
			}
		};
		btnFloorUp = Button.newButton( loc.getString( "Floor.Up" ), aclFloor, "up", loc.getString( "Floor.Up.ToolTip" ) );
		eastPanel.add( btnFloorUp, "0,3,0,3" );
		btnFloorDown = Button.newButton( loc.getString( "Floor.Down" ), aclFloor, "down", loc.getString( "Floor.Down.ToolTip" ) );
		eastPanel.add( btnFloorDown, "2,3" );

		// Additional Infos:
		lblFloorxOffset = new JLabel( loc.getString( "Floor.xOffset" ) );
		lblFlooryOffset = new JLabel( loc.getString( "Floor.yOffset" ) );
		lblFloorWidth = new JLabel( loc.getString( "Floor.Width" ) );
		lblFloorHeight = new JLabel( loc.getString( "Floor.Height" ) );
		txtFloorxOffset = new JTextField();
		txtFlooryOffset = new JTextField();
		txtFloorWidth = new JTextField();
		txtFloorHeight = new JTextField();
		eastPanel.add( lblFloorxOffset, "0,5" );
		eastPanel.add( lblFlooryOffset, "0,6" );
		eastPanel.add( lblFloorWidth, "0,7" );
		eastPanel.add( lblFloorHeight, "0,8" );
		eastPanel.add( txtFloorxOffset, "2,5" );
		eastPanel.add( txtFlooryOffset, "2,6" );
		eastPanel.add( txtFloorWidth, "2,7" );
		eastPanel.add( txtFloorHeight, "2,8" );

		ActionListener aclFloorSize = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent ae ) {
				try {
					final int xOffset = nfInteger.parse( txtFloorxOffset.getText() ).intValue();
					final int yOffset = nfInteger.parse( txtFlooryOffset.getText() ).intValue();
					final int width = nfInteger.parse( txtFloorWidth.getText() ).intValue();
					final int height = nfInteger.parse( txtFloorHeight.getText() ).intValue();
					currentFloor.setMinimumSize( xOffset, yOffset, width, height );
					getLeftPanel().getMainComponent().displayFloor();
				} catch( ParseException ex ) {
					ZETLoader.sendError( "Parsing nicht möglich." ); // TODO loc
					return;
				}
				ZETLoader.sendMessage( "Floor-Size geändert." ); // TODO loc
			}
		};
		txtFloorxOffset.addActionListener( aclFloorSize );
		txtFlooryOffset.addActionListener( aclFloorSize );
		txtFloorWidth.addActionListener( aclFloorSize );
		txtFloorHeight.addActionListener( aclFloorSize );

		lblFloorSizeDesc = new JLabel( loc.getString( "Floor.Area" ) );
		lblFloorSize = new JLabel( "" );
		eastPanel.add( lblFloorSizeDesc, "0,10,2,10" );
		eastPanel.add( lblFloorSize, "0,11,2,11" );

		return eastPanel;
	}

	private JPanel getEastRoomPanel() {
		double size[][] = // Columns
						{{TableLayout.FILL},
			//Rows
			{TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED,
				10,
				TableLayout.PREFERRED,
				10,
				TableLayout.PREFERRED,
        TableLayout.FILL,
			}
		};

		JPanel eastPanel = new JPanel( new TableLayout( size ) );

		int row = 0;

		lblRoomName = new JLabel( loc.getString( "Room.Name" ) );
		eastPanel.add( lblRoomName, "0, " + row++ );
		txtRoomName = new JTextField();
		txtRoomName.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtRoomName.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
//					boolean success = projectControl.renameRoom( (Room)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon(), txtRoomName.getText() );
//					if( !success )
//						guiControl.alertError( "Floor with that name already exists" );
//					else
//						updateFloorView();
				}
			}
		} );
		eastPanel.add( txtRoomName, "0, " + row++ );
		row++;

		// add size information
		lblRoomSizeDesc = new JLabel( loc.getString( "Room.Area" ) );
		lblRoomSize = new JLabel( "" );
		eastPanel.add( lblRoomSizeDesc, "0, " + row++ );
		eastPanel.add( lblRoomSize, "0, " + row++ );

		deleteRoom = new JButton( "Raum Loeschen" );
		deleteRoom.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				Room currentRoom = (Room)selectedPolygon;
				projectControl.deletePolygon( currentRoom );
				updateRoomList();
				getLeftPanel().getMainComponent().displayFloor();
			}
		} );
		eastPanel.add( deleteRoom, "0, " + ++row );
		row++;


		moveRoom = new JButton( "Epsilon-Verschieben" );
		moveRoom.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				projectControl.refineRoomCoordinates( (Room)selectedPolygon, ZETProperties.getRasterSizeSnap() );
				getLeftPanel().getMainComponent().displayFloor();
			}
		} );
		eastPanel.add( moveRoom, "0, " + ++row );
		row++;

		return eastPanel;
	}

	private JPanel getEastTeleportAreaPanel() {
		double size[][] = // Columns
						{{TableLayout.FILL},
			//Rows
			{TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, 20, TableLayout.FILL
			}
		};

		JPanel eastPanel = new JPanel( new TableLayout( size ) );

		int row = 0;

		// The area name
		lblTeleportAreaName = new JLabel( loc.getString( "Teleportation.Name") );
		eastPanel.add( lblTeleportAreaName, "0, " + row++ );
		txtTeleportAreaName = new JTextField();
		txtTeleportAreaName.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtTeleportAreaName.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
;//					((TeleportArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setName( txtTeleportAreaName.getText() );
			}
		} );
		eastPanel.add( txtTeleportAreaName, "0, " + row++ );
		row++;

		// Target-Aea-Selector
		lblTargetArea = new JLabel( loc.getString( "Teleportation.TargetArea") );
		eastPanel.add( lblTargetArea, "0, " + row++ );
		cbxTargetArea = new JComboBox();
		cbxTargetArea.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged( ItemEvent e ) {
				if( cbxTargetArea.getSelectedIndex() == -1 )
					return;
//				if( getLeftPanel().getMainComponent().getSelectedPolygons().size() > 0 ) {
//					final TeleportArea a = (TeleportArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
//					a.setTargetArea( (TeleportArea)cbxTargetArea.getSelectedItem() );
//				}
			}
		} );
		cbxTargetArea.setRenderer( new ComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {

				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				setText( value != null ? ((TeleportArea)value).getName() : "" );
				return this;
			}
		} );
		eastPanel.add( cbxTargetArea, "0, " + row++ );
		row++;

		// Target-Exit-Selector
		lblTargetExit = new JLabel( loc.getString( "Teleportation.TargetExit" ) );
		eastPanel.add( lblTargetExit, "0, " + row++ );
		cbxTargetExit = new JComboBox();
		cbxTargetExit.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged( ItemEvent e ) {
				if( cbxTargetExit.getSelectedIndex() == -1 )
					return;
//				if( getLeftPanel().getMainComponent().getSelectedPolygons().size() > 0 ) {
//					final TeleportArea a = (TeleportArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
//					a.setExitArea( (EvacuationArea)cbxTargetExit.getSelectedItem() );
//				}
			}
		} );
		cbxTargetExit.setRenderer( new ComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				setText( value != null ? ((EvacuationArea)value).getName() : "" );
				return this;
			}
		} );
		eastPanel.add( cbxTargetExit, "0, " + row++ );
		row++;

		return eastPanel;
	}

	private JPanel getEastEdgePanel() {
		double size[][] = // Columns
						{{TableLayout.FILL},
			//Rows
			{TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, 20, TableLayout.FILL
			}
		};

		JPanel eastPanel = new JPanel( new TableLayout( size ) );

		int row = 0;

		lblEdgeType = new JLabel( "Edge type" );
		eastPanel.add( lblEdgeType, "0, " + row++ );
		row++;

		lblEdgeLength = new JLabel( "Länge:" );
		eastPanel.add( lblEdgeLength, "0, " + row++ );
		row++;

		lblEdgeExitName = new JLabel( loc.getString( "Evacuation.Name" ) );
		eastPanel.add( lblEdgeExitName, "0, " + row++ );
		txtEdgeExitName = new JTextField();
		txtEdgeExitName.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtEdgeExitName.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
					// Find the attached exit and set the name
//					if( getLeftPanel().getMainComponent().getSelectedEdge() instanceof TeleportEdge ) {
//						TeleportEdge te = (TeleportEdge)getLeftPanel().getMainComponent().getSelectedEdge();
//						if( ((Room)te.getLinkTarget().getAssociatedPolygon()).getAssociatedFloor() instanceof DefaultEvacuationFloor ) {
//							// we have an evacuation exit
//							Room r = (Room)te.getLinkTarget().getAssociatedPolygon();
//							EvacuationArea ea = r.getEvacuationAreas().get( 0 );
//							ea.setName( txtEdgeExitName.getText() );
//						}
//					}
				}
					//((EvacuationArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setName( txtEdgeExitName.getText() );
			}
		} );
		eastPanel.add( txtEdgeExitName, "0, " + row++ );
		row++;

		// Attractivity
		lblEvacuationAttractivity = new JLabel( loc.getString( "Evacuation.Attractivity" ) );
		eastPanel.add( lblEvacuationAttractivity, "0, " + row++ );

		txtEvacuationAttractivity = new JTextField( " " );
		txtEvacuationAttractivity.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtEvacuationAttractivity.addKeyListener( new KeyAdapter() {
			@Override
			public void keyReleased( KeyEvent e ) {
//				try {
//					((EvacuationArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setAttractivity( nfInteger.parse(
//									txtEvacuationAttractivity.getText() ).intValue() );
//				} catch( ParseException ex ) {
//					ZETLoader.sendError( loc.getString( "gui.error.NonParsableNumberString" ) );
//				} catch( IllegalArgumentException ex ) {
//					ZETLoader.sendError( ex.getLocalizedMessage() );
//				}
			}
		} );
		eastPanel.add( txtEvacuationAttractivity, "0, " + row++ );
		row++;



		// The area name
//		lblTeleportAreaName = new JLabel( loc.getString( "Teleportation.Name") );
//		eastPanel.add( lblTeleportAreaName, "0, " + row++ );
//
//		// Target-Aea-Selector
//		lblTargetArea = new JLabel( loc.getString( "Teleportation.TargetArea") );
//		eastPanel.add( lblTargetArea, "0, " + row++ );
//		cbxTargetArea = new JComboBox();
//		cbxTargetArea.addItemListener( new ItemListener() {
//			@Override
//			public void itemStateChanged( ItemEvent e ) {
//				if( cbxTargetArea.getSelectedIndex() == -1 )
//					return;
//				if( getLeftPanel().getMainComponent().getSelectedPolygons().size() > 0 ) {
//					final TeleportArea a = (TeleportArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
//					a.setTargetArea( (TeleportArea)cbxTargetArea.getSelectedItem() );
//				}
//			}
//		} );
//		cbxTargetArea.setRenderer( new ComboBoxRenderer() {
//			@Override
//			public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
//
//				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
//				setText( value != null ? ((TeleportArea)value).getName() : "" );
//				return this;
//			}
//		} );
//		eastPanel.add( cbxTargetArea, "0, " + row++ );
//		row++;
//
//		// Target-Exit-Selector
//		lblTargetExit = new JLabel( loc.getString( "Teleportation.TargetExit" ) );
//		eastPanel.add( lblTargetExit, "0, " + row++ );
//		cbxTargetExit = new JComboBox();
//		cbxTargetExit.addItemListener( new ItemListener() {
//			@Override
//			public void itemStateChanged( ItemEvent e ) {
//				if( cbxTargetExit.getSelectedIndex() == -1 )
//					return;
//				if( getLeftPanel().getMainComponent().getSelectedPolygons().size() > 0 ) {
//					final TeleportArea a = (TeleportArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
//					a.setExitArea( (EvacuationArea)cbxTargetExit.getSelectedItem() );
//				}
//			}
//		} );
//		cbxTargetExit.setRenderer( new ComboBoxRenderer() {
//			@Override
//			public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
//				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
//				setText( value != null ? ((EvacuationArea)value).getName() : "" );
//				return this;
//			}
//		} );
//		eastPanel.add( cbxTargetExit, "0, " + row++ );
//		row++;

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

		// Floor properties
		lblFloorName.setText( loc.getString( "Floor.Name" ) );
		btnFloorUp.setText( loc.getString( "Floor.Up" ) );
		btnFloorUp.setToolTipText( loc.getString( "Floor.Up.ToolTip" ) );
		btnFloorDown.setText( loc.getString( "Floor.Down" ) );
		btnFloorDown.setToolTipText( loc.getString( "Floor.Down.ToolTip" ) );
		lblFloorxOffset.setText( loc.getString( "Floor.xOffset" ) );
		lblFlooryOffset.setText( loc.getString( "Floor.yOffset" ) );
		lblFloorWidth.setText( loc.getString( "Floor.Width" ) );
		lblFloorHeight.setText( loc.getString( "Floor.Height" ) );
		lblFloorSizeDesc.setText( loc.getString( "Floor.Area" ) );

		// Room properties
		lblRoomName.setText( loc.getString( "Room.Name" ) );
		lblRoomSizeDesc.setText( loc.getString( "Room.Area" ) );

		// Assignment area
		lblAssignmentType.setText( loc.getString( "Assignment.Type" ) );
		lblAssignmentEvacueeNumber.setText( loc.getString( "Assignment.Persons" ) );
		btnAssignmentSetDefaultEvacuees.setText( loc.getString( "Assignment.SetDefaultEvacuees" ) );
		btnAssignmentSetDefaultEvacuees.setToolTipText( loc.getString( "Assignment.SetDefaultEvacuees.ToolTip" ) );
		lblPreferredExit.setText( loc.getString( "Assignment.PreferredExit" ) );
		lblMaxPersonsDesc.setText( loc.getString( "Assignment.MaxPersons" ) );
		lblMaxPersonsWarning.setText( loc.getString( "Assignment.AreaWarning" ) );
		lblAreaSizeDesc.setText( loc.getString( "Assignment.Area" ) );

		// Delay area
		lblDelayType.setText( loc.getString( "Delay.Type" ) + ":" );
		lblDelayFactor.setText( loc.getString( "Delay.Factor" ) );
		btnDelaySetDefault.setText( loc.getString( "Delay.TypeDefault" ) );
		btnDelaySetDefault.setToolTipText( loc.getString( "Delay.TypeDefault.ToolTip" ) );

		// Save area

		// Evacuation area
		lblEvacuationAreaName.setText( loc.getString( "Evacuation.Name" ) );
		lblEvacuationAttractivity.setText( loc.getString( "Evacuation.Attractivity" ) );

		// Stair Area
		lblStairFactorUp.setText( loc.getString( "Stair.FactorUp" ) + ":" );
		lblStairFactorDown.setText( loc.getString( "Stair.FactorDown" ) + ":" );
		lblStairPreset.setText( loc.getString( "Stair.Preset" ) + ":" );
		lblStairPresetDescription.setText( ZLocalization.getSingleton().getString( ((StairPreset)cbxStairPresets.getSelectedItem() ).getText() ) );

		// Teleportation area
		lblTeleportAreaName.setText( "Teleportation.Name" );
		lblTargetArea.setText( loc.getString( "Teleportation.TargetArea" ) );
		lblTargetExit.setText( loc.getString( "Teleportation.TargetExit" ) );

		loc.clearPrefix();
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
	 * Changes the selected displayed floor. This includes changing the GUI so
	 * that it displays the new floor.
	 * @param floor the new floor that is shown
	 */
	public void changeFloor( Floor floor ) {
		floorSelector.setSelectedItem( floor );
	}

	public void displayProject() {
		displayProject( projectControl );
	}

	/**
	 * Sets the z {@link ds.z.Project} that is displayed in the edit view.
	 * @param projectControl
	 */
	final public void displayProject( ZControl projectControl ) {
		if( projectControl != null ) {

			// Clearing is done in the set-methods called later ?
			//floorSelector.clear();
			roomSelector.clear();
			assignmentTypeSelector.clear();
		}

		// derregister old floor
		EventServer.getInstance().unregisterListener( getFloor(), ZModelRoomEvent.class );
		JFloor floor = new JFloor( editStatus, guiControl, projectControl.getProject().getBuildingPlan().getFloors().get( 1 ) );
		editStatus.controlFloor( floor, projectControl.getProject().getBuildingPlan().getFloors().get( 1 ) );
		getLeftPanel().setMainComponent( floor );
		EventServer.getInstance().registerListener( getFloor(), ZModelRoomEvent.class );

		this.projectControl = projectControl;

		if( projectControl != null ) {
			guiControl.getPolygonPopup().recreate( projectControl.getProject().getCurrentAssignment() );
			guiControl.getEdgePopup().recreate();
			guiControl.getPointPopup().recreate();
		}

		//This is independent of the rest of the displaying work
		//floorSelector.displayFloors( projectControl.getProject() );
		updateFloorList();
		assignmentTypeSelector.setControl( projectControl );
		assignmentTypeSelector.displayAssignmentTypesForCurrentProject();
		// If more than one floor, display the second.
		// what happens if a project has no floor?
		if( PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) )
			if( projectControl.getProject().getBuildingPlan().getFloors().size() >= 2 )
				changeFloor( projectControl.getProject().getBuildingPlan().getFloors().get( 1 ) );
			else
				changeFloor( projectControl.getProject().getBuildingPlan().getFloors().get( 0 ) );
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
	 * Sets the selected floor to a specified id
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
		cbxPreferredExit.removeAllItems();
		cbxTargetExit.removeAllItems();
		cbxTargetArea.removeAllItems();
		for( Floor f : projectControl.getProject().getBuildingPlan().getFloors() )
			for( Room r : f.getRooms() ) {
				for( EvacuationArea e : r.getEvacuationAreas() ) {
					cbxPreferredExit.addItem( e );
					cbxTargetExit.addItem( e );
				}
				for( TeleportArea e : r.getTeleportAreas() )
					cbxTargetArea.addItem( e );
			}
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
	public void updateFloorList() {
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

	public void setEditMode( EditModeOld em ) {
		if( getLeftPanel().getMainComponent() == null )
			return;
//		getLeftPanel().getMainComponent().resetEdit();
//		getLeftPanel().getMainComponent().setEditMode( em );
	}

	public void setFloorNameFocus() {
		txtFloorName.requestFocusInWindow();
		txtFloorName.setSelectionStart( 0 );
		txtFloorName.setSelectionEnd( txtFloorName.getText().length() );
	}

	public void setRoomNameFocus() {
		txtRoomName.requestFocusInWindow();
		txtRoomName.setSelectionStart( 0 );
		txtRoomName.setSelectionEnd( txtRoomName.getText().length() );
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

	public void stateChanged( /*ChangeEvent e*/ ) {
		// Show possibly new floor list (floors added/removed or names changed)
//		if( (e.getSource() instanceof BuildingPlan) || (e.getSource() instanceof Floor && e.getField() != null ? e.getField().equals( "name" ) : false) )
//			floorSelector.displayFloors( myProject );
		// TODO-Event: hier wird das evacuation-created-event abgefangen!
		// irgendwie anders regeln
//		if( e instanceof EvacuationAreaCreatedEvent ) {
//			EvacuationAreaCreatedEvent eac = (EvacuationAreaCreatedEvent)e;
//			if( eac.getMessage().equals( "created" ) )
//				cbxPreferredExit.addItem( eac.getSource() );
//			else
//				cbxPreferredExit.removeItem( eac.getSource() );
//		}
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
}
