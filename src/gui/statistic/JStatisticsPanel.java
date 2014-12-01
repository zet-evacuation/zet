
package gui.statistic;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;


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
