/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
package gui.statistic;

import io.visualization.CAVisualizationResults;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import batch.BatchResult;
import org.zet.cellularautomaton.statistic.MultipleCycleCAStatistic;
import statistic.ca.gui.JCAStatisticPanel;
import ds.GraphVisualizationResults;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import io.visualization.EvacuationSimulationResults;
import java.awt.Dimension;
import javax.swing.JTextArea;

/**
 *
 * @author Jan-Philipp Kappmeier
 * @author Timon Kelter
 */
public class JStatisticPanel extends JPanel {

  private JCAStatisticPanel jcasp;

  public JStatisticPanel() {
    super();
    jcasp = new JCAStatisticPanel();
    //jcasp.setPreferredSize( new Dimension( 1000, 1000 ) );
    setLayout( new BorderLayout() );
    //add( jcasp );
    add( jcasp, BorderLayout.CENTER );
  }

  public void setCellularAutomaton( EvacuationCellularAutomaton ca ) {
    jcasp.setCellularAutomaton( ca );
  }

  public void setMultipleCycleCAStatistic( MultipleCycleCAStatistic mccas ) {
    jcasp.setMultipleCycleCAStatistic( mccas );
  }

  public void setCA( EvacuationSimulationResults cavr ) {
    jcasp.setCA( cavr );
  }

  public void setGraph( GraphVisualizationResults gvr ) {
    jcasp.setGraph( gvr );
  }

  public void setResult( BatchResult result ) {
    jcasp.setResult( result );
    add( jcasp.getSplitPane(), BorderLayout.CENTER );
  }
}
