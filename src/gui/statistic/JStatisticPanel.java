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
