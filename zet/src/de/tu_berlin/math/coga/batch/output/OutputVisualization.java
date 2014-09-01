
package de.tu_berlin.math.coga.batch.output;

import ds.GraphVisualizationResults;
import gui.GUIControl;
import io.visualization.CAVisualizationResults;
import javax.swing.Icon;
import javax.swing.ImageIcon;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class OutputVisualization extends AbstractOutput implements TreeListItem {
  private final static Icon visIcon = new ImageIcon( "./icons/playback-24.png" );
  private GUIControl control;

  public OutputVisualization( GUIControl control ) {
    this.control = control;
  }

  @Override
  public boolean consumes( Class<?> c ) {
    return( c.equals( GraphVisualizationResults.class ) || c.equals( CAVisualizationResults.class ) );
  }

  @Override
  public void consume( Object o ) {
    if( o instanceof GraphVisualizationResults ) {
      showGraphResults( (GraphVisualizationResults) o );
    } else if( o instanceof CAVisualizationResults ) {
      showCellularAutomatonResults( (CAVisualizationResults) o );
    } else {
      throw new IllegalArgumentException( "Type " + o.getClass() + " is not supported! Only "
              + GraphVisualizationResults.class + " and " + CAVisualizationResults.class + " are possible." );
    }
  }

  /**
   * Sends results from a graph visualization to a zet visualization panel.
   * @param gvr
   */
  private void showGraphResults( GraphVisualizationResults gvr ) {
    //GraphVisualizationResults gvr = algorithmControl.getGraphVisResults();
  	control.addVisualization( gvr );
    //visualization.getControl().setGraphControl( gvr );
    //visualization = visualizationView.getGLContainer();
  }

  /**
   * Sends results from a simulation run to a zet visualization panel.
   * @param cav
   */
  private void showCellularAutomatonResults( CAVisualizationResults cav ) {
    System.err.println( "Call to unsupported operation visualization of simulation results!" );
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
