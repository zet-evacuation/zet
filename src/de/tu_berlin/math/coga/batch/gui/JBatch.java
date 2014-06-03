/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui;

import algo.ca.algorithm.evac.EvacuationCellularAutomatonBackToFront;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonFrontToBack;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonInOrder;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonRandom;
import de.tu_berlin.coga.netflow.dynamic.earliestarrival.SEAAPAlgorithm;
import batch.load.BatchProjectEntry;
import batch.plugins.AlgorithmicPlugin;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;
import de.tu_berlin.math.coga.batch.Computation;
import de.tu_berlin.math.coga.batch.ComputationList;
import de.tu_berlin.math.coga.batch.algorithm.AlgorithmList;
import de.tu_berlin.math.coga.batch.gui.action.AddAlgorithmAction;
import de.tu_berlin.math.coga.batch.gui.action.AddCurrentProjectAction;
import de.tu_berlin.math.coga.batch.gui.action.AddInputDirectoryAction;
import de.tu_berlin.math.coga.batch.gui.action.AddInputFilesAction;
import de.tu_berlin.math.coga.batch.gui.action.NewComputationAction;
import de.tu_berlin.math.coga.batch.gui.action.OperationAction;
import de.tu_berlin.math.coga.batch.gui.action.RunComputationAction;
import de.tu_berlin.math.coga.batch.gui.action.StopComputationAction;
import de.tu_berlin.math.coga.batch.gui.input.AlgorithmPluginNode;
import de.tu_berlin.math.coga.batch.gui.input.ComputationListNode;
import de.tu_berlin.math.coga.batch.gui.input.ComputationNode;
import de.tu_berlin.math.coga.batch.gui.input.InputListNode;
import de.tu_berlin.math.coga.batch.gui.input.InputNode;
import de.tu_berlin.math.coga.batch.gui.input.OperationAlgorithmSelectNode;
import de.tu_berlin.math.coga.batch.input.FileCrawler;
import de.tu_berlin.math.coga.batch.input.FileFormat;
import de.tu_berlin.math.coga.batch.input.InputAlgorithm;
import de.tu_berlin.math.coga.batch.input.InputFile;
import de.tu_berlin.math.coga.batch.input.InputList;
import de.tu_berlin.math.coga.batch.operations.AtomicOperation;
import de.tu_berlin.math.coga.batch.operations.BasicOptimization;
import de.tu_berlin.math.coga.batch.operations.Operation;
import de.tu_berlin.math.coga.batch.operations.OperationList;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import ds.ProjectLoader;
import de.tu_berlin.coga.zet.model.Project;
import gui.GUIControl;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 *
 * @author gross
 */
public class JBatch extends JPanel {

	private class InputKeyListener implements KeyListener {

		public InputKeyListener() {
		}

		@Override
		public void keyTyped( final KeyEvent e ) {
		}

		@Override
		public void keyPressed( final KeyEvent e ) {
			if( e.getKeyCode() == KeyEvent.VK_DELETE )
				for( TreePath path : selectionListener.getSelectedPaths() ) {
					Object leaf = path.getLastPathComponent();
					if( leaf instanceof InputNode && path.getPathComponent( path.getPathCount() - 3 ) instanceof InputListNode ) {
						InputFile file = ((InputNode)leaf).getUserObject();
						InputList list = ((InputListNode)path.getPathComponent( path.getPathCount() - 3 )).getInput();
						list.remove( file );
					}
					if( leaf instanceof ComputationNode && path.getPathComponent( path.getPathCount() - 2 ) instanceof ComputationListNode ) {
						Computation computation = ((ComputationNode)leaf).getUserObject();
						ComputationList list = ((ComputationListNode)path.getPathComponent( path.getPathCount() - 2 )).getComputations();
						list.remove( computation );
					}
				}
			updateTreeTable();
		}

		@Override
		public void keyReleased( KeyEvent e ) {
		}
	}

	private class InputSelectionListener implements TreeSelectionListener {

		private ComputationList selectedComputations;

		public InputSelectionListener() {
			selectedComputations = new ComputationList();
		}

		public ComputationList getSelectedComputations() {
			return selectedComputations;
		}

		public TreePath[] getSelectedPaths() {
			return table.getTree().getTreeSelectionModel().getSelectionPaths();
		}

		@Override
		public void valueChanged( TreeSelectionEvent e ) {
			selectedComputations.clear();
			TreePath[] paths = table.getTree().getTreeSelectionModel().getSelectionPaths();
			for( TreePath path : paths )
				for( Object object : path.getPath() )
					if( object instanceof ComputationNode )
						selectedComputations.add( ((ComputationNode)object).getComputation() );
			cellularAutomaton.setEnabled( !selectedComputations.isEmpty() );
			cellularAutomaton2.setEnabled( !selectedComputations.isEmpty() );
			cellularAutomaton3.setEnabled( !selectedComputations.isEmpty() );
			cellularAutomaton4.setEnabled( !selectedComputations.isEmpty() );
			tjandraOptimized.setEnabled( !selectedComputations.isEmpty() );
			addCurrentProjectAction.setEnabled( !selectedComputations.isEmpty() );
			addInputDirectoryAction.setEnabled( !selectedComputations.isEmpty() );
			addInputFilesAction.setEnabled( !selectedComputations.isEmpty() );

			basicOptimization.setEnabled( !selectedComputations.isEmpty() );
		}
	}

