/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * OpenGLHelper.java
 * Created on 29.01.2008, 23:29:46
 */

package opengl.helper;

import javax.media.opengl.GL;

/**
 * A utility class that allows to set up several projections in the
 * {@code OpenGL} world. Whenever a method
 * (except {@link #resetProjection(javax.media.opengl.GL)}) is called, the
 * current projection and modelview matrices are pushed to the stack.
 * @author Jan-Philipp Kappmeier
 */
public final class ProjectionHelper {

	/**
	 * Avoids instanciation of the utility class.
	 */
  private ProjectionHelper() { }

  /**
   * Sets up an orthogonal projection.
   * @param gl the OpenGL graphics context
   * @param viewportWidth the width of the viewport
   * @param viewportHeight the height of the viewport
   * @param x the center point {@code x}-position in world coorinates
   * @param y the center point {@code y}-position in world coorinates
   * @param z the center point {@code z}-position in world coorinates
   * @param favouredWidth
   * @param favouredHeight
   * @param depth the depth that can be used to draw
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
   * Sets up an orthognal projection. In opposite to {@link #setViewOrthogonal()},
	 * the visible range is stretched if the screen is wider than the viewport. In
	 * that case the upper and lower part are cut of.
   * @param gl the OpenGL graphics context
   * @param viewportWidth the width of the vewport
   * @param viewportHeight the height of the viewport
   * @param x the center point {@code x}-position in world coorinates
   * @param y the center point {@code y}-position in world coorinates
   * @param z the center point {@code z}-position in world coorinates
   * @param favouredWidth
   * @param favouredHeight
   * @param depth the depth that can be used to draw
   */
  public static void setViewStretchedOrthogonal( GL gl, int viewportWidth, double viewportHeight, double x, double y, double z, double favouredWidth, double favouredHeight, double depth ) {
    // Avoid division by zero
		if( viewportHeight <= 0 )
      viewportHeight = 1;
    if( viewportWidth <= 0 )
      viewportWidth = 1;

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
   * Sets up a polar view projection.
   * @param gl the OpenGL graphics context
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
	 * Sets up a projection to print text on the screen. It uses an orthogonal
	 * projection and allows to address each pixel in the submittet range.
	 * @param gl the OpenGL render context
	 * @param width the width of the screen
	 * @param height the height of the screen
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

	/**
	 * Resets a projection to the old status by deleting projection matrices from
	 * the stack.
	 * @param gl the OpenGL render context
	 */
	public static void resetProjection( GL gl ) {
    // restore matrices
    gl.glMatrixMode( gl.GL_PROJECTION );
    gl.glPopMatrix();
    gl.glMatrixMode( gl.GL_MODELVIEW );
    gl.glPopMatrix();
	}
}
