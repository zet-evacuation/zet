/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
import org.zetool.opengl.framework.abs.Drawable;
import org.zetool.opengl.framework.abs.VisualizationModelProvider;

/**
 *
 * @param <U> the child elements
 * @param <V> the drawable type belonging to this control class
 * @param <W> an external type providing general information about the hierarchy
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractZETVisualizationControl<U, V extends Drawable, W extends VisualizationModelProvider> extends AbstractControl<U, V> {

    protected final W visualizationModel;

    public AbstractZETVisualizationControl(V controlled, W visualizationModel) {
        super(controlled);
        this.visualizationModel = visualizationModel;
    }

    public AbstractZETVisualizationControl(W visualizationModel) {
        this.visualizationModel = visualizationModel;
    }

}
