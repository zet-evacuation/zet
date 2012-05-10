/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui;

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
            table.setInput(computationList);
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
            //addAlgorithmAction.setEnabled(!selectedComputations.isEmpty());
            tjandraOptimzed.setEnabled(!selectedComputations.isEmpty());
            addCurrentProjectAction.setEnabled(!selectedComputations.isEmpty());
            addInputDirectoryAction.setEnabled(!selectedComputations.isEmpty());
            addInputFilesAction.setEnabled(!selectedComputations.isEmpty());
        }
    }

    JInputView table;
    private AddInputDirectoryAction addInputDirectoryAction;
    private AddInputFilesAction addInputFilesAction;
    private ComputationList computationList;
    private GUIControl control;
    private final AddCurrentProjectAction addCurrentProjectAction;
    private  AddAlgorithmAction addAlgorithmAction;
    private InputSelectionListener selectionListener;
    private InputKeyListener keyListener;
    private final AddAlgorithmAction tjandraOptimzed;



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

        JTaskPaneGroup algorithmPane = new JTaskPaneGroup();
        algorithmPane.setTitle("Algorithm");
        algorithmPane.setSpecial(true);
        
        JTaskPaneGroup eafPane = new JTaskPaneGroup();
        eafPane.setTitle("Earliest Arrival");
        eafPane.add(tjandraOptimzed = new AddAlgorithmAction(this));
        
        algorithmPane.add(eafPane);//(addAlgorithmAction = new AddAlgorithmAction(this));
        
        //actionPane.add(algorithmPane);        
        
        actionPane.add(new RunComputationAction(this));
        actionPane.add(new StopComputationAction(this));

        taskPaneContainer.add(actionPane);
        taskPaneContainer.add(inputPane);
        taskPaneContainer.add(algorithmPane);
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
    
    public void addAlgorithm(Class<? extends Algorithm> algorithmClass) {
        for (Computation computation : selectionListener.getSelectedComputations()) {
            AlgorithmList algorithmList = computation.getAlgorithms();
            InputAlgorithm algorithm = new InputAlgorithm(algorithmClass);
            algorithmList.add(algorithm);
        }           
        table.setInput(computationList);
    }        
    
    public void addComputation(Computation computation) {
        computationList.add(computation);
        table.setInput(computationList);
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
        table.setInput(computationList);
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
        table.setInput(computationList);
    }

    public ComputationList getComputationList() {
        return computationList;
    }    
}
