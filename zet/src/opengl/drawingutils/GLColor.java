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

/**
 * Class GLColor
 * Created 08.05.2008, 02:29:56
 */

package opengl.drawingutils;

import java.awt.Color;
import javax.media.opengl.GL;
import opengl.framework.abs.Drawable;

/**
 * The class {@code GLColor} encapsulates a <i>color</i> defined by RGB values.
 * Each value (red, green and blue) can be in the range from 0.0 to 1.0 or from
 * 0 to 255. Additionally each color can have an alpha value, where a value of
 * 0 means that the color is transparent. The class provides methods to set
 * it in {@code OpenGL} as a color. It also allows blending and additional
 * operations.
 * @author Jan-Philipp Kappmeier
 */
public final class GLColor implements Drawable {
	/** The red part of the color. */
	private double R = 0.0;
	/** The green part of the color. */
	private double G = 0.0;
	/** The blue part of the color. */
	private double B = 0.0;
	/** The alpha part of the color. */
	private double A = 1.0;
	/** The material definitions of the color. Only used by {@code OpenGL} */
	private float[] mat = new float[4];

	/** The {@code GLColor} version of white. */
	public static final GLColor white = new GLColor( Color.white );
	/** The {@code GLColor} version of light gray. */
	public static final GLColor lightGray = new GLColor( Color.lightGray );
	/** The {@code GLColor} version of gray. */
	public static final GLColor gray = new GLColor( Color.gray );
	/** The {@code GLColor} version of dark gray. */
	public static final GLColor darkGray = new GLColor( Color.darkGray );
	/** The {@code GLColor} version of black. */
	public static final GLColor black = new GLColor( Color.black );
	/** The {@code GLColor} version of red. */
	public static final GLColor red = new GLColor( Color.red );
	/** The {@code GLColor} version of pink. */
	public static final GLColor pink = new GLColor( Color.pink );
	/** The {@code GLColor} version of orange. */
	public static final GLColor orange = new GLColor( 255, 127, 0 );
	/** The {@code GLColor} version of yellow. */
	public static final GLColor yellow = new GLColor( 255, 205, 0 );
	/** The {@code GLColor} version of green. */
	public static final GLColor green = new GLColor( Color.green );
	/** The {@code GLColor} version of magenta. */
	public static final GLColor magenta = new GLColor( Color.magenta );
	/** The {@code GLColor} version of cyan. */
	public static final GLColor cyan = new GLColor( Color.cyan );
	/** The {@code GLColor} version of blue. */
	public static final GLColor blue = new GLColor( Color.blue );
	/** The {@code GLColor} version of indigo. */
	public static final GLColor indigo = new GLColor( 111, 0, 255 );
	/** The {@code GLColor} version of violet. */
	public static final GLColor violet = new GLColor( 143, 0, 255 );
	/**
	 * Initialize with black.
	 */
	public GLColor() { }

	/**
	 * Initializes a color without any transparency.
	 * @param red the red part of the color in the range 0 to 255
	 * @param green the green part of the color in the range 0 to 255
	 * @param blue the blue part of the color in the range 0 to 255
	 */
	public GLColor(int red, int green, int blue) {
		setValue( red, green, blue, 255 );
	}

	/**
	 * Initializes a color with additional transparency.
	 * @param red the red part of the color in the range 0 to 255
	 * @param green the green part of the color in the range 0 to 255
	 * @param blue the blue part of the color in the range 0 to 255
	 * @param alpha the alpha value of the color. 0 for full transparency
	 */
	public GLColor( int red, int green, int blue, int alpha ) {
		setValue( red, green, blue, alpha );
	}

	/**
	 * Initializes a color with additional transparency. All parameters are float
	 * which is the natural floating point precision of {@code OpenGL}.
	 * @param red the red part of the color in the range {@code 0.0f} to {@code 1.0f}
	 * @param green the green part of the color in the range{@code  0.0f} to {@code 1.0f}
	 * @param blue the blue part of the color in the range {@code 0.0f} to {@code 1.0f}
	 * @param alpha the alpha value of the color. {@code 0.0f} for full transparency
	 */
	public GLColor( float red, float green, float blue, float alpha ) {
		setValue( red, green, blue, alpha );
	}
	
	/**
	 * Initializes a color with additional transparency.
	 * @param red the red part of the color in the range 0.0 to 1.0
	 * @param green the green part of the color in the range 0.0 to 1.0
	 * @param blue the blue part of the color in the range 0.0 to 1.0
	 * @param alpha the alpha value of the color. 0.0 for full transparency
	 */
	public GLColor(double red, double green, double blue, double alpha)	{
		setValue(red, green, blue, alpha);
	}

	/**
	 * Initialize with java {@code Color} object without any alpha. The alpha of
	 * the color is ignored.
	 * @param c the color
	 */
	public GLColor( Color c ) {
		setValue( c.getRed(), c.getGreen(), c.getBlue(), 255 );
	}

	/**
	 * Initialize with java {@code Color} object and specified alpha value that
	 * differs from the colors alpha.
	 * @param c the color
	 * @param alpha the alpha value that shall be used
	 */
	public GLColor( Color c, int alpha ) {
		setValue( c.getRed(), c.getGreen(), c.getBlue(), alpha );
	}

