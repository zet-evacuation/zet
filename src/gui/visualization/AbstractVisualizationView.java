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
 * Class JVisualizationView
 * Created 08.06.2008, 00:12:49
 */

package gui.visualization;

import org.zet.components.model.editor.AbstractSplitPropertyWindow;
import javax.media.opengl.GLCanvas;

/**
 *
 * @param <T> 
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractVisualizationView<T extends GLCanvas> extends AbstractSplitPropertyWindow<VisualizationPanel<T>> {
	private T canvas;
	
	/**
	 * 
	 * @param vis
	 */
	public AbstractVisualizationView( VisualizationPanel<T> vis ) {
		super( vis );
		canvas = vis.getGLContainer();
	}
	
	/**
	 * 
	 * @return the text for the title bar
	 */
	@Override
	protected String getAdditionalTitleBarText() {
		return "";
	}
	
	/**
	 * 
	 * @return the {@code GLContainer}
	 */
	public final T getGLContainer() {
		return canvas;
	}
}
