
package de.tu_berlin.math.coga.batch.output;

import org.zetool.components.batch.output.TreeListItem;
import org.zetool.components.batch.output.AbstractOutput;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import de.zet_evakuierung.model.BuildingPlan;
import org.zetool.math.vectormath.Vector3;
import ds.GraphVisualizationResults;
import io.visualization.BuildingResults;
import io.visualization.BuildingResults.Wall;
import io.visualization.EvacuationSimulationResults;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TikZOut extends AbstractOutput implements TreeListItem {
  private final static Icon tikzIcon = new ImageIcon( "./icons/document-24.png" );
  Map<Integer,StringBuilder> sbs = new HashMap<>();
  private String basePath = "D:\\Eigene Dateien\\Arbeit\\Dissertation\\pictures\\";
  private PrintWriter output = new PrintWriter( System.out );

  int count = 0;

  @Override
  public boolean consumes( Class<?> c ) {
    return( c.equals( GraphVisualizationResults.class ) || c.equals( BuildingPlan.class ) );
  }
  
  final StringBuilder waste = new StringBuilder();
  private StringBuilder getSb ( int floor ) {
    if( floor < 3 ) {
      return waste;
    }
    
    int actualFloor = (floor-3)/2;
    
    StringBuilder sb = sbs.get( actualFloor );
    if( sb == null ) {
      sb = new StringBuilder();

      //sb.append( "  \\subfloat[\\label{fig:tikz-generator-" + actualFloor + "}Floor " + actualFloor + ".]{\n" );
      
      //sb.append( "\\begin{tikzpicture}[scale=0.5741]\n" );
      sb.append( "\\begin{tikzpicture}[scale=0.0541]\n" );      
      sbs.put( actualFloor, sb );
    }
    return sb;
  }
  
  private void closeSbs() {    
    for( Integer floor : sbs.keySet() ) {
      StringBuilder sb = sbs.get( floor );
      sb.append( "  \\end{tikzpicture}\n" );
      //sb.append( "  }\n" );
    }
  }
  

  @Override
  public void consume( Object o ) {
    if( o instanceof GraphVisualizationResults ) {
      showGraphResults( (GraphVisualizationResults) o );
      count++;
    } else if( o instanceof BuildingPlan ) {
      showBuildingPlan( (BuildingPlan)o );
      count++;
    } else {
      throw new IllegalArgumentException( "Type " + o.getClass() + " is not supported! Only "
              + GraphVisualizationResults.class + " and " + EvacuationSimulationResults.class + " are possible." );
    }
    if( count == 2 ) {
      closeSbs();
      
      for( Integer floor : sbs.keySet() ) {
        try {
          output = new PrintWriter( new File( basePath, "practice-test-evacuation-generated-" + floor + ".tikz" ) );
          StringBuilder sb = sbs.get( floor );
          output.write( sb.toString() );
          output.flush();
          output.close();
        } catch( FileNotFoundException ex ) {
          System.err.println( "File out failed!" );
        }        
      }
      

      System.out.println( "\\begin{figure}" );
      System.out.println( "  \\centering" );
      for( Integer floor : sbs.keySet() ) {
        System.out.println( "  \\subfloat[\\label{fig:practice_test-evacuation-generated-" + floor + "}Floor " + (floor+1) + ".]{" );
        System.out.println( "  \\tikzsetnextfilename{practice-test-evacuation-generated-" + floor + "}" );
        System.out.println( "  \\input{pictures/practice-test-evacuation-generated-" + floor + ".tikz}" );
        System.out.println( "  }" );
      }
      //for( StringBuilder sb : sbs.values() ) {
      //  System.out.println( sb.toString() );        
      //}
      System.out.println( "	\\caption{ZET automatic generated floor plan.}%");
      System.out.println( "	\\label{fig:tikz-generator}%" );
      System.out.println( "\\end{figure}" );
    }
  }

  @Override
  public String getDescription() {
    return "Generates a tikz picture from a building.";
  }

  @Override
  public String getTitle() {
    return "TikZ out.";
  }

  @Override
  public Icon getIcon() {
    return tikzIcon;
  }

  private void showGraphResults( GraphVisualizationResults gvr ) {
    DirectedGraph graph = gvr.getNetwork();
    Set<Node> sources = new HashSet<>();
    Set<Node> fake = new HashSet<>();
    for( Edge e : graph.edges() ) {
      if( gvr.isSourceNode( e.start() ) ) {
        System.out.println( "source: " + e.end() );
        sources.add( e.end() );
        fake.add( e.start() );
      } else if( gvr.isSourceNode( e.end() ) ) {
        System.out.println( "source: " + e.start() );
        sources.add( e.start() );
        fake.add( e.end() );
      }
    }
    
    for( Node n : graph ) {
      if( !fake.contains( n ) && !(n.id() == 0) ) {
 				int nodeFloor = gvr.getNodeToFloorMapping().get( n );
        Vector3 position = gvr.getNodePositionMapping().get( n );
        StringBuilder sb = this.getSb( nodeFloor );
        if( sources.contains( n ) ) {
          // source
          sb.append( "  \\node[emptyNodeBuilding,sourceBuilding,anchor=center] (" ).append( n.id() ).append( ") at (" ).append( position.x/400 ).append("," ).append( position.y/400).append(") {};\n");
        } else if( gvr.isEvacuationNode( n ) ) {
          // sink
          sb.append( "  \\node[emptyNodeBuilding,sinkBuilding,anchor=center] (" ).append( n.id() ).append( ") at (" ).append( position.x/400 ).append("," ).append( position.y/400).append(") {};\n");
        } else {
          // normal node
          sb.append( "  \\node[emptyNodeBuilding,intermediateBuilding,anchor=center] (" ).append( n.id() ).append( ") at (" ).append( position.x/400 ).append("," ).append( position.y/400).append(") {};\n");
        }
      }
    }
    
    for( Edge e : graph.edges() ) {
      if( !(fake.contains( e.start() ) || fake.contains( e.end() ) || e.start().id() == 0 || e.end().id() == 0 || e.end().id() < e.start().id()) ) {
        // \draw[thick] (2) to node[near,right,pos=0.8]{\tiny{}6} (10);
        int nodeFloor1 = gvr.getNodeToFloorMapping().get( e.start() );
        int nodeFloor2 = gvr.getNodeToFloorMapping().get( e.end() );
        if( nodeFloor1 == nodeFloor2 ) {
          StringBuilder sb = getSb( nodeFloor1 );//sbs.get( nodeFloor1 );
          if( sb == null ) {
            throw new IllegalStateException( "SB null! for floor" + nodeFloor1 );
          }
          sb.append( "  \\draw[] (" ).append( e.start().id() ).append( ") to (" ).append( e.end().id() ).append(");\n");          
        }
      }
    }
    
  }

  private void showBuildingPlan( BuildingPlan buildingPlan ) {
    BuildingResults br = new BuildingResults( buildingPlan );
    for( Wall w : br.getWalls() ) {
      Point2D last = null;
      int segmentNumber = 0;
      for( Point2D p : w ) {
        if( last == null ) {
          last = p;
        } else {
          // draw a line from last to p
          StringBuilder sb = getSb( w.getFloor().id() );
          if( w.getWallType( segmentNumber ) == Wall.ElementType.PASSABLE ) {
            //sb.append( "  \\draw[dashed] (" );
          } else {            
            sb.append( "  \\draw[] (" );
            sb.append( last.getX()/400 ).append( "," )
                    .append( -last.getY()/400 ).append( ") to (" ).append( p.getX()/400 )
                    .append( "," ).append( -p.getY()/400 ).append(");\n");
          }
          
          last = p;
          segmentNumber++;
        }
      }
      
    }
  }
}