	/**
	 * Initialize with java <code>Color</code> object and specified alpha value
	 * that differs from the colors alpha.
	 * @param c the color
	 * @param alpha the alpha value that shall be used
	 */
	public GLColor( Color c, double alpha ) {
		setValue( c.getRed()/255., c.getGreen()/255., c.getBlue()/255., alpha );
	}
	
	/**
	 * Sets both, the RGB variables and the material to the specified values.
	 * @param r the red part of the color
	 * @param g the green part of the color
	 * @param b the blue part of the color
	 * @param a the alpha value of the color
	 */
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

	/**
	 * Sets both, the RGB variables and the material to the specified values.
	 * @param r the red part of the color
	 * @param g the green part of the color
	 * @param b the blue part of the color
	 * @param a the alpha value of the color
	 */
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

	/**
	 * Corrects color values. Values larger than 1.0 are reduced to 1.0 and values
	 * smaller than 0.0 are increased to 0.0.
	 */
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

	/**
	 * Returns the red part of the color.
	 * @return the red part of the color
	 */
	public final double getRed() {
		return R;
	}
	
	/**
	 * Returns the green part of the color.
	 * @return the green part of the color
	 */
	public final double getGreen() {
		return G;
	}
	
	/**
	 * Returns the blue part of the color.
	 * @return the blue part of the color
	 */
	public final double getBlue() {
		return B;
	}
	
	/**
	 * Returns the alpha part of the color.
	 * @return the alpha part of the color
	 */
	public final double getAlpha() {
		return A;
	}

	/**
	 * <p>Sets the color in {@code OpenGL}, can set color using materials and
	 * color depending from the current mode</p>
	 * <p>Note that it is not allowed to use {@code glIsEnabled()} between
	 * {@code glBegin()} and {@code glEnd()}. The method is used here, so to
	 * change the color in that special case use {@link #performGL( GL, boolean )}
	 * instead.</p>
	 * @param gl the {@code OpenGL} context on which shall be drawn
	 */
	public void draw( GL gl ) {
	if( gl.glIsEnabled(  GL.GL_LIGHTING ) && !gl.glIsEnabled( GL.GL_COLOR_MATERIAL ) ) {
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat, 0);
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat, 0);
		} else
			gl.glColor4d( R, G, B, A );
	}

	/**
	 * Sets the color in {@code OpenGL}, can set color using materials and color,
	 * depending from the value of {@code lighting}.
	 * @param gl the {@code OpenGL} context on which shall be drawn
	 * @param lighting sets whether the lighting or non-lighting version should be used
	 */
	public final void draw( GL gl, boolean lighting ) {
		if( lighting ) {
			gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, mat, 0 );
			gl.glMaterialfv( GL.GL_FRONT, GL.GL_AMBIENT, mat, 0 );
		} else
			gl.glColor4d( R, G, B, A );
	}

	/**
	 * Returns a new {@code GLColor}. The {@code percentage} how bright the color
	 * will be. A value of 0 returns black while a value of 1 will a copy of this
	 * instance.
	 * @param percentage the brightness of the color.
	 * @return the created blended color
	 */
	public GLColor blend( double percentage ) {
		return black.blend( this, percentage );
	}

	/**
	 * Returns a 50%-mixture of the passed {@code color} and the color of this
	 * instance.
	 * @param color the color which is used for the mix
	 * @return the mixed color
	 */
	public GLColor blend( GLColor color ) {
		return blend( color, 0.5 );
	}

	/**
	 * Creates a blended color that is a mixture of {@code percentage} fraction
	 * of this color and {@code 1-percentage} fraction of {@code color}. A
	 * percentage of 1 will return a copy of {@code color}, a value of 0 will
	 * return a copy of this instance.
	 * @param color the color used for the blending
	 * @param percentage the percentage of which the colors are blended.
	 * @return the new color
	 */
	public GLColor blend( GLColor color, double percentage ) {
	    if( percentage >= 1 )
	        return new GLColor( color.R, color.G, color.B, color.A );
	    
	    if( percentage <= 0 )
	        return new GLColor( this.R, this.G, this.B, this.A );
	    
	    GLColor blendedColor = new GLColor(
	            (1-percentage) * this.R + percentage * color.R,
	            (1-percentage) * this.G + percentage * color.G,
	            (1-percentage) * this.B + percentage * color.B,
	            (1-percentage) * this.A + percentage * color.A
	    );
	    
	    //blendedColor.setRange();
	    return blendedColor;
	}

	public static GLColor blend( GLColor color1, GLColor color2, double percentage ) {
		GLColor blendedColor = new GLColor(
						color1.R + (color2.R - color1.R)*percentage,
						color1.G + (color2.G - color1.G)*percentage,
						color1.B + (color2.B - color1.B)*percentage,
						color1.A + (color2.A - color1.A)*percentage
						);
		return blendedColor;
	}

	/**
	 * Returns a new {@code GLColor}. The {@code percentage} how bright the color
	 * will be. A value of 0 returns a copy of this instance white while a value
	 * of 1 will return white.
	 * @param percentage the brightness of the color.
	 * @return the created blended color
	 */
	public GLColor blendInv( double percentage ) {
		return blend( white, percentage );
	}

	public void update() {
		
	}

	@Override
	public String toString() {
		return "("+ Math.round(R*255) + "," + Math.round(G*255) + "," + Math.round(B*255) + ")";
	}

	public void delete() {
		
	}


}
