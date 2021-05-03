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
package de.zet_evakuierung.visualization.network;

import java.awt.Color;

import gui.visualization.QualityPreset;
import org.zetool.opengl.drawingutils.GLColor;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface GraphVisualizationProperties {

    public default double getScaling() {
        return 1;
    }

    public default double getFloorHeight() {
        return 10;
    }

    public default GLColor getNodeColor() {
        return new GLColor(250, 250, 100);
    }

    public default GLColor getEvacuationColor() {
        return new GLColor(10, 170, 80);
    }

    public default GLColor getSourceColor() {
        return new GLColor(0, 6, 255);
    }

    public default GLColor getDeletedSourceColor() {
        return new GLColor(140, 0, 200);
    }

    public default GLColor getNodeBorderColor() {
        return new GLColor(Color.white);
    }

    public default double getNodeRadius() {
        return 1.3;
    }

    public default QualityPreset getQualityPreset() {
        return QualityPreset.MediumQuality;
    }

    public default GLColor getFlowColor() {
        return new GLColor(0, 103, 149);
    }

    /**
     * Returns the color used for the front- and backside of the flow unit.
     *
     * @return the color used for the front- and backside of the flow unit
     */
    public default GLColor getFlowFaceColor() {
        GLColor flowColor = getFlowColor();
        float r, g, b, a;
        r = (float) flowColor.getRed() * (float) 0.8;
        g = (float) flowColor.getGreen() * (float) 0.8;
        b = (float) flowColor.getBlue() * (float) 0.8;
        a = (float) flowColor.getAlpha() * (float) 0.8;
        GLColor result = new GLColor(r, g, b, a);
        return result;
    }

    public default GLColor getEdgeColor() {
        return new GLColor(Color.BLACK);
    }

}
