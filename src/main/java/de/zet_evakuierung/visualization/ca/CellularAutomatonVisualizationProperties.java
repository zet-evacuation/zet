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
package de.zet_evakuierung.visualization.ca;

import java.awt.Color;

import gui.visualization.QualityPreset;
import org.zetool.opengl.drawingutils.GLColor;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface CellularAutomatonVisualizationProperties {

    public default double getScaling() {
        return 1;
    }

    public default double getFloorHeight() {
        return 10;
    }

    public default GLColor getStairColor() {
        return new GLColor(255, 255, 175);
    }

    public default boolean isShowTypeColor() {
        return true;
    }

    public default GLColor getFloorColor() {
        return new GLColor(192, 192, 192);
    }

    public default GLColor getDelayColor() {
        return new GLColor(255, 0, 0);
    }

    public default boolean isCellTypeVisible() {
        return true;
    }

    public default GLColor getEvacuationColor() {
        return new GLColor(10, 170, 80);
    }

    public default GLColor getSafeColor() {
        return new GLColor(255, 255, 0);

    }

    public default boolean isGridVisible() {
        return true;
    }

    public default GLColor getInvalidColor() {
        return new GLColor(130, 55, 101);
    }

    public default double getIndividualHeight() {
        return 12.0;
    }

    public default double getIndividualRadius() {
        return 2.0;
    }

    public default GLColor getIndividualColor() {
        return new GLColor(0, 103, 149);
    }

    public default QualityPreset getQualityPreset() {
        return QualityPreset.MediumQuality;
    }

    public default GLColor getGridColor() {
        return new GLColor(Color.LIGHT_GRAY);
    }

    public default boolean isSmooth() {
        return true;
    }

}
