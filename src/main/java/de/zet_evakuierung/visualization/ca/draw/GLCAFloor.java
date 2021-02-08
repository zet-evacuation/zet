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
package de.zet_evakuierung.visualization.ca.draw;

import java.util.List;
import java.util.Objects;

import javax.media.opengl.GL2;

import de.zet_evakuierung.visualization.ca.model.GLFloorModel;
import org.zetool.opengl.drawingutils.GLVector;
import org.zetool.opengl.framework.abs.AbstractDrawable;

/**
 * @author Jan-Philipp Kappmeier
 */
public class GLCAFloor extends AbstractDrawable<GLRoom, GLFloorModel> {

    private Iterable<GLIndividual> allIndividuals;

    public GLCAFloor(GLFloorModel model, Iterable<GLIndividual> allIndividuals) {
        super(model, new GLVector(model.getXPosition(), model.getYPosition(), model.getZPosition()));
        this.allIndividuals = Objects.requireNonNull(allIndividuals);
    }

    @Override
    public void update() {
    }

    public void setIndividuals(List<GLIndividual> allIndividuals) {
        this.allIndividuals = allIndividuals;
    }

    @Override
    public String toString() {
        return "GLCAFloor";
    }

    @Override
    public void performDynamicDrawing(GL2 gl) {
        for (GLIndividual individual : allIndividuals) {
            if (individual.getModel().onFloor() == model.getFloorNumber()) {
                individual.performDynamicDrawing(gl);
            }
        }
    }
}