/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.tu_berlin.math.coga.batch.input.reader;

import org.zetool.components.batch.input.reader.InputFileReader;
import static de.tu_berlin.math.coga.batch.input.reader.DimacsReader.commentLine;
import ds.graph.problem.RawMinimumCostFlowProblem;

/**
 * A reader for minimum cost flow problem instances stored in the DIMACS format:
 * http://dimacs.rutgers.edu/Challenges/
 * @author Martin Groß
 * @author Jan-Philipp Kappmeier
 */
public class DimacsMinimumCostFlowFileReader extends DimacsReader<RawMinimumCostFlowProblem> {
  boolean propertiesOnly = false;

  int[] caps = null;
  int[] costs = null;
  int currentEdgeIndex = 0;
  int[] ends = null;
  int[] starts = null;
  int numberOfEdges = -1;
  int numberOfNodes = -1;
  int numberOfSupply = 0;
  int[] supplies = null;

  int[] edgeCosts;
  int[] degrees;

  RawMinimumCostFlowProblem problem = null;

  private class CharP extends DimacsLineOperation {
    @Override
    public void parseLine( String[] tokens ) {
      assert numberOfNodes == -1 && numberOfEdges == -1 : "Problem has been defined twice.";
      assert tokens[0].equals("min") : "File is not specfied a minimum cost flow instance.";
      numberOfNodes = Integer.parseInt(tokens[1]);
      numberOfEdges = Integer.parseInt(tokens[2]);
      assert numberOfNodes >= 0 : "File specifies a negative amount of nodes.";
      assert numberOfEdges >= 0 : "File specifies a negative amount of edges.";
      supplies = new int[numberOfNodes];

      if (propertiesOnly) {
        //problem = new RawMaximumFlowProblem(numberOfNodes, numberOfEdges);
        //System.out.println( "Stopping with " + numberOfNodes + " nodes and " + numberOfEdges + " edges." );
        //setStop( true );
      } else {
          caps = new int[numberOfEdges];
          costs = new int[numberOfEdges];
          ends = new int[numberOfEdges];
          starts = new int[numberOfEdges];
      }
    }    
  }
  
  private class CharN extends DimacsLineOperation {
    @Override
    public void parseLine( String[] tokens ) {
      assert numberOfNodes >= 0 && numberOfEdges >= 0 : "File specifies terminals before the problem itself.";
      final int id = Integer.parseInt( tokens[0] ) - 1;
      final int supply = Integer.parseInt( tokens[1] );
      assert 0 <= id && id < numberOfNodes : "Terminal ID is invalid.";
      numberOfSupply += Math.max( supply, 0 );
      supplies[id] = supply;
    }
  }
  
  private class CharA extends DimacsLineOperation {
    @Override
    public void parseLine( String[] tokens ) {
      if( propertiesOnly ) {
        problem = new RawMinimumCostFlowProblem( numberOfNodes, numberOfEdges, numberOfSupply );
        System.out.println( "Stopping with " + numberOfNodes + " nodes and " + numberOfEdges + " edges." );
        setStop( true );
        return;
      }
      assert numberOfNodes >= 0 & numberOfEdges >= 0 : "Edges must be defined after sources and sinks.";
      final int start = Integer.parseInt( tokens[0] ) - 1;
      final int end = Integer.parseInt( tokens[1] ) - 1;
      final int capacity = Integer.parseInt( tokens[3] );
      final int cost = Integer.parseInt( tokens[4] );
      assert 0 <= start && start < numberOfNodes : "Illegal start node.";
      assert 0 <= end && end < numberOfNodes : "Illegal end node.";
      assert capacity >= 0 : "Negative capacities are not allowed.";
      assert start != end : "Loops are not allowed";
      caps[currentEdgeIndex] = capacity;
      costs[currentEdgeIndex] = cost;
      starts[currentEdgeIndex] = start;
      ends[currentEdgeIndex] = end;
      ++currentEdgeIndex;
    }
  }
  
  
  private class CharP2 extends DimacsLineOperation {
    @Override
    public void parseLine( String[] tokens ) {
                        assert numberOfNodes == -1 && numberOfEdges == -1 : "Problem has been defined twice.";
      assert tokens[1].equals( "min" ) : "File is not specfied a minimum cost flow instance.";
      numberOfNodes = Integer.parseInt( tokens[2] );
      numberOfEdges = Integer.parseInt( tokens[3] );
      assert numberOfNodes >= 0 : "File specifies a negative amount of nodes.";
      assert numberOfEdges >= 0 : "File specifies a negative amount of edges.";
      supplies = new int[numberOfNodes];
      if( !propertiesOnly ) {
        degrees = new int[numberOfNodes];
      }
    }
  }

  
  private class CharA2 extends DimacsLineOperation {
    @Override
    public void parseLine( String[] tokens ) {
      assert numberOfNodes >= 0 & numberOfEdges >= 0 : "Edges must be defined after sources and sinks.";
      final int start = Integer.parseInt(tokens[1]) - 1;
      ++degrees[start];
    }
  }
  
