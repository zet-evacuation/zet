/**
 * Class JCAView
 * Erstellt 30.04.2008, 09:51:45
 */

package gui.ca;

import converter.ZToCAConverter;
import gui.components.JFloorScrollPane;
import gui.components.AbstractSplitPropertyWindow;
import javax.swing.JButton;
import javax.swing.JPanel;

import statistic.ca.CAStatistic;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JCAView extends AbstractSplitPropertyWindow<JFloorScrollPane<JRasterFloor>> {
	
	public JCAView() {
		super( new JFloorScrollPane<JRasterFloor>( new JRasterFloor() ) );
	}

	public void updateFloorView() {
		getLeftPanel().getMainComponent().displayFloor(getLeftPanel().getMainComponent().getFloor (), 
				ZToCAConverter.getInstance().getLatestMapping(), ZToCAConverter.getInstance().getLatestContainer());
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	protected JPanel getEastBar() {
		JPanel panel = new JPanel();
		JButton button = new JButton( "Klick mich" );
		panel.add( button );
		return new JPanel();
	}

	/**
	 * 
	 * @return
	 */
	@Override
	protected String getTitleBarText() {
		return "";
	}

	/**
	 * 
	 */
	public void localize() {
		
	}
	
	public void setCAStatistic(CAStatistic cas){
		getLeftPanel().getMainComponent().setCAStatistic(cas);
	}
}
