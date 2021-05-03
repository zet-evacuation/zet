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
package de.zet_evakuierung.visualization.network.draw;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.awt.TextureRenderer;

import de.zet_evakuierung.visualization.network.GraphVisualizationProperties;
import de.zet_evakuierung.visualization.network.model.GLNodeModel;
import gui.visualization.QualityPreset;
import org.zetool.opengl.drawingutils.GLColor;
import org.zetool.opengl.drawingutils.GLVector;
import org.zetool.opengl.framework.abs.AbstractDrawable;
import org.zetool.opengl.helper.Texture;
import org.zetool.opengl.helper.TextureManager;

public class GLNode extends AbstractDrawable<GLFlowEdge, GLNodeModel> {

    private double graphHeight = 70.0;
    private double radius;
    int nodeDisplayMode = GLU.GLU_FILL;//GLU.GLU_SILHOUETTE;
    int flowDisplayMode = GLU.GLU_FILL;
    final GLColor nodeColor;
    GLColor evacuationColor;
    GLColor sourceColor;
    GLColor deletedSourceColor;
    GLColor nodeBorderColor;
    // TODO read quality preset from VisualizatonOptionManager
    //private static QualityPreset qualityPreset = VisualizationOptionManager.getQualityPreset();
    private static QualityPreset qualityPreset = QualityPreset.VeryHighQuality;// QualityPreset.MediumQuality;

    public GLNode(GLNodeModel model, GraphVisualizationProperties properties) {
        super(model, new GLVector(model.getXPosition(), model.getYPosition(), model.getZPosition()));

        radius = properties.getNodeRadius();
        nodeColor = properties.getNodeColor();
        evacuationColor = properties.getEvacuationColor();
        sourceColor = properties.getSourceColor();
        deletedSourceColor = properties.getDeletedSourceColor();
        nodeBorderColor = properties.getNodeBorderColor();
        GLU_INSTANCE.gluQuadricDrawStyle(GLU_QUADRIC, nodeDisplayMode);
        GLU_INSTANCE.gluQuadricOrientation(GLU_QUADRIC, GLU.GLU_OUTSIDE);
    }

    @Override
    public void performDynamicDrawing(GL2 gl) {
        if (getModel().isCurrentlyOccupied()) {
            performFlowDrawing(gl);
        }
    }

    /**
     * Draws a node as a solid sphere. The number of slices and stacks is defined by the given quality preset.
     *
     * @param gl the context on which the node is drawn
     */
    protected void performFlowDrawing(GL2 gl) {
        GLU_INSTANCE.gluQuadricDrawStyle(GLU_QUADRIC, flowDisplayMode);

        gl.glColor4d(1.0, 0.0, 0.0, 1.0);

        //gl.glEnable( gl.GL_BLEND );
        //gl.glBlendFunc( gl.GL_SRC_ALPHA, gl.GL_ONE );
        GLU_INSTANCE.gluSphere(GLU_QUADRIC, radius * 0.8, qualityPreset.nodeSlices, qualityPreset.nodeStacks);
        //gl.glDisable( GL.GL_BLEND );
    }

    @Override
    public void update() {
    }

