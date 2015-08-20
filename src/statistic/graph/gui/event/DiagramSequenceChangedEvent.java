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
/*
 * DiagramSequenceChangedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DiagramData;

/**
 *
 * @author Martin Groß
 */
public class DiagramSequenceChangedEvent implements DiagramEvent {
    
    private DiagramData diagram;
    private DiagramData diagram2;

    public DiagramSequenceChangedEvent(DiagramData diagram, DiagramData diagram2) {
        this.diagram = diagram;
        this.diagram2 = diagram2;
    }

    public DiagramData getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramData diagram) {
        this.diagram = diagram;
    }

    public DiagramData getDiagram2() {
        return diagram2;
    }

    public void setDiagram2(DiagramData diagram2) {
        this.diagram2 = diagram2;
    }

    @Override
    public String toString() {
        return "DiagramSequenceChanged: " + diagram + " <-> " + diagram2;
    }
}
