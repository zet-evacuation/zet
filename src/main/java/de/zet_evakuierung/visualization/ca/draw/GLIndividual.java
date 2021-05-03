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

import java.awt.Color;

import javax.media.opengl.GL2;

import de.zet_evakuierung.visualization.ca.CellularAutomatonVisualizationProperties;
import de.zet_evakuierung.visualization.ca.model.GLIndividualModel;
import gui.visualization.QualityPreset;
import gui.visualization.util.Tuple;
import org.zetool.math.vectormath.Vector3;
import org.zetool.opengl.drawingutils.GLColor;
import org.zetool.opengl.framework.abs.AbstractDrawable;

/**
 * Draws an individual on a {@code OpenGL} canvas.
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLIndividual extends AbstractDrawable<GLIndividual, GLIndividualModel> {

    /**
     * Access to properties of the visualization run.
     */
    protected final CellularAutomatonVisualizationProperties properties;
    private final double individualHeight;
    private final double individualRadius;
    private final GLColor individualColor;
    private final GLColor deadColor;
    private final QualityPreset qualityPreset;

    public GLIndividual(GLIndividualModel model, CellularAutomatonVisualizationProperties properties) {
        super(model);
        this.properties = properties;
        individualHeight = properties.getIndividualHeight();
        individualRadius = properties.getIndividualRadius();

        individualColor = properties.getIndividualColor();
        deadColor = properties.getInvalidColor();
        qualityPreset = properties.getQualityPreset();
    }

    @Override
    public void update() {
    }

    static GLColor red = new GLColor(Color.red);
    static GLColor green = new GLColor(Color.green);

    @Override
    public void performDynamicDrawing(GL2 gl) {
        model.stepUpdate();
        if (model.isInvisible()) {
            return;
        }
        GLColor headColor;
        GLColor bodyColor;
        if (model.isDead()) {
            headColor = deadColor;
            bodyColor = deadColor;
        } else if (model.isEvacuated()) {
            return;
        } else {
            //headColor = green.blend( red, model.getHeadInformation() );
            headColor = properties.getEvacuationColor();
            bodyColor = individualColor;
        }
        gl.glPushMatrix();
        Tuple pos = model.getCurrentPosition();
        gl.glTranslated(pos.x, pos.y, 0.1);
        bodyColor.draw(gl);

        GLU_INSTANCE.gluCylinder(GLU_QUADRIC, /*1.2 **/ individualRadius, 0.0, individualHeight, qualityPreset.individualBodySlices, qualityPreset.individualBodyStacks);
        headColor.draw(gl);
        gl.glTranslated(0, 0, individualHeight - individualRadius * 0.7);

        // here the head is drawn...
        // perform frustum test if the center point is within the frustum
        Vector3 check = new Vector3(pos.x, pos.y, 1);
        // TODO Frustum
        //if( frustum.isPointInFrustum( check ) == Frustum.CullingLocation.inside )
        GLU_INSTANCE.gluSphere(GLU_QUADRIC, /*1.5 * */ individualRadius * 0.7, qualityPreset.individualHeadSlices, qualityPreset.individualHeadStacks);

        gl.glPopMatrix();
    }
}
