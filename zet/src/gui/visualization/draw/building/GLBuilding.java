/*
 * Created on 19.06.2008
 *
 */
package gui.visualization.draw.building;

import gui.visualization.control.building.GLBuildingControl;
import gui.visualization.control.building.GLWallControl;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import opengl.framework.abs.AbstractDrawable;

/**
 * @author Daniel Pluempe, Jan-Philipp Kappmeier
 *
 */
public class GLBuilding extends AbstractDrawable<GLWall, GLBuildingControl, GLWallControl> {

	/**
	 * @param control
	 * @param cullingShape
	 */
	public GLBuilding( GLBuildingControl control ) {
		super( control );
		callChildren = false;
	}

	@Override
	public void performDrawing( GLAutoDrawable drawable ) {
		super.performDrawing( drawable );
		if( repaint ) {
			performStaticDrawing( drawable );
		}
		drawable.getGL().glCallList( displayList );
	}

	@Override
	public void performStaticDrawing( GLAutoDrawable drawable ) {
		// Erzeuge eine display-Liste falls nicht schon längst gemacht
		GL gl = drawable.getGL();
		if( displayList <= 0 )
			gl.glDeleteLists( displayList, 1 );
		displayList = gl.glGenLists( 1 );
		gl.glNewList( displayList, GL.GL_COMPILE );
		staticDrawAllChildren( drawable );
		gl.glEndList();
		repaint = false;
	}
	
	/**
	 * {@inheritDoc}
	 * @see opengl.framework.abs.AbstractDrawable#update()
	 */
	@Override
	public void update() {
		repaint = true;
	}
}
