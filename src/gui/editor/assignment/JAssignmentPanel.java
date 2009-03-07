/*
 * DistributionTab.java
 * Created on 16. Dezember 2007, 22:48
 */
package gui.editor.assignment;

import ds.Project;
import ds.z.Assignment;
import ds.z.AssignmentType;
import gui.JEditor;
import gui.components.ComboBoxRenderer;
import gui.components.framework.Button;
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
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import localization.Localization;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import util.random.distributions.Distribution;
import util.random.distributions.ErlangDistribution;
import util.random.distributions.NormalDistribution;
import util.random.distributions.UniformDistribution;
import util.random.distributions.ExponentialDistribution;
import util.random.distributions.HyperExponentialDistribution;

/**
 * A panel containing all assignments and assignment type of a
 * {@link ds.Project} of an evacuation. Basically the assignmant works as a
 * box that contains some types (that can describe different types of people).
 * All of the parameters of the different assignment types can be set
 * as probability distributions. Currently
 * {@link util.random.distributions.NormalDistribution},
 * {@link util.random.distributions.UniformDistribution},
 * {@link util.random.distributions.ExponentialDistribution},
 * {@link util.random.distributions.ErlangDistribution} and
 * {@link util.random.distributions.HyperExponentialDistribution} are supported.
 * @author Jan-Philipp Kappmeier
 */
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
	Project myProject;
	private static final Localization loc = Localization.getInstance();	// Objekte für die Listen
	private Assignment currentAssignment;
	private AssignmentType currentAssignmentType;
	private JTextField addText1;
	private JTextField addText2;
	private JList lstAssignment;
	private AssignmentListModel assignmentSelector;
	private JList lstAssignmentType;
	private AssignmentTypeListModel assignmentTypeSelector;
	private JTextField txtDefaultEvacuees;
	private ArrayList<DistributionEntry> params;
	private AssignmentTableModel tablemodel;
	private JTable distributionTable;
	private ChartPanel chartPanel;
	private JComboBox distributions;

	public JAssignmentPanel( JDialog parent, Project p ) {
		super();
		myProject = p;
		params = new ArrayList<DistributionEntry>();
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
		tablemodel.addTableModelListener( new TableModelListener() {

			public void tableChanged( TableModelEvent e ) {
				drawCharts();
			}
		} );
		distributionTable = new JTable( tablemodel );
		distributionTable.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		distributionTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {

			public void valueChanged( ListSelectionEvent e ) {
				drawCharts();
			}
		} );

		distributions = new JComboBox();
		distributions.addItem( loc.getString( "gui.editor.assignment.normalDistribution" ) );
		distributions.addItem( loc.getString( "gui.editor.assignment.uniformDistribution" ) );
		distributions.addItem( loc.getString( "gui.editor.assignment.exponentialDistribution" ) );
		distributions.addItem( loc.getString( "gui.editor.assignment.erlangDistribution" ) );
		distributions.addItem( loc.getString( "gui.editor.assignment.hyperExponentialDistribution" ) );
		distributionTable.getColumnModel().getColumn( COL_DISTRIBUTION ).setCellEditor( new DefaultCellEditor( distributions ) );
		distributionTable.getColumnModel().getColumn( COL_PARAM1 ).setCellEditor( new SelectingCellEditor() );
		distributionTable.getColumnModel().getColumn( COL_PARAM2 ).setCellEditor( new SelectingCellEditor() );
		distributionTable.getColumnModel().getColumn( COL_PARAM3 ).setCellEditor( new SelectingCellEditor() );
		distributionTable.getColumnModel().getColumn( COL_PARAM4 ).setCellEditor( new SelectingCellEditor() );
		distributionTable.getColumnModel().getColumn( COL_PARAM3 ).setCellRenderer( new InactiveRenderer() );
		distributionTable.getColumnModel().getColumn( COL_PARAM4 ).setCellRenderer( new InactiveRenderer() );

		JPanel rightPanel = getRightPanel();
		add( rightPanel, BorderLayout.CENTER );
		JPanel leftPanel = getLeftPanel();
		leftPanel.setMaximumSize( new Dimension( 200, 0 ) );
		add( leftPanel, BorderLayout.WEST );
		assignmentSelector.displayAssignments( myProject );
	}

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

		leftPanel.add( new JLabel( loc.getString( "gui.editor.assignment.JAssignmentPanel.labelAssignments" ) ), "1, " + row++ );
		row++;

		assignmentSelector = new AssignmentListModel();
		lstAssignment = new JList();
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

		leftPanel.add( new JLabel( "Name" ), "1, " + row++ );
		row++;

		addText1 = new JTextField();
		leftPanel.add( addText1, "1, " + row++ );
		row++;

		JButton saveChangesButton = Button.newButton( loc.getString( "gui.editor.assignment.JAssignmentPanel.buttonAssignmentSave" ), aclAssignmentSaveChanges );
		leftPanel.add( saveChangesButton, "1, " + row++ );
		row++;

		row += 4;

		JButton changeButton = Button.newButton( loc.getString( "gui.editor.assignment.JAssignmentPanel.buttonAdd" ), aclAddAssignment );
		leftPanel.add( changeButton, "1, " + row++ );
		row++;

		JButton deleteButton = Button.newButton( loc.getString( "gui.editor.assignment.JAssignmentPanel.buttonDelete" ), aclDeleteAssignment );
		leftPanel.add( deleteButton, "1, " + row++ );
		row++;

		// Rechter Teil
		row = 1;
		leftPanel.add( new JLabel( loc.getString( "gui.editor.assignment.JAssignmentPanel.labelAssignmentTypes" ) ), "3, " + row++ );
		row++;

		assignmentTypeSelector = new AssignmentTypeListModel();
		lstAssignmentType = new JList();
		lstAssignmentType.setModel( assignmentTypeSelector );
		lstAssignmentType.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		lstAssignmentType.setCellRenderer( new AssignmentTypeListRenderer() );
		lstAssignmentType.setSelectionModel( new AssignmentTypeListSelectionModel() );

		leftPanel.add( new JScrollPane( lstAssignmentType ), "3, " + row++ );
		row++;

		leftPanel.add( new JLabel( "Name" ), "3, " + row++ );
		row++;

		addText2 = new JTextField();
		leftPanel.add( addText2, "3, " + row++ );
		row++;

		leftPanel.add( new JLabel( loc.getString( "gui.editor.assignment.JAssignmentPanel.labelDefaultEvacuees" ) ), "3, " + row++ );
		row++;
		txtDefaultEvacuees = new JTextField();
		txtDefaultEvacuees.addKeyListener( kylEvacuees );
		leftPanel.add( txtDefaultEvacuees, "3, " + row++ );
		row++;

		JButton addButton = Button.newButton( loc.getString( "gui.editor.assignment.JAssignmentPanel.buttonAssignmentSave" ), aclAssignmentTypeSaveChanges );
		leftPanel.add( addButton, "3, " + row++ );
		row++;

		changeButton = Button.newButton( loc.getString( "gui.editor.assignment.JAssignmentPanel.buttonAdd" ), aclAddAssignmentType );
		leftPanel.add( changeButton, "3, " + row++ );
		row++;

		deleteButton = Button.newButton( loc.getString( "gui.editor.assignment.JAssignmentPanel.buttonDelete" ), aclDeleteAssignmentType );
		leftPanel.add( deleteButton, "3, " + row++ );
		row++;

		return leftPanel;
	}

	private JPanel getRightPanel() {
		JPanel rightPanel = new JPanel( new GridBagLayout() );

		JPanel tableContainer = new JPanel( new BorderLayout() );
		tableContainer.add( distributionTable.getTableHeader(), BorderLayout.PAGE_START );
		tableContainer.add( distributionTable, BorderLayout.CENTER );

		rightPanel.add( tableContainer, new GridBagConstraints( 0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
						GridBagConstraints.BOTH, new Insets( 16, 0, 0, 16 ), 0, 0 ) );

		XYSeriesCollection c = new XYSeriesCollection();
		JFreeChart chart = ChartFactory.createXYLineChart( "Verteilungen", // Title
						"Werte", // X-Axis label
						"Wahrscheinlichkeit", // Y-Axis label
						c, // Dataset
						PlotOrientation.VERTICAL,
						false, true, false // Show legend
						);

		chartPanel = new ChartPanel( chart );
		rightPanel.add( chartPanel, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 16, 0, 0, 16 ), 0, 0 ) );

		rightPanel.add( chartPanel, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 16, 0, 0, 16 ), 0, 0 ) );

		return rightPanel;
	}

	private void initDistributions() {
		loc.setPrefix( "ds.z.AssignmentType." );
		params.add( new DistributionEntry( loc.getString( "diameter" ), new NormalDistribution() ) );
		params.add( new DistributionEntry( loc.getString( "age" ), new UniformDistribution() ) );
		params.add( new DistributionEntry( loc.getString( "familiarity" ), new NormalDistribution() ) );
		params.add( new DistributionEntry( loc.getString( "panic" ), new NormalDistribution() ) );
		params.add( new DistributionEntry( loc.getString( "decisiveness" ), new NormalDistribution() ) );
		loc.setPrefix( "" );
	}
	/*****************************************************************************
	 *                                                                           *
	 * Action listener                                                           *
	 *                                                                           *
	 ****************************************************************************/
	ActionListener aclAddAssignment = new ActionListener() {

		public void actionPerformed( ActionEvent e ) {
			try {
				Assignment add = new Assignment( addText1.getText() );
				myProject.addAssignment( add );
				addText1.setText( "" );
				assignmentSelector.displayAssignments( myProject );
				lstAssignment.setSelectedValue( add, true );
			} catch( IllegalArgumentException ex ) {
				JEditor.showErrorMessage( "Fehler", ex.getLocalizedMessage() );
			}
		}
	};
	ActionListener aclAddAssignmentType = new ActionListener() {

		public void actionPerformed( ActionEvent e ) {
			if( currentAssignment != null ) {
				try {
					AssignmentType at = new AssignmentType( addText2.getText(),
									getDefaultAssignmentTypeNormal( "diameter" ),
									getDefaultAssignmentTypeNormal( "age" ),
									getDefaultAssignmentTypeNormal( "familiarity" ),
									getDefaultAssignmentTypeNormal( "panic" ),
									getDefaultAssignmentTypeNormal( "decisiveness" ) );
					at.setDefaultEvacuees( Integer.parseInt( txtDefaultEvacuees.getText() ) );
					currentAssignment.addAssignmentType( at );
					addText2.setText( "" );
					assignmentTypeSelector.displayAssignmentTypes();
					lstAssignmentType.setSelectedValue( at, true );
				} catch( NumberFormatException ex ) {
					JEditor.showErrorMessage( "Fehler", loc.getString( "gui.error.NonParsableNumber" ) );
				} catch( IllegalArgumentException ex ) {
					JEditor.showErrorMessage( "Fehler", ex.getLocalizedMessage() );
				}
			}
		}
	};
	ActionListener aclAssignmentSaveChanges = new ActionListener() {

		public void actionPerformed( ActionEvent e ) {
			try {
				currentAssignment.setName( addText1.getText() );
				lstAssignment.repaint();
			} catch( IllegalArgumentException ex ) {
				JEditor.showErrorMessage( "Fehler", ex.getLocalizedMessage() );
			}
		}
	};
	ActionListener aclAssignmentTypeSaveChanges = new ActionListener() {

		public void actionPerformed( ActionEvent e ) {
			if( currentAssignmentType != null ) {
				try {
					currentAssignmentType.setName( addText2.getText() );
					currentAssignmentType.setDefaultEvacuees( Integer.parseInt( txtDefaultEvacuees.getText() ) );
					lstAssignmentType.repaint();
				} catch( NumberFormatException ex ) {
					JEditor.showErrorMessage( "Fehler", loc.getString( "gui.error.NonParsableNumber" ) );
				} catch( IllegalArgumentException ex ) {
					JEditor.showErrorMessage( "Fehler", ex.getLocalizedMessage() );
				}
			}
		}
	};
	ActionListener aclDeleteAssignment = new ActionListener() {

		public void actionPerformed( ActionEvent e ) {
			if( currentAssignment != null ) {
				if( distributionTable.isEditing() ) {
					distributionTable.getCellEditor().cancelCellEditing();
					distributionTable.clearSelection();
				}

				myProject.deleteAssignment( currentAssignment );
				assignmentSelector.displayAssignments( myProject );
			}
		}
	};
	ActionListener aclDeleteAssignmentType = new ActionListener() {

		public void actionPerformed( ActionEvent e ) {
			if( currentAssignment != null && currentAssignmentType != null ) {
				if( distributionTable.isEditing() ) {
					distributionTable.getCellEditor().cancelCellEditing();
					distributionTable.clearSelection();
				}

				currentAssignment.deleteAssignmentType( currentAssignmentType );
				assignmentSelector.displayAssignments( myProject );
			}
		}
	};
	KeyListener kylEvacuees = new KeyListener() {

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

		public void keyPressed( KeyEvent e ) {
		}

		public void keyReleased( KeyEvent e ) {
		}
	};

	/*****************************************************************************
	 *                                                                           *
	 * Helper methods                                                            *
	 *                                                                           *
	 ****************************************************************************/
	public void drawCharts() {
		int[] sel = distributionTable.getSelectedRows();
		XYSeriesCollection c = new XYSeriesCollection();
		JFreeChart chart = null;
		int nodes = 100;
		for( int i = 0; i < sel.length; i++ ) {
			XYSeries a = new XYSeries( "Stat" + i );
			Distribution distribution = params.get( sel[i] ).getDistribution();
			double distance = (distribution.getMax() - distribution.getMin()) / (nodes - 1);
			for( int j = 0; j < nodes; j++ ) {
				double pos = distribution.getMin() + j * distance;
				a.add( pos, distribution.getDensityAt( pos ) );
			}
			c.addSeries( a );
		}
		chart = ChartFactory.createXYLineChart( "Verteilungen", // Title
						"Werte", // X-Axis label
						"Wahrscheinlichkeit", // Y-Axis label
						c, // Dataset
						PlotOrientation.VERTICAL,
						false, true, false // Show legend
						);

		chartPanel.setChart( chart );
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
			default:
				break;
		}
	}

	/**
	 * Returns default values for a {@link util.NormalDistribution} distribution
	 * for a specified parameter.
	 * @param type the parameter
	 * @return the normal distribution
	 */
	private NormalDistribution getDefaultAssignmentTypeNormal( String type ) {
		if( type.equals( "diameter" ) ) {
			return new NormalDistribution( 0.5, 1.0, 0.4, 0.7 );
		}
		if( type.equals( "age" ) ) {
			return new NormalDistribution( 16, 1, 14, 18 );
		}
		if( type.equals( "familiarity" ) ) {
			return new NormalDistribution( 0.8, 1.0, 0.7, 1.0 );
		}
		if( type.equals( "panic" ) ) {
			return new NormalDistribution( 0.5, 1.0, 0.0, 1.0 );
		}
		if( type.equals( "decisiveness" ) ) {
			return new NormalDistribution( 0.3, 1.0, 0.0, 1.0 );
		}
		return null;
	}

	/*****************************************************************************
	 *                                                                           *
	 * Models and renderer for control elements.                                 *
	 *                                                                           *
	 ****************************************************************************/
	private class DistributionEntry {

		private String name;
		private Distribution distribution;

		DistributionEntry( String name, Distribution distribution ) {
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

		public Distribution getDistribution() {
			return distribution;
		}

		public String getDistributionName() {
			if( distribution instanceof UniformDistribution ) {
				return loc.getString( "gui.editor.assignment.uniformDistribution" );
			} else if( distribution instanceof NormalDistribution ) {
				return loc.getString( "gui.editor.assignment.normalDistribution" );
			} else if( distribution instanceof ErlangDistribution ) {
				return loc.getString( "gui.editor.assignment.erlangDistribution" );
			} else if( distribution instanceof ExponentialDistribution ) {
				return loc.getString( "gui.editor.assignment.exponentialDistribution" );
			} else if( distribution instanceof HyperExponentialDistribution ) {
				return loc.getString( "gui.editor.assignment.hyperExponentialDistribution" );
			} else {
				return ("Unknown distribution");
			}
		}

		public void setDistribution( Distribution distribution ) {
			this.distribution = distribution;
		}

		public boolean useParam( int id ) {
			if( distribution instanceof UniformDistribution ) {
				return id >= 1 && id <= 2 ? true : false;
			} else if( distribution instanceof NormalDistribution ) {
				return id >= 1 && id <= 4 ? true : false;
			} else if( distribution instanceof ErlangDistribution ) {
				return id >= 1 && id <= 4 ? true : false;
			} else if( distribution instanceof ExponentialDistribution ) {
				return id >= 1 && id <= 3 ? true : false;
			} else if( distribution instanceof HyperExponentialDistribution ) {
				return id >= 1 && id <= 5 ? true : false;
			} else {
				throw new java.lang.IllegalArgumentException( "Unknown distribution" );
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
						throw new java.lang.IllegalArgumentException( "Wrong parameter for uniform distribution" );
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
						throw new java.lang.IllegalArgumentException( "Wrong parameter for normal distribution" );
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
						throw new java.lang.IllegalArgumentException( "Wrong parameter for Erlang distribution" );
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
						throw new java.lang.IllegalArgumentException( "Wrong parameter for exponential distribution" );
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
						throw new java.lang.IllegalArgumentException( "Wrong parameter for exponential distribution" );
				}
			} else {
				throw new java.lang.IllegalArgumentException( "Unknown distribution" );
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
				throw new java.lang.IllegalArgumentException( "Unknown distribution" );
			}
		}
	}

	private class AssignmentTableModel extends AbstractTableModel {

		@Override
		public Class getColumnClass( int column ) {
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
					return null;
			}
		}

		@Override
		public String getColumnName( int column ) {
			switch( column ) {
				case COL_NAME:
					return "";
				case COL_DISTRIBUTION:
					return "Verteilung";
				case COL_PARAM1:
					return "Minimum";
				case COL_PARAM2:
					return "Maximum";
				case COL_PARAM3:
					return "Erw.-Wert/Lambda";
				case COL_PARAM4:
					return "Varianz";
				default:
					return null;
			}
		}

		public int getRowCount() {
			return params.size();
		}

		public int getColumnCount() {
			return COL_PARAM4 + 1;
		}

		public Object getValueAt( int row, int column ) {
			switch( column ) {
				case COL_NAME:
					return params.get( row ).getName();
				case COL_DISTRIBUTION:
					return params.get( row ).getDistributionName();
				case COL_PARAM1:
					return loc.getFloatConverter().format( params.get( row ).getParam( 1 ) );
				case COL_PARAM2:
					return loc.getFloatConverter().format( params.get( row ).getParam( 2 ) );
				case COL_PARAM3:
					return loc.getFloatConverter().format( params.get( row ).getParam( 3 ) );
				case COL_PARAM4:
					return loc.getFloatConverter().format( params.get( row ).getParam( 4 ) );
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
								JEditor.showErrorMessage( "Error", "Unbekannte Verteilung" );
						}
						drawCharts();
						break;
					case COL_PARAM1:
						params.get( row ).setParam( 1, loc.getFloatConverter().parse( (String) value ).doubleValue() );
						drawCharts();
						break;
					case COL_PARAM2:
						params.get( row ).setParam( 2, loc.getFloatConverter().parse( (String) value ).doubleValue() );
						drawCharts();
						break;
					case COL_PARAM3:
						params.get( row ).setParam( 3, loc.getFloatConverter().parse( (String) value ).doubleValue() );
						drawCharts();
						break;
					case COL_PARAM4:
						params.get( row ).setParam( 4, loc.getFloatConverter().parse( (String) value ).doubleValue() );
						drawCharts();
						break;
					default:
						;
				}
			} catch( java.text.ParseException e ) {
			} catch( java.lang.IllegalArgumentException e ) {
				// tritt auf, wenn parameter out of bounds sind
			}
			distributionTable.repaint(); // Necessary for (de)activating columns
		}
	}

	private class AssignmentTypeListModel extends DefaultListModel {

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

	private class AssignmentListModel extends DefaultListModel {

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
				addText1.setText( "" );
				addText2.setText( "" );
				txtDefaultEvacuees.setText( "" );
				assignmentTypeSelector.displayAssignmentTypes();

				distributionTable.setEnabled( false );
				chartPanel.setEnabled( false );
			}
		}
	}

	private class AssignmentListSelectionModel extends DefaultListSelectionModel {

		@Override
		public void setSelectionInterval( int index0, int index1 ) {
			super.setSelectionInterval( index0, index1 );
			currentAssignment = (Assignment) assignmentSelector.elementAt(
							getMinSelectionIndex() );
			addText1.setText( currentAssignment.getName() );
			assignmentTypeSelector.displayAssignmentTypes();

			// Display first entry if possible
			if( assignmentTypeSelector.getSize() > 0 ) {
				lstAssignmentType.setSelectedIndex( 0 );
			} else {
				currentAssignmentType = null;
				addText2.setText( "" );
				txtDefaultEvacuees.setText( "" );
			}
		}
	}

	/**
	 * This class can display Assignment Objects in a <code>JList</code>.
	 */
	private class AssignmentListRenderer extends ComboBoxRenderer {

		private Font thickFont = getFont().deriveFont( Font.BOLD );
		private Font thinFont = getFont().deriveFont( Font.PLAIN );

		@Override
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
			JLabel me = (JLabel) super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

			if( value != null ) {
				Assignment a = ((Assignment) value);
				setFont( a == myProject.getCurrentAssignment() ? thickFont : thinFont );
				setText( a.getName() );
			}
			return this;
		}
	}

	private class AssignmentTypeListSelectionModel extends DefaultListSelectionModel {

		@Override
		public void setSelectionInterval( int index0, int index1 ) {
			super.setSelectionInterval( index0, index1 );

			currentAssignmentType = (AssignmentType) assignmentTypeSelector.elementAt( getMinSelectionIndex() );

			addText2.setText( currentAssignmentType.getName() );
			txtDefaultEvacuees.setText( Integer.toString( currentAssignmentType.getDefaultEvacuees() ) );
			params.get( 0 ).setDistribution( currentAssignmentType.getDiameter() );
			params.get( 1 ).setDistribution( currentAssignmentType.getAge() );
			params.get( 2 ).setDistribution( currentAssignmentType.getFamiliarity() );
			params.get( 3 ).setDistribution( currentAssignmentType.getPanic() );
			params.get( 4 ).setDistribution( currentAssignmentType.getDecisiveness() );
			distributionTable.repaint();
		}
	}

	/**
	 * This class can display AssignmentType Objects in a JList.
	 */
	private class AssignmentTypeListRenderer extends ComboBoxRenderer {

		@Override
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
			super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
			if( value != null ) {
				AssignmentType at = ((AssignmentType) value);
				setText( at.getName() );
			}
			return this;
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
					((JTextField) e.getComponent()).selectAll();
				}
			} );
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
				Component normal = super.getTableCellRendererComponent( table, value, false, false, row, column );
				activeColorUnselected = normal.getForeground();
				normal = super.getTableCellRendererComponent( table, value, true, true, row, column );
				activeColorSelected = normal.getForeground();
			}

			Component normal = super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );

			if( !tablemodel.isCellEditable( row, column ) ) {
				normal.setForeground( inactiveColor );
			} else {
				normal.setForeground( isSelected ? activeColorSelected : activeColorUnselected );
			}

			return this;
		}
	}
}
