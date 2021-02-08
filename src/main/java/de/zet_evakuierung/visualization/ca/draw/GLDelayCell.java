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
package de.zet_evakuierung.visualization.ca.draw;

import java.util.function.Function;

import de.zet_evakuierung.visualization.ca.model.GLCellModel;
import gui.visualization.VisualizationOptionManager;
import org.zetool.common.util.Direction8;
import org.zetool.opengl.drawingutils.GLColor;

public class GLDelayCell extends GLCell {

    public GLDelayCell(GLCellModel model, Function<Direction8, GLColor> neighborColor) {
        super(model, VisualizationOptionManager.getDelayCellFloorColor(), neighborColor);
    }

    @Override
    protected void updateFloorColor() {
        if (VisualizationOptionManager.getAlwaysDisplayCellType()) {
            color = getDefaultColor();
        } else {
            super.updateFloorColor();
        }
    }
}