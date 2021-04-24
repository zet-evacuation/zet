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
package de.zet_evakuierung.visualization.building.model;

import java.awt.geom.Point2D;
import java.util.Iterator;

import de.zet_evakuierung.visualization.AbstractVisualizationModel;
import de.zet_evakuierung.visualization.building.BuildingVisualizationProperties;
import io.visualization.BuildingResults;
import io.visualization.BuildingResults.Wall;

/**
 * @author Jan-Philipp Kappmeier
 * @author Daniel R. Schmidt
 */
public class GLWallModel extends AbstractVisualizationModel<BuildingVisualizationModel>
        implements Iterable<Point2D.Double> {

    private final Wall backingWall;
    /**
     * Access to properties of the visualization run.
     */
    private final BuildingVisualizationProperties properties;

    public GLWallModel(BuildingResults buildingResults, Wall wall,
            BuildingVisualizationProperties properties,
            BuildingVisualizationModel visualizationModel) {
        super(visualizationModel);
        this.backingWall = wall;
        this.properties = properties;
    }
    public boolean isBarrier() {
        return backingWall.isBarrier();
    }

    public boolean isRoomLeft() {
        return backingWall.isRoomLeft();
    }

    public boolean isRoomRight() {
        return backingWall.isRoomRight();
    }

    public Wall.ElementType getWallType(int wallSegment) {
        return backingWall.getWallType(wallSegment);
    }

    public double getZPosition() {
        final int floor = backingWall.getFloor();
        final double height = floor * properties.getFloorHeight();
        return height * properties.getScaling();
    }

    @Override
    public Iterator<Point2D.Double> iterator() {
        final Iterator<Point2D.Double> sourceIterator = backingWall.getPoints().iterator();
        return new Iterator<Point2D.Double>() {
            @Override
            public boolean hasNext() {
                return sourceIterator.hasNext();
            }

            @Override
            public Point2D.Double next() {
                Point2D.Double nextSourcePoint = sourceIterator.next();
                return new Point2D.Double(nextSourcePoint.x * properties.getScaling(),
                        (-1) * nextSourcePoint.y * properties.getScaling());
            }
        };
    }
}
