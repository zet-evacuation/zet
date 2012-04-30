/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import de.tu_berlin.math.coga.batch.input.reader.*;
import java.io.File;

/**
 *
 * @author Martin Gross
 */
public enum FileFormat {

    DIMACS_MAXIMUM_FLOW(ProblemType.MAXIMUM_FLOW, DimacsMaximumFlowFileReader.class, "DIMACS Maximum Flow Problem", "max"),
    DIMACS_MINIMUM_COST_FLOW(ProblemType.MINIMUM_COST_FLOW, DimacsMinimumCostFlowFileReader.class, "DIMACS Minimum Cost Flow Problem", "min", "net"),
    RMFGEN_MAXIMUM_FLOW(ProblemType.MAXIMUM_FLOW, RMFGENMaximumFlowFileReader.class, "RMFGEN Maximum Flow Problem", "rmf"),
    ZET_PROJECT(ProblemType.EVACUATION_PROJECT, ZETProjectFileReader.class, "ZET Evacuation Project", "zet");
    private final String description;
    private final String[] extensions;
    private final ProblemType problemType;
    private final Class<? extends InputFileReader> reader;

    private FileFormat(ProblemType problemType, Class<? extends InputFileReader> reader, String description, String... extensions) {
        System.out.println(problemType);
        this.description = description;
        this.extensions = extensions;
        this.problemType = problemType;
        this.reader = reader;
    }

    public String getDescription() {
        return description;
    }

    public String[] getExtensions() {
        return extensions;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public Class<? extends InputFileReader> getReader() {
        return reader;
    }
    
    public static FileFormat determineFileFormat(File file) {
        int index = file.getName().lastIndexOf(".");
        String ext = (index >= 0)? file.getName().substring(file.getName().lastIndexOf(".") + 1) : "";
        for (FileFormat format : FileFormat.values()) {
            for (String extension : format.getExtensions()) {
                if (ext.equals(extension)) {
                    return format;
                }
            }
        }
        return null;
    }
}
