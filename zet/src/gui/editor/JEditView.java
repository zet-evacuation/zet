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
 * Erstellt 29.04.2008, 21:06:42
 */
package gui.editor;

import gui.components.AbstractSplitPropertyWindow;
import ds.PropertyContainer;
import ds.z.Assignment;
import ds.z.AssignmentArea;
import ds.z.AssignmentType;
import ds.z.DelayArea;
import ds.z.Edge;
import ds.z.EvacuationArea;
import ds.z.Floor;
import ds.z.PlanPoint;
import ds.z.PlanPolygon;
import ds.z.Room;
import ds.z.RoomEdge;
import ds.z.StairArea;
import ds.z.StairPreset;
import ds.z.TeleportArea;
import ds.z.TeleportEdge;
import ds.z.ZControl;
import gui.Control;
import gui.JEditor;
import gui.ZETMain;
import gui.ZETProperties;
import gui.components.ComboBoxRenderer;
import gui.components.framework.Button;
import gui.components.AssignmentTypeComboBoxModel;
import gui.components.FloorComboBoxModel;
import gui.components.JFloorScrollPane;
import gui.components.JRuler;
import gui.components.RoomComboBoxModel;
import gui.components.framework.Menu;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JEditView extends AbstractSplitPropertyWindow<JFloorScrollPane<JFloor>> {
	/** Message code indicating that the delay area panel should be displayed. @see setEastPanelType() */
	public static final int DELAY_AREA_PANEL = 0;
	/** Message code indicating that the assignment area panel should be displayed. @see setEastPanelType() */
	public static final int ASSIGNMENT_AREA_PANEL = 1;
	/** Message code indicating that the room panel should be displayed. @see setEastPanelType() */
	public static final int ROOM_PANEL = 2;
	/** Message code indicating that the default panel should be displayed. @see setEastPanelType() */
	public static final int DEFAULT_PANEL = 3;
	/** Message code indicating that the floor panel should be displayed. @see setEastPanelType() */
	public static final int FLOOR_PANEL = 4;
	/** Message code indicating that the evacuation area panel should be displayed. @see setEastPanelType() */
	public static final int EVACUATION_AREA_PANEL = 5;
	/** Message code indicating that the stair area panel should be displayed. @see setEastPanelType() */
	public static final int STAIR_AREA_PANEL = 6;
	/** Message code indicating that the stair area panel should be displayed. @see setEastPanelType() */
	public static final int TELEPORT_AREA_PANEL = 7;
	/** The currently active panel type */
	private static int eastPanelType;
	/** The control object for the loaded project. */
  private ZControl projectControl;
	/** The currently visible {@link ds.z.Floor} */
	private Floor currentFloor;
	/** Model for a floor-selector combo box. */
	private FloorComboBoxModel floorSelector;
	/** Model for a room-selector combo box. */
	private RoomComboBoxModel roomSelector;
	/** Model for a assignmentType-selector combo box. */
	private gui.components.AssignmentTypeComboBoxModel assignmentTypeSelector;
	/** A lable that shows a string explaining the floor selection combo box. */
	private JLabel lblFloorSelector;
	/** A label that contains the number of the currently visible floor. */
	private JLabel lblFloorNumber;
	/** A label that shows a string explaining the room selection combo box. */
	private JLabel lblRoomSelector;
	// Graphics components that are not directly displayed on this JEditorPanel
	/** All JPolygons share the same popup menu, which is stored here. */
	private JPopupMenu pupPolygon;
	/** All JPolygons share the same popup menu listeners, which are stored here. */
	private List<PolygonPopupListener> polygonPopupListeners;
	/** All JEdges share the same popup menu, which is stored here. */
	private JPopupMenu pupEdge;
	/** All JEdges share the same popup menu listeners, which are stored here. */
	private List<EdgePopupListener> edgePopupListeners;
	/** All JEdges share a second popup menu which is only displayed on edge points. 
	 * This menu is stored here. */
	private JPopupMenu pupPoint;
	/** All JEdges share the same popup menu listeners for pupPoint, which are stored here. */
	private List<PointPopupListener> pointPopupListeners;
	// Components for the east bar
	private JComboBox cbxFloors;
	/**  The CardLayout object of the east subpanel. */
	private CardLayout eastSubBarCardLayout;
	/** The panel representing the variable part of the east bar. */
	private JPanel eastSubBar;
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
	private final Control guiControl;

	/** Describes the teleport area name field. */
	private JLabel lblTeleportAreaName;
	/** The anme filed for a teleport area. */
	private JTextField txtTeleportAreaName;

	/** Describes the target area combo box.  */
	private JLabel lblTargetArea;
	/** A combo box selecting the target area of a target area. */
	private JComboBox cbxTargetArea;

	/** A combo box selecting the possible exits for a teleport area. */
	private JComboBox cbxTargetExit;
	/** Describes the target exit combo box. */
	private JLabel lblTargetExit;

	/**
	 * Initializes the editing view of ZET.
	 * @param projectControl the project which is displayed in the editor
	 * @param guiControl
	 */
	public JEditView( ZControl projectControl, Control guiControl ) {
		super( new JFloorScrollPane<JFloor>( new JFloor( guiControl ) ) );
		this.guiControl = guiControl;
		// this.myProject = project; is now called later - TIMON
		final JFloor centerPanel = this.getLeftPanel().getMainComponent();
		centerPanel.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				if( centerPanel.getSelectedPolygons() == null ||
								centerPanel.getSelectedPolygons().size() != 1 )
					setEastPanelType( FLOOR_PANEL );
				else {
					PlanPolygon p = centerPanel.getSelectedPolygons().get( 0 ).getPlanPolygon();
					if( p instanceof Room )
						setEastPanelType( ROOM_PANEL );
					else if( p instanceof DelayArea )
						setEastPanelType( DELAY_AREA_PANEL );
					else if( p instanceof EvacuationArea )
						setEastPanelType( EVACUATION_AREA_PANEL );
					else if( p instanceof AssignmentArea )
						setEastPanelType( ASSIGNMENT_AREA_PANEL );
					else if( p instanceof StairArea )
						setEastPanelType( STAIR_AREA_PANEL );
					else if ( p instanceof TeleportArea )
						setEastPanelType( TELEPORT_AREA_PANEL );
					else
						setEastPanelType( DEFAULT_PANEL );
				}
			}
		} );
		displayProject( projectControl );
		this.getLeftPanel().getHorizontalScrollBar().addAdjustmentListener( adlPlanImage );
		this.getLeftPanel().getVerticalScrollBar().addAdjustmentListener( adlPlanImage );
	}
	
	public JEditView( Control guiControl ) {
		super( new JFloorScrollPane<JFloor>( new JFloor( guiControl ) ) );
		this.guiControl = guiControl;
		final JFloor centerPanel = this.getLeftPanel().getMainComponent();
		centerPanel.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				if( centerPanel.getSelectedPolygons() == null ||
								centerPanel.getSelectedPolygons().size() != 1 )
					setEastPanelType( FLOOR_PANEL );
				else {
					PlanPolygon p = centerPanel.getSelectedPolygons().get( 0 ).getPlanPolygon();
					if( p instanceof Room )
						setEastPanelType( ROOM_PANEL );
					else if( p instanceof DelayArea )
						setEastPanelType( DELAY_AREA_PANEL );
					else if( p instanceof EvacuationArea )
						setEastPanelType( EVACUATION_AREA_PANEL );
					else if( p instanceof AssignmentArea )
						setEastPanelType( ASSIGNMENT_AREA_PANEL );
					else if( p instanceof StairArea )
						setEastPanelType( STAIR_AREA_PANEL );
					else if( p instanceof TeleportArea )
						setEastPanelType( TELEPORT_AREA_PANEL );
					else
						setEastPanelType( DEFAULT_PANEL );
				}
			}
		} );
		this.getLeftPanel().getHorizontalScrollBar().addAdjustmentListener( adlPlanImage );
		this.getLeftPanel().getVerticalScrollBar().addAdjustmentListener( adlPlanImage );
	}
	private AdjustmentListener adlPlanImage = new AdjustmentListener() {
		@Override
		public void adjustmentValueChanged( AdjustmentEvent e ) {
			getLeftPanel().getMainComponent().getPlanImage().setVisibleRect( getLeftPanel().getMainComponent().getVisibleRect() );
			getLeftPanel().getMainComponent().getPlanImage().update();
		}
	};


	/**
	 * Returns the index of the panel currently visible in the east panel.
	 * @return the index of the panel currently visible in the east panel
	 */
	public int getEastPanelType() {
		return eastPanelType;
	}

	/**
	 * Sets the panel visible in the east panel.
	 * @param panel the panel
	 */
	public void setEastPanelType( int panel ) {
		PlanPolygon selectedPolygon = (panel == DEFAULT_PANEL || panel == FLOOR_PANEL) ? null : getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
		switch( panel ) {
			case DELAY_AREA_PANEL:
				txtDelayFactor.setText( nfFloat.format( ((DelayArea)selectedPolygon).getSpeedFactor() ) );
				cbxDelayType.setSelectedItem( ((DelayArea)selectedPolygon).getDelayType() );
				eastSubBarCardLayout.show( eastSubBar, "delayArea" );
				break;
			case STAIR_AREA_PANEL:
				txtStairFactorUp.setText( nfFloat.format( ((StairArea)selectedPolygon).getSpeedFactorUp() ) );
				txtStairFactorDown.setText( nfFloat.format( ((StairArea)selectedPolygon).getSpeedFactorDown() ) );
				eastSubBarCardLayout.show( eastSubBar, "stairArea" );
				break;
			case ASSIGNMENT_AREA_PANEL:
				txtNumberOfPersons.setText( nfInteger.format( ((AssignmentArea)selectedPolygon).getEvacuees() ) );
				assignmentTypeSelector.setSelectedItem( ((AssignmentArea)selectedPolygon).getAssignmentType() );
				eastSubBarCardLayout.show( eastSubBar, "assignmentArea" );
				double area = Math.round( selectedPolygon.areaMeter() * 100 ) / 100.0;
				lblAreaSize.setText( nfFloat.format( area ) + " m²" );
				if( projectControl.getProject().getBuildingPlan().getRasterized() ) {
					lblMaxPersons.setText( nfInteger.format( ((AssignmentArea)selectedPolygon).getMaxEvacuees() ) );
					lblMaxPersonsWarning.setText( "" );
				} else {
					double persons = Math.round( (area / (0.4 * 0.4)) * 100 ) / 100.0;
					lblMaxPersons.setText( nfFloat.format( persons ) );
					lblMaxPersonsWarning.setText( loc.getString( "gui.editor.JEditorPanel.labelAreaWarning" ) );
				}
				if( ((AssignmentArea)selectedPolygon).getExitArea() == null )
					cbxPreferredExit.setSelectedIndex( -1 );
				else
					cbxPreferredExit.setSelectedItem( ((AssignmentArea)selectedPolygon).getExitArea() );
				break;
			case ROOM_PANEL:
				txtRoomName.setText( ((Room)selectedPolygon).getName() );
				eastSubBarCardLayout.show( eastSubBar, "room" );
				break;
			case DEFAULT_PANEL:
				eastSubBarCardLayout.show( eastSubBar, "default" );
				break;
			case FLOOR_PANEL:
				txtFloorName.setText( this.currentFloor.getName() );
				eastSubBarCardLayout.show( eastSubBar, "floor" );
				break;
			case EVACUATION_AREA_PANEL:
				txtEvacuationAreaName.setText( ((EvacuationArea)selectedPolygon).getName() );
				txtEvacuationAttractivity.setText( nfInteger.format( ((EvacuationArea)selectedPolygon).getAttractivity() ) );
				eastSubBarCardLayout.show( eastSubBar, "evacuation" );
				break;
			case TELEPORT_AREA_PANEL:
				eastSubBarCardLayout.show( eastSubBar, "teleport" );
				txtTeleportAreaName.setText( ((TeleportArea)selectedPolygon).getName() );
				if( ((TeleportArea)selectedPolygon).getExitArea() == null )
					cbxTargetExit.setSelectedIndex( -1 );
				else
					cbxTargetExit.setSelectedItem( ((TeleportArea)selectedPolygon).getExitArea() );

				if( ((TeleportArea)selectedPolygon).getTargetArea() == null )
					cbxTargetArea.setSelectedIndex( -1 );
				else
					cbxTargetArea.setSelectedItem( ((TeleportArea)selectedPolygon).getTargetArea() );
				break;
			default:
				JOptionPane.showMessageDialog( JEditor.getInstance(),
								loc.getString( "gui.editor.JEditorPanel.WrongPanelID" ),
								loc.getString( "gui.NotGraveError" ), JOptionPane.ERROR_MESSAGE );
				return;
		}
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

		floorSelector = new FloorComboBoxModel();
		cbxFloors = new JComboBox();
		cbxFloors.setModel( floorSelector );
		cbxFloors.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				if( cbxFloors.getSelectedItem() != null )
;//					((Floor)cbxFloors.getSelectedItem()).removeChangeListener( roomSelector );
				else
					return;

				final int add = PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) ? 1 : 0;
				lblFloorNumber.setText( String.format( loc.getStringWithoutPrefix( "gui.editor.JEditorPanel.labelOnFloor" ), cbxFloors.getSelectedIndex() + add ) );
				btnFloorDown.setEnabled( !(cbxFloors.getSelectedIndex() == 0 || cbxFloors.getSelectedIndex() == 1 && add == 0) );
				JEditor.getInstance().enableMenuFloorDown( !(cbxFloors.getSelectedIndex() == 0 || cbxFloors.getSelectedIndex() == 1 && add == 0) );
				btnFloorUp.setEnabled( !(cbxFloors.getSelectedIndex() == cbxFloors.getItemCount() - 1 || cbxFloors.getSelectedIndex() == 0 && add == 0) );
				JEditor.getInstance().enableMenuFloorUp( !(cbxFloors.getSelectedIndex() == cbxFloors.getItemCount() - 1 || cbxFloors.getSelectedIndex() == 0 && add == 0) );
				Floor dspFloor = (Floor)cbxFloors.getSelectedItem();
				currentFloor = dspFloor;
				updateFloorView();
