package gui.visualization.draw.graph;

import java.util.Iterator;
import opengl.framework.abs.AbstractDrawable; 
import opengl.helper.CullingShapeCube;

import gui.visualization.control.graph.GLGraphControl; 
import gui.visualization.control.graph.GLGraphFloorControl;

import javax.media.opengl.GLAutoDrawable;
 
public class GLGraph extends AbstractDrawable<GLGraphFloor, GLGraphControl, GLGraphFloorControl> {
//public class GLGraph extends AbstractDrawable<CullingShapeCube, GLGraphFloor, GLGraphControl, GLGraphFloorControl> {

    public GLGraph (GLGraphControl control ) {
//			super(control, new CullingShapeCube() );
			super(control );
    }

    /* (non-Javadoc)
     * @see opengl.framework.abs.AbstractDrawable#update()
     */
    @Override
    public void update() {
        
    }
}

