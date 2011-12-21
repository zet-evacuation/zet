/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import ds.z.Project;
import javax.swing.ImageIcon;

/**
 *
 * @author gross
 */
public class InputProjectNode extends BatchTreeTableNode<Project> {
    
    public InputProjectNode(Project project) {
        super(project, new String[] { String.valueOf(project.getBuildingPlan().floorCount()), String.valueOf(project.getBuildingPlan().getEvacuationAreasCount()), String.valueOf(project.getBuildingPlan().maximalEvacuees())}, new ImageIcon("./icons/box_16.png"));
    }
}
