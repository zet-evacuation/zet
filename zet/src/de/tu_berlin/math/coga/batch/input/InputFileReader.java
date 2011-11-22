/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import java.io.File;

/**
 *
 * @author Martin Groß
 */
public abstract class InputFileReader<T> extends Algorithm<File, T> {
    
    public File getFile() {
        return getProblem();
    }
    
    public void setFile(File file) {
        setProblem(file);
    }
    
    public abstract String[] getProperties();
}
