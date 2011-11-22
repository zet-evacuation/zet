/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import java.io.File;
import java.util.LinkedList;

/**
 *
 * @author Martin Groß
 */
public class InputGroup extends LinkedList<File> {
    
    private InputFileReader reader;
    private InputFileType type;

    public InputGroup(InputFileType inputFileType) {
        type = inputFileType;
    }

    public InputFileReader getReader() {
        return reader;
    }

    public void setReader(InputFileReader reader) {
        this.reader = reader;
    }

    public InputFileType getType() {
        return type;
    }

    public void setType(InputFileType type) {
        this.type = type;
    }
}
