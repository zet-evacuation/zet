/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import ds.z.Project;

/**
 *
 * @author gross
 */
public class InputProject extends Input<Project> {
    
    private Project project;

    public InputProject(Project project) {
        this.project = project;
    }

    @Override
    public Project getInput() {
        return project;
    }    
    
    @Override
    public String[] getPropertyNames() {
        return new String[] { "Number of Floors, Number of Exits, Maximal Number of Evacuees"};
    }

    @Override
    public String[] getProperties() {            
        return new String[] { String.valueOf(project.getBuildingPlan().floorCount()), String.valueOf(project.getBuildingPlan().getEvacuationAreasCount()), String.valueOf(project.getBuildingPlan().maximalEvacuees())};
    }

    @Override
    public String getTooltip() {
        return project.getPath();
    }    
    
    @Override
    public String toString() {
        return project.getName();
    }    
}
