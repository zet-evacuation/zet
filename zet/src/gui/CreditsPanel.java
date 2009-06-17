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
 * Class CreditsPanel
 * Erstellt 18.05.2008, 20:05:12
 */
package gui;

import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import opengl.framework.JOrthoPanel;
import opengl.helper.TextureFont;
import opengl.helper.TextureManager;

/**
 * An OpenGL-panel displaying some textual copyright information about the zet
 * evacuation tool. The information scrolls from bottom to top, and starts again.
 * @author Jan-Philipp Kappmeier
 */
public class CreditsPanel extends JOrthoPanel {
	/** The size of the panel, used for text position calculation. */
	private static int width = 480;
	/** The instance of the texture manager. */
	private TextureManager texMan;
	/** Describes if the textures are loaded or not */
	private boolean texturesLoaded = false;
	/** The texture containing the font information */
	private Texture texFont;
	/** The texture containing the logo */
	private Texture texLogo;
	/** A texture-font object using to draw text on the {@code OpenGL} panel */
	private TextureFont font;
	/** The top position of the text */
	private double startPos = -128; // 256 height of picture, 84 black pixels
	/** The array containing the text. */
	private CreditsString[] lines = {
		new CreditsString( "zet evakuierungs-tool", true ),
		new CreditsString( "(c) 2007-08 Projektgruppe 517, TU-Dortmund", true ),
		new CreditsString( "(c) 2008-09 zet development team", true ),
		new CreditsString( "", false ),
		new CreditsString( "http://www.zet-evakuierung.de", true ),
		new CreditsString( "", false ),
		new CreditsString( "", false ),
		new CreditsString( "Credits:", true ),
		new CreditsString( "", true ),
		new CreditsString( "Martin Groß", true ),
		new CreditsString( "Moukarram Kabbash", true ),
		new CreditsString( "Jan-Philipp Kappmeier", true ),
		new CreditsString( "Sophia Kardung", true ),
		new CreditsString( "Timon Kelter", true ),
		new CreditsString( "Joscha Kulbatzki", true ),
		new CreditsString( "Daniel Plümpe", true ),
		new CreditsString( "Marcel Preuß", true ),
		new CreditsString( "Gordon Schlechter", true ),
		new CreditsString( "Melanie Schmidt", true ),
		new CreditsString( "Sylvie Temme", true ),
		new CreditsString( "Matthias Woste", true ),
		new CreditsString( "", true ),
		new CreditsString( "", true ),
		new CreditsString( "This program is free software; you can", false ),
		new CreditsString( "redistribute it and/or modify it under", false ),
		new CreditsString( "the terms of the GNU General Public Li-", false ),
		new CreditsString( "cense as published by the Free Software", false ),
		new CreditsString( "Foundation; either version 2 of the Li-", false ),
		new CreditsString( "cense, or (at your opinion) any later", false ),
		new CreditsString( "version.", false ),
		new CreditsString( "", false ),
		new CreditsString( "zet is distributed in the hope that it", false ),
		new CreditsString( "will be useful but WITHOUT ANY WARRANTY;", false ),
		new CreditsString( "without even the implied warranty of", false ),
		new CreditsString( "MERCHANTABILITY or FITNESS FOR A PAR-", false ),
		new CreditsString( "TICULAR PURPOSE. See the GNU General", false ),
		new CreditsString( "Public License for more details.", false ),
		new CreditsString( "", false ),
		new CreditsString( "You should have received a copy of the", false ),
		new CreditsString( "GNU General Public Licence along with", false ),
		new CreditsString( "zet; if not, write to the Free Software", false ),
		new CreditsString( "Foundation, Inc., 51 Franklin Street,", false ),
		new CreditsString( "Fifth Floor, Boston, MA 02110-131, USA,", false ),
		new CreditsString( "or have a look at", false ),
		new CreditsString( "http://www.gnu.org/licenses/", false ),
		new CreditsString( "", false ),
		new CreditsString( "", false ),
		new CreditsString( "Third-party components used by zet:", false ),
		new CreditsString( "", false ),
		new CreditsString( "MersenneTwister Version 13", false ),
		new CreditsString( "Copyright (c) 2003 by Sean Luke.", false ),
		new CreditsString( "Portions copyright (c) 1993 by Michael", false ),
		new CreditsString( "Lecuyer.", false ),
		new CreditsString( "", false ),
		new CreditsString( "MersenneTwisterFast Version 13", false ),
		new CreditsString( "Copyright (c) 2003 by Sean Luke.", false ),
		new CreditsString( "Portions copyright (c) 1993 by Michael", false ),
		new CreditsString( "Lecuyer.", false ),
		new CreditsString( "", false ),
		new CreditsString( "MTRandom", false ),
		new CreditsString( "Copyright (c) 2005 by David Beaumont.", false ),
		new CreditsString( "", false ),
		new CreditsString( "TableLayout JDK 1.5 2007-04-21", false ),
		new CreditsString( "Copyright (c) 2001 by Daniel Barbalace.", false ),
		new CreditsString( "https://tablelayout.dev.java.net/", false ),
		new CreditsString( "", false ),
		new CreditsString( "JFreeChart 1.0.3", false ),
		new CreditsString( "Copyright (c) 2000-2009 by Object", false ),
		new CreditsString( "Refinery and Contributors.", false ),
		new CreditsString( "http://www.jfree.org/jfreechart/", false ),
		new CreditsString( "", false ),
		new CreditsString( "JCommon 1.0.7", false ),
		new CreditsString( "Copyright (c) 2007-2009 by Object", false ),
		new CreditsString( "Refinery and Contributors.", false ),
		new CreditsString( "http://www.jfree.org/jcommon/", false ),
		new CreditsString( "", false ),
		new CreditsString( "SSJ 2.1.2", false ),
		new CreditsString( "Copyright (c) 2008  Pierre L'Ecuyer", false ),
		new CreditsString( "and Université de Montréal", false ),
		new CreditsString( "http://www.iro.umontreal.ca/~simardr/ssj/", false ),
		new CreditsString( "", false ),
		new CreditsString( "XStream 1.3", false ),
		new CreditsString( "Copyright (c) 2003-2006, Joe Walnes", false ),
		new CreditsString( "Copyright (c) 2006-2007, XStream Committers", false ),
		new CreditsString( "http://xstream.codehaus.org/", false ),
		new CreditsString( "", false ),
		new CreditsString( "JOGL 1.1.1-rc6", false ),
		new CreditsString( "Copyright (c) 2003-2007 Sun Microsystems", false ),
		new CreditsString( "https://jogl.dev.java.net/", false ),
		new CreditsString( "", false ),
		new CreditsString( "JMF 2.1.1e", false ),
		new CreditsString( "Copyright (c) 1995-2003 Sun Microsystems", false ),
		new CreditsString( "http://java.sun.com/javase/technologies/", false ),
		new CreditsString( "desktop/media/jmf/", false )
		//new CreditsString( "----------------------------------------", false ),
	};