  private class CharA3 extends DimacsLineOperation {
    @Override
    public void parseLine( String[] tokens ) {
      assert numberOfNodes >= 0 & numberOfEdges >= 0 : "Edges must be defined after sources and sinks.";
      final int start = Integer.parseInt( tokens[1] ) - 1;
      final int end = Integer.parseInt( tokens[2] ) - 1;
      final int capacity = Integer.parseInt( tokens[4] );
      final int cost = Integer.parseInt( tokens[5] );
      assert 0 <= start && start < numberOfNodes : "Illegal start node.";
      assert 0 <= end && end < numberOfNodes : "Illegal end node.";
      assert capacity >= 0 : "Negative capacities are not allowed.";
      assert start != end : "Loops are not allowed";
      int index = nodeIndices[start] + indices[start]++;
      capacities[index] = capacity;
      costs[index] = cost;
      edges[index] = end;
    }
  }
  
  /** Caches the number of nodes and edges. */
  private String[] properties;

  @Override
  protected void preOperation() {
    problem = null;
    switch( getOptimization() ) {
      case SPEED:
        caps = null;
        costs = null;
        currentEdgeIndex = 0;
        ends = null;
        starts = null;
        numberOfEdges = -1;
        numberOfNodes = -1;
        numberOfSupply = 0;
        supplies = null;
        
        registerLineOperation( 'p', new CharP() );
        registerLineOperation( 'n', new CharN() );
        registerLineOperation( 'a', new CharA() );
        break;
      case MEMORY:
        numberOfEdges = -1;
        numberOfNodes = -1;
        registerLineOperation( 'p', new CharP2() );
        registerLineOperation( 'n', new CharN() );
        registerLineOperation( 'a', new CharA2() );
        break;
      default:
        throw new AssertionError( "Should not occur." );
    }
  }

    int[] nodeIndices;
    int[] capacities;
    int[] indices;
    int[] edges;

  @Override
  protected void phaseComplete() {
    if( getOptimization() == InputFileReader.OptimizationHint.MEMORY ) {
      nodeIndices = new int[numberOfNodes];
      nodeIndices[0] = 0;
      for( int i = 1; i < numberOfNodes; i++ ) {
        nodeIndices[i] = nodeIndices[i - 1] + degrees[i - 1];
      }
      capacities = new int[numberOfEdges];
      costs = new int[numberOfEdges];
      indices = new int[numberOfNodes];
      edges = new int[numberOfEdges];
        
      registerLineOperation( 'p', commentLine );
      registerLineOperation( 'n', commentLine );
      registerLineOperation( 'a', new CharA3() );      
    }
  }
  
  @Override
  protected RawMinimumCostFlowProblem postOperation() {
    if( problem != null ) { /// we have a properties only run and stopped
      return problem;
    }

    switch( getOptimization() ) {
      case SPEED:
        degrees = new int[numberOfNodes];
        for (int i = 0; i < numberOfEdges; i++) {
            ++degrees[starts[i]];
        }
        nodeIndices = new int[numberOfNodes];
        nodeIndices[0] = 0;
        for (int i = 1; i < numberOfNodes; i++) {
            nodeIndices[i] = nodeIndices[i - 1] + degrees[i - 1];
        }
        capacities = new int[numberOfEdges];
        edgeCosts = new int[numberOfEdges];
        indices = new int[numberOfNodes];
        edges = new int[numberOfEdges];
        for (int i = 0; i < numberOfEdges; i++) {
            int newIndex = nodeIndices[starts[i]] + indices[starts[i]]++;
            edges[newIndex] = ends[i];
            edgeCosts[newIndex] = costs[i];
            capacities[newIndex] = caps[i];
        }
        return new RawMinimumCostFlowProblem(numberOfNodes, numberOfEdges, numberOfSupply, nodeIndices, edges, capacities, edgeCosts, supplies);        
      case MEMORY:
        return new RawMinimumCostFlowProblem(numberOfNodes, numberOfEdges, numberOfSupply, nodeIndices, edges, capacities, costs, supplies);
      default:
        throw new AssertionError( "Cannot occur!" );
    }  
  }

  @Override
	public Class<RawMinimumCostFlowProblem> getTypeClass() {
		return RawMinimumCostFlowProblem.class;
  }

  @Override
  public String[] getProperties() {
    if( properties == null ) {
      properties = new String[3];
      if( !isProblemSolved() ) {
        propertiesOnly = true;
        runAlgorithm( getProblem() );
        propertiesOnly = false;
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
}