	private JInputView table;
	private AddInputDirectoryAction addInputDirectoryAction;
	private AddInputFilesAction addInputFilesAction;
	private ComputationList computationList;
	private GUIControl control;
	private final AddCurrentProjectAction addCurrentProjectAction;
	private AddAlgorithmAction addAlgorithmAction;
	private InputSelectionListener selectionListener;
	private InputKeyListener keyListener;
	private final AddAlgorithmAction tjandraOptimized;
	private final AddAlgorithmAction cellularAutomaton;
	private final AddAlgorithmAction cellularAutomaton2;
	private final AddAlgorithmAction cellularAutomaton3;
	private final AddAlgorithmAction cellularAutomaton4;
	private final NewComputationAction newComputationAction = null;
	private final OperationAction basicOptimization;

	public JBatch( GUIControl control ) {
		super( new BorderLayout() );
		this.control = control;
		JTaskPane taskPaneContainer = new JTaskPane();
		// add JTaskPaneGroups to the container
		JTaskPaneGroup actionPane = new JTaskPaneGroup();
		actionPane.setTitle( "Computation" );
		actionPane.setSpecial( true );
		actionPane.add( new NewComputationAction( this ) );

		JTaskPaneGroup inputPane = new JTaskPaneGroup();
		inputPane.setTitle( "Input" );
		inputPane.setSpecial( true );
		inputPane.add( addCurrentProjectAction = new AddCurrentProjectAction( this ) );
		inputPane.add( addInputFilesAction = new AddInputFilesAction( this ) );
		inputPane.add( addInputDirectoryAction = new AddInputDirectoryAction( this ) );
		//actionPane.add(inputPane);

		JTaskPaneGroup activityPane = new JTaskPaneGroup();
		activityPane.setTitle( "Use cases" );

		basicOptimization = new OperationAction( this, new BasicOptimization(), "Basic Optimization" );
		activityPane.add( basicOptimization );

		JTaskPaneGroup simulationPane = new JTaskPaneGroup();
		simulationPane.setTitle( "Simulation" );
		simulationPane.setSpecial( true );

		JTaskPaneGroup caPane = new JTaskPaneGroup();
		caPane.setTitle( "Cellular Automaton" );
		caPane.add( cellularAutomaton = new AddAlgorithmAction( this, EvacuationCellularAutomatonInOrder.class, "In Order" ) );
		caPane.add( cellularAutomaton2 = new AddAlgorithmAction( this, EvacuationCellularAutomatonBackToFront.class, "Back-to-Front" ) );
		caPane.add( cellularAutomaton3 = new AddAlgorithmAction( this, EvacuationCellularAutomatonFrontToBack.class, "Front-to-Back" ) );
		caPane.add( cellularAutomaton4 = new AddAlgorithmAction( this, EvacuationCellularAutomatonRandom.class, "Randomized" ) );

		simulationPane.add( caPane );

		JTaskPaneGroup optimizationPane = new JTaskPaneGroup();
		optimizationPane.setTitle( "Optimization" );
		optimizationPane.setSpecial( true );

		JTaskPaneGroup eafPane = new JTaskPaneGroup();
		eafPane.setTitle( "Earliest Arrival" );
		eafPane.add( tjandraOptimized = new AddAlgorithmAction( this, SEAAPAlgorithm.class, "Tjandra (Optimized)" ) );

		optimizationPane.add( eafPane );

		actionPane.add( new RunComputationAction( this ) );
		actionPane.add( new StopComputationAction( this ) );

		taskPaneContainer.add( actionPane );
		taskPaneContainer.add( inputPane );
		taskPaneContainer.add( activityPane );
		taskPaneContainer.add( simulationPane );
		taskPaneContainer.add( optimizationPane );
		add( new JScrollPane( taskPaneContainer ), BorderLayout.WEST );

		table = new JInputView();
		table.getTree().addTreeSelectionListener( selectionListener = new InputSelectionListener() );
		table.getTree().addKeyListener( keyListener = new InputKeyListener() );

		table.getTree().addMouseListener( new MouseAdapter() {

			@Override
			public void mouseClicked( MouseEvent e ) {
				System.out.println( "Mouse klicked on tree." );
				if( e.getClickCount() == 2 ) {
					System.out.println( "Double click!!!" );
					System.out.println( "Column: " + table.getTree().getSelectedColumn() );
					System.out.println( "Row: " + table.getTree().getSelectedRow() );
					table.getTree().getTreeSelectionModel().getSelectionPaths();

					TreePath selectedPath = table.getTree().getTreeSelectionModel().getSelectionPath();
					System.out.println( selectedPath );
					Object p = selectedPath.getLastPathComponent();
					if( p instanceof AlgorithmPluginNode ) {
						AlgorithmPluginNode pluginNode = (AlgorithmPluginNode)p;
						AlgorithmicPlugin<?, ?> selectedPlugin = pluginNode.getUserObject();
						System.out.println( "Plugin: " + selectedPlugin );
						// Activate the node and deactivate all other!
						OperationAlgorithmSelectNode selectNode = (OperationAlgorithmSelectNode)pluginNode.getParent();

						int selectedIndex = selectNode.getUserObject().indexOf( selectedPlugin );
						selectNode.getUserObject().setIndex( selectedIndex );

						int index = 0;
						Enumeration<AlgorithmPluginNode> a = selectNode.children();
						while( a.hasMoreElements() ) {
							AlgorithmPluginNode otherPlugin = a.nextElement();
							if( index == selectedIndex ) {
								AtomicOperation<?, ?> ao = selectNode.getUserObject();
								AlgorithmicPlugin<?, ?> plugin = otherPlugin.getUserObject();
								ao.setSelectedAlgorithm( plugin.getAlgorithm() );

								otherPlugin.setSelected( true );
							} else
								otherPlugin.setSelected( false );
							index++;
						}
						repaint();
					}
				}
			}

		} );

		add( new JScrollPane( table ), BorderLayout.CENTER );

		PropertySheetPanel properties = new PropertySheetPanel();
		add( new JScrollPane( properties ), BorderLayout.EAST );

		computationList = new ComputationList();
		Computation computation = new Computation();
		computation.setTitle( computationList.generateGenericComputationTitle() );
		computationList.add( computation );
		table.setInput( computationList );
		Object root = table.getTree().getTreeTableModel().getRoot();
		Object child = table.getTree().getTreeTableModel().getChild( root, 0 );
		TreePath path = new TreePath( new Object[]{root, child} );
		table.getTree().getTreeSelectionModel().setSelectionPath( path );
	}

