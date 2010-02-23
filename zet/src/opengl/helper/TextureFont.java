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
 * Class TextureFont
 * Erstellt 18.05.2008, 00:10:48
 */

package opengl.helper;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import javax.media.opengl.GL;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TextureFont {
  int base;
  int texture;
  int count;
  int width;
  int height;
  int m_size;
  int m_width;
	Texture fontTexture;
	
	GL gl;

	public TextureFont( GL gl, Texture fontTexture) {
		this.gl = gl;
		this.fontTexture = fontTexture;
		
	}

	/**
	 * Builds a TextureFont on Microsoft Windows
	 * @param cpl the number of characters in one line
	 * @param cpr the number of characters in one row
	 * @param size the quadratic size of the characters within the file 
	 * @param targetsize the size that each character should have
	 * @param skipspace the space which the next letter should start more right
	 */
	// TODO unsigned values! exception if negative
	public void buildFont( int cpl, int cpr, int size, int targetsize, double skipspace ) {
		m_width = (int)skipspace;
		m_size = targetsize;
		double	cx;								// character x coordinates
		double	cy;								// character y coordinates

		count = cpr  * cpl;
		base = gl.glGenLists( count );							 // create a display list big enough for all characters
		
		fontTexture.bind();				// select the font texture

		for( int i=0; i < count; i++ ) {
			cx = (i%cpl)/(double)cpl;			 // x position of current character (left)
			cy = (i/cpl)/(double)cpr;					// y position of current character (top)

			gl.glNewList( base + i, gl.GL_COMPILE );				// create a listelement
				// somehow, the upper and lower ends of the bitmap are the opposite as on
				// C++... is it a java thing?
				gl.glBegin( gl.GL_QUADS );					// each character is a quad
					gl.glTexCoord2d( cx, cy+(1/(double)cpr) );			// texture coordinates bottom left
					gl.glVertex2i( 0, 0 );				// vertex coordinates bottom left
					gl.glTexCoord2d( cx + (1/(double)cpl), cy+(1/(double)cpr));	// texture coordinates bottom right
					gl.glVertex2i( targetsize, 0 );				// vertex coordinates bottom right
					gl.glTexCoord2d( cx + (1/(double)cpl), cy);			// texture coordinates top right
					gl.glVertex2i( targetsize, targetsize );				// vertex coordinates top right
					gl.glTexCoord2d( cx, cy );				 // texture coordinates top left
					gl.glVertex2i( 0, targetsize );				// vertex coordinates top left
				gl.glEnd();
				gl.glTranslated( skipspace, 0, 0 );	// each character is only 10 pixels wide, move to the right
			gl.glEndList();							 // finish list
		}
	}

public void buildFont3( int cpl, int cpr, int size, float targetsize, float skipspace ) {
		//m_width = skipspace;
		//m_size = targetsize;
		double	cx;								// character x coordinates
		double	cy;								// character y coordinates

		count = cpr  * cpl;
		base = gl.glGenLists( count );							 // create a display list big enough for all characters

		fontTexture.bind();				// select the font texture

		for( int i=0; i < count; i++ ) {
			cx = (i%cpl)/(double)cpl;			 // x position of current character (left)
			cy = (i/cpl)/(double)cpr;					// y position of current character (top)

			gl.glNewList( base + i, gl.GL_COMPILE );				// create a listelement
				// somehow, the upper and lower ends of the bitmap are the opposite as on
				// C++... is it a java thing?
				gl.glBegin( gl.GL_QUADS );					// each character is a quad
					gl.glTexCoord2d( cx, cy+(1/(double)cpr) );			// texture coordinates bottom left
					gl.glVertex3f( 0, 0, targetsize );				// vertex coordinates bottom left
					gl.glTexCoord2d( cx + (1/(double)cpl), cy+(1/(double)cpr));	// texture coordinates bottom right
					gl.glVertex3f( targetsize, 0, targetsize );				// vertex coordinates bottom right
					gl.glTexCoord2d( cx + (1/(double)cpl), cy);			// texture coordinates top right
					gl.glVertex3f( targetsize, 0, 0 );				// vertex coordinates top right
					gl.glTexCoord2d( cx, cy );				 // texture coordinates top left
					gl.glVertex3f( 0, 0, 0 );				// vertex coordinates top left
				gl.glEnd();
				gl.glTranslated( skipspace, 0, 0 );	// each character is only 10 pixels wide, move to the right
			gl.glEndList();							 // finish list
		}
	}


	public void print( float x, float y, float z, String string ) {
		gl.glPushMatrix();
		gl.glPushAttrib( gl.GL_LIST_BIT );				// push display list bits
		gl.glTranslated( x, y, z );							// textposition (0,0 - Bottom Left)

		// todo test if > 256 works...
		CharBuffer buf = CharBuffer.wrap( string.toCharArray() );

		// now reset the list-base index, subtract 32 because the space (first
		// non-hidden character) has index 33, after that use the glCallLists
		// method to display the lists corresponding to the indices in the buffer
		gl.glListBase( base - 32 );

		// need to multiplicate by 2! why???
		gl.glCallLists( string.length()*2, gl.GL_UNSIGNED_BYTE, buf );			// print text to screen
		gl.glPopAttrib();
		gl.glPopMatrix();
	}

	/**
	 * Prints a text to the screen. The text is allowed to be up to 256 chars. It
	 * is assumed that the current projection is an orthogonal projection.
	 * @param x the x-position where the text starts
	 * @param y the y-position where the text starts
	 * @param string - pointer to a char array with the text to be printed
	 */
	public void print( int x, int y, String string ) {
		if( string == null )// quit, if there is no texst
			return;
		if( fontTexture == null )
			return;

//		fontTexture.bind();

		gl.glPushMatrix();
		gl.glPushAttrib( gl.GL_LIST_BIT );				// push display list bits
		gl.glTranslated( x, y, 0 );							// textposition (0,0 - Bottom Left)

		byte[] array = (byte[])Array.newInstance( byte.class, string.length() ) ;
		for( int i = 0; i < string.length(); ++i )
			array[i] = (byte)string.charAt( i );
		//ByteBuffer bbuf = ByteBuffer.wrap( string.getBytes() );
		ByteBuffer bbuf = ByteBuffer.wrap( array );

		// now reset the list-base index, subtract 32 because the space (first
		// non-hidden character) has index 33, after that use the glCallLists
		// method to display the lists corresponding to the indices in the buffer
		gl.glListBase( base - 32 );
			
		gl.glCallLists( string.length(), gl.GL_UNSIGNED_BYTE, bbuf );			// print text to screen
		gl.glPopAttrib();
		gl.glPopMatrix();
	}
}
