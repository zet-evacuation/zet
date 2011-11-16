/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.algorithm;

import de.tu_berlin.math.coga.batch.input.FileReader;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.DynamicNetwork;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
public class Algorithms {
    
    private static Algorithms instance;

    public static Algorithms getInstance() {
        if (instance == null) {
            instance = new Algorithms();
        }
        return instance;
    }

    private Network network;
    
    private HashMap<Edge, Algorithm> algorithmForEdge;
    private HashMap<Class, Node> nodeForClass;    
    
    private Algorithms() {
        algorithmForEdge = new HashMap<Edge, Algorithm>();
        nodeForClass = new HashMap<Class, Node>();        
    }

    public List<Algorithm<?,?>> getAlgorithms(Class<?> problem, Class<?> solution) {
        Path path = network.getPath(nodeForClass.get(problem), nodeForClass.get(solution));
        if (path == null) {
            return null;
        } else {
            LinkedList<Algorithm<?,?>> result = new LinkedList<Algorithm<?, ?>>();
            for (Edge edge : path) {
                result.add(algorithmForEdge.get(edge));
            }
            return result;
        }
    }

    public <P,S> void registerAlgorithm(Algorithm<P,S> algorithm, Class<P> problem, Class<S> solution) {
        Node problemNode = nodeForClass.get(problem);
        Node solutionNode = nodeForClass.get(solution);
        if (problemNode == null) {
            network.setNodeCapacity(network.getNodeCapacity() + 1);
            problemNode = network.getNode(network.numberOfNodes() - 1);
            nodeForClass.put(problem, problemNode);
        }
        if (solutionNode == null) {
            network.setNodeCapacity(network.getNodeCapacity() + 1);
            solutionNode = network.getNode(network.numberOfNodes() - 1);
            nodeForClass.put(solution, solutionNode);
        }
        network.setEdgeCapacity(network.getEdgeCapacity() + 1);
        Edge edge = network.createAndSetEdge(problemNode, solutionNode);
        algorithmForEdge.put(edge, algorithm);
    }
}