	public GUIControl getControl() {
		return control;
	}

	public void add( BatchProjectEntry entry ) {
	}

	public void addAlgorithm( Class<? extends Algorithm> algorithmClass, String title ) {
		for( Computation computation : selectionListener.getSelectedComputations() ) {
			AlgorithmList algorithmList = computation.getAlgorithms();
			InputAlgorithm algorithm = new InputAlgorithm( algorithmClass, title );
			algorithmList.add( algorithm );
		}
		updateTreeTable();
	}

	public void addOperation( Operation operation, String title ) {
		for( Computation computation : selectionListener.getSelectedComputations() ) {
			OperationList operations = computation.getOperations();
			operations.add( operation );
		}
		updateTreeTable();
	}

	public void addComputation( Computation computation ) {
		computationList.add( computation );
		updateTreeTable();
	}

	public void addCurrentProject() {
		addProject( control.getZControl().getProject() );
	}

	public void addInputFiles( File[] selectedFiles ) {
		addInputFiles( selectedFiles, false, false );
	}

	public void addInputFiles( File[] selectedFiles, boolean recursive, boolean followingLinks ) {
		FileCrawler crawler = new FileCrawler( recursive, followingLinks );
		List<String> extensions = FileFormat.getAllKnownExtensions();
		List<File> files = new LinkedList<>();
		for( File file : selectedFiles )
			if( file.isDirectory() )
				files.addAll( crawler.listFiles( file, extensions ) );
			else if( file.isFile() )
				files.add( file );
		for( Computation computation : selectionListener.getSelectedComputations() ) {
			final InputList input = computation.getInput();
			for( File file : files ) {
				final InputFile inputFile = new InputFile( file );
				if( !input.contains( inputFile ) ) {
					input.add( inputFile );
				}
			}
		}
		updateTreeTable();
	}

	public void addProject( Project project ) {
		try {
			ProjectLoader.save( project );
		} catch( IOException ex ) {
			Logger.getLogger( JBatch.class.getName() ).log( Level.SEVERE, null, ex );
		}
		File file = project.getProjectFile();
		for( Computation computation : selectionListener.getSelectedComputations() ) {
			InputList input = computation.getInput();
			InputFile inputFile = new InputFile( file );
			if( !input.contains( inputFile ) )
				input.add( inputFile );
		}
		updateTreeTable();
	}

	public ComputationList getComputationList() {
		return computationList;
	}

	protected void updateTreeTable() {
		// the following is not used.
		int[] selected = table.getTree().getSelectedRows();
		List<TreePath> paths = new LinkedList<>();
		for( int i = 0; i < selected.length; i++ ) {
			TreePath path = table.getTree().getPathForRow( selected[i] );
			paths.add( path );
		}
		table.setInput( computationList );
		/*
		 for (TreePath path : paths) {
		 for (Object node : path.getPath()) {
		 table.getTree().getTreeTableModel().get
		 }
		 path.getPath()
		 System.out.println(path);
		 int row = table.getTree().getRowForPath(path);
		 System.out.println(row);
		 if (row >= 0) {
		 table.getTree().getSelectionModel().addSelectionInterval(row, row);
		 }
		 }
		 JTree tree;
		 System.out.println(table.getTree().getPathForRow(4));*/
	}
}
