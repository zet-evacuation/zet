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
package zet.gui.main.tabs.quickVisualization;

import ds.PropertyContainer;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.components.model.editor.floor.AbstractFloor;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JPotentialCell extends JCellPolygon {

    private int potential;
    private int maxPotential;
    private float colorValue = 0f;
    private boolean showPotentialValue;

    public JPotentialCell(EvacCell cell, AbstractFloor floor, int potential, int maxPotential, EvacuationCellularAutomaton ca) {
        super(cell, potential > 0 ? new Color(1 - (float) potential / maxPotential, 1 - (float) potential / maxPotential, 1.0f) : Color.white, ca);
        this.potential = potential;
        this.maxPotential = maxPotential;
        setToolTipText();
        showPotentialValue = PropertyContainer.getGlobal().getAsBoolean("editor.options.cavis.showIndividualNames");
    }

    /**
     *
     * @param cell
     * @param floor
     * @param lineColor
     * @param potential
     * @param maxPotential
     * @param ca
     */
    public JPotentialCell(EvacCell cell, AbstractFloor floor, Color lineColor, int potential, int maxPotential, EvacuationCellularAutomaton ca) {
        super(cell, potential > 0 ? new Color(1 - (float) potential / maxPotential, 1 - (float) potential / maxPotential, 1.0f) : Color.white, lineColor, ca);
        colorValue = 1 - (float) potential / maxPotential;
        this.potential = potential;
        this.maxPotential = maxPotential;
        setToolTipText();
        showPotentialValue = PropertyContainer.getGlobal().getAsBoolean("editor.options.cavis.showPotentialValue");
    }

    @Override
    protected void setToolTipText() {
        String s = "<html>";
        s += "Potenzial: " + Integer.toString(potential);
        s += "</html>";
        setToolTipText(s);
    }

    @Override
    public void paintCell(Graphics2D g2) {
        super.paintCell(g2);
        if (!showPotentialValue) {
            return;
        }
        if (colorValue <= 0.5) {
            drawName(Integer.toString(potential), g2, Color.white);
        } else {
            drawName(Integer.toString(potential), g2, Color.black);
        }
    }
}
