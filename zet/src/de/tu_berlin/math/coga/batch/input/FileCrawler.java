/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author gross
 */
public class FileCrawler {
    
    private List<FileFilter> filters;
    private boolean recursive;    
    
    public FileCrawler() {        
    }
    
    protected boolean isSymlink(File file) {
        return false;
    }    
    
    public List<File> listFiles(File root, FileFilter filter) {
        List<File> result = new LinkedList<File>();
        listFiles(root, filter, result);
        return result;
    }

    protected void listFiles(File root, FileFilter filter, List<File> list) {
        for (File file : root.listFiles(filter)) {
            if (recursive && file.isDirectory() && !isSymlink(file)) {
                listFiles(file, filter, list);
            }
        }
    }    
}
