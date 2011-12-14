/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input.reader;

import ds.graph.problem.RawMaximumFlowProblem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * A reader for maximum flow problem instances stored in the DIMACS format: 
 * http://www.avglab.com/andrew/CATS/maxflow_formats.htm.
 * @author Martin Groß
 */
public class DimacsMaximumFlowFileReader extends InputFileReader<RawMaximumFlowProblem> {

    /**
     * Caches the number of nodes and edges.
     */
    private String[] properties;

    /**
     * Returns the number of nodes and edges of the maximum flow instance and
     * reads this information from the header of the file, if necessary.
     * @return an array containing the number of nodes as its first entry and 
     * the number of edges as its second.
     */
    @Override
    public String[] getProperties() {
        if (properties == null) {
            properties = new String[2];
            if (!isProblemSolved()) {
                RawMaximumFlowProblem problem = null;
                switch (getOptimization()) {
                    case SPEED:
                        problem = runAlgorithmSpeed(getProblem(), true);
                        break;
                    case MEMORY:
                        problem = runAlgorithmMemory(getProblem(), true);
                        break;
                    default:
                        throw new AssertionError("Should not occur.");
                }
                properties[0] = "" + problem.getNumberOfNodes();
                properties[1] = "" + problem.getNumberOfEdges();
            } else {
                properties[0] = "" + getSolution().getNumberOfNodes();
                properties[1] = "" + getSolution().getNumberOfEdges();
            }
        }
        return properties;
    }

    /**
     * Reads the maximum flow problem from the specified file.
     * @param file the file which contains the maximum flow problem.
     * @return the maximum flow problem. Requires 4n + 8m + O(1) storage.
     */
    @Override
    protected RawMaximumFlowProblem runAlgorithm(File file) {
        switch (getOptimization()) {
            case SPEED:
                return runAlgorithmSpeed(file, false);
            case MEMORY:
                return runAlgorithmMemory(file, false);
            default:
                throw new AssertionError("Should not occur.");
        }
    }

