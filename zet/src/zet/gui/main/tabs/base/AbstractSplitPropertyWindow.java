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
 * Class AbstractSplitPropertyWindow
 * Created 29.04.2008, 21:03:51
 */

package zet.gui.main.tabs.base;

import de.tu_berlin.coga.common.localization.Localized;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @param <T>
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractSplitPropertyWindow<T extends JComponent> extends JPanel implements Localized {
	/** The class-type of the container. This is set only one single time in the constructor. */
	private T leftPanel;
	JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, false );

	public AbstractSplitPropertyWindow( T panel ) {
		leftPanel = panel;
		addComponents();
	}

	protected final void addComponents() {
		setLayout( new BorderLayout() );
		// Initialize the window as a whole by putting everything together
		//JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, false, getLeftPanel(), createEastBar() );#
		splitPane.setRightComponent( createEastBar() );
		splitPane.setLeftComponent( getLeftPanel() );
		splitPane.setResizeWeight( 1.0d );
		splitPane.setPreferredSize( new Dimension( 800, 600 ) );
		splitPane.setDividerLocation( 680 );
		splitPane.setOneTouchExpandable( false );
		add( splitPane, BorderLayout.CENTER );
	}

	abstract protected JPanel createEastBar();

	public T getLeftPanel() {
		return leftPanel;
	}

	abstract protected String getAdditionalTitleBarText();
}
