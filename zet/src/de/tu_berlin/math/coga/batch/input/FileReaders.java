/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input;

import de.tu_berlin.math.coga.batch.input.reader.InputFileReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
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
    
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }    
    
    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
    
    public static void main(String[] args) {
        try {
            Class[] classes = getClasses("de.tu_berlin.math.coga.batch.input.reader");
            for (Class c : classes) {
                if (InputFileReader.class.isAssignableFrom(c)) {
                    System.out.println(c);
                }
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
