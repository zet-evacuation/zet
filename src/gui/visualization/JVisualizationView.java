/**
 * Class JVisualizationView
 * Erstellt 10.06.2008, 11:07:01
 */
package gui.visualization;

import gui.components.ComboBoxRenderer;
import gui.components.FloorComboBoxModel;
import gui.components.PotentialSelectionModel;
import gui.visualization.control.GLControl;
import gui.visualization.control.GLControl.CellInformationDisplay;
import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;
import javax.media.opengl.GLCapabilities;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import localization.Localization;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JVisualizationView extends AbstractVisualizationView<Visualization> {
	/** The visualization panel. */
	private Visualization visualization;
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

	private final int HEAD_INFORMATION_NOTHING = 0;
	private final int HEAD_INFORMATION_PANIC = 1;
	private final int HEAD_INFORMATION_SPEED = 2;
	private final int HEAD_INFORMATION_EXHAUSTION = 3;
	private final int HEAD_INFORMATION_ALARMED = 4;
	private final int HEAD_INFORMATION_CHOSEN_EXIT = 5;
	
	public JVisualizationView( GLCapabilities caps ) {
		super( new VisualizationPanel<Visualization>( new Visualization( caps ) ) );
		visualization = getGLContainer();
		
		final JSlider slider = new JSlider();
		slider.setMinimum( -90 );
		slider.setMaximum( 90 );
		slider.setValue( 0 );
		slider.setMinorTickSpacing( 1 );
		slider.setMajorTickSpacing(10 );
		slider.setPaintTicks( true );
		slider.setPaintLabels( true );
		slider.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				GLControl control = visualization.getControl();
				if( control == null )
					return;
				if( slider.getValue() == 0 )
					control.setSpeedFactor( 1 );
				else if( slider.getValue() < 0 ) {
					control.setSpeedFactor( (10-(-slider.getValue()*0.1))*0.1 );
				} else {
					control.setSpeedFactor( (slider.getValue()+10)*0.1 );
				}
			}
		});
		Hashtable<Integer, JComponent> table = new Hashtable<Integer,JComponent>();
		for( int i = 1; i <= 10; i++ )
			table.put( new Integer( -i*10 ), new JLabel( Localization.getInstance().getFloatConverter().format( (10-i)*0.1 ) ) );
		table.put( new Integer( 0 ), new JLabel( "1") );
		for( int i = 1; i < 10  ; i++ )
			table.put( new Integer( i*10 ), new JLabel( "" + (i+1) ) );
		slider.setLabelTable( table );
		this.getLeftPanel().add( slider, BorderLayout.SOUTH );
	}

	public void enableFloorSelector( boolean selected ) {
		floorSelector.setEnabled( selected );
	}

	 @Override
	protected JPanel getEastBar() {
		double size[][] = // Columns
						{
			{ 10, TableLayout.FILL, 10 },
			//Rows
			{ 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.FILL
			}
		};
		JPanel eastPanel = new JPanel( new TableLayout( size ) );

		floorSelector = new JComboBox();
		floorSelectorModel = new FloorComboBoxModel();

		floorSelector.setModel( floorSelectorModel );
		floorSelector.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				if( floorSelector.getSelectedIndex() >= 0 ) {
					System.out.println( "Ausgewählter Floor:" + floorSelector.getSelectedItem() );
					selectedFloor = floorSelectorModel.getFloorIDFromIndex( floorSelector.getSelectedIndex() );
				} else {
					return;
				}
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
				JLabel me = (JLabel) super.getListCellRendererComponent( list, value, index,
								isSelected, cellHasFocus );

				if( value != null ) {
					setText( (String)value );
				}
				return this;
			}
		} );
		int row = 1;

		lblFloorSelector = new JLabel (loc.getString ("gui.editor.JEditorPanel.labelFloors") + ":");
		eastPanel.add (lblFloorSelector, "1, " + row++);
		eastPanel.add (floorSelector, "1, " + row++);
		row++;

		
		potentialSelector = new JComboBox();
		potentialSelector.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e ) {
				if( e.getItem() == null || e.getStateChange() == ItemEvent.DESELECTED )
					return;
				PotentialSelectionModel.PotentialEntry potentialEntry = (PotentialSelectionModel.PotentialEntry)e.getItem();
				//btnShowPotential.setSelected( false );
				//btnShowDynamicPotential.setSelected( false );
				visualization.getControl().activatePotential( potentialEntry.getPotential() );
				visualization.getControl().showPotential( CellInformationDisplay.STATIC_POTENTIAL );
				getGLContainer().repaint();
			}
		});
		lblPotentialSelector = new JLabel (loc.getString ("gui.visualization.labelPotentials") + ":");
		eastPanel.add (lblPotentialSelector, "1, " + row++);
		eastPanel.add (potentialSelector, "1, " + row++);
		row++;		
		
		headColorSelector = new JComboBox();
		headColorSelector.addItem( loc.getString( "gui.visualization.headsNothing" ) );
		headColorSelector.addItem( loc.getString( "gui.visualization.headsPanic" ) );
		headColorSelector.addItem( loc.getString( "gui.visualization.headsSpeed" ) );
		headColorSelector.addItem( loc.getString( "gui.visualization.headsExhaustion" ) );
		headColorSelector.addItem( loc.getString( "gui.visualization.headsAlarmed" ) );
		headColorSelector.addItem( loc.getString( "gui.visualization.headsChosenExit" ));
		headColorSelector.setSelectedIndex( 1 );
		headColorSelector.addActionListener( new ActionListener() {
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
						visualization.getControl().showIndividualInformation( GLControl.IndividualInformationDisplay.CHOSEN_EXIT);
						break;
				}
				getLeftPanel().getGLContainer().repaint();
			}
		} );
		lblHeadSelector = new JLabel (loc.getString ("gui.visualization.labelHeads") + ":");
		eastPanel.add (lblHeadSelector, "1, " + row++);
		eastPanel.add (headColorSelector, "1, " + row++);
		row++;		
		
		return eastPanel;
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
	 * can be used to access external gui elements.
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
