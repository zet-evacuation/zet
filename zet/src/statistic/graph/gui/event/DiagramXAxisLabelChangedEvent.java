/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * DiagramTitleChangedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DiagramData;

/**
 *
 * @author Martin Groß
 */
public class DiagramXAxisLabelChangedEvent implements DiagramChangedEvent {
    
    private DiagramData diagram;
    private String newXAxisLabel;

    public DiagramXAxisLabelChangedEvent(DiagramData diagram, String newXAxisLabel) {
        this.diagram = diagram;
        this.newXAxisLabel = newXAxisLabel;
    }

    public DiagramData getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramData diagram) {
        this.diagram = diagram;
    }

    public String getNewXAxisLabel() {
        return newXAxisLabel;
    }

    public void setNewXAxisLabel(String newXAxisLabel) {
        this.newXAxisLabel = newXAxisLabel;
    }
}