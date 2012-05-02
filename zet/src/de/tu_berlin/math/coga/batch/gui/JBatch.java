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
import de.tu_berlin.math.coga.batch.gui.action.*;
import de.tu_berlin.math.coga.batch.input.*;
import ds.ProjectLoader;
import ds.z.Project;
import gui.GUIControl;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
    private Computation computation;
    private GUIControl control;
    private final AddCurrentProjectAction addCurrentProjectAction;

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
        algorithmPane.add(new AddAlgorithmAction(this));
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
        setComputation(new Computation(ProblemType.EVACUATION_PROJECT));
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
        return computation;
    }

    public void setComputation(Computation computation) {
        this.computation = computation;
        if (computation != null) {
            table.setInput(computation.getInput());
            addCurrentProjectAction.setEnabled(true);
            addInputDirectoryAction.setEnabled(true);
            addInputFilesAction.setEnabled(true);
        }
    }

    public void addInputFiles(File[] selectedFiles) {
        addInputFiles(selectedFiles, false, false);
    }

    public void addInputFiles(File[] selectedFiles, boolean recursive, boolean followingLinks) {
        if (computation == null) {
            throw new IllegalStateException();
        }
        FileCrawler crawler = new FileCrawler(recursive, followingLinks);
        List<String> extensions = computation.getType().getExtensions();
        List<File> files = new LinkedList<>();        
        for (File file : selectedFiles) {
            if (file.isDirectory()) {
                files.addAll(crawler.listFiles(file, extensions));
            } else if (file.isFile()) {
                files.add(file);
            }
        }        
        InputList input = computation.getInput();
        for (File file : files) {
            InputFile inputFile = new InputFile(file);
            if (!input.contains(inputFile)) {
                input.add(inputFile);
            }
        }
        table.setInput(input);    
    }

    public void addCurrentProject() {
        Project project = control.getZControl().getProject();
        try {
            ProjectLoader.save(project);
        } catch (IOException ex) {
            Logger.getLogger(JBatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        File file = project.getProjectFile();
        System.out.println(file);
        System.out.println(FileFormat.determineFileFormat(file));
        InputList input = computation.getInput();
        input.add(new InputFile(file));
        table.setInput(input);
        /*
        InputProjectReader reader = new NetworkFlowModelProjectReader();
        reader.setProblem(project);
        reader.run();
        System.out.println("Done X");
        System.out.println(reader.getSolution());        
        System.out.println("Done Y");*/
    }
}
