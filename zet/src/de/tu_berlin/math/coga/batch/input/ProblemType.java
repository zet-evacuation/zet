/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import javax.swing.filechooser.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import javax.swing.filechooser.FileNameExtensionFilter;
import static de.tu_berlin.math.coga.batch.input.FileFormat.*;

/**
 *
 * @author gross
 */
public enum ProblemType {

    MAXIMUM_FLOW("Maximum Flow Problem", "Number of Nodes, Number of Edges", DIMACS_MAXIMUM_FLOW, RMFGEN_MAXIMUM_FLOW),
    MINIMUM_COST_FLOW("Minimum Cost Flow Problem", "Number of Nodes, Number of Edges, Total Supply", DIMACS_MINIMUM_COST_FLOW);
    private final String description;
    private final FileFilter fileFilter;
    private final FileFormat[] fileFormats;
    private final String[] propertyNames;

    private ProblemType(String description, String properties, FileFormat... formats) {
        this.description = description;
        this.propertyNames = properties.split("\\s*,\\s*");
        this.fileFormats = formats;
        List<String> extensions = new LinkedList<>();
        for (FileFormat format : formats) {
            extensions.addAll(Arrays.asList(format.getExtensions()));
        }
        this.fileFilter = new FileNameExtensionFilter(description, extensions.toArray(new String[0]));
    }

    public String getDescription() {
        return description;
    }

    public FileFilter getFileFilter() {
        return fileFilter;
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
