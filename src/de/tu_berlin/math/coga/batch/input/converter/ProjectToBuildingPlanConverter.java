/*
 * ProjectToBuildingPlanConverter.java
 * 
 */
package de.tu_berlin.math.coga.batch.input.converter;

import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.zet.model.BuildingPlan;
import de.tu_berlin.coga.zet.model.Project;

/**
 * A convenience converter that extracts the building plan from a project.
 * 
 * @author Martin Groß
 */
public class ProjectToBuildingPlanConverter extends Algorithm<Project, BuildingPlan> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected BuildingPlan runAlgorithm(Project problem) {
        return problem.getBuildingPlan();
    }
}
