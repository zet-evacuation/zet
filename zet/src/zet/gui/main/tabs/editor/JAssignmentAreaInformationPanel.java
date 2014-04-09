/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zet.gui.main.tabs.editor;

import de.tu_berlin.math.coga.components.framework.Button;
import de.tu_berlin.coga.zet.model.AssignmentArea;
import de.tu_berlin.coga.zet.model.AssignmentType;
import de.tu_berlin.coga.zet.model.EvacuationArea;
import info.clearthought.layout.TableLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import zet.gui.GUILocalization;
import zet.gui.components.model.AssignmentTypeComboBoxModel;
import zet.gui.components.model.ComboBoxRenderer;
import zet.gui.main.JZetWindow;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JAssignmentAreaInformationPanel extends JInformationPanel<AssignmentArea> {

	/** Model for a assignmentType-selector combo box. */
	private zet.gui.components.model.AssignmentTypeComboBoxModel assignmentTypeSelector;
	private JLabel lblAssignmentType;
	private JLabel lblAssignmentEvacueeNumber;
	private JButton btnAssignmentSetDefaultEvacuees;
	private JLabel lblAreaSizeDesc;
	private JLabel lblMaxPersonsDesc;
	private JLabel lblMaxPersons;
	private JLabel lblMaxPersonsWarning;
	private JLabel lblPreferredExit;
	private JTextField txtNumberOfPersons;
	private JComboBox<EvacuationArea> cbxPreferredExit;
	private JLabel lblAreaSize;

	public JAssignmentAreaInformationPanel() {
		super( new double[]{TableLayout.FILL},
						new double[]{ //Rows
							TableLayout.PREFERRED, TableLayout.PREFERRED, 20, // Assignment type
							TableLayout.PREFERRED, TableLayout.PREFERRED, 20, // Number of evacuees
							TableLayout.PREFERRED, 20, // Button for default number of evacuees
							TableLayout.PREFERRED, TableLayout.PREFERRED, 20, // Preferred exit
							TableLayout.PREFERRED, TableLayout.PREFERRED, 10, // Area
							TableLayout.PREFERRED, TableLayout.PREFERRED, 20, // Max number of persons for area
							TableLayout.PREFERRED, // Warning
							TableLayout.FILL // Fill the rest of space
						} );
		init();
	}

	private void init() {
		int row = 0;

		// AssignmentType-Selector
		lblAssignmentType = new JLabel( loc.getString( "Assignment.Type" ) );
		this.add( lblAssignmentType, "0, " + row++ );
		assignmentTypeSelector = new AssignmentTypeComboBoxModel( projectControl );
		//assignmentTypeSelector.setFloorPanel( this.getLeftPanel().getMainComponent() );
		JComboBox<AssignmentType> cbxAssignmentType = new JComboBox<>( assignmentTypeSelector );
		cbxAssignmentType.setRenderer( new ComboBoxRenderer<AssignmentType>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent( JList<? extends AssignmentType> list, AssignmentType value,
							int index, boolean isSelected, boolean cellHasFocus ) {
				JLabel me = (JLabel)super.getListCellRendererComponent( list, value, index,
								isSelected, cellHasFocus );

				if( value != null )
					setText( value.getName() );
				return this;
			}
			/** Prohibits serialization. */
			private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
				throw new UnsupportedOperationException( "Serialization not supported" );
			}
		} );
		this.add( cbxAssignmentType, "0, " + row++ );
		row++;

		// Number of Evacuees
		lblAssignmentEvacueeNumber = new JLabel( loc.getString( "Assignment.Persons" ) );
		this.add( lblAssignmentEvacueeNumber, "0, " + row++ );
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
		this.add( txtNumberOfPersons, "0, " + row++ );
		row++;
		btnAssignmentSetDefaultEvacuees = Button.newButton( loc.getString( "Assignment.SetDefaultEvacuees" ), loc.getString( "Assignment.SetDefaultEvacuees.ToolTip" ) );
		btnAssignmentSetDefaultEvacuees.setToolTipText( loc.getString( "Assignment.SetDefaultEvacuees.ToolTip" ) );
		btnAssignmentSetDefaultEvacuees.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				//AssignmentArea a = (AssignmentArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
				//int persons = Math.min( a.getAssignmentType().getDefaultEvacuees(), a.getMaxEvacuees() );
				//a.setEvacuees( persons );
				//txtNumberOfPersons.setText( nfInteger.format( a.getEvacuees() ) );
			}
		} );
		this.add( btnAssignmentSetDefaultEvacuees, "0, " + row++ );
		row++;

		// Preferred-Exit-Selector
		lblPreferredExit = new JLabel( loc.getString( "Assignment.PreferredExit" ) );
		this.add( lblPreferredExit, "0, " + row++ );
		cbxPreferredExit = new JComboBox<>();
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
		cbxPreferredExit.setRenderer( new ComboBoxRenderer<EvacuationArea>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent( JList<? extends EvacuationArea> list, EvacuationArea value, int index, boolean isSelected, boolean cellHasFocus ) {
				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
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
		this.add( cbxPreferredExit, "0, " + row++ );
		row++;

		lblAreaSizeDesc = new JLabel( loc.getString( "Assignment.Area" ) );
		lblAreaSize = new JLabel( "" );
		this.add( lblAreaSizeDesc, "0, " + row++ );
		this.add( lblAreaSize, "0, " + row++ );
		row++;

		lblMaxPersonsDesc = new JLabel( loc.getString( "Assignment.MaxPersons" ) );
		lblMaxPersons = new JLabel( "" );
		this.add( lblMaxPersonsDesc, "0, " + row++ );
		this.add( lblMaxPersons, "0, " + row++ );
		row++;

		lblMaxPersonsWarning = new JLabel( loc.getString( "Assignment.AreaWarning" ) );
		this.add( lblMaxPersonsWarning, "0, " + row++ );
		row++;

	}

	@Override
	public void update() {
		txtNumberOfPersons.setText( nfInteger.format( current.getEvacuees() ) );
		assignmentTypeSelector.setSelectedItem( current.getAssignmentType() );
		double area = Math.round( current.areaMeter() * 100 ) / 100.0;
		lblAreaSize.setText( nfFloat.format( area ) + " m²" );
		if( projectControl.getProject().getBuildingPlan().isRastered() ) {
			lblMaxPersons.setText( nfInteger.format( current.getMaxEvacuees() ) );
			lblMaxPersonsWarning.setText( "" );
		} else {
			double persons = Math.round( (area / (0.4 * 0.4)) * 100 ) / 100.0;
			lblMaxPersons.setText( nfFloat.format( persons ) );
			lblMaxPersonsWarning.setText( loc.getString( "gui.EditPanel.Assignment.AreaWarning" ) );
		}
		if( current.getExitArea() == null )
			cbxPreferredExit.setSelectedIndex( -1 );
		else
			cbxPreferredExit.setSelectedItem( current.getExitArea() );

	}

	@Override
	public void localize() {
		loc.setPrefix( "gui.EditPanel." );
		lblAssignmentType.setText( loc.getString( "Assignment.Type" ) );
		lblAssignmentEvacueeNumber.setText( loc.getString( "Assignment.Persons" ) );
		btnAssignmentSetDefaultEvacuees.setText( loc.getString( "Assignment.SetDefaultEvacuees" ) );
		btnAssignmentSetDefaultEvacuees.setToolTipText( loc.getString( "Assignment.SetDefaultEvacuees.ToolTip" ) );
		lblPreferredExit.setText( loc.getString( "Assignment.PreferredExit" ) );
		lblMaxPersonsDesc.setText( loc.getString( "Assignment.MaxPersons" ) );
		lblMaxPersonsWarning.setText( loc.getString( "Assignment.AreaWarning" ) );
		lblAreaSizeDesc.setText( loc.getString( "Assignment.Area" ) );
		loc.clearPrefix();
	}
}
