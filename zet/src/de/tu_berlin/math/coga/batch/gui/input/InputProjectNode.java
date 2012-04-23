/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.input.InputProject;
import javax.swing.ImageIcon;

/**
 *
 * @author gross
 */
public class InputProjectNode extends BatchTreeTableNode<InputProject> {
    
    public InputProjectNode(InputProject project) {
        super(project, project.getProperties(), new ImageIcon("./icons/box_16.png"));
    }
}
