/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui;

import algo.ca.algorithm.evac.EvacuationCellularAutomatonBackToFront;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonFrontToBack;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonInOrder;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonRandom;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
import batch.load.BatchProjectEntry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;
import de.tu_berlin.math.coga.batch.Computation;
import de.tu_berlin.math.coga.batch.ComputationList;
import de.tu_berlin.math.coga.batch.algorithm.AlgorithmList;
import de.tu_berlin.math.coga.batch.gui.action.*;
import de.tu_berlin.math.coga.batch.gui.input.ComputationListNode;
import de.tu_berlin.math.coga.batch.gui.input.ComputationNode;
import de.tu_berlin.math.coga.batch.gui.input.InputListNode;
import de.tu_berlin.math.coga.batch.gui.input.InputNode;
import de.tu_berlin.math.coga.batch.input.*;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.ProjectLoader;
import ds.z.Project;
import gui.GUIControl;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
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
        public void keyTyped(KeyEvent e) {            
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                for (TreePath path : selectionListener.getSelectedPaths()) {
                    Object leaf = path.getLastPathComponent();
                    if (leaf instanceof InputNode && path.getPathComponent(path.getPathCount() - 3) instanceof InputListNode) {
                        InputFile file = ((InputNode) leaf).getUserObject();
                        InputList list = ((InputListNode) path.getPathComponent(path.getPathCount() - 3)).getInput();
                        list.remove(file);
                    }
                    if (leaf instanceof ComputationNode && path.getPathComponent(path.getPathCount() - 2) instanceof ComputationListNode) {
                        Computation computation = ((ComputationNode) leaf).getUserObject();
                        ComputationList list = ((ComputationListNode) path.getPathComponent(path.getPathCount() - 2)).getComputations();
                        list.remove(computation);
                    }
                }
            }
            updateTreeTable();
        }

        @Override
        public void keyReleased(KeyEvent e) {            
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
        public void valueChanged(TreeSelectionEvent e) {
            selectedComputations.clear();
            TreePath[] paths = table.getTree().getTreeSelectionModel().getSelectionPaths();
            for (TreePath path : paths) {
                for (Object object : path.getPath()) {
                    if (object instanceof ComputationNode) {
                        selectedComputations.add(((ComputationNode) object).getComputation());
                    }
                }
            }
            cellularAutomaton.setEnabled(!selectedComputations.isEmpty());
            cellularAutomaton2.setEnabled(!selectedComputations.isEmpty());
            cellularAutomaton3.setEnabled(!selectedComputations.isEmpty());
            cellularAutomaton4.setEnabled(!selectedComputations.isEmpty());
            tjandraOptimized.setEnabled(!selectedComputations.isEmpty());
            addCurrentProjectAction.setEnabled(!selectedComputations.isEmpty());
            addInputDirectoryAction.setEnabled(!selectedComputations.isEmpty());
            addInputFilesAction.setEnabled(!selectedComputations.isEmpty());
        }
    }

    private JInputView table;
    private AddInputDirectoryAction addInputDirectoryAction;
    private AddInputFilesAction addInputFilesAction;
    private ComputationList computationList;
    private GUIControl control;
    private final AddCurrentProjectAction addCurrentProjectAction;
    private  AddAlgorithmAction addAlgorithmAction;
    private InputSelectionListener selectionListener;
    private InputKeyListener keyListener;
    private final AddAlgorithmAction tjandraOptimized;
    private final AddAlgorithmAction cellularAutomaton;
    private final AddAlgorithmAction cellularAutomaton2;
    private final AddAlgorithmAction cellularAutomaton3;
    private final AddAlgorithmAction cellularAutomaton4;


    public JBatch(GUIControl control) {
        super(new BorderLayout());
        this.control = control;
        JTaskPane taskPaneContainer = new JTaskPane();
        // add JTaskPaneGroups to the container
        JTaskPaneGroup actionPane = new JTaskPaneGroup();
        actionPane.setTitle("Computation");
        actionPane.setSpecial(true);         
        actionPane.add(new NewComputationAction(this));

        JTaskPaneGroup inputPane = new JTaskPaneGroup();
        inputPane.setTitle("Input");
        inputPane.setSpecial(true);
        inputPane.add(addCurrentProjectAction = new AddCurrentProjectAction(this));
        inputPane.add(addInputFilesAction = new AddInputFilesAction(this));
        inputPane.add(addInputDirectoryAction = new AddInputDirectoryAction(this));
        //actionPane.add(inputPane);

        JTaskPaneGroup simulationPane = new JTaskPaneGroup();
        simulationPane.setTitle("Simulation");
        simulationPane.setSpecial(true);
        
        JTaskPaneGroup caPane = new JTaskPaneGroup();
        caPane.setTitle("Cellular Automaton");
        caPane.add(cellularAutomaton = new AddAlgorithmAction(this, EvacuationCellularAutomatonInOrder.class, "In Order"));
        caPane.add(cellularAutomaton2 = new AddAlgorithmAction(this, EvacuationCellularAutomatonBackToFront.class, "Back-to-Front"));
        caPane.add(cellularAutomaton3 = new AddAlgorithmAction(this, EvacuationCellularAutomatonFrontToBack.class, "Front-to-Back"));
        caPane.add(cellularAutomaton4 = new AddAlgorithmAction(this, EvacuationCellularAutomatonRandom.class, "Randomized"));
        
        simulationPane.add(caPane);
        
        JTaskPaneGroup optimizationPane = new JTaskPaneGroup();
        optimizationPane.setTitle("Optimization");
        optimizationPane.setSpecial(true);
        
        JTaskPaneGroup eafPane = new JTaskPaneGroup();
        eafPane.setTitle("Earliest Arrival");
        eafPane.add(tjandraOptimized = new AddAlgorithmAction(this, SEAAPAlgorithm.class, "Tjandra (Optimized)"));
        
        optimizationPane.add(eafPane);     
        
        actionPane.add(new RunComputationAction(this));
        actionPane.add(new StopComputationAction(this));

        taskPaneContainer.add(actionPane);
        taskPaneContainer.add(inputPane);
        taskPaneContainer.add(simulationPane);
        taskPaneContainer.add(optimizationPane);
        add(new JScrollPane(taskPaneContainer), BorderLayout.WEST);
        
        table = new JInputView();
        table.getTree().addTreeSelectionListener(selectionListener = new InputSelectionListener());
        table.getTree().addKeyListener(keyListener = new InputKeyListener());
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        PropertySheetPanel properties = new PropertySheetPanel();
        add(new JScrollPane(properties), BorderLayout.EAST);
        
        computationList = new ComputationList();
        Computation computation = new Computation();
        computation.setTitle(computationList.generateGenericComputationTitle());
        computationList.add(computation);
        table.setInput(computationList);    
        Object root = table.getTree().getTreeTableModel().getRoot(); 
        Object child = table.getTree().getTreeTableModel().getChild(root, 0);
        TreePath path = new TreePath(new Object[] { root, child });
        table.getTree().getTreeSelectionModel().setSelectionPath(path);
    }

    public GUIControl getControl() {
        return control;
    }

    
    public void add(BatchProjectEntry entry) {
    }    
    
    public void addAlgorithm(Class<? extends Algorithm> algorithmClass, String title) {
        for (Computation computation : selectionListener.getSelectedComputations()) {
            AlgorithmList algorithmList = computation.getAlgorithms();
            InputAlgorithm algorithm = new InputAlgorithm(algorithmClass, title);
            algorithmList.add(algorithm);
        }           
        updateTreeTable();
    }        
    
    public void addComputation(Computation computation) {
        computationList.add(computation);
        updateTreeTable();
    }    
    
    public void addCurrentProject() {
        addProject(control.getZControl().getProject());
    }    

    public void addInputFiles(File[] selectedFiles) {
        addInputFiles(selectedFiles, false, false);
    }

    public void addInputFiles(File[] selectedFiles, boolean recursive, boolean followingLinks) {
        FileCrawler crawler = new FileCrawler(recursive, followingLinks);
        List<String> extensions = FileFormat.getAllKnownExtensions();
        List<File> files = new LinkedList<>();        
        for (File file : selectedFiles) {
            if (file.isDirectory()) {
                files.addAll(crawler.listFiles(file, extensions));
            } else if (file.isFile()) {
                files.add(file);
            }
        }
        for (Computation computation : selectionListener.getSelectedComputations()) {
            InputList input = computation.getInput();
            for (File file : files) {
                InputFile inputFile = new InputFile(file);
                if (!input.contains(inputFile)) {
                    input.add(inputFile);
                }
            }      
        }    
        updateTreeTable();
    }

    public void addProject(Project project) {
        try {
            ProjectLoader.save(project);
        } catch (IOException ex) {
            Logger.getLogger(JBatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        File file = project.getProjectFile();
        for (Computation computation : selectionListener.getSelectedComputations()) {
            InputList input = computation.getInput();
            InputFile inputFile = new InputFile(file);
            if (!input.contains(inputFile)) {
                input.add(inputFile);
            }            
        }
        updateTreeTable();
    }

    public ComputationList getComputationList() {
        return computationList;
    }

    protected void updateTreeTable() {
        int[] selected = table.getTree().getSelectedRows();
        List<TreePath> paths = new LinkedList<>();
        for (int i = 0; i < selected.length; i++) {
            TreePath path = table.getTree().getPathForRow(selected[i]);
            paths.add(path);
        }
        table.setInput(computationList);
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