    @Override
    public void performStaticDrawing(GL2 gl) {
//        if( getModel().isCurrentlyOccupied() ) {
//            performFlowDrawing( drawable );
//        }
        GLU_INSTANCE.gluQuadricDrawStyle(GLU_QUADRIC, nodeDisplayMode);

        nodeBorderColor.draw(gl);
        double xOffset = -this.getModel().getXPosition();
        double yOffset = -this.getModel().getYPosition();

        if (model.isRectangleVisible()) {
            gl.glBegin(GL.GL_LINES);
            gl.glVertex3d(this.getModel().getNwX() + xOffset, this.getModel().getNwY() + yOffset, -model.getFloorHeight() + 0.1);
            gl.glVertex3d(this.getModel().getSeX() + xOffset, this.getModel().getNwY() + yOffset, -model.getFloorHeight() + 0.1);

            gl.glVertex3d(this.getModel().getSeX() + xOffset, this.getModel().getNwY() + yOffset, -model.getFloorHeight() + 0.1);
            gl.glVertex3d(this.getModel().getSeX() + xOffset, this.getModel().getSeY() + yOffset, -model.getFloorHeight() + 0.1);

            gl.glVertex3d(this.getModel().getSeX() + xOffset, this.getModel().getSeY() + yOffset, -model.getFloorHeight() + 0.1);
            gl.glVertex3d(this.getModel().getNwX() + xOffset, this.getModel().getSeY() + yOffset, -model.getFloorHeight() + 0.1);

            gl.glVertex3d(this.getModel().getNwX() + xOffset, this.getModel().getSeY() + yOffset, -model.getFloorHeight() + 0.1);
            gl.glVertex3d(this.getModel().getNwX() + xOffset, this.getModel().getNwY() + yOffset, -model.getFloorHeight() + 0.1);
            gl.glEnd();
        }
        //gl.glEnable( gl.GL_BLEND );
        //gl.glBlendFunc( gl.GL_SRC_ALPHA, gl.GL_ONE );

        if (model.isEvacuationNode()) {
            evacuationColor.draw(gl);
        } else {
            if (model.isSourceNode()) {
                sourceColor.draw(gl);
            } else {
                if (model.isDeletedSourceNode()) {
                    deletedSourceColor.draw(gl);
                } else {
                    nodeColor.draw(gl);
                }
            }
        }

        //System.out.println( "Textur s_1 beim Zeichnen benutzt" );
//                boolean enableLight = gl.glIsEnabled(  GL.GL_LIGHTING );
//        TextureManager texMan = TextureManager.getInstance();
//        String texName = "s_" + model.getNumber();
//
//        if( !texMan.contains( texName ) ) {
//            createTexture( model.getNumber() );
//        }
//
//        Texture tex = texMan.contains( texName ) ? texMan.get( texName ) : texMan.get( "empty" );
//        //gl.glDisable( GL.GL_LIGHTING );
//        gl.glEnable( GL.GL_TEXTURE_2D );
//        tex.bind();
//
//        //System.out.println( "Textur s_1 beim Zeichnen benutzt" );
//
//        glu.gluQuadricTexture(quadObj, true);
//        glu.gluQuadricNormals(quadObj, GLU.GLU_SMOOTH);
//
//        glu.gluQuadricOrientation( quadObj, GLU.GLU_INSIDE  );
//
//        gl.glPushMatrix();
//
//        gl.glRotated( 90+180, 1, 0, 0);
//        //gl.glRotated( 180, 0, 1, 0);
        GLU_INSTANCE.gluSphere(GLU_QUADRIC, radius, qualityPreset.nodeSlices, qualityPreset.nodeStacks);
//        gl.glDisable( GL.GL_TEXTURE_2D );
//        //if( enableLight )
//        //    gl.glEnable( GL.GL_LIGHTING );
//        gl.glPopMatrix();
    }
    static Image[] n = new Image[10];

    static {
        try {
            for (int i = 0; i < 10; ++i) {
                n[i] = ImageIO.read(new File("./textures/n_" + i + ".png"));
            }
        } catch (IOException ex) {
            System.out.println("Image not found");
        }
    }

    private void createTexture(int number) {
        try {
            BufferedImage image = new BufferedImage(1024, 512, BufferedImage.TYPE_INT_ARGB);

            TextureRenderer renderer = new TextureRenderer(1024, 512, true);
            //Graphics2D g2 = renderer.createGraphics();
            Graphics2D g2 = (Graphics2D) image.getGraphics();
            Texture t;

            int width = 1024;
            int height = 512;

            String name = "";
            if (number < 10) {
                int x = width / 2 - 170 / 2;
                int y = height / 2 - 240 / 2;

                g2.fillRect(0, 0, width, height);

                g2.drawImage(n[number], x, y, null);
                name = "s_" + number;
            } else if (number < 100) {
                int y = height / 2 - 240 / 2;
                g2.fillRect(0, 0, width, height);
                g2.drawImage(n[number / 10], width / 2 - 170, y, null);
                g2.drawImage(n[number % 10], width / 2, y, null);
                name = "s_" + number;

            }
            ImageIO.write(image, "png", new File("./textures/temp.png"));
            System.out.println("./textures/temp.png" + " for file " + number);

            // Now use it as you would any other OpenGL texture
            //Texture tex = new Texture(null, height, x );//  renderer.getTexture();
            TextureManager texMan = TextureManager.getInstance();

            if (!name.isEmpty()) {
                texMan.newTexture(name, "./textures/temp.png");
            }

        } catch (IOException ex) {
            Logger.getLogger(GLNode.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
