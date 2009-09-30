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

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import opengl.framework.JMovingEyePanel;
import opengl.helper.TextureFontStrings;
import opengl.helper.Texture;
import opengl.helper.TextureFont;
import opengl.helper.TextureManager;
import util.vectormath.Vector3;

/**
 * An OpenGL-panel displaying some textual copyright information about the zet
 * evacuation tool. The information scrolls from bottom to top, and starts again.
 * @author Jan-Philipp Kappmeier
 */
public class CreditsPanel extends JMovingEyePanel {
	/** The size of the panel, used for getText getX calculation. */
	private static int width = 480;
	/** The instance of the texture manager. */
	private TextureManager texMan;
	/** Describes if the textures are loaded or not */
	private boolean texturesLoaded = false;
	/** The texture containing the font information */
	private Texture texFont;
	/** The texture containing the logo */
	private Texture texLogo;
	/** A texture-font object using to draw getText on the {@code OpenGL} panel */
	private TextureFont font;
	/** The top getX of the getText */
	//private double startPos = -128; // 256 height of picture, 84 black pixels
	private float startPos = -5; // 256 height of picture, 84 black pixels
	/** The array containing the getText. */
	private TextureFontStrings lines = new TextureFontStrings( false );

	public CreditsPanel() {
		super();

		initLines();
	}

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
			texMan.setGL( drawable.getGL() );
			GLU glu = new GLU();
			texMan.setGLU( glu );
			loadTextures();
			texturesLoaded = true;
			//texFont = texMan.get( "font1" );
			//texLogo = texMan.get( "logo1" );
			font = new TextureFont( drawable.getGL(), texFont );
			font.buildFont3( 16, 14, 16, 0.7f, (0.7f*3)/4 );
			//font.buildFont( 16, 8, 16, 24, 19 );
			texFont.bind();
		}
		this.pitch( 20 );
		Vector3 pos = getPos();
		pos.y += 7;
	}

	@Override
	/**
	 * Displays the getText on the screen.
	 * @param drawable the context which is used for the credits panel
	 */
	public void display( GLAutoDrawable drawable ) {
		super.display( drawable );	// clear the screen
		//GL gl = drawable.getGL();

		//super.updateViewport( drawable, WIDTH, WIDTH, width, width )

		//gl.glEnable( GL.GL_TEXTURE_2D );

//gl.glTranslatef(0, 0,-6);
//gl.glTranslatef(-1.5f,0,0);
		gl.glLoadIdentity();
		this.look();
//l.glTranslatef(-1.5f, 0,-6);
//gl.glBegin( GL.GL_TRIANGLES );
//  gl.glColor3f(1, 0, 0); gl.glVertex3f(-1,-1, -3);
//  gl.glColor3f(0, 0, 1); gl.glVertex3f( 1,-1, -3);
//  gl.glColor3f(0, 1, 0); gl.glVertex3f( 0, 1, -3);
//gl.glEnd();
		gl.glEnable( GL.GL_TEXTURE_2D );
		texLogo.bind();
		gl.glBegin( GL.GL_QUADS );
			gl.glTexCoord2f( 0.0f, 1.0f );
			gl.glVertex3d( -10, -4, startPos );					// Unten links
			gl.glTexCoord2f( 0.0f, 0.0f );
			gl.glVertex3d( -10, -4, startPos -10 );					// Oben links
			gl.glTexCoord2f( 1.0f, 0.0f );
			gl.glVertex3d( 10, -4, startPos- 10 );						// Oben rechts
			gl.glTexCoord2f( 1.0f, 1.0f );
			gl.glVertex3d( 10, -4, startPos );						// Unten rechts
		gl.glEnd();


//		this.switchToPrintScreen( gl );
//
//		texLogo.bind();
//		// Draw a logo
//		gl.glBegin( GL.GL_QUADS );
//			gl.glTexCoord2f( 0.0f, 1.0f );
//			gl.glVertex3d( (width-256)/2, startPos, startPos );
//			gl.glTexCoord2f( 0.0f, 0.0f );
//			gl.glVertex3d( (width-256)/2, startPos + 128, startPos + 128 );
//			gl.glTexCoord2f( 1.0f, 0.0f );
//			gl.glVertex3d( (width-256)/2+256, startPos + 128, startPos + 128 );
//			gl.glTexCoord2f( 1.0f, 1.0f );
//			gl.glVertex3d( (width-256)/2+250, startPos, startPos );
//		gl.glEnd();
//
//		gl.glFlush();
		texFont.bind();
		String text = "Text";
		//font.print( -1.7f, -1, -3, getText );
		drawLines( lines, startPos + 1  );
//
//		this.switchToOrthoScreen( gl );
	}

	@Override
	/**
	 * Moves the getText a bit upwards. The scrolling is not time independent, that means
	 * lags can occur on slower hardware.
	 */
	public void animate() {
		double timePerPixel = 39;	// in milliseconds
		timePerPixel = 1000;
		startPos -= ( this.getDeltaTime() / timePerPixel );
		if( true ) return;
		super.animate();
		startPos += ( this.getDeltaTime() / timePerPixel );
	}

	/**
	 * Loads the texture file from harddisk.
	 */
	private void loadTextures() {
		texLogo = texMan.newTexture( "logo1", "./textures/logo1.png" );
		texFont = texMan.newTexture( "font1", "./textures/font1.bmp" );
	}

	/**
	 * Draws some lines on the screen using the texture font. If the getText was
	 * completely scrolled over the whole screen, the top getX is resetted.
	 * @param lines an array containing all lines that should be displayed
	 */
	private void drawLines( TextureFontStrings lines, float start ) {
		//font.print( -1.7f, -1, -3, getText );
		//int start = (int) Math.floor( startPos );
		int end = 0;
		for( int i = 0; i < lines.size(); i++ ) {
			//font.print( lines[i].getX(), start - i * 16, lines[i].getText() );
			font.print( -lines.getX( i ), -4f, start + i * 1.1f, lines.getText(i) );
			//end = start - i * 16 + 12;
		}
		if( end > this.getHeight() + 48 )
			startPos = -128;
	}

	/**
	 * Adds all lines of the credits getText to the font line object.
	 */
	private void initLines() {
		lines.setXoffset( 10.3 );
		lines.add( "zet evakuierungs-tool", true );
		lines.add( "(c) 2007-08 Projektgruppe 517, TU-Dortmund", true );
		lines.add( "(c) 2008-09 zet development team", true );
		lines.add( "", false );
		lines.add( "http://www.zet-evakuierung.de", true );
		lines.add( "", false );
		lines.add( "", false );
		lines.add( "Credits:", true );
		lines.add( "", true );
		lines.add( "Martin Groß", true );
		lines.add( "Moukarram Kabbash", true );
		lines.add( "Jan-Philipp Kappmeier", true );
		lines.add( "Sophia Kardung", true );
		lines.add( "Timon Kelter", true );
		lines.add( "Joscha Kulbatzki", true );
		lines.add( "Daniel Plümpe", true );
		lines.add( "Marcel Preuß", true );
		lines.add( "Gordon Schlechter", true );
		lines.add( "Melanie Schmidt", true );
		lines.add( "Sylvie Temme", true );
		lines.add( "Matthias Woste", true );
		lines.add( "", true );
		lines.add( "", true );
		lines.add( "This program is free software; you can", false );
		lines.add( "redistribute it and/or modify it under", false );
		lines.add( "the terms of the GNU General Public Li-", false );
		lines.add( "cense as published by the Free Software", false );
		lines.add( "Foundation; either version 2 of the Li-", false );
		lines.add( "cense, or (at your opinion) any later", false );
		lines.add( "version.", false );
		lines.add( "", false );
		lines.add( "zet is distributed in the hope that it", false );
		lines.add( "will be useful but WITHOUT ANY WARRANTY;", false );
		lines.add( "without even the implied warranty of", false );
		lines.add( "MERCHANTABILITY or FITNESS FOR A PAR-", false );
		lines.add( "TICULAR PURPOSE. See the GNU General", false );
		lines.add( "Public License for more details.", false );
		lines.add( "", false );
		lines.add( "You should have received a copy of the", false );
		lines.add( "GNU General Public Licence along with", false );
		lines.add( "zet; if not, write to the Free Software", false );
		lines.add( "Foundation, Inc., 51 Franklin Street,", false );
		lines.add( "Fifth Floor, Boston, MA 02110-131, USA,", false );
		lines.add( "or have a look at", false );
		lines.add( "http://www.gnu.org/licenses/", false );
		lines.add( "", false );
		lines.add( "", false );
		lines.add( "Third-party components used by zet:", false );
		lines.add( "", false );
		lines.add( "MersenneTwister Version 13", false );
		lines.add( "Copyright (c) 2003 by Sean Luke.", false );
		lines.add( "Portions copyright (c) 1993 by Michael", false );
		lines.add( "Lecuyer.", false );
		lines.add( "", false );
		lines.add( "MersenneTwisterFast Version 13", false );
		lines.add( "Copyright (c) 2003 by Sean Luke.", false );
		lines.add( "Portions copyright (c) 1993 by Michael", false );
		lines.add( "Lecuyer.", false );
		lines.add( "", false );
		lines.add( "MTRandom", false );
		lines.add( "Copyright (c) 2005 by David Beaumont.", false );
		lines.add( "", false );
		lines.add( "TableLayout JDK 1.5 2007-04-21", false );
		lines.add( "Copyright (c) 2001 by Daniel Barbalace.", false );
		lines.add( "https://tablelayout.dev.java.net/", false );
		lines.add( "", false );
		lines.add( "JFreeChart 1.0.3", false );
		lines.add( "Copyright (c) 2000-2009 by Object", false );
		lines.add( "Refinery and Contributors.", false );
		lines.add( "http://www.jfree.org/jfreechart/", false );
		lines.add( "", false );
		lines.add( "JCommon 1.0.7", false );
		lines.add( "Copyright (c) 2007-2009 by Object", false );
		lines.add( "Refinery and Contributors.", false );
		lines.add( "http://www.jfree.org/jcommon/", false );
		lines.add( "", false );
		lines.add( "SSJ 2.1.2", false );
		lines.add( "Copyright (c) 2008  Pierre L'Ecuyer", false );
		lines.add( "and Université de Montréal", false );
		lines.add( "http://www.iro.umontreal.ca/~simardr/ssj/", false );
		lines.add( "", false );
		lines.add( "XStream 1.3", false );
		lines.add( "Copyright (c) 2003-2006, Joe Walnes", false );
		lines.add( "Copyright (c) 2006-2007, XStream Committers", false );
		lines.add( "http://xstream.codehaus.org/", false );
		lines.add( "", false );
		lines.add( "JOGL 1.1.1-rc6", false );
		lines.add( "Copyright (c) 2003-2007 Sun Microsystems", false );
		lines.add( "https://jogl.dev.java.net/", false );
		lines.add( "", false );
		lines.add( "JMF 2.1.1e", false );
		lines.add( "Copyright (c) 1995-2003 Sun Microsystems", false );
		lines.add( "http://java.sun.com/javase/technologies/", false );
		lines.add( "desktop/media/jmf/", false );
	}
}
