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
package de.tu_berlin.math.coga.batch.output;

import org.zetool.components.batch.output.TreeListItem;
import org.zetool.components.batch.output.AbstractOutput;
import de.zet_evakuierung.model.BuildingPlan;
import ds.GraphVisualizationResults;
import gui.GUIControl;
import io.visualization.BuildingResults;
import io.visualization.EvacuationSimulationResults;
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
    return( c.equals( GraphVisualizationResults.class ) || c.equals( EvacuationSimulationResults.class ) || c.equals( BuildingPlan.class ) );
  }

  @Override
  public void consume( Object o ) {
    if( o instanceof GraphVisualizationResults ) {
      showGraphResults( (GraphVisualizationResults) o );
    } else if( o instanceof EvacuationSimulationResults ) {
      showCellularAutomatonResults( (EvacuationSimulationResults) o );
    } else if( o instanceof BuildingPlan ) {
      showBuildingPlan( (BuildingPlan)o );
    } else {
      System.err.println( "Unknown class type: " + o );
      throw new IllegalArgumentException( "Type " + o.getClass() + " is not supported! Only "
              + GraphVisualizationResults.class + " and " + EvacuationSimulationResults.class + " are possible." );
    }
  }

  private void showBuildingPlan( BuildingPlan buildingPlan ) {
    BuildingResults br = new BuildingResults( buildingPlan );
    control.addVisualization( br );
    
  }

  /**
   * Sends results from a graph visualization to a zet visualization panel.
   * @param gvr
   */
  private void showGraphResults( GraphVisualizationResults gvr ) {
  	control.addVisualization( gvr );
    
  }

  /**
   * Sends results from a simulation run to a zet visualization panel.
   * @param cav
   */
  private void showCellularAutomatonResults( EvacuationSimulationResults cav ) {
    control.addVisualization( cav );
    
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
