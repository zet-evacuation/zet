/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui;

import batch.load.BatchProjectEntry;
import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;
import de.tu_berlin.math.coga.batch.Computation;
import de.tu_berlin.math.coga.batch.gui.actions.AddInputFilesAction;
import de.tu_berlin.math.coga.batch.gui.actions.NewComputationAction;
import de.tu_berlin.math.coga.batch.gui.actions.AddCurrentProjectAction;
import de.tu_berlin.math.coga.batch.gui.actions.ChooseAlgorithmAction;
import de.tu_berlin.math.coga.batch.gui.actions.RemoveInputFilesAction;
import de.tu_berlin.math.coga.batch.gui.actions.RunComputationAction;
import de.tu_berlin.math.coga.batch.gui.actions.StopComputationAction;
import ds.z.Project;
import gui.GUIControl;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author gross
 */
public class JBatch extends JPanel {

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
        inputPane.add(new RemoveInputFilesAction(this));
        actionPane.add(inputPane);

        JTaskPaneGroup algorithmPane = new JTaskPaneGroup();
        algorithmPane.setTitle("Algorithm");
        algorithmPane.setSpecial(true);
        algorithmPane.add(new ChooseAlgorithmAction(this));
        actionPane.add(algorithmPane);        
        
        actionPane.add(new RunComputationAction(this));
        actionPane.add(new StopComputationAction(this));

        taskPaneContainer.add(actionPane);
        add(new JScrollPane(taskPaneContainer), BorderLayout.WEST);
    }

    public void addProject(Project project) {
    }

    public void add(BatchProjectEntry entry) {
    }
    
    public Computation getSelectedComputation() {
        return null;
    }
}