//				dspFloor.addChangeListener( roomSelector );
				getLeftPanel().getTopRuler().setWidth( dspFloor.getWidth() );
				getLeftPanel().getLeftRuler().setHeight( dspFloor.getHeight() );
				getLeftPanel().getTopRuler().offset = zet.util.ConversionTools.roundScale3( dspFloor.getxOffset() / 1000.0 - 0.8 );
				getLeftPanel().getLeftRuler().offset = zet.util.ConversionTools.roundScale3( dspFloor.getyOffset() / 1000.0 - 0.8 );

				// FloorName
				txtFloorName.setText( dspFloor.getName() );
				// Title of the window
				JEditor.getInstance().setTitle( getTitleBarText() );
			}
		} );
		cbxFloors.setRenderer( new ComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent( JList list, Object value,
							int index, boolean isSelected, boolean cellHasFocus ) {
				JLabel me = (JLabel)super.getListCellRendererComponent( list, value, index,
								isSelected, cellHasFocus );

				if( value != null )
					setText( ((Floor)value).getName() );
				return this;
			}
		} );

		final JComboBox cbxRooms = new JComboBox();
		roomSelector = new RoomComboBoxModel( projectControl, floorSelector );
		floorSelector.setRoomSelector( roomSelector );
		cbxRooms.setModel( roomSelector );
		cbxRooms.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				getFloor().showPolygon( (PlanPolygon)cbxRooms.getSelectedItem() );
			}
		} );
		cbxRooms.setRenderer( new ComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent( JList list, Object value,
							int index, boolean isSelected, boolean cellHasFocus ) {
				JLabel me = (JLabel)super.getListCellRendererComponent( list, value, index,
								isSelected, cellHasFocus );

				if( value != null ) {
					Room p = (Room)value;
					setText( p.getName() );
				}
				return this;
			}
		} );

		int row = 1;

		loc.setPrefix( "" );
		lblFloorSelector = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelFloors" ) + ":" );
		eastPanel.add( lblFloorSelector, "1, " + row++ );
		eastPanel.add( cbxFloors, "1, " + row++ );
		lblFloorNumber = new JLabel( "1" );
		eastPanel.add( lblFloorNumber, "1, " + row++ );
		row++;
		lblRoomSelector = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelRooms" ) + ":" );
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

		eastPanel.add( eastSubBar, "1, " + row++ );

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
		lblAssignmentType = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelAssignmentType" ) );
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
		lblAssignmentEvacueeNumber = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelPersons" ) );
		eastPanel.add( lblAssignmentEvacueeNumber, "0, " + row++ );
		txtNumberOfPersons = new JTextField( "10" );
		txtNumberOfPersons.addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent e ) {
				JEditor.setEditing( true );
			}

			public void focusLost( FocusEvent e ) {
				JEditor.setEditing( false );
			}
		} );
		txtNumberOfPersons.addKeyListener( new KeyListener() {
			public void keyTyped( KeyEvent e ) {
			}

			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
					try {
						int persons = Math.min( nfInteger.parse( txtNumberOfPersons.getText() ).intValue(), ((AssignmentArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).getMaxEvacuees() );
						((AssignmentArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setEvacuees( persons );
					} catch( Exception ex ) {
						ZETMain.sendError( ex.getLocalizedMessage() );
					}
			}

			public void keyReleased( KeyEvent e ) {
			}
		} );
		eastPanel.add( txtNumberOfPersons, "0, " + row++ );
		row++;
		btnAssignmentSetDefaultEvacuees = Button.newButton( loc.getString( "gui.editor.JEditorPanel.assignmentNameDefault" ), loc.getString( "gui.editor.JEditorPanel.assignmentTooltipDefault" ) );
		btnAssignmentSetDefaultEvacuees.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				AssignmentArea a = (AssignmentArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
				int persons = Math.min( a.getAssignmentType().getDefaultEvacuees(), a.getMaxEvacuees() );
				a.setEvacuees( persons );
				txtNumberOfPersons.setText( nfInteger.format( a.getEvacuees() ) );
			}
		} );
		eastPanel.add( btnAssignmentSetDefaultEvacuees, "0, " + row++ );
		row++;

		// Preferred-Exit-Selector
		lblPreferredExit = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelPreferredExit" ) );
		eastPanel.add( lblPreferredExit, "0, " + row++ );
		cbxPreferredExit = new JComboBox();
		cbxPreferredExit.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e ) {
				//if( e.getStateChange() == ItemEvent.SELECTED ) {
				if( cbxPreferredExit.getSelectedIndex() == -1 )
					return;
				if( getLeftPanel().getMainComponent().getSelectedPolygons().size() > 0 ) {
					AssignmentArea a = (AssignmentArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
					a.setExitArea( (EvacuationArea)cbxPreferredExit.getSelectedItem() );
				}
			//}
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

		lblAreaSizeDesc = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelArea" ) );
		lblAreaSize = new JLabel( "" );
		eastPanel.add( lblAreaSizeDesc, "0, " + row++ );
		eastPanel.add( lblAreaSize, "0, " + row++ );
		row++;

		lblMaxPersonsDesc = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelMaxPersons" ) );
		lblMaxPersons = new JLabel( "" );
		eastPanel.add( lblMaxPersonsDesc, "0, " + row++ );
		eastPanel.add( lblMaxPersons, "0, " + row++ );
		row++;

		lblMaxPersonsWarning = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelAreaWarning" ) );
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
		lblDelayType = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelDelayType" ) + ":" );
		eastPanel.add( lblDelayType, "0, " + row++ );
		cbxDelayType = new JComboBox( new DefaultComboBoxModel( DelayArea.DelayType.values() ) );
		cbxDelayType.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e ) {
				if( e.getStateChange() == ItemEvent.SELECTED )
					((DelayArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setDelayType( (DelayArea.DelayType)e.getItem() );
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
		lblDelayFactor = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelFactor" ) );
		eastPanel.add( lblDelayFactor, "0, " + row++ );
		txtDelayFactor = new JTextField( " " );
		txtDelayFactor.addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent e ) {
				JEditor.setEditing( true );
			}

			public void focusLost( FocusEvent e ) {
				JEditor.setEditing( false );
			}
		} );
		txtDelayFactor.addKeyListener( new KeyListener() {
			public void keyTyped( KeyEvent e ) {
			}

			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
					try {
						((DelayArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setSpeedFactor( nfFloat.parse( txtDelayFactor.getText() ).doubleValue() );
					} catch( ParseException ex ) {
						ZETMain.sendError( loc.getString( "gui.error.NonParsableFloatString" ) );
					} catch( IllegalArgumentException ex ) {
						ZETMain.sendError( ex.getLocalizedMessage() );
					}
			}

			public void keyReleased( KeyEvent e ) {
			}
		} );
		eastPanel.add( txtDelayFactor, "0, " + row++ );
		row++;

		btnDelaySetDefault = Button.newButton( loc.getString( "gui.editor.JEditorPanel.delayTypeDefault" ),
						loc.getString( "gui.editor.JEditorPanel.delayTypeDefaultToolTip" ) );
		btnDelaySetDefault.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				DelayArea a = (DelayArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
				a.setSpeedFactor( a.getDelayType().defaultSpeedFactor );
				txtDelayFactor.setText( nfFloat.format( a.getSpeedFactor() ) );
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
		lblStairFactorUp = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelStairFactorUp" ) + ":" );
		eastPanel.add( lblStairFactorUp, "0, " + row++ );
		txtStairFactorUp = new JTextField( " " );
		txtStairFactorUp.addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent e ) {
				JEditor.setEditing( true );
			}

			public void focusLost( FocusEvent e ) {
				JEditor.setEditing( false );
			}
		} );
		txtStairFactorUp.addKeyListener( new KeyListener() {
			public void keyTyped( KeyEvent e ) {
			}

			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
					try {
						((StairArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setSpeedFactorUp( nfFloat.parse(
										txtStairFactorUp.getText() ).doubleValue() );
					} catch( ParseException ex ) {
						ZETMain.sendError( loc.getString( "gui.error.NonParsableFloatString" ) );
					} catch( IllegalArgumentException ex ) {
						ZETMain.sendError( ex.getLocalizedMessage() );
					}
			}

			public void keyReleased( KeyEvent e ) {
			}
		} );
		eastPanel.add( txtStairFactorUp, "0, " + row++ );
		row++;

		// DelayFactor for going downwards
		lblStairFactorDown = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelStairFactorDown" ) + ":" );
		eastPanel.add( lblStairFactorDown, "0, " + row++ );
		txtStairFactorDown = new JTextField( " " );
		txtStairFactorDown.addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent e ) {
				JEditor.setEditing( true );
			}

			public void focusLost( FocusEvent e ) {
				JEditor.setEditing( false );
			}
		} );
		txtStairFactorDown.addKeyListener( new KeyListener() {
			public void keyTyped( KeyEvent e ) {
			}

			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
					try {
						((StairArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setSpeedFactorDown( nfFloat.parse( txtStairFactorDown.getText() ).doubleValue() );
					} catch( ParseException ex ) {
						ZETMain.sendError( loc.getString( "gui.error.NonParsableFloatString" ) );
					} catch( IllegalArgumentException ex ) {
						ZETMain.sendError( ex.getLocalizedMessage() );
					}
			}

			public void keyReleased( KeyEvent e ) {
			}
		} );
		eastPanel.add( txtStairFactorDown, "0, " + row++ );
		row++;

		// Add combo box with presets
		lblStairPreset = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelStairPreset" ) + ":" );
		cbxStairPresets = new JComboBox();
		cbxStairPresets.addItem( StairPreset.Indoor );
		cbxStairPresets.addItem( StairPreset.Outdoor );
		eastPanel.add( lblStairPreset, "0, " + row++ );
		eastPanel.add( cbxStairPresets, "0, " + row++ );
		cbxStairPresets.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent arg0 ) {
				StairPreset sp = (StairPreset)cbxStairPresets.getSelectedItem();
				NumberFormat nf = loc.getFloatConverter();
				txtStairFactorUp.setText( nf.format( sp.getSpeedFactorUp() ) );
				txtStairFactorDown.setText( nf.format( sp.getSpeedFactorDown() ) );
			}
		});
		lblStairPresetDescription = new JLabel( loc.getString( ((StairPreset)cbxStairPresets.getSelectedItem()).getText() ) ) ;
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

		lblEvacuationAreaName = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelNameEvacuationArea" ) );
		eastPanel.add( lblEvacuationAreaName, "0, " + row++ );
		txtEvacuationAreaName = new JTextField();
		txtEvacuationAreaName.addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent e ) {
				JEditor.setEditing( true );
			}

			public void focusLost( FocusEvent e ) {
				JEditor.setEditing( false );
			}
		} );
		txtEvacuationAreaName.addKeyListener( new KeyListener() {
			public void keyTyped( KeyEvent e ) {
			}

			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
					((EvacuationArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setName( txtEvacuationAreaName.getText() );
			}

			public void keyReleased( KeyEvent e ) {
			}
		} );
		eastPanel.add( txtEvacuationAreaName, "0, " + row++ );
		row++;

		// Attractivity
		lblEvacuationAttractivity = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelAttractivity" ) );
		eastPanel.add( lblEvacuationAttractivity, "0, " + row++ );

		txtEvacuationAttractivity = new JTextField( " " );
		txtEvacuationAttractivity.addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent e ) {
				JEditor.setEditing( true );
			}

			public void focusLost( FocusEvent e ) {
				JEditor.setEditing( false );
			}
		} );
		txtEvacuationAttractivity.addKeyListener( new KeyListener() {
			public void keyTyped( KeyEvent e ) {
			}

			public void keyPressed( KeyEvent e ) {
			}

			public void keyReleased( KeyEvent e ) {
				try {
					((EvacuationArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setAttractivity( nfInteger.parse(
									txtEvacuationAttractivity.getText() ).intValue() );
				} catch( ParseException ex ) {
					ZETMain.sendError( loc.getString( "gui.error.NonParsableNumberString" ) );
				} catch( IllegalArgumentException ex ) {
					ZETMain.sendError( ex.getLocalizedMessage() );
				}
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
				TableLayout.FILL
			}
		};

		JPanel eastPanel = new JPanel( new TableLayout( size ) );

		lblFloorName = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelNameFloor" ) );
		eastPanel.add( lblFloorName, "0,0,2,0" );
		txtFloorName = new JTextField();
		txtFloorName.addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent e ) {
				JEditor.setEditing( true );
			}

			public void focusLost( FocusEvent e ) {
				JEditor.setEditing( false );
			}
		} );
		txtFloorName.addKeyListener( new KeyListener() {
			public void keyTyped( KeyEvent e ) {
			}

			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
					currentFloor.setName( txtFloorName.getText() );
			}

			public void keyReleased( KeyEvent e ) {
			}
		} );
		eastPanel.add( txtFloorName, "0,1,2,1" );

		ActionListener aclFloor = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				if( e.getActionCommand().equals( "down" ) ) {
					final int oldIndex = cbxFloors.getSelectedIndex();
					JEditor.getInstance().getZControl().moveFloorDown( getFloorID() + (ZETProperties.isDefaultFloorHidden() ? 1 : 0) );
					cbxFloors.setSelectedIndex( oldIndex - 1 );
				} else if( e.getActionCommand().equals( "up" ) ) {
					final int oldIndex = cbxFloors.getSelectedIndex();
					JEditor.getInstance().getZControl().moveFloorUp( getFloorID() + (ZETProperties.isDefaultFloorHidden() ? 1 : 0) );
					cbxFloors.setSelectedIndex( oldIndex + 1 );
				} else
					ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " +
									loc.getString( "gui.ContactDeveloper" ) );
			}
		};
		btnFloorUp = Button.newButton( loc.getString( "gui.editor.JEditorPanel.floorUp" ), aclFloor, "up", loc.getString( "gui.editor.JEditorPanel.floorUp.ToolTip" ) );
		eastPanel.add( btnFloorUp, "0,3,0,3" );

		btnFloorDown = Button.newButton( loc.getString( "gui.editor.JEditorPanel.floorDown" ), aclFloor, "down", loc.getString( "gui.editor.JEditorPanel.floorDown.ToolTip" ) );

		eastPanel.add( btnFloorDown, "2,3" );
		return eastPanel;
	}

	private JPanel getEastRoomPanel() {
		double size[][] = // Columns
						{{TableLayout.FILL},
			//Rows
			{TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.FILL
			}
		};

		JPanel eastPanel = new JPanel( new TableLayout( size ) );

		int row = 0;

		lblRoomName = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelNameRoom" ) );
		eastPanel.add( lblRoomName, "0, " + row++ );
		txtRoomName = new JTextField();
		txtRoomName.addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent e ) {
				JEditor.setEditing( true );
			}

			public void focusLost( FocusEvent e ) {
				JEditor.setEditing( false );
			}
		} );
		txtRoomName.addKeyListener( new KeyListener() {
			public void keyTyped( KeyEvent e ) {
			}

			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
					((Room)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setName( txtRoomName.getText() );
			}

			public void keyReleased( KeyEvent e ) {
			}
		} );
		eastPanel.add( txtRoomName, "0, " + row++ );

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
		lblTeleportAreaName = new JLabel( "Name dieses Zielbereichs." );
		eastPanel.add( lblTeleportAreaName, "0, " + row++ );
		txtTeleportAreaName = new JTextField();
		txtTeleportAreaName.addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent e ) {
				JEditor.setEditing( true );
			}

			public void focusLost( FocusEvent e ) {
				JEditor.setEditing( false );
			}
		} );
		txtTeleportAreaName.addKeyListener( new KeyListener() {
			public void keyTyped( KeyEvent e ) {
			}

			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
					((TeleportArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setName( txtTeleportAreaName.getText() );
			}

			public void keyReleased( KeyEvent e ) {
			}
		} );
		eastPanel.add( txtTeleportAreaName, "0, " + row++ );
		row++;

		// Target-Aea-Selector
		lblTargetArea = new JLabel( "Zielbereich:" );
		eastPanel.add( lblTargetArea, "0, " + row++ );
		cbxTargetArea = new JComboBox();
		cbxTargetArea.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e ) {
				if( cbxTargetArea.getSelectedIndex() == -1 )
					return;
				if( getLeftPanel().getMainComponent().getSelectedPolygons().size() > 0 ) {
					final TeleportArea a = (TeleportArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
					a.setTargetArea( (TeleportArea)cbxTargetArea.getSelectedItem() );
				}
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
		lblTargetExit = new JLabel( "Zielpotential:" );
		eastPanel.add( lblTargetExit, "0, " + row++ );
		cbxTargetExit = new JComboBox();
		cbxTargetExit.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e ) {
				if( cbxTargetExit.getSelectedIndex() == -1 )
					return;
				if( getLeftPanel().getMainComponent().getSelectedPolygons().size() > 0 ) {
					final TeleportArea a = (TeleportArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
					a.setExitArea( (EvacuationArea)cbxTargetExit.getSelectedItem() );
				}
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

public void localize() {
		// Title of the window
		// Localization of child components
		getLeftPanel().localize();
		// Localization of own components
		lblFloorSelector.setText( loc.getString( "gui.editor.JEditorPanel.labelFloors" ) + ":" );
		lblFloorNumber.setText( String.format( loc.getStringWithoutPrefix( "gui.editor.JEditorPanel.labelOnFloor" ), cbxFloors.getSelectedIndex() + (PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) ? 1 : 0) ) );
		lblRoomSelector.setText( loc.getString( "gui.editor.JEditorPanel.labelRooms" ) + ":" );
		lblAssignmentType.setText( loc.getString( "gui.editor.JEditorPanel.labelAssignmentType" ) );
		lblAssignmentEvacueeNumber.setText( loc.getString( "gui.editor.JEditorPanel.labelPersons" ) );
		btnAssignmentSetDefaultEvacuees.setText( loc.getString( "gui.editor.JEditorPanel.assignmentNameDefault" ) );
		btnAssignmentSetDefaultEvacuees.setToolTipText( loc.getString( "gui.editor.JEditorPanel.assignmentTooltipDefault" ) );
		lblPreferredExit.setText( loc.getString( "gui.editor.JEditorPanel.labelPreferredExit" ) );
		lblDelayType.setText( loc.getString( "gui.editor.JEditorPanel.labelDelayType" ) + ":" );
		lblDelayFactor.setText( loc.getString( "gui.editor.JEditorPanel.labelFactor" ) );
		btnDelaySetDefault.setText( loc.getString( "gui.editor.JEditorPanel.delayTypeDefault" ) );
		btnDelaySetDefault.setToolTipText( loc.getString( "gui.editor.JEditorPanel.delayTypeDefaultToolTip" ) );
		btnFloorUp.setText( loc.getString( "gui.editor.JEditorPanel.floorUp" ) );
		btnFloorUp.setToolTipText( loc.getString( "gui.editor.JEditorPanel.floorUp.ToolTip" ) );
		btnFloorDown.setText( loc.getString( "gui.editor.JEditorPanel.floorDown" ) );
		btnFloorDown.setToolTipText( loc.getString( "gui.editor.JEditorPanel.floorDown.ToolTip" ) );
		lblFloorName.setText( loc.getString( "gui.editor.JEditorPanel.labelNameFloor" ) );
		lblRoomName.setText( loc.getString( "gui.editor.JEditorPanel.labelNameRoom" ) );
		lblEvacuationAreaName.setText( loc.getString( "gui.editor.JEditorPanel.labelNameEvacuationArea" ) );
		lblEvacuationAttractivity.setText( loc.getString( "gui.editor.JEditorPanel.labelAttractivity" ) );
		lblMaxPersonsDesc.setText( loc.getString( "gui.editor.JEditorPanel.labelMaxPersons" ) );
		lblMaxPersonsWarning.setText( loc.getString( "gui.editor.JEditorPanel.labelArea" ) );
		lblAreaSizeDesc.setText( loc.getString( "gui.editor.JEditorPanel.labelAreaWarning" ) );
		lblStairFactorUp.setText( loc.getString( "gui.editor.JEditorPanel.labelStairFactorUp" ) + ":" );
		lblStairFactorDown.setText( loc.getString( "gui.editor.JEditorPanel.labelStairFactorDown" ) + ":" );
		lblStairPreset.setText( loc.getString( "gui.editor.JEditorPanel.labelStairPreset" ) + ":" );
		lblStairPresetDescription.setText(loc.getString( ((StairPreset)cbxStairPresets.getSelectedItem() ).getText() ) );
	}

	/**
	 * Returns the current titlebar text of the window, so it can be set if
	 * the editor panel is displayed inside an window.
	 * @return the text
	 */
	protected String getTitleBarText() {
		if( projectControl.getProject().getProjectFile() != null )
			return projectControl.getProject().getProjectFile().getName() + " [" + currentFloor.getName() + "]" + " - " +
							loc.getString( "AppTitle" );
		else
			return loc.getString( "NewFile" ) + " [" + currentFloor.getName() + "]" + " - " + loc.getString( "AppTitle" );
	}

	/**
	 * Changes the selected displayed floor. This includes changing the GUI so
	 * that it displays "floor" instead of the previously displayed Floor object. 
	 * @param floor the new floor that is shown
	 */
	protected void changeFloor( Floor floor ) {
		floorSelector.setSelectedItem( floor );
	}

	public void displayProject() {
		displayProject( projectControl );
	}

	public void displayProject( ZControl projectControl ) {
		//boolean show_different_project = (this.projectControl.getZControl() != projectControl.getZControl());
		if( projectControl != null ) {
//			if( show_different_project )
//				myProject.removeChangeListener( this );

			// Clearing is done in the set-methods called later
			floorSelector.clear();
			roomSelector.clear();
			assignmentTypeSelector.clear();


			getLeftPanel().getMainComponent().setZcontrol( projectControl );
			getLeftPanel().getMainComponent().displayFloor( null );
		}

//		myProject = p;
		this.projectControl = projectControl;

		if( projectControl != null ) {
//			if( show_different_project )
//				p.addChangeListener( this );

			recreatePolygonPopupMenu( projectControl.getProject().getCurrentAssignment() );
			recreateEdgePopupMenu();
			recreatePointPopupMenu();
		}

		//This is independent of the rest of the displaying work
		floorSelector.displayFloors( projectControl.getProject() );
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
	 * Returns the gui component of the floor.
	 * @return the gui component of the floor
	 */
	public JFloor getFloor() {
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
		cbxFloors.setSelectedIndex( id );
	}

	/**
	 * Returns the the z format floor that is currently visible. If the evacuation floor
	 * is hidden, the first floor is returned with index 0.
	 * @return the z format floor that is currently visible
	 */
	public int getFloorID() {
		return cbxFloors.getSelectedIndex();
	}

	public ZControl getProjectControl() {
		return projectControl;
	}

	/**
	 * Returns the currently displayed project.
	 * @return the currently displayed project
	 */
//	public Project getZControl() {
//		return projectControl.getZControl();
//	}



	/**
	 * Updates the gui if a new project has been loaded. Loads new combo boxes,
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
		getLeftPanel().getMainComponent().displayFloor( currentFloor );
		roomSelector.displayRoomsForCurrentFloor();
	}

	public void setEditMode( EditMode em ) {
		if( getLeftPanel().getMainComponent() == null )
			return;
		getLeftPanel().getMainComponent().resetEdit();
		GUIOptionManager.setEditMode( em );
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
	public void changeAreaView( ArrayList<AreaVisibility> mode ) {
		// EnumSet.copyOf will not work for empty lists
		GUIOptionManager.setAreaVisibility( mode.isEmpty() ? EnumSet.noneOf( AreaVisibility.class ) : EnumSet.copyOf( mode ) );
		updateFloorView();
	}

	/*****************************************************************************
	 *                                                                           *
	 * Popup methods                                                             *
	 *                                                                           *
	 ****************************************************************************/
	/**
	 * Returns the up-to-date popup menu that is shown on all edges.
	 * @return the up-to-date popup menu that is shown on all edges
	 */
	public JPopupMenu getEdgePopup() {
		return pupEdge;
	}

	/**
	 * Returns the up-to-date popup menu that is shown on all edges end points.
	 * @return the up-to-date popup menu that is shown on all edges and end points
	 */
	public JPopupMenu getPointPopup() {
		return pupPoint;
	}

	/**
	 * Returns the up-to-date popup menu that is shown on all {@link JPolygon}s.
	 * @return the up-to-date popup menu that is shown on all polygons
	 */
	public JPopupMenu getPolygonPopup() {
		return pupPolygon;
	}

	/**
	 * This method is called internally to recreate an up-to-date JPopupMenu
	 * for the JEdge objects. It also recreates the EdgePopupListeners. 
	 *
	 * This whole method is superfluous until now, because the JEdges' PopupMenu
	 * does not include any dynamic elements till now. To keep up the consistency 
	 * with the JPolygon PopupMenu we nevertheless created this method.
	 */
	private void recreateEdgePopupMenu() {
		if( pupEdge == null ) {
			edgePopupListeners = new LinkedList<EdgePopupListener>();
			edgePopupListeners.add( new EdgePopupListener() );

			pupEdge = new JPopupMenu();
			loc.setPrefix( "gui.editor.JEditorPanel." );
			Menu.addMenuItem( pupEdge, loc.getString( "popupInsertNewPoint" ),
							edgePopupListeners.get( 0 ), "insertPoint" );
			Menu.addMenuItem( pupEdge, loc.getString( "popupCreatePassage" ),
							edgePopupListeners.get( 0 ), "makePassable" );
			Menu.addMenuItem( pupEdge, loc.getString( "popupCreatePassageRoom" ),
							edgePopupListeners.get( 0 ), "createPassageRoom" );
			Menu.addMenuItem( pupEdge, loc.getString( "popupCreateFloorPassage" ),
							edgePopupListeners.get( 0 ), "makeTeleport" );
			Menu.addMenuItem( pupEdge, loc.getString( "popupCreateEvacuationPassage" ),
							edgePopupListeners.get( 0 ), "makeEvacEdge" );
			Menu.addMenuItem( pupEdge, loc.getString( "popupShowPassageTarget" ),
							edgePopupListeners.get( 0 ), "showPassageTarget" );
			Menu.addMenuItem( pupEdge, loc.getString( "popupRevertPassage" ),
							edgePopupListeners.get( 0 ), "revertPassage" );
			loc.setPrefix( "" );
		}
	}

	/**
	 * This method is called internally to recreate an up-to-date JPopupMenu
	 * for the JEdge (Point) objects. It also recreates the JEdgePopupListeners. 
	 *
	 * This whole method is superfluous until now, because the JEdges' point PopupMenu
	 * does not include any dynamic elements till now. To kepp up the consistency 
	 * with the JPolygon PopupMenu we nevertheless created this method.
	 */
	private void recreatePointPopupMenu() {
		if( pupPoint == null ) {
			pointPopupListeners = new LinkedList<PointPopupListener>();
			pointPopupListeners.add( new PointPopupListener() );

			pupPoint = new JPopupMenu();

			Menu.addMenuItem( pupPoint, loc.getString(
							"gui.editor.JEditorPanel.popupDeletePoint" ),
							pointPopupListeners.get( 0 ), "deletePoint" );
		}
	}

	/**
	 * This method is called internally to recreate an up-to-date JPopupMenu
	 * for the JPolygon objects. It also recreates the PolygonPopupListeners.
	 */
	private void recreatePolygonPopupMenu( Assignment currentAssignment ) {
		polygonPopupListeners = new LinkedList<PolygonPopupListener>();

		pupPolygon = new JPopupMenu();
		JMenu jmnCreateAssArea = Menu.addMenu( pupPolygon,
						loc.getString( "gui.editor.JEditorPanel.popupDefaultAssignmentArea" ) );
		jmnCreateAssArea.setEnabled( currentAssignment != null );
		if( currentAssignment != null ) {
			PolygonPopupListener listener;
			for( AssignmentType t : currentAssignment.getAssignmentTypes() ) {
				listener = new PolygonPopupListener( t , guiControl );
				polygonPopupListeners.add( listener );
				Menu.addMenuItem( jmnCreateAssArea, t.getName(), listener );
			}
		}
	}

	/** This method should be called every time before the JEdge popup menu
	 * is shown.
	 * @param currentEdge The Edge that is displayed by the JEdge
	 * on which the PopupMenu shall be shown. 
	 * @param mousePosition the position at which the popup menu is shown with
	 * coordinates that must be relative to the whole Floor
	 */
	protected void setPopupEdge( Edge currentEdge, Point mousePosition ) {
		boolean passable = (currentEdge instanceof RoomEdge) && ((RoomEdge)currentEdge).isPassable();
		// passage-Creation
		((JMenuItem)pupEdge.getComponent( 1 )).setVisible( !passable );
		// passage-room creation
		((JMenuItem)pupEdge.getComponent( 2 )).setVisible( !passable );
		// Teleport-Creation
		((JMenuItem)pupEdge.getComponent( 3 )).setVisible( !passable );
		// EvacuationEdge-Creation
		((JMenuItem)pupEdge.getComponent( 4 )).setVisible( !passable );
		// Show Partner edge
		((JMenuItem)pupEdge.getComponent( 5 )).setVisible( currentEdge instanceof TeleportEdge );
		// revert passage
		((JMenuItem)pupEdge.getComponent( 6 )).setVisible( passable );

		for( EdgePopupListener p : edgePopupListeners )
			p.setEdge( currentEdge, mousePosition, PropertyContainer.getInstance().getAsBoolean(
							"editor.options.view.rasterizedPaintMode" ) );
	}

	/** This method should be called every time before the JEdge point popup menu
	 * is shown.
	 * @param currentEdge The Edge on which the PointPopupMenu 
	 * shall be shown. 
	 * @param currentPoint The PlanPoint on which the PointPopupMenu 
	 * shall be shown. */
	protected void setPopupPoint( Edge currentEdge, PlanPoint currentPoint ) {
		for( PointPopupListener p : pointPopupListeners )
			p.setPoint( currentEdge, currentPoint );
	}

	/** This method should be called every time before the JPolygon popup menu
	 * is shown.
	 * @param currentPolygon The PlanPolygon that is displayed by the JPolygon
	 * on which the PopupMenu shall be shown. */
	protected void setPopupPolygon( PlanPolygon currentPolygon ) {
		System.out.println( "Popup now belongs to " + currentPolygon.toString() );
		for( PolygonPopupListener p : polygonPopupListeners )
			p.setPolygon( currentPolygon );
	}

// TODO-Event werfe wirklich events
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
		JRuler topRuler = getLeftPanel().getTopRuler();
		JRuler leftRuler = getLeftPanel().getLeftRuler();
		Floor floor = getCurrentFloor();
		topRuler.setWidth( floor.getWidth() );
		leftRuler.setHeight( floor.getHeight() );
		topRuler.offset = zet.util.ConversionTools.roundScale3( floor.getxOffset() / 1000.0 - 0.8 );
		leftRuler.offset = zet.util.ConversionTools.roundScale3( floor.getyOffset() / 1000.0 - 0.8 );
		topRuler.repaint();
		leftRuler.repaint();
	}

	/*****************************************************************************
	 *                                                                           *
	 * Helper methods                                                            *
	 *                                                                           *
	 ****************************************************************************/
	/**
	 * This is a helper method for other GUI objects who need to transform 
	 * points that are given in their own coordinate space into the coordinate
	 * space of the Floor.
	 * @param source The Component in whose coordinate space the Point "toConvert"
	 * is specified. It must be an object which is located directly or indirectly
	 * upon the JEditorPanel's JFloor object.
	 * @param toConvert The point to convert
	 * @returns The same point as "toConvert", but relative to the surrounding
	 * JFloor object.
	 */
	Point convertPointToFloorCoordinates( Component source, Point toConvert ) {
		return SwingUtilities.convertPoint( source, toConvert, getFloor() );
	}
}
