/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input.reader;

import java.io.File;

/**
 *
 * @author gross
 */
public class UnknownFileReader extends InputFileReader<Void> {

    @Override
    public String[] getProperties() {
        return new String[0];
    }

    @Override
    protected Void runAlgorithm(File problem) {
        return null;
    }    
}
