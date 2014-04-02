/*
 * DimacsMinimumCostFlowFileReader.java
 *
 */
package de.tu_berlin.math.coga.batch.input.reader;

import ds.graph.problem.RawMinimumCostFlowProblem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * A reader for minimum cost flow problem instances stored in the DIMACS format:
 * http://dimacs.rutgers.edu/Challenges/
 * @author Martin Groß
 */
public class DimacsMinimumCostFlowFileReader extends InputFileReader<RawMinimumCostFlowProblem> {
	@Override
	public Class<RawMinimumCostFlowProblem> getTypeClass() {
		return RawMinimumCostFlowProblem.class;
	}

    /**
     * Caches the number of nodes and edges.
     */
    private String[] properties;

    /**
     * Returns the number of nodes, edges and total supply of the minimum cost
     * flow instance and reads this information from the header of the file, if
     * necessary.
     * @return an array containing the number of nodes as its first entry,
     * the number of edges as its second and the total supply as its third.
     */
    @Override
    public String[] getProperties() {
        if (properties == null) {
            properties = new String[3];
            if (!isProblemSolved()) {
                RawMinimumCostFlowProblem problem = null;
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
                properties[2] = "" + problem.getNumberOfSupply();
            } else {
                properties[0] = "" + getSolution().getNumberOfNodes();
                properties[1] = "" + getSolution().getNumberOfEdges();
                properties[2] = "" + getSolution().getNumberOfSupply();
            }
        }
        return properties;
    }

