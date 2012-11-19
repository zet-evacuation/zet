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
import ds.PropertyContainer;
import ds.z.AssignmentArea;
import ds.z.DelayArea;
import ds.z.EvacuationArea;
import ds.z.Floor;
import ds.z.PlanPolygon;
import ds.z.Room;
import ds.z.StairArea;
import ds.z.StairPreset;
import ds.z.TeleportArea;
import ds.z.ZControl;
import ds.z.ZLocalization;
import ds.z.ZModelRoomEvent;
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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import zet.gui.GUILocalization;
import zet.gui.components.model.ComboBoxRenderer;
import zet.gui.components.model.FloorComboBox;
import zet.gui.components.model.RoomComboBoxModel;
import zet.gui.main.JZetWindow;
import zet.gui.main.tabs.base.AbstractSplitPropertyWindow;
import zet.gui.main.tabs.base.JFloorScrollPane;
import zet.gui.main.tabs.base.JPolygon;
import zet.gui.main.tabs.editor.EditStatus;
import zet.gui.main.tabs.editor.JAssignmentAreaInformationPanel;
import zet.gui.main.tabs.editor.JDefaultInformationPanel;
import zet.gui.main.tabs.editor.JDelayAreaInformationPanel;
import zet.gui.main.tabs.editor.JEvacuationAreaInformationPanel;
import zet.gui.main.tabs.editor.JFloor;
import zet.gui.main.tabs.editor.JFloorInformationPanel;
import zet.gui.main.tabs.editor.JInformationPanel;
import zet.gui.main.tabs.editor.JRoomInformationPanel;
import zet.gui.main.tabs.editor.SelectedElements;

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
public class JEditView extends AbstractSplitPropertyWindow<JFloorScrollPane<JFloor>> implements Observer {
	private JLabel lblEdgeType;
	private JLabel lblEdgeLength;
	private JLabel lblEdgeExitName;
	private JTextField txtEdgeExitName;
	private final EditStatus editStatus;
	private final SelectedElements selection;

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
					if( p instanceof Room )
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
		StairArea( new JDefaultInformationPanel() ),
		/** Message code indicating that the stair area panel should be displayed. */
		TeleportArea( new JDefaultInformationPanel() ),
		/** A single edge is select. No area or polygon. */
		Edge( new JDefaultInformationPanel() );

		private Panels( JInformationPanel panel ) {
			this.panel = Objects.requireNonNull( panel );
		}
		
		final private JInformationPanel panel;

		public JInformationPanel getPanel() {
			return panel;
		}
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
	private JTextField txtStairFactorUp;
	private JTextField txtStairFactorDown;
	
	private JLabel lblStairFactorUp;
	private JLabel lblStairFactorDown;
	/** A label for the stair speed preset. */
	private JLabel lblStairPreset;
	/** A selection box for the stair speed presets. */
	private JComboBox<StairPreset> cbxStairPresets;
	/** A label describing the current preset. */
	private JLabel lblStairPresetDescription;



	private boolean disableUpdate = false;
	private final GUIControl guiControl;
	//private PlanPolygon<?> selectedPolygon;


	/** Describes the teleportation area name field. */
	private JLabel lblTeleportAreaName;
	/** The name filed for a teleportation area. */
	private JTextField txtTeleportAreaName;

	/** Describes the target area combo box.  */
	private JLabel lblTargetArea;
	/** A combo box selecting the target area of a target area. */
	private JComboBox<TeleportArea> cbxTargetArea;

	/** A combo box selecting the possible exits for a teleportation area. */
	private JComboBox<EvacuationArea> cbxTargetExit;
	/** Describes the target exit combo box. */
	private JLabel lblTargetExit;

	final NumberFormat nfFloat = DefaultLoc.getSingleton().getFloatConverter();
	final NumberFormat nfInteger = DefaultLoc.getSingleton().getIntegerConverter();

