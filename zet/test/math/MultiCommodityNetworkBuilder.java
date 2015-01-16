
package math;

import org.zetool.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.graph.DefaultDirectedGraph;
import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.coga.graph.Node;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class MultiCommodityNetworkBuilder {
  static HashMap<Node,Integer> sourceCommodityMapping;
  static HashMap<Node,Integer> sinkCommodityMapping;
  static DefaultDirectedGraph network;
	static IdentifiableIntegerMapping<Edge> capacities;
	static IdentifiableIntegerMapping<Node> supplies;
  static IdentifiableIntegerMapping<Edge> transitTimes;
  static int T;
  static int pathLength;
  static Set<String> usedPaths = new HashSet<>();
  static StringBuilder sb = new StringBuilder();
  static String[] outputLines;
  private static PrintWriter output = new PrintWriter( System.out );
  
  public static void addElements( LinkedList<Integer> list, int l, int k ) {
    if( l >= k ) { // Alternative: l > k, seems more natural but the examples so far use only l >= k.
      return;
    }
    for( int i = 1; i <= k; ++i ) {
      list.add( l );
      addElements( list, l+1, k );
      list.add( -l );
    }
  }
  
  /**
   * Iterates through a list of point-indices. Each number stands vor a vertex, thus a list with n entries creates
   * a path of n-1 vertices.
   * 
   * At each vertex (but the ones with index 0) there is an additional incoming or outgoing arc. Outgoing arcs are
   * indicated by negative indices.
   * 
   * @param list list of integers representing sources/sinks adjacent to the main path
   */
  public static void buildFromList( LinkedList<Integer> list) {
    int nodeCount = list.size() + (list.size()-2);
    int edgeCount = (list.size()-1) + (list.size()-2);
    
    
    pathLength = list.size();
    
    sourceCommodityMapping = new HashMap<>();
    sinkCommodityMapping = new HashMap<>();
    network = new DefaultDirectedGraph( nodeCount, edgeCount );
    capacities = new IdentifiableIntegerMapping<>( edgeCount );
    supplies = new IdentifiableIntegerMapping<> ( edgeCount );
    transitTimes = new IdentifiableIntegerMapping<>( edgeCount );
    
    Iterator<Integer> iter = list.iterator();
    int firstNode = iter.next();
    assert firstNode == 0;
    int currentPathNodeIndex = 0;
    int currentTerminalNodeIndex = list.size()-1;
    
    sourceCommodityMapping.put( network.getNode( currentPathNodeIndex ), 0 );
    supplies.set( network.getNode( currentPathNodeIndex ), 1 );
    System.out.println( "Adding node " + 0 );
    while( iter.hasNext() ) {
      currentPathNodeIndex++;
      currentTerminalNodeIndex++;
      
      int current = iter.next();
      System.out.println( "Adding node " + current );
      // Create new edge on the line
      Edge e = network.createAndSetEdge( network.getNode( currentPathNodeIndex-1), network.getNode( currentPathNodeIndex ) );
      transitTimes.set( e, 0 );
      capacities.set( e, 1 );
      supplies.set( network.getNode( currentPathNodeIndex ), 0 );

      if( current == 0 ) { // the last node
        // the edge to the sink is already created
        sinkCommodityMapping.put( network.getNode( currentPathNodeIndex ), 0 );
        supplies.set( network.getNode( currentPathNodeIndex ), -1 ); // set correct supply
      } else  if( current > 0 ) { // add a source
        e = network.createAndSetEdge( network.getNode( currentTerminalNodeIndex ), network.getNode( currentPathNodeIndex ) );
        transitTimes.set( e, 0 );
        capacities.set( e, 1 );
                
        sourceCommodityMapping.put( network.getNode( currentTerminalNodeIndex ), current );
        supplies.set( network.getNode( currentTerminalNodeIndex ), 1 );
      } else { // current < 0, add a sink
        e = network.createAndSetEdge( network.getNode( currentPathNodeIndex ), network.getNode( currentTerminalNodeIndex ) );
        transitTimes.set( e, -current );
        capacities.set( e, 1 );
                
        sinkCommodityMapping.put( network.getNode( currentTerminalNodeIndex ), -current );
        supplies.set( network.getNode( currentTerminalNodeIndex ), -1 );
      }
    }
  }
  
  public static void buildEdgeConstraint( Edge e ) {
    //System.out.println( "Edge " + e );
    outputLines = new String[T];
    Arrays.fill( outputLines, "" );
    // classify the edge
    if( e.start().id() < pathLength && e.end().id() < pathLength ) {
      //System.out.println( " - path edge" );
      buildEdgeConstraintPathEdge( e );
    } else if( e.end().id() < pathLength ) {
      //System.out.println( " - start edge" );
      buildEdgeConstraintStartEdge( e );
    } else if( e.start().id() < pathLength ) {
      //System.out.println( " - end edge" );
      buildEdgeConstraintEndEdge( e );
    } else {
      throw new AssertionError( "Illegal edge!" );
    }
    for( int i = 0; i < T; ++i ) {
      if( !outputLines[i].isEmpty() ) {
        outputLines[i] += " <= 1";        
      }
    }
    //System.out.println( Arrays.toString( outputLines ) );
  }
  
  private static void buildEdgeConstraintPathEdge( Edge e ) {
    int sourceIndex = e.start().id() + pathLength - 1;
    // paths from sources with index <= source index travel along the path
    
    // all links with sink index > source index are possible targets
    for( Node s : sourceCommodityMapping.keySet()) {
      if( s.id() <= sourceIndex ) {
        //System.out.println( "Source: " + s.id() );
        // Iterate over possible sinks
        
        // Handle the 0-path separately
        for( Node t : sinkCommodityMapping.keySet() ) {
          if( (t.id() > sourceIndex || t.id() == pathLength-1 )
              && sourceCommodityMapping.get( s ).equals( sinkCommodityMapping.get( t ) ) ) {
            //System.out.println( "Sink: " + t.id() );
            generatePaths( s, t, outputLines );
          }
        }
      }
    }
  }
  
  private static void buildEdgeConstraintStartEdge( Edge e ) {
    int sourceIndex = e.start().id();
    Node s = e.start();
    
    //System.out.println( "Source: " + s.id() );
    for( Node t : sinkCommodityMapping.keySet() ) {
      if( t.id() > sourceIndex && sourceCommodityMapping.get( s ).equals( sinkCommodityMapping.get( t ) ) ) {
        //System.out.println( "Sink: " + t.id() );
        generatePaths( s, t, outputLines );
      }
    }
  }
  
  private static void buildEdgeConstraintEndEdge( Edge e ) {
    int sinkIndex = e.end().id();
    Node t = e.end();
    
    for( Node s : sourceCommodityMapping.keySet() ) {
      if( s.id() < sinkIndex && sourceCommodityMapping.get( s ).equals( sinkCommodityMapping.get( t ) ) ) {
        //System.out.println( "Source: " + s.id() );
        //System.out.println( "Sink: " + t.id() );
        generatePaths( s, t, outputLines );
      }
    }
  }
  
  static void generatePaths( Node s, Node t, String[] outputLines ) {
    // Now we have found a s.id()-t.id()-path that uses arc e
    // check which of the path copies can be used for any of the time points 1, ..., T
    int pathTransitTime = sinkCommodityMapping.get( t );
    for( int theta = 1; theta <= T; ++theta ) {
      if( theta + pathTransitTime > T ) {
        break;
      }
      //System.out.println( "Use P_{" + s.id() + "," + t.id() + "} at time theta=" + theta );
      attach( outputLines, theta-1, pathName( s, t, theta ) );
    }
  }
  
  
  public static String pathName( Node source, Node sink, int time ) {
    String name = "P" + source.id() + "_" + sink.id() + "_" + time;
    usedPaths.add( name );
    return name;
  }
  
  /**
   * Parses a path name, so this must be changed alongside with {@link #pathName(de.tu_berlin.coga.graph.Node, de.tu_berlin.coga.graph.Node, int) }
   * @param s
   * @param theta
   * @return 
   */
  private static boolean arrivesAt( String s, int theta ) {
    String input = s.substring( 1 ); // the first letter is 'P'
    String[] split = input.split( "_" );
    int sourceIndex = Integer.parseInt( split[0] );
    int startTime = Integer.parseInt( split[2] );
    int transitTime = sourceCommodityMapping.get( network.getNode( sourceIndex ) );
    return startTime + transitTime <= theta;
  }

  private static int startNode( String s ) {
    String input = s.substring( 1 ); // the first letter is 'P'
    String[] split = input.split( "_" );
    return Integer.parseInt( split[0] );
  }

  private static int endNode( String s ) {
    String input = s.substring( 1 ); // the first letter is 'P'
    String[] split = input.split( "_" );
    return Integer.parseInt( split[1] );
  }
  
  private static void attach( String[] array, int index, String value ) {
    String s = array[index];
    if( s.isEmpty() ) {
      array[index] = value;
    } else {
      array[index] = s + " + " + value;
    }
  }
  
  private static int maxFlowForTime( int t ) {
    int capacity = 0;
    for( Node s : sourceCommodityMapping.keySet() ) {
      if( sourceCommodityMapping.get( s ) < t ) {
        capacity++;
      }
    }
    return capacity;
  }

  private static void printConstraintEdgeCapacities() {
    output.println( "\n\\edge capacities" );
    for( Edge e : network.allEdges() ) {
    // Edge e = network.getEdge( 8 );
      buildEdgeConstraint( e );
      for( int i = 0; i < T; ++i ) {
        if( !outputLines[i].isEmpty() ) {
          output.println( outputLines[i] );        
        }
      }
      output.flush();
    }
  }
  
  private static void printConstraintsSupplies() {
    // constraints respecting outflow constraints for the sources, e.g. capacity
    output.println( "\n\\source capacities" );
    outputLines = new String[network.nodeCount()];
    Arrays.fill( outputLines, "" );
    for( String s : usedPaths ) {
      int sourceIndex = startNode( s );
      attach( outputLines, sourceIndex, s );
    }
    for( int i = 0; i < network.nodeCount(); ++i ) {
      if( !outputLines[i].isEmpty() ) {
        output.println( outputLines[i] + " <= " + supplies.get( network.getNode( i ) ) );
      }
    }
    output.flush();
  }

  private static void printConstraintsSuppliesSingle() {
    output.println( "\n\\source capacities" );
    outputLines = new String[sourceCommodityMapping.values().size()];
    Arrays.fill( outputLines, "" );
    for( String s : usedPaths ) {
      int sourceIndex = startNode( s );
      int commodity = sourceCommodityMapping.get( network.getNode( sourceIndex ) );
      attach( outputLines, commodity, s );
    }
    for( int i = 0; i < outputLines.length; ++i ) {
      if( !outputLines[i].isEmpty() ) {
        int commodityCount = 0;
        for( Node s : sourceCommodityMapping.keySet() ) {
          if( sourceCommodityMapping.get( s ) == i ) {
            commodityCount += supplies.get( s );            
          }
        }
        output.println( outputLines[i] + " <= " + commodityCount );
      }
    }
    output.flush();
  }
  
  private static void printConstraintsDemands() {
    // constraints respecting outflow constraints for the sinks, e.g. capacity
    output.println( "\n\\sink capacities" );
    outputLines = new String[network.nodeCount()];
    Arrays.fill( outputLines, "" );
    for( String s : usedPaths ) {
      int sinkIndex = endNode( s );
      attach( outputLines, sinkIndex, s );
    }
    for( int i = 0; i < network.nodeCount(); ++i ) {
      if( !outputLines[i].isEmpty() ) {
        output.println( outputLines[i] + " <= " + -supplies.get( network.getNode( i ) ) );
      }
    }
    output.flush();
  }

  private static void printConstraintsDemandsSingle() {
    // constraints respecting outflow constraints for the sinks, e.g. capacity
    output.println( "\n\\sink capacities" );
    outputLines = new String[sinkCommodityMapping.values().size()];
    Arrays.fill( outputLines, "" );
    for( String s : usedPaths ) {
      int sinkIndex = endNode( s );
      int commodity = sinkCommodityMapping.get( network.getNode( sinkIndex ) );
      attach( outputLines, commodity, s );
    }
    for( int i = 0; i < outputLines.length; ++i ) {
      int commodityCount = 0;
      for( Node s : sinkCommodityMapping.keySet() ) {
        if( sinkCommodityMapping.get( s ) == i ) {
          commodityCount += -supplies.get( s );
        }
      }
      if( !outputLines[i].isEmpty() ) {
        output.println( outputLines[i] + " <= " + commodityCount );
      }
    }
    output.flush();
  }
  
  private static void printConstraintsNonNegativity() {
    output.println( "\n\\non-negativity" );
    for( String s : usedPaths ) {
      output.println( s + " >= 0" );
    }
    output.flush();
  }
  
  private static void printConstraintsBetas() {
    // constraints regarding betas, eg the approximation-constraints for each point in time
    output.println( "\n\\beta constraints" );
    for( int theta = 1; theta <= T; ++theta ) {
      // iterate over all paths used and check if they would arrive at theta
      String outputLine = "";
      for( String s : usedPaths ) {
        if( arrivesAt( s, theta ) ) {
          if( outputLine.isEmpty() ) {
            outputLine = s;
          } else {
            outputLine += " + " + s;
          }
        }
      }
      
      outputLine += " -" + maxFlowForTime( theta ) + "betas >= 0";
      output.println( outputLine );
    }
    output.flush();
  }
  
  private static void printObjectiveMaximization( int timeHorizon ) {
    String line = "";
    for( Node s : sourceCommodityMapping.keySet() ) {
      int sourceCommodity = sourceCommodityMapping.get( s );
      for( Node t : sinkCommodityMapping.keySet() ) {
        int sinkCommodity = sinkCommodityMapping.get( t );
        if( t.id() < s.id() || sourceCommodity != sinkCommodity ) { // skip sinks with lower id than the source
          continue;
        }
        System.out.println( "P" + s.id() + "_" + t.id() );
        int travelTime = sourceCommodity;
        System.out.println( "Travel time: " + travelTime );
        for( int theta = 1; theta <= timeHorizon-travelTime; ++theta ) {
          if( line.isEmpty() ) {
            line = pathName( s, t, theta );
          } else {
            line = line + " + " + pathName( s, t, theta );
          }
        }
      }
    }
    output.println( line );
  }
  
  private static void printBetaSingleSource() {
    output.println( "Maximize" );
    output.println( "path_based_multi_commodity_flow:" );

    output.println( "betas" );
    
    output.println( "\nSubject to" );
    
    printConstraintEdgeCapacities();
  
    printConstraintsSuppliesSingle();
        
    printConstraintsDemandsSingle();
    
    printConstraintsBetas();
    
    printConstraintsNonNegativity();
    output.println( "\nEnd");
  }
  
  private static void printBetaMultiSource() {
    output.println( "Maximize" );
    output.println( "path_based_multi_commodity_flow:" );

    output.println( "betas" );
    
    output.println( "\nSubject to" );
    
    printConstraintEdgeCapacities();
  
    printConstraintsSupplies();
        
    printConstraintsDemands();
    
    printConstraintsBetas();
    
    printConstraintsNonNegativity();
    output.println( "\nEnd");
  }
  
  public static void printMaxFlowSingleSource( int timeHorizon ) {
    output.println( "Maximize" );
    output.println( "path_based_multi_commodity_flow:" );

    printObjectiveMaximization( timeHorizon );
    
    output.println( "\nSubject to" );
    
    printConstraintEdgeCapacities();
  
    printConstraintsSuppliesSingle();

    printConstraintsDemandsSingle();
    
    //printConstraintsBetas();
    
    printConstraintsNonNegativity();
    output.println( "\nEnd");
  }
  
  public static void printMaxFlowMultiSource( int timeHorizon ) {
    output.println( "Maximize" );
    output.println( "path_based_multi_commodity_flow:" );

    printObjectiveMaximization( timeHorizon );
    
    output.println( "\nSubject to" );
    
    printConstraintEdgeCapacities();
  
    printConstraintsSupplies();

    printConstraintsDemands();
    
    //printConstraintsBetas();
    
    printConstraintsNonNegativity();
    output.println( "\nEnd");
  }
  
  public static void main( String[] args ) {

    int k = 4; // equals the time horizon!
    T = k;
    LinkedList<Integer> list = new LinkedList<>();
    list.add( 0 );
    addElements( list, 1, k);
    list.add( 0 );
    
    System.out.println( list );
    
    buildFromList( list );
    System.out.println( network );
    System.out.println( capacities );
    System.out.println( supplies );
    System.out.println( transitTimes );
    System.out.println( sourceCommodityMapping );
    System.out.println( sinkCommodityMapping );
    

    try {
      //output = new PrintWriter( new File( "D:\\Eigene Dateien\\Arbeit\\Dissertation\\data\\mclp", "mf-ss-c3-t3.lp" ) );
      output = new PrintWriter( new File( "D:\\Eigene Dateien\\Arbeit\\Dissertation\\data\\mclp", "ss-lp4.lp" ) );
    } catch( FileNotFoundException ex ) {
      System.err.println( "WTF!" );
      System.exit( 1 );
    }
    
    // The LP output for beta approximation
    //printBetaMultiSource();
    printBetaSingleSource();
   
    // The LP output for max flow over time
    //T = 3;
    //printMaxFlowMultiSource( T );
    //printMaxFlowSingleSource( T );
   
    output.close();
  }
}
