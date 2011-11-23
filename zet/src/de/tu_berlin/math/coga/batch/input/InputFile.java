/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import de.tu_berlin.math.coga.batch.input.reader.InputFileReader;
import java.io.File;

/**
 *
 * @author gross
 */
public class InputFile {
    
    private File file;
    private FileFormat format;
    private InputFileReader reader;

    public InputFile(File file) {
        this.file = file;
        this.format = FileFormat.determineFileFormat(file);
        try {
            this.reader = format.getReader().newInstance();
            reader.setFile(file);
        } catch (InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
    
    public String[] getPropertyNames() {
        return format.getProblemType().getPropertyNames();
    }

    public String[] getProperties() {            
        return reader.getProperties();
    }

    public Object getName() {
        return file.getName();
    }
    
}
