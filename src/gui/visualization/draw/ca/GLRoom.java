package gui.visualization.draw.ca;

import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.ca.GLCellControl;
import gui.visualization.control.ca.GLRoomControl;
import gui.visualization.util.VisualizationConstants;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import opengl.drawingutils.GLVector;
import opengl.framework.abs.AbstractDrawable;

public class GLRoom extends AbstractDrawable<GLCell, GLRoomControl, GLCellControl> {
	private GLVector ul;	// upper left
	private GLVector ur;	// upper right
	private GLVector ll;	// lower left
	private GLVector lr;	// lower right
//public class GLRoom extends AbstractDrawable<CullingShapeCube, GLCell, GLRoomControl, GLCellControl> {
	public GLRoom( GLRoomControl control ) {
		super( control );
//			super(control, new CullingShapeCube() );
		this.position.x = control.getXPosition() * VisualizationConstants.SIZE_MULTIPLICATOR;
		this.position.y = control.getYPosition() * VisualizationConstants.SIZE_MULTIPLICATOR;
		if( VisualizationOptionManager.showSpaceBetweenCells() ) {	
			ul = new GLVector( 0 * VisualizationConstants.SIZE_MULTIPLICATOR, 0 * VisualizationConstants.SIZE_MULTIPLICATOR, -1 * VisualizationConstants.SIZE_MULTIPLICATOR );
			ur = new GLVector( control.getWidth() * 40 * VisualizationConstants.SIZE_MULTIPLICATOR, 0 * VisualizationConstants.SIZE_MULTIPLICATOR, -1 * VisualizationConstants.SIZE_MULTIPLICATOR );
			ll = new GLVector( 0 * VisualizationConstants.SIZE_MULTIPLICATOR, control.getHeight() * -40 * VisualizationConstants.SIZE_MULTIPLICATOR, -1 * VisualizationConstants.SIZE_MULTIPLICATOR );
			lr = new GLVector( control.getWidth() * 40 * VisualizationConstants.SIZE_MULTIPLICATOR, control.getHeight() * -40 * VisualizationConstants.SIZE_MULTIPLICATOR, -1 * VisualizationConstants.SIZE_MULTIPLICATOR );
		}
	}

	@Override
	public void update() {
		
	}
	
	@Override
	public void performDrawing( GLAutoDrawable drawable ) {
		super.performDrawing( drawable );
		GL gl = drawable.getGL();
		if( VisualizationOptionManager.showSpaceBetweenCells() ) {
			// draw a floor
			VisualizationOptionManager.getCellSeperationColor().performGL( gl );
			gl.glBegin(GL.GL_QUADS);
				gl.glNormal3d( 0, 0, 1 );
				ul.draw( drawable );
				ur.draw( drawable );
				lr.draw( drawable );
				ll.draw( drawable );
			gl.glEnd();
			
		}
	}

	@Override
	public String toString() {
		return "GLRoom";
	}
}

