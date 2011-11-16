/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
public class FileReaders {

    private static FileReaders instance;

    public static FileReaders getInstance() {
        if (instance == null) {
            instance = new FileReaders();
        }
        return instance;
    }
    private List<FileReader> readers;
    private LinkedHashMap<String, List<FileReader>> readersForExtension;

    private FileReaders() {
        readers = new LinkedList<FileReader>();
        readersForExtension = new LinkedHashMap<String, List<FileReader>>();
    }

    public List getReaders() {
        return readers;
    }

    public List getReadersForExtension(String extension) {
        return readersForExtension.get(extension);
    }

    public void registerReader(FileReader<?> reader) {
        registerReader(reader, reader.getSupportedExtensions());
    }

    protected void registerReader(FileReader<?> reader, String[] extensions) {
        for (String extension : extensions) {
            if (!readersForExtension.containsKey(extension)) {
                readersForExtension.put(extension, new LinkedList<FileReader>());
            }
            readersForExtension.get(extension).add(reader);
        }
        readers.add(reader);
    }

    public void unregisterReader(FileReader<?> reader) {
        for (String extension : readersForExtension.keySet()) {
            if (readersForExtension.get(extension) != null) {
                readersForExtension.get(extension).remove(reader);
            }
        }
        readers.remove(reader);
    }
}
