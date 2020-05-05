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
package statistic.graph.gui;

import java.awt.Color;

/**
 *
 * @author Martin Groß
 */
public class StatisticAttributes {

    private Color color;
    private DiagramData diagram;
    private String name;

    public StatisticAttributes() {
        name = "";
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public DiagramData getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramData diagram) {
        this.diagram = diagram;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public StatisticAttributes clone() {
        StatisticAttributes clone = new StatisticAttributes();
        clone.setColor(color);
        clone.setDiagram(diagram);
        clone.setName(name);
        return clone;
    }
}
