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
 * Class JVisualizationView
 * Created 10.06.2008, 11:07:01
 */

package gui.visualization;

import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import ds.PropertyContainer;
import gui.GUIControl;
import gui.ZETMain;
import zet.gui.components.model.ComboBoxRenderer;
import zet.gui.components.model.FloorComboBoxModel;
import de.tu_berlin.math.coga.components.JArrayPanel;
import zet.gui.components.tabs.visualization.PotentialSelectionModel;
import gui.visualization.control.GLControl;
import gui.visualization.control.GLControl.CellInformationDisplay;
import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.util.Hashtable;
import javax.media.opengl.GLCapabilities;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JVisualizationView extends AbstractVisualizationView<ZETVisualization> {
	/** The visualization panel. */
	private ZETVisualization visualization;
	/** A combo box that allows selecting the visible floor (if not all are visible) */
	private JComboBox floorSelector;
	/** The model for the floor selection combo box */
	private FloorComboBoxModel floorSelectorModel;
	/** A combo box selecting the currently visible potential */
	private JComboBox potentialSelector;
	/** A combo box for selecting the information displayed on the head of the individuals. */
	private JComboBox headColorSelector;
	/** The label for the floor combo box. */
	private JLabel lblFloorSelector;
	/** The label for the potential combo box. */
	private JLabel lblPotentialSelector;
	/** The label for the combo box for the information on the heads. */
	private JLabel lblHeadSelector;
	/** The currently selected floor */
	private int selectedFloor = 0;
	/** A text field containing the position of the camera. */
	private JTextField txtCameraPosition;
	/** A text field containing the view vector of the camera. */
	private JTextField txtCameraView;
	/** A text field containing the up vector of the camera. */
	private JTextField txtCameraUp;
	/** The label for the camera position. */
	private JLabel lblCameraPosition;
	/** The label for the view vector of the camera. */
	private JLabel lblCameraView;
	/** The label for the up vector of the camera. */
	private JLabel lblCameraUp;
	/** A button that sets a new camera position. */
	private JButton setCameraPosition;
	// TODO better, directly take the enum
	private final int HEAD_INFORMATION_NOTHING = 0;
	private final int HEAD_INFORMATION_PANIC = 1;
	private final int HEAD_INFORMATION_SPEED = 2;
	private final int HEAD_INFORMATION_EXHAUSTION = 3;
	private final int HEAD_INFORMATION_ALARMED = 4;
	private final int HEAD_INFORMATION_CHOSEN_EXIT = 5;
	private final int HEAD_INFORMATION_REACTION = 6;
	DefaultLoc loc = DefaultLoc.getSingleton();
	
	JArrayPanel cameraPanel;
	private final GUIControl guiControl;
	public JVisualizationView( GLCapabilities caps, GUIControl guiControl ) {
		super( new VisualizationPanel<ZETVisualization>( new ZETVisualization( caps, guiControl ) ) );
		visualization = getGLContainer();
		this.guiControl = guiControl;

		final JSlider slider = new JSlider();
		slider.setMinimum( -90 );
		slider.setMaximum( 90 );
		slider.setValue( 0 );
		slider.setMinorTickSpacing( 1 );
		slider.setMajorTickSpacing( 10 );
		slider.setPaintTicks( true );
		slider.setPaintLabels( true );
		slider.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged( ChangeEvent e ) {
				GLControl control = visualization.getControl();
				if( control == null )
					return;
				if( slider.getValue() == 0 )
					control.setSpeedFactor( 1 );
				else if( slider.getValue() < 0 )
					control.setSpeedFactor( (10 - (-slider.getValue() * 0.1)) * 0.1 );
				else
					control.setSpeedFactor( (slider.getValue() + 10) * 0.1 );
			}
		} );
		@SuppressWarnings( "UseOfObsoleteCollectionType" ) // hashtable has to be used here due to slider
		Hashtable<Integer, JComponent> table = new Hashtable<Integer, JComponent>();
		for( int i = 1; i <= 10; i++ )
			table.put( new Integer( -i * 10 ), new JLabel( DefaultLoc.getSingleton().getFloatConverter().format( (10 - i) * 0.1 ) ) );
		table.put( new Integer( 0 ), new JLabel( "1" ) );
		for( int i = 1; i < 10; i++ )
			table.put( new Integer( i * 10 ), new JLabel( "" + (i + 1) ) );
		slider.setLabelTable( table );
		this.getLeftPanel().add( slider, BorderLayout.SOUTH );
		setFloorSelectorEnabled( !PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.floors" ) );
	}

	@Override
	protected JPanel createEastBar() {
		double size[][] = // Columns
						{
			{10, TableLayout.FILL, 10},
			//Rows
			{10,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 16,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 16,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 16,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 16,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 16,
				TableLayout.FILL
			}
		};
		final JPanel eastPanel = new JPanel( new TableLayout( size ) );

		floorSelector = new JComboBox();
		floorSelectorModel = new FloorComboBoxModel();

		floorSelector.setModel( floorSelectorModel );
		floorSelector.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				if( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.floors" ) )
					return;
				if( floorSelector.getSelectedIndex() >= 0 ) {
					System.out.println( "Ausgewählter Floor:" + floorSelector.getSelectedItem() );
					selectedFloor = floorSelectorModel.getFloorIDFromIndex( floorSelector.getSelectedIndex() );
				} else
					return;
				if( visualization.getControl() != null ) {
					visualization.getControl().showFloor( selectedFloor );
					getLeftPanel().getGLContainer().repaint();
				}
			}
		} );
		floorSelector.setRenderer( new ComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent( JList list, Object value,
							int index, boolean isSelected, boolean cellHasFocus ) {
				JLabel me = (JLabel)super.getListCellRendererComponent( list, value, index,
								isSelected, cellHasFocus );

				if( value != null )
					setText( (String)value );
				return this;
			}
		} );
		int row = 1;

		if( loc == null )
			loc = DefaultLoc.getSingleton();


		lblFloorSelector = new JLabel( loc.getString( "gui.editor.JEditorPanel.labelFloors" ) + ":" );
		eastPanel.add( lblFloorSelector, "1, " + row++ );
		eastPanel.add( floorSelector, "1, " + row++ );
		row++;

		potentialSelector = new JComboBox();
		potentialSelector.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged( ItemEvent e ) {
				if( e.getItem() == null || e.getStateChange() == ItemEvent.DESELECTED )
					return;
				PotentialSelectionModel.PotentialEntry potentialEntry = (PotentialSelectionModel.PotentialEntry)e.getItem();
				guiControl.visualizationShowCellInformation( CellInformationDisplay.NoPotential );
				visualization.getControl().activatePotential( potentialEntry.getPotential() );
				visualization.getControl().showPotential( CellInformationDisplay.StaticPotential );
				getGLContainer().repaint();
			}
		} );
		lblPotentialSelector = new JLabel( loc.getString( "gui.visualizationView.labelPotentials" ) + ":" );
		eastPanel.add( lblPotentialSelector, "1, " + row++ );
		eastPanel.add( potentialSelector, "1, " + row++ );
		row++;

		headColorSelector = new JComboBox();
		headColorSelector.addItem( loc.getString( "gui.visualizationView.headsNothing" ) );
		headColorSelector.addItem( loc.getString( "gui.visualizationView.headsPanic" ) );
		headColorSelector.addItem( loc.getString( "gui.visualizationView.headsSpeed" ) );
		headColorSelector.addItem( loc.getString( "gui.visualizationView.headsExhaustion" ) );
		headColorSelector.addItem( loc.getString( "gui.visualizationView.headsAlarmed" ) );
		headColorSelector.addItem( loc.getString( "gui.visualizationView.headsChosenExit" ) );
		headColorSelector.addItem( loc.getString( "gui.visualizationView.headsReactionTime" ) );
		headColorSelector.setSelectedIndex( 1 );
		headColorSelector.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				switch( headColorSelector.getSelectedIndex() ) {
					default:
					case HEAD_INFORMATION_NOTHING:
						visualization.getControl().showIndividualInformation( GLControl.IndividualInformationDisplay.NOTHING );
						break;
					case HEAD_INFORMATION_PANIC:
						visualization.getControl().showIndividualInformation( GLControl.IndividualInformationDisplay.PANIC );
						break;
					case HEAD_INFORMATION_SPEED:
						visualization.getControl().showIndividualInformation( GLControl.IndividualInformationDisplay.SPEED );
						break;
					case HEAD_INFORMATION_EXHAUSTION:
						visualization.getControl().showIndividualInformation( GLControl.IndividualInformationDisplay.EXHAUSTION );
						break;
					case HEAD_INFORMATION_ALARMED:
						visualization.getControl().showIndividualInformation( GLControl.IndividualInformationDisplay.ALARMED );
						break;
					case HEAD_INFORMATION_CHOSEN_EXIT:
						visualization.getControl().showIndividualInformation( GLControl.IndividualInformationDisplay.CHOSEN_EXIT );
						break;
					case HEAD_INFORMATION_REACTION:
						visualization.getControl().showIndividualInformation( GLControl.IndividualInformationDisplay.REACTION_TIME );
				}
				getLeftPanel().getGLContainer().repaint();
			}
		} );
		lblHeadSelector = new JLabel( loc.getString( "gui.visualizationView.labelHeads" ) + ":" );
		eastPanel.add( lblHeadSelector, "1, " + row++ );
		eastPanel.add( headColorSelector, "1, " + row++ );
		row++;

		txtCameraPosition = new JTextField( "(0;0;0)" );
		txtCameraView = new JTextField( "(0;0;0)" );
		txtCameraUp = new JTextField( "(0;0;0)" );
		lblCameraPosition = new JLabel( loc.getStringWithoutPrefix( "gui.visualizationView.cameraPosition" ) );
		lblCameraView = new JLabel( loc.getStringWithoutPrefix( "gui.visualizationView.cameraView" ) );
		lblCameraUp = new JLabel( loc.getStringWithoutPrefix( "gui.visualizationView.cameraUp" ) );
		cameraPanel = new JArrayPanel(1,7);
		cameraPanel.set( lblCameraPosition, 0, 0 );
		cameraPanel.set( lblCameraView, 0, 2 );
		cameraPanel.set( lblCameraUp, 0, 4 );
		cameraPanel.set( txtCameraPosition, 0, 1 );
		cameraPanel.set( txtCameraView, 0, 3 );
		cameraPanel.set( txtCameraUp, 0, 5 );

		setCameraPosition = new JButton( loc.getStringWithoutPrefix( "gui.visualizationView.cameraSetPosition" ) );
		eastPanel.add( setCameraPosition, "1, " + row++ );
		setCameraPosition.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				try {
					visualization.getCamera().getPos().parse( txtCameraPosition.getText() );
					visualization.getCamera().getView().parse( txtCameraView.getText() );
					visualization.getCamera().getUp().parse( txtCameraUp.getText() );
					visualization.repaint();
				} catch( ParseException ex ) {
					ZETMain.sendError( loc.getStringWithoutPrefix( "gui.error.CameraParseError" ) );
				}
			}
		});
		cameraPanel.set( setCameraPosition, 0, 6 );
		cameraPanel.rebuild();
		eastPanel.add( cameraPanel, "1, " + row++ );
		return eastPanel;
	}

	/**
	 * Localizes the components on the east bar of the visualization view.
	 */
	@Override
	public void localize() {
		lblFloorSelector.setText( loc.getString( "gui.editor.JEditorPanel.labelFloors" ) + ":" );
		lblPotentialSelector.setText( loc.getString( "gui.visualizationView.labelPotentials" ) + ":" );
		lblHeadSelector.setText( loc.getString( "gui.visualizationView.labelHeads" ) + ":" );
		lblCameraPosition.setText( loc.getStringWithoutPrefix( "gui.visualizationView.cameraPosition" ) );
		lblCameraView.setText( loc.getStringWithoutPrefix( "gui.visualizationView.cameraView" ) );
		lblCameraUp.setText( loc.getStringWithoutPrefix( "gui.visualizationView.cameraUp" ) );
		setCameraPosition.setText( loc.getStringWithoutPrefix( "gui.visualizationView.cameraSetPosition" ) );
		int index = headColorSelector.getSelectedIndex();
		headColorSelector.removeAllItems();
		headColorSelector.addItem( loc.getString( "gui.visualizationView.headsNothing" ) );
		headColorSelector.addItem( loc.getString( "gui.visualizationView.headsPanic" ) );
		headColorSelector.addItem( loc.getString( "gui.visualizationView.headsSpeed" ) );
		headColorSelector.addItem( loc.getString( "gui.visualizationView.headsExhaustion" ) );
		headColorSelector.addItem( loc.getString( "gui.visualizationView.headsAlarmed" ) );
		headColorSelector.addItem( loc.getString( "gui.visualizationView.headsChosenExit" ) );
		headColorSelector.setSelectedIndex( index );
	}

	/**
	 * Updates the camera position information in the text fields.
	 */
	public void updateCameraInformation() {
		this.txtCameraPosition.setText( visualization.getCamera().getPos().toString() );
		this.txtCameraView.setText( visualization.getCamera().getView().toString() );
		this.txtCameraUp.setText( visualization.getCamera().getUp().toString() );
	}

	/**
	 * Enables and disables the floor selector on the right part of the view.
	 * @param val decides whether the floor selector is enabled or disabled
	 */
	public void setFloorSelectorEnabled( boolean val ) {
		floorSelector.setEnabled( val );
	}

	/**
	 * Updates the floor selection combo box on the right panel.
	 */
	public void updateFloorSelector() {
		floorSelectorModel.displayFloors( visualization.getControl().getFloorNames() );
		floorSelector.setModel( floorSelectorModel );
		if( floorSelector.getItemCount() > 0 )
			floorSelector.setSelectedIndex( 0 );
		selectedFloor = floorSelectorModel.getFloorIDFromIndex( floorSelector.getSelectedIndex() );
	}

	/**
	 * Updates the potential selection combo box on the right panel.
	 */
	public void updatePotentialSelector() {
		potentialSelector.setModel( new PotentialSelectionModel( visualization.getControl().getPotentialManager() ) );
	}

	/**
	 * Unselects all elements of the potential selection box.
	 */
	public void unselectPotentialSelector() {
		potentialSelector.setSelectedIndex( -1 );
	}

	/**
	 * Adds another <code>ItemListener</code> to the potential selection box. This
	 * can be used to access external GUI elements.
	 * @param listener
	 */
	public void addPotentialItemListener( ItemListener listener ) {
		potentialSelector.addItemListener( listener );
	}

	/**
	 * Returns the currently selected floor.
	 * @return the currently selected floor.
	 */
	public int getSelectedFloorID() {
		return selectedFloor;
	}
}
