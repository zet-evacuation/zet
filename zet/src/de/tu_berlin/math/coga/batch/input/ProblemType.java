/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import static de.tu_berlin.math.coga.batch.input.FileFormat.*;

/**
 *
 * @author gross
 */
public enum ProblemType {

    MAXIMUM_FLOW("Maximum Flow Problem", "Number of Nodes, Number of Edges", DIMACS_MAXIMUM_FLOW, RMFGEN_MAXIMUM_FLOW),
    MINIMUM_COST_FLOW("Minimum Cost Flow Problem", "Number of Nodes, Number of Edges, Total Supply", DIMACS_MINIMUM_COST_FLOW);
    private final String description;
    private final FileFormat[] fileFormats;
    private final String[] propertyNames;

    private ProblemType(String description, String properties, FileFormat... formats) {
        this.description = description;
        this.propertyNames = properties.split("\\s*,\\s*");
        this.fileFormats = formats;
    }

    public String getDescription() {
        return description;
    }

    public FileFormat[] getFileFormats() {
        return fileFormats;
    }

    public String[] getPropertyNames() {
        return propertyNames;
    }

    public String toString() {
        return description;
    }
}
