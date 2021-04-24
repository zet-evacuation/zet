/*
 * zet evacuation tool copyright © 2007-20 zet evacuation team
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
package gui.visualization;

import de.zet_evakuierung.visualization.building.BuildingVisualizationProperties;
import de.zet_evakuierung.visualization.ca.CellularAutomatonVisualizationProperties;
import de.zet_evakuierung.visualization.network.GraphVisualizationProperties;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationVisualizationProperties implements CellularAutomatonVisualizationProperties,
        GraphVisualizationProperties, BuildingVisualizationProperties {

    private double scaling = GraphVisualizationProperties.super.getScaling();
    private double floorHeight = GraphVisualizationProperties.super.getFloorHeight();

    @Override
    public double getFloorHeight() {
        return floorHeight;
    }

    public void setFloorHeight(double floorHeight) {
        this.floorHeight = floorHeight;
    }

    @Override
    public double getScaling() {
        return scaling;
    }

    public void setScaling(double scaling) {
        this.scaling = scaling;
    }
}