	@Override
	/**
	 * Initializes the graphics context and initializes the texture font.
	 *  @param drawable the context which is used for the credits panel
	 */
	public void initGFX( GLAutoDrawable drawable ) {
		drawable.getGL().glEnable( GL.GL_TEXTURE_2D );
		super.initGFX( drawable );
		if( !texturesLoaded ) {
			texMan = TextureManager.getInstance();
			loadTextures();
			texturesLoaded = true;
			texFont = texMan.get( "font1" );
			texLogo = texMan.get( "logo1" );
			font = new TextureFont( drawable.getGL(), texFont );
			font.buildFont( 16, 14, 16, 12, 9 );
			//font.buildFont( 16, 8, 16, 24, 19 );
			texFont.bind();
		}
	}

	@Override
	/**
	 * Displays the text on the screen.
	 * @param drawable the context which is used for the credits panel
	 */
	public void display( GLAutoDrawable drawable ) {
		super.display( drawable );	// clear the screen
		//GL gl = drawable.getGL();

		gl.glEnable( GL.GL_TEXTURE_2D );
		this.switchToPrintScreen( gl );

		texLogo.bind();
		// Draw a logo
		gl.glBegin( GL.GL_QUADS );
			gl.glTexCoord2f( 0.0f, 1.0f );
			gl.glVertex3d( (width-256)/2, startPos, 0 );
			gl.glTexCoord2f( 0.0f, 0.0f );
			gl.glVertex3d( (width-256)/2, startPos + 128, 0 );
			gl.glTexCoord2f( 1.0f, 0.0f );
			gl.glVertex3d( (width-256)/2+256, startPos + 128, 0 );
			gl.glTexCoord2f( 1.0f, 1.0f );
			gl.glVertex3d( (width-256)/2+250, startPos, 0 );
		gl.glEnd();

		texFont.bind();
		drawLines( lines );

		this.switchToOrthoScreen( gl );
	}

	@Override
	/**
	 * Moves the text a bit upwards. The scrolling is not time independent, that means
	 * lags can occur on slower hardware.
	 */
	public void animate() {
		super.animate();
		double timePerPixel = 39;	// in milliseconds
		timePerPixel = 50;
		startPos += ( this.getDeltaTime() / timePerPixel );
	}

	/**
	 * Loads the texture file from harddisk.
	 */
	private void loadTextures() {
		texMan.load( "font1", "./textures/font1.bmp" );
		texMan.load( "logo1", "./textures/logo1.png" );
	}

	/**
	 * Draws some lines on the screen using the texture font. If the text was
	 * completely scrolled over the whole screen, the top position is resetted.
	 * @param lines an array containing all lines that should be displayed
	 */
	private void drawLines( CreditsString[] lines ) {
		int start = (int) Math.floor( startPos );
		int end = 0;
		for( int i = 0; i < lines.length; i++ ) {
			font.print( lines[i].position(), start - i * 16, lines[i].text() );
			end = start - i * 16 + 12;
		}
		if( end > this.getHeight() + 48 )
			startPos = -128;
	}
	
	/**
	 * A special inner class that represents a line of text. It saves
	 * the information if the line is left-aligned or centered. The
	 * with of the window has to be given from outside.
	 */
	private class CreditsString {
		/** Indicates wheather the text is centered, or not. */
		private boolean centered;
		/** The content of the line. */
		private String string;

		/**
		 * Creates a new line with alignment information.
		 * @param string the text of the line
		 * @param centered indicates wheather the line is centered, or not
		 */
		public CreditsString( String string, boolean centered ) {
			this.string = string;
			this.centered = centered;
		}

		/**
		 * Returns the <code>x</code>-position of the line
		 * @return the <code>x</code>-position of the line
		 */
		public int position() {
			return centered ? (width - (string.length() * 9))/2 : (width - (40*9))/2;
		}

		/**
		 * Returns the content of the line
		 * @return
		 */
		public String text() {
			return string;
		}
	}
}
