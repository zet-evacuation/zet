/**
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

	/** The instance of the texture manager*/
	private TextureManager texMan;
	/** Describes if the textures are loaded or not */
	private boolean texturesLoaded = false;
	/** The texture containing the font information */
	private Texture fontTex;
	/** A texture-font object using to draw text on the OpenGL panel */
	private TextureFont font;
	/** The top position of the text */
	private double startPos = -10;

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
			fontTex = texMan.get( "font1" );
			font = new TextureFont( drawable.getGL(), fontTex );
			font.buildFont( 16, 14, 16, 12, 9 );
			//font.buildFont( 16, 8, 16, 24, 19 );
			fontTex.bind();
		}
	}

	@Override
	/**
	 * Displays the text on the screen.
	 *  @param drawable the context which is used for the credits panel
	 */
	public void display( GLAutoDrawable drawable ) {
		super.display( drawable );	// let clear the screen
		GL gl = drawable.getGL();

		gl.glEnable( GL.GL_TEXTURE_2D );
		this.switchToPrintScreen( gl );
		String[] lines = {"zet evakuierungs-tool",
			"",
			"Projektgruppe 517",
			"TU Dortmund",
			"",
			"",
			"Credits:",
			"",
			"Martin Groß",
			"Moukarram Kabbash",
			"Jan-Philipp Kappmeier",
			"Sophia Kardung",
			"Timon Kelter",
			"Joscha Kulbatzki",
			"Daniel Plümpe",
			"Marcel Preuß",
			"Gordon Schlechter",
			"Melanie Schmidt",
			"Sylvie Temme",
			"Matthias Woste",
			"",
			"",
			"copyright 2007-08"
		};
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
		startPos += ( this.getDeltaTime() / timePerPixel );
	}

	/**
	 * Loads the texture file from harddisk.
	 */
	private void loadTextures() {
		texMan.load( "font1", "./textures/font1.bmp" );
	}

	/**
	 * Draws some lines on the screen using the texture font. If the text was
	 * completely scrolled over the whole screen, the top position is resetted.
	 * @param lines an array containing all lines that should be displayed
	 */
	private void drawLines( String[] lines ) {
		int start = (int) Math.floor( startPos );
		int end = 0;
		for( int i = 0; i < lines.length; i++ ) {
			font.print( 48, start - i * 16, lines[i] );
			end = start - i * 16 + 12;
		}
		if( end > this.getHeight() + 48 )
			startPos = -10;
	}
}
