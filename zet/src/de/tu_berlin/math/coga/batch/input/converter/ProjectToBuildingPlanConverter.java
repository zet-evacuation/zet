/*
 * ProjectToBuildingPlanConverter.java
 * 
 */
package de.tu_berlin.math.coga.batch.input.converter;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.z.BuildingPlan;
import ds.z.Project;

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
