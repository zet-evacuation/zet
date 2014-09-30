
package de.tu_berlin.math.coga.batch.output;

import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.coga.netflow.ds.flow.EdgeBasedFlowOverTime;
import de.tu_berlin.coga.netflow.dynamic.eatapprox.EarliestArrivalFlowPattern;
import de.tu_berlin.coga.netflow.dynamic.eatapprox.EarliestArrivalFlowPatternBuilder;
import de.tu_berlin.math.coga.batch.gui.action.RunComputationAction;
import ds.GraphVisualizationResults;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.results.EvacuationRecording;
import ds.ca.results.ExitAction;
import io.visualization.EvacuationSimulationResults;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * An output option that gives out arrival patterns at the command line.
 * @author Jan-Philipp Kappmeier
 */
public class OutputText extends AbstractOutput implements TreeListItem {
  private final static Icon visIcon = new ImageIcon( "./icons/document-24.png" );

  private PrintWriter output = new PrintWriter( System.out );
  //private String basePath = "/homes/combi/kappmeie/Dateien/Programme/zet/output/diss/icem/mcf/";
  private String basePath = "../../output/diss/telefunken/single-speed-fast";

  private boolean combinedOut = true;

  private ArrayList<ArrayList<Integer>> arrivals = new ArrayList<>();


  public OutputText() {
  }

  @Override
  public boolean consumes( Class<?> c ) {
    return (c.equals( GraphVisualizationResults.class ) || c.equals( EvacuationSimulationResults.class ));
  }

  @Override
  public void consume( Object o ) {
    if( o instanceof GraphVisualizationResults ) {
      graphResults( (GraphVisualizationResults)o );
    } else if( o instanceof EvacuationSimulationResults ) {
      cellularAutomaton( (EvacuationSimulationResults)o );
    } else {
      throw new IllegalArgumentException( "Type " + o.getClass() + " is not supported! Only "
              + GraphVisualizationResults.class + " and " + EvacuationSimulationResults.class + " are possible." );
    }
  }

  private int maxValue = 0;

  @Override
  public void consume( Object o, int run ) {
    if( combinedOut ) {
      ArrayList<Integer> currentRun = new ArrayList<>();
      arrivals.add( currentRun );

      EvacuationSimulationResults cav = (EvacuationSimulationResults)o;

      EvacuationRecording recording = cav.getRecording();
      EvacuationCellularAutomaton ca = new EvacuationCellularAutomaton( recording.getInitialConfig() );

      int safe = 0;
      int time = 0;

      //output.write( "Time,Safe\n" );
      System.out.println( "Recording size: " + recording.length() );
      while( recording.hasNext() ) {
        recording.nextActions();
        Vector<ExitAction> exits = recording.filterActions( ExitAction.class );
        safe += exits.size();
        //output.write( (++time) + "," + safe + "\n" );
        currentRun.add( safe );
        maxValue = Math.max( maxValue, safe );
      }

      int runs = RunComputationAction.runs-1;
      if( run == runs ) {
        try {
          output = new PrintWriter( new File( basePath, "combined.csv" ) );
        } catch( FileNotFoundException ex ) {
          System.err.println( "WTF!" );
        }

        // write csv-file
        String s = "Zeit;";
        for( int i = 1; i <= runs; ++i ) {
          s += i + ";";
        }
        output.write( s + "\n" );
        output.write( "\n" );
        int maxTime = 0;
        for( ArrayList<Integer> list : arrivals ) {
          maxTime = Math.max( maxTime, list.size() );
        }
        for( int i = 0; i < maxTime; ++i ) {
          s = "" + (i + 1) + ";";
          for( int j = 0; j <= runs; ++j ) {
            ArrayList<Integer> curve = arrivals.get( j );
            int value = i < curve.size() ? curve.get( i ) : maxValue;
            s += value + ";";
          }
          output.write( s + "\n" );
        }
        output.flush();
        output.close();
        output = new PrintWriter( System.out );
      }


    } else {
      try {
        output = new PrintWriter( new File( basePath, "eat-" + run + ".csv" ) );
      } catch( FileNotFoundException ex ) {
        System.err.println( "WTF!" );
      }
      consume( o );
      output.close();
      output = new PrintWriter( System.out );
    }
  }


  /**
   * Sends results from a graph visualization to a zet visualization panel.
   * @param gvr
   */
  private void graphResults( GraphVisualizationResults gvr ) {
    EdgeBasedFlowOverTime ef = gvr.getFlow();
        
    System.out.println( "Building arrival pattern: " );
    int timeHorizon = -1;
    for( Edge e : gvr.getNetwork().incidentEdges( gvr.getSupersink() ) ) {
      timeHorizon = Math.max( timeHorizon, ef.get( e ).getLastTimeWithNonZeroValue() );
    }
    EarliestArrivalFlowPattern pattern = EarliestArrivalFlowPatternBuilder.fromEdgeBased( ef,
            gvr.getNetwork().incomingEdges( gvr.getSupersink() ), timeHorizon );

    System.out.println( "Pattern: " );
    System.out.println( pattern );
  }

  /**
   * Sends results from a simulation run to a zet visualization panel.
   * @param cav
   */
  private void cellularAutomaton( EvacuationSimulationResults cav ) {
    EvacuationRecording recording = cav.getRecording();
    EvacuationCellularAutomaton ca = new EvacuationCellularAutomaton( recording.getInitialConfig() );

    int safe = 0;
    int time = 0;

    output.write( "Time,Safe\n" );
    while( recording.hasNext() ) {
      recording.nextActions();
      Vector<ExitAction> exits = recording.filterActions( ExitAction.class );
      safe += exits.size();
      output.write( (++time) + "," + safe + "\n" );
    }
    output.flush();
  }

  @Override
  public String getDescription() {
    return "Sends results of project simulation/optimization to zet visualization.";
  }

  @Override
  public String getTitle() {
    return "Visualize results.";
  }

  @Override
  public Icon getIcon() {
    return visIcon;
  }
}
