
package de.tu_berlin.math.coga.batch.output;

import ds.GraphVisualizationResults;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.results.Action;
import ds.ca.results.EvacuationRecording;
import ds.ca.results.ExitAction;
import ds.ca.results.MoveAction;
import io.visualization.EvacuationSimulationResults;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * An output option that gives out arrival patterns at the command line.
 * @author Jan-Philipp Kappmeier
 */
public class OutputText extends AbstractOutput implements TreeListItem {
  private final static Icon visIcon = new ImageIcon( "./icons/document-24.png" );

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

  /**
   * Sends results from a graph visualization to a zet visualization panel.
   * @param gvr
   */
  private void graphResults( GraphVisualizationResults gvr ) {

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

    System.out.println( "Time,Safe" );
    while( recording.hasNext() ) {
      Vector<Action> actions = recording.nextActions();
      Vector<ExitAction> exits = recording.filterActions( ExitAction.class );
      safe += exits.size();
      System.out.println( (++time) + "," + safe );
    }

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
