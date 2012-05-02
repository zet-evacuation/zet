/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import static de.tu_berlin.math.coga.batch.input.FileFormat.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Martin Gross
 */
public enum ProblemType {

    EVACUATION_PROJECT("Evacuation Project", "Number of Floors, Number of Exits, Maximal Number of Evacuees"),
    MAXIMUM_FLOW("Maximum Flow Problem", "Number of Nodes, Number of Edges"),
    MINIMUM_COST_FLOW("Minimum Cost Flow Problem", "Number of Nodes, Number of Edges, Total Supply");
    private final String description;
    private List<String> extensions;
    private FileFilter fileFilter;
    private final String[] propertyNames;

    private ProblemType(String description, String properties) {
        this.description = description;
        this.propertyNames = properties.split("\\s*,\\s*");
    }

    public String getDescription() {
        return description;
    }

    public List<String> getExtensions() {
        if (extensions == null) {
            extensions = new LinkedList<>();
            for (FileFormat format : FileFormat.values()) {
                if (format.getProblemType() == this) {
                    extensions.addAll(Arrays.asList(format.getExtensions()));
                }                
            }
            fileFilter = new FileNameExtensionFilter(description, extensions.toArray(new String[0]));
        }
        return extensions;
    }    
    
    public FileFilter getFileFilter() {
        if (fileFilter == null) {
            extensions = new LinkedList<>();
            for (FileFormat format : FileFormat.values()) {
                if (format.getProblemType() == this) {
                    extensions.addAll(Arrays.asList(format.getExtensions()));
                }                
            }
            fileFilter = new FileNameExtensionFilter(description, extensions.toArray(new String[0]));
        }
        return fileFilter;
    }

    public String[] getPropertyNames() {
        return propertyNames;
    }

    @Override
    public String toString() {
        return description;
    }
}
