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
package zet.gui.main.tabs.quickVisualization;

import org.zetool.common.localization.LocalizationManager;
import org.zetool.common.util.Direction8;
import ds.PropertyContainer;
import ds.ca.evac.EvacCell;
import ds.ca.evac.EvacuationCellularAutomaton;
import de.zet_evakuierung.model.PlanPolygon;
import org.zet.components.model.editor.CoordinateTools;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.EnumSet;
import org.zet.components.model.editor.polygon.AbstractPolygon;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings("serial")
public class JCellPolygon extends AbstractPolygon {

    private Color fillColor;
    EnumSet<Direction8> borders = EnumSet.noneOf(Direction8.class);
    private EvacCell cell;
    private final boolean showIndividualNames;
    private static final NumberFormat nfFloat = LocalizationManager.getManager().getFloatConverter();// NumberFormat.getNumberInstance( DefaultLoc.getSingleton().getLocale() );
    private EvacuationCellularAutomaton ca;

    /**
     * @param cell
     * @param fillColor the border color
     * @param ca
     */
    public JCellPolygon(EvacCell cell, Color fillColor, EvacuationCellularAutomaton ca) {
        this(cell, fillColor, Color.black, ca);
    }

    /**
     * @param cell
     * @param fillColor the border color
     * @param lineColor
     * @param ca
     */
    public JCellPolygon(EvacCell cell, Color fillColor, Color lineColor, EvacuationCellularAutomaton ca) {
        super(lineColor);
        this.ca = ca;
        this.cell = cell;
        this.fillColor = fillColor;
        setOpaque(false);
        showIndividualNames = PropertyContainer.getInstance().getAsBoolean("editor.options.cavis.showIndividualNames");
        setUpToolTipText();
    }

    protected void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * Only call this after the component has been added to a container. Otherwise operations like setBounds, which are
     * called herein, will fail.
     *
     * @param p the polygon
     */
    @Override
    public void displayPolygon(PlanPolygon p) {
        myPolygon = p;

        if (p != null) {
            // Contains absolute bounds
            Rectangle areabounds = CoordinateTools.translateToScreen(p.bounds());
            setBounds(areabounds);

            // This already copies the polygon
            drawingPolygon = CoordinateTools.translateToScreen(getAWTPolygon());
            drawingPolygon.translate(-areabounds.x, -areabounds.y);
        }
        if (drawingPolygon == null) {
            throw new IllegalStateException("Drawing polygon is null");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (cell.getIndividual() == null) {
            paintCell(g2);
        } else {
            paintIndividual(g2);
        }
    }

    public void paintCell(Graphics2D g2) {
        fill(g2, getForeground(), getBackground(), fillColor);
    }

    public void paintIndividual(Graphics2D g2) {
        if (!showIndividualNames) {
            return;
        }
        fill(g2, getForeground(), getBackground(), Color.green);
        drawName(Integer.toString(cell.getIndividual().getNumber()), g2, Color.black);
    }

    private void fill(Graphics2D g2, Color foreground, Color background, Color fill) {
        g2.setPaint(fill);
        if (drawingPolygon.npoints > 0) {
            g2.fillPolygon(drawingPolygon);
        }

        g2.setPaint(foreground);
        g2.drawPolygon(drawingPolygon);

        g2.setPaint(background);
        Rectangle bounds = drawingPolygon.getBounds();
        BasicStroke thick = new BasicStroke(5);
        g2.setStroke(thick);
        if (borders.contains(Direction8.Top)) {
            g2.drawLine(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y);
        }
        if (borders.contains(Direction8.Down)) {
            g2.drawLine(bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y + bounds.height);
        }
        if (borders.contains(Direction8.Left)) {
            g2.drawLine(bounds.x, bounds.y, bounds.x, bounds.y + bounds.height);
        }
        if (borders.contains(Direction8.Right)) {
            g2.drawLine(bounds.x + bounds.width, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height);
        }
    }

    public void addWall(Direction8 direction) {
        borders.add(direction);
    }

    /**
     * This function does nothing. Overwrite this function in derived components to automatically set an appropriate
     * tool tip.
     */
    protected void setToolTipText() {
    }

    void update() {
        setUpToolTipText();
    }

    private void setUpToolTipText() {
        if (cell.getIndividual() != null) {
            String s = "<html>";
            s += "Alter: " + Integer.toString(cell.getIndividual().getAge()) + "<br>";
            nfFloat.setMaximumFractionDigits(2);
            s += "Wunschgeschwindigkeit: " + nfFloat.format(ca.absoluteSpeed(cell.getIndividual().getMaxSpeed())) + "m/s";
            s += "</html>";
            setToolTipText(s);
        } else {
            setToolTipText();
        }
    }

    /** Prohibits serialization. */
    private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException {
        throw new UnsupportedOperationException("Serialization not supported");
    }
    
    /** Prohibits serialization. */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("Serialization not supported");
    }
}