    /**
     * Reads the problem in a speed-optimized way. 12n + 20m + O(1) Bytes are
     * required.
     * @param file the file which contains the maximum flow problem.
     * @param propertiesOnly whether only the number of nodes and edges should 
     * be read. Much faster than reading the whole file.
     * @return the maximum flow problem. 4n + 8m + O(1) Bytes required for 
     * storage.
     */
    protected RawMaximumFlowProblem runAlgorithmSpeed(File file, boolean propertiesOnly) {
        int[] caps = null;
        int currentEdgeIndex = 0;
        int[] ends = null;
        int[] starts = null;
        int numberOfEdges = -1;
        int numberOfNodes = -1;
        int sinkIndex = -1;
        int sourceIndex = -1;
        String line = null;
        int lineIndex = 1;
        String[] tokens;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((line = reader.readLine()) != null) {
                tokens = line.split("\\s+");
                switch (tokens[0]) {
                    case "c":
                        break;
                    case "p":
                        assert numberOfNodes == -1 && numberOfEdges == -1 : "Problem has been defined twice.";
                        assert tokens[1].equals("max") : "File is not specfied a maximum flow instance.";
                        numberOfNodes = Integer.parseInt(tokens[2]);
                        numberOfEdges = Integer.parseInt(tokens[3]);
                        assert numberOfNodes >= 0 : "File specifies a negative amount of nodes.";
                        assert numberOfEdges >= 0 : "File specifies a negative amount of edges.";
                        if (propertiesOnly) {
                            return new RawMaximumFlowProblem(numberOfNodes, numberOfEdges);
                        } else {
                            caps = new int[numberOfEdges];
                            ends = new int[numberOfEdges];
                            starts = new int[numberOfEdges];
                        }
                        break;
                    case "n":
                        assert numberOfNodes >= 0 && numberOfEdges >= 0 : "File specifies terminals before the problem itself.";
                        final int id = Integer.parseInt(tokens[1]) - 1;
                        assert 0 <= id && id < numberOfNodes : "Terminal ID is invalid.";
                        switch (tokens[2]) {
                            case "s":
                                assert sourceIndex == -1 : "File contains multiple sources.";
                                sourceIndex = id;
                                break;
                            case "t":
                                assert sinkIndex == -1 : "File contains multiple sinks.";
                                sinkIndex = id;
                                break;
                            default:
                                throw new IllegalArgumentException("Illegal node descriptor.");
                        }
                        break;
                    case "a":
                        assert numberOfNodes >= 0 & numberOfEdges >= 0 && sourceIndex >= 0 && sinkIndex >= 0 : "Edges must be defined after sources and sinks.";
                        final int start = Integer.parseInt(tokens[1]) - 1;
                        final int end = Integer.parseInt(tokens[2]) - 1;
                        final int capacity = Integer.parseInt(tokens[3]);
                        assert 0 <= start && start < numberOfNodes : "Illegal start node.";
                        assert 0 <= end && end < numberOfNodes : "Illegal end node.";
                        assert capacity >= 0 : "Negative capacities are not allowed.";
                        assert start != end : "Loops are not allowed";
                        caps[currentEdgeIndex] = capacity;
                        starts[currentEdgeIndex] = start;
                        ends[currentEdgeIndex] = end;
                        ++currentEdgeIndex;
                        break;
                }
                ++lineIndex;
            }
            assert currentEdgeIndex == numberOfEdges : "Edges not of the specified size-";
        } catch (AssertionError error) {
            System.err.println("Error reading " + file);
            System.err.println(error.getMessage());
            System.err.println(lineIndex + ": " + line);
        } catch (IOException ex) {
            System.err.println("Exception during DimacsLoader loaded file from " + file);
        }
        int[] degrees = new int[numberOfNodes];
        for (int i = 0; i < numberOfEdges; i++) {
            ++degrees[starts[i]];
        }
        int[] nodeIndices = new int[numberOfNodes];
        nodeIndices[0] = 0;
        for (int i = 1; i < numberOfNodes; i++) {
            nodeIndices[i] = nodeIndices[i - 1] + degrees[i - 1];
        }
        int[] capacities = new int[numberOfEdges];
        int[] indices = new int[numberOfNodes];
        int[] edges = new int[numberOfEdges];
        for (int i = 0; i < numberOfEdges; i++) {
            int newIndex = nodeIndices[starts[i]] + indices[starts[i]]++;
            edges[newIndex] = ends[i];
            capacities[newIndex] = caps[i];
        }
        return new RawMaximumFlowProblem(numberOfNodes, numberOfEdges, nodeIndices, edges, capacities, sinkIndex, sourceIndex);
    }

    /**
     * Reads the problem in a speed-optimized way. 12n + 8m + O(1) Bytes are
     * required.
     * @param file the file which contains the maximum flow problem.
     * @param propertiesOnly whether only the number of nodes and edges should 
     * be read. Much faster than reading the whole file.
     * @return the maximum flow problem. 4n + 8m + O(1) Bytes required for 
     * storage.
     */    
    protected  RawMaximumFlowProblem runAlgorithmMemory(File file, boolean propertiesOnly) {
        int numberOfEdges = -1;
        int numberOfNodes = -1;
        int sinkIndex = -1;
        int sourceIndex = -1;
        String line = null;
        int lineIndex = 1;
        int[] degrees = null;
        String[] tokens;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((line = reader.readLine()) != null) {
                tokens = line.split("\\s+");
                switch (tokens[0]) {
                    case "c":
                        break;
                    case "p":
                        assert numberOfNodes == -1 && numberOfEdges == -1 : "Problem has been defined twice.";
                        assert tokens[1].equals("max") : "File is not specfied a maximum flow instance.";
                        numberOfNodes = Integer.parseInt(tokens[2]);
                        numberOfEdges = Integer.parseInt(tokens[3]);
                        assert numberOfNodes >= 0 : "File specifies a negative amount of nodes.";
                        assert numberOfEdges >= 0 : "File specifies a negative amount of edges.";
                        if (propertiesOnly) {
                            return new RawMaximumFlowProblem(numberOfNodes, numberOfEdges);
                        } else {
                            degrees = new int[numberOfNodes];
                        }
                        break;
                    case "n":
                        assert numberOfNodes >= 0 && numberOfEdges >= 0 : "File specifies terminals before the problem itself.";
                        final int id = Integer.parseInt(tokens[1]) - 1;
                        assert 0 <= id && id < numberOfNodes : "Terminal ID is invalid.";
                        switch (tokens[2]) {
                            case "s":
                                assert sourceIndex == -1 : "File contains multiple sources.";
                                sourceIndex = id;
                                break;
                            case "t":
                                assert sinkIndex == -1 : "File contains multiple sinks.";
                                sinkIndex = id;
                                break;
                            default:
                                throw new IllegalArgumentException("Illegal node descriptor.");
                        }
                        break;
                    case "a":
                        assert numberOfNodes >= 0 & numberOfEdges >= 0 && sourceIndex >= 0 && sinkIndex >= 0 : "Edges must be defined after sources and sinks.";
                        final int start = Integer.parseInt(tokens[1]) - 1;
                        ++degrees[start];
                        break;
                }
                ++lineIndex;
            }
        } catch (AssertionError error) {
            System.err.println("Error reading " + file);
            System.err.println(error.getMessage());
            System.err.println(lineIndex + ": " + line);
        } catch (IOException ex) {
            System.err.println("Exception during DimacsLoader loaded file from " + file);
        }
        int[] nodeIndices = new int[numberOfNodes];
        nodeIndices[0] = 0;
        for (int i = 1; i < numberOfNodes; i++) {
            nodeIndices[i] = nodeIndices[i - 1] + degrees[i - 1];
        }
        int[] capacities = new int[numberOfEdges];
        int[] indices = new int[numberOfNodes];
        int[] edges = new int[numberOfEdges];

        lineIndex = 1;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((line = reader.readLine()) != null) {
                tokens = line.split("\\s+");
                switch (tokens[0]) {
                    case "c":
                    case "p":
                    case "n":
                        break;
                    case "a":
                        assert numberOfNodes >= 0 & numberOfEdges >= 0 && sourceIndex >= 0 && sinkIndex >= 0 : "Edges must be defined after sources and sinks.";
                        final int start = Integer.parseInt(tokens[1]) - 1;
                        final int end = Integer.parseInt(tokens[2]) - 1;
                        final int capacity = Integer.parseInt(tokens[3]);
                        assert 0 <= start && start < numberOfNodes : "Illegal start node.";
                        assert 0 <= end && end < numberOfNodes : "Illegal end node.";
                        assert capacity >= 0 : "Negative capacities are not allowed.";
                        assert start != end : "Loops are not allowed";
                        int index = nodeIndices[start] + indices[start]++;
                        capacities[index] = capacity;
                        edges[index] = end;
                        break;
                }
                ++lineIndex;
            }
        } catch (AssertionError error) {
            System.err.println("Error reading " + file);
            System.err.println(error.getMessage());
            System.err.println(lineIndex + ": " + line);
        } catch (IOException ex) {
            System.err.println("Exception during DimacsLoader loaded file from " + file);
        }
        return new RawMaximumFlowProblem(numberOfNodes, numberOfEdges, nodeIndices, edges, capacities, sinkIndex, sourceIndex);
    }

    public static void main(String[] args) {
        DimacsMaximumFlowFileReader reader = new DimacsMaximumFlowFileReader();
        reader.setFile(new File("/homes/combi/gross/Desktop/liver.n6c10.max"));
        System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        reader.setOptimization(Optimization.SPEED);
        reader.run();
        RawMaximumFlowProblem p = reader.getSolution();
        reader = null;
        System.gc();
        System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - p.getNumberOfEdges() * 8 - p.getNumberOfNodes() * 4);
        System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - p.getNumberOfEdges() * 8 - p.getNumberOfNodes() * 12);
        System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - p.getNumberOfEdges() * 20 - p.getNumberOfNodes() * 12);
        //System.out.println();
//        System.out.println("Runtime: " + reader.getRuntimeAsString());
    }
}