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
package gui.batch;

import batch.Batch;
import batch.CellularAutomatonAlgorithm;
import batch.GraphAlgorithm;
import batch.load.BatchProjectEntry;
import ds.Project;
import ds.PropertyContainer;
import ds.z.Assignment;
import gui.JEditor;
import gui.ZETMain;
import gui.editor.properties.JPropertyComboBox;
import gui.editor.properties.PropertyFilesSelectionModel;
import gui.editor.properties.PropertyFilesSelectionModel.Property;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.rndutils.RandomUtils;

/**
 *
 * @author Timon
 */
public class JBatchView extends JPanel {
	private static final int COL_NAME = 0;
	private static final int COL_ASSIGNMENT = COL_NAME + 1;
	private static final int COL_CA_ALGO = COL_ASSIGNMENT + 1;
	private static final int COL_SIMULATE = COL_CA_ALGO + 1;
	private static final int COL_CYCLES = COL_SIMULATE + 1;
	private static final int COL_CA_MAX_TIME = COL_CYCLES + 1;
	private static final int COL_OPTIMIZE = COL_CA_MAX_TIME + 1;
	private static final int COL_GRAPH_ALGO = COL_OPTIMIZE + 1;
	private static final int COL_GRAPH_MAX_TIME = COL_GRAPH_ALGO + 1;
	private static final int COL_EVACUATION_PLAN_TYPE = COL_GRAPH_MAX_TIME + 1;
	private static final int COL_EVACUATION_PLAN_CYCLES = COL_EVACUATION_PLAN_TYPE + 1;
	private static final int COL_PROPERTIES = COL_EVACUATION_PLAN_CYCLES + 1;
	private static final int STARTING_CYCLES = PropertyContainer.getInstance().getAsInt( "properties.simulation.runCount" );
	private Batch batch;
	private int cycles = STARTING_CYCLES;
	private JTable tblEntries;
	private BatchTableModel tablemodel;
	private JTextField txtCycles;
	private JTextField txtSeed;
	private JPopupMenu popup;
	private JCheckBox chkTempFiles;

	public JBatchView() {
		super( new BorderLayout() );

		batch = new Batch();

		add( createMainPanel(), BorderLayout.CENTER );
		add( createSouthPanel(), BorderLayout.SOUTH );
	}