    /**
     * Reads the minimum cost flow problem from the specified file.
     * @param file the file which contains the minimum cost flow problem.
     * @return the minimum cost flow problem. Requires 8n + 12m + O(1) storage.
     */
    @Override
    protected RawMinimumCostFlowProblem runAlgorithm(File file) {
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
     * Reads the problem in a speed-optimized way. 16n + 28m + O(1) Bytes are
     * required.
     * @param file the file which contains the minimum cost flow problem.
     * @param propertiesOnly whether only the number of nodes, edges and supply
     * should be read. Much faster than reading the whole file.
     * @return the minimum cost flow problem. 8n + 12m + O(1) Bytes required for
     * storage.
     */
    protected RawMinimumCostFlowProblem runAlgorithmSpeed(File file, boolean propertiesOnly) {
        int[] caps = null;
        int[] costs = null;
        int currentEdgeIndex = 0;
        int[] ends = null;
        int[] starts = null;
        int numberOfEdges = -1;
        int numberOfNodes = -1;
        int numberOfSupply = 0;
        int[] supplies = null;
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
                        assert tokens[1].equals("min") : "File is not specfied a minimum cost flow instance.";
                        numberOfNodes = Integer.parseInt(tokens[2]);
                        numberOfEdges = Integer.parseInt(tokens[3]);
                        assert numberOfNodes >= 0 : "File specifies a negative amount of nodes.";
                        assert numberOfEdges >= 0 : "File specifies a negative amount of edges.";
                        supplies = new int[numberOfNodes];
                        if (!propertiesOnly) {
                            caps = new int[numberOfEdges];
                            costs = new int[numberOfEdges];
                            ends = new int[numberOfEdges];
                            starts = new int[numberOfEdges];
                        }
                        break;
                    case "n":
                        assert numberOfNodes >= 0 && numberOfEdges >= 0 : "File specifies terminals before the problem itself.";
                        final int id = Integer.parseInt(tokens[1]) - 1;
                        final int supply = Integer.parseInt(tokens[2]);
                        assert 0 <= id && id < numberOfNodes : "Terminal ID is invalid.";
                        numberOfSupply += Math.max(supply, 0);
                        supplies[id] = supply;
                        break;
                    case "a":
                        if (propertiesOnly) {
                            return new RawMinimumCostFlowProblem(numberOfNodes, numberOfEdges, numberOfSupply);
                        }
                        assert numberOfNodes >= 0 & numberOfEdges >= 0 : "Edges must be defined after sources and sinks.";
                        final int start = Integer.parseInt(tokens[1]) - 1;
                        final int end = Integer.parseInt(tokens[2]) - 1;
                        final int capacity = Integer.parseInt(tokens[4]);
                        final int cost = Integer.parseInt(tokens[5]);
                        assert 0 <= start && start < numberOfNodes : "Illegal start node.";
                        assert 0 <= end && end < numberOfNodes : "Illegal end node.";
                        assert capacity >= 0 : "Negative capacities are not allowed.";
                        assert start != end : "Loops are not allowed";
                        caps[currentEdgeIndex] = capacity;
                        costs[currentEdgeIndex] = cost;
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
        int[] edgeCosts = new int[numberOfEdges];
        int[] indices = new int[numberOfNodes];
        int[] edges = new int[numberOfEdges];
        for (int i = 0; i < numberOfEdges; i++) {
            int newIndex = nodeIndices[starts[i]] + indices[starts[i]]++;
            edges[newIndex] = ends[i];
            edgeCosts[newIndex] = costs[i];
            capacities[newIndex] = caps[i];
        }
        return new RawMinimumCostFlowProblem(numberOfNodes, numberOfEdges, numberOfSupply, nodeIndices, edges, capacities, edgeCosts, supplies);
    }

    /**
     * Reads the problem in a speed-optimized way. 12n + 8m + O(1) Bytes are
     * required.
     * @param file the file which contains the minimum cost flow problem.
     * @param propertiesOnly whether only the number of nodes and edges should
     * be read. Much faster than reading the whole file.
     * @return the minimum cost flow problem. 4n + 8m + O(1) Bytes required for
     * storage.
     */
    protected  RawMinimumCostFlowProblem runAlgorithmMemory(File file, boolean propertiesOnly) {
        int numberOfEdges = -1;
        int numberOfNodes = -1;
        String line = null;
        int lineIndex = 1;
        int[] degrees = null;
        int numberOfSupply = 0;
        int[] supplies = null;
        String[] tokens;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((line = reader.readLine()) != null) {
                tokens = line.split("\\s+");
                switch (tokens[0]) {
                    case "c":
                        break;
                    case "p":
                        assert numberOfNodes == -1 && numberOfEdges == -1 : "Problem has been defined twice.";
                        assert tokens[1].equals("min") : "File is not specfied a minimum cost flow instance.";
                        numberOfNodes = Integer.parseInt(tokens[2]);
                        numberOfEdges = Integer.parseInt(tokens[3]);
                        assert numberOfNodes >= 0 : "File specifies a negative amount of nodes.";
                        assert numberOfEdges >= 0 : "File specifies a negative amount of edges.";
                        supplies = new int[numberOfNodes];
                        if (!propertiesOnly) {
                            degrees = new int[numberOfNodes];
                        }
                        break;
                    case "n":
                        assert numberOfNodes >= 0 && numberOfEdges >= 0 : "File specifies terminals before the problem itself.";
                        final int id = Integer.parseInt(tokens[1]) - 1;
                        final int supply = Integer.parseInt(tokens[2]);
                        assert 0 <= id && id < numberOfNodes : "Terminal ID is invalid.";
                        numberOfSupply += Math.max(supply, 0);
                        supplies[id] = supply;
                        break;
                    case "a":
                        assert numberOfNodes >= 0 & numberOfEdges >= 0 : "Edges must be defined after sources and sinks.";
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
        int[] costs = new int[numberOfEdges];
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
                        assert numberOfNodes >= 0 & numberOfEdges >= 0 : "Edges must be defined after sources and sinks.";
                        final int start = Integer.parseInt(tokens[1]) - 1;
                        final int end = Integer.parseInt(tokens[2]) - 1;
                        final int capacity = Integer.parseInt(tokens[4]);
                        final int cost = Integer.parseInt(tokens[5]);
                        assert 0 <= start && start < numberOfNodes : "Illegal start node.";
                        assert 0 <= end && end < numberOfNodes : "Illegal end node.";
                        assert capacity >= 0 : "Negative capacities are not allowed.";
                        assert start != end : "Loops are not allowed";
                        int index = nodeIndices[start] + indices[start]++;
                        capacities[index] = capacity;
                        costs[index] = cost;
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
        return new RawMinimumCostFlowProblem(numberOfNodes, numberOfEdges, numberOfSupply, nodeIndices, edges, capacities, costs, supplies);
    }
}
