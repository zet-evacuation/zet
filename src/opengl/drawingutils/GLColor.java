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
 *//**
 * Class GLColor
 * Erstellt 08.05.2008, 02:29:56
 */

package opengl.drawingutils;

import java.awt.Color;
import javax.media.opengl.GL;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public final class GLColor implements DrawingHelper {
	private double R = 0.0;
	private double G = 0.0;
	private double B = 0.0;
	private double A = 1.0;
	private float[] mat = new float[4];
	
	/**
	 * Initialize with black.
	 */
	public GLColor() { }
	
	public GLColor(int red, int green, int blue) {
		setValue( red, green, blue, 255 );
	}

	public GLColor( int red, int green, int blue, int alpha ) {
		setValue( red, green, blue, alpha );
	}

	public GLColor( float red, float green, float blue, float alpha ) {
		setValue( red, green, blue, alpha );
	}
	
	public GLColor(double red, double green, double blue, double alpha)
	{
		setValue(red, green, blue, alpha);
	}

	/**
	 * Initialize with java <code>Color</code> object.
	 * @param c the color
	 */
	public GLColor( Color c ) {
		setValue( c.getRed(), c.getGreen(), c.getBlue(), 255 );
	}

	/**
	 * Initialize with java <code>Color</code> object and specified alpha value.
	 * @param c the color
	 * @param alpha the alpha value that shall be used
	 */
	public GLColor( Color c, int alpha ) {
		setValue( c.getRed(), c.getGreen(), c.getBlue(), alpha );
	}

	/**
	 * Initialize with java <code>Color</code> object and specified alpha value.
	 * @param c the color
	 * @param alpha the alpha value that shall be used
	 */
	public GLColor( Color c, double alpha ) {
		setValue( c.getRed()/255., c.getGreen()/255., c.getBlue()/255., alpha );
	}

	
	private final void setValue( double r, double g, double b, double a ) {
		R = r;
		G = g;
		B = b;
		A = a;
		setRange();
		mat[0] = (float)R;
		mat[1] = (float)G;
		mat[2] = (float)B;
		mat[3] = (float)A;
	}

	private final void setValue( int r, int g, int b, int a ) {
		R = r / 255.;
		G = g / 255.;
		B = b / 255.;
		A = a / 255.;
		setRange();
		mat[0] = (float)R;
		mat[1] = (float)G;
		mat[2] = (float)B;
		mat[3] = (float)A;
	}
	
	private final void setRange( ) {
		R = Math.min( R, 1.0 );
		R = Math.max( R, 0.0 );
		G = Math.min( G, 1.0 );
		G = Math.max( G, 0.0 );
		B = Math.min( B, 1.0 );
		B = Math.max( B, 0.0 );
		A = Math.min( A, 1.0 );
		A = Math.max( A, 0.0 );
	}
	
	public final double getRed() {
		return R;
	}
	
	public final double getGreen() {
		return G;
	}
	
	public final double getBlue() {
		return B;
	}
	
	public final double getAlpha() {
		return A;
	}

	/**
	 * <p>Sets the color in OpenGL, can set color using materials and color depending
	 * from the current mode</p>
	 * <p>Note that it is not allowed to use glIsEnabled() between glBegin() and
	 * glEnd(). Use performGL( GL, boolean ) instead.</p>
	 * @param gl the OpenGL context on which shall be drawn
	 */
	public final void performGL( GL gl ) {
		if( gl.glIsEnabled(  GL.GL_LIGHTING) ) {
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat, 0);
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat, 0);
		} else
			gl.glColor4d( R, G, B, A );
	}

	/**
	 * Sets the color in OpenGL, can set color using materials and color.
	 * @param gl the OpenGL context on which shall be drawn
	 * @param lighting sets wheather the lighting or non-lighting version should be used
	 */
	public final void performGL( GL gl, boolean lighting ) {
		if( lighting ) {
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat, 0);
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat, 0);
		} else
			gl.glColor4d( R, G, B, A );
	}
	
	public GLColor blend(double percentage){
	    GLColor blendedColor = new GLColor(R * percentage, G * percentage, B * percentage, A);
	    //blendedColor.setRange();
	    return blendedColor;
	}
	
	public GLColor blend(GLColor color, double percentage){
	    if(percentage >= 1){
	        return new GLColor(color.R, color.G, color.B, color.A);
	    }
	    
	    if(percentage <= 0){
	        return new GLColor(this.R, this.G, this.B, this.A);
	    }
	    
	    GLColor blendedColor = new GLColor(
	            (1-percentage) * this.R + percentage*color.R,
	            (1-percentage) * this.G + percentage*color.G,
	            (1-percentage) * this.B + percentage*color.B,
	            (1-percentage) * this.A + percentage*color.A
	    );
	    
	    //blendedColor.setRange();
	    return blendedColor;
	}
}