	private JPanel createSouthPanel() {
		txtCycles = new JTextField( Integer.toString( STARTING_CYCLES ), 3 );
		txtSeed = new JTextField( Long.toString( RandomUtils.getInstance().getSeed() ), 20 );
		chkTempFiles = new JCheckBox( Localization.getInstance().getString( "gui.editor.JBatchView.tempfiles" ), false );
		JButton btnRun = new JButton( Localization.getInstance().getString( "gui.editor.JBatchView.start" ) );

		int x = 0;

		JPanel south = new JPanel( new GridBagLayout() );
		south.add( new JLabel( Localization.getInstance().getString( "gui.editor.JBatchView.cycles" ) + " " ), new GridBagConstraints(
						x++, 0, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets( 5, 5, 5, 5 ), 0, 0 ) );
		south.add( txtCycles, new GridBagConstraints(
						x++, 0, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets( 5, 5, 5, 5 ), 0, 0 ) );
		south.add( new JLabel( Localization.getInstance().getString( "gui.editor.JBatchView.seed" ) + " " ), new GridBagConstraints(
						x++, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets( 5, 5, 5, 5 ), 0, 0 ) );
		south.add( txtSeed, new GridBagConstraints(
						x++, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets( 5, 5, 5, 5 ), 0, 0 ) );
		south.add( chkTempFiles, new GridBagConstraints(
						x++, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets( 5, 5, 5, 5 ), 0, 0 ) );
		south.add( btnRun, new GridBagConstraints(
						x++, 0, 1, 1, 0.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets( 5, 5, 5, 5 ), 0, 0 ) );

		txtCycles.addKeyListener( new KeyAdapter() {
			@Override
			public void keyReleased( KeyEvent e ) {
				String errorMessage = null;
				try {
					cycles = Integer.parseInt( txtCycles.getText() );
				} catch( NumberFormatException ex ) {
					errorMessage = Localization.getInstance().getString(
									"gui.error.NonParsableNumber" );
				} catch( RuntimeException ex ) {
					errorMessage = ex.getLocalizedMessage();
				}

				if( errorMessage != null ) {
					ZETMain.sendError( errorMessage );
					if( !txtCycles.getText().equals( "" ) )
						txtCycles.setText( Integer.toString( cycles ) );
				} else {
					ZETMain.sendError( "" ); // Clear error messages
					batch.setCyclesForAllEntries( cycles );
					tablemodel.fireTableDataChanged();
				}
			}
		} );
		txtSeed.addKeyListener( new KeyAdapter() {
			@Override
			public void keyReleased( KeyEvent e ) {
				String errorMessage = null;
				try {
					RandomUtils.getInstance().setSeed( Long.parseLong( txtSeed.getText() ) );
				} catch( NumberFormatException ex ) {
					errorMessage = Localization.getInstance().getString(
									"gui.error.NonParsableNumber" );
				} catch( RuntimeException ex ) {
					errorMessage = ex.getLocalizedMessage();
				}

				if( errorMessage != null ) {
					ZETMain.sendError( errorMessage );
					if( !txtSeed.getText().equals( "" ) &&
									!txtSeed.getText().equals( "-" ) )
						txtSeed.setText( Integer.toString( cycles ) );
				} else
					ZETMain.sendError( "" );
			}
		} );

		btnRun.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				try {
					if( batch.getEntries().isEmpty() ) {
						ZETMain.sendError( Localization.getInstance().getString( "gui.editor.JBatchView.emptyBatch" ) );
						return;
					}

					// Set seed (even if the user didn't edit it)
					try {
						RandomUtils.getInstance().setSeed( Long.parseLong( txtSeed.getText() ) );
					} catch( NumberFormatException ex ) {
						ZETMain.sendError( Localization.getInstance().getString(
										"gui.error.NonParsableNumber" ) );
						return;
					}

					JEditor.getInstance().setBatchResult( batch.execute( chkTempFiles.isSelected() ) );
				} catch( Exception ex ) {
					ZETMain.sendError( ex.getLocalizedMessage() );
					ex.printStackTrace();
				}
			}
		} );

		return south;
	}

	private JPanel createMainPanel() {
		JPanel main = new JPanel( new GridBagLayout() );
		JPanel tableButtons = new JPanel( new FlowLayout() );

		// Create the main table
		tablemodel = new BatchTableModel();
		tblEntries = new JTable( tablemodel );
		JComboBox cbxGraphAlgos = new JComboBox();
		for( GraphAlgorithm g : GraphAlgorithm.values() )
			cbxGraphAlgos.addItem( g );

		JComboBox cbxCAAlgos = new JComboBox();
		for( CellularAutomatonAlgorithm caa : CellularAutomatonAlgorithm.values() )
			cbxCAAlgos.addItem( caa );

		JComboBox cbxEvacuationOptimizationType = new JComboBox();
		for( EvacuationOptimizationType eot : EvacuationOptimizationType.values() )
			cbxEvacuationOptimizationType.addItem( eot );

		tblEntries.setFillsViewportHeight( true );
		// Set table column editors
		SelectingCellEditor selectingEditor = new SelectingCellEditor();
		tblEntries.getColumnModel().getColumn( COL_NAME ).setCellEditor( selectingEditor );
		tblEntries.getColumnModel().getColumn( COL_ASSIGNMENT ).setCellEditor( new AssignmentCellEditor() );
		tblEntries.getColumnModel().getColumn( COL_CA_ALGO ).setCellEditor( new DefaultCellEditor( cbxCAAlgos ) );
		tblEntries.getColumnModel().getColumn( COL_CYCLES ).setCellEditor( selectingEditor );
		tblEntries.getColumnModel().getColumn( COL_CA_MAX_TIME ).setCellEditor( selectingEditor );
		tblEntries.getColumnModel().getColumn( COL_GRAPH_MAX_TIME ).setCellEditor( selectingEditor );
		tblEntries.getColumnModel().getColumn( COL_GRAPH_ALGO ).setCellEditor( new DefaultCellEditor( cbxGraphAlgos ) );
		tblEntries.getColumnModel().getColumn( COL_PROPERTIES ).setCellEditor( new DefaultCellEditor( new JPropertyComboBox() ) );
		tblEntries.getColumnModel().getColumn( COL_EVACUATION_PLAN_CYCLES ).setCellEditor( selectingEditor );
		tblEntries.getColumnModel().getColumn( COL_EVACUATION_PLAN_TYPE ).setCellEditor( new DefaultCellEditor( cbxEvacuationOptimizationType ) );
		// Set table column renderers
		InactiveRenderer inactiveRenderer = new InactiveRenderer();
		tblEntries.getColumnModel().getColumn( COL_ASSIGNMENT ).setCellRenderer( new AssignmentRenderer() );
		tblEntries.getColumnModel().getColumn( COL_CA_ALGO ).setCellRenderer( inactiveRenderer );
		tblEntries.getColumnModel().getColumn( COL_CYCLES ).setCellRenderer( inactiveRenderer );
		tblEntries.getColumnModel().getColumn( COL_CA_MAX_TIME ).setCellRenderer( inactiveRenderer );
		tblEntries.getColumnModel().getColumn( COL_GRAPH_MAX_TIME ).setCellRenderer( inactiveRenderer );
		tblEntries.getColumnModel().getColumn( COL_GRAPH_ALGO ).setCellRenderer( inactiveRenderer );
		tblEntries.getColumnModel().getColumn( COL_EVACUATION_PLAN_CYCLES ).setCellRenderer( inactiveRenderer );
		tblEntries.getColumnModel().getColumn( COL_EVACUATION_PLAN_TYPE ).setCellRenderer( inactiveRenderer );
		// Set table column widths
		tblEntries.getColumnModel().getColumn( COL_NAME ).setPreferredWidth( 100 );
		tblEntries.getColumnModel().getColumn( COL_ASSIGNMENT ).setPreferredWidth( 100 );
		tblEntries.getColumnModel().getColumn( COL_CA_ALGO ).setPreferredWidth( 120 );
		tblEntries.getColumnModel().getColumn( COL_SIMULATE ).setPreferredWidth( 50 );
		tblEntries.getColumnModel().getColumn( COL_CYCLES ).setPreferredWidth( 50 );
		tblEntries.getColumnModel().getColumn( COL_CA_MAX_TIME ).setPreferredWidth( 80 );
		tblEntries.getColumnModel().getColumn( COL_OPTIMIZE ).setPreferredWidth( 50 );
		tblEntries.getColumnModel().getColumn( COL_GRAPH_ALGO ).setPreferredWidth( 210 );
		tblEntries.getColumnModel().getColumn( COL_GRAPH_MAX_TIME ).setPreferredWidth( 80 );
		tblEntries.getColumnModel().getColumn( COL_EVACUATION_PLAN_CYCLES ).setPreferredWidth( 50 );
		tblEntries.getColumnModel().getColumn( COL_EVACUATION_PLAN_TYPE ).setPreferredWidth( 80 );


		int y = 0;
		main.add( new JLabel( Localization.getInstance().getString( "gui.editor.JBatchView.batchCaption" ) + " " ), new GridBagConstraints(
						0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets( 5, 5, 0, 5 ), 0, 0 ) );
		main.add( new JScrollPane( tblEntries ), new GridBagConstraints(
						0, y++, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets( 5, 5, 0, 5 ), 0, 0 ) );
		main.add( tableButtons, new GridBagConstraints(
						0, y++, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
						new Insets( 5, 5, 20, 5 ), 0, 0 ) );

		JButton btnNewEntry = new JButton( Localization.getInstance().getString( "gui.editor.JBatchView.newEntry" ) );
		btnNewEntry.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				addProject( JEditor.getInstance().getZControl().getProject() );
			}
		} );
		tableButtons.add( btnNewEntry );
		JButton btnDeleteEntry = new JButton( Localization.getInstance().getString( "gui.editor.JBatchView.deleteEntry" ) );
		btnDeleteEntry.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				if( tblEntries.getSelectedRow() >= 0 ) {
					if( tblEntries.isEditing() )
						tblEntries.getCellEditor().cancelCellEditing();
					batch.removeEntry( batch.getEntries().get( tblEntries.getSelectedRow() ) );
					tablemodel.fireTableDataChanged();
				} else
					ZETMain.sendMessage( Localization.getInstance().getString( "gui.editor.JBatchView.selectLineFirst" ) );
			}
		} );
		tableButtons.add( btnDeleteEntry );
		JButton btnClearEntries = new JButton( Localization.getInstance().getString( "gui.editor.JBatchView.clearEntries" ) );
		btnClearEntries.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				if( tblEntries.isEditing() )
					tblEntries.getCellEditor().cancelCellEditing();
				batch.clearEntries();
				tablemodel.fireTableDataChanged();
			}
		} );
		tableButtons.add( btnClearEntries );

		// Install popup menu for the table
		popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem( Localization.getInstance().getString( "gui.editor.JBatchView.confidenceIntervalCaption" ) );
		menuItem.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				JOptionPane.showInputDialog( tblEntries, Localization.getInstance().getString( "gui.editor.JBatchView.confidenceIntervalText" ) + " " );
			// TODO: # Cycles berechnen ...
			}
		} );
		popup.add( menuItem );
		tblEntries.addMouseListener( new MouseAdapter() {
			@Override
			public void mousePressed( MouseEvent e ) {
				maybeShowPopup( e );
			}

			@Override
			public void mouseReleased( MouseEvent e ) {
				maybeShowPopup( e );
			}

			private void maybeShowPopup( MouseEvent e ) {
				if( e.isPopupTrigger() )
					popup.show( e.getComponent(), e.getX(), e.getY() );
			}
		} );
		return main;
	}

	/**
	 * Adds a {@link ds.Project} to the batch table.
	 * @param project the added project
	 */
	public void addProject( Project project ) {
		try {
			if( tblEntries.isEditing() )
				tblEntries.getCellEditor().cancelCellEditing();
			batch.addEntry( "Neuer Eintrag", project, cycles, GraphAlgorithm.SuccessiveEarliestArrivalAugmentingPathOptimized, CellularAutomatonAlgorithm.RandomOrder );
			tablemodel.fireTableDataChanged();
		} catch( Exception ex ) {
			ZETMain.sendError( ex.getLocalizedMessage() );
		}
	}

	/**
	 * Adds a {@link BatchProjectEntry} to the batch table. The entry can be
	 * loaded from a batch task file.
	 * @param batchProjectEntry the new batch entry.
	 */
	public void add( BatchProjectEntry batchProjectEntry ) {
		try {
			if( tblEntries.isEditing() )
				tblEntries.getCellEditor().cancelCellEditing();
			// Lade Projekt
			File projectFile = new File( batchProjectEntry.getProjectFile() );
			Project project = Project.load( projectFile );
			Assignment assignment = null; // = new Assignment( "bla" );
			// suche assignment
			for( Assignment a : project.getAssignments() )
				if( a.getName().equals( batchProjectEntry.getAssignment() ) ) {
					assignment = a;
					break;
				}
			if( assignment == null )
				throw new IllegalStateException( "Assignment " + batchProjectEntry.getAssignment() + " not found!" );
			Property property = PropertyFilesSelectionModel.getInstance().getPropertyByName( batchProjectEntry.getProperty() );
			if( property == null )
				throw new IllegalStateException( "Property " + batchProjectEntry.getProperty() + " not found!" );
			batch.addEntry(
							batchProjectEntry.getName(),
							project,
							assignment,
							batchProjectEntry.isUseCellularAutomaton(),
							batchProjectEntry.getCellularAutomanMaximalTime(),
							batchProjectEntry.getCellularAutomatonRuns(),
							batchProjectEntry.getGraphAlgorithm(),
							batchProjectEntry.getCellularAutomatonAlgorithm(),
							batchProjectEntry.isUseGraph(),
							batchProjectEntry.getGraphTimeHorizon(),
							batchProjectEntry.getEvacuationOptimizationType(),
							batchProjectEntry.getEvacuationOptimizationRuns(),
							property );
		} catch( Exception ex ) {
			ZETMain.sendError( ex.getLocalizedMessage() );
		}
	}

	private class BatchTableModel extends AbstractTableModel {
		@Override
		public Class getColumnClass( int column ) {
			switch( column ) {
				case COL_NAME:
					return String.class;
				case COL_ASSIGNMENT:
					return Assignment.class;
				case COL_CA_ALGO:
					return CellularAutomatonAlgorithm.class;
				case COL_SIMULATE:
					return Boolean.class;
				case COL_CYCLES:
					return Integer.class;
				case COL_CA_MAX_TIME:
					return Double.class;
				case COL_GRAPH_MAX_TIME:
					return Integer.class;
				case COL_OPTIMIZE:
					return Boolean.class;
				case COL_GRAPH_ALGO:
					return GraphAlgorithm.class;
				case COL_EVACUATION_PLAN_CYCLES:
					return Integer.class;
				case COL_EVACUATION_PLAN_TYPE:
					return EvacuationOptimizationType.class;
				case COL_PROPERTIES:
					return PropertyFilesSelectionModel.Property.class;
				default:
					return null;
			}
		}

		@Override
		public String getColumnName( int column ) {
			switch( column ) {
				case COL_NAME:
					return Localization.getInstance().getString( "gui.editor.JBatchView.table.column.name" );
				case COL_ASSIGNMENT:
					return Localization.getInstance().getString( "gui.editor.JBatchView.table.column.assignment" );
				case COL_CA_ALGO:
					return Localization.getInstance().getString( "gui.editor.JBatchView.table.column.caAlgo" );
				case COL_SIMULATE:
					return Localization.getInstance().getString( "gui.editor.JBatchView.table.column.simulate" );
				case COL_CYCLES:
					return Localization.getInstance().getString( "gui.editor.JBatchView.table.column.cycles" );
				case COL_CA_MAX_TIME:
					return Localization.getInstance().getString( "gui.editor.JBatchView.table.column.caMaxTime" );
				case COL_GRAPH_MAX_TIME:
					return Localization.getInstance().getString( "gui.editor.JBatchView.table.column.graphMaxTime" );
				case COL_OPTIMIZE:
					return Localization.getInstance().getString( "gui.editor.JBatchView.table.column.optimize" );
				case COL_GRAPH_ALGO:
					return Localization.getInstance().getString( "gui.editor.JBatchView.table.column.algo" );
				case COL_EVACUATION_PLAN_CYCLES:
					return Localization.getInstance().getString( "gui.editor.JBatchView.table.column.evacCycles" );
				case COL_EVACUATION_PLAN_TYPE:
					return Localization.getInstance().getString( "gui.editor.JBatchView.table.column.evacType" );
				case COL_PROPERTIES:
					return Localization.getInstance().getString( "gui.editor.JBatchView.table.column.properties" );
				default:
					return null;
			}
		}

		public int getRowCount() {
			return batch.getEntries().size();
		}

		public int getColumnCount() {
			return COL_PROPERTIES + 1;
		}

		public Object getValueAt( int row, int column ) {
			switch( column ) {
				case COL_NAME:
					return batch.getEntries().get( row ).getName();
				case COL_ASSIGNMENT:
					return batch.getEntries().get( row ).getAssignment();
				case COL_CA_ALGO:
					return batch.getEntries().get( row ).getCellularAutomatonAlgo();
				case COL_SIMULATE:
					return batch.getEntries().get( row ).getUseCa();
				case COL_CYCLES:
					return batch.getEntries().get( row ).getCycles();
				case COL_CA_MAX_TIME:
					return batch.getEntries().get( row ).getCaMaxTime();
				case COL_GRAPH_MAX_TIME:
					return batch.getEntries().get( row ).getGraphMaxTime();
				case COL_OPTIMIZE:
					return batch.getEntries().get( row ).getUseGraph();
				case COL_GRAPH_ALGO:
					return batch.getEntries().get( row ).getGraphAlgo();
				case COL_EVACUATION_PLAN_CYCLES:
					return batch.getEntries().get( row ).getOptimizedEvacuationPlanCycles();
				case COL_EVACUATION_PLAN_TYPE:
					return batch.getEntries().get( row ).getEvacuationOptimizationType();
				case COL_PROPERTIES:
					return batch.getEntries().get( row ).getProperty();
				default:
					return null;
			}
		}

		@Override
		public boolean isCellEditable( int row, int column ) {
			switch( column ) {
				case COL_CA_ALGO:
					return batch.getEntries().get( row ).getUseCa();
				case COL_CYCLES:
					return batch.getEntries().get( row ).getUseCa();
				case COL_GRAPH_ALGO:
					return batch.getEntries().get( row ).getUseGraph();
				case COL_CA_MAX_TIME:
					return batch.getEntries().get( row ).getUseCa();
				case COL_GRAPH_MAX_TIME:
					return batch.getEntries().get( row ).getUseGraph();
				case COL_EVACUATION_PLAN_TYPE:
					return batch.getEntries().get( row ).getUseGraph();
				case COL_EVACUATION_PLAN_CYCLES:
					return !batch.getEntries().get( row ).getEvacuationOptimizationType().equals( EvacuationOptimizationType.None ) && batch.getEntries().get( row ).getUseGraph();
				default:
					return true;
			}
		}

		@Override
		public void setValueAt( Object aValue, int row, int column ) {
			switch( column ) {
				case COL_NAME:
					batch.getEntries().get( row ).setName( (String)aValue );
					break;
				case COL_ASSIGNMENT:
					batch.getEntries().get( row ).setAssignment( (Assignment)aValue );
					break;
				case COL_CA_ALGO:
					batch.getEntries().get( row ).setCellularAutomatonAlgo( (CellularAutomatonAlgorithm)aValue );
					break;
				case COL_SIMULATE:
					batch.getEntries().get( row ).setUseCa( ((Boolean)aValue).booleanValue() );
					break;
				case COL_CYCLES:
					try {
						batch.getEntries().get( row ).setCycles( Integer.parseInt( aValue.toString() ) );
					} catch( NumberFormatException ex ) {
						ZETMain.sendError( Localization.getInstance().getString(
										"gui.error.NonParsableNumber" ) );
					} catch( RuntimeException ex ) {
						ZETMain.sendError( ex.getLocalizedMessage() );
					}
					break;
				case COL_CA_MAX_TIME:
					try {
						batch.getEntries().get( row ).setCaMaxTime( Double.parseDouble( aValue.toString() ) );
					} catch( NumberFormatException ex ) {
						ZETMain.sendError( Localization.getInstance().getString(
										"gui.error.NonParsableFloatString" ) );
					} catch( RuntimeException ex ) {
						ZETMain.sendError( ex.getLocalizedMessage() );
					}
					break;
				case COL_OPTIMIZE:
					batch.getEntries().get( row ).setUseGraph( ((Boolean)aValue).booleanValue() );
					break;
				case COL_GRAPH_ALGO:
					batch.getEntries().get( row ).setGraphAlgo( (GraphAlgorithm)aValue );
					break;
				case COL_EVACUATION_PLAN_CYCLES:
					try {
						batch.getEntries().get( row ).setOptimizedEvacuationPlanCycles( Integer.parseInt( aValue.toString() ) );
					} catch( NumberFormatException ex ) {
						ZETMain.sendError( Localization.getInstance().getString(
										"gui.error.NonParsableNumber" ) );
					} catch( RuntimeException ex ) {
						ZETMain.sendError( ex.getLocalizedMessage() );
					}
					break;
				case COL_EVACUATION_PLAN_TYPE:
					batch.getEntries().get( row ).setEvacuationOptimizationType( (EvacuationOptimizationType)aValue );
					if( aValue != EvacuationOptimizationType.None ) {
						if( batch.getEntries().get( row ).getOptimizedEvacuationPlanCycles() <= 0 )
							batch.getEntries().get( row ).setOptimizedEvacuationPlanCycles( 1 );
					} else
						batch.getEntries().get( row ).setOptimizedEvacuationPlanCycles( 0 );
					break;
				case COL_PROPERTIES:
					batch.getEntries().get( row ).setProperty( (PropertyFilesSelectionModel.Property)aValue );
					break;
				case COL_GRAPH_MAX_TIME:
					try {
						batch.getEntries().get( row ).setGraphMaxTime( Integer.parseInt( aValue.toString() ) );
					} catch( NumberFormatException ex ) {
						ZETMain.sendError( Localization.getInstance().getString(
										"gui.error.NonParsableNumber" ) );
					} catch( RuntimeException ex ) {
						ZETMain.sendError( ex.getLocalizedMessage() );
					}
					break;
				default:
					;
			}
			tblEntries.repaint(); // Necessary for (de)activating columns
		}
	}

	/** A Table Cell editor that immediately selects the whole Text when it is activated. */
	private class SelectingCellEditor extends DefaultCellEditor {
		public SelectingCellEditor() {
			super( new JTextField() );

			setClickCountToStart( 1 );

			getComponent().addFocusListener( new FocusAdapter() {
				@Override
				public void focusGained( FocusEvent e ) {
					((JTextField)e.getComponent()).selectAll();
				}
			} );
		}
	}

	/** A Table Cell editor that displays assignments. */
	private class AssignmentCellEditor extends DefaultCellEditor {
		private DefaultComboBoxModel model;

		public AssignmentCellEditor() {
			super( new JComboBox() );

			model = new DefaultComboBoxModel();
			((JComboBox)getComponent()).setModel( model );
			((JComboBox)getComponent()).setRenderer( new BasicComboBoxRenderer() {
				@Override
				public Component getListCellRendererComponent( JList list,
								Object value,
								int index,
								boolean isSelected,
								boolean cellHasFocus ) {
					Component res = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

					((JLabel)res).setText( ((Assignment)value).getName() );

					return res;
				}
			} );

		}

		@Override
		public Component getTableCellEditorComponent( JTable table,
						Object value,
						boolean isSelected,
						int row,
						int column ) {
			Component res = super.getTableCellEditorComponent( table, value, isSelected, row, column );

			// Build the list from scratch each time the editor is shown
			Project p = batch.getEntries().get( row ).getProject();
			model.removeAllElements();
			for( Assignment a : p.getAssignments() )
				model.addElement( a );

			return res;
		}
	}

	/** Renders uneditable cells specially marked. */
	public class InactiveRenderer extends DefaultTableCellRenderer {
		private final Color inactiveColor = Color.GRAY;
		private Color activeColorSelected = null;
		private Color activeColorUnselected = null;

		@Override
		public Component getTableCellRendererComponent(
						JTable table, Object value,
						boolean isSelected, boolean hasFocus,
						int row, int column ) {

			if( activeColorSelected == null ) {
				Component normal = super.getTableCellRendererComponent( table, value,
								false, false, row, column );
				activeColorUnselected = normal.getForeground();
				normal = super.getTableCellRendererComponent( table, value,
								true, true, row, column );
				activeColorSelected = normal.getForeground();
			}

			Component normal = super.getTableCellRendererComponent( table, value,
							isSelected, hasFocus, row, column );

			if( !tablemodel.isCellEditable( row, column ) )
				normal.setForeground( inactiveColor );
			else
				normal.setForeground( isSelected ? activeColorSelected : activeColorUnselected );

			return this;
		}
	}

	/** Renders assignments. */
	public class AssignmentRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(
						JTable table, Object value,
						boolean isSelected, boolean hasFocus,
						int row, int column ) {

			Component normal = super.getTableCellRendererComponent( table, value,
							isSelected, hasFocus, row, column );

			setText( ((Assignment)value).getName() );

			return this;
		}
	}
}
