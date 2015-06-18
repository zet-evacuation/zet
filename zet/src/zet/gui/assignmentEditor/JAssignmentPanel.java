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

package zet.gui.assignmentEditor;

import org.zetool.common.localization.Localization;
import org.zetool.common.localization.LocalizationManager;
import org.zetool.rndutils.distribution.Distribution;
import org.zetool.rndutils.distribution.continuous.ErlangDistribution;
import org.zetool.rndutils.distribution.continuous.ExponentialDistribution;
import org.zetool.rndutils.distribution.continuous.HyperExponentialDistribution;
import org.zetool.rndutils.distribution.continuous.NormalDistribution;
import org.zetool.rndutils.distribution.continuous.UniformDistribution;
import de.zet_evakuierung.model.Project;
import de.zet_evakuierung.model.Assignment;
import de.zet_evakuierung.model.AssignmentType;
import de.zet_evakuierung.model.ZControl;
import de.tu_berlin.math.coga.components.framework.Button;
import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.KeyAdapter;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import zet.gui.components.model.ComboBoxRenderer;
import zet.gui.GUILocalization;

/**
 * A panel containing all assignments and assignment type of a
 * {@link ds.Project} of an evacuation. Basically the assignment works as a
 * box that contains some types (that can describe different types of people).
 * All of the parameters of the different assignment types can be set
 * as probability distributions. Currently
 * {@link org.zetool.rndutils.distribution.continuous.NormalDistribution},
 * {@link org.zetool.rndutils.distribution.continuous.UniformDistribution},
 * {@link org.zetool.rndutils.distribution.continuous.ExponentialDistribution},
 * {@link ErlangDistribution} and
 * {@link HyperExponentialDistribution} are supported.
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings("serial")
public class JAssignmentPanel extends JPanel {
	private static final int COL_NAME = 0;
	private static final int COL_DISTRIBUTION = 1;
	private static final int COL_PARAM1 = 2;
	private static final int COL_PARAM2 = 3;
	private static final int COL_PARAM3 = 4;
	private static final int COL_PARAM4 = 5;
	private static final int DIST_NORMAL = 0;
	private static final int DIST_UNIFORM = 1;
	private static final int DIST_EXPONENTIAL = 2;
	private static final int DIST_ERLANG = 3;
	private static final int DIST_HYPEREXPONENTIAL = 4;
	private final ZControl myProject;
	private static final Localization loc = GUILocalization.loc;	// the ZET GUI localization object
	private Assignment currentAssignment;
	private AssignmentType currentAssignmentType;
	private JTextField addAssignmentText;
	private JTextField addAssignmentTypeText;
	private JList<Assignment> lstAssignment;
	private AssignmentListModel assignmentSelector;
	private JList<AssignmentType> lstAssignmentType;
	private AssignmentTypeListModel assignmentTypeSelector;
	private JTextField txtDefaultEvacuees;
	private final ArrayList<DistributionEntry<Double>> params;
	private AssignmentTableModel tablemodel;
	private JTable distributionTable;
	/** A panel containing the plot of the density */
	private ChartPanel chartPanel;
	/** The chart of the probability dense function */
	private JFreeChart chart;
	private JComboBox<String> distributions;
	private JDialog parent;

	/**
	 * Initializes the an {@code JAssignmentPanel} instance with lists for all
	 * assignments, the types they contain and an area for their distributions.
	 * @param parent the parent window in which the panel is contained.
	 * @param p the project whose assignments are edited
	 */
	public JAssignmentPanel( JDialog parent, ZControl p ) {
		super();
		myProject = p;
		params = new ArrayList<>( 5 );
		this.parent = parent;
		addComponents();
	}

	/*****************************************************************************
	 *                                                                           *
	 * Initialization of the Dialog                                              *
	 *                                                                           *
	 ****************************************************************************/
	/**
	 * Adds all components to the panel.
	 */
	private void addComponents() {
		setLayout( new BorderLayout() );
		initDistributions();

		// Create the main table
		tablemodel = new AssignmentTableModel();
		tablemodel.addTableModelListener( e -> { drawCharts(); } );
		distributionTable = new JTable( tablemodel );
		distributionTable.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		distributionTable.getSelectionModel().addListSelectionListener( (ListSelectionEvent e) -> {
			drawCharts();
		});

		distributions = new JComboBox<>();
		distributions.addItem( loc.getString( "gui.AssignmentEditor.Distribution.NormalDistribution" ) );
		distributions.addItem( loc.getString( "gui.AssignmentEditor.Distribution.UniformDistribution" ) );
		distributions.addItem( loc.getString( "gui.AssignmentEditor.Distribution.ExponentialDistribution" ) );
		distributions.addItem( loc.getString( "gui.AssignmentEditor.Distribution.ErlangDistribution" ) );
		distributions.addItem( loc.getString( "gui.AssignmentEditor.Distribution.HyperExponentialDistribution" ) );
		distributionTable.getColumnModel().getColumn( COL_DISTRIBUTION ).setCellEditor( new DefaultCellEditor( distributions ) );
		distributionTable.getColumnModel().getColumn( COL_PARAM1 ).setCellEditor( new SelectingCellEditor() );
		distributionTable.getColumnModel().getColumn( COL_PARAM2 ).setCellEditor( new SelectingCellEditor() );
		distributionTable.getColumnModel().getColumn( COL_PARAM3 ).setCellEditor( new SelectingCellEditor() );
		distributionTable.getColumnModel().getColumn( COL_PARAM4 ).setCellEditor( new SelectingCellEditor() );
		distributionTable.getColumnModel().getColumn( COL_PARAM3 ).setCellRenderer( new InactiveRenderer() );
		distributionTable.getColumnModel().getColumn( COL_PARAM4 ).setCellRenderer( new InactiveRenderer() );

		add( getRightPanel(), BorderLayout.CENTER );
		final JPanel leftPanel = getLeftPanel();
		leftPanel.setMaximumSize( new Dimension( 200, 0 ) );
		add( leftPanel, BorderLayout.WEST );
		assignmentSelector.displayAssignments( myProject.getProject() );
	}

	/**
	 * Returns the panel on the left side of the {@code JAssignmentPanel} that
	 * contains lists of assignments and assignment types.
	 * @return the panel on the left side of the {@code JAssignmentPanel}
	 */
	private JPanel getLeftPanel() {
		final int space = 16;
		double size[][] = // Columns
						{
			{ space, TableLayout.FILL, space, TableLayout.FILL, space },
			//Rows
			{ space, TableLayout.PREFERRED, // Label
				1, TableLayout.FILL, // Auswahlbox
				space, TableLayout.PREFERRED, // Label Name
				1, TableLayout.PREFERRED, // TextFeld
				1, TableLayout.PREFERRED,
				1, TableLayout.PREFERRED,
				1, TableLayout.PREFERRED, // Button
				space, TableLayout.PREFERRED, // Button Neu
				5, TableLayout.PREFERRED, // Button Löschen
				space
			}
		};

		JPanel leftPanel = new JPanel( new TableLayout( size ) );
		int row = 1;

		leftPanel.add( new JLabel( loc.getString( "gui.AssignmentEditor.label.Assignments" ) ), "1, " + row++ );
		row++;

		assignmentSelector = new AssignmentListModel();
		lstAssignment = new JList<>();
		lstAssignment.setModel( assignmentSelector );
		lstAssignment.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		lstAssignment.setCellRenderer( new AssignmentListRenderer() );
		lstAssignment.setSelectionModel( new AssignmentListSelectionModel() );
		lstAssignment.addMouseListener( new MouseAdapter() {

			@Override
			public void mousePressed( MouseEvent e ) {
				if( e.getClickCount() == 2 ) {
					setCurrent();
				}
			}
		} );

		leftPanel.add( new JScrollPane( lstAssignment ), "1, " + row++ );
		row++;

		leftPanel.add( new JLabel( loc.getString( "gui.AssignmentEditor.label.Name" ) ), "1, " + row++ );
		row++;

		addAssignmentText = new JTextField();
		leftPanel.add( addAssignmentText, "1, " + row++ );
		row++;

		JButton assignmentChange = Button.newButton( loc.getString( "gui.AssignmentEditor.button.AssignmentSave" ), aclAssignmentSaveChanges );
		assignmentChange.setToolTipText( loc.getString( "gui.AssignmentEditor.button.AssignmentSave.ToolTip" ) );
		leftPanel.add( assignmentChange, "1, " + row++ );
		row++;

		row += 4;

		JButton assignmentAdd = Button.newButton( loc.getString( "gui.AssignmentEditor.button.AssignmentAdd" ), aclAssignmentAdd );
		assignmentAdd.setToolTipText( loc.getString( "gui.AssignmentEditor.button.AssignmentSave.ToolTip" ) );
		leftPanel.add( assignmentAdd, "1, " + row++ );
		row++;

		JButton assignmentDelete = Button.newButton( loc.getString( "gui.AssignmentEditor.button.AssignmentDelete" ), aclAssignmentDelete );
		assignmentDelete.setToolTipText( loc.getString( "gui.AssignmentEditor.button.AssignmentSave.ToolTip" ) );
		leftPanel.add( assignmentDelete, "1, " + row++ );
		row++;

		// Rechter Teil
		row = 1;
		leftPanel.add( new JLabel( loc.getString( "gui.AssignmentEditor.label.AssignmentTypes" ) ), "3, " + row++ );
		row++;

		assignmentTypeSelector = new AssignmentTypeListModel();
		lstAssignmentType = new JList<>();
		lstAssignmentType.setModel( assignmentTypeSelector );
		lstAssignmentType.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		lstAssignmentType.setCellRenderer( new AssignmentTypeListRenderer() );
		lstAssignmentType.setSelectionModel( new AssignmentTypeListSelectionModel() );

		leftPanel.add( new JScrollPane( lstAssignmentType ), "3, " + row++ );
		row++;

		leftPanel.add( new JLabel( loc.getString( "gui.AssignmentEditor.label.Name" ) ), "3, " + row++ );
		row++;

		addAssignmentTypeText = new JTextField();
		leftPanel.add( addAssignmentTypeText, "3, " + row++ );
		row++;

		leftPanel.add( new JLabel( loc.getString( "gui.AssignmentEditor.label.DefaultEvacuees" ) ), "3, " + row++ );
		row++;
		txtDefaultEvacuees = new JTextField();
		txtDefaultEvacuees.addKeyListener( kylEvacuees );
		leftPanel.add( txtDefaultEvacuees, "3, " + row++ );
		row++;

		JButton assignmentTypeChange = Button.newButton( loc.getString( "gui.AssignmentEditor.button.AssignmentTypeSave" ), aclAssignmentTypeSaveChanges );
		assignmentTypeChange.setToolTipText( loc.getString( "gui.AssignmentEditor.button.AssignmentTypeSave.ToolTip" ) );
		leftPanel.add( assignmentTypeChange, "3, " + row++ );
		row++;

		JButton assignmentTypeAdd = Button.newButton( loc.getString( "gui.AssignmentEditor.button.AssignmentTypeAdd" ), aclAssignmentTypeAdd );
		assignmentTypeAdd.setToolTipText( loc.getString( "gui.AssignmentEditor.button.AssignmentTypeAdd.ToolTip" ) );
		leftPanel.add( assignmentTypeAdd, "3, " + row++ );
		row++;

		JButton assignmentTypeDelete = Button.newButton( loc.getString( "gui.AssignmentEditor.button.AssignmentTypeDelete" ), aclAssignmentTypeDelete );
		assignmentTypeDelete.setToolTipText( loc.getString( "gui.AssignmentEditor.button.AssignmentTypeSave.ToolTip" ) );
		leftPanel.add( assignmentTypeDelete, "3, " + row++ );
		row++;

		return leftPanel;
	}

	/**
	 * Returns the panel on the right side of the {@code JAssignmentPanel} that
	 * contains distributions for all properties and a plot of the distribution
	 * with the current parameters.
	 * @return the panel on the right side of the {@code JAssignmentPanel}
	 */
	private JPanel getRightPanel() {
		final JPanel rightPanel = new JPanel( new GridBagLayout() );

		final JPanel tableContainer = new JPanel( new BorderLayout() );
		tableContainer.add( distributionTable.getTableHeader(), BorderLayout.PAGE_START );
		tableContainer.add( distributionTable, BorderLayout.CENTER );

		rightPanel.add( tableContainer, new GridBagConstraints( 0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets( 16, 0, 0, 16 ), 0, 0 ) );

		XYSeriesCollection c = new XYSeriesCollection();
		chart = ChartFactory.createXYLineChart( loc.getStringWithoutPrefix( "gui.AssignmentEditor.plot.Title" ),
						loc.getStringWithoutPrefix( "gui.AssignmentEditor.plot.Values" ), // x axis label
						loc.getStringWithoutPrefix( "gui.AssignmentEditor.plot.Probability" ), // y axis label
						c, // Dataset
						PlotOrientation.VERTICAL,
						false, true, false // Show legend, tooltips, url
						);

		chartPanel = new ChartPanel( chart );
		rightPanel.add( chartPanel, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 16, 0, 0, 16 ), 0, 0 ) );

		rightPanel.add( chartPanel, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 16, 0, 0, 16 ), 0, 0 ) );

		return rightPanel;
	}

	/**
	 * Initializes the distributions to defaults such that they are valid. They
	 * are changed when a specific assignment is selected.
	 */
	private void initDistributions() {
		loc.setPrefix( "gui.z.AssignmentType." );
		params.add( new DistributionEntry<>( loc.getString( "diameter" ), new NormalDistribution() ) );
		params.add( new DistributionEntry<>( loc.getString( "age" ), new NormalDistribution() ) );
		params.add( new DistributionEntry<>( loc.getString( "familiarity" ), new NormalDistribution() ) );
		params.add( new DistributionEntry<>( loc.getString( "panic" ), new NormalDistribution() ) );
		params.add( new DistributionEntry<>( loc.getString( "decisiveness" ), new NormalDistribution() ) );
		params.add( new DistributionEntry<>( loc.getString( "reaction" ), new UniformDistribution() ) );
		loc.setPrefix( "" );
	}
	/*****************************************************************************
	 *                                                                           *
	 * Action listener                                                           *
	 *                                                                           *
	 ****************************************************************************/
	private ActionListener aclAssignmentAdd = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			try {
				Assignment add = new Assignment( addAssignmentText.getText() );
				myProject.addAssignment( add );
				
				addAssignmentText.setText( "" );
				assignmentSelector.displayAssignments( myProject.getProject() );
				lstAssignment.setSelectedValue( add, true );
			} catch( IllegalArgumentException ex ) {
				JOptionPane.showMessageDialog( parent, ex.getMessage(), loc.getString( "gui.General.Error" ), JOptionPane.ERROR_MESSAGE );
			}
		}
	};
	private ActionListener aclAssignmentTypeAdd = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			if( currentAssignment != null ) {
				try {
					AssignmentType at = new AssignmentType( addAssignmentTypeText.getText(),
									ZControl.getDefaultAssignmentTypeDistribution( "diameter" ),
									ZControl.getDefaultAssignmentTypeDistribution( "age" ),
									ZControl.getDefaultAssignmentTypeDistribution( "familiarity" ),
									ZControl.getDefaultAssignmentTypeDistribution( "panic" ),
									ZControl.getDefaultAssignmentTypeDistribution( "decisiveness" ),
									ZControl.getDefaultAssignmentTypeDistribution( "reaction" ) );
					at.setDefaultEvacuees( Integer.parseInt( txtDefaultEvacuees.getText() ) );
					currentAssignment.addAssignmentType( at );
					addAssignmentTypeText.setText( "" );
					assignmentTypeSelector.displayAssignmentTypes();
					lstAssignmentType.setSelectedValue( at, true );
				} catch( NumberFormatException ex ) {
					JOptionPane.showMessageDialog( parent, loc.getString( "gui.AssignmentEditor.Error.IllegalNumberOfEvacuees" ), loc.getString( "gui.General.Error" ), JOptionPane.ERROR_MESSAGE );
				} catch( IllegalArgumentException ex ) {
					JOptionPane.showMessageDialog( parent, ex.getMessage(), loc.getString( "gui.General.Error" ), JOptionPane.ERROR_MESSAGE );
				}
			}
		}
	};
	private ActionListener aclAssignmentSaveChanges = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			if( currentAssignment == null )
				return;
			try {
				currentAssignment.setName( addAssignmentText.getText() );
				lstAssignment.repaint();
			} catch( IllegalArgumentException ex ) {// empty text field, exception is localized
				JOptionPane.showMessageDialog( parent, ex.getMessage(), loc.getString( "gui.General.Error" ), JOptionPane.ERROR_MESSAGE );
			}
		}
	};
	private ActionListener aclAssignmentTypeSaveChanges = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			if( currentAssignmentType != null ) {
				try {
					currentAssignmentType.setName( addAssignmentTypeText.getText() );
					currentAssignmentType.setDefaultEvacuees( Integer.parseInt( txtDefaultEvacuees.getText() ) );
					lstAssignmentType.repaint();
				} catch( NumberFormatException ex ) {
					JOptionPane.showMessageDialog( parent, loc.getString( "gui.AssignmentEditor.Error.IllegalNumberOfEvacuees" ), loc.getString( "gui.General.Error" ), JOptionPane.ERROR_MESSAGE );
				} catch( IllegalArgumentException ex ) {// empty text field, exception is localized
					JOptionPane.showMessageDialog( parent, ex.getMessage(), loc.getString( "gui.General.Error" ), JOptionPane.ERROR_MESSAGE );
				}
			}
		}
	};
	private ActionListener aclAssignmentDelete = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			if( currentAssignment != null ) {
				if( distributionTable.isEditing() ) {
					distributionTable.getCellEditor().cancelCellEditing();
					distributionTable.clearSelection();
				}

				myProject.deleteAssignment( currentAssignment );
				assignmentSelector.displayAssignments( myProject.getProject() );
			}
		}
	};
	private ActionListener aclAssignmentTypeDelete = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			if( currentAssignment != null && currentAssignmentType != null ) {
				if( distributionTable.isEditing() ) {
					distributionTable.getCellEditor().cancelCellEditing();
					distributionTable.clearSelection();
				}

				currentAssignment.deleteAssignmentType( currentAssignmentType );
				assignmentSelector.displayAssignments( myProject.getProject() );
			}
		}
	};
	private final KeyListener kylEvacuees = new KeyAdapter() {
		@Override
		public void keyTyped( KeyEvent e ) {
			switch( e.getKeyChar() ) {
				case KeyEvent.VK_0:
				case KeyEvent.VK_1:
				case KeyEvent.VK_2:
				case KeyEvent.VK_3:
				case KeyEvent.VK_4:
				case KeyEvent.VK_5:
				case KeyEvent.VK_6:
				case KeyEvent.VK_7:
				case KeyEvent.VK_8:
				case KeyEvent.VK_9:
//					changed = true;
					return;
				default:
					e.consume();
			}
		}
	};

	/*****************************************************************************
	 *                                                                           *
	 * Helper methods                                                            *
	 *                                                                           *
	 ****************************************************************************/
	/**
	 * Displays the currently selected distribution on the chart.
	 */
	public void drawCharts() {
		int[] sel = distributionTable.getSelectedRows();
		XYSeriesCollection c = new XYSeriesCollection();
		int nodes = 150;
		for( int i = 0; i < sel.length; i++ ) {
			Object b = distributionTable.getModel().getValueAt( sel[i], 0 );
			XYSeries dataSeries = new XYSeries( b.toString() );
			Distribution<Double> distribution = params.get( sel[i] ).getDistribution();
			double distance = (distribution.getMax() - distribution.getMin()) / (nodes - 1);
			for( int j = 0; j < nodes; j++ ) {
				double pos = distribution.getMin() + j * distance;
				dataSeries.add( pos, distribution.getDensityAt( pos ) );
			}
			c.addSeries( dataSeries );
		}
		JFreeChart newChart = ChartFactory.createXYLineChart( loc.getStringWithoutPrefix( "gui.AssignmentEditor.plot.Title" ),
						loc.getStringWithoutPrefix( "gui.AssignmentEditor.plot.Values" ), // X-Axis label
						loc.getStringWithoutPrefix( "gui.AssignmentEditor.plot.Probability" ), // Y-Axis label
						c, // Dataset
						PlotOrientation.VERTICAL,
						false, true, false // Show legend, tooltips, url
						);

		chartPanel.setChart( newChart );
	}

	/**
	 * Sets the currently selected assignment as current.
	 */
	private void setCurrent() {
		if( currentAssignment != null ) {
			myProject.setCurrentAssignment( currentAssignment );
			// Repaint Assignment list to show the change
			lstAssignment.repaint();
		}
	}

	/**
	 * Updates the distribution assigned to a row in the entry model to the
	 * currently set distribution in the table.
	 * @param row the row
	 */
	private void updateDistributions( int row ) {
		switch( row ) {
			case 0:
				currentAssignmentType.setDiameter( params.get( 0 ).getDistribution() );
				break;
			case 1:
				currentAssignmentType.setAge( params.get( 1 ).getDistribution() );
				break;
			case 2:
				currentAssignmentType.setFamiliarity( params.get( 2 ).getDistribution() );
				break;
			case 3:
				currentAssignmentType.setPanic( params.get( 3 ).getDistribution() );
				break;
			case 4:
				currentAssignmentType.setDecisiveness( params.get( 4 ).getDistribution() );
				break;
			case 5:
				currentAssignmentType.setReaction( params.get( 5 ).getDistribution() );
				break;
			default:
				break;
		}
	}

	/*****************************************************************************
	 *                                                                           *
	 * Models and renderer for control elements.                                 *
	 *                                                                           *
	 ****************************************************************************/
	/**
	 * A utility class encapsulation distributions. Thus parameters of all
	 * distributions can be changed by an index and a name of the distribution
	 * can be returned. This is used in the table, where rows belong to parameters.
	 */
	private class DistributionEntry<E extends Number> {
		private String name;
		private Distribution<E> distribution;

		DistributionEntry( String name, Distribution<E> distribution ) {
			this.name = name;
			this.distribution = distribution;
		}

		public String getName() {
			return name;
		}

		public void setName( String name ) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		public Distribution<E> getDistribution() {
			return distribution;
		}

		public String getDistributionName() {
			if( distribution instanceof UniformDistribution ) {
				return loc.getString( "gui.AssignmentEditor.Distribution.UniformDistribution" );
			} else if( distribution instanceof NormalDistribution ) {
				return loc.getString( "gui.AssignmentEditor.Distribution.NormalDistribution" );
			} else if( distribution instanceof ErlangDistribution ) {
				return loc.getString( "gui.AssignmentEditor.Distribution.ErlangDistribution" );
			} else if( distribution instanceof ExponentialDistribution ) {
				return loc.getString( "gui.AssignmentEditor.Distribution.ExponentialDistribution" );
			} else if( distribution instanceof HyperExponentialDistribution ) {
				return loc.getString( "gui.AssignmentEditor.Distribution.HyperExponentialDistribution" );
			} else {
				throw new AssertionError( "Not supported yet." );
			}
		}

		public void setDistribution( Distribution<E> distribution ) {
			this.distribution = distribution;
		}

		public boolean useParam( int id ) {
			if( distribution instanceof UniformDistribution ) {
				return id >= 1 && id <= 2;
			} else if( distribution instanceof NormalDistribution ) {
				return id >= 1 && id <= 4;
			} else if( distribution instanceof ErlangDistribution ) {
				return id >= 1 && id <= 4;
			} else if( distribution instanceof ExponentialDistribution ) {
				return id >= 1 && id <= 3;
			} else if( distribution instanceof HyperExponentialDistribution ) {
				return id >= 1 && id <= 5;
			} else {
				throw new AssertionError( "Not supported yet." );
			}
		}

		public void setParam( int id, double val ) {
			if( distribution instanceof UniformDistribution ) {
				switch( id ) {
					case 1:
						((UniformDistribution) distribution).setMin( val );
						break;
					case 2:
						((UniformDistribution) distribution).setMax( val );
						break;
					default:
						throw new java.lang.IllegalArgumentException( loc.getString( "gui.AssignmentEditor.Error.DistributionParameter.Uniform" ) );
				}
			} else if( distribution instanceof NormalDistribution ) {
				switch( id ) {
					case 1:
						((NormalDistribution) distribution).setMin( val );
						break;
					case 2:
						((NormalDistribution) distribution).setMax( val );
						break;
					case 3:
						((NormalDistribution) distribution).setExpectedValue( val );
						break;
					case 4:
						((NormalDistribution) distribution).setVariance( val );
						break;
					default:
						throw new java.lang.IllegalArgumentException( loc.getString( "gui.AssignmentEditor.Error.DistributionParameter.Normal" ) );
				}
			} else if( distribution instanceof ErlangDistribution ) {
				switch( id ) {
					case 1:
						((ErlangDistribution) distribution).setMin( val );
						break;
					case 2:
						((ErlangDistribution) distribution).setMax( val );
						break;
					case 3:
						((ErlangDistribution) distribution).setLambda( val );
						break;
					case 4:
						((ErlangDistribution) distribution).setK( (int) val );
						break;
					default:
						throw new java.lang.IllegalArgumentException( loc.getString( "gui.AssignmentEditor.Error.DistributionParameter.Erlang" ) );
				}
			} else if( distribution instanceof ExponentialDistribution ) {
				switch( id ) {
					case 1:
						((ExponentialDistribution) distribution).setMin( val );
						break;
					case 2:
						((ExponentialDistribution) distribution).setMax( val );
						break;
					case 3:
						((ExponentialDistribution) distribution).setLambda( val );
						break;
					default:
						throw new java.lang.IllegalArgumentException( loc.getString( "gui.AssignmentEditor.Error.DistributionParameter.Exponential") );
				}
			} else if( distribution instanceof HyperExponentialDistribution ) {
				switch( id ) {
					case 1:
						((HyperExponentialDistribution) distribution).setMin( val );
						break;
					case 2:
						((HyperExponentialDistribution) distribution).setMax( val );
						break;
					case 3:
						((HyperExponentialDistribution) distribution).setLambda1( val );
						break;
					case 4:
						((HyperExponentialDistribution) distribution).setLambda2( val );
						break;
					default:
						throw new java.lang.IllegalArgumentException( loc.getString( "gui.AssignmentEditor.Error.DistributionParameter.Hyperexponential") );
				}
			} else {
				throw new UnsupportedOperationException( "Not supported yet." );
			}
		}

		public double getParam( int id ) {
			if( distribution instanceof UniformDistribution ) {
				switch( id ) {
					case 1:
						return ((UniformDistribution) distribution).getMin();
					case 2:
						return ((UniformDistribution) distribution).getMax();
					default:
						return 0;
				}
			} else if( distribution instanceof NormalDistribution ) {
				switch( id ) {
					case 1:
						return ((NormalDistribution) distribution).getMin();
					case 2:
						return ((NormalDistribution) distribution).getMax();
					case 3:
						return ((NormalDistribution) distribution).getExpectedValue();
					case 4:
						return ((NormalDistribution) distribution).getVariance();
					default:
						return 0;
				}
			} else if( distribution instanceof ErlangDistribution ) {
				switch( id ) {
					case 1:
						return ((ErlangDistribution) distribution).getMin();
					case 2:
						return ((ErlangDistribution) distribution).getMax();
					case 3:
						return ((ErlangDistribution) distribution).getLambda();
					case 4:
						return ((ErlangDistribution) distribution).getK();
					default:
						return 0;
				}
			} else if( distribution instanceof ExponentialDistribution ) {
				switch( id ) {
					case 1:
						return ((ExponentialDistribution) distribution).getMin();
					case 2:
						return ((ExponentialDistribution) distribution).getMax();
					case 3:
						return ((ExponentialDistribution) distribution).getLambda();
					default:
						return 0;
				}
			} else if( distribution instanceof HyperExponentialDistribution ) {
				switch( id ) {
					case 1:
						return ((HyperExponentialDistribution) distribution).getMin();
					case 2:
						return ((HyperExponentialDistribution) distribution).getMax();
					case 3:
						return ((HyperExponentialDistribution) distribution).getLambda1();
					case 4:
						return ((HyperExponentialDistribution) distribution).getLambda2();
					default:
						return 0;
				}
			} else {
				throw new UnsupportedOperationException( "Not supported yet." );
			}
		}
	}

	/**
	 * A model for the table of distributions.
	 */
	private class AssignmentTableModel extends AbstractTableModel {
		@Override
		public Class<String> getColumnClass( int column ) {
			switch( column ) {
				case COL_NAME:
					return String.class;
				case COL_DISTRIBUTION:
					return String.class;
				case COL_PARAM1:
					return String.class;
				case COL_PARAM2:
					return String.class;
				case COL_PARAM3:
					return String.class;
				case COL_PARAM4:
					return String.class;
				default:
					throw new AssertionError( column );
			}
		}

		@Override
		public String getColumnName( int column ) {
			switch( column ) {
				case COL_NAME:
					return "";
				case COL_DISTRIBUTION:
					return loc.getStringWithoutPrefix( "gui.AssignmentEditor.label.ProbabilityDistribution" ) ;
				case COL_PARAM1:
					return loc.getStringWithoutPrefix( "gui.AssignmentEditor.label.Minimum" ) ;
				case COL_PARAM2:
					return loc.getStringWithoutPrefix( "gui.AssignmentEditor.label.Maximum" ) ;
				case COL_PARAM3:
					return loc.getStringWithoutPrefix( "gui.AssignmentEditor.label.ExpectedValue" ) ;
				case COL_PARAM4:
					return loc.getStringWithoutPrefix( "gui.AssignmentEditor.label.Variance" ) ;
				default:
					return null;
			}
		}

		@Override
		public int getRowCount() {
			return params.size();
		}

		@Override
		public int getColumnCount() {
			return COL_PARAM4 + 1;
		}

		@Override
		public Object getValueAt( int row, int column ) {
			switch( column ) {
				case COL_NAME:
					return params.get( row ).getName();
				case COL_DISTRIBUTION:
					return params.get( row ).getDistributionName();
				case COL_PARAM1:
					return LocalizationManager.getManager().getFloatConverter().format( params.get( row ).getParam( 1 ) );
				case COL_PARAM2:
					return LocalizationManager.getManager().getFloatConverter().format( params.get( row ).getParam( 2 ) );
				case COL_PARAM3:
					return LocalizationManager.getManager().getFloatConverter().format( params.get( row ).getParam( 3 ) );
				case COL_PARAM4:
					return LocalizationManager.getManager().getFloatConverter().format( params.get( row ).getParam( 4 ) );
				default:
					return null;
			}
		}

		@Override
		public boolean isCellEditable( int row, int column ) {
			switch( column ) {
				case COL_NAME:
					return false;
				case COL_PARAM3:
					return params.get( row ).useParam( 3 );
				case COL_PARAM4:
					return params.get( row ).useParam( 4 );
				default:
					return true;
			}
		}

		@Override
		public void setValueAt( Object value, int row, int column ) {
			try {
				switch( column ) {
					case COL_NAME:
						params.get( row ).setName( (String) value );
						break;
					case COL_DISTRIBUTION:
						switch( distributions.getSelectedIndex() ) {
							case DIST_NORMAL:
								if( !(params.get( row ).getDistribution() instanceof NormalDistribution) ) {
									params.get( row ).setDistribution( new NormalDistribution() );
									updateDistributions( row );
								}
								break;
							case DIST_UNIFORM:
								if( !(params.get( row ).getDistribution() instanceof UniformDistribution) ) {
									params.get( row ).setDistribution( new UniformDistribution() );
									updateDistributions( row );
								}
								break;
							case DIST_EXPONENTIAL:
								if( !(params.get( row ).getDistribution() instanceof ExponentialDistribution) ) {
									params.get( row ).setDistribution( new ExponentialDistribution() );
									updateDistributions( row );
								}
								break;
							case DIST_ERLANG:
								if( !(params.get( row ).getDistribution() instanceof ErlangDistribution) ) {
									params.get( row ).setDistribution( new ErlangDistribution() );
									updateDistributions( row );
								}
								break;
							case DIST_HYPEREXPONENTIAL:
								if( !(params.get( row ).getDistribution() instanceof HyperExponentialDistribution) ) {
									params.get( row ).setDistribution( new HyperExponentialDistribution() );
									updateDistributions( row );
								}
								break;
							default:

						}
						drawCharts();
						break;
					case COL_PARAM1:
						params.get( row ).setParam( 1, LocalizationManager.getManager().getFloatConverter().parse( (String) value ).doubleValue() );
						drawCharts();
						break;
					case COL_PARAM2:
						params.get( row ).setParam( 2, LocalizationManager.getManager().getFloatConverter().parse( (String) value ).doubleValue() );
						drawCharts();
						break;
					case COL_PARAM3:
						params.get( row ).setParam( 3, LocalizationManager.getManager().getFloatConverter().parse( (String) value ).doubleValue() );
						drawCharts();
						break;
					case COL_PARAM4:
						params.get( row ).setParam( 4, LocalizationManager.getManager().getFloatConverter().parse( (String) value ).doubleValue() );
						drawCharts();
						break;
					default:
						throw new UnsupportedOperationException( "Not supported yet." );
				}
			} catch( java.text.ParseException | java.lang.IllegalArgumentException e ) {
				// Occurs, if a string not starting with a number is entered.
			}
			distributionTable.repaint(); // Necessary for (de)activating columns
		}
	}

	/**
	 * A model for the assignment type list. It enables and disables the
	 * table and charts if at least one assignment type is present in the
	 * current assignment.
	 */
	private class AssignmentTypeListModel extends DefaultListModel<AssignmentType> {

		public void displayAssignmentTypes() {
			removeAllElements();
			if( currentAssignment != null ) {
				for( AssignmentType at : currentAssignment.getAssignmentTypes() ) {
					addElement( at );
				}
			}

			distributionTable.setEnabled( getSize() > 0 );
			chartPanel.setEnabled( getSize() > 0 );
		}
	}

	/**
	 * A model for the assignment list. Automatically selects the first type, if
	 * present and resets components if necessary.
	 */
	private class AssignmentListModel extends DefaultListModel<Assignment> {
		public void displayAssignments( Project p ) {
			removeAllElements();
			if( p != null ) {
				for( Assignment a : p.getAssignments() ) {
					addElement( a );
				}
				if( getSize() > 0 ) {
					lstAssignment.setSelectedIndex( 0 );
				} else {
					currentAssignment = null;
				}
			}

			// The test fields will be automatically reinitialized, but only
			// if there still is any assignment to display. If that is not the
			// case we must clear the fields manually
			if( getSize() == 0 ) {
				currentAssignment = null;
				currentAssignmentType = null;
				addAssignmentText.setText( "" );
				addAssignmentTypeText.setText( "" );
				txtDefaultEvacuees.setText( "" );
				assignmentTypeSelector.displayAssignmentTypes();

				distributionTable.setEnabled( false );
				chartPanel.setEnabled( false );
			}
		}
	}

	/**
	 * Handles the case of multiple selections in the assignment list, which means
	 * that the first of the assignments is set to be current assignment.
	 */
	private class AssignmentListSelectionModel extends DefaultListSelectionModel {
		@Override
		public void setSelectionInterval( int index0, int index1 ) {
			super.setSelectionInterval( index0, index1 );
			currentAssignment = assignmentSelector.elementAt( getMinSelectionIndex() );
			addAssignmentText.setText( currentAssignment.getName() );
			assignmentTypeSelector.displayAssignmentTypes();

			// Display first entry if possible
			if( assignmentTypeSelector.getSize() > 0 )
				lstAssignmentType.setSelectedIndex( 0 );
			else {
				currentAssignmentType = null;
				addAssignmentTypeText.setText( "" );
				txtDefaultEvacuees.setText( "" );
			}
		}
	}

	/**
	 * This class can display [@link Assignment} objects in a {@code JList}. The
	 * currently selected assignment is printed bold.
	 */
	private class AssignmentListRenderer extends ComboBoxRenderer<Assignment> {
		final private Font boldFont = getFont().deriveFont( Font.BOLD );
		final private Font normalFont = getFont().deriveFont( Font.PLAIN );

		@Override
		public Component getListCellRendererComponent( JList<? extends Assignment> list, Assignment value, int index, boolean isSelected, boolean cellHasFocus ) {
			super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

			if( value != null ) {
				Assignment a = value;
				setFont( a == myProject.getProject().getCurrentAssignment() ? boldFont : normalFont );
				setText( a.getName() );
			}
			return this;
		}
	}

	/**
	 * A model of {@link AssignmentType} objects in a {@code JList}. Automatically
	 * updates the table and plot if an assignment type is selected.
	 */
	@SuppressWarnings("serial")
	private class AssignmentTypeListSelectionModel extends DefaultListSelectionModel {
		@Override
		public void setSelectionInterval( int index0, int index1 ) {
			super.setSelectionInterval( index0, index1 );

			currentAssignmentType = assignmentTypeSelector.elementAt( getMinSelectionIndex() );

			addAssignmentTypeText.setText( currentAssignmentType.getName() );
			txtDefaultEvacuees.setText( Integer.toString( currentAssignmentType.getDefaultEvacuees() ) );
			params.get( 0 ).setDistribution( currentAssignmentType.getDiameter() );
			params.get( 1 ).setDistribution( currentAssignmentType.getAge() );
			params.get( 2 ).setDistribution( currentAssignmentType.getFamiliarity() );
			params.get( 3 ).setDistribution( currentAssignmentType.getPanic() );
			params.get( 4 ).setDistribution( currentAssignmentType.getDecisiveness() );
			params.get( 5 ).setDistribution( currentAssignmentType.getReaction() );
			distributionTable.repaint();
		}
	}

	/**
	 * Displays {@link AssignmentType} objects in a  {@code JList}. They are
	 * represented by their name.
	 */
	@SuppressWarnings("serial")
	private class AssignmentTypeListRenderer extends ComboBoxRenderer<AssignmentType> {
		@Override
		public Component getListCellRendererComponent( JList<? extends AssignmentType> list, AssignmentType value, int index, boolean isSelected, boolean cellHasFocus ) {
			super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
			if( value != null ) {
				setText( value.getName() );
			}
			return this;
		}
	}

	/**
	 * A {@code TableCellEditor} that immediately selects the whole text when it is activated.
	 */
	private class SelectingCellEditor extends DefaultCellEditor {
		public SelectingCellEditor() {
			super( new JTextField() );
			setClickCountToStart( 1 );
			getComponent().addFocusListener( new FocusAdapter() {
				@Override
				public void focusGained( FocusEvent e ) {
					((JTextField) e.getComponent()).selectAll();
				}
			} );
		}
	}

	/**
	 * A {@code TableCellRenderer} that renders non-editable cells that are
	 * especially marked.
	 */
	@SuppressWarnings("serial")
	public class InactiveRenderer extends DefaultTableCellRenderer {
		private final Color inactiveColor = Color.GRAY;
		private Color activeColorSelected = null;
		private Color activeColorUnselected = null;

		@Override
		public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
			Component normal;
			if( activeColorSelected == null ) {
				normal = super.getTableCellRendererComponent( table, value, false, false, row, column );
				activeColorUnselected = normal.getForeground();
				normal = super.getTableCellRendererComponent( table, value, true, true, row, column );
				activeColorSelected = normal.getForeground();
			}
			normal = super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
			normal.setForeground( tablemodel.isCellEditable( row, column ) ? isSelected ? activeColorSelected : activeColorUnselected : inactiveColor );
			return this;
		}
	}
}