	public JEditView( EditStatus editStatus, GUIControl guiControl, SelectedElements selection ) {
		super( new JFloorScrollPane<>( new JFloor( editStatus, guiControl ) ) );
		this.selection = selection;
		//((JRoomInformationPanel)Panels.Room.getPanel()).setSelection( selection );
		EventServer.getInstance().registerListener( getFloor(), ZModelRoomEvent.class );
		loc = GUILocalization.getSingleton();
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
		//selectedPolygon = (panel == Panels.Default || panel == Panels.Floor || panel == Panels.Edge ) ? null : getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
		//panel.getPanel().update();
		switch( panel ) {
			case DelayArea:
				Panels.DelayArea.getPanel().update( selection.getSelected().getPlanPolygon() );
				eastSubBarCardLayout.show( eastSubBar, "delayArea" );
				break;
			case StairArea:
				txtStairFactorUp.setText( nfFloat.format( ((StairArea)selection.getSelected().getPlanPolygon()).getSpeedFactorUp() ) );
				txtStairFactorDown.setText( nfFloat.format( ((StairArea)selection.getSelected().getPlanPolygon()).getSpeedFactorDown() ) );
				eastSubBarCardLayout.show( eastSubBar, "stairArea" );
				break;
			case AssignmentArea:
				Panels.AssignmentArea.getPanel().update( ((AssignmentArea)selection.getSelected().getPlanPolygon()) );
				eastSubBarCardLayout.show( eastSubBar, "assignmentArea" );
				break;
			case Room:
				Panels.Room.getPanel().update( ((Room)selection.getSelected().getPlanPolygon()) );
				eastSubBarCardLayout.show( eastSubBar, "room" );
				break;
			case Default:
				eastSubBarCardLayout.show( eastSubBar, "default" );
				break;
			case Floor:
				Panels.Floor.getPanel().update( currentFloor );
				
				eastSubBarCardLayout.show( eastSubBar, "floor" );
				break;
			case EvacuationArea:
				Panels.EvacuationArea.getPanel().update( ((EvacuationArea)selection.getSelected().getPlanPolygon()) );
				eastSubBarCardLayout.show( eastSubBar, "evacuation" );
				break;
			case TeleportArea:
				txtTeleportAreaName.setText( ((TeleportArea)selection.getSelected().getPlanPolygon()).getName() );
				if( ((TeleportArea)selection.getSelected().getPlanPolygon()).getExitArea() == null )
					cbxTargetExit.setSelectedIndex( -1 );
				else
					cbxTargetExit.setSelectedItem( ((TeleportArea)selection.getSelected().getPlanPolygon()).getExitArea() );

				if( ((TeleportArea)selection.getSelected().getPlanPolygon()).getTargetArea() == null )
					cbxTargetArea.setSelectedIndex( -1 );
				else
					cbxTargetArea.setSelectedItem( ((TeleportArea)selection.getSelected().getPlanPolygon()).getTargetArea() );
				eastSubBarCardLayout.show( eastSubBar, "teleport" );
				break;
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
			default:
				guiControl.showErrorMessage( loc.getString( "gui.NotGraveError" ), loc.getString( "gui.editor.JEditorPanel.WrongPanelID" ) );
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

		floorSelector = new FloorComboBox<>();
		floorSelector.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				if( floorSelector.getSelectedItem() == null )
					return;

				final int add = PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) ? 1 : 0;
				lblFloorNumber.setText( String.format( loc.getStringWithoutPrefix( "gui.EditPanel.Default.OnFloor" ), floorSelector.getSelectedIndex() + add ) );
				//btnFloorDown.setEnabled( !(floorSelector.getSelectedIndex() == 0 || floorSelector.getSelectedIndex() == 1 && add == 0) );

				// TODO call the method somehow with the control class...
				//JEditor.getInstance().enableMenuFloorDown( !(cbxFloors.getSelectedIndex() == 0 || cbxFloors.getSelectedIndex() == 1 && add == 0) );
				//btnFloorUp.setEnabled( !(floorSelector.getSelectedIndex() == floorSelector.getItemCount() - 1 || floorSelector.getSelectedIndex() == 0 && add == 0) );
				//JEditor.getInstance().enableMenuFloorUp( !(cbxFloors.getSelectedIndex() == cbxFloors.getItemCount() - 1 || cbxFloors.getSelectedIndex() == 0 && add == 0) );
				Floor dspFloor = (Floor)floorSelector.getSelectedItem();
				currentFloor = dspFloor;
				Panels.Floor.getPanel().setControl( projectControl, guiControl );
				Panels.Floor.getPanel().update( currentFloor );
				//((JFloorInformationPanel)Panels.Floor.getPanel()).setCurrentFloor( currentFloor );

				updateFloorView();
				getLeftPanel().getTopRuler().setWidth( dspFloor.getWidth() );
				getLeftPanel().getLeftRuler().setHeight( dspFloor.getHeight() );
				getLeftPanel().getTopRuler().offset = zet.util.ConversionTools.roundScale3( dspFloor.getxOffset() / 1000.0 - 0.8 );
				getLeftPanel().getLeftRuler().offset = zet.util.ConversionTools.roundScale3( dspFloor.getyOffset() / 1000.0 - 0.8 );

				// FloorName
				//txtFloorName.setText( dspFloor.getName() );
				
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
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent( JList<? extends Room> list, Room value, int index, boolean isSelected, boolean cellHasFocus ) {
				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );	// Needed for correct displaying! Forget return
				if( value != null )
					setText( value.getName() );
				return this;
			}
			/** Prohibits serialization. */
			private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
				throw new UnsupportedOperationException( "Serialization not supported" );
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
		//JInformationPanel card;
		JPanel card;

