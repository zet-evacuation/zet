/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import de.tu_berlin.math.coga.batch.input.reader.InputFileReader;
import java.io.File;
import java.util.Objects;

/**
 *
 * @author gross
 */
public class InputFile {
    
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
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InputFile other = (InputFile) obj;
        if (!Objects.equals(this.file, other.file)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.file);
        return hash;
    }

    public File getFile() {
        return file;
    }    
    
    public FileFormat getFormat() {
        return format;
    }
    
    public ProblemType getProblemType() {
        return format.getProblemType();
    }
    
    public String[] getPropertyNames() {
        return format.getProblemType().getPropertyNames();
    }

    public String[] getProperties() {            
        return reader.getProperties();
    }
    
    public InputFileReader<ProblemType> getReader() {
        return reader;
    }

    public String getTooltip() {
        return file.getPath();
    }
    
    @Override
    public String toString() {
        return file.getName();
    }
}
