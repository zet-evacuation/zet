/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.input.InputFile;
import ds.z.Project;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 *
 * @author gross
 */
public class InputProjectNode extends DefaultMutableTreeTableNode {

    private String[] properties;
    
    public InputProjectNode(Project project) {
        super(project, true);
        int evacuees = project.getBuildingPlan().maximalEvacuees();
        int exits = project.getBuildingPlan().getEvacuationAreasCount();
        int floors = project.getBuildingPlan().floorCount();        
        properties = new String[] { String.valueOf(floors), String.valueOf(exits), String.valueOf(evacuees)};
    }

    @Override
    public int getColumnCount() {
        return properties.length + 1;
    }
    
    public Project getProject() {
        return (Project) getUserObject();
    }

    @Override
    public Object getValueAt(int column) {
        if (column == 0) {
            return getProject().getName();
        } else {
            return properties[column - 1];
        }
    }

    @Override
    public boolean isEditable(int column) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int column) {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return getProject().getName();
    }   
    
}
