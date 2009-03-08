/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/**
 * Class JStatisticPanel
 * Erstellt 30.04.2008, 20:18:19
 */

package gui.statistic;

import io.visualization.CAVisualizationResults;

import java.awt.BorderLayout;
import java.awt.event.*;

import javax.swing.JPanel;
import batch.BatchResult;
import statistic.ca.MultipleCycleCAStatistic;
import statistic.ca.gui.JCAStatisticPanel;
import ds.graph.GraphVisualizationResult;
import ds.ca.CellularAutomaton;

/**
 *
 * @author Kapman, Timon Kelter
 */
public class JStatisticPanel extends JPanel {
	
	private JCAStatisticPanel jcasp;
	
	public JStatisticPanel(){
		super();
		jcasp = new JCAStatisticPanel();
		setLayout(new BorderLayout());
	}
	
	public void setCellularAutomaton(CellularAutomaton ca){
		jcasp.setCellularAutomaton(ca);
	}
	
	public void setMultipleCycleCAStatistic(MultipleCycleCAStatistic mccas){
		jcasp.setMultipleCycleCAStatistic(mccas);
	}
	
	public void setCA(CAVisualizationResults cavr){
		jcasp.setCA(cavr);
	}
	
	public void setGraph(GraphVisualizationResult gvr){
		jcasp.setGraph(gvr);
	}
	
	public void setResult(BatchResult result){
		jcasp.setResult(result);
		add(jcasp.getSplitPane(), BorderLayout.CENTER);
	}
}
