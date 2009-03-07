/*
 * GridGenerator.java
 *
 */

package algo.graph.util.generator;

import ds.graph.Identifiable;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import java.util.Random;

/**
 *
 */
public class GridGenerator {
    
    public static Network generateGridNetwork(int width, int height) {
        Network network = new Network(width * height, 4 * width * height - 2 * (width + height));
        for (int row = 0; row < height; row++) {
            for (int column = 1; column < width; column++) {
                network.createAndSetEdge(network.getNode(row * width + column - 1), network.getNode(row * width + column));
                network.createAndSetEdge(network.getNode(row * width + column), network.getNode(row * width + column - 1));
            }
        }
        for (int column = 0; column < width; column++) {
            for (int row = 1; row < height; row++) {            
                network.createAndSetEdge(network.getNode((row - 1) * width + column), network.getNode(row * width + column));
                network.createAndSetEdge(network.getNode(row * width + column), network.getNode((row - 1) * width + column));
            }
        }        
        return network;
    }
    
    public static Network generateDualGridNetwork(int width, int height) {
        width--;
        height--;
        Network network = new Network(width * height + 2, 2 * width * height + (width + height));
        Node source = network.getNode(network.numberOfNodes() - 2);
        Node sink = network.getNode(network.numberOfNodes() - 1);
        for (int row = 0; row < height; row++) {
            for (int column = 1; column < width; column++) {
                network.createAndSetEdge(network.getNode(row * width + column - 1), network.getNode(row * width + column));
            }
        }
        for (int column = 0; column < width; column++) {
            for (int row = 1; row < height; row++) {            
                network.createAndSetEdge(network.getNode((row - 1) * width + column), network.getNode(row * width + column));
            }
        }        
        for (int column = 0; column < width; column++) {
            network.createAndSetEdge(source, network.getNode(column));
        }                
        for (int row = 0; row < height; row++) {            
            network.createAndSetEdge(source, network.getNode(row * width + width - 1));
        }                
        for (int column = 0; column < width; column++) {
            network.createAndSetEdge(network.getNode((height - 1) * width + column), sink);
        }                
        for (int row = 0; row < height; row++) {            
            network.createAndSetEdge(network.getNode(row * width), sink);
        }        
        return network;
    }
    
    public static <T extends Identifiable> IdentifiableIntegerMapping<T> createCostFunction(int width, int height, Network network, IdentifiableIntegerMapping<T> capacities) {
        IdentifiableIntegerMapping<T> costs = null;
        Node source = network.getNode(network.numberOfNodes() - 2);
        Node sink = network.getNode(network.numberOfNodes() - 1);
        for (int row = 0; row < height; row++) {
            for (int column = 1; column < width; column++) {
                //network.createAndSetEdge(network.getNode(row * width + column - 1), network.getNode(row * width + column));
                //capacities.get(identifiableObject);
            }
        }
        for (int column = 0; column < width; column++) {
            for (int row = 1; row < height; row++) {            
                network.createAndSetEdge(network.getNode((row - 1) * width + column), network.getNode(row * width + column));
            }
        }        
        for (int column = 0; column < width; column++) {
            network.createAndSetEdge(source, network.getNode(column));
        }                
        for (int row = 0; row < height; row++) {            
            network.createAndSetEdge(source, network.getNode(row * width + width - 1));
        }                
        for (int column = 0; column < width; column++) {
            network.createAndSetEdge(network.getNode((height - 1) * width + column), sink);
        }                
        for (int row = 0; row < height; row++) {            
            network.createAndSetEdge(network.getNode(row * width), sink);
        }        
        return costs;
    }    
    
    public static <T extends Identifiable> IdentifiableIntegerMapping<T> generateMappingWithUniformlyDistributedFunctionValues(Iterable<T> domain, int min, int max) {
        long seed = System.nanoTime();
        System.out.println("Seed: " + seed);
        return generateMappingWithUniformlyDistributedFunctionValues(domain, min, max, seed);
    }
    
    public static <T extends Identifiable> IdentifiableIntegerMapping<T> generateMappingWithUniformlyDistributedFunctionValues(Iterable<T> domain, int min, int max, long seed) {
        int largestID = 0;
        for (T x : domain) {
            if (largestID < x.id()) largestID = x.id();
        }
        IdentifiableIntegerMapping<T> mapping = new IdentifiableIntegerMapping<T>(largestID + 1);
        Random rng = new Random(seed);
        for (T x : domain) {
            mapping.set(x, rng.nextInt(max - min + 1) + min);
        }
        return mapping;
    }

}
