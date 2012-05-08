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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author gross
 */
public class JBatch extends JPanel {
    
    //static ProblemType test = ProblemType.EVACUATION_PROJECT;
    //static ProblemType test2 = ProblemType.MAXIMUM_FLOW;
    //static ProblemType test3 = ProblemType.MINIMUM_COST_FLOW;
    
    private AddInputDirectoryAction addInputDirectoryAction;
    private AddInputFilesAction addInputFilesAction;
    private ComputationList computations;
    private GUIControl control;
    private final AddCurrentProjectAction addCurrentProjectAction;
    private final AddAlgorithmAction addAlgorithmAction;

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
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        PropertySheetPanel properties = new PropertySheetPanel();
        add(new JScrollPane(properties), BorderLayout.EAST);
    }

    public GUIControl getControl() {
        return control;
    }
    
    JInputView table;

    public void addProject(Project project) {
    }

    public void add(BatchProjectEntry entry) {
    }

    public Computation getComputation() {
        return computations.get(0);
    }

    public void setComputation(Computation computation) {
        this.computations = new ComputationList();
        computations.add(computation);
        if (computation != null) {
            table.setInput(computations);
            addAlgorithmAction.setEnabled(true);
            addCurrentProjectAction.setEnabled(true);
            addInputDirectoryAction.setEnabled(true);
            addInputFilesAction.setEnabled(true);
        }
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
        InputList input = getComputation().getInput();
        for (File file : files) {
            InputFile inputFile = new InputFile(file);
            if (!input.contains(inputFile)) {
                input.add(inputFile);
            }
        }
        table.setInput(computations);
    }

    public void addCurrentProject() {
        Project project = control.getZControl().getProject();
        try {
            ProjectLoader.save(project);
        } catch (IOException ex) {
            Logger.getLogger(JBatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        File file = project.getProjectFile();
        InputList input = getComputation().getInput();
        input.add(new InputFile(file));
        table.setInput(computations);
    }

    public void addComputation(Computation computation) {
        computations.add(computation);
        table.setInput(computations);
    }
}
