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

import org.zetool.opengl.framework.abs.VisualizationModel;
import org.zetool.opengl.framework.abs.VisualizationModelProvider;
import org.zetool.opengl.helper.Frustum;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class EvacuationVisualizationModel implements VisualizationModel, VisualizationModelProvider {

    public double scaling = 1;
    private Frustum frustum;
    public double defaultFloorHeight = 10;

    public void setScaling(double scaling) {
        this.scaling = scaling;
    }

    @Override
    public Frustum getFrustum() {
        return frustum;
    }

    @Override
    public void setFrustum(Frustum frustum) {
        this.frustum = frustum;
    }

    public void setDefaultFloorHeight(double defaultFloorHeight) {
        this.defaultFloorHeight = defaultFloorHeight;
    }

    public abstract double getStep();

    @Override
    public abstract void addTime(long timeNanoSeconds);

    @Override
    public abstract void setTime(long time);

    @Override
    public abstract void resetTime();

    @Override
    public abstract boolean isFinished();

}
