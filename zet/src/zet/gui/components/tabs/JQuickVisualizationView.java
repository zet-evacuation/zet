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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/**
 * Class JQuickVisualizationView
 * Created 30.04.2008, 09:51:45
 */

package zet.gui.components.tabs;

import converter.ZToCAConverter;
import zet.gui.components.tabs.base.JFloorScrollPane;
import zet.gui.components.tabs.base.AbstractSplitPropertyWindow;
import javax.swing.JButton;
import javax.swing.JPanel;

import statistic.ca.CAStatistic;
import zet.gui.components.tabs.quickVisualization.JRasterFloor;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JQuickVisualizationView extends AbstractSplitPropertyWindow<JFloorScrollPane<JRasterFloor>> {
	
	public JQuickVisualizationView() {
		super( new JFloorScrollPane<JRasterFloor>( new JRasterFloor() ) );
	}

	public void updateFloorView() {
		getLeftPanel().getMainComponent().displayFloor(getLeftPanel().getMainComponent().getFloor (), 
				ZToCAConverter.getInstance().getLatestMapping(), ZToCAConverter.getInstance().getLatestContainer());
	}
	
	/**
	 * Returns a panel displayed on the right side of the window.
	 * @return a panel displayed on the right side of the window
	 */
	@Override
	protected JPanel createEastBar() {
		JPanel panel = new JPanel();
		JButton button = new JButton( "Klick mich" );
		panel.add( button );
		return new JPanel();
	}

	/**
	 * Returns the text for the title bar.
	 * @return the text for the title bar
	 */
	@Override
	protected String getAdditionalTitleBarText() {
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
