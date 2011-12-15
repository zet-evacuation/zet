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
import de.tu_berlin.math.coga.batch.gui.action.AddInputFilesAction;
import de.tu_berlin.math.coga.batch.gui.action.NewComputationAction;
import de.tu_berlin.math.coga.batch.gui.action.AddCurrentProjectAction;
import de.tu_berlin.math.coga.batch.gui.action.AddAlgorithmAction;
import de.tu_berlin.math.coga.batch.gui.action.AddInputDirectoryAction;
import de.tu_berlin.math.coga.batch.gui.action.RunComputationAction;
import de.tu_berlin.math.coga.batch.gui.action.StopComputationAction;
import ds.z.Project;
import gui.GUIControl;
import java.awt.BorderLayout;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

/**
 *
 * @author gross
 */
public class JBatch extends JPanel {
    
    private Computation computation;

    public JBatch(GUIControl control) {
        super(new BorderLayout());
        JTaskPane taskPaneContainer = new JTaskPane();
        // add JTaskPaneGroups to the container
        JTaskPaneGroup actionPane = new JTaskPaneGroup();
        actionPane.setTitle("Computation");
        actionPane.setSpecial(true);         
        actionPane.add(new NewComputationAction(this));

        JTaskPaneGroup inputPane = new JTaskPaneGroup();
        inputPane.setTitle("Input");
        inputPane.setSpecial(true);
        inputPane.add(new AddCurrentProjectAction(this));
        inputPane.add(new AddInputFilesAction(this));
        inputPane.add(new AddInputDirectoryAction(this));
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
    }
    
    JInputView table;

    public void addProject(Project project) {
    }

    public void add(BatchProjectEntry entry) {
    }

    public void addInputFiles(File[] selectedFiles) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Computation getComputation() {
        return computation;
    }

    public void setComputation(Computation computation) {
        this.computation = computation;
        table.setInput(computation.getInput());
    }
}
