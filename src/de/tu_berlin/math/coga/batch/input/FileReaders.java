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
    private List<InputFileReader> readers;
    private LinkedHashMap<String, List<InputFileReader>> readersForExtension;

    private FileReaders() {
        readers = new LinkedList<InputFileReader>();
        readersForExtension = new LinkedHashMap<String, List<InputFileReader>>();
    }

    public List getReaders() {
        return readers;
    }

    public List getReadersForExtension(String extension) {
        return readersForExtension.get(extension);
    }

    public void registerReader(InputFileReader<?> reader) {
        //registerReader(reader, reader.getSupportedExtensions());
    }

    protected void registerReader(InputFileReader<?> reader, String[] extensions) {
        for (String extension : extensions) {
            if (!readersForExtension.containsKey(extension)) {
                readersForExtension.put(extension, new LinkedList<InputFileReader>());
            }
            readersForExtension.get(extension).add(reader);
        }
        readers.add(reader);
    }

    public void unregisterReader(InputFileReader<?> reader) {
        for (String extension : readersForExtension.keySet()) {
            if (readersForExtension.get(extension) != null) {
                readersForExtension.get(extension).remove(reader);
            }
        }
        readers.remove(reader);
    }
}
