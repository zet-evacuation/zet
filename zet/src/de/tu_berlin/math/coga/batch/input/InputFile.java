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
public class InputFile extends Input<File> {
    
    private File file;
    private FileFormat format;
    private InputFileReader reader;

    public InputFile(File file) {
        if (!file.exists() || !file.canRead()) {
            throw new IllegalArgumentException(file + " cannot be read.");
        }
        this.file = file;
        this.format = FileFormat.determineFileFormat(file);
        try {
            this.reader = format.getReader().newInstance();
            reader.setFile(file);
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new AssertionError("Reader could not be initialized.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof InputFile) && file.equals(((InputFile) obj).file);
    }

    @Override
    public File getInput() {
        return file;
    }    
    
    @Override
    public String[] getPropertyNames() {
        return format.getProblemType().getPropertyNames();
    }

    @Override
    public String[] getProperties() {            
        return reader.getProperties();
    }

    @Override
    public String getTooltip() {
        return file.getPath();
    }
    
    @Override
    public String toString() {
        return file.getName();
    }
}
