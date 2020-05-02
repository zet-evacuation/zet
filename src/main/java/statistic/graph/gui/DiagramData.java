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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
/*
 * DiagramData.java
 *
 */
package statistic.graph.gui;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author Martin Groß
 */
@XStreamAlias("diagram")
public class DiagramData implements Cloneable {

    private String title;
    private DiagramType type;
    private String xAxisLabel;
    private String yAxisLabel;

    public DiagramData() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DiagramType getType() {
        return type;
    }

    public void setType(DiagramType type) {
        this.type = type;
    }

    public String getXAxisLabel() {
        return xAxisLabel;
    }

    public void setXAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    public String getYAxisLabel() {
        return yAxisLabel;
    }

    public void setYAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    @Override
    protected DiagramData clone() {
        DiagramData clone = new DiagramData();
        clone.setTitle(title);
        clone.setType(type);
        clone.setXAxisLabel(xAxisLabel);
        clone.setYAxisLabel(yAxisLabel);
        return clone;
    }

    @Override
    public String toString() {
        return title;
    }
}
