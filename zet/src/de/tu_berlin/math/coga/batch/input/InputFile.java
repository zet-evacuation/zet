/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import java.io.File;

/**
 *
 * @author gross
 */
public class InputFile {
    
    private File file;
    private InputFileReader reader;

    public InputFile(File file, InputFileReader reader) {
        this.file = file;
        this.reader = reader;
    }
    
    public String[] getPropertyNames() {
        return new String[] { "Nodes", "Edges", "Supply", "Time Horizon" };
    }

    public String[] getProperties() {
        reader.setFile(file);         
        return reader.getProperties();
    }

    public Object getName() {
        return file.getName();
    }
    
}
