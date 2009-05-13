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
/**
 * Class GLIndividual2
 * Erstellt 10.06.2008, 22:23:01
 */
package gui.visualization.draw.ca;

import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.ca.GLIndividualControl;
import gui.visualization.util.Tuple;
import gui.visualization.util.VisualizationConstants;
import java.awt.Color;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import opengl.drawingutils.GLColor;
import opengl.framework.abs.AbstractDrawable;

/**
 * Draws an individual on a {@code OpenGL} canvas.
 * @author Jan-Philipp Kappmeier
 */
public class GLIndividual extends AbstractDrawable<GLIndividual, GLIndividualControl, GLIndividualControl> {
//public class GLIndividual extends AbstractDrawable<CullingShapeSphere, GLIndividual, GLIndividualControl, GLIndividualControl> {

	private static double individualHeight = VisualizationOptionManager.getIndividualHeight() * VisualizationConstants.SIZE_MULTIPLICATOR;
	private static double individualRadius = VisualizationOptionManager.getIndividualRadius() * VisualizationConstants.SIZE_MULTIPLICATOR;
	private static GLColor individualColor = VisualizationOptionManager.getIndividualColor();
	private static GLColor deadColor = new GLColor( 130, 55, 101 );

	public GLIndividual( GLIndividualControl control ) {
//		super( control, new CullingShapeSphere() );
		super( control );
	}

	@Override
	public void update() {
	}
	
	static GLColor red = new GLColor( Color.red );
	static GLColor green = new GLColor( Color.green );

	@Override
	public void performDrawing( GLAutoDrawable drawable ) {
		control.stepUpdate();
		if( control.isInvisible() )
			return;
		GLColor headColor;
		GLColor bodyColor;
		if( control.isDead() ) {
			headColor = deadColor;
			bodyColor = deadColor;
		} else if( control.isEvacuated() ) {
			return;
		} else {
			headColor = green.blend( red, control.getHeadInformation() );
			bodyColor = individualColor;
		}
		GL gl = drawable.getGL();
		gl.glPushMatrix();
		Tuple pos = control.getCurrentPosition();
		gl.glTranslated( pos.x * VisualizationConstants.SIZE_MULTIPLICATOR, pos.y * VisualizationConstants.SIZE_MULTIPLICATOR, 1 * VisualizationConstants.SIZE_MULTIPLICATOR );
		bodyColor.performGL( gl );
		glu.gluCylinder( quadObj, individualRadius, 0.0, individualHeight, 12, 1 );
		headColor.performGL( gl );
		gl.glTranslated( 0, 0, individualHeight - individualRadius * 0.7 );
		glu.gluSphere( quadObj, individualRadius * 0.7, 8, 8 );
		gl.glPopMatrix();
	}
}
