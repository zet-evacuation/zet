/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author gross
 */
public class FileCrawler {

    private boolean followingLinks;
    private boolean recursive;
    private HashMap<File, Boolean> visited;

    public FileCrawler(boolean recursive, boolean followLinks) {
        this.recursive = recursive;
        this.followingLinks = followLinks;
    }

    public boolean isFollowingLinks() {
        return followingLinks;
    }

    public void setFollowingLinks(boolean followLinks) {
        this.followingLinks = followLinks;
    }

    /**
     * Returns whether subdirectories are processed as well.
     * @return 
     */
    public boolean isRecursive() {
        return recursive;
    }

    /**
     * Sets whether subdirectories should be processed as well.
     * @param recursive 
     */
    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public List<File> listFiles(File root) {
        visited = new HashMap<>();
        List<File> result = new LinkedList<>();
        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File file) {
                return true;
            }
        };
        listFiles(root, filter, result);
        return result;
    }

    public List<File> listFiles(File root, final List<String> extensions) {
        visited = new HashMap<>();
        List<File> result = new LinkedList<>();
        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File file) {
                int index = file.getName().lastIndexOf(".");
                String extension = (index >= 0)? file.getName().substring(index+1) : "";                
                return extensions.contains(extension) || (file.isDirectory() && isRecursive()) || (Files.isSymbolicLink(file.toPath()) && isFollowingLinks());
            }
        };
        listFiles(root, filter, result);
        return result;
    }

    public List<File> listFiles(File root, FileFilter filter) {
        visited = new HashMap<>();
        List<File> result = new LinkedList<>();
        listFiles(root, filter, result);
        return result;
    }

    protected void listFiles(File root, FileFilter filter, List<File> list) {
        visited.put(root, true);
        for (File file : root.listFiles(filter)) {
            if (recursive && file.isDirectory() && file.canRead() && !visited.containsKey(file)) {
                if (!Files.isSymbolicLink(file.toPath())) {
                    listFiles(file, filter, list);
                } else {
                    if (followingLinks) {
                        try {
                            listFiles(file.getCanonicalFile(), filter, list);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            } else if (file.isFile()) {
                list.add(file);
            }
        }
    }
}
