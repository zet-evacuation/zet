/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import de.tu_berlin.math.coga.graph.io.dimacs.DimacsReader;
import ds.graph.problem.MaximumFlowProblem;
import java.io.File;

/**
 *
 * @author gross
 */
public class DimacsFileReader implements FileReader<MaximumFlowProblem> {

    private File file;
    
    @Override
    public String getDescription() {
        return "Reader for Dimacs Files.";
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file; 
    }

    @Override
    public Class<MaximumFlowProblem> getOutputType() {
        return MaximumFlowProblem.class;
    }

    @Override
    public String[] getSupportedExtensions() {
        return new String[] { "max" };
    }

    @Override
    public MaximumFlowProblem load() {
        if (file == null || !file.exists()) {
            return null;
        }
        DimacsReader reader = new DimacsReader(file.getAbsolutePath());
        reader.load();
        return reader.getMaximumFlowProblem();
    }    
}
