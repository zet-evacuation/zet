/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import java.io.File;

/**
 *
 * @author Martin Groß
 */
public interface FileReader<T> {
    
    String getDescription();
    File getFile();
    void setFile(File file);
    Class<T> getOutputType();
    String[] getSupportedExtensions();
    T load();    
}
