/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input.reader;

import ds.z.Project;
import java.io.File;

/**
 *
 * @author gross
 */
public class ZETProjectFileReader extends InputFileReader<Project> {

    private String[] properties;
    
    @Override
    public String[] getProperties() {
        if (properties == null) {
            properties = new String[2];
            if (!isProblemSolved()) {
                run();
            } else {
                properties[0] = "" + getSolution().getBuildingPlan().floorCount();
                properties[1] = "" + getSolution().getBuildingPlan().getEvacuationAreasCount();
            }
        }
        return properties;
    }

    @Override
    protected Project runAlgorithm(File problem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
