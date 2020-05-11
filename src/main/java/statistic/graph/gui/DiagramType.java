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
package statistic.graph.gui;

/**
 *
 * @author Martin Groß
 */
public enum DiagramType {

    AREA_CHART("Flächendiagramm", true, true, true, true),
    BAR_CHART("Säulendiagramm", true, true, true, true),
    LINE_CHART("Liniendiagramm", true, true, true, true),
    PIE_CHART("Kreisdiagramm", false, false, false, false),
    PIE_CHART_3D("Kreisdiagramm 3D", false, false, false, false),
    RING_CHART("Ringdiagramm", false, false, false, false),
    STEP_CHART("Stufendiagramm", true, true, true, true),
    STEP_AREA_CHART("Stufenflächendiagramm", true, true, true, true),
    TABLE("Tabelle", false, false, true, true);
    private String description;
    private boolean hasXAxis;
    private boolean hasYAxis;
    private boolean supportsSingleValues;
    private boolean supportsNestedLists;

    private DiagramType(String description, boolean hasXAxis, boolean hasYAxis) {
        this.description = description;
        this.hasXAxis = hasXAxis;
        this.hasYAxis = hasYAxis;
    }

    private DiagramType(String description, boolean hasXAxis, boolean hasYAxis, boolean supportsSingleValues, boolean supportsNestedLists) {
        this.description = description;
        this.hasXAxis = hasXAxis;
        this.hasYAxis = hasYAxis;
        this.supportsSingleValues = supportsSingleValues;
        this.supportsNestedLists = supportsNestedLists;
    }    
    
    public boolean hasXAxis() {
        return hasXAxis;
    }

    public boolean hasYAxis() {
        return hasYAxis;
    }
    
    public boolean supportsSingleValues() {
        return supportsSingleValues;
    }
    
    public boolean supportsNestedLists() {
        return supportsNestedLists;
    }

    @Override
    public String toString() {
        return description;
    }
}
