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
/*
 * OpenGLHelper.java
 * Created on 29.01.2008, 23:29:46
 */
package opengl.helper;

import javax.media.opengl.GL;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public final class ProjectionHelper {

  private ProjectionHelper() { }

  /**
   * 
   * @param gl
   * @param viewportWidth
   * @param viewportHeight
   * @param x
   * @param y
   * @param z
   * @param favouredWidth
   * @param favouredHeight
   * @param depth
   */
  public static void setViewOrthogonal( GL gl, int viewportWidth, double viewportHeight, double x, double y, double z, double favouredWidth, double favouredHeight, double depth ) {
    // Avoid division by zero
    if( viewportHeight <= 0 ) {
      viewportHeight = 1;
    }
    if( viewportWidth <= 0 ) {
      viewportWidth = 1;
    }

    // Set up projection matrices for 2d-like orthogonal projection
    gl.glMatrixMode( GL.GL_PROJECTION );										// Set projection-matrix-mode
    gl.glLoadIdentity();																		// Load identity matrix
    double aspect = viewportWidth / viewportHeight;					// calculate new aspect ratio
    double aspectInverse = viewportHeight / viewportWidth;	// calculate new aspect ratio

    // Calculate real width and height
    double rWidth;
    double rHeight;
    if( aspect < 1 ) {
      rWidth = favouredWidth;
      rHeight = favouredWidth * aspectInverse;
    } else {
      rWidth = favouredHeight * aspect;
      rHeight = favouredHeight;
    }

    // Set up orthoganal view arout x,y,z
    gl.glOrtho( x - rWidth * 0.5, x + rWidth * 0.5, y - rHeight * 0.5, y + rHeight * 0.5, z - depth * 0.5, z + depth * 0.5 );
  }

  /**
   * 
   * @param gl
   * @param viewportWidth
   * @param viewportHeight
   * @param x
   * @param y
   * @param z
   * @param favouredWidth
   * @param favouredHeight
   * @param depth
   */
  public static void setViewStretchedOrthogonal( GL gl, int viewportWidth, double viewportHeight, double x, double y, double z, double favouredWidth, double favouredHeight, double depth ) {
    // Avoid division by zero
    if( viewportHeight <= 0 ) {
      viewportHeight = 1;
    }
    if( viewportWidth <= 0 ) {
      viewportWidth = 1;
    }

    // Set up projection matrices for 2d-like orthogonal projection
    gl.glMatrixMode( GL.GL_PROJECTION );										// Set projection-matrix-mode
    gl.glLoadIdentity();																		// Load identity matrix
    double aspect = viewportHeight / viewportWidth;					// calculate new aspect ratio
    double aspectInverse = viewportWidth / viewportHeight;	// calculate new aspect ratio

    // Calculate real width and height
    double rWidth;
    double rHeight;
    if( aspect < 1 ) {
      rWidth = favouredWidth;
      rHeight = favouredWidth * aspectInverse;
    } else {
      rWidth = favouredHeight * aspect;
      rHeight = favouredHeight;
    }

    // Set up orthoganal view arout x,y,z
    gl.glOrtho( x - rWidth * 0.5, x + rWidth * 0.5, y - rHeight * 0.5, y + rHeight * 0.5, z - depth * 0.5, z + depth * 0.5 );
  }

  /**
   * 
   * @param gl
   * @param distance
   * @param azimuth
   * @param incidence
   * @param twist
   */
  public static void setViewPolar( GL gl, double distance, double azimuth, double incidence, double twist ) {
    gl.glTranslated( 0.0, 0.0, -distance );
    gl.glRotated( -twist, 0.0, 0.0, 1.0 );
    gl.glRotated( -incidence, 1.0, 0.0, 0.0 );
    gl.glRotated( -azimuth, 0.0, 0.0, 1.0 );
		System.out.println( "Set to distance " + distance );
  }

		
	/**
	 * 
	 * @param gl
	 * @param width
	 * @param height
	 */
	public static void setPrintScreenProjection( GL gl, int width, int height ) {
    gl.glMatrixMode( gl.GL_PROJECTION );
    gl.glPushMatrix();
    gl.glLoadIdentity();
    gl.glOrtho( 0, width, 0, height, -100, 100 ); // full size orthogonal screen
    gl.glMatrixMode( gl.GL_MODELVIEW );
    gl.glPushMatrix();
    gl.glLoadIdentity();
	}
	
	public static void resetProjection( GL gl ) {
    // restore matrices
    gl.glMatrixMode( gl.GL_PROJECTION );
    gl.glPopMatrix();
    gl.glMatrixMode( gl.GL_MODELVIEW );
    gl.glPopMatrix();
	}
}
