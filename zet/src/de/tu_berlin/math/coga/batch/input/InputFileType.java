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
    ADVEST_EVACUATION("Advest Evacuation Instance", "dat", "Nodes", "Edges"),
    DIMACS_MAXIMUM_FLOW_PROBLEM("Dimacs Maximum Flow Problem", "max", "Nodes", "Edges"),    
    GZET_PROJECT("GZipped ZET Project", "gzet"),
    XML("XML", "xml"),
    ZET_PROJECT("ZET Project", "zet");
    
    private final String description;
    private final String[] fileExtensions;
    private final String[] propertyNames;

    private InputFileType(String description, String extensions, String... properties) {
        this.description = description;
        this.fileExtensions = extensions.split(",");
        this.propertyNames = properties;
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
}
