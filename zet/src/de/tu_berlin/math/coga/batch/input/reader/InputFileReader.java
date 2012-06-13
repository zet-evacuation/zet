/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input.reader;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import java.io.File;

/**
 *
 * @author Martin Groß
 */
public abstract class InputFileReader<T> extends Algorithm<File,T> {

    public enum Optimization {
        SPEED, MEMORY;
    }
    
    private Optimization optimization = Optimization.SPEED;

    public InputFileReader() {
        super();
        getParameterSet().addParameter("Optimization", "Specifies whether the reader should try to conserve runtime or memory", Optimization.SPEED);
    }
    
    public File getFile() {
        return getProblem();
    }
    
    public void setFile(File file) {
        setProblem(file);
    }

    public Optimization getOptimization() {
        return optimization;
    }

    public void setOptimization(Optimization optimization) {
        this.optimization = optimization;
    }
    
    public abstract String[] getProperties();
}
