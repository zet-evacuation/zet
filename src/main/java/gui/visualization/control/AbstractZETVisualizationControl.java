/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package gui.visualization.control;

import org.zetool.opengl.framework.abs.AbstractControl;
import org.zetool.opengl.framework.abs.AbstractDrawable;
import org.zetool.opengl.framework.abs.Controlable;

/**
 *
 * @param <U>
 * @param <V> 
 * @param <W>
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractZETVisualizationControl<U extends AbstractControl<?, ?>, V extends AbstractDrawable<?, ?>, W extends Controlable> extends AbstractControl<U,V> {
	protected W mainControl;

	public AbstractZETVisualizationControl( V controlled, W mainControl ) {
		super( controlled );
		this.mainControl = mainControl;
	}

	public AbstractZETVisualizationControl( W mainControl ) {
		this.mainControl = mainControl;
	}

	/**
	 * Does not set main control. Need to set it manually!
	 */
	public AbstractZETVisualizationControl() {

	}

}
