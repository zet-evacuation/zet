/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

/**
 *
 * @author gross
 */
public enum InputFileType {
    ADVEST_EVACUATION("Advest Evacuation Instance", "dat", null, "Nodes", "Edges"),
    DIMACS_MAXIMUM_FLOW_PROBLEM("Dimacs Maximum Flow Problem", "max", new DimacsMaximumFlowFileReader(), "Nodes", "Edges"),    
    GZET_PROJECT("GZipped ZET Project", "gzet", null),
    XML("XML", "xml", null),
    ZET_PROJECT("ZET Project", "zet", null);
    
    private final String description;
    private final String[] fileExtensions;
    private final String[] propertyNames;
    private final InputFileReader reader;

    private InputFileType(String description, String extensions, InputFileReader reader, String... properties) {
        this.description = description;
        this.fileExtensions = extensions.split(",");
        this.propertyNames = properties;
        this.reader = reader;
    }

    public String getDescription() {
        return description;
    }

    public String[] getFileExtensions() {
        return fileExtensions;
    }

    public String[] getPropertyNames() {
        return propertyNames;
    }
    
    public InputFileReader getReader() {
        return reader;
    }
}
