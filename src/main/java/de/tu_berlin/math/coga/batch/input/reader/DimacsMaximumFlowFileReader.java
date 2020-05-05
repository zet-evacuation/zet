/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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

import ds.graph.problem.RawMaximumFlowProblem;

/**
 * A reader for maximum flow problem instances stored in the DIMACS format:
 * http://www.avglab.com/andrew/CATS/maxflow_formats.htm.
 *
 * @author Martin Groß
 * @author Jan-Philipp Kappmeier
 */
public class DimacsMaximumFlowFileReader extends DimacsReader<RawMaximumFlowProblem> {
  boolean propertiesOnly = false;

  int[] caps = null;
  int currentEdgeIndex = 0;
  int[] ends = null;
  int[] starts = null;
  int numberOfEdges = -1;
  int numberOfNodes = -1;
  int sinkIndex = -1;
  int sourceIndex = -1;

  int lineIndex = 1;
  int[] degrees = null;

  RawMaximumFlowProblem sol; //(numberOfNodes, numberOfEdges);

  private class CharP extends DimacsLineOperation {
    @Override
    public void parseLine( String[] tokens ) {
      assert numberOfNodes == -1 && numberOfEdges == -1 : "Problem has been defined twice.";
      assert tokens[0].equals("max") : "File is not specfied a maximum flow instance.";
      numberOfNodes = Integer.parseInt(tokens[1]);
      numberOfEdges = Integer.parseInt(tokens[2]);
      assert numberOfNodes >= 0 : "File specifies a negative amount of nodes.";
      assert numberOfEdges >= 0 : "File specifies a negative amount of edges.";
      if (propertiesOnly) {
        sol = new RawMaximumFlowProblem(numberOfNodes, numberOfEdges);
        System.out.println( "Stopping with " + numberOfNodes + " nodes and " + numberOfEdges + " edges." );
        setStop( true );
      } else {
        caps = new int[numberOfEdges];
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
      assert 0 <= id && id < numberOfNodes : "Terminal ID is invalid.";
      switch( tokens[1] ) {
        case "s":
          assert sourceIndex == -1 : "File contains multiple sources.";
          sourceIndex = id;
          break;
        case "t":
          assert sinkIndex == -1 : "File contains multiple sinks.";
          sinkIndex = id;
          break;
        default:
          throw new IllegalArgumentException( "Illegal node descriptor." );
      }
    }
  }
  
  private class CharA extends DimacsLineOperation {
    @Override
    public void parseLine( String[] tokens ) {
      assert numberOfNodes >= 0 & numberOfEdges >= 0 && sourceIndex >= 0 && sinkIndex >= 0 : "Edges must be defined after sources and sinks.";
      final int start = Integer.parseInt( tokens[0] ) - 1;
      final int end = Integer.parseInt( tokens[1] ) - 1;
      final int capacity = Integer.parseInt( tokens[2] );
      assert 0 <= start && start < numberOfNodes : "Illegal start node.";
      assert 0 <= end && end < numberOfNodes : "Illegal end node.";
      assert capacity >= 0 : "Negative capacities are not allowed.";
      assert start != end : "Loops are not allowed";
      caps[currentEdgeIndex] = capacity;
      starts[currentEdgeIndex] = start;
      ends[currentEdgeIndex] = end;
      ++currentEdgeIndex;
    }  
  }
  
  
  
  private class CharP2 extends DimacsLineOperation {
    @Override
    public void parseLine( String[] tokens ) {
      assert numberOfNodes == -1 && numberOfEdges == -1 : "Problem has been defined twice.";
      assert tokens[1].equals( "max" ) : "File is not specfied a maximum flow instance.";
      numberOfNodes = Integer.parseInt( tokens[1] );
      numberOfEdges = Integer.parseInt( tokens[2] );
      assert numberOfNodes >= 0 : "File specifies a negative amount of nodes.";
      assert numberOfEdges >= 0 : "File specifies a negative amount of edges.";

      if (propertiesOnly) {
        sol = new RawMaximumFlowProblem(numberOfNodes, numberOfEdges);
        System.out.println( "Stopping with " + numberOfNodes + " nodes and " + numberOfEdges + " edges." );
        setStop( true );
      } else {
        degrees = new int[numberOfNodes];
      }
    }
  }

  
  private class CharA2 extends DimacsLineOperation {
    @Override
    public void parseLine( String[] tokens ) {
      assert numberOfNodes >= 0 & numberOfEdges >= 0 && sourceIndex >= 0 && sinkIndex >= 0 : "Edges must be defined after sources and sinks.";
      final int start = Integer.parseInt( tokens[1] ) - 1;
      ++degrees[start];
    }
  }
  
