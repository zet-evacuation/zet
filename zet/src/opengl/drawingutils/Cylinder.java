/**
 * Cylinder.java
 * Created: Sep 14, 2010,2:16:08 PM
 */
package opengl.drawingutils;

import javax.media.opengl.GL;

/**
 * A helping class providing methods to draw a cylinder with more features than
 * the {@code gluCylinder} provides.
 * @author Jan-Philipp Kappmeier
 */
public class Cylinder {
	/**
	 * <p>Draws a cylinder. Basically this does the same as {@code gluCylinder}, but
	 * it only supports cylinders with same radius at base and top. Also, the
	 * number of stacks is fixed to 1.</p>
	 * <p>In addition to the (missing) features of {@code gluCylinder}, this method
	 * provides the ability of a color gradient on the cylinder.</p>
	 * @param gl the graphics object used to draw the cylinder
	 * @param radius the radius of the cylinder
	 * @param height the height of the cylinder
	 * @param slices the number of slices of the cylinder
	 * @param baseColor the color at the bottom
	 * @param topColor the color at the top
	 */
	public static void drawCylinder( GL gl, double radius, double height, int slices, GLColor baseColor, GLColor topColor ) {
		final double step = 2.0 * Math.PI / slices;
		// todo: give support to sign somehow. (change directions of normals)

		if( gl.glIsEnabled( GL.GL_LIGHTING ) )
			gl.glEnable( GL.GL_COLOR_MATERIAL );
		gl.glBegin( GL.GL_QUAD_STRIP );
		for( int i = 0; i <= slices; i++ ) {
			double x, y;
			if( i == slices ) {
				x = Math.sin( 0.0 );
				y = Math.cos( 0.0 );
			} else {
				x = Math.sin( i * step );
				y = Math.cos( i * step );
			}
			normal3d( gl, x, y, 0 );
			baseColor.draw( gl, false );
			gl.glVertex3d( x * radius, y * radius, 0 );
			normal3d( gl, x, y, 0 );
			topColor.draw( gl, false );
			gl.glVertex3d( x * radius, y * radius, height );
		}
		gl.glEnd();

		if( gl.glIsEnabled( GL.GL_LIGHTING ) )
			gl.glDisable( GL.GL_COLOR_MATERIAL );
	}

	/**
	 * Call glNormal3f after scaling normal to unit length.
	 */
	private static void normal3d( GL gl, double x, double y, double z  ) {
		final double mag = Math.sqrt( x * x + y * y + z * z );
		if( mag > 0.00001 ) {
			x /= mag;
			y /= mag;
			z /= mag;
		}
		gl.glNormal3d( x, y, z );
	}
}
