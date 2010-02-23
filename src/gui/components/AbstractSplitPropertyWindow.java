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
 * Erstellt 29.04.2008, 21:03:51
 */

package gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.NumberFormat;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.common.localization.Localized;

/**
 *
 * @param <T> 
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractSplitPropertyWindow<T extends JComponent> extends JPanel implements Localized {
	/** The class-type of the container. This is set only one single time in the constructor. */
	//private final Class<T> leftPanelType;
	private T leftPanel;
	/** The localization class. */
	protected static final Localization loc = Localization.getInstance();
	protected static final NumberFormat nfFloat = loc.getFloatConverter(); //NumberFormat.getNumberInstance( Localization.getInstance().getLocale() );
	protected static final NumberFormat nfInteger = loc.getIntegerConverter(); //NumberFormat.getIntegerInstance( Localization.getInstance().getLocale() );

	public AbstractSplitPropertyWindow( T panel ) {
		//this.leftPanelType = leftPanelType;
		leftPanel = panel;
		addComponents();
	}
	
	protected void addComponents() {
		setLayout( new BorderLayout() );
		// Initialize the window as a whole by putting everything together
		JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
						false, getLeftPanel(), createEastBar() );
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
	
	protected void setLeftPanel( T panel ) {
		leftPanel = panel;
	}
	
	abstract protected String getTitleBarText();
}
