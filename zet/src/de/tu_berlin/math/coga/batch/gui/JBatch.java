/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui;

import batch.load.BatchProjectEntry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;
import de.tu_berlin.math.coga.batch.Computation;
import de.tu_berlin.math.coga.batch.ComputationList;
import de.tu_berlin.math.coga.batch.gui.action.*;
import de.tu_berlin.math.coga.batch.gui.input.ComputationNode;
import de.tu_berlin.math.coga.batch.input.FileCrawler;
import de.tu_berlin.math.coga.batch.input.InputFile;
import de.tu_berlin.math.coga.batch.input.InputList;
import ds.ProjectLoader;
import ds.z.Project;
import gui.GUIControl;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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

    private class InputSelectionListener implements TreeSelectionListener {

        private ComputationList selectedComputations;
        
        public InputSelectionListener() {
            selectedComputations = new ComputationList();
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
            if (!selectedComputations.isEmpty()) {
                addAlgorithmAction.setEnabled(true);
                addCurrentProjectAction.setEnabled(true);
                addInputDirectoryAction.setEnabled(true);
                addInputFilesAction.setEnabled(true);
            }
        }
    }
    
    //static ProblemType test = ProblemType.EVACUATION_PROJECT;
    //static ProblemType test2 = ProblemType.MAXIMUM_FLOW;
    //static ProblemType test3 = ProblemType.MINIMUM_COST_FLOW;
    JInputView table;
    private AddInputDirectoryAction addInputDirectoryAction;
    private AddInputFilesAction addInputFilesAction;
    private ComputationList computationList;
    private GUIControl control;
    private final AddCurrentProjectAction addCurrentProjectAction;
    private final AddAlgorithmAction addAlgorithmAction;
    private InputSelectionListener selectionListener;

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
        algorithmPane.add(addAlgorithmAction = new AddAlgorithmAction(this));
        //actionPane.add(algorithmPane);        
        
        actionPane.add(new RunComputationAction(this));
        actionPane.add(new StopComputationAction(this));

        taskPaneContainer.add(actionPane);
        taskPaneContainer.add(inputPane);
        taskPaneContainer.add(algorithmPane);
        add(new JScrollPane(taskPaneContainer), BorderLayout.WEST);
        
        table = new JInputView();
        table.getTree().addTreeSelectionListener(selectionListener = new InputSelectionListener());
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        PropertySheetPanel properties = new PropertySheetPanel();
        add(new JScrollPane(properties), BorderLayout.EAST);
        
        computationList = new ComputationList();
    }

    public GUIControl getControl() {
        return control;
    }
    
    public void add(BatchProjectEntry entry) {
    }

    public Computation getComputation() {
        return computationList.get(0);
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
        if (getComputation() == null) {
            throw new IllegalStateException();
        }
        FileCrawler crawler = new FileCrawler(recursive, followingLinks);
        List<String> extensions = getComputation().getType().getExtensions();
        List<File> files = new LinkedList<>();        
        for (File file : selectedFiles) {
            if (file.isDirectory()) {
                files.addAll(crawler.listFiles(file, extensions));
            } else if (file.isFile()) {
                files.add(file);
            }
        }
        for (Computation computation : selectionListener.selectedComputations) {
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
        for (Computation computation : selectionListener.selectedComputations) {
            InputList input = computation.getInput();
            input.add(new InputFile(file));            
        }
        table.setInput(computationList);
    }
}