		card = Panels.Floor.getPanel();
		eastSubBar.add( card, "floor" );

		card = Panels.DelayArea.getPanel();
		eastSubBar.add( card, "delayArea" );

		card = getEastStairAreaPanel();
		eastSubBar.add( card, "stairArea" );

		card = Panels.AssignmentArea.getPanel();
		eastSubBar.add( card, "assignmentArea" );

		card = Panels.Room.getPanel();
		eastSubBar.add( card, "room" );

		card = Panels.Default.getPanel();
		eastSubBar.add( card, "default" );

		card = Panels.EvacuationArea.getPanel();
		eastSubBar.add( card, "evacuation" );

		card = getEastTeleportAreaPanel();
		eastSubBar.add( card, "teleport" );

		card = getEastEdgePanel();
		eastSubBar.add( card, "edge" );

		eastPanel.add( eastSubBar, "1, " + row++ );

		loc.clearPrefix();
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
		cbxStairPresets = new JComboBox<>();
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
		cbxTargetArea = new JComboBox<>();
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

		cbxTargetArea.setRenderer( new ComboBoxRenderer<TeleportArea>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent( JList<? extends TeleportArea> list, TeleportArea value, int index, boolean isSelected, boolean cellHasFocus ) {

				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				setText( value != null ? value.getName() : "" );
				return this;
			}
			/** Prohibits serialization. */
			private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
				throw new UnsupportedOperationException( "Serialization not supported" );
			}
		} );
		eastPanel.add( cbxTargetArea, "0, " + row++ );
		row++;

		// Target-Exit-Selector
		lblTargetExit = new JLabel( loc.getString( "Teleportation.TargetExit" ) );
		eastPanel.add( lblTargetExit, "0, " + row++ );
		cbxTargetExit = new JComboBox<>();
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
		cbxTargetExit.setRenderer( new ComboBoxRenderer<EvacuationArea>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent( JList<? extends EvacuationArea> list, EvacuationArea value, int index, boolean isSelected, boolean cellHasFocus ) {
				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				setText( value != null ? value.getName() : "" );
				return this;
			}
			/** Prohibits serialization. */
			private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
				throw new UnsupportedOperationException( "Serialization not supported" );
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



		// Room properties

		// Assignment area
	

		// Delay area

		// Save area

		// Evacuation area

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

	/**
	 * Sets the z {@link ds.z.Project} that is displayed in the edit view.
	 * @param projectControl
	 */
	final public void displayProject( ZControl projectControl ) {
		if( projectControl != null ) {

			// Clearing is done in the set-methods called later ?
			//floorSelector.clear();
			roomSelector.clear();
			//assignmentTypeSelector.clear();
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
		
		for( Panels p : Panels.values() )
			p.getPanel().setControl( projectControl, guiControl );

		//This is independent of the rest of the displaying work
		//floorSelector.displayFloors( projectControl.getProject() );
		updateFloorList();
		//assignmentTypeSelector.setControl( projectControl );
		//assignmentTypeSelector.displayAssignmentTypesForCurrentProject();
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
		//cbxPreferredExit.removeAllItems();
		cbxTargetExit.removeAllItems();
		cbxTargetArea.removeAllItems();
		for( Floor f : projectControl.getProject().getBuildingPlan().getFloors() )
			for( Room r : f.getRooms() ) {
				for( EvacuationArea e : r.getEvacuationAreas() ) {
		//			cbxPreferredExit.addItem( e );
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
