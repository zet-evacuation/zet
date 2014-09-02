/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * Class AbstractVisualizationPanel
 * Erstellt 08.05.2008, 23:38:40
 */

package gui.visualization;

import java.awt.BorderLayout;
import javax.media.opengl.GLCanvas;
import javax.swing.JPanel;

/**
 *
 * @param <T> 
 * @author Jan-Philipp Kappmeier
 */
public class VisualizationPanel<T extends GLCanvas> extends JPanel {
	T glContainer;
	
	public VisualizationPanel( T glContainer ) {
		this.glContainer = glContainer;
		setLayout( new BorderLayout() );
		this.add( glContainer, BorderLayout.CENTER );
	}
	
	public T getGLContainer() {
		return glContainer;
	}
	
	public void setGLContainer( T glContainer ) {
		this.glContainer = glContainer;
	}
}
