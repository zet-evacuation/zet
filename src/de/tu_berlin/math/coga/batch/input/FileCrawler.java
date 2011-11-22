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

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    protected boolean isSymlink(File file) throws IOException {
        if (file == null) {
            return false;
        }
        File canonicalParent = (file.getParentFile() == null) ? null : file.getParentFile().getCanonicalFile();
        File canonical = (canonicalParent == null) ? file : new File(canonicalParent, file.getName());
        return !canonical.getCanonicalFile().equals(canonical.getAbsoluteFile());
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

    public List<File> listFiles(File root, List<String> extensions) {
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
