/**
 * JStatisticsPanel.java
 * Created: May 12, 2010,1:18:11 PM
 */
package gui.statistic;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import umontreal.iro.lecuyer.randvar.GeometricGen;


/**
 * A panel showing different types of statistic.
 * @author Jan-Philipp Kappmeier
 */
public class JStatisticsPanel extends JPanel {
	
	public JStatisticsPanel() {
		super();
		setLayout(new BorderLayout() );
		add( new JTextField( "test" ), BorderLayout.CENTER );
	}

	public void addData() {
		
	}

}