  private class CharA3 extends DimacsLineOperation {
    @Override
    public void parseLine( String[] tokens ) {
      assert numberOfNodes >= 0 & numberOfEdges >= 0 && sourceIndex >= 0 && sinkIndex >= 0 : "Edges must be defined after sources and sinks.";
      final int start = Integer.parseInt( tokens[0] ) - 1;
      final int end = Integer.parseInt( tokens[1] ) - 1;
      final int capacity = Integer.parseInt( tokens[2] );
      assert 0 <= start && start < numberOfNodes : "Illegal start node.";
      assert 0 <= end && end < numberOfNodes : "Illegal end node.";
      assert capacity >= 0 : "Negative capacities are not allowed.";
      assert start != end : "Loops are not allowed";
      int index = nodeIndices[start] + indices[start]++;
      capacities[index] = capacity;
      edges[index] = end;
    }
  }
  
  
  
  /** Caches the number of nodes and edges. */
  private String[] properties;

  @Override
  protected void preOperation() {
    sol = null;
    switch( getOptimization() ) {
      case SPEED:
        currentEdgeIndex = 0;
        numberOfEdges = -1;
        numberOfNodes = -1;
        sinkIndex = -1;
        sourceIndex = -1;
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
    if( getOptimization() == OptimizationHint.MEMORY ) {
      nodeIndices = new int[numberOfNodes];
      nodeIndices[0] = 0;
      for( int i = 1; i < numberOfNodes; i++ ) {
        nodeIndices[i] = nodeIndices[i - 1] + degrees[i - 1];
      }
      capacities = new int[numberOfEdges];
      indices = new int[numberOfNodes];
      edges = new int[numberOfEdges];
      registerLineOperation( 'p', commentLine );
      registerLineOperation( 'n', commentLine );
      registerLineOperation( 'n', new CharA3() );
    }
  }

  
  
  @Override
  protected RawMaximumFlowProblem postOperation() {
    if( sol != null ) { /// we have a properties only run and stopped
      return sol;
    }

    switch( getOptimization() ) {
      case SPEED:
        degrees = new int[numberOfNodes];
        for( int i = 0; i < numberOfEdges; i++ ) {
          ++degrees[starts[i]];
        }
        nodeIndices = new int[numberOfNodes];
        nodeIndices[0] = 0;
        for( int i = 1; i < numberOfNodes; i++ ) {
          nodeIndices[i] = nodeIndices[i - 1] + degrees[i - 1];
        }
        capacities = new int[numberOfEdges];
        indices = new int[numberOfNodes];
        edges = new int[numberOfEdges];
        for( int i = 0; i < numberOfEdges; i++ ) {
          int newIndex = nodeIndices[starts[i]] + indices[starts[i]]++;
          edges[newIndex] = ends[i];
          capacities[newIndex] = caps[i];
        }
        return new RawMaximumFlowProblem( numberOfNodes, numberOfEdges, nodeIndices, edges, capacities, sinkIndex, sourceIndex );
      case MEMORY:
        return new RawMaximumFlowProblem( numberOfNodes, numberOfEdges, nodeIndices, edges, capacities, sinkIndex, sourceIndex );
      default:
        throw new AssertionError( "Cannot occur!" );
    }  
  }

  @Override
  public Class<RawMaximumFlowProblem> getTypeClass() {
    return RawMaximumFlowProblem.class;
  }

  @Override
  public String[] getProperties() {
    if( properties == null ) {
      properties = new String[2];
      if( !isProblemSolved() ) {
        propertiesOnly = true;
        runAlgorithm( getProblem() );
        propertiesOnly = false;
        properties[0] = "" + sol.getNumberOfNodes();
        properties[1] = "" + sol.getNumberOfEdges();
      } else {
        properties[0] = "" + getSolution().getNumberOfNodes();
        properties[1] = "" + getSolution().getNumberOfEdges();
      }
    }
    return properties;
  }
}
