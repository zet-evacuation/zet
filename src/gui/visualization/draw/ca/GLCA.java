package gui.visualization.draw.ca;

import gui.visualization.control.ca.GLCAControl;
import gui.visualization.control.ca.GLCAFloorControl;

import javax.media.opengl.GLAutoDrawable;
import opengl.framework.abs.AbstractDrawable;
import opengl.helper.CullingShapeCube;

//public class GLCA extends AbstractDrawable<CullingShapeCube, GLCAFloor, GLCAControl, GLCAFloorControl> {
public class GLCA extends AbstractDrawable<GLCAFloor, GLCAControl, GLCAFloorControl> {

	public GLCA( GLCAControl control ) {
//		super(control, new CullingShapeCube() );
		super(control );
	}
	
	@Override
    public void update(){ }

	@Override
	public String toString() {
		return "GLCA";
	}
	
	@Override
	public void performDrawing(GLAutoDrawable drawable) {
		super.performDrawing( drawable );
	}
}

